package mz.co.moseguros.rating.domain;

import mz.co.moseguros.organization.RatingMode;

/** Quem calculou o preço de uma cotação (S3-02 §4): a tarifa interna ou o sistema da seguradora. */
public enum RatingProviderKind {
    /** {@code TariffEvaluator} com a tarifa da versão (S3-01). */
    INTERNAL,
    /** Serviço da seguradora, contrato {@code moseguros-rating-1} (S3-02 §8). */
    EXTERNAL;

    public static RatingProviderKind of(RatingMode mode) {
        return mode == RatingMode.EXTERNAL ? EXTERNAL : INTERNAL;
    }
}
