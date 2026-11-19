package mz.co.moseguros.notification.application;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import mz.co.moseguros.notification.NotificationCategory;
import mz.co.moseguros.notification.NotificationRecipient;
import mz.co.moseguros.notification.NotificationRecipientResolver;
import mz.co.moseguros.notification.domain.Destinations;
import mz.co.moseguros.notification.domain.Device;
import mz.co.moseguros.notification.domain.DeviceRepository;
import mz.co.moseguros.notification.domain.MessageChannel;
import mz.co.moseguros.notification.domain.MessageStatus;
import mz.co.moseguros.notification.domain.NotificationMessage;
import mz.co.moseguros.notification.domain.NotificationMessageRepository;
import mz.co.moseguros.notification.domain.NotificationTemplate;
import mz.co.moseguros.notification.domain.NotificationTemplateRepository;
import mz.co.moseguros.notification.infrastructure.ChannelSender;
import mz.co.moseguros.notification.infrastructure.OutboundContent;
import mz.co.moseguros.notification.infrastructure.ProviderErrors;
import mz.co.moseguros.notification.infrastructure.ProviderResult;
import mz.co.moseguros.notification.infrastructure.PushSender;
import mz.co.moseguros.shared.domain.Channel;
import mz.co.moseguros.shared.tenant.RunAsSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Envio de uma linha de {@code notification.message} (S6-04 §4, "Envio de uma linha"), sempre em
 * {@code SYSTEM}:
 * <ol>
 *   <li>transacção 1: {@code select … for update skip locked} da linha {@code QUEUED} na hora → {@code SENDING},
 *       {@code attempts + 1}; lê o destinatário (contactos actuais), renderiza com o modelo actual e grava o
 *       destino mascarado e o hash;</li>
 *   <li>fora de transacção: chama o fornecedor (timeout de 5 s);</li>
 *   <li>transacção 2: {@code SENT}; falha temporária com {@code attempts < 3} → {@code QUEUED} 30 s × 4^(n−1)
 *       depois; falha definitiva ou 3.ª falha → {@code FAILED} e alternativa.</li>
 * </ol>
 * Nunca regista o texto, o destino completo nem o token.
 */
@Service
public class MessageSender {

    public static final int MAX_ATTEMPTS = 3;
    static final Duration FIRST_RETRY = Duration.ofSeconds(30);
    static final Duration RECEIPT_FALLBACK_WINDOW = Duration.ofMinutes(10);

    private static final Logger log = LoggerFactory.getLogger(MessageSender.class);

    private final NotificationMessageRepository messages;
    private final NotificationTemplateRepository templates;
    private final DeviceRepository devices;
    private final NotificationRecipientResolver recipients;
    private final PlanningContexts contexts;
    private final TemplateRenderer renderer;
    private final MessageDispatcher dispatcher;
    private final NotificationMetrics metrics;
    private final Map<Channel, ChannelSender> senders = new EnumMap<>(Channel.class);
    private final ObjectProvider<PushSender> push;
    private final TransactionTemplate tx;
    private final Clock clock;

    public MessageSender(NotificationMessageRepository messages, NotificationTemplateRepository templates,
                         DeviceRepository devices, NotificationRecipientResolver recipients,
                         PlanningContexts contexts, TemplateRenderer renderer, MessageDispatcher dispatcher,
                         NotificationMetrics metrics, List<ChannelSender> channelSenders,
                         ObjectProvider<PushSender> push, PlatformTransactionManager transactionManager, Clock clock) {
        this.messages = messages;
        this.templates = templates;
        this.devices = devices;
        this.recipients = recipients;
        this.contexts = contexts;
        this.renderer = renderer;
        this.dispatcher = dispatcher;
        this.metrics = metrics;
        channelSenders.forEach(s -> senders.put(s.channel(), s));
        this.push = push;
        this.tx = new TransactionTemplate(transactionManager);
        this.tx.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        this.clock = clock;
    }

    /** Envia a linha se estiver {@code QUEUED} e na hora; senão não faz nada. Nunca lança por falha do fornecedor. */
    @RunAsSystem
    public void send(UUID messageId) {
        Attempt attempt = tx.execute(status -> start(messageId));
        if (attempt == null) {
            return;
        }
        ProviderResult result = deliver(attempt);
        tx.executeWithoutResult(status -> finish(messageId, result, attempt.deviceId()));
    }

