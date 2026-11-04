package mz.co.moseguros.proposal.domain;

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
 * Assinatura electrónica simples de uma revisão da proposta ({@code proposal.signature}, S4-04 §5):
 * o tomador confirmou com um código OTP ligado ao SHA-256 do PDF {@code documentFileId}. Não se apaga:
 * uma alteração posterior da proposta marca-a invalidada ({@link #invalidate}).
 */
@Entity
@Table(schema = "proposal", name = "signature")
public class Signature extends BaseEntity {

    /** Método de assinatura; nesta fase só OTP. */
    public enum Method { OTP }

    /** Porque deixou de valer. */
    public enum InvalidationReason { CONTENT_CHANGED, INFO_REQUESTED }

    static final int MAX_USER_AGENT = 200;
    static final int MAX_IP = 45;

    @Column(name = "insurer_id", nullable = false, updatable = false)
    private UUID insurerId;

    @Column(name = "proposal_id", nullable = false, updatable = false)
    private UUID proposalId;

    @Column(name = "content_revision", nullable = false, updatable = false)
    private int contentRevision;

    @Column(name = "signer_user_id", nullable = false, updatable = false)
    private UUID signerUserId;

    @Enumerated(EnumType.STRING)
    @Column(name = "method", nullable = false, updatable = false, length = 10)
    private Method method;

    @Column(name = "document_file_id", nullable = false, updatable = false)
    private UUID documentFileId;

    @Column(name = "document_hash", nullable = false, updatable = false, length = 64)
    private String documentHash;

    @Column(name = "otp_challenge_id", nullable = false, updatable = false)
    private UUID otpChallengeId;

    @Enumerated(EnumType.STRING)
    @Column(name = "otp_channel", nullable = false, updatable = false, length = 10)
    private Channel otpChannel;

    @Column(name = "destination_masked", nullable = false, updatable = false, length = 254)
    private String destinationMasked;

    @Column(name = "assisted_by_user_id", updatable = false)
    private UUID assistedByUserId;

    @Column(name = "assisted_by_org_id", updatable = false)
    private UUID assistedByOrgId;

    @Column(name = "signed_at", nullable = false, updatable = false)
    private Instant signedAt;

    @Column(name = "ip", updatable = false, length = MAX_IP)
    private String ip;

    @Column(name = "user_agent", updatable = false, length = MAX_USER_AGENT)
    private String userAgent;

    @Column(name = "invalidated_at")
    private Instant invalidatedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "invalidated_reason", length = 20)
    private InvalidationReason invalidatedReason;

    protected Signature() {
    }

    /**
     * Dados de uma assinatura confirmada.
     *
     * @param otpChallengeId    desafio que tinha o código
     * @param destinationMasked para onde o código foi enviado, mascarado
     * @param ip                do pedido de confirmação ({@code ClientIpResolver})
     * @param userAgent         do pedido de confirmação (truncado a 200)
     */
    public record Signed(UUID documentFileId, String documentHash, UUID otpChallengeId, Channel otpChannel,
                         String destinationMasked, UUID assistedByUserId, UUID assistedByOrgId, Instant signedAt,
                         String ip, String userAgent) {
    }

    /** Assinatura da revisão actual da proposta, pelo cliente dono. */
    public static Signature of(Proposal proposal, Signed s) {
        Signature signature = new Signature();
        signature.insurerId = proposal.getInsurerId();
        signature.proposalId = proposal.getId();
        signature.contentRevision = proposal.getContentRevision();
        signature.signerUserId = proposal.getCustomerUserId();
        signature.method = Method.OTP;
        signature.documentFileId = Objects.requireNonNull(s.documentFileId(), "documentFileId");
        signature.documentHash = Objects.requireNonNull(s.documentHash(), "documentHash");
        signature.otpChallengeId = Objects.requireNonNull(s.otpChallengeId(), "otpChallengeId");
        signature.otpChannel = Objects.requireNonNull(s.otpChannel(), "otpChannel");
        signature.destinationMasked = Objects.requireNonNull(s.destinationMasked(), "destinationMasked");
        signature.assistedByUserId = s.assistedByUserId();
        signature.assistedByOrgId = s.assistedByOrgId();
        signature.signedAt = Objects.requireNonNull(s.signedAt(), "signedAt");
        signature.ip = truncate(s.ip(), MAX_IP);
        signature.userAgent = truncate(s.userAgent(), MAX_USER_AGENT);
        return signature;
    }

    /** Deixa de valer (a proposta mudou ou a seguradora pediu informação). Idempotente. */
    void invalidate(InvalidationReason reason, Instant at) {
        if (invalidatedAt == null) {
            this.invalidatedAt = Objects.requireNonNull(at, "at");
            this.invalidatedReason = Objects.requireNonNull(reason, "reason");
        }
    }

    public boolean isValid() {
        return invalidatedAt == null;
    }

    /** Assinatura feita pelo cliente com a ajuda de um mediador (S4-06). */
    public boolean isAssisted() {
        return assistedByUserId != null;
    }

    private static String truncate(String value, int max) {
        return value == null || value.length() <= max ? value : value.substring(0, max);
    }

    public UUID getInsurerId() {
        return insurerId;
    }

    public UUID getProposalId() {
        return proposalId;
    }

    public int getContentRevision() {
        return contentRevision;
    }

    public UUID getSignerUserId() {
        return signerUserId;
    }

    public Method getMethod() {
        return method;
    }

    public UUID getDocumentFileId() {
        return documentFileId;
    }

    public String getDocumentHash() {
        return documentHash;
    }

    public UUID getOtpChallengeId() {
        return otpChallengeId;
    }

    public Channel getOtpChannel() {
        return otpChannel;
    }

    public String getDestinationMasked() {
        return destinationMasked;
    }

    public UUID getAssistedByUserId() {
        return assistedByUserId;
    }

    public UUID getAssistedByOrgId() {
        return assistedByOrgId;
    }

    public Instant getSignedAt() {
        return signedAt;
    }

    public String getIp() {
        return ip;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public Instant getInvalidatedAt() {
        return invalidatedAt;
    }

    public InvalidationReason getInvalidatedReason() {
        return invalidatedReason;
    }
}
