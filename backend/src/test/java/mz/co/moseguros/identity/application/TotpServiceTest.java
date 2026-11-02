package mz.co.moseguros.identity.application;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import org.apache.commons.codec.binary.Base32;
import org.junit.jupiter.api.Test;

/**
 * TOTP (RFC 6238): SHA-1, 6 dígitos, 30 s; aceita os passos −1, 0 e +1.
 */
class TotpServiceTest {

    /** Segredo do anexo B da RFC 6238 ("12345678901234567890") em Base32. */
    static final String RFC_SECRET = "GEZDGNBVGY3TQOJQGEZDGNBVGY3TQOJQ";

    static final Instant NOW = Instant.parse("2026-11-02T10:00:15Z");

    private final TotpService totp = new TotpService(Clock.fixed(NOW, ZoneOffset.UTC));

    @Test
    void generatesTheRfcTestVectors() {
        // RFC 6238, anexo B (SHA-1, 8 dígitos): os últimos 6 dígitos são o código de 6 dígitos
        assertThat(totp.generate(RFC_SECRET, 59 / 30)).isEqualTo("287082");
        assertThat(totp.generate(RFC_SECRET, 1111111109L / 30)).isEqualTo("081804");
        assertThat(totp.generate(RFC_SECRET, 2000000000L / 30)).isEqualTo("279037");
    }

    @Test
    void acceptsThePreviousCurrentAndNextSteps() {
        long step = totp.currentStep();
        assertThat(step).isEqualTo(NOW.getEpochSecond() / 30);
        assertThat(totp.verify(RFC_SECRET, totp.generate(RFC_SECRET, step - 1), null)).hasValue(step - 1);
        assertThat(totp.verify(RFC_SECRET, totp.generate(RFC_SECRET, step), null)).hasValue(step);
        assertThat(totp.verify(RFC_SECRET, totp.generate(RFC_SECRET, step + 1), null)).hasValue(step + 1);
    }

    @Test
    void rejectsStepsOutsideTheWindow() {
        long step = totp.currentStep();
        assertThat(totp.verify(RFC_SECRET, totp.generate(RFC_SECRET, step + 2), null)).isEmpty();
        assertThat(totp.verify(RFC_SECRET, totp.generate(RFC_SECRET, step - 2), null)).isEmpty();
    }

    @Test
    void rejectsAlreadyUsedSteps() {
        long step = totp.currentStep();
        String code = totp.generate(RFC_SECRET, step);
        assertThat(totp.verify(RFC_SECRET, code, step)).isEmpty();
        assertThat(totp.verify(RFC_SECRET, totp.generate(RFC_SECRET, step + 1), step)).hasValue(step + 1);
    }

    @Test
    void rejectsMalformedCodes() {
        assertThat(totp.verify(RFC_SECRET, null, null)).isEmpty();
        assertThat(totp.verify(RFC_SECRET, "12345", null)).isEmpty();
        assertThat(totp.verify(RFC_SECRET, "12a456", null)).isEmpty();
        assertThat(totp.verify(null, "123456", null)).isEmpty();
    }

    @Test
    void newSecretsHaveTwentyRandomBytesInBase32() {
        String secret = totp.newSecret();
        assertThat(secret).hasSize(32).matches("^[A-Z2-7]{32}$");
        assertThat(new Base32().decode(secret)).hasSize(20);
        assertThat(totp.newSecret()).isNotEqualTo(secret);
    }

    @Test
    void otpauthUri() {
        assertThat(totp.otpauthUri("ana@seguradora.co.mz", "ABC"))
                .isEqualTo("otpauth://totp/MoSeguros:ana@seguradora.co.mz?secret=ABC&issuer=MoSeguros"
                        + "&algorithm=SHA1&digits=6&period=30");
    }
}
