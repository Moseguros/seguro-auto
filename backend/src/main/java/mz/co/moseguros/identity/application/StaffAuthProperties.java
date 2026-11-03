package mz.co.moseguros.identity.application;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

/**
 * Login de staff e recuperação de palavra-passe (S1-05).
 *
 * @param maxFailedAttempts   falhas seguidas que bloqueiam a conta (5)
 * @param lockDuration        duração do bloqueio (15 min)
 * @param webBaseUrl          endereço da web; o link de recuperação é {@code {webBaseUrl}/recuperar/{token}}
 *                            ({@code moseguros.web.base-url})
 * @param resetValidity       validade de um pedido de recuperação (30 min)
 * @param maxResetsPerHour    e-mails de recuperação por conta por hora (3)
 */
@ConfigurationProperties(prefix = "moseguros.staff-auth")
public record StaffAuthProperties(
        @DefaultValue("5") int maxFailedAttempts,
        @DefaultValue("PT15M") Duration lockDuration,
        @DefaultValue("http://localhost:5173") String webBaseUrl,
        @DefaultValue("PT30M") Duration resetValidity,
        @DefaultValue("3") int maxResetsPerHour) {

    /** Link de recuperação, sem barra a mais. */
    public String resetLink(String token) {
        String base = webBaseUrl.endsWith("/") ? webBaseUrl.substring(0, webBaseUrl.length() - 1) : webBaseUrl;
        return base + "/recuperar/" + token;
    }
}
