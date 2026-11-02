package mz.co.moseguros.notification;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import mz.co.moseguros.notification.infrastructure.OtpDeliveryChannel;
import mz.co.moseguros.notification.infrastructure.ProviderResult;
import mz.co.moseguros.shared.domain.Channel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Canais falsos dos testes de integração (perfil {@code test}; {@code application-test.yml} desliga
 * os simuladores e o SMTP). Cada canal pode ter sucesso ou falhar de forma síncrona; a falha por
 * recibo faz-se com um envio aceite seguido de um recibo enviado pelo teste. Usam os mesmos nomes
 * de fornecedor dos simuladores ({@code mock-sms}, {@code mock-whatsapp}, {@code smtp}).
 */
@Configuration(proxyBeanMethods = false)
@Profile("test")
public class FakeOtpDeliveryChannels {

    /** Comportamento de um canal. */
    public enum Mode { OK, FAIL }

    private final Map<Channel, Mode> modes = new EnumMap<>(Channel.class);
    private final List<Sent> sent = new CopyOnWriteArrayList<>();

    @Bean
    OtpDeliveryChannel fakeSmsChannel() {
        return new Fake(Channel.SMS, "mock-sms", true);
    }

    @Bean
    OtpDeliveryChannel fakeWhatsAppChannel() {
        return new Fake(Channel.WHATSAPP, "mock-whatsapp", true);
    }

    @Bean
    OtpDeliveryChannel fakeEmailChannel() {
        return new Fake(Channel.EMAIL, "smtp", false);
    }

    /** Todos os canais com sucesso. */
    public synchronized void reset() {
        modes.clear();
    }

    public synchronized void mode(Channel channel, Mode mode) {
        modes.put(channel, mode);
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

    /**
     * Mensagem aceite por um canal falso.
     *
     * @param providerMessageId o id que um fornecedor real devolveria (volta nos recibos)
     */
    public record Sent(Channel channel, String provider, String destination, String text, Map<String, String> params,
                       String providerMessageId) {

        public String code() {
            return params.get(OtpDeliveryChannel.PARAM_CODE);
        }
    }

    private final class Fake implements OtpDeliveryChannel {

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
        public ProviderResult send(String destination, String text, Map<String, String> templateParams) {
            if (modeOf(channel) == Mode.FAIL) {
                return ProviderResult.failed("HTTP 503");
            }
            String id = "fake-" + UUID.randomUUID();
            sent.add(new Sent(channel, provider, destination, text, Map.copyOf(templateParams), id));
            return ProviderResult.accepted(id);
        }
    }
}
