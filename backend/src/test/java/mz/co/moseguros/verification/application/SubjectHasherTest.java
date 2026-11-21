package mz.co.moseguros.verification.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Base64;

import mz.co.moseguros.verification.domain.SubjectType;
import org.junit.jupiter.api.Test;

/** {@link SubjectHasher} (S6-03 §4): HMAC-SHA256 com a chave da verificação. */
class SubjectHasherTest {

    static final String KEY_A = Base64.getEncoder().encodeToString(new byte[32]);
    static final String KEY_B = Base64.getEncoder().encodeToString("0123456789abcdef0123456789abcdef".getBytes());

    @Test
    void sameValueGivesTheSameHash() {
        SubjectHasher hasher = new SubjectHasher(KEY_A);
        String hash = hasher.hash(SubjectType.PLATE, "ABC-123-MC");
        assertThat(hash).hasSize(64).matches("[0-9a-f]{64}");
        assertThat(new SubjectHasher(KEY_A).hash(SubjectType.PLATE, "ABC-123-MC")).isEqualTo(hash);
    }

    @Test
    void differentKeysOrTypesGiveDifferentHashes() {
        String a = new SubjectHasher(KEY_A).hash(SubjectType.PLATE, "ABC-123-MC");
        assertThat(new SubjectHasher(KEY_B).hash(SubjectType.PLATE, "ABC-123-MC")).isNotEqualTo(a);
        assertThat(new SubjectHasher(KEY_A).hash(SubjectType.TOKEN, "ABC-123-MC")).isNotEqualTo(a);
        assertThat(new SubjectHasher(KEY_A).hash(SubjectType.PLATE, "ABC-124-MC")).isNotEqualTo(a);
    }

    @Test
    void theHashDoesNotContainThePlate() {
        String hash = new SubjectHasher(KEY_A).hash(SubjectType.PLATE, "ABC-123-MC");
        assertThat(hash).doesNotContainIgnoringCase("ABC").doesNotContain("123").doesNotContainIgnoringCase("MC");
        assertThat(SubjectHasher.forLog(hash)).hasSize(12).isEqualTo(hash.substring(0, 12));
        assertThat(new SubjectHasher(KEY_A).toString()).doesNotContain(KEY_A);
    }

    @Test
    void refusesAMissingOrShortKey() {
        assertThatThrownBy(() -> new SubjectHasher((String) null)).isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("MOSEGUROS_VERIFICATION_HASH_KEY");
        assertThatThrownBy(() -> new SubjectHasher(" ")).isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> new SubjectHasher(Base64.getEncoder().encodeToString(new byte[16])))
                .isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> new SubjectHasher("not base64!")).isInstanceOf(IllegalStateException.class);
    }
}
