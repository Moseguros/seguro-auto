package mz.co.moseguros.shared.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.logging.SensitiveDataMasker;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

class MsisdnTest {

    @ParameterizedTest
    @ValueSource(strings = {
        "+258841234567",
        "258841234567",
        "841234567",
        "84 123 4567",
        "+258 84-123-4567",
        "258-84-123-45-67",
        " 841234567 "
    })
    void acceptsValidFormatsAndNormalizes(String raw) {
        Msisdn m = Msisdn.of(raw);
        assertThat(m.value()).isEqualTo("258841234567");
        assertThat(m.e164()).isEqualTo("+258841234567");
        assertThat(m.national()).isEqualTo("841234567");
    }

    @ParameterizedTest
    @ValueSource(strings = {"821234567", "831234567", "851234567", "861234567", "871234567"})
    void acceptsAllMobilePrefixes82To87(String raw) {
        assertThat(Msisdn.of(raw).national()).isEqualTo(raw);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {
        "",
        "123",
        "811234567",       // 81 não é móvel
        "881234567",       // 88 não é móvel
        "211234567",       // fixo
        "84123456",        // 8 dígitos
        "8412345678",      // 10 dígitos
        "259841234567",    // outro indicativo
        "+841234567",      // "+" sem indicativo
        "84123456a",
        "+351912345678"
    })
    void rejectsInvalid(String raw) {
        assertThatThrownBy(() -> Msisdn.of(raw))
                .isInstanceOf(DomainException.class)
                .satisfies(e -> {
                    DomainException d = (DomainException) e;
                    assertThat(d.getCode()).isEqualTo("INVALID_MSISDN");
                    assertThat(d.getHttpStatus()).isEqualTo(400);
                });
    }

    @Test
    void maskShowsOnlyLastFourDigits() {
        assertThat(Msisdn.of("841234567").masked()).isEqualTo("+258*****4567");
    }

    @Test
    void maskIsTheSameAsTheLogMasker() {
        Msisdn m = Msisdn.of("84 765 4321");
        assertThat(SensitiveDataMasker.mask(m.e164())).isEqualTo(m.masked());
    }

    @Test
    void toStringNeverExposesTheFullNumber() {
        assertThat(Msisdn.of("841234567").toString()).doesNotContain("841234567");
    }

    @Test
    void equalityIsByNormalizedValue() {
        assertThat(Msisdn.of("+258 84 123 4567")).isEqualTo(Msisdn.of("841234567"));
    }
}
