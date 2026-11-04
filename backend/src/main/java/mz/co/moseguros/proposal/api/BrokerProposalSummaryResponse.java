package mz.co.moseguros.proposal.api;

import java.time.Instant;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import mz.co.moseguros.proposal.ProposalStatus;
import mz.co.moseguros.proposal.api.ProposalResponse.ProposalChecklist;
import mz.co.moseguros.proposal.api.ProposalResponse.ProposalOrganization;
import mz.co.moseguros.proposal.application.BrokerProposalService.Row;
import mz.co.moseguros.proposal.application.ProposalJson;
import mz.co.moseguros.proposal.domain.PolicyholderSnapshot;
import mz.co.moseguros.proposal.domain.Proposal;
import mz.co.moseguros.shared.domain.Money;

/** Proposta na lista do mediador ({@code listBrokerProposals}, S4-06 §5). */
public record BrokerProposalSummaryResponse(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        UUID id,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "PR-2027-000123")
        String number,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        ProposalStatus status,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "Nome do tomador",
                example = "João Carlos Sitoe")
        String customerName,
        @Schema(example = "+258*****4567")
        String customerMsisdnMasked,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        ProposalOrganization insurer,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "Auto Base")
        String productName,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        Money total,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        Instant createdAt,
        @JsonInclude(JsonInclude.Include.ALWAYS)
        @Schema(nullable = true)
        Instant expiresAt,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        ProposalChecklist checklist) {

    static BrokerProposalSummaryResponse from(Row row, ProposalJson json) {
        Proposal p = row.proposal();
        PolicyholderSnapshot holder = json.policyholder(p.getPolicyholder());
        return new BrokerProposalSummaryResponse(p.getId(), p.getNumber(), p.getStatus(), holder.fullName(),
                holder.msisdnMasked(), new ProposalOrganization(p.getInsurerId(), p.getInsurerName()),
                p.getProductName(), Money.ofCentavos(p.getTotalAmount()), p.getCreatedAt(), p.getExpiresAt(),
                ProposalResponse.checklist(p, row.inspectionStatus(), p.getCurrentSignature() != null));
    }
}
