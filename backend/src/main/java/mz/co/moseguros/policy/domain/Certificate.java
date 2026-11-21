package mz.co.moseguros.policy.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import mz.co.moseguros.policy.CertificateStatus;
import mz.co.moseguros.shared.persistence.BaseEntity;

/**
 * Certificado de uma apólice ({@code policy.certificate}, S6-01 §3 e regra 7): válido de {@code start_at}
 * a {@code end_at}. O código de verificação ({@code verificationToken}) nunca vai para logs nem para a
 * auditoria; {@link #toString()} não o mostra.
 */
@Entity
@Table(schema = "policy", name = "certificate")
public class Certificate extends BaseEntity {

    @Column(name = "policy_id", nullable = false, updatable = false)
    private UUID policyId;

    @Column(name = "insurer_id", nullable = false, updatable = false)
    private UUID insurerId;

    @Column(name = "policyholder_user_id", nullable = false, updatable = false)
    private UUID policyholderUserId;

    @Column(name = "number", nullable = false, updatable = false, length = 15)
    private String number;

    @Column(name = "valid_from", nullable = false)
    private Instant validFrom;

    @Column(name = "valid_to", nullable = false)
    private Instant validTo;

    @Column(name = "verification_token", nullable = false, updatable = false, length = 22)
    private String verificationToken;

    @Column(name = "pdf_file_id")
    private UUID pdfFileId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 10)
    private CertificateStatus status;

    @Column(name = "revoked_at")
    private Instant revokedAt;

    @Column(name = "revoked_reason", length = 30)
    private String revokedReason;

    protected Certificate() {
    }

    /** Certificado {@code VALID} da apólice acabada de emitir. */
    public static Certificate issue(Policy policy, String number, String verificationToken) {
        Objects.requireNonNull(policy, "policy");
        Certificate c = new Certificate();
        c.policyId = policy.getId();
        c.insurerId = policy.getInsurerId();
        c.policyholderUserId = policy.getPolicyholderUserId();
        c.number = Objects.requireNonNull(number, "number");
        c.validFrom = policy.getStartAt();
        c.validTo = policy.getEndAt();
        c.verificationToken = Objects.requireNonNull(verificationToken, "verificationToken");
        c.status = CertificateStatus.VALID;
        return c;
    }

    /**
     * Liga o PDF do certificado (S6-02 §1). Só uma vez: um certificado já com PDF não muda.
     *
     * @throws IllegalStateException se já tiver PDF
     */
    public void attachPdf(UUID fileId) {
        Objects.requireNonNull(fileId, "fileId");
        if (pdfFileId != null) {
            throw new IllegalStateException("The certificate already has a PDF");
        }
        pdfFileId = fileId;
    }

    public UUID getPolicyId() {
        return policyId;
    }

    public UUID getInsurerId() {
        return insurerId;
    }

    public UUID getPolicyholderUserId() {
        return policyholderUserId;
    }

    public String getNumber() {
        return number;
    }

    public Instant getValidFrom() {
        return validFrom;
    }

    public Instant getValidTo() {
        return validTo;
    }

    public String getVerificationToken() {
        return verificationToken;
    }

    public UUID getPdfFileId() {
        return pdfFileId;
    }

    public CertificateStatus getStatus() {
        return status;
    }

    public Instant getRevokedAt() {
        return revokedAt;
    }

    public String getRevokedReason() {
        return revokedReason;
    }
}
