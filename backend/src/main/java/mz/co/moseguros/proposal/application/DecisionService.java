package mz.co.moseguros.proposal.application;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import mz.co.moseguros.catalog.ProductType;
import mz.co.moseguros.identity.IdentityApi;
import mz.co.moseguros.identity.KycState;
import mz.co.moseguros.identity.KycStatus;
import mz.co.moseguros.proposal.AcceptanceMode;
import mz.co.moseguros.proposal.ProposalAccepted;
import mz.co.moseguros.proposal.ProposalInfoRequested;
import mz.co.moseguros.proposal.ProposalRejected;
import mz.co.moseguros.proposal.ProposalStatus;
import mz.co.moseguros.proposal.domain.InfoRequestItem;
import mz.co.moseguros.proposal.domain.Inspection;
import mz.co.moseguros.proposal.domain.InspectionRepository;
import mz.co.moseguros.proposal.domain.InspectionStatus;
import mz.co.moseguros.proposal.domain.Proposal;
import mz.co.moseguros.proposal.domain.ProposalChannel;
import mz.co.moseguros.proposal.domain.ProposalRepository;
import mz.co.moseguros.proposal.domain.RejectionReasonCode;
import mz.co.moseguros.proposal.domain.Signature;
import mz.co.moseguros.proposal.domain.SignatureRepository;
import mz.co.moseguros.proposal.domain.SlaStatus;
import mz.co.moseguros.proposal.domain.UnderwritingRules;
import mz.co.moseguros.shared.audit.ActorType;
import mz.co.moseguros.shared.audit.AuditRecord;
import mz.co.moseguros.shared.audit.AuditService;
import mz.co.moseguros.shared.domain.Ids;
import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.error.ErrorCodes;
import mz.co.moseguros.shared.error.FieldErrorCodes;
import mz.co.moseguros.shared.security.CurrentUser;
import mz.co.moseguros.shared.tenant.TenantContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Propostas na seguradora ({@code /insurer/proposals}, S4-05 §7): fila com o SLA, consulta e as
 * decisões do subscritor (aceitar, recusar, pedir informação). Âmbito {@code ORG} de seguradora: só
 * propostas da própria seguradora já submetidas ({@code submitted_at is not null}); as outras → 404
 * {@code PROPOSAL_NOT_FOUND}.
 *
 * <p>Ordem das verificações nas decisões (como na vistoria, S4-03): proposta (404), {@code UNDER_REVIEW}
 * (409 {@code PROPOSAL_NOT_UNDER_REVIEW}), {@code version} (409 {@code CONCURRENT_MODIFICATION}), corpo
 * (400) e as condições da aceitação (409 {@code INSPECTION_NOT_APPROVED}, {@code KYC_NOT_VERIFIED}).
 */
@Service
public class DecisionService {

    static final int TEXT_MIN = 10;
    private static final Logger log = LoggerFactory.getLogger(DecisionService.class);

    private final ProposalRepository proposals;
    private final InspectionRepository inspections;
    private final SignatureRepository signatures;
    private final ProposalQueryService queries;
    private final ProposalSystemLookups lookups;
    private final IdentityApi identity;
    private final ProposalNotifier notifier;
    private final ApplicationEventPublisher events;
    private final AuditService audit;
    private final CurrentUser currentUser;
    private final ProposalProperties properties;
    private final Clock clock;

    public DecisionService(ProposalRepository proposals, InspectionRepository inspections,
                           SignatureRepository signatures, ProposalQueryService queries, ProposalSystemLookups lookups,
                           IdentityApi identity, ProposalNotifier notifier, ApplicationEventPublisher events,
                           AuditService audit, CurrentUser currentUser, ProposalProperties properties, Clock clock) {
        this.proposals = proposals;
        this.inspections = inspections;
        this.signatures = signatures;
        this.queries = queries;
        this.lookups = lookups;
        this.identity = identity;
        this.notifier = notifier;
        this.events = events;
        this.audit = audit;
        this.currentUser = currentUser;
        this.properties = properties;
        this.clock = clock;
    }