    /** Transacção 1: bloqueia, passa a {@code SENDING} e prepara o conteúdo; {@code null} se não há envio. */
    private Attempt start(UUID messageId) {
        NotificationMessage message = messages.lockDue(messageId, clock.instant()).orElse(null);
        if (message == null) {
            return null;
        }
        message.startAttempt();
        Optional<NotificationRecipient> recipient = recipients.resolve(message.getUserId());
        if (recipient.isEmpty() || !recipient.get().active()) {
            message.markSkipped("recipient not active");
            metrics.message(message.getChannel(), MessageStatus.SKIPPED);
            log.info("Notification message {} ({}) skipped: recipient not active", message.getId(),
                    message.getChannel());
            return null;
        }
        NotificationTemplate template = templates.findByCodeAndChannelAndLocale(message.getTemplateCode(),
                message.getChannel(), message.getLocale()).filter(NotificationTemplate::isActive).orElse(null);
        if (template == null) {
            failDefinitively(message, null, "template not active");
            return null;
        }
        String destination;
        String token = null;
        if (message.getChannel() == MessageChannel.PUSH) {
            Device device = devices.findById(message.getDeviceId()).filter(Device::isActive).orElse(null);
            if (device == null) {
                failDefinitively(message, null, "device not active");
                return null;
            }
            token = device.getToken();
            destination = null;
            message.destination("device:" + device.getId(), Destinations.hash(token));
        } else {
            destination = destination(message.getChannel(), recipient.get());
            if (destination == null) {
                failDefinitively(message, null, "no verified destination");
                return null;
            }
            message.destination(Destinations.mask(destination), Destinations.hash(destination));
        }
        Map<String, String> values = message.getParams();
        String subject;
        String text;
        try {
            subject = renderer.render(template.getSubject(), values);
            text = renderer.render(template.getBody(), values);
        } catch (TemplateRenderer.TemplateException e) {
            failDefinitively(message, null, "template error: " + e.code());
            return null;
        }
        if (message.getChannel() == MessageChannel.SMS) {
            SmsText.Measured sms = SmsText.fit(text, renderer.asciiFold());
            if (!sms.fits()) {
                metrics.smsTruncated(message.getTemplateCode());
                log.warn("SMS of notification message {} ({}) truncated to {} characters", message.getId(),
                        message.getTemplateCode(), sms.limit());
            }
            text = sms.text();
        }
        List<String> whatsappParams = new ArrayList<>();
        for (String name : template.getWhatsappParams()) {
            whatsappParams.add(values.getOrDefault(name, ""));
        }
        Map<String, String> data = new LinkedHashMap<>();
        data.put("notificationId", message.getNotificationId().toString());
        String deepLink = message.getData().get(NotificationMessage.DATA_DEEP_LINK);
        if (deepLink != null) {
            data.put(NotificationMessage.DATA_DEEP_LINK, deepLink);
        }
        return new Attempt(message.getId(), message.getChannel(), message.getAttempts(), destination, token,
                message.getDeviceId(), subject, text, template.getWhatsappTemplateName(), whatsappParams, data);
    }

    private static String destination(MessageChannel channel, NotificationRecipient recipient) {
        return switch (channel) {
            case SMS, WHATSAPP -> recipient.msisdn() != null && recipient.msisdnVerified()
                    ? recipient.msisdn().e164() : null;
            case EMAIL -> recipient.email() != null && recipient.emailVerified() ? recipient.email() : null;
            default -> null;
        };
    }

    /** Fora de transacção: o fornecedor. */
    private ProviderResult deliver(Attempt a) {
        if (a.channel() == MessageChannel.PUSH) {
            PushSender pushSender = push.getIfAvailable();
            if (pushSender == null) {
                return ProviderResult.temporary("none", "push not configured");
            }
            try {
                return pushSender.send(a.token(), a.subject(), a.text(), a.data());
            } catch (RuntimeException e) {
                return ProviderErrors.classify(pushSender.provider(), e);
            }
        }
        ChannelSender sender = senders.get(a.channel().toDeliveryChannel());
        if (sender == null) {
            return ProviderResult.temporary("none", "channel not configured");
        }
        try {
            return sender.send(new OutboundContent(a.destination(), a.subject(), a.text(), a.whatsappTemplate(),
                    a.whatsappParams()));
        } catch (RuntimeException e) {
            return ProviderErrors.classify(sender.provider(), e);
        }
    }

    /** Transacção 2: grava o resultado e, numa falha definitiva, a alternativa. */
    private void finish(UUID messageId, ProviderResult result, UUID deviceId) {
        NotificationMessage message = messages.findById(messageId).orElseThrow();
        if (message.getStatus() != MessageStatus.SENDING) {
            return; // o job devolveu-a à fila entretanto
        }
        Instant now = clock.instant();
        if (result.success()) {
            message.markSent(result.provider(), result.providerMessageId(), now);
            metrics.message(message.getChannel(), MessageStatus.SENT);
            log.info("Notification message {} ({}, {}) via {} to {}: SENT", message.getId(), message.getTemplateCode(),
                    message.getChannel(), result.provider(), message.getDestinationMasked());
            return;
        }
        if (!result.permanent() && message.getAttempts() < MAX_ATTEMPTS) {
            Instant next = now.plus(FIRST_RETRY.multipliedBy((long) Math.pow(4, message.getAttempts() - 1)));
            message.retryAt(next, result.provider(), result.reason());
            log.info("Notification message {} ({}) attempt {} failed ({}); next at {}", message.getId(),
                    message.getChannel(), message.getAttempts(), result.reason(), next);
            return;
        }
        if (message.getChannel() == MessageChannel.PUSH && result.permanent() && deviceId != null) {
            devices.findById(deviceId).ifPresent(d -> d.deactivate(Device.DeactivationReason.INVALID_TOKEN, now));
        }
        failDefinitively(message, result.provider(), result.reason());
    }

