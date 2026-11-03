package mz.co.moseguros.identity.domain;

/**
 * Estado de um convite, calculado a partir das datas ({@link Invitation#status}).
 */
public enum InvitationStatus {
    /** Por usar e dentro da validade. */
    PENDING,
    /** O convidado activou a conta. */
    ACCEPTED,
    /** Passou a validade sem activação. */
    EXPIRED,
    /** Revogado (manualmente, substituído por outro convite ou por excesso de códigos errados). */
    REVOKED
}
