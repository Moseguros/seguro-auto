package mz.co.moseguros.proposal.application;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import mz.co.moseguros.proposal.ProposalStatus;
import mz.co.moseguros.proposal.domain.Inspection;
import mz.co.moseguros.proposal.domain.InspectionMode;
import mz.co.moseguros.proposal.domain.InspectionPhotoRepository;
import mz.co.moseguros.proposal.domain.InspectionRepository;
import mz.co.moseguros.proposal.domain.InspectionStatus;
import mz.co.moseguros.proposal.domain.PhotoSlot;
import mz.co.moseguros.proposal.domain.Proposal;
import mz.co.moseguros.proposal.domain.ProposalRepository;
import mz.co.moseguros.shared.audit.ActorType;
import mz.co.moseguros.shared.audit.AuditRecord;
import mz.co.moseguros.shared.audit.AuditService;
import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.error.ErrorCodes;
import mz.co.moseguros.shared.error.FieldErrorCodes;
import mz.co.moseguros.shared.security.CurrentUser;
import mz.co.moseguros.shared.tenant.TenantContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Revisão das vistorias pela seguradora ({@code /insurer/inspections}; S4-03 §6). Permissão
 * {@code inspection:review}, âmbito {@code ORG} de seguradora: só vistorias da própria seguradora de
 * propostas já submetidas ({@code submitted_at is not null}); as outras dão 404
 * {@code INSPECTION_NOT_FOUND}.
 *
 * <p>Ordem das verificações nas decisões: vistoria (404), proposta {@code UNDER_REVIEW} (409
 * {@code PROPOSAL_NOT_UNDER_REVIEW}), {@code version} da vistoria (409), corpo (400) e o estado da
 * vistoria (409 {@code INVALID_STATE_TRANSITION} ou {@code INSPECTION_PHOTOS_MISSING}).
 */
@Service
public class InspectionReviewService {

    private static final Logger log = LoggerFactory.getLogger(InspectionReviewService.class);

    private final InspectionRepository inspections;
    private final InspectionPhotoRepository photos;
    private final ProposalRepository proposals;
    private final ProposalQueryService queries;
    private final ProposalJson json;
    private final AuditService audit;
    private final CurrentUser currentUser;
    private final UnderwritingService underwriting;
    private final Clock clock;

    public InspectionReviewService(InspectionRepository inspections, InspectionPhotoRepository photos,
                                   ProposalRepository proposals, ProposalQueryService queries, ProposalJson json,
                                   AuditService audit, CurrentUser currentUser, UnderwritingService underwriting,
                                   Clock clock) {
        this.inspections = inspections;
        this.photos = photos;
        this.proposals = proposals;
        this.queries = queries;
        this.json = json;
        this.audit = audit;
        this.currentUser = currentUser;
        this.underwriting = underwriting;
        this.clock = clock;
    }

    /** Linha da lista da seguradora. */
    public record InspectionSummary(Inspection inspection, String proposalNumber, String customerName,
                                    String vehiclePlate, int photoCount) {
    }

    /**
     * {@code listInsurerInspections}.
     *
     * @param status {@code null} = {@code SUBMITTED}
     * @param mode   {@code null} = todos
     */
    @PreAuthorize("hasAuthority('inspection:review')")
    @Transactional(readOnly = true)
    public Page<InspectionSummary> list(InspectionStatus status, InspectionMode mode, Pageable pageable) {
        UUID insurerId = TenantContext.insurerId();
        Page<Inspection> page = inspections.findForInsurer(insurerId,
                status == null ? InspectionStatus.SUBMITTED : status, mode, pageable);
        List<UUID> ids = page.getContent().stream().map(Inspection::getId).toList();
        Map<UUID, Proposal> byId = proposals.findAllById(page.getContent().stream().map(Inspection::getProposalId)
                .toList()).stream().collect(Collectors.toMap(Proposal::getId, Function.identity()));
        Map<UUID, Integer> counts = new HashMap<>();
        if (!ids.isEmpty()) {
            for (Object[] row : photos.countActive(ids)) {
                counts.put((UUID) row[0], ((Number) row[1]).intValue());
            }
        }
        return page.map(i -> {
            Proposal p = byId.get(i.getProposalId());
            return new InspectionSummary(i, p == null ? null : p.getNumber(),
                    p == null ? null : json.policyholder(p.getPolicyholder()).fullName(),
                    p == null ? null : p.getVehicleSnapshot().path("plate").asText(null),
                    counts.getOrDefault(i.getId(), 0));
        });
    }