    // --- Leituras ---

    /**
     * Filtros da fila.
     *
     * @param statuses vazio = {@code UNDER_REVIEW}
     * @param q        número exacto ou parte do nome do tomador (sem distinguir maiúsculas)
     * @param from     primeiro dia de submissão (Maputo), inclusivo
     * @param to       último dia de submissão (Maputo), inclusivo
     */
    public record Filter(Set<ProposalStatus> statuses, SlaStatus slaStatus, ProductType productType,
                         ProposalChannel channel, String q, LocalDate from, LocalDate to) {
    }

    /** Linha da fila. */
    public record Row(Proposal proposal, KycState kycState, InspectionStatus inspectionStatus, SlaStatus slaStatus) {
    }

    /** Proposta para a seguradora (S4-05 §7). */
    public record InsurerDetails(ProposalDetails details, KycStatus kyc, List<Signature> signatures,
                                 SlaStatus slaStatus) {

        /** A verificação mais recente é desta seguradora (S4-01 "Questões"). */
        public boolean verifiedByThisInsurer() {
            return kyc.verifiedByInsurerId() != null
                    && kyc.verifiedByInsurerId().equals(details.proposal().getInsurerId());
        }
    }

    /** {@code listInsurerProposals}. */
    @PreAuthorize("hasAuthority('proposal:read')")
    @Transactional(readOnly = true)
    public Page<Row> list(Filter filter, Pageable pageable) {
        UUID insurerId = TenantContext.current().insurerId();
        if (insurerId == null) {
            return Page.empty(pageable);
        }
        Instant now = clock.instant();
        Page<Proposal> page = proposals.findAll(specification(insurerId, filter, now), pageable);
        List<UUID> withInspection = page.getContent().stream().filter(Proposal::isInspectionRequired)
                .map(Proposal::getId).toList();
        Map<UUID, InspectionStatus> inspectionByProposal = withInspection.isEmpty() ? Map.of()
                : inspections.findByProposalIdIn(withInspection).stream()
                .collect(Collectors.toMap(Inspection::getProposalId, Inspection::getStatus));
        Map<UUID, KycState> kycByCustomer = new LinkedHashMap<>();
        return page.map(p -> new Row(p,
                kycByCustomer.computeIfAbsent(p.getCustomerUserId(), id -> identity.kycStatus(id).state()),
                inspectionByProposal.get(p.getId()),
                SlaStatus.of(p.getStatus(), p.getSlaAlertAt(), p.getSlaDueAt(), now)));
    }

    /** {@code getInsurerProposal}. */
    @PreAuthorize("hasAuthority('proposal:read')")
    @Transactional(readOnly = true)
    public InsurerDetails get(UUID id) {
        return insurerDetails(load(id));
    }

    // --- Decisões ---

