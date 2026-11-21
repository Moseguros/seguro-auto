package mz.co.moseguros.notification;

import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import mz.co.moseguros.notification.infrastructure.ChannelSender;
import mz.co.moseguros.notification.infrastructure.OutboundContent;
import mz.co.moseguros.notification.infrastructure.ProviderResult;
import mz.co.moseguros.notification.infrastructure.PushSender;
import mz.co.moseguros.shared.domain.Channel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Canais falsos dos testes de integração (perfil {@code test}; {@code application-test.yml} desliga
 * os simuladores, o SMTP e o push). Cada canal pode ter sucesso ou falhar de forma síncrona (temporária ou
 * definitiva); a falha por recibo faz-se com um envio aceite seguido de um recibo enviado pelo teste. Usam os
 * mesmos nomes de fornecedor dos simuladores ({@code mock-sms}, {@code mock-whatsapp}, {@code smtp},
 * {@code mock-push}). Desde o S6-04 implementam {@link ChannelSender} e {@link PushSender}.
 */
@Configuration(proxyBeanMethods = false)
@Profile("test")
public class FakeOtpDeliveryChannels {

    /** Comportamento de um canal. */
    public enum Mode {
        OK,
        /** Falha temporária ("HTTP 503"). */
        FAIL,
        /** Falha definitiva (destino ou token inválido). */
        FAIL_PERMANENT
    }

    /** Canal do push nos {@link Sent} (não existe em {@code shared.domain.Channel}). */
    public static final String PUSH = "PUSH";

    private final Map<Channel, Mode> modes = new EnumMap<>(Channel.class);
    private volatile Mode pushMode = Mode.OK;
    private final List<Sent> sent = new CopyOnWriteArrayList<>();
    private final List<Push> pushes = new CopyOnWriteArrayList<>();

    @Bean
    ChannelSender fakeSmsChannel() {
        return new Fake(Channel.SMS, "mock-sms", true);
    }

    @Bean
    ChannelSender fakeWhatsAppChannel() {
        return new Fake(Channel.WHATSAPP, "mock-whatsapp", true);
    }

    @Bean
    ChannelSender fakeEmailChannel() {
        return new Fake(Channel.EMAIL, "smtp", false);
    }

    @Bean
    PushSender fakePushSender() {
        return new FakePush();
    }

    /** Todos os canais com sucesso. */
    public synchronized void reset() {
        modes.clear();
        pushMode = Mode.OK;
    }

    public synchronized void mode(Channel channel, Mode mode) {
        modes.put(channel, mode);
    }

    public void pushMode(Mode mode) {
        this.pushMode = mode;
    }

    synchronized Mode modeOf(Channel channel) {
        return modes.getOrDefault(channel, Mode.OK);
    }

    /** Mensagens aceites, pela ordem de envio. */
    public List<Sent> sent() {
        return List.copyOf(sent);
    }

    /** Mensagens aceites para o destino indicado (E.164 ou e-mail). */
    public List<Sent> sentTo(String destination) {
        return sent.stream().filter(s -> s.destination().equals(destination)).toList();
    }

    /** Última mensagem aceite para o destino. */
    public Sent lastTo(String destination) {
        List<Sent> messages = sentTo(destination);
        if (messages.isEmpty()) {
            throw new AssertionError("No message was sent to " + destination);
        }
        return messages.get(messages.size() - 1);
    }

    /** Último código (OTP ou assinatura) enviado para o destino, ignorando as notificações. */
    public Sent lastCodeTo(String destination) {
        List<Sent> messages = sentTo(destination).stream().filter(s -> s.code() != null).toList();
        if (messages.isEmpty()) {
            throw new AssertionError("No code was sent to " + destination);
        }
        return messages.get(messages.size() - 1);
    }

    /** Push aceites, pela ordem de envio. */
    public List<Push> pushes() {
        return List.copyOf(pushes);
    }

    /**
     * Mensagem aceite por um canal falso.
     *
     * @param params            {@code subject}, {@code template} e os parâmetros WhatsApp com nome ({@code code},
     *                          {@code ref}, {@code insurer}, {@code total} nos códigos; {@code p1}… nas
     *                          notificações)
     * @param providerMessageId o id que um fornecedor real devolveria (volta nos recibos)
     */
    public record Sent(Channel channel, String provider, String destination, String text, Map<String, String> params,
                       String providerMessageId) {

        /** Código OTP ou de assinatura; {@code null} nas notificações. */
        public String code() {
            return params.get("code");
        }

        public String subject() {
            return params.get("subject");
        }
    }

    /** Push aceite (o token completo, só nos testes). */
    public record Push(String token, String title, String body, Map<String, String> data, String providerMessageId) {
    }

    /** Parâmetros com nome, como o {@code MockWhatsAppSender} os envia. */
    public static Map<String, String> params(OutboundContent content) {
        Map<String, String> params = new LinkedHashMap<>();
        if (content.subject() != null) {
            params.put("subject", content.subject());
        }
        String template = content.whatsappTemplate();
        if (template != null) {
            params.put("template", template);
            if (ChannelSender.OTP_TEMPLATE.equals(template)) {
                putIfPresent(params, "code", content.whatsappParam(0));
            } else if (ChannelSender.SIGN_TEMPLATE.equals(template)) {
                putIfPresent(params, "code", content.whatsappParam(0));
                putIfPresent(params, "ref", content.whatsappParam(1));
                putIfPresent(params, "insurer", content.whatsappParam(2));
                putIfPresent(params, "total", content.whatsappParam(3));
            } else {
                for (int i = 0; i < content.whatsappParams().size(); i++) {
                    putIfPresent(params, "p" + (i + 1), content.whatsappParams().get(i));
                }
            }
        }
        return params;
    }

    private static void putIfPresent(Map<String, String> params, String key, String value) {
        if (value != null) {
            params.put(key, value);
        }
    }

    private final class Fake implements ChannelSender {

        private final Channel channel;
        private final String provider;
        private final boolean receipts;

        Fake(Channel channel, String provider, boolean receipts) {
            this.channel = channel;
            this.provider = provider;
            this.receipts = receipts;
        }

        @Override
        public Channel channel() {
            return channel;
        }

        @Override
        public String provider() {
            return provider;
        }

        @Override
        public boolean acceptsDeliveryReceipts() {
            return receipts;
        }

        @Override
        public ProviderResult send(OutboundContent content) {
            Mode mode = modeOf(channel);
            if (mode == Mode.FAIL) {
                return ProviderResult.temporary(provider, "HTTP 503");
            }
            if (mode == Mode.FAIL_PERMANENT) {
                return ProviderResult.permanent(provider, "HTTP 400");
            }
            String id = "fake-" + UUID.randomUUID();
            sent.add(new Sent(channel, provider, content.destination(), content.text(), Map.copyOf(params(content)),
                    id));
            return ProviderResult.accepted(provider, id);
        }
    }

    private final class FakePush implements PushSender {

        @Override
        public String provider() {
            return "mock-push";
        }

        @Override
        public ProviderResult send(String deviceToken, String title, String body, Map<String, String> data) {
            if (pushMode == Mode.FAIL) {
                return ProviderResult.temporary("mock-push", "UNAVAILABLE");
            }
            if (pushMode == Mode.FAIL_PERMANENT || deviceToken.startsWith("invalid-")) {
                return ProviderResult.permanent("mock-push", "UNREGISTERED");
            }
            String id = "fake-push-" + UUID.randomUUID();
            pushes.add(new Push(deviceToken, title, body, Map.copyOf(data), id));
            return ProviderResult.accepted("mock-push", id);
        }
    }
}
