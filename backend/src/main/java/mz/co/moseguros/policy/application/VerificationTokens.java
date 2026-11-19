package mz.co.moseguros.policy.application;

import java.security.SecureRandom;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Códigos de verificação dos certificados (S6-01 regra 6): 16 bytes de {@link SecureRandom} (128 bits) em
 * Base64 URL sem {@code =} (22 caracteres). O código nunca vai para logs. O URL de verificação é
 * {@code {moseguros.web.public-base-url}/v/{código}}.
 */
@Component
public class VerificationTokens {

    /** Comprimento do código. */
    public static final int LENGTH = 22;
    private static final int BYTES = 16;
    private static final Base64.Encoder ENCODER = Base64.getUrlEncoder().withoutPadding();

    private final SecureRandom random = new SecureRandom();
    private final String publicBaseUrl;

    public VerificationTokens(@Value("${moseguros.web.public-base-url}") String publicBaseUrl) {
        this.publicBaseUrl = publicBaseUrl.endsWith("/") ? publicBaseUrl.substring(0, publicBaseUrl.length() - 1)
                : publicBaseUrl;
    }

    /** Novo código aleatório. */
    public String newToken() {
        byte[] bytes = new byte[BYTES];
        random.nextBytes(bytes);
        return ENCODER.encodeToString(bytes);
    }

    /** {@code {public-base-url}/v/{token}}. */
    public String verificationUrl(String token) {
        return publicBaseUrl + "/v/" + token;
    }
}
