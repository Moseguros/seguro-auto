package mz.co.moseguros.proposal.api;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;

/** Alteração da proposta pelo mediador ({@code updateBrokerProposal}, S4-06 §5). */
public record UpdateBrokerProposalRequest(
        @Schema(description = "Nova data de início (hoje a hoje + 60 dias, Africa/Maputo); omitida mantém a actual",
                example = "2027-01-10")
        LocalDate startDate,
        @Schema(description = "Só com policyholderSource = BROKER (senão NOT_ALLOWED); omitido mantém o actual")
        BrokerPolicyholderRequest policyholder,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED,
                description = "Versão lida; diferente da gravada → 409 CONCURRENT_MODIFICATION")
        Long version) {
}
