package mz.co.moseguros.proposal.api;

import io.swagger.v3.oas.annotations.media.Schema;

/** Submissão da proposta ({@code submitProposal}, S4-05 §6). */
public record SubmitProposalRequest(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED,
                description = "Versão lida; diferente da gravada → 409 CONCURRENT_MODIFICATION")
        Long version) {
}