    /** Vistoria com as fotografias activas ({@code getInsurerInspection}). */
    @PreAuthorize("hasAuthority('inspection:review')")
    @Transactional(readOnly = true)
    public InspectionDetails get(UUID id) {
        return queries.inspectionDetails(load(id).inspection());
    }

    /**
     * {@code approveInspection}: {@code SUBMITTED → APPROVED}. O modo efectivo é o pedido ou, sem ele,
     * o da vistoria: em {@code ON_SITE} as notas são obrigatórias; em {@code SELF_PHOTOS} os seis ângulos.
     * Auditoria {@code INSPECTION.APPROVE}. Com a proposta em análise, as regras de aceitação são
     * avaliadas de novo (S4-05 §8).
     */
    @PreAuthorize("hasAuthority('inspection:review')")
    @Transactional
    public InspectionDetails approve(UUID id, InspectionMode mode, String notes, Long version) {
        Loaded loaded = loadForDecision(id, version);
        Inspection inspection = loaded.inspection();
        InspectionMode effective = mode == null ? inspection.getMode() : mode;
        String text = notes == null || notes.isBlank() ? null : notes.strip();
        FieldErrors errors = new FieldErrors();
        if (text == null && effective == InspectionMode.ON_SITE) {
            errors.add("notes", FieldErrorCodes.REQUIRED, "Describe the on-site inspection");
        } else if (text != null && text.length() > Inspection.REVIEW_NOTES_MAX) {
            errors.add("notes", FieldErrorCodes.TOO_LONG, "At most " + Inspection.REVIEW_NOTES_MAX + " characters");
        }
        errors.throwIfAny();
        Map<String, Object> before = state(inspection);
        Instant now = clock.instant();
        inspection.approve(photos.findActive(inspection.getId()), effective, text, currentUser.id(), now);
        inspections.saveAndFlush(inspection);
        record(loaded.proposal(), inspection, "INSPECTION.APPROVE", before);
        log.info("Inspection {} approved by {}", inspection.getId(), currentUser.id());
        reevaluate(loaded.proposal(), now);
        return queries.inspectionDetails(inspection);
    }

    /**
     * {@code rejectInspection}: {@code SUBMITTED → REJECTED} com o motivo (10–1000) e os ângulos a
     * repetir. A proposta continua {@code UNDER_REVIEW}. Auditoria {@code INSPECTION.REJECT}.
     *
     * @param retakeSlots nomes de {@link PhotoSlot}, sem repetidos (opcional)
     */
    @PreAuthorize("hasAuthority('inspection:review')")
    @Transactional
    public InspectionDetails reject(UUID id, String reason, List<String> retakeSlots, Long version) {
        Loaded loaded = loadForDecision(id, version);
        Inspection inspection = loaded.inspection();
        String text = reason == null ? null : reason.strip();
        FieldErrors errors = new FieldErrors();
        if (text == null || text.isEmpty()) {
            errors.add("reason", FieldErrorCodes.REQUIRED, "The reason is required");
        } else if (text.length() < Inspection.REJECTION_REASON_MIN) {
            errors.add("reason", FieldErrorCodes.TOO_SHORT,
                    "At least " + Inspection.REJECTION_REASON_MIN + " characters");
        } else if (text.length() > Inspection.REJECTION_REASON_MAX) {
            errors.add("reason", FieldErrorCodes.TOO_LONG,
                    "At most " + Inspection.REJECTION_REASON_MAX + " characters");
        }
        List<PhotoSlot> slots = new ArrayList<>();
        Set<PhotoSlot> seen = EnumSet.noneOf(PhotoSlot.class);
        List<String> requested = retakeSlots == null ? List.of() : retakeSlots;
        for (int i = 0; i < requested.size(); i++) {
            String field = "retakeSlots[" + i + "]";
            PhotoSlot slot = PhotoSlot.parse(requested.get(i)).orElse(null);
            if (slot == null) {
                errors.add(field, FieldErrorCodes.INVALID_SLOT, "Unknown angle");
            } else if (!seen.add(slot)) {
                errors.add(field, FieldErrorCodes.DUPLICATED, "Repeated angle");
            } else {
                slots.add(slot);
            }
        }
        errors.throwIfAny();
        Map<String, Object> before = state(inspection);
        inspection.reject(text, slots, currentUser.id(), clock.instant());
        inspections.saveAndFlush(inspection);
        record(loaded.proposal(), inspection, "INSPECTION.REJECT", before);
        log.info("Inspection {} rejected by {}", inspection.getId(), currentUser.id());
        return queries.inspectionDetails(inspection);
    }

