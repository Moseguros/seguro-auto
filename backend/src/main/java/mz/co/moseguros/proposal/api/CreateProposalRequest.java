package mz.co.moseguros.proposal.api;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import mz.co.moseguros.proposal.domain.ProposalChannel;

/** Proposta a partir de uma cotação do cliente ({@code createProposal}, S4-02 §9). */
public record CreateProposalRequest(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "Cotação OFFERED do próprio cliente")
        UUID quoteId,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED,
                description = "APP ou WEB (cada cliente envia o seu; BROKER → NOT_ALLOWED)")
        ProposalChannel channel) {
}
