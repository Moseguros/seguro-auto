package mz.co.moseguros.catalog.domain;

import java.math.BigDecimal;
import java.util.List;

import mz.co.moseguros.catalog.CapitalType;
import mz.co.moseguros.catalog.CoverageCode;
import mz.co.moseguros.catalog.DeductibleType;
import mz.co.moseguros.shared.domain.Money;

/**
 * Dados de uma cobertura, tal como pedidos ({@code CoverageRequest}) ou gravados
 * ({@link Coverage#toSpec()}). Os campos podem vir a {@code null} num pedido: a validação é de
 * {@link CoverageRules}.
 */
public record CoverageSpec(
        CoverageCode code,
        Boolean mandatory,
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

    /** {@code requiresCodes}, ou a lista vazia. */
    public List<CoverageCode> requiresOrEmpty() {
        return requiresCodes == null ? List.of() : requiresCodes;
    }

    /** {@code territorialScope}, ou a lista vazia. */
    public List<String> scopeOrEmpty() {
        return territorialScope == null ? List.of() : territorialScope;
    }
}
