package mz.co.moseguros.notification.application;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Clock;

import mz.co.moseguros.notification.DeliveryStatus;
import mz.co.moseguros.notification.OtpDeliveryFailed;
import mz.co.moseguros.notification.domain.OutboundMessage;
import mz.co.moseguros.notification.domain.OutboundMessageRepository;
import mz.co.moseguros.notification.infrastructure.NotificationProperties;
import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.error.ErrorCodes;
import mz.co.moseguros.shared.tenant.RunAsSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Recibos de entrega dos fornecedores: actualizam {@code outbound_message} e, numa falha de um
 * OTP, publicam {@link OtpDeliveryFailed} (o módulo Identidade tenta então o canal seguinte).
 */
@Service
public class DeliveryReceiptService {

    private static final Logger log = LoggerFactory.getLogger(DeliveryReceiptService.class);

    /** Estado indicado num recibo. */
    public enum ReceiptStatus { DELIVERED, FAILED }

    private final OutboundMessageRepository messages;
    private final OtpMessageService otpMessages;
    private final NotificationProperties properties;
    private final ApplicationEventPublisher events;
    private final Clock clock;

    public DeliveryReceiptService(OutboundMessageRepository messages, OtpMessageService otpMessages,
                                  NotificationProperties properties, ApplicationEventPublisher events, Clock clock) {
        this.messages = messages;
        this.otpMessages = otpMessages;
        this.properties = properties;
        this.events = events;
        this.clock = clock;
    }

    /**
     * Aplica um recibo.
     *
     * @throws DomainException {@code UNAUTHORIZED} (401) se o segredo não for o configurado
     *                         (comparação em tempo constante); {@code NOT_FOUND} (404) se o
     *                         fornecedor não for conhecido
     */
    @RunAsSystem
    @Transactional
    public void receive(String provider, String secret, String providerMessageId, ReceiptStatus status,
                        String reason) {
        if (!secretMatches(secret)) {
            throw new DomainException(ErrorCodes.UNAUTHORIZED, 401, "Invalid webhook secret");
        }
        if (otpMessages.receiptProvider(provider).isEmpty()) {
            throw new DomainException(ErrorCodes.NOT_FOUND, 404, "Unknown delivery receipt provider");
        }
        OutboundMessage message = messages.findFirstByProviderAndProviderMessageId(provider, providerMessageId)
                .orElse(null);
        if (message == null) {
            log.info("Delivery receipt from {} for unknown message ignored", provider);
            return;
        }
        DeliveryStatus previous = message.getStatus();
        if (status == ReceiptStatus.DELIVERED) {
            message.markDelivered(clock.instant());
        } else {
            message.markFailed(reason == null || reason.isBlank() ? "delivery receipt: failed" : "receipt: " + reason);
        }
        log.info("Delivery receipt for message {} ({}, challenge {}): {} -> {}", message.getId(), message.getChannel(),
                message.getCorrelationId(), previous, message.getStatus());
        if (status == ReceiptStatus.FAILED && previous != DeliveryStatus.FAILED && message.isOtp()) {
            events.publishEvent(new OtpDeliveryFailed(message.getId(), message.getCorrelationId(),
                    message.getChannel()));
        }
    }

    private boolean secretMatches(String secret) {
        String expected = properties.webhookSecret();
        if (expected == null || expected.isBlank() || secret == null) {
            return false;
        }
        return MessageDigest.isEqual(expected.getBytes(StandardCharsets.UTF_8), secret.getBytes(StandardCharsets.UTF_8));
    }
}
