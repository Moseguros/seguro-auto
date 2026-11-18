package mz.co.moseguros.payment.application.gateway;

import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * Envio assíncrono das intenções {@link DispatchMode#ASYNC_AFTER_COMMIT} (M-Pesa; S5-02 §4, passo 11):
 * depois do commit da criação, entrega o id da intenção a uma thread {@code payment-dispatch-*}, que
 * chama o {@code PaymentDispatchJob} (compare-and-set {@code CREATED → PENDING}, envio ao fornecedor e
 * aplicação do resultado). Um rollback não envia nada. O S5-04 completa o envio M-Pesa (prazos,
 * consultas de estado, recuperação das intenções {@code CREATED} esquecidas).
 */
public class AsyncDispatcher {

    private static final Logger log = LoggerFactory.getLogger(AsyncDispatcher.class);

    private final Consumer<UUID> dispatch;
    private final ExecutorService executor;

    /** @param dispatch o envio de uma intenção ({@code PaymentDispatchJob::dispatch}) */
    public AsyncDispatcher(Consumer<UUID> dispatch, int threads) {
        this.dispatch = dispatch;
        AtomicInteger counter = new AtomicInteger();
        ThreadFactory factory = r -> {
            Thread t = new Thread(r, "payment-dispatch-" + counter.incrementAndGet());
            t.setDaemon(true);
            return t;
        };
        this.executor = Executors.newFixedThreadPool(threads, factory);
    }

    /** Agenda o envio para depois do commit da transacção actual (ou já, sem transacção). */
    public void dispatchAfterCommit(UUID intentId) {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            submit(intentId);
            return;
        }
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                submit(intentId);
            }
        });
    }

    private void submit(UUID intentId) {
        executor.submit(() -> {
            try {
                dispatch.accept(intentId);
            } catch (RuntimeException e) {
                log.warn("Payment dispatch failed intentId={} ({})", intentId, e.getClass().getSimpleName());
            }
        });
    }

    @PreDestroy
    void shutdown() {
        executor.shutdown();
    }
}
