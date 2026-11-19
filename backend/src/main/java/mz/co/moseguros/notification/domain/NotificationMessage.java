package mz.co.moseguros.notification.domain;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import mz.co.moseguros.shared.persistence.BaseEntity;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * Uma linha por canal e destino de uma notificação, mais a linha {@code INBOX} do centro de notificações
 * ({@code notification.message}, S6-04 §2). O texto enviado por SMS, WhatsApp, e-mail ou push não se grava:
 * gera-se de novo a partir de {@link #getParams()} e do modelo actual.
 */
@Entity
@Table(schema = "notification", name = "message")
public class NotificationMessage extends BaseEntity {

    public static final int MAX_ERROR = 200;
    public static final String DATA_DEEP_LINK = "deepLink";
    public static final String DATA_WEB_PATH = "webPath";

    @Column(name = "notification_id", nullable = false, updatable = false)
    private UUID notificationId;

    @Column(name = "user_id", nullable = false, updatable = false)
    private UUID userId;

    @Column(name = "template_code", nullable = false, updatable = false, length = 60)
    private String templateCode;

    @Column(name = "category", nullable = false, updatable = false, length = 20)
    private String category;

    @Enumerated(EnumType.STRING)
    @Column(name = "channel", nullable = false, updatable = false, length = 10)
    private MessageChannel channel;

    @Column(name = "locale", nullable = false, updatable = false, length = 5)
    private String locale;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "params", nullable = false, updatable = false, columnDefinition = "jsonb")
    private Map<String, String> params;

    @Column(name = "title", updatable = false, length = 200)
    private String title;

    @Column(name = "body", updatable = false)
    private String body;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "data", updatable = false, columnDefinition = "jsonb")
    private Map<String, String> data;

    @Column(name = "device_id", updatable = false)
    private UUID deviceId;

    @Column(name = "destination_masked", length = 254)
    private String destinationMasked;

    @Column(name = "destination_hash", length = 64)
    private String destinationHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 10)
    private MessageStatus status;

    @Column(name = "attempts", nullable = false)
    private int attempts;

    @Column(name = "next_attempt_at")
    private Instant nextAttemptAt;

    @Column(name = "last_error", length = 200)
    private String lastError;

    @Column(name = "provider", length = 30)
    private String provider;

    @Column(name = "provider_message_id", length = 200)
    private String providerMessageId;

    @Column(name = "sent_at")
    private Instant sentAt;

    @Column(name = "delivered_at")
    private Instant deliveredAt;

    @Column(name = "read_at")
    private Instant readAt;

    protected NotificationMessage() {
    }

    /** Linha do centro de notificações, já entregue. */
    public static NotificationMessage inbox(UUID notificationId, UUID userId, String templateCode, String category,
                                            String locale, Map<String, String> params, String title, String body,
                                            Map<String, String> data, Instant now) {
        NotificationMessage m = base(notificationId, userId, templateCode, category, MessageChannel.INBOX, locale,
                params, data);
        m.title = Objects.requireNonNull(title, "title");
        m.body = Objects.requireNonNull(body, "body");
        m.status = MessageStatus.DELIVERED;
        m.deliveredAt = now;
        return m;
    }

    /** Linha de um canal externo, à espera de envio ({@code next_attempt_at = now}). */
    public static NotificationMessage queued(UUID notificationId, UUID userId, String templateCode, String category,
                                             MessageChannel channel, String locale, Map<String, String> params,
                                             Map<String, String> data, UUID deviceId, Instant now) {
        if (!channel.external()) {
            throw new IllegalArgumentException("Not an external channel: " + channel);
        }
        if (channel == MessageChannel.PUSH && deviceId == null) {
            throw new IllegalArgumentException("A push message needs a device");
        }
        NotificationMessage m = base(notificationId, userId, templateCode, category, channel, locale, params, data);
        m.deviceId = deviceId;
        m.status = MessageStatus.QUEUED;
        m.nextAttemptAt = now;
        return m;
    }

    private static NotificationMessage base(UUID notificationId, UUID userId, String templateCode, String category,
                                            MessageChannel channel, String locale, Map<String, String> params,
                                            Map<String, String> data) {
        NotificationMessage m = new NotificationMessage();
        m.notificationId = Objects.requireNonNull(notificationId, "notificationId");
        m.userId = Objects.requireNonNull(userId, "userId");
        m.templateCode = Objects.requireNonNull(templateCode, "templateCode");
        m.category = Objects.requireNonNull(category, "category");
        m.channel = channel;
        m.locale = Objects.requireNonNull(locale, "locale");
        m.params = Map.copyOf(params);
        m.data = data == null || data.isEmpty() ? null : Map.copyOf(data);
        return m;
    }

    /** Começa uma tentativa ({@code QUEUED → SENDING}). */
    public void startAttempt() {
        if (status != MessageStatus.QUEUED) {
            throw new IllegalStateException("Message " + getId() + " is " + status);
        }
        status = MessageStatus.SENDING;
        attempts++;
        nextAttemptAt = null;
    }

    /** Destino desta tentativa (mascarado e com hash). */
    public void destination(String masked, String hash) {
        this.destinationMasked = masked;
        this.destinationHash = hash;
    }

    public void markSent(String provider, String providerMessageId, Instant at) {
        this.status = MessageStatus.SENT;
        this.provider = provider;
        this.providerMessageId = truncate(providerMessageId, 200);
        this.sentAt = at;
        this.lastError = null;
    }

    /** Falha temporária: volta à fila para {@code nextAttemptAt}. */
    public void retryAt(Instant nextAttemptAt, String provider, String error) {
        this.status = MessageStatus.QUEUED;
        this.nextAttemptAt = nextAttemptAt;
        this.provider = provider;
        this.lastError = truncate(error, MAX_ERROR);
    }

    public void markFailed(String provider, String error) {
        this.status = MessageStatus.FAILED;
        if (provider != null) {
            this.provider = provider;
        }
        this.lastError = truncate(error == null || error.isBlank() ? "unknown" : error, MAX_ERROR);
        this.nextAttemptAt = null;
    }

    /** Linha que já não se envia (ex.: o destino deixou de existir). */
    public void markSkipped(String reason) {
        this.status = MessageStatus.SKIPPED;
        this.lastError = truncate(reason, MAX_ERROR);
        this.nextAttemptAt = null;
    }

    public void markDelivered(Instant at) {
        this.status = MessageStatus.DELIVERED;
        this.deliveredAt = at;
    }

    /** Linha presa em {@code SENDING} (a aplicação caiu a meio): volta à fila; a tentativa conta. */
    public void requeueStuck(Instant now) {
        if (status == MessageStatus.SENDING) {
            status = MessageStatus.QUEUED;
            nextAttemptAt = now;
        }
    }

    /** Marca como lida (só {@code INBOX}); idempotente. */
    public boolean markRead(Instant at) {
        if (channel != MessageChannel.INBOX) {
            throw new IllegalStateException("Only inbox messages are read");
        }
        if (readAt != null) {
            return false;
        }
        readAt = at;
        return true;
    }

    private static String truncate(String value, int max) {
        return value == null || value.length() <= max ? value : value.substring(0, max);
    }

    public UUID getNotificationId() {
        return notificationId;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getTemplateCode() {
        return templateCode;
    }

    public String getCategory() {
        return category;
    }

    public MessageChannel getChannel() {
        return channel;
    }

    public String getLocale() {
        return locale;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public Map<String, String> getData() {
        return data == null ? Map.of() : data;
    }

    public UUID getDeviceId() {
        return deviceId;
    }

    public String getDestinationMasked() {
        return destinationMasked;
    }

    public String getDestinationHash() {
        return destinationHash;
    }

    public MessageStatus getStatus() {
        return status;
    }

    public int getAttempts() {
        return attempts;
    }

    public Instant getNextAttemptAt() {
        return nextAttemptAt;
    }

    public String getLastError() {
        return lastError;
    }

    public String getProvider() {
        return provider;
    }

    public String getProviderMessageId() {
        return providerMessageId;
    }

    public Instant getSentAt() {
        return sentAt;
    }

    public Instant getDeliveredAt() {
        return deliveredAt;
    }

    public Instant getReadAt() {
        return readAt;
    }
}