    /** {@code FAILED} e alternativa (passo 6). */
    private void failDefinitively(NotificationMessage message, String provider, String reason) {
        message.markFailed(provider, reason);
        metrics.message(message.getChannel(), MessageStatus.FAILED);
        log.info("Notification message {} ({}, {}) FAILED after {} attempt(s): {}", message.getId(),
                message.getTemplateCode(), message.getChannel(), message.getAttempts(), reason);
        fallback(message);
    }

    /**
     * Alternativa a uma linha que falhou de vez: no push, só se todas as linhas push da notificação falharam e a
     * categoria não é obrigatória; nos outros, o canal seguinte ainda não tentado ({@link ChannelPlanner#next}).
     */
    void fallback(NotificationMessage failed) {
        NotificationCategory category;
        try {
            category = NotificationCategory.valueOf(failed.getCategory());
        } catch (IllegalArgumentException e) {
            return;
        }
        List<NotificationMessage> rows = messages.findByNotificationId(failed.getNotificationId());
        if (failed.getChannel() == MessageChannel.PUSH) {
            if (category.mandatory()) {
                return;
            }
            boolean allPushFailed = rows.stream().filter(r -> r.getChannel() == MessageChannel.PUSH)
                    .allMatch(r -> r.getStatus() == MessageStatus.FAILED);
            if (!allPushFailed) {
                return;
            }
        }
        NotificationRecipient recipient = recipients.resolve(failed.getUserId())
                .filter(NotificationRecipient::active).orElse(null);
        if (recipient == null) {
            return;
        }
        List<MessageChannel> tried = rows.stream().map(NotificationMessage::getChannel)
                .filter(MessageChannel::external).distinct().toList();
        ChannelPlanner.Context context = contexts.build(category, recipient, failed.getTemplateCode(),
                failed.getLocale());
        Optional<MessageChannel> next = contexts.planner().next(context, failed.getChannel(), tried);
        if (next.isEmpty()) {
            log.info("Notification {}: no fallback channel after {}", failed.getNotificationId(), failed.getChannel());
            return;
        }
        NotificationMessage row = messages.save(NotificationMessage.queued(failed.getNotificationId(),
                failed.getUserId(), failed.getTemplateCode(), failed.getCategory(), next.get(), failed.getLocale(),
                failed.getParams(), failed.getData(), null, clock.instant()));
        metrics.fallback(failed.getChannel(), next.get());
        log.info("Notification {}: fallback {} -> {} (message {})", failed.getNotificationId(), failed.getChannel(),
                next.get(), row.getId());
        dispatcher.afterCommit(List.of(row.getId()));
    }

    /**
     * Recibo de entrega de uma linha das notificações (S6-04 §4, "Recibos de entrega"); quem chama já validou o
     * segredo e o fornecedor e corre numa transacção em {@code SYSTEM}.
     *
     * @return {@code false} se nenhuma linha tiver este id do fornecedor
     */
    public boolean applyReceipt(String provider, String providerMessageId, boolean delivered, String reason) {
        NotificationMessage message = messages.findFirstByProviderAndProviderMessageId(provider, providerMessageId)
                .orElse(null);
        if (message == null) {
            return false;
        }
        Instant now = clock.instant();
        MessageStatus previous = message.getStatus();
        if (delivered) {
            if (previous != MessageStatus.FAILED) {
                message.markDelivered(now);
                metrics.message(message.getChannel(), MessageStatus.DELIVERED);
            }
        } else if (previous != MessageStatus.FAILED) {
            message.markFailed(null, reason == null || reason.isBlank() ? "delivery receipt: failed"
                    : "receipt: " + reason);
            metrics.message(message.getChannel(), MessageStatus.FAILED);
            if (message.getSentAt() != null
                    && !message.getSentAt().plus(RECEIPT_FALLBACK_WINDOW).isBefore(now)) {
                fallback(message);
            }
        }
        log.info("Delivery receipt for notification message {} ({}): {} -> {}", message.getId(),
                message.getChannel(), previous, message.getStatus());
        return true;
    }

    /** O que se envia numa tentativa. */
    private record Attempt(UUID messageId, MessageChannel channel, int attempts, String destination, String token,
                           UUID deviceId, String subject, String text, String whatsappTemplate,
                           List<String> whatsappParams, Map<String, String> data) {

        @Override
        public String toString() {
            return "Attempt[" + messageId + ", " + channel + "]";
        }
    }
}
