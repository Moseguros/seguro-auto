package mz.co.moseguros.identity.domain;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import mz.co.moseguros.identity.KycState;
import mz.co.moseguros.identity.KycStatus;
import mz.co.moseguros.shared.crypto.EncryptedStringConverter;
import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.error.ErrorCodes;
import mz.co.moseguros.shared.persistence.BaseEntity;

/**
 * Verificação de identidade de um cliente ({@code identity.kyc_check}; S4-01 §2). Guarda uma
 * cópia dos dados do perfil no momento do envio (o subscritor compara-a com as fotografias) e a
 * {@link ProfileFingerprint} desses dados. O número do documento está em claro no Java e cifrado
 * na coluna.
 *
 * <p>Transições: {@code PENDING → VERIFIED} ({@link #verify}) e {@code PENDING → REJECTED}
 * ({@link #reject}); qualquer outra dá 409 {@code INVALID_STATE_TRANSITION}.
 */
@Entity
@Table(schema = "identity", name = "kyc_check")
public class KycCheck extends BaseEntity {

    @Column(name = "user_id", nullable = false, updatable = false)
    private UUID userId;

    @Column(name = "insurer_id", nullable = false, updatable = false)
    private UUID insurerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 10)
    private KycCheckStatus status;

    @Column(name = "full_name", nullable = false, updatable = false, length = 120)
    private String fullName;

    @Column(name = "birth_date", nullable = false, updatable = false)
    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "id_doc_type", nullable = false, updatable = false, length = 10)
    private IdDocType idDocType;

    @Convert(converter = EncryptedStringConverter.class)
    @Column(name = "id_doc_number_enc", nullable = false, updatable = false)
    private String idDocNumber;

    @Column(name = "profile_fingerprint", nullable = false, updatable = false, length = 64)
    private String profileFingerprint;

    @Column(name = "doc_front_file_id", nullable = false)
    private UUID docFrontFileId;

    @Column(name = "doc_back_file_id")
    private UUID docBackFileId;

    @Column(name = "selfie_file_id", nullable = false)
    private UUID selfieFileId;

    @Column(name = "submitted_at", nullable = false, updatable = false)
    private Instant submittedAt;

    @Column(name = "reviewed_by")
    private UUID reviewedBy;

    @Column(name = "reviewed_at")
    private Instant reviewedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "rejection_reason_code", length = 30)
    private KycRejectionReason rejectionReasonCode;

    @Column(name = "rejection_reason", length = 500)
    private String rejectionReason;

    protected KycCheck() {
    }

    /**
     * Verificação nova, {@code PENDING}, com a cópia do perfil (que tem de estar completo). Os
     * ficheiros juntam-se com {@link #attachFiles} antes de gravar.
     */
    public static KycCheck submit(CustomerProfile profile, UUID insurerId, Instant now) {
        if (!profile.isComplete()) {
            throw new IllegalArgumentException("KYC requires a complete profile");
        }
        KycCheck check = new KycCheck();
        check.userId = profile.getUserId();
        check.insurerId = Objects.requireNonNull(insurerId, "insurerId");
        check.status = KycCheckStatus.PENDING;
        check.fullName = profile.getFullName();
        check.birthDate = profile.getBirthDate();
        check.idDocType = profile.getIdDocType();
        check.idDocNumber = profile.getIdDocNumber();
        check.profileFingerprint = ProfileFingerprint.of(profile);
        check.submittedAt = Objects.requireNonNull(now, "now");
        return check;
    }

    /** O verso é obrigatório para {@code BI} e {@code DIRE} e proibido para {@code PASSPORT}. */
    public static boolean requiresBack(IdDocType type) {
        return type != IdDocType.PASSPORT;
    }

    /** Ficheiros guardados no {@code document} (dono {@code KYC}, {@code ownerId} = {@link #getId()}). */
    public void attachFiles(UUID docFront, UUID docBack, UUID selfie) {
        this.docFrontFileId = Objects.requireNonNull(docFront, "docFront");
        this.docBackFileId = docBack;
        this.selfieFileId = Objects.requireNonNull(selfie, "selfie");
    }

    /** {@code PENDING → VERIFIED}. */
    public void verify(UUID reviewer, Instant now) {
        ensurePending();
        this.status = KycCheckStatus.VERIFIED;
        this.reviewedBy = Objects.requireNonNull(reviewer, "reviewer");
        this.reviewedAt = Objects.requireNonNull(now, "now");
    }

    /** {@code PENDING → REJECTED}; o motivo ({@code reason}) já validado. */
    public void reject(UUID reviewer, Instant now, KycRejectionReason code, String reason) {
        ensurePending();
        this.status = KycCheckStatus.REJECTED;
        this.reviewedBy = Objects.requireNonNull(reviewer, "reviewer");
        this.reviewedAt = Objects.requireNonNull(now, "now");
        this.rejectionReasonCode = Objects.requireNonNull(code, "code");
        this.rejectionReason = reason;
    }

    /** {@code INVALID_STATE_TRANSITION} (409) se não estiver {@code PENDING}. */
    public void ensurePending() {
        if (status != KycCheckStatus.PENDING) {
            throw DomainException.of(ErrorCodes.INVALID_STATE_TRANSITION,
                    "The identity check is " + status + ", not PENDING");
        }
    }

    /**
     * Estado KYC de um cliente (S4-01 §5) a partir da sua verificação mais recente.
     *
     * @param latest             verificação mais recente ({@code submitted_at desc}); {@code null} = nenhuma
     * @param currentFingerprint {@link ProfileFingerprint} do perfil actual ({@code null} se incompleto)
     */
    public static KycStatus statusOf(KycCheck latest, String currentFingerprint) {
        if (latest == null) {
            return KycStatus.none();
        }
        return switch (latest.status) {
            case PENDING -> new KycStatus(KycState.PENDING, latest.getId(), null, null);
            case REJECTED -> new KycStatus(KycState.REJECTED, latest.getId(), null, null);
            case VERIFIED -> new KycStatus(
                    latest.profileFingerprint.equals(currentFingerprint) ? KycState.VERIFIED : KycState.OUTDATED,
                    latest.getId(), latest.reviewedAt, latest.insurerId);
        };
    }

    /** {@code *********345A}. */
    public String idDocNumberMasked() {
        return CustomerProfileRules.maskDocumentNumber(idDocNumber);
    }

    /** Primeiro nome, para as mensagens. */
    public String firstName() {
        String trimmed = fullName.trim();
        int space = trimmed.indexOf(' ');
        return space < 0 ? trimmed : trimmed.substring(0, space);
    }

    public UUID getUserId() {
        return userId;
    }

    public UUID getInsurerId() {
        return insurerId;
    }

    public KycCheckStatus getStatus() {
        return status;
    }

    public String getFullName() {
        return fullName;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public IdDocType getIdDocType() {
        return idDocType;
    }

    /** Em claro: só para o subscritor ({@code getInsurerKycCheck}); nunca para um log. */
    public String getIdDocNumber() {
        return idDocNumber;
    }

    public String getProfileFingerprint() {
        return profileFingerprint;
    }

    public UUID getDocFrontFileId() {
        return docFrontFileId;
    }

    public UUID getDocBackFileId() {
        return docBackFileId;
    }

    public UUID getSelfieFileId() {
        return selfieFileId;
    }

    public Instant getSubmittedAt() {
        return submittedAt;
    }

    public UUID getReviewedBy() {
        return reviewedBy;
    }

    public Instant getReviewedAt() {
        return reviewedAt;
    }

    public KycRejectionReason getRejectionReasonCode() {
        return rejectionReasonCode;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }
}
