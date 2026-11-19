package mz.co.moseguros.shared.captcha;

import java.net.URI;
import java.time.Duration;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

/**
 * {@code moseguros.captcha} (S6-03 §7).
 *
 * @param provider  {@code none} ou {@code turnstile} (por defeito {@code turnstile}: esquecer a
 *                  configuração nunca desliga o CAPTCHA)
 * @param turnstile configuração do Cloudflare Turnstile; o segredo fica no cofre ({@code secret-path}),
 *                  nunca aqui
 */
@ConfigurationProperties(prefix = "moseguros.captcha")
public record CaptchaProperties(@DefaultValue("turnstile") Provider provider, @DefaultValue Turnstile turnstile) {

    /** Valor de {@code moseguros.captcha.provider}. */
    public enum Provider {
        NONE,
        TURNSTILE
    }

    /**
     * @param siteKey          chave pública do site (vai para a web em {@code /public/verifications/config})
     * @param secretPath       caminho do segredo no cofre ({@code SecretStore}, mount {@code secret})
     * @param allowedHostnames anfitriões aceites no {@code hostname} da resposta
     * @param verifyUrl        endpoint {@code siteverify}
     * @param timeout          tempo máximo da ligação e da leitura
     */
    public record Turnstile(String siteKey,
                            @DefaultValue("secret/platform/captcha/turnstile") String secretPath,
                            @DefaultValue List<String> allowedHostnames,
                            @DefaultValue("https://challenges.cloudflare.com/turnstile/v0/siteverify") URI verifyUrl,
                            @DefaultValue("PT3S") Duration timeout) {

        public Turnstile {
            allowedHostnames = allowedHostnames == null ? List.of()
                    : allowedHostnames.stream().filter(h -> h != null && !h.isBlank()).map(String::strip).toList();
        }
    }
}
