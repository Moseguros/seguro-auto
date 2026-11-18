package mz.co.moseguros.paymentworker.parser;

/**
 * Rodapé de um ficheiro de movimentos (S5-08 §1).
 *
 * @param recordCount         número de registos de detalhe declarado
 * @param totalAmountCentavos soma dos montantes declarada
 * @param totalFeesCentavos   soma das tarifas/comissões declarada; {@code null} se o formato não a tiver
 */
public record FileTrailer(int recordCount, long totalAmountCentavos, Long totalFeesCentavos) {
}
