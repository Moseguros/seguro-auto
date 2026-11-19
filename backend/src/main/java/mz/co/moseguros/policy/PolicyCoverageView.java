package mz.co.moseguros.policy;

import java.math.BigDecimal;

import mz.co.moseguros.shared.domain.Money;

/**
 * Cobertura de uma apólice (S6-01 §9): cópia do {@code CoverageLineView} da proposta, como foi cotada.
 *
 * @param capital           {@code null} se a cobertura não tiver capital
 * @param deductibleType    {@code NONE}, {@code FIXED} ou {@code PERCENT}
 * @param deductibleAmount  só em {@code FIXED}
 * @param deductiblePercent só em {@code PERCENT} ({@code 10} = 10 %)
 */
public record PolicyCoverageView(String code, boolean mandatory, Money capital, String deductibleType,
                                 Money deductibleAmount, BigDecimal deductiblePercent, Money premium) {
}
