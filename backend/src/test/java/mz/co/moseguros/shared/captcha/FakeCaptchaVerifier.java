package mz.co.moseguros.shared.captcha;

import java.util.Optional;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

/**
 * CAPTCHA falso dos testes de integração (perfil {@code test}, S6-03 §7): faz-se passar pelo Turnstile (o
 * cabeçalho é exigido) e responde {@code INVALID} ao token {@code "invalid"}, {@code UNAVAILABLE} a
 * {@code "down"} e {@code VALID} aos outros.
 */
@Configuration(proxyBeanMethods = false)
@Profile("test")
public class FakeCaptchaVerifier {

    public static final String SITE_KEY = "test-site-key";

    @Bean
    @Primary
    CaptchaVerifier testCaptchaVerifier() {
        return new CaptchaVerifier() {
            @Override
            public CaptchaProvider provider() {
                return CaptchaProvider.TURNSTILE;
            }

            @Override
            public Optional<String> siteKey() {
                return Optional.of(SITE_KEY);
            }

            @Override
            public CaptchaResult verify(String token, String clientIp) {
                return switch (token) {
                    case "invalid" -> CaptchaResult.INVALID;
                    case "down" -> CaptchaResult.UNAVAILABLE;
                    default -> CaptchaResult.VALID;
                };
            }
        };
    }
}
