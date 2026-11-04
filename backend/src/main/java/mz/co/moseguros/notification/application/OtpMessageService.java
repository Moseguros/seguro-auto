package mz.co.moseguros.notification.application;

import java.time.Clock;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import mz.co.moseguros.notification.DeliveryResult;
import mz.co.moseguros.notification.DeliveryStatus;
import mz.co.moseguros.notification.SignOtpText;
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
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;

/**
 * Envio de códigos OTP: grava a mensagem em {@code outbound_message} ({@code PENDING}), entrega-a
 * ao fornecedor do canal e grava o resultado ({@code SENT} ou {@code FAILED}).
 *
 * <p>A chamada ao fornecedor fica fora de transacção quando quem chama não tem uma (a linha é
 * gravada antes e actualizada depois). Nunca grava nem regista o texto, o código ou o destino
 * completo.
 */
@Service
public class OtpMessageService {

    private static final Logger log = LoggerFactory.getLogger(OtpMessageService.class);

    private final Map<Channel, OtpDeliveryChannel> channels = new EnumMap<>(Channel.class);
    private final OutboundMessageRepository messages;
    private final MessageSource texts;
    private final TransactionTemplate tx;
    private final Clock clock;

    public OtpMessageService(List<OtpDeliveryChannel> channels, OutboundMessageRepository messages,
                             @Qualifier("notificationMessageSource") MessageSource texts, TransactionTemplate tx,
                             Clock clock) {
        for (OtpDeliveryChannel channel : channels) {
            OtpDeliveryChannel previous = this.channels.put(channel.channel(), channel);
            if (previous != null) {
                throw new IllegalStateException("Two delivery channels for " + channel.channel() + ": "
                        + previous.provider() + " and " + channel.provider());
            }
        }
        this.messages = messages;
        this.texts = texts;
        this.tx = tx;
        this.clock = clock;
    }

    /** Ver {@code NotificationApi.sendOtp}. */
    @RunAsSystem
    public DeliveryResult sendOtp(Channel channel, String destination, String code, int validityMinutes,
                                  String language, UUID correlationId) {
        return send(channel, destination, code, OutboundMessage.Purpose.OTP, correlationId,
                sender -> deliver(sender, channel, destination, code, validityMinutes, locale(language)));
    }

    /** Ver {@code NotificationApi.sendSignOtp}. */
    @RunAsSystem
    public DeliveryResult sendSignOtp(Channel channel, String destination, String code, int validityMinutes,
                                      SignOtpText text, String language, UUID correlationId) {
        return send(channel, destination, code, OutboundMessage.Purpose.SIGN_OTP, correlationId,
                sender -> deliverSign(sender, channel, destination, code, validityMinutes, text, locale(language)));
    }

    private DeliveryResult send(Channel channel, String destination, String code, OutboundMessage.Purpose purpose,
                                UUID correlationId, java.util.function.Function<OtpDeliveryChannel, ProviderResult> delivery) {
        Objects.requireNonNull(code, "code");
        OtpDeliveryChannel sender = channels.get(channel);
        String provider = sender == null ? "none" : sender.provider();
        String masked = Destinations.mask(destination);
        UUID messageId = tx.execute(status -> messages.save(OutboundMessage.pending(channel, purpose,
                masked, Destinations.hash(destination), correlationId, provider)).getId());

        ProviderResult result = delivery.apply(sender);

        tx.executeWithoutResult(status -> {
            OutboundMessage message = messages.findById(messageId).orElseThrow();
            if (result.accepted()) {
                message.markSent(result.providerMessageId(), clock.instant());
            } else {
                message.markFailed(result.failureReason());
            }
        });
        DeliveryStatus status = result.accepted() ? DeliveryStatus.SENT : DeliveryStatus.FAILED;
        log.info("OTP message {} via {} ({}) to {} for challenge {}: {}{}", messageId, channel, provider, masked,
                correlationId, status, result.accepted() ? "" : " (" + result.failureReason() + ")");
        return new DeliveryResult(messageId, status, result.accepted() ? null : result.failureReason());
    }