    /**
     * {@code acceptProposal}: {@code ACCEPTED} com {@code acceptance_mode = MANUAL}. Vistoria
     * obrigatória {@code APPROVED} (409 {@code INSPECTION_NOT_APPROVED}); KYC {@code VERIFIED} ou total
     * abaixo do limiar do KYC simplificado (409 {@code KYC_NOT_VERIFIED}); {@code note} até 1000.
     */
    @PreAuthorize("hasAuthority('proposal:decide')")
    @Transactional
    public InsurerDetails accept(UUID id, String note, Long version) {
        Proposal proposal = loadForDecision(id, version);
        String text = note == null || note.isBlank() ? null : note.strip();
        FieldErrors errors = new FieldErrors();
        if (text != null && text.length() > Proposal.DECISION_TEXT_MAX) {
            errors.add("note", FieldErrorCodes.TOO_LONG, "At most " + Proposal.DECISION_TEXT_MAX + " characters");
        }
        errors.throwIfAny();
        if (proposal.isInspectionRequired()) {
            InspectionStatus inspection = inspections.findByProposalId(proposal.getId())
                    .map(Inspection::getStatus).orElse(null);
            if (inspection != InspectionStatus.APPROVED) {
                throw DomainException.of(ErrorCodes.INSPECTION_NOT_APPROVED, "The inspection has not been approved");
            }
        }
        UnderwritingRules rules = lookups.underwritingRules(proposal.getProductVersionId());
        KycState kyc = identity.kycStatus(proposal.getCustomerUserId()).state();
        if (UnderwritingService.kycRequired(rules, kyc, proposal.getTotalAmount())) {
            throw DomainException.of(ErrorCodes.KYC_NOT_VERIFIED, "The customer's identity has not been verified");
        }
        Instant now = clock.instant();
        Map<String, Object> before = state(proposal);
        proposal.acceptManually(decider(), text, now, properties.expiry());
        proposals.saveAndFlush(proposal);
        events.publishEvent(new ProposalAccepted(Ids.newId(), now, proposal.getId(), proposal.getInsurerId(),
                proposal.getCustomerUserId(), proposal.getBrokerOrgId(), AcceptanceMode.MANUAL));
        record(proposal, "PROPOSAL.ACCEPT", before);
        notifier.accepted(proposal);
        log.info("Proposal {} accepted by {}", proposal.getNumber(), currentUser.id());
        return insurerDetails(proposal);
    }

    /**
     * {@code rejectProposal}: {@code REJECTED} com {@code reasonCode} (obrigatório) e {@code reason}
     * (10–1000).
     */
    @PreAuthorize("hasAuthority('proposal:decide')")
    @Transactional
    public InsurerDetails reject(UUID id, String reasonCode, String reason, Long version) {
        Proposal proposal = loadForDecision(id, version);
        FieldErrors errors = new FieldErrors();
        RejectionReasonCode code = null;
        if (reasonCode == null || reasonCode.isBlank()) {
            errors.add("reasonCode", FieldErrorCodes.REQUIRED, "The reason code is required");
        } else {
            code = RejectionReasonCode.parse(reasonCode).orElse(null);
            if (code == null) {
                errors.add("reasonCode", FieldErrorCodes.INVALID_VALUE, "Unknown reason code");
            }
        }
        String text = text(errors, "reason", reason);
        errors.throwIfAny();
        Instant now = clock.instant();
        Map<String, Object> before = state(proposal);
        proposal.reject(decider(), code, text, now);
        proposals.saveAndFlush(proposal);
        events.publishEvent(new ProposalRejected(Ids.newId(), now, proposal.getId(), proposal.getInsurerId(),
                proposal.getCustomerUserId(), proposal.getBrokerOrgId(), code.name()));
        record(proposal, "PROPOSAL.REJECT", before);
        notifier.rejected(proposal);
        log.info("Proposal {} rejected by {} ({})", proposal.getNumber(), currentUser.id(), code);
        return insurerDetails(proposal);
    }

