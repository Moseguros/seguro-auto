package mz.co.moseguros.proposal.api;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

/** Corpos das decisões do subscritor sobre a proposta (S4-05 §7). */
final class ProposalDecisionRequests {

    private ProposalDecisionRequests() {
    }

    /** {@code acceptProposal}. */
    public record AcceptProposalRequest(
            @Schema(description = "Nota opcional (até 1000 caracteres)", maxLength = 1000)
            String note,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED,
                    description = "Versão lida; diferente da gravada → 409 CONCURRENT_MODIFICATION")
            Long version) {
    }

    /** {@code rejectProposal}. */
    public record RejectProposalRequest(
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED,
                    allowableValues = {"RISK_NOT_ACCEPTED", "KYC_FAILED", "INSPECTION_FAILED", "FALSE_DECLARATION",
                            "DOCUMENTS_INVALID", "OTHER"})
            String reasonCode,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED, minLength = 10, maxLength = 1000,
                    example = "Histórico de sinistros acima do aceitável.")
            String reason,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            Long version) {
    }

    /** {@code requestProposalInfo}. */
    public record RequestProposalInfoRequest(
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED, minLength = 10, maxLength = 1000,
                    example = "Envie de novo a fotografia do chassis.")
            String message,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED,
                    description = "1 ou mais, sem repetidos: DECLARATIONS, SUITABILITY, INSPECTION, KYC, "
                            + "POLICYHOLDER, OTHER")
            List<String> items,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            Long version) {
    }
}
