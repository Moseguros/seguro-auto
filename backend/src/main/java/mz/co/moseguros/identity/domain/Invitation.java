package mz.co.moseguros.identity.domain;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import mz.co.moseguros.shared.domain.Emails;
import mz.co.moseguros.shared.persistence.BaseEntity;

/**
 * Convite de staff ({@code identity.invitation}); só se grava o SHA-256 do token. O fluxo de
 * aceitação é do S1-04.
 */
@Entity
@Table(schema = "identity", name = "invitation")
public class Invitation extends BaseEntity {

    @Column(name = "email", nullable = false, updatable = false, length = 254)
    private String email;

    @Column(name = "full_name", nullable = false, length = 120)
    private String fullName;

    /** {@code null} = staff da plataforma. */
    @Column(name = "organization_id", updatable = false)
    private UUID organizationId;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "role_codes", nullable = false, columnDefinition = "text[]")
    private List<String> roleCodes;

    @Column(name = "token_hash", nullable = false, length = 64)
    private String tokenHash;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "accepted_at")
    private Instant acceptedAt;

    @Column(name = "revoked_at")
    private Instant revokedAt;

    /** {@code null} quando criado pelo sistema. */
    @Column(name = "invited_by_user_id", updatable = false)
    private UUID invitedByUserId;

    /** Conta ligada a este convite (no {@code accept} do S1-04 ou na reposição de acesso do S1-05). */
    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "activation_attempts", nullable = false)
    private int activationAttempts;

    protected Invitation() {
    }

    public static Invitation create(String email, String fullName, UUID organizationId, List<String> roleCodes,
                                    String tokenHash, Instant expiresAt, UUID invitedByUserId) {
        if (roleCodes == null || roleCodes.isEmpty()) {
            throw new IllegalArgumentException("An invitation needs at least one role");
        }
        if (fullName == null || fullName.isBlank()) {
            throw new IllegalArgumentException("An invitation needs the full name");
        }
        Invitation invitation = new Invitation();
        invitation.email = Emails.normalize(email);
        invitation.fullName = fullName.trim();
        invitation.organizationId = organizationId;
        invitation.roleCodes = List.copyOf(roleCodes);
        invitation.tokenHash = Objects.requireNonNull(tokenHash, "tokenHash");
        invitation.expiresAt = Objects.requireNonNull(expiresAt, "expiresAt");
        invitation.invitedByUserId = invitedByUserId;
        return invitation;
    }

    /** Estado calculado: revogado, aceite, expirado ou pendente (por esta ordem). */
    public InvitationStatus status(Instant now) {
        if (revokedAt != null) {
            return InvitationStatus.REVOKED;
        }
        if (acceptedAt != null) {
            return InvitationStatus.ACCEPTED;
        }
        if (!expiresAt.isAfter(now)) {
            return InvitationStatus.EXPIRED;
        }
        return InvitationStatus.PENDING;
    }

    /** Ainda pode ser usado pelo convidado ({@code PENDING}). */
    public boolean isUsable(Instant now) {
        return status(now) == InvitationStatus.PENDING;
    }

    /** Reenvio: token novo (o anterior deixa de funcionar) e validade nova. */
    public void renew(String tokenHash, Instant expiresAt) {
        this.tokenHash = Objects.requireNonNull(tokenHash, "tokenHash");
        this.expiresAt = Objects.requireNonNull(expiresAt, "expiresAt");
    }

    public void linkUser(UUID userId) {
        this.userId = userId;
    }

    public void revoke(Instant at) {
        if (revokedAt == null) {
            this.revokedAt = at;
        }
    }

    public void accept(Instant at) {
        this.acceptedAt = at;
    }

    /** Mais uma tentativa de activação com código errado; devolve o total. */
    public int registerFailedActivation() {
        return ++this.activationAttempts;
    }

    public String getEmail() {
        return email;
    }

    public String getFullName() {
        return fullName;
    }

    public UUID getOrganizationId() {
        return organizationId;
    }

    public List<String> getRoleCodes() {
        return List.copyOf(roleCodes);
    }

    public String getTokenHash() {
        return tokenHash;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public Instant getAcceptedAt() {
        return acceptedAt;
    }

    public Instant getRevokedAt() {
        return revokedAt;
    }

    public UUID getInvitedByUserId() {
        return invitedByUserId;
    }

    public UUID getUserId() {
        return userId;
    }

    public int getActivationAttempts() {
        return activationAttempts;
    }
}
