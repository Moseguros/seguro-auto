package mz.co.moseguros.verification.application;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

/**
 * {@code moseguros.verification} (S6-03 §4 a §6).
 *
 * @param hashKey   chave do HMAC do {@code subject_hash} (32 bytes em Base64,
 *                  {@code MOSEGUROS_VERIFICATION_HASH_KEY}); sem ela a aplicação não arranca
 * @param cacheTtl  tempo de vida das consultas à apólice em cache (60 s)
 * @param rateLimit pedidos por minuto
 */
@ConfigurationProperties(prefix = "moseguros.verification")
public record VerificationProperties(String hashKey, @DefaultValue("PT60S") Duration cacheTtl,
                                     @DefaultValue RateLimit rateLimit) {

    /**
     * @param publicPerMinute pedidos públicos por IP ({@code verifyCertificateToken} e {@code verifyPlate}
     *                        juntos)
     * @param policePerMinute pedidos de cada agente (os três endpoints da polícia)
     */
    public record RateLimit(@DefaultValue("60") int publicPerMinute, @DefaultValue("300") int policePerMinute) {

        public RateLimit {
            if (publicPerMinute < 1 || policePerMinute < 1) {
                throw new IllegalArgumentException("moseguros.verification.rate-limit.* must be >= 1");
            }
        }
    }
}
