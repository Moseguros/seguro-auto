package mz.co.moseguros.shared.captcha;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.Optional;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import mz.co.moseguros.shared.secrets.SecretStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

/**
 * Cloudflare Turnstile (S6-03 §7): {@code POST siteverify} ({@code application/x-www-form-urlencoded}:
 * {@code secret}, {@code response}, {@code remoteip}) com {@code RestClient}, tempo máximo de
 * {@code timeout} (3 s) e circuit breaker {@code captcha} (janela de 20 chamadas, abre com 50 % de falhas,
 * 30 s aberto).
 *
 * <p>{@code success = true} e {@code hostname} em {@code allowed-hostnames} → {@link CaptchaResult#VALID};
 * {@code success = false} ou outro anfitrião → {@link CaptchaResult#INVALID}; tempo esgotado, erro de rede,
 * resposta de erro, cofre sem o segredo ou circuito aberto → {@link CaptchaResult#UNAVAILABLE} (falha
 * fechada). O segredo vem do cofre ({@code secret-path}) em cada pedido; nem ele nem o token vão para os
 * logs.
 */
public class TurnstileCaptchaVerifier implements CaptchaVerifier {

    static final String BREAKER = "captcha";
    /** Tamanho máximo de um token Turnstile (documentação da Cloudflare). */
    static final int MAX_TOKEN_LENGTH = 2048;
    private static final Logger log = LoggerFactory.getLogger(TurnstileCaptchaVerifier.class);

    private final CaptchaProperties.Turnstile properties;
    private final SecretStore secrets;
    private final RestClient http;
    private final CircuitBreaker breaker;

    public TurnstileCaptchaVerifier(CaptchaProperties.Turnstile properties, SecretStore secrets) {
        this.properties = properties;
        this.secrets = secrets;
        Duration timeout = properties.timeout();
        JdkClientHttpRequestFactory factory = new JdkClientHttpRequestFactory(HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .followRedirects(HttpClient.Redirect.NEVER)
                .connectTimeout(timeout)
                .build());
        factory.setReadTimeout(timeout);
        this.http = RestClient.builder().requestFactory(factory).build();
        this.breaker = CircuitBreakerRegistry.of(CircuitBreakerConfig.custom()
                        .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
                        .slidingWindowSize(20)
                        .minimumNumberOfCalls(10)
                        .failureRateThreshold(50)
                        .waitDurationInOpenState(Duration.ofSeconds(30))
                        .permittedNumberOfCallsInHalfOpenState(3)
                        .recordExceptions(Exception.class)
                        .build())
                .circuitBreaker(BREAKER);
    }

    @Override
    public CaptchaProvider provider() {
        return CaptchaProvider.TURNSTILE;
    }

    @Override
    public Optional<String> siteKey() {
        return Optional.of(properties.siteKey());
    }

    @Override
    public CaptchaResult verify(String token, String clientIp) {
        if (token == null || token.isBlank() || token.length() > MAX_TOKEN_LENGTH) {
            return CaptchaResult.INVALID;
        }
        String secret;
        try {
            secret = secrets.get(properties.secretPath()).orElse(null);
        } catch (RuntimeException e) {
            log.warn("CAPTCHA secret could not be read from the secret store ({})", properties.secretPath());
            return CaptchaResult.UNAVAILABLE;
        }
        if (secret == null || secret.isBlank()) {
            log.warn("CAPTCHA secret missing in the secret store ({})", properties.secretPath());
            return CaptchaResult.UNAVAILABLE;
        }
        JsonNode body;
        try {
            body = breaker.executeSupplier(() -> call(secret, token, clientIp));
        } catch (CallNotPermittedException e) {
            log.warn("CAPTCHA circuit breaker open");
            return CaptchaResult.UNAVAILABLE;
        } catch (RuntimeException e) {
            log.warn("CAPTCHA verification failed: {}", e.getClass().getSimpleName());
            return CaptchaResult.UNAVAILABLE;
        }
        if (body == null || !body.path("success").isBoolean()) {
            return CaptchaResult.UNAVAILABLE;
        }
        if (!body.path("success").asBoolean()) {
            return CaptchaResult.INVALID;
        }
        String hostname = body.path("hostname").asText("");
        return properties.allowedHostnames().stream().anyMatch(h -> h.equalsIgnoreCase(hostname))
                ? CaptchaResult.VALID : CaptchaResult.INVALID;
    }

    private JsonNode call(String secret, String token, String clientIp) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("secret", secret);
        form.add("response", token);
        if (clientIp != null && !clientIp.isBlank()) {
            form.add("remoteip", clientIp);
        }
        return http.post()
                .uri(properties.verifyUrl())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .accept(MediaType.APPLICATION_JSON)
                .body(form)
                .retrieve()
                .body(JsonNode.class);
    }

    /** Estado do circuito (testes). */
    CircuitBreaker.State circuitState() {
        return breaker.getState();
    }

    @Override
    public String toString() {
        return "TurnstileCaptchaVerifier[siteKey=" + properties.siteKey() + ", secret=***]";
    }
}
