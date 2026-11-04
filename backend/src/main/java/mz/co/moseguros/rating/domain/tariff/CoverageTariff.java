package mz.co.moseguros.rating.domain.tariff;

import java.util.List;
import java.util.Objects;

import mz.co.moseguros.catalog.CoverageCode;

/** Tarifa de uma cobertura: a base e os factores, pela ordem em que se aplicam. */
public record CoverageTariff(CoverageCode coverageCode, BaseTable base, List<FactorTable> factors) {

    public CoverageTariff {
        Objects.requireNonNull(coverageCode, "coverageCode");
        Objects.requireNonNull(base, "base");
        factors = factors == null ? List.of() : List.copyOf(factors);
    }
}
