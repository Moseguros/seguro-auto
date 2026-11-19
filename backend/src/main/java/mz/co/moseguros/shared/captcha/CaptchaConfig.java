package mz.co.moseguros.shared.captcha;

import mz.co.moseguros.shared.secrets.SecretStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;

/**
 * Escolhe o {@link CaptchaVerifier} (S6-03 §7) por {@code moseguros.captcha.provider}: {@code none} →
 * {@link AlwaysValidCaptchaVerifier}; {@code turnstile} → {@link TurnstileCaptchaVerifier} (exige
 * {@code site-key} e {@code allowed-hostnames}). {@code none} com o perfil {@code prod} impede o arranque;
 * com {@code staging} regista um {@code WARN}.
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(CaptchaProperties.class)
public class CaptchaConfig {

    private static final Logger log = LoggerFactory.getLogger(CaptchaConfig.class);

    @Bean
    CaptchaVerifier captchaVerifier(CaptchaProperties properties, SecretStore secrets, Environment environment) {
        return create(properties, secrets, environment);
    }

    static CaptchaVerifier create(CaptchaProperties properties, SecretStore secrets, Environment environment) {
        if (properties.provider() == CaptchaProperties.Provider.NONE) {
            if (environment.acceptsProfiles(Profiles.of("prod"))) {
                throw new IllegalStateException(
                        "moseguros.captcha.provider=none is not allowed with the 'prod' profile");
            }
            if (environment.acceptsProfiles(Profiles.of("staging"))) {
                log.warn("CAPTCHA disabled (moseguros.captcha.provider=none): plate verification is not protected");
            }
            return new AlwaysValidCaptchaVerifier();
        }
        CaptchaProperties.Turnstile turnstile = properties.turnstile();
        if (turnstile.siteKey() == null || turnstile.siteKey().isBlank()) {
            throw new IllegalStateException("moseguros.captcha.turnstile.site-key (MOSEGUROS_CAPTCHA_SITE_KEY) is "
                    + "required with moseguros.captcha.provider=turnstile");
        }
        if (turnstile.allowedHostnames().isEmpty()) {
            throw new IllegalStateException("moseguros.captcha.turnstile.allowed-hostnames "
                    + "(MOSEGUROS_CAPTCHA_ALLOWED_HOSTNAMES) is required with moseguros.captcha.provider=turnstile");
        }
        return new TurnstileCaptchaVerifier(turnstile, secrets);
    }
}
