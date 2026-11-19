package mz.co.moseguros.notification.application;

import java.util.List;
import java.util.UUID;

import mz.co.moseguros.notification.infrastructure.NotificationConfig.NotificationExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * Entrega as linhas {@code QUEUED} ao {@code notificationExecutor} depois do commit (S6-04 §4, passo 7); sem
 * transacção activa, entrega logo. Se a fila estiver cheia ou o {@code afterCommit} se perder, o
 * {@link NotificationDispatchJob} envia-as no ciclo seguinte.
 */
@Component
public class MessageDispatcher {

    private static final Logger log = LoggerFactory.getLogger(MessageDispatcher.class);

    private final NotificationExecutor executor;
    private final ObjectProvider<MessageSender> sender;

    public MessageDispatcher(NotificationExecutor executor, ObjectProvider<MessageSender> sender) {
        this.executor = executor;
        this.sender = sender;
    }

    public void afterCommit(List<UUID> messageIds) {
        if (messageIds.isEmpty()) {
            return;
        }
        List<UUID> ids = List.copyOf(messageIds);
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            submit(ids);
            return;
        }
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                submit(ids);
            }
        });
    }

    private void submit(List<UUID> ids) {
        try {
            executor.execute(() -> {
                for (UUID id : ids) {
                    try {
                        sender.getObject().send(id);
                    } catch (RuntimeException e) {
                        log.warn("Notification message {} not sent now ({}); the dispatch job retries", id,
                                e.getClass().getSimpleName());
                    }
                }
            });
        } catch (TaskRejectedException e) {
            log.warn("Notification executor full: {} message(s) left for the dispatch job", ids.size());
        }
    }
}
