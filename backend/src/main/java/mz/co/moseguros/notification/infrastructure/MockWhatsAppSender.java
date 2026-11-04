package mz.co.moseguros.notification.infrastructure;

import java.util.LinkedHashMap;
import java.util.Map;

import mz.co.moseguros.shared.domain.Channel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * WhatsApp pelo simulador local ({@code infra/mocks/sms-mock}), com os modelos {@code moseguros_otp}
 * ({@code POST {mock-url}/whatsapp {to, template, params: {code}}} → 202 {@code {id}}) e
 * {@code moseguros_sign_otp} (S4-04: {@code params: {code, ref, insurer, total}}).
 */
@Component
@ConditionalOnProperty(name = "moseguros.notification.whatsapp.provider", havingValue = "mock",
        matchIfMissing = true)
public class MockWhatsAppSender implements OtpDeliveryChannel {

    public static final String PROVIDER = "mock-whatsapp";
    public static final String OTP_TEMPLATE = "moseguros_otp";

    private final RestClient http;
    private final String baseUrl;

    public MockWhatsAppSender(@Qualifier("notificationRestClient") RestClient http,
                              NotificationProperties properties) {
        this.http = http;
        this.baseUrl = properties.whatsapp().mockUrl();
    }

    @Override
    public Channel channel() {
        return Channel.WHATSAPP;
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
        String template = templateParams.getOrDefault(PARAM_TEMPLATE, OTP_TEMPLATE);
        Map<String, String> params = new LinkedHashMap<>();
        params.put(PARAM_CODE, templateParams.get(PARAM_CODE));
        if (SIGN_TEMPLATE.equals(template)) {
            params.put(PARAM_REF, templateParams.get(PARAM_REF));
            params.put(PARAM_INSURER, templateParams.get(PARAM_INSURER));
            params.put(PARAM_TOTAL, templateParams.get(PARAM_TOTAL));
        }
        MockSmsSender.MockResponse response = http.post()
                .uri(baseUrl + "/whatsapp")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("to", destination, "template", template, "params", params))
                .retrieve()
                .body(MockSmsSender.MockResponse.class);
        return ProviderResult.accepted(response == null ? null : response.id());
    }
}
