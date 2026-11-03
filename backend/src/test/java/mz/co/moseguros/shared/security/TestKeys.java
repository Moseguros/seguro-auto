package mz.co.moseguros.shared.security;

import java.security.KeyPair;
import java.util.Base64;

/**
 * Chaves RSA para testes (geradas por {@link JwtKeyConfig#generate()}).
 */
public final class TestKeys {

    private TestKeys() {
    }

    public static KeyPair rsa() {
        return JwtKeyConfig.generate();
    }

    /** Chave privada em PEM PKCS#8. */
    public static String pem(KeyPair keyPair) {
        return "-----BEGIN PRIVATE KEY-----\n"
                + Base64.getMimeEncoder(64, "\n".getBytes()).encodeToString(keyPair.getPrivate().getEncoded())
                + "\n-----END PRIVATE KEY-----\n";
    }
}
