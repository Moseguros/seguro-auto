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
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * E-mails a partir de modelos ({@code messages/notifications_*.properties}: {@code <modelo>.subject} e
 * {@code <modelo>.body}), pelo mesmo canal de e-mail do OTP. Grava a mensagem em
 * {@code outbound_message} ({@code PENDING}), entrega-a e grava o resultado.
 *
 * <p>Como no OTP, nunca grava nem regista o texto (pode ter links com tokens) nem o destino
 * completo. Os argumentos podem ser {@code MessageSourceResolvable}: são traduzidos na língua
 * do e-mail.
 */
@Service
public class EmailMessageService {

    private static final Logger log = LoggerFactory.getLogger(EmailMessageService.class);

    private final OtpDeliveryChannel email;
    private final OutboundMessageRepository messages;
    private final MessageSource texts;
    private final TransactionTemplate tx;
    private final Clock clock;

    public EmailMessageService(List<OtpDeliveryChannel> channels, OutboundMessageRepository messages,
                               @Qualifier("notificationMessageSource") MessageSource texts, TransactionTemplate tx,
                               Clock clock) {
        this.email = channels.stream().filter(c -> c.channel() == Channel.EMAIL).findFirst().orElse(null);
        this.messages = messages;
        this.texts = texts;
        this.tx = tx;
        this.clock = clock;
    }

    /** Ver {@code NotificationApi.sendEmail}. */
    @RunAsSystem
    public DeliveryResult sendEmail(String to, String templateKey, Object[] args, String language, String purpose,
                                    UUID correlationId) {
        OutboundMessage.Purpose messagePurpose = OutboundMessage.Purpose.valueOf(purpose);
        String provider = email == null ? "none" : email.provider();
        String masked = Destinations.mask(to);
        UUID messageId = tx.execute(status -> messages.save(OutboundMessage.pending(Channel.EMAIL, messagePurpose,
                masked, Destinations.hash(to), correlationId, provider)).getId());

        ProviderResult result = deliver(to, templateKey, args, locale(language));

        tx.executeWithoutResult(status -> {
            OutboundMessage message = messages.findById(messageId).orElseThrow();
            if (result.accepted()) {
                message.markSent(result.providerMessageId(), clock.instant());
            } else {
                message.markFailed(result.failureReason());
            }
        });
        DeliveryStatus status = result.accepted() ? DeliveryStatus.SENT : DeliveryStatus.FAILED;
        log.info("E-mail {} ({}, {}) via {} to {} for {}: {}{}", messageId, messagePurpose, templateKey, provider,
                masked, correlationId, status, result.accepted() ? "" : " (" + result.failureReason() + ")");
        return new DeliveryResult(messageId, status, result.accepted() ? null : result.failureReason());
    }

    private ProviderResult deliver(String to, String templateKey, Object[] args, Locale locale) {
        if (email == null) {
            return ProviderResult.failed("channel not configured");
        }
        try {
            String subject = texts.getMessage(templateKey + ".subject", args, locale);
            String body = texts.getMessage(templateKey + ".body", args, locale);
            return email.send(to, body, Map.of(OtpDeliveryChannel.PARAM_SUBJECT, subject));
        } catch (MailException e) {
            return ProviderResult.failed("smtp error: " + e.getClass().getSimpleName());
        } catch (RuntimeException e) {
            log.warn("Unexpected error from provider {} ({})", email.provider(), e.getClass().getName());
            return ProviderResult.failed("provider error: " + e.getClass().getSimpleName());
        }
    }

    private static Locale locale(String language) {
        return "en".equalsIgnoreCase(language) ? Locale.ENGLISH : NotificationConfig.DEFAULT_LOCALE;
    }
}
