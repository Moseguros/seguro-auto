package mz.co.moseguros.identity.application;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

/**
 * Convites de staff e primeiro administrador da plataforma (S1-04).
 *
 * @param webBaseUrl     endereço da web; o link do convite é {@code {webBaseUrl}/convite/{token}}
 *                       ({@code moseguros.web.base-url})
 * @param validity       validade de um convite (7 dias)
 * @param maxActivationAttempts códigos TOTP errados que revogam o convite (5)
 */
@ConfigurationProperties(prefix = "moseguros.invitation")
public record InvitationProperties(
        @DefaultValue("http://localhost:5173") String webBaseUrl,
        @DefaultValue("P7D") Duration validity,
        @DefaultValue("5") int maxActivationAttempts) {

    /** Link do convite, sem barra a mais. */
    public String link(String token) {
        String base = webBaseUrl.endsWith("/") ? webBaseUrl.substring(0, webBaseUrl.length() - 1) : webBaseUrl;
        return base + "/convite/" + token;
    }
}
