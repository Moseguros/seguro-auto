package mz.co.moseguros.proposal.api;

import java.time.Instant;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import mz.co.moseguros.catalog.ProductType;
import mz.co.moseguros.proposal.ProposalStatus;
import mz.co.moseguros.proposal.api.ProposalResponse.ProposalOrganization;
import mz.co.moseguros.proposal.domain.Proposal;
import mz.co.moseguros.shared.domain.Money;

/** Proposta na lista do cliente ({@code listMyProposals}, S4-02 §9). */
public record ProposalSummaryResponse(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        UUID id,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "PR-2027-000123")
        String number,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        ProposalStatus status,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        ProposalOrganization insurer,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "Auto Base")
        String productName,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        ProductType productType,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "ABC-123-MC")
        String vehiclePlate,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        Money total,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        Instant startAt,
        @JsonInclude(JsonInclude.Include.ALWAYS)
        @Schema(nullable = true)
        Instant expiresAt,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        Instant createdAt) {

    static ProposalSummaryResponse from(Proposal p) {
        return new ProposalSummaryResponse(p.getId(), p.getNumber(), p.getStatus(),
                new ProposalOrganization(p.getInsurerId(), p.getInsurerName()), p.getProductName(),
                p.getProductType(), p.getVehicleSnapshot().path("plate").asText(null),
                Money.ofCentavos(p.getTotalAmount()), p.getStartAt(), p.getExpiresAt(), p.getCreatedAt());
    }
}
