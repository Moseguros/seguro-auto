package mz.co.moseguros.proposal;

import java.math.BigDecimal;

import mz.co.moseguros.shared.domain.Money;

/**
 * Cobertura de uma proposta para outros módulos ({@link ProposalView}, S4-02 §12), como foi cotada.
 *
 * @param capital           {@code null} se a cobertura não tiver capital
 * @param deductibleType    {@code NONE}, {@code FIXED} ou {@code PERCENT}
 * @param deductibleAmount  só em {@code FIXED}
 * @param deductiblePercent só em {@code PERCENT} ({@code 10} = 10 %)
 */
public record CoverageLineView(String code, boolean mandatory, Money capital, String deductibleType,
                               Money deductibleAmount, BigDecimal deductiblePercent, Money premium) {
}
