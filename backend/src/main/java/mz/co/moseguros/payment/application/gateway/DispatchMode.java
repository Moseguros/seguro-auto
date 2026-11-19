package mz.co.moseguros.payment.application.gateway;

/** Como a intenção é entregue ao fornecedor (S5-02 §4, passo 11). */
public enum DispatchMode {
    /** Na mesma transacção da criação (entidade e referência). */
    SYNC,
    /** Depois do commit, pelo {@link AsyncDispatcher} (M-Pesa, S5-04). */
    ASYNC_AFTER_COMMIT
}