    /** Fornecedores que enviam recibos de entrega, pelo nome ({@code mock-sms}…). */
    Optional<OtpDeliveryChannel> receiptProvider(String provider) {
        return channels.values().stream()
                .filter(c -> c.acceptsDeliveryReceipts() && c.provider().equals(provider))
                .findFirst();
    }

    private ProviderResult deliver(OtpDeliveryChannel sender, Channel channel, String destination, String code,
                                   int validityMinutes, Locale locale) {
        String minutes = Integer.toString(validityMinutes);
        return guarded(sender, () -> {
            if (channel == Channel.EMAIL) {
                String subject = texts.getMessage("otp.email.subject", null, locale);
                String body = texts.getMessage("otp.email.body", new Object[] {code, minutes}, locale);
                return sender.send(destination, body,
                        Map.of(OtpDeliveryChannel.PARAM_CODE, code, OtpDeliveryChannel.PARAM_SUBJECT, subject));
            }
            String text = texts.getMessage("otp.sms", new Object[] {code, minutes}, locale);
            return sender.send(destination, text, Map.of(OtpDeliveryChannel.PARAM_CODE, code));
        });
    }

    /**
     * Código de assinatura (S4-04 §4): SMS {@code otp.sign.sms} (ou {@code otp.sign-assisted.sms}),
     * e-mail {@code otp.sign[-assisted].email.*}, WhatsApp com o modelo {@code moseguros_sign_otp}
     * ({@code code}, {@code ref}, {@code insurer}, {@code total}).
     */
    private ProviderResult deliverSign(OtpDeliveryChannel sender, Channel channel, String destination, String code,
                                       int validityMinutes, SignOtpText text, Locale locale) {
        Object[] args = {code, text.documentRef(), text.insurerName(), text.totalFormatted(),
                Integer.toString(validityMinutes), text.assisted() ? text.assistedByName() : ""};
        String prefix = text.assisted() ? "otp.sign-assisted" : "otp.sign";
        return guarded(sender, () -> {
            if (channel == Channel.EMAIL) {
                String subject = texts.getMessage(prefix + ".email.subject", args, locale);
                String body = texts.getMessage(prefix + ".email.body", args, locale);
                return sender.send(destination, body,
                        Map.of(OtpDeliveryChannel.PARAM_CODE, code, OtpDeliveryChannel.PARAM_SUBJECT, subject));
            }
            String sms = texts.getMessage(prefix + ".sms", args, locale);
            Map<String, String> params = new LinkedHashMap<>();
            params.put(OtpDeliveryChannel.PARAM_TEMPLATE, OtpDeliveryChannel.SIGN_TEMPLATE);
            params.put(OtpDeliveryChannel.PARAM_CODE, code);
            params.put(OtpDeliveryChannel.PARAM_REF, text.documentRef());
            params.put(OtpDeliveryChannel.PARAM_INSURER, text.insurerName());
            params.put(OtpDeliveryChannel.PARAM_TOTAL, text.totalFormatted());
            return sender.send(destination, sms, params);
        });
    }

    /** Converte as falhas do fornecedor em {@code FAILED} com um motivo curto (nunca lança). */
    private ProviderResult guarded(OtpDeliveryChannel sender, java.util.function.Supplier<ProviderResult> send) {
        if (sender == null) {
            return ProviderResult.failed("channel not configured");
        }
        try {
            return send.get();
        } catch (RestClientResponseException e) {
            return ProviderResult.failed("HTTP " + e.getStatusCode().value());
        } catch (ResourceAccessException e) {
            return ProviderResult.failed("provider unreachable or timeout");
        } catch (MailException e) {
            return ProviderResult.failed("smtp error: " + e.getClass().getSimpleName());
        } catch (RuntimeException e) {
            log.warn("Unexpected error from provider {} ({})", sender.provider(), e.getClass().getName());
            return ProviderResult.failed("provider error: " + e.getClass().getSimpleName());
        }
    }

    private static Locale locale(String language) {
        return "en".equalsIgnoreCase(language) ? Locale.ENGLISH : NotificationConfig.DEFAULT_LOCALE;
    }
}
