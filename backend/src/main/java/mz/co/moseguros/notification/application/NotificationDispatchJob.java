package mz.co.moseguros.notification.application;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import mz.co.moseguros.notification.domain.NotificationMessage;
import mz.co.moseguros.notification.domain.NotificationMessageRepository;
import mz.co.moseguros.shared.tenant.RunAsSystem;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Garante o envio das notificações (S6-04 §4): a cada 15 s ({@code Africa/Maputo}, lock
 * {@value #LOCK}) devolve à fila as linhas presas em {@code SENDING} há mais de 2 minutos (a tentativa conta) e
 * envia até 200 linhas {@code QUEUED} na hora, por {@code next_attempt_at}. Cobre um {@code afterCommit}
 * perdido e as novas tentativas.
 */
@Component
public class NotificationDispatchJob {

    public static final String LOCK = "notification.dispatch";
    static final int BATCH = 200;
    static final Duration STUCK_AFTER = Duration.ofMinutes(2);
    private static final Logger log = LoggerFactory.getLogger(NotificationDispatchJob.class);

    private final NotificationMessageRepository messages;
    private final MessageSender sender;
    private final TransactionTemplate tx;
    private final Clock clock;

    public NotificationDispatchJob(NotificationMessageRepository messages, MessageSender sender, TransactionTemplate tx,
                                   Clock clock) {
        this.messages = messages;
        this.sender = sender;
        this.tx = tx;
        this.clock = clock;
    }

    /** @return o número de linhas tentadas */
    @Scheduled(cron = "*/15 * * * * *", zone = "Africa/Maputo")
    @SchedulerLock(name = LOCK, lockAtMostFor = "PT1M")
    @RunAsSystem
    public int run() {
        Instant now = clock.instant();
        Integer requeued = tx.execute(status -> {
            List<NotificationMessage> stuck = messages.stuckSending(now.minus(STUCK_AFTER));
            stuck.forEach(m -> m.requeueStuck(now));
            return stuck.size();
        });
        List<UUID> due = tx.execute(status -> messages.dueIds(now, BATCH));
        int failed = 0;
        for (UUID id : due) {
            try {
                sender.send(id);
            } catch (RuntimeException e) {
                failed++;
                log.error("Notification message {} could not be processed ({})", id, e.getClass().getSimpleName(), e);
            }
        }
        if (!due.isEmpty() || (requeued != null && requeued > 0)) {
            log.info("Job finished job={} processed={} requeued={} failed={}", LOCK, due.size(), requeued, failed);
        }
        return due.size();
    }
}
