package mz.co.moseguros.shared.crypto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Map;

import mz.co.moseguros.shared.error.DomainException;
import org.junit.jupiter.api.Test;

class CryptoServiceTest {

    private static final String KEY_1 = newKey();
    private static final String KEY_2 = newKey();

    private final CryptoService service = new CryptoService(new CryptoProperties(Map.of(1, KEY_1), 1));

    @Test
    void sameValueEncryptsDifferentlyEachTime() {
        byte[] a = service.encrypt("123456789");
        byte[] b = service.encrypt("123456789");
        assertThat(a).isNotEqualTo(b);
    }

    @Test
    void decryptReturnsTheOriginal() {
        String original = "Bilhete 110100123456B — ç";
        assertThat(service.decrypt(service.encrypt(original))).isEqualTo(original);
    }

    @Test
    void formatIsKeyVersionThenIvThenCiphertextAndTag() {
        byte[] value = service.encrypt("123456789");
        assertThat(value[0]).isEqualTo((byte) 1);
        // 1 (versão) + 12 (IV) + 9 (texto) + 16 (etiqueta)
        assertThat(value).hasSize(1 + 12 + 9 + 16);
        assertThat(new String(value, StandardCharsets.ISO_8859_1)).doesNotContain("123456789");
    }

    @Test
    void nullStaysNull() {
        assertThat(service.encrypt(null)).isNull();
        assertThat(service.decrypt(null)).isNull();
    }

    @Test
    void tamperedValueFailsWithDecryptionFailed() {
        byte[] value = service.encrypt("123456789");
        value[value.length - 1] ^= 0x01;
        assertThatThrownBy(() -> service.decrypt(value))
                .isInstanceOfSatisfying(DomainException.class, e -> {
                    assertThat(e.getCode()).isEqualTo("DECRYPTION_FAILED");
                    assertThat(e.getHttpStatus()).isEqualTo(500);
                });
    }

    @Test
    void tamperedIvFails() {
        byte[] value = service.encrypt("123456789");
        value[3] ^= 0x01;
        assertThatThrownBy(() -> service.decrypt(value)).isInstanceOf(DomainException.class);
    }

    @Test
    void unknownKeyVersionOrTruncatedValueFails() {
        byte[] value = service.encrypt("123456789");
        value[0] = 9;
        assertThatThrownBy(() -> service.decrypt(value))
                .isInstanceOfSatisfying(DomainException.class, e -> assertThat(e.getCode()).isEqualTo("DECRYPTION_FAILED"));
        assertThatThrownBy(() -> service.decrypt(new byte[] {1, 2, 3}))
                .isInstanceOfSatisfying(DomainException.class, e -> assertThat(e.getCode()).isEqualTo("DECRYPTION_FAILED"));
    }

    @Test
    void valueFromKeyVersion1IsStillReadableAfterVersion2BecomesCurrent() {
        byte[] old = service.encrypt("123456789");

        CryptoService rotated = new CryptoService(new CryptoProperties(Map.of(1, KEY_1, 2, KEY_2), 2));

        assertThat(rotated.decrypt(old)).isEqualTo("123456789");
        byte[] fresh = rotated.encrypt("987654321");
        assertThat(fresh[0]).isEqualTo((byte) 2);
        assertThat(rotated.decrypt(fresh)).isEqualTo("987654321");
        // Sem a chave 2, o valor novo não é legível.
        assertThatThrownBy(() -> service.decrypt(fresh)).isInstanceOf(DomainException.class);
    }

    @Test
    void wrongKeyFails() {
        CryptoService other = new CryptoService(new CryptoProperties(Map.of(1, KEY_2), 1));
        assertThatThrownBy(() -> other.decrypt(service.encrypt("123456789"))).isInstanceOf(DomainException.class);
    }

    @Test
    void invalidConfigurationIsRejected() {
        assertThat(new CryptoProperties(Map.of(1, KEY_1), 2).isValid()).isFalse();          // versão sem chave
        assertThat(new CryptoProperties(Map.of(1, ""), 1).isValid()).isFalse();              // chave vazia
        assertThat(new CryptoProperties(Map.of(1, "c2hvcnQ="), 1).isValid()).isFalse();      // 5 bytes
        assertThat(new CryptoProperties(Map.of(1, "not base64!"), 1).isValid()).isFalse();
        assertThat(new CryptoProperties(Map.of(1, KEY_1), 1).isValid()).isTrue();
        assertThatThrownBy(() -> new CryptoService(new CryptoProperties(Map.of(1, ""), 1)))
                .isInstanceOf(IllegalStateException.class);
    }

    private static String newKey() {
        byte[] key = new byte[32];
        new SecureRandom().nextBytes(key);
        return Base64.getEncoder().encodeToString(key);
    }
}
