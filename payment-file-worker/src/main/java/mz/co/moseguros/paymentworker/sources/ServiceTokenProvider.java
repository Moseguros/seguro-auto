package mz.co.moseguros.paymentworker.sources;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.function.Function;

import mz.co.moseguros.paymentworker.config.WorkerProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

/**
 * Token de serviço do backend (S5-09 §1 e §4): {@code client_credentials} com Basic em
 * {@code POST /internal/v1/auth/token}. Guarda-o até {@code expires_in − 60 s}; um 401 de um pedido
 * autenticado ({@link #withToken}) força um token novo e repete uma vez. O token e o segredo nunca vão
 * para os logs.
 */
@Component
public class ServiceTokenProvider {

    static final Duration EXPIRY_MARGIN = Duration.ofSeconds(60);
    static final String TOKEN_PATH = "/internal/v1/auth/token";
    static final String SCOPE = "payments:file-sources";

    private static final Logger log = LoggerFactory.getLogger(ServiceTokenProvider.class);

    private final RestClient rest;
    private final WorkerProperties properties;
    private final Clock clock;
    private final Object lock = new Object();
    private volatile String token;
    private volatile Instant validUntil = Instant.MIN;

    public ServiceTokenProvider(RestClient backendRestClient, WorkerProperties properties, Clock clock) {
        this.rest = backendRestClient;
        this.properties = properties;
        this.clock = clock;
    }

    /** Token válido (pede um novo se não houver ou estiver perto de expirar). */
    public String token() {
        synchronized (lock) {
            if (token == null || !clock.instant().isBefore(validUntil)) {
                requestToken();
            }
            return token;
        }
    }

    /** Esquece o token (o backend recusou-o). */
    public void invalidate() {
        synchronized (lock) {
            token = null;
            validUntil = Instant.MIN;
        }
    }

    /**
     * Corre um pedido autenticado; se o backend responder 401, pede um token novo e repete uma vez.
     */
    public <T> T withToken(Function<String, T> call) {
        String current = token();
        try {
            return call.apply(current);
        } catch (HttpClientErrorException.Unauthorized e) {
            log.info("Backend refused the service token (401): requesting a new one");
            synchronized (lock) {
                if (current.equals(token)) {
                    invalidate();
                }
            }
            return call.apply(token());
        }
    }

    private void requestToken() {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "client_credentials");
        form.add("scope", SCOPE);
        Map<?, ?> body = rest.post()
                .uri(properties.backendBaseUrl() + TOKEN_PATH)
                .headers(h -> h.setBasicAuth(properties.clientId(),
                        properties.clientSecret() == null ? "" : properties.clientSecret()))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(form)
                .retrieve()
                .body(Map.class);
        Object accessToken = body == null ? null : body.get("access_token");
        if (!(accessToken instanceof String value) || value.isBlank()) {
            throw new IllegalStateException("The backend token response has no access_token");
        }
        long expiresIn = body.get("expires_in") instanceof Number n ? n.longValue() : 0;
        token = value;
        validUntil = clock.instant().plusSeconds(expiresIn).minus(EXPIRY_MARGIN);
        log.debug("Service token obtained (expires in {} s)", expiresIn);
    }
}
