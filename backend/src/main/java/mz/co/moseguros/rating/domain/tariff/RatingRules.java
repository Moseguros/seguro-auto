package mz.co.moseguros.rating.domain.tariff;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import mz.co.moseguros.catalog.CoverageCode;

/**
 * Tarifa interna de uma versão de produto ({@code rules.rating}, formato {@code rating-v1.json}),
 * já lida pelo {@link RatingRulesParser}. As listas mantêm a ordem do JSON (os índices são os dos
 * ponteiros dos erros e da decomposição).
 *
 * @param durationFactors vazia quando a tarifa não tem {@code durationFactors}
 */
public record RatingRules(String schemaVersion, List<DurationFactor> durationFactors, List<CoverageTariff> coverages) {

    public static final String SCHEMA_VERSION = "rating-v1";

    /** Factor de duração para {@code days} dias. */
    public record DurationFactor(int days, BigDecimal factor) {

        public DurationFactor {
            Objects.requireNonNull(factor, "factor");
        }
    }

    public RatingRules {
        durationFactors = durationFactors == null ? List.of() : List.copyOf(durationFactors);
        coverages = List.copyOf(coverages);
    }

    /** Primeira entrada de {@code coverages} para o código. */
    public Optional<CoverageTariff> coverage(CoverageCode code) {
        return coverages.stream().filter(c -> c.coverageCode() == code).findFirst();
    }

    /** Factor da primeira entrada de {@code durationFactors} com {@code days}. */
    public Optional<BigDecimal> durationFactor(int days) {
        return durationFactors.stream().filter(d -> d.days() == days).map(DurationFactor::factor).findFirst();
    }
}
