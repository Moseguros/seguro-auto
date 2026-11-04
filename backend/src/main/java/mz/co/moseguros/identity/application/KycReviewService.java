package mz.co.moseguros.identity.application;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import jakarta.persistence.criteria.Predicate;
import mz.co.moseguros.identity.application.IdentitySystemLookups.CustomerContact;
import mz.co.moseguros.identity.domain.KycCheck;
import mz.co.moseguros.identity.domain.KycCheckRepository;
import mz.co.moseguros.identity.domain.KycCheckStatus;
import mz.co.moseguros.identity.domain.KycRejectionReason;
import mz.co.moseguros.notification.DeliveryResult;
import mz.co.moseguros.notification.NotificationApi;
import mz.co.moseguros.shared.audit.ActorType;
import mz.co.moseguros.shared.audit.AuditRecord;
import mz.co.moseguros.shared.audit.AuditService;
import mz.co.moseguros.shared.domain.Msisdn;
import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.error.ErrorCodes;
import mz.co.moseguros.shared.error.FieldErrorCodes;
import mz.co.moseguros.shared.error.FieldValidationException;
import mz.co.moseguros.shared.security.CurrentUser;
import mz.co.moseguros.shared.tenant.TenantContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Revisão do KYC pela seguradora ({@code /insurer/kyc}; S4-01 §7 e §9). Permissão {@code kyc:review},
 * âmbito {@code ORG} de seguradora: o RLS só mostra as verificações enviadas à seguradora do
 * utilizador; as outras dão 404 {@code KYC_NOT_FOUND}.
 *
 * <p>Depois de verificar ou recusar, se o cliente tiver e-mail verificado, envia-lhe um e-mail na
 * sua língua (fora da transacção: uma falha do envio fica registada em {@code outbound_message} e
 * não desfaz a decisão).
 */
@Service
public class KycReviewService {

    public static final int REASON_MIN = 10;
    public static final int REASON_MAX = 500;
    static final String EMAIL_PURPOSE = "KYC";
    static final String VERIFIED_TEMPLATE = "kyc.verified.email";
    static final String REJECTED_TEMPLATE = "kyc.rejected.email";

    private static final Logger log = LoggerFactory.getLogger(KycReviewService.class);

    private final KycCheckRepository checks;
    private final IdentitySystemLookups lookups;
    private final NotificationApi notifications;
    private final AuditService audit;
    private final CurrentUser currentUser;
    private final TransactionTemplate tx;
    private final Clock clock;
    private final String webBaseUrl;

    public KycReviewService(KycCheckRepository checks, IdentitySystemLookups lookups, NotificationApi notifications,
                            AuditService audit, CurrentUser currentUser, TransactionTemplate tx, Clock clock,
                            @Value("${moseguros.web.base-url:http://localhost:5173}") String webBaseUrl) {
        this.checks = checks;
        this.lookups = lookups;
        this.notifications = notifications;
        this.audit = audit;
        this.currentUser = currentUser;
        this.tx = tx;
        this.clock = clock;
        this.webBaseUrl = webBaseUrl.endsWith("/") ? webBaseUrl.substring(0, webBaseUrl.length() - 1) : webBaseUrl;
    }

    /**
     * Verificação com os dados para o subscritor.
     *
     * @param msisdn   telemóvel do cliente ({@code null} se só tem e-mail); sai mascarado
     * @param previous as outras verificações do mesmo cliente para esta seguradora, da mais recente
     *                 para a mais antiga
     */
    public record ReviewDetails(KycCheck check, Msisdn msisdn, List<KycCheck> previous) {
    }

    /**
     * Verificações enviadas à seguradora.
     *
     * @param status {@code null} = {@code PENDING}
     * @param q      procura no nome, sem diferenciar maiúsculas
     */
    @PreAuthorize("hasAuthority('kyc:review')")
    @Transactional(readOnly = true)
    public Page<KycCheck> list(KycCheckStatus status, String q, Pageable pageable) {
        UUID insurerId = TenantContext.insurerId();
        return checks.findAll(filter(insurerId, status == null ? KycCheckStatus.PENDING : status, q), pageable);
    }

    /** Verificação com o número do documento em claro e os ids dos ficheiros. Auditoria {@code KYC.VIEW}. */
    @PreAuthorize("hasAuthority('kyc:review')")
    @Transactional
    public ReviewDetails get(UUID id) {
        KycCheck check = load(id);
        record(check, "KYC.VIEW", null, new ViewState(check.getId()));
        return details(check);
    }

    /**
     * {@code PENDING → VERIFIED}. Auditoria {@code KYC.VERIFY}.
     *
     * @throws DomainException {@code KYC_NOT_FOUND} (404), {@code INVALID_STATE_TRANSITION} ou
     *                         {@code CONCURRENT_MODIFICATION} (409)
     */
    @PreAuthorize("hasAuthority('kyc:review')")
    public ReviewDetails verify(UUID id, long version) {
        ReviewDetails details = tx.execute(status -> {
            KycCheck check = loadForDecision(id, version);
            DecisionState before = DecisionState.of(check);
            check.verify(currentUser.id(), clock.instant());
            checks.saveAndFlush(check);
            record(check, "KYC.VERIFY", before, DecisionState.of(check));
            return details(check);
        });
        KycCheck check = details.check();
        log.info("Identity check {} verified by {}", check.getId(), currentUser.id());
        sendEmail(check, VERIFIED_TEMPLATE, new Object[] {check.firstName()});
        return details;
    }

