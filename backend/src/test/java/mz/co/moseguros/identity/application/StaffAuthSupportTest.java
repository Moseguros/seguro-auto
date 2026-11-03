package mz.co.moseguros.identity.application;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.time.Instant;

import org.junit.jupiter.api.Test;

/** Utilitários do login de staff, da recuperação e da lista de utilizadores (S1-05). */
class StaffAuthSupportTest {

    @Test
    void retryAfterIsRoundedUpWithAMinimumOfOneSecond() {
        Instant now = Instant.parse("2026-11-02T10:00:00Z");
        assertThat(StaffLoginService.secondsUntil(now.plusSeconds(900), now)).isEqualTo(900);
        assertThat(StaffLoginService.secondsUntil(now.plusMillis(899_001), now)).isEqualTo(900);
        assertThat(StaffLoginService.secondsUntil(now.plusMillis(10), now)).isEqualTo(1);
        assertThat(StaffLoginService.secondsUntil(now, now)).isEqualTo(1);
    }

    @Test
    void resetLinkHasNoDoubleSlash() {
        StaffAuthProperties withSlash = new StaffAuthProperties(5, Duration.ofMinutes(15), "https://app.moseguros.co.mz/",
                Duration.ofMinutes(30), 3);
        StaffAuthProperties withoutSlash = new StaffAuthProperties(5, Duration.ofMinutes(15),
                "http://localhost:5173", Duration.ofMinutes(30), 3);

        assertThat(withSlash.resetLink("abc")).isEqualTo("https://app.moseguros.co.mz/recuperar/abc");
        assertThat(withoutSlash.resetLink("abc")).isEqualTo("http://localhost:5173/recuperar/abc");
    }

    @Test
    void searchTermsEscapeLikeWildcards() {
        assertThat(StaffUserService.escapeLike("50%_a\\b")).isEqualTo("50\\%\\_a\\\\b");
        assertThat(StaffUserService.escapeLike("ana")).isEqualTo("ana");
    }
}
