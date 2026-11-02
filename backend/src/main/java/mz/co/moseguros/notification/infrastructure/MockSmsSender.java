package mz.co.moseguros.notification.infrastructure;

import java.util.Map;

import mz.co.moseguros.shared.domain.Channel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * SMS pelo simulador local ({@code infra/mocks/sms-mock}):
 * {@code POST {mock-url}/sms {to, text}} → 202 {@code {id}}.
 */
@Component
@ConditionalOnProperty(name = "moseguros.notification.sms.provider", havingValue = "mock", matchIfMissing = true)
public class MockSmsSender implements OtpDeliveryChannel {

    public static final String PROVIDER = "mock-sms";

    private final RestClient http;
    private final String baseUrl;

    public MockSmsSender(@Qualifier("notificationRestClient") RestClient http, NotificationProperties properties) {
        this.http = http;
        this.baseUrl = properties.sms().mockUrl();
    }

    @Override
    public Channel channel() {
        return Channel.SMS;
    }

    @Override
    public String provider() {
        return PROVIDER;
    }

    @Override
    public boolean acceptsDeliveryReceipts() {
        return true;
    }

    @Override
    public ProviderResult send(String destination, String text, Map<String, String> templateParams) {
        MockResponse response = http.post()
                .uri(baseUrl + "/sms")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("to", destination, "text", text))
                .retrieve()
                .body(MockResponse.class);
        return ProviderResult.accepted(response == null ? null : response.id());
    }

    /** Resposta dos simuladores. */
    record MockResponse(String id) {
    }
}
