package mz.co.moseguros.notification.infrastructure;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

/**
 * Canais de envio ({@code moseguros.notification}).
 *
 * @param webhookSecret segredo exigido no cabeçalho {@code X-Webhook-Secret} dos recibos de
 *                      entrega; vazio = todos os recibos são recusados (401)
 * @param timeout       tempo máximo de cada envio (ligação e resposta)
 * @param sms           fornecedor de SMS
 * @param whatsapp      fornecedor de WhatsApp
 * @param email         envio de e-mail (SMTP)
 */
@ConfigurationProperties(prefix = "moseguros.notification")
public record NotificationProperties(
        String webhookSecret,
        @DefaultValue("PT5S") Duration timeout,
        @DefaultValue Provider sms,
        @DefaultValue Provider whatsapp,
        @DefaultValue Email email) {

    /**
     * @param provider {@code mock} (único nesta fase; os reais entram numa ficha própria)
     * @param mockUrl  URL base do simulador ({@code infra/mocks/sms-mock})
     */
    public record Provider(@DefaultValue("mock") String provider,
                           @DefaultValue("http://localhost:8090") String mockUrl) {
    }

    /**
     * @param provider {@code smtp}
     * @param from     remetente
     */
    public record Email(@DefaultValue("smtp") String provider,
                        @DefaultValue("MoSeguros <nao-responder@moseguros.co.mz>") String from) {
    }
}
