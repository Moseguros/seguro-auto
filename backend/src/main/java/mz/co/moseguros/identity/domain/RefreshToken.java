package mz.co.moseguros.identity.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import mz.co.moseguros.shared.persistence.BaseEntity;

/**
 * Token de renovação de sessão ({@code identity.refresh_token}); só se grava o SHA-256. Só
 * visível em âmbito {@code SYSTEM}. A rotação e a detecção de reutilização são do S1-02.
 */
@Entity
@Table(schema = "identity", name = "refresh_token")
public class RefreshToken extends BaseEntity {

    static final int DEVICE_INFO_MAX = 200;

    /** Motivo da revogação. */
    public enum RevokedReason {
        ROTATED, LOGOUT, REUSE_DETECTED, PASSWORD_RESET, ACCESS_RESET, DISABLED, ORG_SUSPENDED
    }

    @Column(name = "user_id", nullable = false, updatable = false)
    private UUID userId;

    /** Igual para todos os tokens de uma sessão. */
    @Column(name = "family_id", nullable = false, updatable = false)
    private UUID familyId;

    @Column(name = "token_hash", nullable = false, updatable = false, length = 64)
    private String tokenHash;

    @Column(name = "expires_at", nullable = false, updatable = false)
    private Instant expiresAt;

    /** Limite absoluto da sessão (staff). */
    @Column(name = "session_expires_at", updatable = false)
    private Instant sessionExpiresAt;

    @Column(name = "revoked_at")
    private Instant revokedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "revoked_reason", length = 20)
    private RevokedReason revokedReason;

    @Column(name = "replaced_by_id")
    private UUID replacedById;

    @Column(name = "device_info", updatable = false, length = DEVICE_INFO_MAX)
    private String deviceInfo;

    protected RefreshToken() {
    }

    /** {@code deviceInfo} (o {@code User-Agent}) é truncado a 200 caracteres. */
    public static RefreshToken issue(UUID userId, UUID familyId, String tokenHash, Instant expiresAt,
                                     Instant sessionExpiresAt, String deviceInfo) {
        RefreshToken token = new RefreshToken();
        token.userId = Objects.requireNonNull(userId, "userId");
        token.familyId = Objects.requireNonNull(familyId, "familyId");
        token.tokenHash = Objects.requireNonNull(tokenHash, "tokenHash");
        token.expiresAt = Objects.requireNonNull(expiresAt, "expiresAt");
        token.sessionExpiresAt = sessionExpiresAt;
        token.deviceInfo = deviceInfo == null || deviceInfo.length() <= DEVICE_INFO_MAX
                ? deviceInfo : deviceInfo.substring(0, DEVICE_INFO_MAX);
        return token;
    }

    /** Revoga (só a primeira revogação conta). */
    public void revoke(RevokedReason reason, Instant at, UUID replacedById) {
        if (revokedAt != null) {
            return;
        }
        this.revokedAt = Objects.requireNonNull(at, "at");
        this.revokedReason = Objects.requireNonNull(reason, "reason");
        this.replacedById = replacedById;
    }

    public boolean isRevoked() {
        return revokedAt != null;
    }

    public UUID getUserId() {
        return userId;
    }

    public UUID getFamilyId() {
        return familyId;
    }

    public String getTokenHash() {
        return tokenHash;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public Instant getSessionExpiresAt() {
        return sessionExpiresAt;
    }

    public Instant getRevokedAt() {
        return revokedAt;
    }

    public RevokedReason getRevokedReason() {
        return revokedReason;
    }

    public UUID getReplacedById() {
        return replacedById;
    }

    public String getDeviceInfo() {
        return deviceInfo;
    }
}