    /**
     * {@code requestProposalInfo}: {@code INFO_REQUESTED} com a mensagem (10–1000) e os itens (1+, sem
     * repetidos). A assinatura fica invalidada ({@code INFO_REQUESTED}); com o item {@code INSPECTION}
     * a vistoria volta a {@code PENDING} (se estava {@code SUBMITTED} ou {@code APPROVED}).
     */
    @PreAuthorize("hasAuthority('proposal:decide')")
    @Transactional
    public InsurerDetails requestInfo(UUID id, String message, List<String> items, Long version) {
        Proposal proposal = loadForDecision(id, version);
        FieldErrors errors = new FieldErrors();
        String text = text(errors, "message", message);
        List<InfoRequestItem> parsed = new ArrayList<>();
        if (items == null || items.isEmpty()) {
            errors.add("items", FieldErrorCodes.REQUIRED, "At least one item is required");
        } else {
            Set<InfoRequestItem> seen = EnumSet.noneOf(InfoRequestItem.class);
            for (int i = 0; i < items.size(); i++) {
                String field = "items[" + i + "]";
                InfoRequestItem item = InfoRequestItem.parse(items.get(i)).orElse(null);
                if (item == null) {
                    errors.add(field, FieldErrorCodes.INVALID_VALUE, "Unknown item");
                } else if (!seen.add(item)) {
                    errors.add(field, FieldErrorCodes.ANSWER_DUPLICATED, "Repeated item");
                } else {
                    parsed.add(item);
                }
            }
        }
        errors.throwIfAny();
        Instant now = clock.instant();
        Map<String, Object> before = state(proposal);
        proposal.requestInfo(text, parsed, now, properties.expiry());
        String inspectionBefore = null;
        if (parsed.contains(InfoRequestItem.INSPECTION) && proposal.isInspectionRequired()) {
            Inspection inspection = inspections.findByProposalId(proposal.getId()).orElse(null);
            if (inspection != null && (inspection.getStatus() == InspectionStatus.SUBMITTED
                    || inspection.getStatus() == InspectionStatus.APPROVED)) {
                inspectionBefore = inspection.getStatus().name();
                inspection.reopen();
                inspections.saveAndFlush(inspection);
            }
        }
        proposals.saveAndFlush(proposal);
        events.publishEvent(new ProposalInfoRequested(Ids.newId(), now, proposal.getId(), proposal.getInsurerId(),
                proposal.getCustomerUserId(), proposal.getBrokerOrgId(),
                parsed.stream().map(Enum::name).toList()));
        Map<String, Object> extra = new LinkedHashMap<>();
        if (inspectionBefore != null) {
            extra.put("inspectionReopenedFrom", inspectionBefore);
        }
        record(proposal, "PROPOSAL.REQUEST_INFO", before, extra);
        notifier.infoRequested(proposal);
        log.info("Proposal {}: information requested by {} {}", proposal.getNumber(), currentUser.id(), parsed);
        return insurerDetails(proposal);
    }

    // --- Apoio ---

    private InsurerDetails insurerDetails(Proposal proposal) {
        ProposalDetails details = queries.details(proposal);
        return new InsurerDetails(details, identity.kycStatus(proposal.getCustomerUserId()),
                signatures.findByProposalIdOrderBySignedAtDesc(proposal.getId()),
                SlaStatus.of(proposal.getStatus(), proposal.getSlaAlertAt(), proposal.getSlaDueAt(), clock.instant()));
    }

    private Proposal loadForDecision(UUID id, Long version) {
        Proposal proposal = load(id);
        proposal.requireUnderReview();
        if (version == null) {
            new FieldErrors().add("version", FieldErrorCodes.REQUIRED, "The version is required").throwIfAny();
        }
        proposal.requireVersion(version);
        return proposal;
    }

    /**
     * Proposta da seguradora actual já submetida.
     *
     * @throws DomainException 404 {@code PROPOSAL_NOT_FOUND}
     */
    private Proposal load(UUID id) {
        UUID insurerId = TenantContext.current().insurerId();
        if (id == null || insurerId == null) {
            throw ProposalQueryService.notFound();
        }
        return proposals.findById(id)
                .filter(p -> p.getInsurerId().equals(insurerId) && p.getSubmittedAt() != null)
                .orElseThrow(ProposalQueryService::notFound);
    }

    /** Texto obrigatório de 10 a 1000 caracteres (sem os espaços das pontas). */
    private static String text(FieldErrors errors, String field, String value) {
        String text = value == null ? null : value.strip();
        if (text == null || text.isEmpty()) {
            errors.add(field, FieldErrorCodes.REQUIRED, "The " + field + " is required");
        } else if (text.length() < TEXT_MIN) {
            errors.add(field, FieldErrorCodes.TOO_SHORT, "At least " + TEXT_MIN + " characters");
        } else if (text.length() > Proposal.DECISION_TEXT_MAX) {
            errors.add(field, FieldErrorCodes.TOO_LONG, "At most " + Proposal.DECISION_TEXT_MAX + " characters");
        }
        return text;
    }

    private String decider() {
        return currentUser.id().toString();
    }

    private static Map<String, Object> state(Proposal p) {
        Map<String, Object> state = new LinkedHashMap<>();
        state.put("status", p.getStatus().name());
        return state;
    }

