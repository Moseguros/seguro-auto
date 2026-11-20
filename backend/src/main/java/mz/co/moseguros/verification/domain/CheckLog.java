package mz.co.moseguros.verification.domain;

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
 * Uma consulta de verificação (S6-03 §1 e §4), em {@code verification.check_log}. Só se insere: nunca
 * guarda a matrícula nem o código em claro ({@code subjectHash} é um HMAC), nem o IP completo
 * ({@code ipPrefix}).
 */
@Entity
@Table(schema = "verification", name = "check_log")
public class CheckLog extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "subject_type", nullable = false, updatable = false)
    private SubjectType subjectType;

    @Column(name = "subject_hash", nullable = false, updatable = false)
    private String subjectHash;

    @Column(name = "plate_masked", updatable = false)
    private String plateMasked;

    @Enumerated(EnumType.STRING)
    @Column(name = "result", nullable = false, updatable = false)
    private VerificationResult result;

    @Column(name = "policy_id", updatable = false)
    private UUID policyId;

    @Column(name = "insurer_id", updatable = false)
    private UUID insurerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "checker_type", nullable = false, updatable = false)
    private CheckerType checkerType;

    @Column(name = "checker_user_id", updatable = false)
    private UUID checkerUserId;

    @Column(name = "checker_org_id", updatable = false)
    private UUID checkerOrgId;

    @Column(name = "ip_prefix", updatable = false)
    private String ipPrefix;

    @Column(name = "checked_at", nullable = false, updatable = false)
    private Instant checkedAt;

    protected CheckLog() {
    }

    /**
     * @param checkerUserId obrigatório em {@link CheckerType#POLICE}, {@code null} em {@link CheckerType#PUBLIC}
     */
    public static CheckLog record(SubjectType subjectType, String subjectHash, String plateMasked,
                                  VerificationResult result, UUID policyId, UUID insurerId, CheckerType checkerType,
                                  UUID checkerUserId, UUID checkerOrgId, String ipPrefix, Instant checkedAt) {
        Objects.requireNonNull(checkerType, "checkerType");
        if ((checkerType == CheckerType.POLICE) != (checkerUserId != null)) {
            throw new IllegalArgumentException("checkerUserId is required for POLICE and forbidden for PUBLIC");
        }
        CheckLog log = new CheckLog();
        log.subjectType = Objects.requireNonNull(subjectType, "subjectType");
        log.subjectHash = Objects.requireNonNull(subjectHash, "subjectHash");
        log.plateMasked = plateMasked;
        log.result = Objects.requireNonNull(result, "result");
        log.policyId = policyId;
        log.insurerId = insurerId;
        log.checkerType = checkerType;
        log.checkerUserId = checkerUserId;
        log.checkerOrgId = checkerOrgId;
        log.ipPrefix = ipPrefix;
        log.checkedAt = Objects.requireNonNull(checkedAt, "checkedAt");
        return log;
    }

    public SubjectType getSubjectType() {
        return subjectType;
    }

    public String getSubjectHash() {
        return subjectHash;
    }

    public String getPlateMasked() {
        return plateMasked;
    }

    public VerificationResult getResult() {
        return result;
    }

    public UUID getPolicyId() {
        return policyId;
    }

    public UUID getInsurerId() {
        return insurerId;
    }

    public CheckerType getCheckerType() {
        return checkerType;
    }

    public UUID getCheckerUserId() {
        return checkerUserId;
    }

    public UUID getCheckerOrgId() {
        return checkerOrgId;
    }

    public String getIpPrefix() {
        return ipPrefix;
    }

    public Instant getCheckedAt() {
        return checkedAt;
    }
}
