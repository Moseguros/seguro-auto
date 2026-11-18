package mz.co.moseguros.payment.domain;

/** Tipos de {@code payment.payment_event} (S5-02 §2). */
@io.swagger.v3.oas.annotations.media.Schema(name = "PaymentEventType", enumAsRef = true)
public enum PaymentEventType {
    CREATED,
    DISPATCHED,
    PROVIDER_RESPONSE,
    STATUS_QUERY,
    STATUS_CHANGED,
    REFERENCE_PAID,
    MANUAL_RESOLUTION
}
