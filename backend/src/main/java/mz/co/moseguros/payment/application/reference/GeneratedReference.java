package mz.co.moseguros.payment.application.reference;

/**
 * Referência gerada (S5-07 §1).
 *
 * @param reference     11 dígitos: factura (7) + mês (2) + controlo (2)
 * @param invoiceNumber número de factura da entidade (1–9999999), nunca reutilizado
 * @param month         mês da emissão em {@code Africa/Maputo} (1–12)
 */
public record GeneratedReference(String reference, int invoiceNumber, int month) {
}
