package mz.co.moseguros.notification.infrastructure;

import java.time.Duration;
import java.util.List;

import mz.co.moseguros.notification.domain.MessageChannel;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

/**
 * Canais de envio ({@code moseguros.notification}).
 *
 * @param webhookSecret segredo exigido no cabeçalho {@code X-Webhook-Secret} dos recibos de
 *                      entrega; vazio = todos os recibos são recusados (401)
 * @param timeout       tempo máximo de cada envio (ligação e resposta)
 * @param sms           fornecedor de SMS e normalização do texto (S6-04 §5)
 * @param whatsapp      fornecedor de WhatsApp
 * @param email         envio de e-mail (SMTP)
 * @param channelOrder  ordem dos canais das notificações (S6-04 §4): {@code PUSH, WHATSAPP, SMS, EMAIL}
 * @param push          fornecedor de push (S6-04 §6)
 */
@ConfigurationProperties(prefix = "moseguros.notification")
public record NotificationProperties(
        String webhookSecret,
        @DefaultValue("PT5S") Duration timeout,
        @DefaultValue Sms sms,
        @DefaultValue Provider whatsapp,
        @DefaultValue Email email,
        @DefaultValue({"PUSH", "WHATSAPP", "SMS", "EMAIL"}) List<MessageChannel> channelOrder,
        @DefaultValue Push push) {

    /** Ordem por defeito dos canais. */
    public static final List<MessageChannel> DEFAULT_ORDER =
            List.of(MessageChannel.PUSH, MessageChannel.WHATSAPP, MessageChannel.SMS, MessageChannel.EMAIL);

    public NotificationProperties {
        if (timeout == null) {
            timeout = Duration.ofSeconds(5);
        }
        if (sms == null) {
            sms = new Sms("mock", "http://localhost:8090", true);
        }
        if (whatsapp == null) {
            whatsapp = new Provider("mock", "http://localhost:8090");
        }
        if (email == null) {
            email = new Email("smtp", "MoSeguros <nao-responder@moseguros.co.mz>");
        }
        channelOrder = channelOrder == null || channelOrder.isEmpty() ? DEFAULT_ORDER : List.copyOf(channelOrder);
        if (channelOrder.contains(MessageChannel.INBOX) || channelOrder.stream().distinct().count()
                != channelOrder.size()) {
            throw new IllegalArgumentException("moseguros.notification.channel-order: external channels, no repeats");
        }
        if (push == null) {
            push = new Push("mock", null);
        }
    }

    /**
     * @param provider {@code mock} (único nesta fase; os reais entram numa ficha própria)
     * @param mockUrl  URL base do simulador ({@code infra/mocks/sms-mock})
     */
    public record Provider(@DefaultValue("mock") String provider,
                           @DefaultValue("http://localhost:8090") String mockUrl) {
    }

    /**
     * @param provider   {@code mock}
     * @param mockUrl    URL base do simulador
     * @param asciiFold  tira os acentos dos SMS das notificações ({@code SmsText}, S6-04 §5)
     */
    public record Sms(@DefaultValue("mock") String provider,
                      @DefaultValue("http://localhost:8090") String mockUrl,
                      @DefaultValue("true") boolean asciiFold) {
    }

    /**
     * @param provider {@code smtp}
     * @param from     remetente
     */
    public record Email(@DefaultValue("smtp") String provider,
                        @DefaultValue("MoSeguros <nao-responder@moseguros.co.mz>") String from) {
    }

    /**
     * @param provider {@code mock} ({@code MockPushSender}) ou {@code fcm} ({@code FcmPushSender}); {@code mock}
     *                 não arranca com o perfil {@code prod}
     * @param mockUrl  URL do {@code sms-mock} ({@code POST /push}); vazio = só log
     */
    public record Push(@DefaultValue("mock") String provider, String mockUrl) {
    }
}