    private void record(Proposal proposal, String action, Map<String, Object> before) {
        record(proposal, action, before, Map.of());
    }

    private void record(Proposal proposal, String action, Map<String, Object> before, Map<String, Object> extra) {
        Map<String, Object> after = new LinkedHashMap<>();
        after.put("status", proposal.getStatus().name());
        if (proposal.getAcceptanceMode() != null) {
            after.put("acceptanceMode", proposal.getAcceptanceMode().name());
        }
        if (proposal.getRejectionReasonCode() != null) {
            after.put("rejectionReasonCode", proposal.getRejectionReasonCode().name());
        }
        if (proposal.getDecisionReason() != null) {
            after.put("decisionReason", proposal.getDecisionReason());
        }
        if (proposal.getInfoRequestedAt() != null) {
            after.put("infoRequestMessage", proposal.getInfoRequestMessage());
            after.put("infoRequestItems", proposal.getInfoRequestItems().stream().map(Enum::name).toList());
        }
        if (proposal.getExpiresAt() != null) {
            after.put("expiresAt", proposal.getExpiresAt().toString());
        }
        after.putAll(extra);
        audit.record(new AuditRecord(ActorType.STAFF, currentUser.id().toString(), proposal.getInsurerId(), action,
                "Proposal", proposal.getId().toString(), before, after));
    }

    // --- Filtro da fila ---

    static Specification<Proposal> specification(UUID insurerId, Filter filter, Instant now) {
        return (root, query, cb) -> {
            List<Predicate> where = new ArrayList<>();
            where.add(cb.equal(root.get("insurerId"), insurerId));
            where.add(cb.isNotNull(root.get("submittedAt")));
            Set<ProposalStatus> statuses = filter.statuses() == null || filter.statuses().isEmpty()
                    ? Set.of(ProposalStatus.UNDER_REVIEW) : filter.statuses();
            where.add(root.get("status").in(statuses));
            if (filter.slaStatus() != null) {
                where.add(cb.equal(root.get("status"), ProposalStatus.UNDER_REVIEW));
                Expression<Instant> alertAt = root.get("slaAlertAt");
                Expression<Instant> dueAt = root.get("slaDueAt");
                switch (filter.slaStatus()) {
                    case ON_TIME -> where.add(cb.greaterThan(alertAt, now));
                    case ALERT -> {
                        where.add(cb.lessThanOrEqualTo(alertAt, now));
                        where.add(cb.greaterThan(dueAt, now));
                    }
                    case OVERDUE -> where.add(cb.lessThanOrEqualTo(dueAt, now));
                    default -> throw new IllegalStateException();
                }
            }
            if (filter.productType() != null) {
                where.add(cb.equal(root.get("productType"), filter.productType()));
            }
            if (filter.channel() != null) {
                where.add(cb.equal(root.get("channel"), filter.channel()));
            }
            if (filter.from() != null) {
                where.add(cb.greaterThanOrEqualTo(root.get("submittedAt"),
                        filter.from().atStartOfDay(Proposal.ZONE).toInstant()));
            }
            if (filter.to() != null) {
                where.add(cb.lessThan(root.get("submittedAt"),
                        filter.to().plusDays(1).atStartOfDay(Proposal.ZONE).toInstant()));
            }
            String q = filter.q() == null ? null : filter.q().strip();
            if (q != null && !q.isEmpty()) {
                Expression<String> fullName = cb.function("jsonb_extract_path_text", String.class,
                        root.get("policyholder"), cb.literal("fullName"));
                String pattern = "%" + escapeLike(q.toLowerCase(java.util.Locale.ROOT)) + "%";
                where.add(cb.or(cb.equal(root.get("number"), q.toUpperCase(java.util.Locale.ROOT)),
                        cb.like(cb.lower(fullName), pattern, '\\')));
            }
            return cb.and(where.toArray(Predicate[]::new));
        };
    }

    private static String escapeLike(String value) {
        return value.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
    }
}
