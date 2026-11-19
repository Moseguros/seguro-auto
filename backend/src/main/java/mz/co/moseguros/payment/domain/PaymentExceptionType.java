package mz.co.moseguros.payment.domain;

/** Tipos de {@code payment.payment_exception} (S5-02 §2; tratados no S5-10 e S5-12; i18n {@code paymentExceptionType.<CODE>}). */
@io.swagger.v3.oas.annotations.media.Schema(name = "PaymentExceptionType", enumAsRef = true)
public enum PaymentExceptionType {
    AMOUNT_MISMATCH,
    EXPIRED_REFERENCE,
    ALREADY_PAID,
    UNKNOWN_REFERENCE,
    CANCELLED_REFERENCE,
    /** O dono do alvo recebeu um pagamento que já não pode aceitar (ex.: proposta expirada). */
    TARGET_NOT_PAYABLE,
    UNRESOLVED_STATUS
}
