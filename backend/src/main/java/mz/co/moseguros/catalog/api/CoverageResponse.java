package mz.co.moseguros.catalog.api;

import java.math.BigDecimal;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import mz.co.moseguros.catalog.CapitalType;
import mz.co.moseguros.catalog.CoverageCode;
import mz.co.moseguros.catalog.CoverageView;
import mz.co.moseguros.catalog.DeductibleType;
import mz.co.moseguros.shared.domain.Money;

/**
 * Cobertura de uma versão. Os campos que não se aplicam ao tipo de capital ou de franquia são
 * omitidos; na lista do catálogo público também {@code requiresCodes}, {@code territorialScope},
 * {@code description} e {@code exclusions}.
 */
public record CoverageResponse(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        CoverageCode code,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        boolean mandatory,
        List<CoverageCode> requiresCodes,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        CapitalType capitalType,
        Money capital,
        List<Money> capitalOptions,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        DeductibleType deductibleType,
        Money deductibleAmount,
        @Schema(example = "10.00")
        BigDecimal deductiblePercent,
        Money deductibleMinAmount,
        List<String> territorialScope,
        String description,
        String exclusions) {

    static CoverageResponse full(CoverageView c) {
        return new CoverageResponse(c.code(), c.mandatory(), c.requiresCodes(), c.capitalType(), c.capital(),
                c.capitalOptions(), c.deductibleType(), c.deductibleAmount(), c.deductiblePercent(),
                c.deductibleMinAmount(), c.territorialScope(), c.description(), c.exclusions());
    }

    /** Só capital e franquia (lista do catálogo público). */
    static CoverageResponse summary(CoverageView c) {
        return new CoverageResponse(c.code(), c.mandatory(), null, c.capitalType(), c.capital(),
                c.capitalOptions(), c.deductibleType(), c.deductibleAmount(), c.deductiblePercent(),
                c.deductibleMinAmount(), null, null, null);
    }
}
