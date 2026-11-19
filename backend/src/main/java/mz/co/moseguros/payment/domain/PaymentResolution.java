package mz.co.moseguros.payment.domain;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Resolução de uma excepção de pagamento pelo financeiro ({@code payment_exception.resolution}, S5-10 §6;
 * i18n {@code paymentResolution.<CODE>}). Nenhuma movimenta dinheiro.
 */
@Schema(name = "PaymentResolution", enumAsRef = true)
public enum PaymentResolution {
    /** O pagamento é associado a uma intenção da seguradora, que passa a {@code SUCCEEDED}. */
    LINKED,
    /** A seguradora devolve o dinheiro fora da plataforma; a plataforma só regista a decisão. */
    REFUND,
    /** Fecha sem efeito. */
    IGNORED,
    /** Intenção M-Pesa {@code UNKNOWN} confirmada no portal do fornecedor: {@code SUCCEEDED}. */
    MARK_SUCCEEDED,
    /** Intenção M-Pesa {@code UNKNOWN} sem cobrança: {@code FAILED}. */
    MARK_FAILED
}
