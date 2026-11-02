package mz.co.moseguros.shared.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import mz.co.moseguros.shared.error.DomainException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

class NuitTest {

    @ParameterizedTest
    @CsvSource({
        "123456789,123456789",
        "'123 456 789',123456789",
        "' 400123456 ',400123456",
        "000000000,000000000",
        "'1 2 3 4 5 6 7 8 9',123456789",
        "999999999,999999999"
    })
    void acceptsNineDigitsIgnoringSpaces(String raw, String expected) {
        assertThat(Nuit.of(raw).value()).isEqualTo(expected);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "12345678", "1234567890", "12345678a", "123-456-789", "１２３４５６７８９", "abcdefghi"})
    void rejectsInvalid(String raw) {
        assertThatThrownBy(() -> Nuit.of(raw))
                .isInstanceOf(DomainException.class)
                .extracting(e -> ((DomainException) e).getCode())
                .isEqualTo("INVALID_NUIT");
    }

    @Test
    void masksAllButLastFour() {
        assertThat(Nuit.of("123456789").masked()).isEqualTo("*****6789");
        assertThat(Nuit.of("123456789").toString()).isEqualTo("*****6789");
    }
}
