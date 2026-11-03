package mz.co.moseguros.rating.domain.tariff;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;

import mz.co.moseguros.organization.ChargeRule;
import mz.co.moseguros.organization.ChargeType;
import mz.co.moseguros.rating.domain.tariff.PremiumBreakdown.ChargeLine;
import org.junit.jupiter.api.Test;

/** Encargos sobre o prémio comercial (S3-01 §9, passo 6). */
class ChargeCalculatorTest {

    private static final ChargeRule STAMP = ChargeRule.percent("DEMO_STAMP", "Selo", "Stamp", new BigDecimal("2.5"));
    private static final ChargeRule FEE = ChargeRule.fixed("DEMO_FEE", "Taxa", "Fee", 5000);

    @Test
    void percentIsRoundedHalfUpAndFixedIsTheValue() {
        assertThat(ChargeCalculator.calculate(19_500, List.of(STAMP, FEE))).extracting(ChargeLine::amount)
                .containsExactly(488L, 5000L); // 487,5 → 488
        assertThat(ChargeCalculator.calculate(804_375, List.of(STAMP))).extracting(ChargeLine::amount)
                .containsExactly(20_109L); // 20 109,375 → 20 109
        assertThat(ChargeCalculator.calculate(4_163_500, List.of(STAMP))).extracting(ChargeLine::amount)
                .containsExactly(104_088L); // 104 087,5 → 104 088
    }

    @Test
    void allChargesAreOnTheCommercialPremiumInTheConfiguredOrder() {
        ChargeRule tenPercent = ChargeRule.percent("TEN", "Dez", "Ten", BigDecimal.TEN);
        List<ChargeLine> lines = ChargeCalculator.calculate(100_000, List.of(FEE, tenPercent, STAMP));
        assertThat(lines).extracting(ChargeLine::code).containsExactly("DEMO_FEE", "TEN", "DEMO_STAMP");
        assertThat(lines).extracting(ChargeLine::amount).containsExactly(5000L, 10_000L, 2500L);
        assertThat(lines.get(1).type()).isEqualTo(ChargeType.PERCENT);
        assertThat(lines.get(0).fixedAmount()).isEqualTo(5000L);
    }

    @Test
    void noChargesGiveAnEmptyList() {
        assertThat(ChargeCalculator.calculate(100, null)).isEmpty();
        assertThat(ChargeCalculator.calculate(100, List.of())).isEmpty();
    }
}
