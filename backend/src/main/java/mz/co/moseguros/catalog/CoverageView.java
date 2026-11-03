package mz.co.moseguros.catalog;

import java.math.BigDecimal;
import java.util.List;

import mz.co.moseguros.shared.domain.Money;

/**
 * Cobertura de uma versão de produto, para outros módulos ({@link CatalogApi}).
 *
 * @param capital           em {@code FIXED} e {@code PER_PERSON} (por pessoa); senão {@code null}
 * @param capitalOptions    em {@code OPTIONS}; senão {@code null}
 * @param deductibleAmount  em franquia {@code FIXED}; senão {@code null}
 * @param deductiblePercent em franquia {@code PERCENT}; senão {@code null}
 * @param territorialScope  códigos ISO 3166-1 alfa-2 (inclui sempre {@code MZ})
 */
public record CoverageView(
        CoverageCode code,
        boolean mandatory,
        List<CoverageCode> requiresCodes,
        CapitalType capitalType,
        Money capital,
        List<Money> capitalOptions,
        DeductibleType deductibleType,
        Money deductibleAmount,
        BigDecimal deductiblePercent,
        Money deductibleMinAmount,
        List<String> territorialScope,
        String description,
        String exclusions) {
}
