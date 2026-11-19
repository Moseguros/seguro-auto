package mz.co.moseguros.notification.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import mz.co.moseguros.notification.DeliveryStatus;
import mz.co.moseguros.shared.domain.Channel;
import mz.co.moseguros.shared.persistence.BaseEntity;

/**
 * Mensagem enviada a um utilizador ({@code notification.outbound_message}). Nunca guarda o texto
 * nem o código: só o destino mascarado e o seu hash. Só visível em âmbito {@code SYSTEM}.
 */
@Entity
@Table(schema = "notification", name = "outbound_message")
public class OutboundMessage extends BaseEntity {

    /** Para que serve a mensagem. */
    public enum Purpose { OTP, INVITATION, PASSWORD_RESET, KYC, SIGN_OTP, PROPOSAL, PAYMENT }

    static final int MAX_FAILURE_REASON = 200;
    static final int MAX_PROVIDER_MESSAGE_ID = 100;

    @Enumerated(EnumType.STRING)
    @Column(name = "channel", nullable = false, updatable = false, length = 10)
    private Channel channel;

    @Enumerated(EnumType.STRING)
    @Column(name = "purpose", nullable = false, updatable = false, length = 30)
    private Purpose purpose;

    @Column(name = "destination_masked", nullable = false, updatable = false, length = 254)
    private String destinationMasked;

    @Column(name = "destination_hash", nullable = false, updatable = false, length = 64)
    private String destinationHash;

    @Column(name = "correlation_id", updatable = false)
    private UUID correlationId;

    @Column(name = "provider", nullable = false, updatable = false, length = 30)
    private String provider;

    @Column(name = "provider_message_id", length = 100)
    private String providerMessageId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 12)
    private DeliveryStatus status;

    @Column(name = "failure_reason", length = 200)
    private String failureReason;

    @Column(name = "sent_at")
    private Instant sentAt;

    @Column(name = "delivered_at")
    private Instant deliveredAt;

    protected OutboundMessage() {
    }

    /** Nova mensagem {@code PENDING}, antes de a entregar ao fornecedor. */
    public static OutboundMessage pending(Channel channel, Purpose purpose, String destinationMasked,
                                          String destinationHash, UUID correlationId, String provider) {
        OutboundMessage message = new OutboundMessage();
        message.channel = Objects.requireNonNull(channel, "channel");
        message.purpose = Objects.requireNonNull(purpose, "purpose");
        message.destinationMasked = Objects.requireNonNull(destinationMasked, "destinationMasked");
        message.destinationHash = Objects.requireNonNull(destinationHash, "destinationHash");
        message.correlationId = correlationId;
        message.provider = Objects.requireNonNull(provider, "provider");
        message.status = DeliveryStatus.PENDING;
        return message;
    }

    /** O fornecedor aceitou a mensagem. */
    public void markSent(String providerMessageId, Instant at) {
        this.providerMessageId = truncate(providerMessageId, MAX_PROVIDER_MESSAGE_ID);
        this.status = DeliveryStatus.SENT;
        this.sentAt = at;
    }

    /** Recibo de entrega. */
    public void markDelivered(Instant at) {
        this.status = DeliveryStatus.DELIVERED;
        this.deliveredAt = at;
    }

    /** O fornecedor recusou, não respondeu ou enviou um recibo de falha. */
    public void markFailed(String reason) {
        this.status = DeliveryStatus.FAILED;
        this.failureReason = truncate(reason == null || reason.isBlank() ? "unknown" : reason, MAX_FAILURE_REASON);
    }

    /** Código OTP (login, contacto, teste) ou código de assinatura (S4-04): um recibo de falha gera alternativa. */
    public boolean isOtp() {
        return purpose == Purpose.OTP || purpose == Purpose.SIGN_OTP;
    }

    public Channel getChannel() {
        return channel;
    }

    public Purpose getPurpose() {
        return purpose;
    }

    public String getDestinationMasked() {
        return destinationMasked;
    }

    public String getDestinationHash() {
        return destinationHash;
    }

    public UUID getCorrelationId() {
        return correlationId;
    }

    public String getProvider() {
        return provider;
    }

    public String getProviderMessageId() {
        return providerMessageId;
    }

    public DeliveryStatus getStatus() {
        return status;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public Instant getSentAt() {
        return sentAt;
    }

    public Instant getDeliveredAt() {
        return deliveredAt;
    }

    private static String truncate(String value, int max) {
        return value == null || value.length() <= max ? value : value.substring(0, max);
    }
}
