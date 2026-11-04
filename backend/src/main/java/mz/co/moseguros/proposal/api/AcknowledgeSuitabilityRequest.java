package mz.co.moseguros.proposal.api;

import io.swagger.v3.oas.annotations.media.Schema;

/** Confirmação consciente dos avisos de adequação ({@code acknowledgeProposalSuitability}). */
public record AcknowledgeSuitabilityRequest(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED,
                description = "Versão lida; diferente da gravada → 409 CONCURRENT_MODIFICATION")
        Long version) {
}
