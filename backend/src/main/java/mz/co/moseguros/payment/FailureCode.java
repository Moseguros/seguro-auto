package mz.co.moseguros.payment;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Motivo de uma intenção que não terminou em {@code SUCCEEDED} ({@code payment_intent.failure_code},
 * S5-02 §3; i18n {@code paymentFailure.<CODE>}). Está no pacote público (e não em {@code domain})
 * porque faz parte do {@link IntentView}.
 */
@Schema(enumAsRef = true)
public enum FailureCode {
    CUSTOMER_CANCELLED,
    INSUFFICIENT_BALANCE,
    INVALID_MSISDN,
    PROVIDER_REJECTED,
    PROVIDER_CONFIG_ERROR,
    PROVIDER_UNAVAILABLE,
    TIMEOUT_EXPIRED,
    /** A intenção passou o prazo ({@code PaymentExpiryJob}). */
    EXPIRED,
    /** Substituída: outra intenção do mesmo alvo foi paga, ou o montante mudou (referência). */
    SUPERSEDED,
    /** Resolvida pelo financeiro (S5-10). */
    MANUAL
}
