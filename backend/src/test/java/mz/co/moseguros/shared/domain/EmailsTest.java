package mz.co.moseguros.shared.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import mz.co.moseguros.shared.error.DomainException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

class EmailsTest {

    @Test
    void normalizesToLowerCaseWithoutSpaces() {
        assertThat(Emails.normalize("  Ana.Silva @Exemplo.CO.MZ ")).isEqualTo("ana.silva@exemplo.co.mz");
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "ana", "ana@", "@exemplo.co.mz", "ana@exemplo", "ana@@exemplo.co.mz", "a@b@c.mz"})
    void rejectsInvalid(String raw) {
        assertThatThrownBy(() -> Emails.normalize(raw))
                .isInstanceOfSatisfying(DomainException.class, e -> {
                    assertThat(e.getCode()).isEqualTo("INVALID_EMAIL");
                    assertThat(e.getHttpStatus()).isEqualTo(400);
                });
        assertThat(Emails.isValid(raw)).isFalse();
    }

    @Test
    void rejectsLongerThan254() {
        String local = "a".repeat(64);
        String domain = "b".repeat(185) + ".co.mz"; // 64 + 1 + 191 = 256
        assertThat(Emails.isValid(local + "@" + domain)).isFalse();
        assertThat(Emails.isValid(local + "@" + "b".repeat(183) + ".co.mz")).isTrue(); // 254
    }

    @Test
    void blankIsNullWhenOptional() {
        assertThat(Emails.normalizeOrNull("  ")).isNull();
        assertThat(Emails.normalizeOrNull(null)).isNull();
        assertThat(Emails.normalizeOrNull("X@Y.MZ")).isEqualTo("x@y.mz");
    }
}
