package mz.co.moseguros.payment.application;

import java.util.UUID;
import java.util.function.Consumer;

import mz.co.moseguros.payment.application.gateway.AsyncDispatcher;
import mz.co.moseguros.payment.infrastructure.PaymentExecutorConfig.MpesaC2bExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * {@link AsyncDispatcher} sobre o {@code mpesaC2bExecutor} (S5-04 §2): depois do commit da criação
 * submete {@code MpesaC2bJob.run(intentId)}. Executor cheio → {@code WARN} e a intenção fica
 * {@code CREATED}: o {@code PaymentDispatchRecoveryJob} volta a submetê-la.
 */
@Component
public class ExecutorAsyncDispatcher implements AsyncDispatcher {

    private static final Logger log = LoggerFactory.getLogger(ExecutorAsyncDispatcher.class);

    private final MpesaC2bExecutor executor;
    private final Consumer<UUID> dispatch;

    @Autowired
    public ExecutorAsyncDispatcher(MpesaC2bExecutor executor, MpesaC2bJob job) {
        this(executor, job::run);
    }

    ExecutorAsyncDispatcher(MpesaC2bExecutor executor, Consumer<UUID> dispatch) {
        this.executor = executor;
        this.dispatch = dispatch;
    }

    @Override
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

    @Override
    public boolean submit(UUID intentId) {
        try {
            executor.execute(() -> {
                try {
                    dispatch.accept(intentId);
                } catch (RuntimeException e) {
                    log.warn("Payment dispatch failed intentId={} ({})", intentId, e.getClass().getSimpleName());
                }
            });
            return true;
        } catch (TaskRejectedException e) {
            log.warn("Payment dispatch rejected, executor full intentId={}: the recovery job will retry", intentId);
            return false;
        }
    }
}
