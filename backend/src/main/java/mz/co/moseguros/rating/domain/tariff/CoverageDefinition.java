package mz.co.moseguros.rating.domain.tariff;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import mz.co.moseguros.catalog.CapitalType;
import mz.co.moseguros.catalog.CoverageCode;
import mz.co.moseguros.catalog.CoverageView;
import mz.co.moseguros.catalog.DeductibleType;
import mz.co.moseguros.shared.domain.Money;

/**
 * Cobertura de uma versão de produto, como o avaliador a vê (do {@link CoverageView} do catálogo),
 * com os valores em centavos.
 *
 * @param capital        {@code FIXED} e {@code PER_PERSON} (por pessoa); senão {@code null}
 * @param capitalOptions {@code OPTIONS}, por ordem crescente; senão {@code null}
 */
public record CoverageDefinition(CoverageCode code, boolean mandatory, List<CoverageCode> requiresCodes,
                                 CapitalType capitalType, Long capital, List<Long> capitalOptions,
                                 DeductibleType deductibleType, Long deductibleAmount, BigDecimal deductiblePercent,
                                 Long deductibleMinAmount) {

    public CoverageDefinition {
        Objects.requireNonNull(code, "code");
        Objects.requireNonNull(capitalType, "capitalType");
        requiresCodes = requiresCodes == null ? List.of() : List.copyOf(requiresCodes);
        capitalOptions = capitalOptions == null ? null : List.copyOf(capitalOptions);
        deductibleType = deductibleType == null ? DeductibleType.NONE : deductibleType;
    }

    public static CoverageDefinition from(CoverageView view) {
        return new CoverageDefinition(view.code(), view.mandatory(), view.requiresCodes(), view.capitalType(),
                amount(view.capital()),
                view.capitalOptions() == null ? null : view.capitalOptions().stream().map(Money::amount).toList(),
                view.deductibleType(), amount(view.deductibleAmount()), view.deductiblePercent(),
                amount(view.deductibleMinAmount()));
    }

    private static Long amount(Money money) {
        return money == null ? null : money.amount();
    }
}
