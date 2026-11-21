package mz.co.moseguros.notification.infrastructure;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import mz.co.moseguros.shared.domain.Channel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * WhatsApp pelo simulador local ({@code infra/mocks/sms-mock}):
 * {@code POST {mock-url}/whatsapp {to, template, params}} → 202 {@code {id}}. O {@code moseguros_otp} leva
 * {@code {code}} e o {@code moseguros_sign_otp} {@code {code, ref, insurer, total}} (os nomes que o simulador
 * valida); os modelos das notificações (S6-04 §10) levam os parâmetros por ordem ({@code p1}, {@code p2}…).
 */
@Component
@ConditionalOnProperty(name = "moseguros.notification.whatsapp.provider", havingValue = "mock",
        matchIfMissing = true)
public class MockWhatsAppSender implements ChannelSender {

    public static final String PROVIDER = "mock-whatsapp";

    private static final List<String> SIGN_PARAMS = List.of("code", "ref", "insurer", "total");

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
    public ProviderResult send(OutboundContent content) {
        String template = content.whatsappTemplate() == null ? OTP_TEMPLATE : content.whatsappTemplate();
        MockSmsSender.MockResponse response = http.post()
                .uri(baseUrl + "/whatsapp")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("to", content.destination(), "template", template, "params", params(template, content)))
                .retrieve()
                .body(MockSmsSender.MockResponse.class);
        return ProviderResult.accepted(PROVIDER, response == null ? null : response.id());
    }

    /** Parâmetros com nome, como o simulador (e um fornecedor real) os espera. */
    static Map<String, String> params(String template, OutboundContent content) {
        Map<String, String> params = new LinkedHashMap<>();
        if (OTP_TEMPLATE.equals(template)) {
            params.put("code", content.whatsappParam(0));
        } else if (SIGN_TEMPLATE.equals(template)) {
            for (int i = 0; i < SIGN_PARAMS.size(); i++) {
                params.put(SIGN_PARAMS.get(i), content.whatsappParam(i));
            }
        } else {
            for (int i = 0; i < content.whatsappParams().size(); i++) {
                params.put("p" + (i + 1), content.whatsappParams().get(i));
            }
        }
        params.values().removeIf(java.util.Objects::isNull);
        return params;
    }
}
