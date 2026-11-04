package mz.co.moseguros.proposal.application;

import java.time.Clock;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import mz.co.moseguros.proposal.ProposalSubmitted;
import mz.co.moseguros.proposal.domain.Proposal;
import mz.co.moseguros.proposal.domain.ProposalRepository;
import mz.co.moseguros.proposal.domain.UnderwritingEvaluator.Evaluation;
import mz.co.moseguros.shared.audit.AuditRecord;
import mz.co.moseguros.shared.audit.AuditService;
import mz.co.moseguros.shared.domain.Ids;
import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.error.ErrorCodes;
import mz.co.moseguros.shared.security.CurrentUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Submissão da proposta pelo cliente ou pelo mediador ({@code submitProposal}, S4-05 §6). Tudo numa
 * transacção: os eventos saem depois do commit (outbox) e o e-mail ao cliente também.
 */
@Service
public class SubmissionService {

    private static final Logger log = LoggerFactory.getLogger(SubmissionService.class);

    private final ProposalQueryService queries;
    private final ProposalRepository proposals;
    private final ProposalSystemLookups lookups;
    private final UnderwritingService underwriting;
    private final ProposalService proposalService;
    private final ProposalNotifier notifier;
    private final ApplicationEventPublisher events;
    private final AuditService audit;
    private final CurrentUser currentUser;
    private final ProposalProperties properties;
    private final Clock clock;

    public SubmissionService(ProposalQueryService queries, ProposalRepository proposals, ProposalSystemLookups lookups,
                             UnderwritingService underwriting, ProposalService proposalService,
                             ProposalNotifier notifier, ApplicationEventPublisher events, AuditService audit,
                             CurrentUser currentUser, ProposalProperties properties, Clock clock) {
        this.queries = queries;
        this.proposals = proposals;
        this.lookups = lookups;
        this.underwriting = underwriting;
        this.proposalService = proposalService;
        this.notifier = notifier;
        this.events = events;
        this.audit = audit;
        this.currentUser = currentUser;
        this.properties = properties;
        this.clock = clock;
    }

    /**
     * Por esta ordem: proposta do cliente ou do mediador (404); {@code DRAFT} ou {@code INFO_REQUESTED}
     * (409 {@code INVALID_STATE_TRANSITION}); {@code version} (409 {@code CONCURRENT_MODIFICATION});
     * completa e assinada (409 {@code PROPOSAL_INCOMPLETE} com {@code missing}); seguradora activa (409
     * {@code INSURER_NOT_ACTIVE}). Depois: {@code submitted_at} (só a primeira), {@code ProposalSubmitted},
     * avaliação das regras e {@code ACCEPTED} (automática) ou {@code UNDER_REVIEW} com o SLA; auditoria
     * {@code PROPOSAL.SUBMIT}; e-mail {@code proposal.accepted} ou {@code proposal.submitted}.
     */
    @PreAuthorize("hasAnyAuthority('self:proposals','proposal:create-for-client')")
    @Transactional
    public ProposalDetails submit(UUID proposalId, Long version) {
        Proposal proposal = queries.accessible(proposalId);
        proposal.requireSubmittable();
        DeclarationService.requireVersion(proposal, version);
        Instant now = clock.instant();
        List<ProposalCompleteness.Item> missing = ProposalCompleteness.missing(
                ProposalCompleteness.Facts.of(proposal, queries.inspectionStatus(proposal)), now, true);
        if (!missing.isEmpty()) {
            List<String> items = missing.stream().map(Enum::name).toList();
            throw DomainException.of(ErrorCodes.PROPOSAL_INCOMPLETE, "There are still steps missing: " + items)
                    .withProperty("missing", items);
        }
        boolean insurerActive = lookups.insurer(proposal.getInsurerId())
                .map(ProposalSystemLookups.InsurerRef::active).orElse(false);
        if (!insurerActive) {
            throw DomainException.of(ErrorCodes.INSURER_NOT_ACTIVE, "The insurer is not accepting proposals");
        }

        String before = proposal.getStatus().name();
        proposal.submit(now);
        events.publishEvent(new ProposalSubmitted(Ids.newId(), now, proposal.getId(), proposal.getInsurerId(),
                proposal.getCustomerUserId(), proposal.getBrokerOrgId(), proposal.getTotalAmount()));

        Evaluation evaluation = underwriting.evaluate(proposal, now);
        boolean accepted = underwriting.apply(proposal, evaluation, now);
        if (!accepted) {
            proposal.startReview(now, properties.decisionAlert(), properties.decisionSla());
        }
        proposals.saveAndFlush(proposal);

        Map<String, Object> after = new LinkedHashMap<>();
        after.put("status", proposal.getStatus().name());
        after.put("outcome", evaluation.outcome().name());
        after.put("failures", evaluation.failures().stream().map(Enum::name).toList());
        audit.record(new AuditRecord(proposalService.actorType(), currentUser.id().toString(),
                proposal.getInsurerId(), "PROPOSAL.SUBMIT", "Proposal", proposal.getId().toString(),
                Map.of("status", before), after));
        if (!accepted) {
            notifier.submitted(proposal);
        }
        log.info("Proposal {} submitted: {} {}", proposal.getNumber(), evaluation.outcome(), evaluation.failures());
        return queries.details(proposal);
    }
}
