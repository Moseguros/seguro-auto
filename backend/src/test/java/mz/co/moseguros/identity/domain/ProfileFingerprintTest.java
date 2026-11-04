package mz.co.moseguros.identity.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDate;
import java.util.HexFormat;

import org.junit.jupiter.api.Test;

/** Impressão digital do perfil (S4-01 §4), com o caso de referência da ficha. */
class ProfileFingerprintTest {

    /** SHA-256 de {@code ANA MARIA MUSSA|1990-05-14|BI|110100123456A}. */
    private static final String EXPECTED = "d1d36fe875f9e8b9d8ab89a20af908a5ccb39fd218e7f2c8c976b0732a9e2cb2";

    @Test
    void referenceCase() throws Exception {
        String input = "ANA MARIA MUSSA|1990-05-14|BI|110100123456A";
        String digest = HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256")
                .digest(input.getBytes(StandardCharsets.UTF_8)));
        assertThat(digest).isEqualTo(EXPECTED);

        assertThat(ProfileFingerprint.of(" Ana Maria Mussa ", LocalDate.of(1990, 5, 14), IdDocType.BI,
                "110100123456A")).isEqualTo(EXPECTED);
    }

    @Test
    void anyChangeInTheFourValuesChangesTheFingerprint() {
        LocalDate birth = LocalDate.of(1990, 5, 14);
        String base = ProfileFingerprint.of("Ana Maria Mussa", birth, IdDocType.BI, "110100123456A");
        assertThat(ProfileFingerprint.of("ana maria mussa", birth, IdDocType.BI, "110100123456A")).isEqualTo(base);
        assertThat(ProfileFingerprint.of("Ana Mussa", birth, IdDocType.BI, "110100123456A")).isNotEqualTo(base);
        assertThat(ProfileFingerprint.of("Ana Maria Mussa", birth.plusDays(1), IdDocType.BI, "110100123456A"))
                .isNotEqualTo(base);
        assertThat(ProfileFingerprint.of("Ana Maria Mussa", birth, IdDocType.DIRE, "110100123456A"))
                .isNotEqualTo(base);
        assertThat(ProfileFingerprint.of("Ana Maria Mussa", birth, IdDocType.BI, "110100123456B"))
                .isNotEqualTo(base);
        assertThat(base).matches("^[0-9a-f]{64}$");
    }

    @Test
    void incompleteDataHasNoFingerprint() {
        assertThat(ProfileFingerprint.of("Ana Maria Mussa", LocalDate.of(1990, 5, 14), IdDocType.BI, null)).isNull();
        assertThat(ProfileFingerprint.of(null, LocalDate.of(1990, 5, 14), IdDocType.BI, "X")).isNull();
    }
}