    /**
     * Reavaliação ao aprovar a vistoria (S4-05 §8), na mesma transacção: com a proposta em
     * {@code UNDER_REVIEW}, avalia de novo as regras; {@code AUTOMATIC} → aceitação automática; senão
     * grava só o novo {@code underwriting_result}.
     */
    private void reevaluate(Proposal proposal, Instant now) {
        if (proposal.getStatus() != ProposalStatus.UNDER_REVIEW) {
            return;
        }
        boolean accepted = underwriting.apply(proposal, underwriting.evaluate(proposal, now), now);
        proposals.saveAndFlush(proposal);
        if (accepted) {
            log.info("Proposal {} accepted automatically after the inspection was approved", proposal.getNumber());
        }
    }

    private record Loaded(Inspection inspection, Proposal proposal) {
    }

    private Loaded loadForDecision(UUID id, Long version) {
        Loaded loaded = load(id);
        if (loaded.proposal().getStatus() != ProposalStatus.UNDER_REVIEW) {
            throw DomainException.of(ErrorCodes.PROPOSAL_NOT_UNDER_REVIEW,
                    "The proposal is not under review (status " + loaded.proposal().getStatus() + ")");
        }
        if (version == null) {
            new FieldErrors().add("version", FieldErrorCodes.REQUIRED, "The version is required").throwIfAny();
        }
        loaded.inspection().requireVersion(version);
        return loaded;
    }

    /**
     * Vistoria da seguradora actual de uma proposta já submetida.
     *
     * @throws DomainException 404 {@code INSPECTION_NOT_FOUND}
     */
    private Loaded load(UUID id) {
        UUID insurerId = TenantContext.current().insurerId();
        Inspection inspection = id == null || insurerId == null ? null : inspections.findById(id)
                .filter(i -> i.getInsurerId().equals(insurerId)).orElse(null);
        Proposal proposal = inspection == null ? null : proposals.findById(inspection.getProposalId())
                .filter(p -> p.getInsurerId().equals(insurerId) && p.getSubmittedAt() != null).orElse(null);
        if (proposal == null) {
            throw DomainException.of(ErrorCodes.INSPECTION_NOT_FOUND, "Inspection not found");
        }
        return new Loaded(inspection, proposal);
    }

    private static Map<String, Object> state(Inspection i) {
        Map<String, Object> state = new LinkedHashMap<>();
        state.put("status", i.getStatus().name());
        state.put("mode", i.getMode().name());
        state.put("reviewNotes", i.getReviewNotes());
        state.put("rejectionReason", i.getRejectionReason());
        state.put("retakeSlots", i.getRetakeSlots().stream().map(Enum::name).toList());
        return state;
    }

    private void record(Proposal proposal, Inspection inspection, String action, Map<String, Object> before) {
        Map<String, Object> after = new LinkedHashMap<>();
        after.put("proposalId", proposal.getId());
        after.putAll(state(inspection));
        after.put("reviewedAt", inspection.getReviewedAt());
        audit.record(new AuditRecord(ActorType.STAFF, currentUser.id().toString(), inspection.getInsurerId(), action,
                "Inspection", inspection.getId().toString(), before, after));
    }
}
