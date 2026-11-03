package mz.co.moseguros.rating;

/**
 * Estado de uma cotação (S3-02 §10). Nas leituras usa-se o estado efectivo: uma cotação
 * {@code OFFERED} cuja validade passou, ou cuja data de início já passou, sai como {@code EXPIRED}
 * (no S3 nenhum job grava {@code EXPIRED}; a coluna aceita-o para uso futuro).
 */
public enum QuoteStatus {
    /** Com preço, dentro da validade. */
    OFFERED,
    /** Sem preço; o motivo está em {@link UnavailableReason}. */
    NOT_AVAILABLE,
    /** Com preço, mas fora da validade ou com a data de início no passado. */
    EXPIRED,
    /** Usada numa proposta ({@link RatingApi#markUsed}). */
    USED
}
