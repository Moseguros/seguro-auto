package mz.co.moseguros.payment.domain;

import io.swagger.v3.oas.annotations.media.Schema;

/** Quem confirmou o pagamento ({@code payment_intent.confirmed_by}, S5-02 §2). */
@Schema(name = "PaymentConfirmedBy", enumAsRef = true)
public enum ConfirmedBy {
    /** Resposta do fornecedor ao pedido (M-Pesa, S5-04). */
    PROVIDER,
    /** Consulta de estado (S5-04). */
    STATUS_QUERY,
    /** Ficheiro de movimentos do banco (MEPS/BMEPS, S5-10). */
    REFERENCE_FILE,
    /** Resolução de uma excepção pelo financeiro (S5-10). */
    MANUAL
}
