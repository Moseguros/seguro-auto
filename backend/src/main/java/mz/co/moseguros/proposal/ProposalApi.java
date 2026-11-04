package mz.co.moseguros.proposal;

import java.util.Optional;
import java.util.UUID;

import mz.co.moseguros.proposal.application.ProposalQueryService;
import org.springframework.stereotype.Service;

/**
 * Interface pública do módulo Propostas (modelo transversal §3). {@code markConverted} entra no
 * S6-01; {@code brokerCustomers} no S11-01; {@code submitFromUssd}, {@code findAwaitingPayment} e
 * {@code findByQuoteId} no S12-02.
 */
@Service
public class ProposalApi {

    private final ProposalQueryService queries;

    public ProposalApi(ProposalQueryService queries) {
        this.queries = queries;
    }

    /**
     * Proposta no âmbito de quem chama (RLS); vazio se não existir ou não for visível. O S6-01
     * chama-o num listener em {@code SYSTEM}.
     */
    public Optional<ProposalView> view(UUID proposalId) {
        return queries.view(proposalId);
    }
}
