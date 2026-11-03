package mz.co.moseguros.rating;

/**
 * Cobertura de uma cotação com o seu prémio (S3-02 §10).
 *
 * @param capitalAmount   capital resolvido em centavos ({@code 0} se a cobertura não tiver capital)
 * @param deductibleType  {@code NONE}, {@code FIXED} ou {@code PERCENT}
 * @param deductibleValue {@code FIXED}: centavos em texto ({@code "500000"}); {@code PERCENT}: a
 *                        percentagem ({@code "10"}); {@code NONE}: {@code null}. O mínimo da franquia
 *                        não vem aqui: quem precisa lê-o do catálogo (o S4-02 faz assim)
 * @param premiumAmount   prémio da cobertura em centavos
 */
public record QuotedCoverage(String code, boolean mandatory, long capitalAmount,
                             String deductibleType, String deductibleValue, long premiumAmount) {
}
