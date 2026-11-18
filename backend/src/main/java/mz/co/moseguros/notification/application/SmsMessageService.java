package mz.co.moseguros.notification.application;

import java.time.Clock;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import mz.co.moseguros.notification.DeliveryResult;
import mz.co.moseguros.notification.DeliveryStatus;
import mz.co.moseguros.notification.domain.Destinations;
import mz.co.moseguros.notification.domain.OutboundMessage;
import mz.co.moseguros.notification.domain.OutboundMessageRepository;
import mz.co.moseguros.notification.infrastructure.NotificationConfig;
import mz.co.moseguros.notification.infrastructure.OtpDeliveryChannel;
import mz.co.moseguros.notification.infrastructure.ProviderResult;
import mz.co.moseguros.shared.domain.Channel;
import mz.co.moseguros.shared.tenant.RunAsSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * SMS a partir de modelos ({@code messages/notifications_*.properties}: a chave do modelo, com os
 * argumentos formatados por {@code MessageFormat}), pelo canal SMS do OTP, sem alternativa (S5-02 §10).
 * Grava a mensagem em {@code outbound_message} ({@code PENDING}), entrega-a e grava o resultado.
 *
 * <p>Como no OTP e no e-mail, nunca grava nem regista o texto nem o destino completo, e nunca lança
 * por falha do fornecedor.
 */
@Service
public class SmsMessageService {

    private static final Logger log = LoggerFactory.getLogger(SmsMessageService.class);

    private final OtpDeliveryChannel sms;
    private final OutboundMessageRepository messages;
    private final MessageSource texts;
    private final TransactionTemplate tx;
    private final Clock clock;

    public SmsMessageService(List<OtpDeliveryChannel> channels, OutboundMessageRepository messages,
                             @Qualifier("notificationMessageSource") MessageSource texts, TransactionTemplate tx,
                             Clock clock) {
        this.sms = channels.stream().filter(c -> c.channel() == Channel.SMS).findFirst().orElse(null);
        this.messages = messages;
        this.texts = texts;
        this.tx = tx;
        this.clock = clock;
    }

    /** Ver {@code NotificationApi.sendSms}. */
    @RunAsSystem
    public DeliveryResult sendSms(String toE164, String templateKey, Object[] args, String language, String purpose,
                                  UUID correlationId) {
        OutboundMessage.Purpose messagePurpose = OutboundMessage.Purpose.valueOf(purpose);
        String provider = sms == null ? "none" : sms.provider();
        String masked = Destinations.mask(toE164);
        UUID messageId = tx.execute(status -> messages.save(OutboundMessage.pending(Channel.SMS, messagePurpose,
                masked, Destinations.hash(toE164), correlationId, provider)).getId());

        ProviderResult result = deliver(toE164, templateKey, args, locale(language));

        tx.executeWithoutResult(status -> {
            OutboundMessage message = messages.findById(messageId).orElseThrow();
            if (result.accepted()) {
                message.markSent(result.providerMessageId(), clock.instant());
            } else {
                message.markFailed(result.failureReason());
            }
        });
        DeliveryStatus status = result.accepted() ? DeliveryStatus.SENT : DeliveryStatus.FAILED;
        log.info("SMS {} ({}, {}) via {} to {} for {}: {}{}", messageId, messagePurpose, templateKey, provider, masked,
                correlationId, status, result.accepted() ? "" : " (" + result.failureReason() + ")");
        return new DeliveryResult(messageId, status, result.accepted() ? null : result.failureReason());
    }

    private ProviderResult deliver(String to, String templateKey, Object[] args, Locale locale) {
        if (sms == null) {
            return ProviderResult.failed("channel not configured");
        }
        try {
            String text = texts.getMessage(templateKey, args, locale);
            return sms.send(to, text, Map.of());
        } catch (RuntimeException e) {
            log.warn("Unexpected error from provider {} ({})", sms.provider(), e.getClass().getName());
            return ProviderResult.failed("provider error: " + e.getClass().getSimpleName());
        }
    }

    private static Locale locale(String language) {
        return "en".equalsIgnoreCase(language) ? Locale.ENGLISH : NotificationConfig.DEFAULT_LOCALE;
    }
}
