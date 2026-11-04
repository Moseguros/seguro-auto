package mz.co.moseguros.proposal.api;

import java.time.Instant;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import mz.co.moseguros.catalog.ProductType;
import mz.co.moseguros.identity.KycState;
import mz.co.moseguros.proposal.AcceptanceMode;
import mz.co.moseguros.proposal.ProposalStatus;
import mz.co.moseguros.proposal.application.DecisionService.Row;
import mz.co.moseguros.proposal.application.ProposalJson;
import mz.co.moseguros.proposal.domain.InspectionStatus;
import mz.co.moseguros.proposal.domain.Proposal;
import mz.co.moseguros.proposal.domain.ProposalChannel;
import mz.co.moseguros.proposal.domain.SlaStatus;
import mz.co.moseguros.shared.domain.Money;

/** Proposta na fila da seguradora ({@code listInsurerProposals}, S4-05 §7). */
public record InsurerProposalSummaryResponse(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        UUID id,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "PR-2027-000123")
        String number,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        ProposalStatus status,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "Ana Maria Mussa")
        String customerName,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "Auto Base")
        String productName,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        ProductType productType,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "ABC-123-MC")
        String vehiclePlate,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        Money total,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        ProposalChannel channel,
        @JsonInclude(JsonInclude.Include.ALWAYS)
        @Schema(nullable = true, example = "Corretora Demo")
        String brokerName,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        Instant submittedAt,
        @JsonInclude(JsonInclude.Include.ALWAYS)
        @Schema(nullable = true, description = "Fim do prazo de decisão; só em UNDER_REVIEW")
        Instant slaDueAt,
        @JsonInclude(JsonInclude.Include.ALWAYS)
        @Schema(nullable = true, description = "Só em UNDER_REVIEW")
        SlaStatus slaStatus,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        KycState kycState,
        @JsonInclude(JsonInclude.Include.ALWAYS)
        @Schema(nullable = true, description = "null sem vistoria (sem danos próprios)")
        InspectionStatus inspectionStatus,
        @JsonInclude(JsonInclude.Include.ALWAYS)
        @Schema(nullable = true)
        AcceptanceMode acceptanceMode) {

    static InsurerProposalSummaryResponse from(Row row, ProposalJson json) {
        Proposal p = row.proposal();
        return new InsurerProposalSummaryResponse(p.getId(), p.getNumber(), p.getStatus(),
                json.policyholder(p.getPolicyholder()).fullName(), p.getProductName(), p.getProductType(),
                p.getVehicleSnapshot().path("plate").asText(null), Money.ofCentavos(p.getTotalAmount()),
                p.getChannel(), p.getBrokerName(), p.getSubmittedAt(), p.getSlaDueAt(), row.slaStatus(),
                row.kycState(), row.inspectionStatus(), p.getAcceptanceMode());
    }
}
