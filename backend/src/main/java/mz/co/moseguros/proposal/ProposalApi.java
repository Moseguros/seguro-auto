package mz.co.moseguros.proposal;

import java.util.Optional;
import java.util.UUID;

import mz.co.moseguros.proposal.application.ProposalConversionService;
import mz.co.moseguros.proposal.application.ProposalQueryService;
import org.springframework.stereotype.Service;

/**
 * Interface pública do módulo Propostas (modelo transversal §3). {@code brokerCustomers} entra no
 * S11-01; {@code submitFromUssd}, {@code findAwaitingPayment} e {@code findByQuoteId} no S12-02.
 */
@Service
public class ProposalApi {

    private final ProposalQueryService queries;
    private final ProposalConversionService conversions;

    public ProposalApi(ProposalQueryService queries, ProposalConversionService conversions) {
        this.queries = queries;
        this.conversions = conversions;
    }

    /**
     * Proposta no âmbito de quem chama (RLS); vazio se não existir ou não for visível. O S6-01
     * chama-o num listener em {@code SYSTEM}.
     */
    public Optional<ProposalView> view(UUID proposalId) {
        return queries.view(proposalId);
    }

    /**
     * Apólice emitida (S6-01): {@code PAID → CONVERTED} com {@code policy_id} e auditoria
     * {@code PROPOSAL.CONVERT}, na transacção de quem chama e no seu âmbito. Já {@code CONVERTED} com a
     * mesma apólice → nada.
     *
     * @throws mz.co.moseguros.shared.error.DomainException 409 {@code INVALID_STATE_TRANSITION} fora de
     *         {@code PAID} ou com outra apólice; 404 {@code PROPOSAL_NOT_FOUND}
     */
    public void markConverted(UUID proposalId, UUID policyId) {
        conversions.markConverted(proposalId, policyId);
    }
}
