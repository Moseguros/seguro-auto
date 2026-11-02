package mz.co.moseguros.shared.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;

import mz.co.moseguros.shared.error.DomainException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class MoneyTest {

    @Test
    void factories() {
        assertThat(Money.ofCentavos(299900)).isEqualTo(new Money(299900, "MZN"));
        assertThat(Money.zero().amount()).isZero();
        assertThat(Money.ofMeticais(new BigDecimal("2999.00")).amount()).isEqualTo(299900);
    }

    @ParameterizedTest
    @CsvSource({
        "10.005,1001",
        "10.004,1000",
        "10.0049,1000",
        "0.015,2",
        "-1.005,-101",
        "1,100",
        "1234.5,123450"
    })
    void ofMeticaisRoundsHalfUpToTwoDecimals(String meticais, long centavos) {
        assertThat(Money.ofMeticais(new BigDecimal(meticais)).amount()).isEqualTo(centavos);
    }

    @Test
    void plusAndMinus() {
        Money a = Money.ofCentavos(1050);
        Money b = Money.ofCentavos(200);
        assertThat(a.plus(b)).isEqualTo(Money.ofCentavos(1250));
        assertThat(a.minus(b)).isEqualTo(Money.ofCentavos(850));
        assertThat(b.minus(a).isNegative()).isTrue();
        assertThat(a.isNegative()).isFalse();
    }

    @Test
    void overflowFails() {
        assertThatThrownBy(() -> Money.ofCentavos(Long.MAX_VALUE).plus(Money.ofCentavos(1)))
                .isInstanceOf(ArithmeticException.class);
    }

    @ParameterizedTest
    @CsvSource({
        "1000,0.17,170",
        "999,0.5,500",      // 499.5 → 500
        "333,0.015,5",      // 4.995 → 5
        "100,3,300",
        "101,0.5,51",       // 50.5 → 51
        "-101,0.5,-51"      // HALF_UP afasta de zero
    })
    void timesRoundsHalfUp(long amount, String factor, long expected) {
        assertThat(Money.ofCentavos(amount).times(new BigDecimal(factor)).amount()).isEqualTo(expected);
    }

    @Test
    void compareTo() {
        assertThat(Money.ofCentavos(1)).isLessThan(Money.ofCentavos(2));
        assertThat(Money.ofCentavos(2).compareTo(Money.ofCentavos(2))).isZero();
    }

    @Test
    void onlyMzn() {
        assertThatThrownBy(() -> new Money(100, "USD"))
                .isInstanceOf(DomainException.class)
                .extracting(e -> ((DomainException) e).getCode())
                .isEqualTo("INVALID_CURRENCY");
        assertThatThrownBy(() -> new Money(100, null)).isInstanceOf(DomainException.class);
    }
}
