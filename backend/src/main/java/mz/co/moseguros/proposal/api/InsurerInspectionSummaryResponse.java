package mz.co.moseguros.proposal.api;

import java.time.Instant;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import mz.co.moseguros.proposal.application.InspectionReviewService.InspectionSummary;
import mz.co.moseguros.proposal.domain.Inspection;
import mz.co.moseguros.proposal.domain.InspectionMode;
import mz.co.moseguros.proposal.domain.InspectionStatus;

/** Linha da fila de vistorias da seguradora ({@code listInsurerInspections}, S4-03 §6). */
public record InsurerInspectionSummaryResponse(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        UUID id,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        UUID proposalId,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "PR-2027-000123")
        String proposalNumber,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "Ana Maria Mussa")
        String customerName,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "ABC-123-MC")
        String vehiclePlate,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        InspectionMode mode,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        InspectionStatus status,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "Fotografias activas")
        int photoCount,
        Instant submittedAt) {

    static InsurerInspectionSummaryResponse from(InspectionSummary s) {
        Inspection i = s.inspection();
        return new InsurerInspectionSummaryResponse(i.getId(), i.getProposalId(), s.proposalNumber(),
                s.customerName(), s.vehiclePlate(), i.getMode(), i.getStatus(), s.photoCount(), i.getSubmittedAt());
    }
}
