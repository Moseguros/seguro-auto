package mz.co.moseguros.identity.application;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import mz.co.moseguros.document.DocumentApi;
import mz.co.moseguros.document.FileClassification;
import mz.co.moseguros.document.FileRef;
import mz.co.moseguros.document.StoreFileCommand;
import mz.co.moseguros.identity.KycState;
import mz.co.moseguros.identity.KycStatus;
import mz.co.moseguros.identity.domain.CustomerProfile;
import mz.co.moseguros.identity.domain.CustomerProfileRepository;
import mz.co.moseguros.identity.domain.IdDocType;
import mz.co.moseguros.identity.domain.KycCheck;
import mz.co.moseguros.identity.domain.KycCheckRepository;
import mz.co.moseguros.identity.domain.KycCheckStatus;
import mz.co.moseguros.identity.domain.ProfileFingerprint;
import mz.co.moseguros.shared.audit.ActorType;
import mz.co.moseguros.shared.audit.AuditRecord;
import mz.co.moseguros.shared.audit.AuditService;
import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.error.ErrorCodes;
import mz.co.moseguros.shared.error.FieldErrorCodes;
import mz.co.moseguros.shared.error.Problems.FieldError;
import mz.co.moseguros.shared.security.CurrentUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * KYC do cliente autenticado ({@code /me/kyc}; S4-01 §3). Corre em âmbito {@code CUSTOMER}, com
 * {@code self:profile}: o RLS só mostra o perfil e as verificações do próprio cliente.
 */
@Service
public class KycService {

    /** Tipo de dono dos ficheiros no {@code document} ({@link KycFileAccessPolicy}). */
    public static final String OWNER_TYPE = "KYC";
    public static final String PURPOSE_DOC_FRONT = "KYC_DOC_FRONT";
    public static final String PURPOSE_DOC_BACK = "KYC_DOC_BACK";
    public static final String PURPOSE_SELFIE = "KYC_SELFIE";
    /** Fotografias: nunca PDF. */
    public static final Set<String> ALLOWED_CONTENT_TYPES = Set.of("image/jpeg", "image/png", "image/heic");

    private static final Logger log = LoggerFactory.getLogger(KycService.class);

    private final CurrentUser currentUser;
    private final CustomerProfileRepository profiles;
    private final KycCheckRepository checks;
    private final IdentitySystemLookups lookups;
    private final DocumentApi documents;
    private final AuditService audit;
    private final Clock clock;

    public KycService(CurrentUser currentUser, CustomerProfileRepository profiles, KycCheckRepository checks,
                      IdentitySystemLookups lookups, DocumentApi documents, AuditService audit, Clock clock) {
        this.currentUser = currentUser;
        this.profiles = profiles;
        this.checks = checks;
        this.lookups = lookups;
        this.documents = documents;
        this.audit = audit;
        this.clock = clock;
    }

    /** Ficheiro enviado numa parte do pedido; a parte em falta é {@code null}. */
    public record Upload(byte[] content, String originalFilename, String contentType) {
    }

    /** Estado actual e a verificação mais recente ({@code null} sem nenhuma). */
    public record MyKyc(KycState state, KycCheck latest) {
    }

    /**
     * Envia o documento e a selfie (S4-01 §3), pela ordem: perfil incompleto → 409
     * {@code PROFILE_INCOMPLETE}; verificação pendente → 409 {@code KYC_ALREADY_PENDING}; estado
     * {@code VERIFIED} → 409 {@code KYC_ALREADY_VERIFIED}; partes inválidas → 400
     * {@code VALIDATION_ERROR}; ficheiros → {@code FILE_EMPTY} (400), {@code FILE_TOO_LARGE} (413) ou
     * {@code FILE_TYPE_NOT_ALLOWED} (415). Os ficheiros guardam-se cada um na sua transacção
     * ({@link DocumentApi#store}); se o pedido falhar a seguir ficam órfãos e são limpos (S2-01), e a
     * verificação não fica gravada. Auditoria {@code KYC.SUBMIT}.
     *
     * @param insurerId UUID (em texto) da seguradora a quem a verificação é enviada
     */
    @PreAuthorize("hasAuthority('self:profile')")
    @Transactional
    public KycCheck submit(String insurerId, Upload docFront, Upload docBack, Upload selfie) {
        UUID userId = currentUser.id();
        CustomerProfile profile = profiles.findByUserId(userId)
                .orElseThrow(() -> DomainException.of(ErrorCodes.NOT_FOUND, "Customer profile not found"));
        if (!profile.isComplete()) {
            throw DomainException.of(ErrorCodes.PROFILE_INCOMPLETE, "Complete the profile before the identity check");
        }
        if (checks.existsByUserIdAndStatus(userId, KycCheckStatus.PENDING)) {
            throw alreadyPending();
        }
        KycCheck latest = checks.findFirstByUserIdOrderBySubmittedAtDesc(userId).orElse(null);
        if (KycCheck.statusOf(latest, ProfileFingerprint.of(profile)).state() == KycState.VERIFIED) {
            throw DomainException.of(ErrorCodes.KYC_ALREADY_VERIFIED, "The identity is already verified");
        }
        UUID insurer = validate(insurerId, profile.getIdDocType(), docFront, docBack, selfie);

        Instant now = clock.instant();
        KycCheck check = KycCheck.submit(profile, insurer, now);
        UUID front = store(check, PURPOSE_DOC_FRONT, docFront);
        UUID back = docBack == null ? null : store(check, PURPOSE_DOC_BACK, docBack);
        UUID selfieId = store(check, PURPOSE_SELFIE, selfie);
        check.attachFiles(front, back, selfieId);
        try {
            checks.saveAndFlush(check);
        } catch (DataIntegrityViolationException e) {
            // Dois envios em simultâneo: o índice ux_kyc_pending_per_user recusa o segundo.
            throw alreadyPending();
        }
        documents.markLinked(front);
        if (back != null) {
            documents.markLinked(back);
        }
        documents.markLinked(selfieId);
        audit.record(new AuditRecord(ActorType.CUSTOMER, userId.toString(), insurer, "KYC.SUBMIT", "KycCheck",
                check.getId().toString(), null, SubmitState.of(check)));
        log.info("Customer {} submitted identity check {} to insurer {}", userId, check.getId(), insurer);
        return check;
    }

