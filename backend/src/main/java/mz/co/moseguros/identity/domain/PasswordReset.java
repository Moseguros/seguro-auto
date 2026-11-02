package mz.co.moseguros.identity.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import mz.co.moseguros.shared.persistence.BaseEntity;

/**
 * Pedido de recuperação de palavra-passe de staff ({@code identity.password_reset}); só se
 * grava o SHA-256 do token. Só visível em âmbito {@code SYSTEM}. O fluxo é do S1-05.
 */
@Entity
@Table(schema = "identity", name = "password_reset")
public class PasswordReset extends BaseEntity {

    @Column(name = "user_id", nullable = false, updatable = false)
    private UUID userId;

    @Column(name = "token_hash", nullable = false, updatable = false, length = 64)
    private String tokenHash;

    @Column(name = "expires_at", nullable = false, updatable = false)
    private Instant expiresAt;

    @Column(name = "used_at")
    private Instant usedAt;

    /** Preenchido quando um pedido mais recente o substitui. */
    @Column(name = "invalidated_at")
    private Instant invalidatedAt;

    protected PasswordReset() {
    }

    public static PasswordReset create(UUID userId, String tokenHash, Instant expiresAt) {
        PasswordReset reset = new PasswordReset();
        reset.userId = Objects.requireNonNull(userId, "userId");
        reset.tokenHash = Objects.requireNonNull(tokenHash, "tokenHash");
        reset.expiresAt = Objects.requireNonNull(expiresAt, "expiresAt");
        return reset;
    }

    public void markUsed(Instant at) {
        this.usedAt = at;
    }

    public void invalidate(Instant at) {
        if (invalidatedAt == null) {
            this.invalidatedAt = at;
        }
    }

    public UUID getUserId() {
        return userId;
    }

    public String getTokenHash() {
        return tokenHash;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public Instant getUsedAt() {
        return usedAt;
    }

    public Instant getInvalidatedAt() {
        return invalidatedAt;
    }
}
