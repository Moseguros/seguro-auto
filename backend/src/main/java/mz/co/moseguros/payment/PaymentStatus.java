package mz.co.moseguros.payment;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Estados de uma intenção de pagamento (S5-02 §3). As transições estão em {@code PaymentIntent} e
 * passam todas pelo {@code IntentStateService}.
 */
@Schema(enumAsRef = true)
public enum PaymentStatus {
    CREATED,
    PENDING,
    UNKNOWN,
    SUCCEEDED,
    FAILED,
    EXPIRED,
    CANCELLED,
    REFUNDED;

    /** {@code CREATED}, {@code PENDING} ou {@code UNKNOWN}: o pagamento pode ainda acontecer. */
    public boolean isActive() {
        return this == CREATED || this == PENDING || this == UNKNOWN;
    }
}
