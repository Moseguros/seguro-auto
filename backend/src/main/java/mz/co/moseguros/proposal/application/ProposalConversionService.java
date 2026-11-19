package mz.co.moseguros.proposal.application;

import java.time.Clock;
import java.util.Map;
import java.util.UUID;

import mz.co.moseguros.proposal.ProposalStatus;
import mz.co.moseguros.proposal.domain.Proposal;
import mz.co.moseguros.proposal.domain.ProposalRepository;
import mz.co.moseguros.shared.audit.ActorType;
import mz.co.moseguros.shared.audit.AuditRecord;
import mz.co.moseguros.shared.audit.AuditService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Conversão da proposta em apólice ({@code ProposalApi.markConverted}, S6-01; modelo transversal §3),
 * chamada pelo {@code policy} na transacção da emissão (em {@code SYSTEM}): {@code PAID → CONVERTED} com a
 * apólice e auditoria {@code PROPOSAL.CONVERT}. Idempotente com a mesma apólice.
 */
@Service
public class ProposalConversionService {

    static final String ACTOR = "ProposalConversionService";
    static final String ACTION = "PROPOSAL.CONVERT";
    private static final Logger log = LoggerFactory.getLogger(ProposalConversionService.class);

    private final ProposalRepository proposals;
    private final AuditService audit;
    private final Clock clock;

    public ProposalConversionService(ProposalRepository proposals, AuditService audit, Clock clock) {
        this.proposals = proposals;
        this.audit = audit;
        this.clock = clock;
    }

    /**
     * Na transacção de quem chama (ou numa nova), no âmbito de quem chama, com a proposta bloqueada.
     *
     * @throws mz.co.moseguros.shared.error.DomainException 404 {@code PROPOSAL_NOT_FOUND}; 409
     *         {@code INVALID_STATE_TRANSITION} fora de {@code PAID} ou já convertida noutra apólice
     */
    @Transactional
    public void markConverted(UUID proposalId, UUID policyId) {
        Proposal proposal = proposals.lock(proposalId).orElseThrow(ProposalQueryService::notFound);
        ProposalStatus from = proposal.getStatus();
        if (!proposal.markConverted(policyId, clock.instant())) {
            return; // já convertida nesta apólice
        }
        proposals.saveAndFlush(proposal);
        audit.record(new AuditRecord(ActorType.SYSTEM, ACTOR, proposal.getInsurerId(), ACTION, "Proposal",
                proposal.getId().toString(), Map.of("status", from.name()),
                Map.of("status", proposal.getStatus().name(), "policyId", policyId.toString())));
        log.info("Proposal converted proposalId={} policyId={}", proposalId, policyId);
    }
}
