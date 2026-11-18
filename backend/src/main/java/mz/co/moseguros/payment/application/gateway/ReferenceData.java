package mz.co.moseguros.payment.application.gateway;

/**
 * Entidade e referência geradas para uma intenção (S5-02 §6).
 *
 * @param entity        5 dígitos ({@code settings.entity} da seguradora)
 * @param reference     11 dígitos: factura (7) + mês (2) + controlo (2) (S5-07)
 * @param invoiceNumber número de factura da entidade (1–9999999)
 * @param month         mês da emissão em {@code Africa/Maputo} (1–12)
 */
public record ReferenceData(String entity, String reference, int invoiceNumber, int month) {
}
