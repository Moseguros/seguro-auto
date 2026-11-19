package mz.co.moseguros.paymentworker.sftp;

import static org.assertj.core.api.Assertions.assertThat;

import java.security.KeyPairGenerator;
import java.security.PublicKey;

import org.junit.jupiter.api.Test;

/** Chave do servidor SFTP pela impressão digital {@code SHA256:…} (S5-09 §5, passo 3). */
class FingerprintHostKeyVerifierTest {

    static PublicKey key() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        return generator.generateKeyPair().getPublic();
    }

    @Test
    void acceptsTheConfiguredFingerprintWithOrWithoutPadding() throws Exception {
        PublicKey key = key();
        String fingerprint = FingerprintHostKeyVerifier.fingerprint(key);
        assertThat(fingerprint).startsWith("SHA256:").doesNotEndWith("=");
        assertThat(new FingerprintHostKeyVerifier(fingerprint, false).verifyServerKey(null, null, key)).isTrue();
        assertThat(new FingerprintHostKeyVerifier(fingerprint + "=", false).verifyServerKey(null, null, key)).isTrue();
    }

    @Test
    void refusesAnotherKey() throws Exception {
        FingerprintHostKeyVerifier verifier =
                new FingerprintHostKeyVerifier(FingerprintHostKeyVerifier.fingerprint(key()), true);
        assertThat(verifier.verifyServerKey(null, null, key())).isFalse();
        assertThat(verifier.rejected()).isTrue();
    }

    @Test
    void withoutFingerprintOnlyAcceptsWhenUnknownKeysAreAllowed() throws Exception {
        assertThat(new FingerprintHostKeyVerifier(null, true).verifyServerKey(null, null, key())).isTrue();
        FingerprintHostKeyVerifier strict = new FingerprintHostKeyVerifier(" ", false);
        assertThat(strict.verifyServerKey(null, null, key())).isFalse();
        assertThat(strict.rejected()).isTrue();
    }
}
