package mz.co.moseguros.notification.infrastructure;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * Push simulado (S6-04 §6). Com {@code moseguros.notification.push.mock-url} definido,
 * {@code POST {mock-url}/push {token: "…" + últimos 8 caracteres, title, body, data}} → 202 {@code {id}}
 * (o {@code sms-mock}); sem ele, só regista {@code INFO} (sem o token). Um token que começa por
 * {@code invalid-} devolve {@code PERMANENT} (para testes manuais).
 */
@Component
@ConditionalOnProperty(name = "moseguros.notification.push.provider", havingValue = "mock", matchIfMissing = true)
public class MockPushSender implements PushSender {

    public static final String PROVIDER = "mock-push";
    static final String INVALID_PREFIX = "invalid-";

    private static final Logger log = LoggerFactory.getLogger(MockPushSender.class);

    private final RestClient http;
    private final String mockUrl;

    public MockPushSender(@Qualifier("notificationRestClient") RestClient http, NotificationProperties properties) {
        this.http = http;
        this.mockUrl = properties.push().mockUrl();
    }

    @Override
    public String provider() {
        return PROVIDER;
    }

    @Override
    public ProviderResult send(String deviceToken, String title, String body, Map<String, String> data) {
        if (deviceToken.startsWith(INVALID_PREFIX)) {
            return ProviderResult.permanent(PROVIDER, "UNREGISTERED");
        }
        if (mockUrl == null || mockUrl.isBlank()) {
            String id = "mock-push-" + UUID.randomUUID();
            log.info("Mock push {} for notification {} (no mock-url)", id, data.get("notificationId"));
            return ProviderResult.accepted(PROVIDER, id);
        }
        Map<String, Object> request = new LinkedHashMap<>();
        request.put("token", tokenTail(deviceToken));
        request.put("title", title);
        request.put("body", body);
        request.put("data", data);
        MockSmsSender.MockResponse response = http.post()
                .uri(mockUrl + "/push")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(MockSmsSender.MockResponse.class);
        return ProviderResult.accepted(PROVIDER, response == null ? null : response.id());
    }

    /** {@code …} e os últimos 8 caracteres do token. */
    static String tokenTail(String token) {
        return "…" + (token.length() <= 8 ? token : token.substring(token.length() - 8));
    }
}