    /** Estado actual (S4-01 §5) e a verificação mais recente. */
    @PreAuthorize("hasAuthority('self:profile')")
    @Transactional(readOnly = true)
    public MyKyc getMine() {
        UUID userId = currentUser.id();
        KycCheck latest = checks.findFirstByUserIdOrderBySubmittedAtDesc(userId).orElse(null);
        if (latest == null) {
            return new MyKyc(KycState.NONE, null);
        }
        String current = profiles.findByUserId(userId).map(ProfileFingerprint::of).orElse(null);
        KycStatus status = KycCheck.statusOf(latest, current);
        return new MyKyc(status.state(), latest);
    }

    /** Valida as partes do pedido (um erro por campo) e devolve a seguradora. */
    private UUID validate(String insurerId, IdDocType docType, Upload docFront, Upload docBack, Upload selfie) {
        List<FieldError> errors = new ArrayList<>();
        UUID insurer = null;
        if (insurerId == null || insurerId.isBlank()) {
            errors.add(new FieldError("insurerId", FieldErrorCodes.REQUIRED, "Choose the insurer"));
        } else {
            insurer = parseUuid(insurerId.trim());
            if (insurer == null || !lookups.insurerActive(insurer)) {
                errors.add(new FieldError("insurerId", FieldErrorCodes.INVALID_INSURER,
                        "Not an active insurer"));
            }
        }
        if (docFront == null) {
            errors.add(new FieldError("docFront", FieldErrorCodes.REQUIRED, "The document front is required"));
        }
        if (KycCheck.requiresBack(docType) && docBack == null) {
            errors.add(new FieldError("docBack", FieldErrorCodes.REQUIRED,
                    "The document back is required for " + docType));
        } else if (!KycCheck.requiresBack(docType) && docBack != null) {
            errors.add(new FieldError("docBack", FieldErrorCodes.NOT_ALLOWED,
                    "A passport has no back page"));
        }
        if (selfie == null) {
            errors.add(new FieldError("selfie", FieldErrorCodes.REQUIRED, "The selfie is required"));
        }
        if (!errors.isEmpty()) {
            throw DomainException.of(ErrorCodes.VALIDATION_ERROR, "Request validation failed")
                    .withProperty("errors", errors);
        }
        return insurer;
    }

    private UUID store(KycCheck check, String purpose, Upload upload) {
        FileRef ref = documents.store(new StoreFileCommand(OWNER_TYPE, check.getId(), purpose,
                FileClassification.SENSITIVE, check.getInsurerId(), upload.originalFilename(), upload.contentType(),
                ALLOWED_CONTENT_TYPES, upload.content()));
        return ref.id();
    }

    private static UUID parseUuid(String value) {
        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private static DomainException alreadyPending() {
        return DomainException.of(ErrorCodes.KYC_ALREADY_PENDING, "An identity check is already under review");
    }

    /** Estado auditado no envio: o documento só mascarado. */
    public record SubmitState(UUID id, UUID insurerId, IdDocType idDocType, String idDocNumberMasked,
                              KycCheckStatus status) {

        static SubmitState of(KycCheck check) {
            return new SubmitState(check.getId(), check.getInsurerId(), check.getIdDocType(),
                    check.idDocNumberMasked(), check.getStatus());
        }
    }
}
