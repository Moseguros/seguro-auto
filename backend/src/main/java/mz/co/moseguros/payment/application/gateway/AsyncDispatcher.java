package mz.co.moseguros.payment.application.gateway;

import java.util.UUID;

/**
 * Envio assíncrono das intenções {@link DispatchMode#ASYNC_AFTER_COMMIT} (M-Pesa; S5-02 §4, passo 11;
 * S5-04 §2). A implementação é o {@code ExecutorAsyncDispatcher} (executor {@code mpesaC2bExecutor}),
 * que corre o {@code MpesaC2bJob}. Um rollback não envia nada; o compare-and-set
 * {@code CREATED → PENDING} do job impede o envio duplo quando a mesma intenção é submetida duas vezes.
 */
public interface AsyncDispatcher {

    /** Agenda o envio para depois do commit da transacção actual (ou já, sem transacção). */
    void dispatchAfterCommit(UUID intentId);

    /**
     * Submete já o envio ao executor.
     *
     * @return {@code false} se o executor recusou (cheio): a intenção fica {@code CREATED} e o
     *         {@code PaymentDispatchRecoveryJob} volta a submetê-la
     */
    boolean submit(UUID intentId);
}
