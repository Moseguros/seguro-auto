package mz.co.moseguros.proposal.api;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;

/** Nova data de início ({@code updateProposal}, S4-02 §9); a duração e o prémio não mudam. */
public record UpdateProposalRequest(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED,
                description = "Entre hoje e hoje + 60 dias (Africa/Maputo); fora → START_DATE_OUT_OF_RANGE",
                example = "2027-01-10")
        LocalDate startDate,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED,
                description = "Versão lida; diferente da gravada → 409 CONCURRENT_MODIFICATION")
        Long version) {
}
