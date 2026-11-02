package mz.co.moseguros.identity.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import mz.co.moseguros.shared.domain.Channel;
import mz.co.moseguros.shared.persistence.BaseEntity;

/**
 * Desafio OTP enviado a um telemóvel ou e-mail ({@code identity.otp_challenge}). Só visível em
 * âmbito {@code SYSTEM}. As regras de envio e validação são do S1-03.
 */
@Entity
@Table(schema = "identity", name = "otp_challenge")
public class OtpChallenge extends BaseEntity {

    /** Tipo do identificador: telemóvel ({@code 258…}) ou e-mail. */
    public enum IdentifierType { MSISDN, EMAIL }

    /** Para que serve o código. */
    public enum Purpose { LOGIN, VERIFY_CONTACT, SIGN, TEST }

    /** Estado da entrega pelo canal. */
    public enum DeliveryStatus { PENDING, SENT, DELIVERED, FAILED }

    @Column(name = "identifier", nullable = false, updatable = false, length = 254)
    private String identifier;

    @Enumerated(EnumType.STRING)
    @Column(name = "identifier_type", nullable = false, updatable = false, length = 10)
    private IdentifierType identifierType;

    @Enumerated(EnumType.STRING)
    @Column(name = "channel", nullable = false, updatable = false, length = 10)
    private Channel channel;

    @Enumerated(EnumType.STRING)
    @Column(name = "purpose", nullable = false, updatable = false, length = 20)
    private Purpose purpose;

    @Column(name = "user_id", updatable = false)
    private UUID userId;

    /** HMAC-SHA256 do código, em hexadecimal (S1-03). */
    @Column(name = "code_hash", nullable = false, updatable = false, length = 64)
    private String codeHash;

    @Column(name = "expires_at", nullable = false, updatable = false)
    private Instant expiresAt;

    @Column(name = "attempts", nullable = false)
    private int attempts;

    @Column(name = "consumed_at")
    private Instant consumedAt;

    @Column(name = "superseded_at")
    private Instant supersededAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_status", nullable = false, length = 12)
    private DeliveryStatus deliveryStatus;

    @Column(name = "sent_at")
    private Instant sentAt;

    @Column(name = "fallback_from_challenge_id", updatable = false)
    private UUID fallbackFromChallengeId;

    /** Primeiro desafio da cadeia (igual ao próprio id no primeiro). */
    @Column(name = "root_challenge_id", nullable = false, updatable = false)
    private UUID rootChallengeId;

    protected OtpChallenge() {
    }

    /**
     * Novo desafio {@code PENDING}. {@code fallbackFrom} é o desafio que este substitui (alternativa
     * de canal ou reenvio) ou {@code null} no primeiro da cadeia.
     */
    public static OtpChallenge issue(String identifier, IdentifierType identifierType, Channel channel,
                                     Purpose purpose, UUID userId, String codeHash, Instant expiresAt,
                                     OtpChallenge fallbackFrom) {
        OtpChallenge challenge = new OtpChallenge();
        challenge.identifier = Objects.requireNonNull(identifier, "identifier");
        challenge.identifierType = Objects.requireNonNull(identifierType, "identifierType");
        challenge.channel = Objects.requireNonNull(channel, "channel");
        challenge.purpose = Objects.requireNonNull(purpose, "purpose");
        challenge.userId = userId;
        challenge.codeHash = Objects.requireNonNull(codeHash, "codeHash");
        challenge.expiresAt = Objects.requireNonNull(expiresAt, "expiresAt");
        challenge.deliveryStatus = DeliveryStatus.PENDING;
        if (fallbackFrom == null) {
            challenge.rootChallengeId = challenge.getId();
        } else {
            challenge.fallbackFromChallengeId = fallbackFrom.getId();
            challenge.rootChallengeId = fallbackFrom.getRootChallengeId();
        }
        return challenge;
    }

    public void markSent(Instant at) {
        this.deliveryStatus = DeliveryStatus.SENT;
        this.sentAt = at;
    }

    public void markFailed() {
        this.deliveryStatus = DeliveryStatus.FAILED;
    }

    public void supersede(Instant at) {
        if (supersededAt == null) {
            this.supersededAt = at;
        }
    }

    public void registerFailedAttempt() {
        this.attempts++;
    }

    public void consume(Instant at) {
        this.consumedAt = at;
    }

    public String getIdentifier() {
        return identifier;
    }

    public IdentifierType getIdentifierType() {
        return identifierType;
    }

    public Channel getChannel() {
        return channel;
    }

    public Purpose getPurpose() {
        return purpose;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getCodeHash() {
        return codeHash;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public int getAttempts() {
        return attempts;
    }

    public Instant getConsumedAt() {
        return consumedAt;
    }

    public Instant getSupersededAt() {
        return supersededAt;
    }

    public DeliveryStatus getDeliveryStatus() {
        return deliveryStatus;
    }

    public Instant getSentAt() {
        return sentAt;
    }

    public UUID getFallbackFromChallengeId() {
        return fallbackFromChallengeId;
    }

    public UUID getRootChallengeId() {
        return rootChallengeId;
    }
}
