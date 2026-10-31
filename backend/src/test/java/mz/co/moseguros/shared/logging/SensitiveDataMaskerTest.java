package mz.co.moseguros.shared.logging;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class SensitiveDataMaskerTest {

    @Test
    void masksMozambicanMobileNumbersKeepingLastFour() {
        assertThat(SensitiveDataMasker.mask("OTP sent to 258841234567"))
                .isEqualTo("OTP sent to 258*****4567");
        assertThat(SensitiveDataMasker.mask("to=+258841234567,"))
                .isEqualTo("to=+258*****4567,");
    }

    @Test
    void doesNotTouchLongerOrShorterDigitRuns() {
        assertThat(SensitiveDataMasker.mask("id 12588412345678")).isEqualTo("id 12588412345678");
        assertThat(SensitiveDataMasker.mask("amount 25884")).isEqualTo("amount 25884");
    }

    @Test
    void masksNuitAfterKeyword() {
        assertThat(SensitiveDataMasker.mask("nuit=123456789")).isEqualTo("nuit=*****6789");
        assertThat(SensitiveDataMasker.mask("NUIT: 400005834")).isEqualTo("NUIT: *****5834");
    }

    @Test
    void masksBearerTokens() {
        assertThat(SensitiveDataMasker.mask("Authorization: Bearer eyJhbGciOi.abc-def_123="))
                .isEqualTo("Authorization: Bearer ***");
    }

    @Test
    void leavesOrdinaryTextUnchanged() {
        assertThat(SensitiveDataMasker.mask("policy AP-2026-000123 issued")).isEqualTo("policy AP-2026-000123 issued");
        assertThat(SensitiveDataMasker.mask(null)).isNull();
        assertThat(SensitiveDataMasker.mask("")).isEmpty();
    }
}
