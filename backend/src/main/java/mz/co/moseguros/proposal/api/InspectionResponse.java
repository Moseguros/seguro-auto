package mz.co.moseguros.proposal.api;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import mz.co.moseguros.proposal.application.InspectionDetails;
import mz.co.moseguros.proposal.domain.Inspection;
import mz.co.moseguros.proposal.domain.InspectionMode;
import mz.co.moseguros.proposal.domain.InspectionStatus;
import mz.co.moseguros.proposal.domain.PhotoSlot;

/**
 * Vistoria (S4-03 §7): o bloco {@code inspection} do {@code ProposalResponse} e a resposta da
 * seguradora. Sem vistoria exigida só tem {@code required = false}; os campos sem valor não saem.
 * {@code reviewed_by} nunca sai.
 */
public record InspectionResponse(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "Alguma cobertura OD_*")
        boolean required,
        @Schema(description = "Só com required = true")
        UUID id,
        InspectionMode mode,
        InspectionStatus status,
        @Schema(example = "84500")
        Integer odometerKm,
        @Schema(description = "Pedido presencial: local e horas preferidas")
        String customerNotes,
        Instant submittedAt,
        @Schema(description = "Ausente até haver revisão")
        InspectionReview review,
        @Schema(description = "Activas, pela ordem dos ângulos e depois do envio")
        List<InspectionPhotoResponse> photos,
        @Schema(description = "Ângulos obrigatórios sem fotografia ([] em ON_SITE)")
        List<PhotoSlot> missingSlots,
        @Schema(example = "6")
        Integer maxExtraPhotos,
        @Schema(description = "Versão da vistoria, para approveInspection e rejectInspection")
        Long version) {

    /** Revisão do subscritor. */
    public record InspectionReview(
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            Instant reviewedAt,
            @Schema(description = "Notas da aprovação")
            String notes,
            @Schema(description = "Só em REJECTED")
            String rejectionReason,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "Ângulos a repetir pedidos na recusa")
            List<PhotoSlot> retakeSlots) {
    }

    static InspectionResponse notRequired() {
        return new InspectionResponse(false, null, null, null, null, null, null, null, null, null, null, null);
    }

    static InspectionResponse from(InspectionDetails details) {
        if (details == null) {
            return notRequired();
        }
        Inspection i = details.inspection();
        InspectionReview review = i.getReviewedAt() == null ? null
                : new InspectionReview(i.getReviewedAt(), i.getReviewNotes(), i.getRejectionReason(),
                        i.getRetakeSlots());
        return new InspectionResponse(true, i.getId(), i.getMode(), i.getStatus(), i.getOdometerKm(),
                i.getCustomerNotes(), i.getSubmittedAt(), review,
                details.photos().stream().map(InspectionPhotoResponse::from).toList(), details.missingSlots(),
                details.maxExtraPhotos(), i.getVersion());
    }
}
