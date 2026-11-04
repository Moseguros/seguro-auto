package mz.co.moseguros.shared.format;

import static org.assertj.core.api.Assertions.assertThat;

import mz.co.moseguros.shared.domain.Money;
import org.junit.jupiter.api.Test;

/** {@link MoneyFormat}: igual ao {@code formatMoney} da web (S3-03 §9). */
class MoneyFormatTest {

    @Test
    void portugueseUsesNonBreakingSpacesAndDecimalComma() {
        assertThat(MoneyFormat.format(Money.ofCentavos(466300), "pt")).isEqualTo("4 663,00 MT");
        assertThat(MoneyFormat.format(Money.ofCentavos(1234567), null)).isEqualTo("12 345,67 MT");
        assertThat(MoneyFormat.format(Money.ofCentavos(99), "pt")).isEqualTo("0,99 MT");
        assertThat(MoneyFormat.format(Money.ofCentavos(0), "xx")).isEqualTo("0,00 MT");
        assertThat(MoneyFormat.format(Money.ofCentavos(100000), "pt")).isEqualTo("1 000,00 MT");
        assertThat(MoneyFormat.format(Money.ofCentavos(-99999), "pt")).isEqualTo("-999,99 MT");
    }

    @Test
    void englishPutsTheCurrencyFirst() {
        assertThat(MoneyFormat.format(Money.ofCentavos(1234567), "en")).isEqualTo("MZN 12,345.67");
        assertThat(MoneyFormat.format(Money.ofCentavos(300000000), "EN")).isEqualTo("MZN 3,000,000.00");
    }
}
