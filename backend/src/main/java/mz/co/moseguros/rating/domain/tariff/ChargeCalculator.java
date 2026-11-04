package mz.co.moseguros.rating.domain.tariff;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import mz.co.moseguros.organization.ChargeRule;
import mz.co.moseguros.organization.ChargeType;
import mz.co.moseguros.rating.domain.tariff.PremiumBreakdown.ChargeLine;

/**
 * Encargos da seguradora (S3-01 §9, passo 6), pela ordem configurada e todos sobre o prémio
 * comercial (não há encargo sobre encargo): {@code PERCENT} → {@code (PC × percent / 100)}
 * arredondado ao centavo ({@code HALF_UP}); {@code FIXED} → o valor.
 */
public final class ChargeCalculator {

    private ChargeCalculator() {
    }

    public static List<ChargeLine> calculate(long commercialPremium, List<ChargeRule> rules) {
        List<ChargeLine> lines = new ArrayList<>();
        if (rules == null) {
            return lines;
        }
        for (ChargeRule r : rules) {
            long amount = r.type() == ChargeType.PERCENT
                    ? BigDecimal.valueOf(commercialPremium).multiply(r.percent()).movePointLeft(2)
                            .setScale(0, RoundingMode.HALF_UP).longValueExact()
                    : r.fixedAmount();
            lines.add(new ChargeLine(r.code(), r.labelPt(), r.labelEn(), r.type(), r.percent(), r.fixedAmount(), amount));
        }
        return lines;
    }
}
