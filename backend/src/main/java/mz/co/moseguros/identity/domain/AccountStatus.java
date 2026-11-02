package mz.co.moseguros.identity.domain;

/**
 * Estado de uma conta.
 */
public enum AccountStatus {
    /** Staff convidado que ainda não activou a conta. */
    PENDING_ACTIVATION,
    ACTIVE,
    /** Bloqueada temporariamente por tentativas falhadas. */
    LOCKED,
    DISABLED
}
