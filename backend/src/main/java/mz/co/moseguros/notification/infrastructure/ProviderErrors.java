package mz.co.moseguros.notification.infrastructure;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.MailParseException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;

/**
 * Converte a excepção de um fornecedor num {@link ProviderResult} com um motivo curto e sem dados pessoais
 * (S6-04 §6): HTTP 400/404/410/422 (destino ou modelo inválido) → {@code PERMANENT}; timeout, erro de rede,
 * 5xx, 429 e o resto → {@code TEMPORARY}.
 */
public final class ProviderErrors {

    private static final Logger log = LoggerFactory.getLogger(ProviderErrors.class);
    private static final Set<Integer> PERMANENT_HTTP = Set.of(400, 404, 410, 422);

    private ProviderErrors() {
    }

    public static ProviderResult classify(String provider, RuntimeException e) {
        if (e instanceof RestClientResponseException http) {
            int status = http.getStatusCode().value();
            String reason = "HTTP " + status;
            return PERMANENT_HTTP.contains(status) ? ProviderResult.permanent(provider, reason)
                    : ProviderResult.temporary(provider, reason);
        }
        if (e instanceof ResourceAccessException) {
            return ProviderResult.temporary(provider, "provider unreachable or timeout");
        }
        if (e instanceof MailParseException) {
            return ProviderResult.permanent(provider, "smtp error: " + e.getClass().getSimpleName());
        }
        if (e instanceof MailException) {
            return ProviderResult.temporary(provider, "smtp error: " + e.getClass().getSimpleName());
        }
        log.warn("Unexpected error from provider {} ({})", provider, e.getClass().getName());
        return ProviderResult.temporary(provider, "provider error: " + e.getClass().getSimpleName());
    }
}