    /**
     * {@code PENDING → REJECTED} com o motivo. Auditoria {@code KYC.REJECT}.
     *
     * @param reasonCode um dos {@link KycRejectionReason} (obrigatório)
     * @param reason     10–500 caracteres; obrigatório com {@code OTHER}
     * @throws DomainException 400 {@code VALIDATION_ERROR}, 404 {@code KYC_NOT_FOUND}, 409
     *                         {@code INVALID_STATE_TRANSITION} ou {@code CONCURRENT_MODIFICATION}
     */
    @PreAuthorize("hasAuthority('kyc:review')")
    public ReviewDetails reject(UUID id, String reasonCode, String reason, long version) {
        KycRejectionReason code = parseReason(reasonCode);
        String text = reason == null || reason.isBlank() ? null : reason.trim();
        validateReason(code, text);
        ReviewDetails details = tx.execute(status -> {
            KycCheck check = loadForDecision(id, version);
            DecisionState before = DecisionState.of(check);
            check.reject(currentUser.id(), clock.instant(), code, text);
            checks.saveAndFlush(check);
            record(check, "KYC.REJECT", before, DecisionState.of(check));
            return details(check);
        });
        KycCheck check = details.check();
        log.info("Identity check {} rejected by {} ({})", check.getId(), currentUser.id(), code);
        Object reasonText = new DefaultMessageSourceResolvable(new String[] {"kyc.reason." + code.name()},
                new Object[] {text == null ? "" : text});
        sendEmail(check, REJECTED_TEMPLATE, new Object[] {check.firstName(), reasonText,
                webBaseUrl + "/cliente/identidade"});
        return details;
    }

    private KycCheck loadForDecision(UUID id, long version) {
        KycCheck check = load(id);
        check.ensurePending();
        if (check.getVersion() != version) {
            throw DomainException.of(ErrorCodes.CONCURRENT_MODIFICATION,
                    "The identity check was changed by another request (version " + check.getVersion() + ")");
        }
        return check;
    }

    /** Verificação da seguradora actual; {@code KYC_NOT_FOUND} (404) se não existir ou não for visível. */
    private KycCheck load(UUID id) {
        UUID insurerId = TenantContext.insurerId();
        return checks.findById(id)
                .filter(c -> c.getInsurerId().equals(insurerId))
                .orElseThrow(() -> DomainException.of(ErrorCodes.KYC_NOT_FOUND, "Identity check not found"));
    }

    private ReviewDetails details(KycCheck check) {
        Msisdn msisdn = lookups.customerContact(check.getUserId()).map(CustomerContact::msisdn).orElse(null);
        List<KycCheck> previous = checks.findByUserIdOrderBySubmittedAtDesc(check.getUserId()).stream()
                .filter(c -> !c.getId().equals(check.getId()))
                .filter(c -> c.getInsurerId().equals(check.getInsurerId()))
                .toList();
        return new ReviewDetails(check, msisdn, previous);
    }

    private void record(KycCheck check, String action, Object before, Object after) {
        audit.record(new AuditRecord(ActorType.STAFF, currentUser.id().toString(), check.getInsurerId(), action,
                "KycCheck", check.getId().toString(), before, after));
    }

    private void sendEmail(KycCheck check, String template, Object[] args) {
        CustomerContact contact = lookups.customerContact(check.getUserId()).orElse(null);
        if (contact == null || contact.verifiedEmail() == null) {
            return; // SMS e push chegam no S6-04
        }
        DeliveryResult result = notifications.sendEmail(contact.verifiedEmail(), template, args, contact.language(),
                EMAIL_PURPOSE, check.getId());
        if (result.failed()) {
            log.warn("Identity check {}: e-mail not delivered ({})", check.getId(), result.failureReason());
        }
    }

    private static KycRejectionReason parseReason(String value) {
        if (value == null || value.isBlank()) {
            throw new FieldValidationException("reasonCode", FieldErrorCodes.REQUIRED, "The reason code is required");
        }
        try {
            return KycRejectionReason.valueOf(value.trim());
        } catch (IllegalArgumentException e) {
            throw new FieldValidationException("reasonCode", FieldErrorCodes.INVALID_VALUE, "Unknown reason code");
        }
    }

    private static void validateReason(KycRejectionReason code, String reason) {
        if (reason == null) {
            if (code == KycRejectionReason.OTHER) {
                throw new FieldValidationException("reason", FieldErrorCodes.REQUIRED,
                        "The reason is required with OTHER");
            }
            return;
        }
        int length = reason.codePointCount(0, reason.length());
        if (length < REASON_MIN) {
            throw new FieldValidationException("reason", FieldErrorCodes.TOO_SHORT,
                    "The reason must have at least " + REASON_MIN + " characters");
        }
        if (length > REASON_MAX) {
            throw new FieldValidationException("reason", FieldErrorCodes.TOO_LONG,
                    "The reason must have at most " + REASON_MAX + " characters");
        }
    }

    private static Specification<KycCheck> filter(UUID insurerId, KycCheckStatus status, String q) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("insurerId"), insurerId));
            predicates.add(cb.equal(root.get("status"), status));
            if (q != null && !q.isBlank()) {
                String like = "%" + q.trim().toLowerCase(Locale.ROOT).replace("\\", "\\\\").replace("%", "\\%")
                        .replace("_", "\\_") + "%";
                predicates.add(cb.like(cb.lower(root.get("fullName")), like, '\\'));
            }
            return cb.and(predicates.toArray(Predicate[]::new));
        };
    }

    /** Estado auditado ao abrir. */
    public record ViewState(UUID id) {
    }

    /** Estado auditado nas decisões (sem dados pessoais). */
    public record DecisionState(KycCheckStatus status, KycRejectionReason reasonCode, Instant reviewedAt) {

        static DecisionState of(KycCheck check) {
            return new DecisionState(check.getStatus(), check.getRejectionReasonCode(), check.getReviewedAt());
        }
    }
}
