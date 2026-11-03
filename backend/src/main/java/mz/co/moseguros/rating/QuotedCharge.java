package mz.co.moseguros.rating;

/**
 * Encargo (imposto ou taxa) de uma cotação, já calculado (S3-02 §10).
 *
 * @param amount centavos
 */
public record QuotedCharge(String code, String labelPt, String labelEn, long amount) {
}
