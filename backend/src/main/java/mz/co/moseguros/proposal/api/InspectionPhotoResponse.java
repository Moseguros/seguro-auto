package mz.co.moseguros.proposal.api;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import mz.co.moseguros.proposal.domain.InspectionPhoto;
import mz.co.moseguros.proposal.domain.PhotoSlot;
import mz.co.moseguros.proposal.domain.PhotoSource;

/** Fotografia da vistoria (S4-03 §5 e §7); o ficheiro descarrega-se em {@code GET /api/v1/files/{fileId}}. */
public record InspectionPhotoResponse(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        UUID id,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        PhotoSlot slot,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        UUID fileId,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        PhotoSource source,
        @Schema(description = "Hora da fotografia indicada pelo cliente", example = "2027-01-06T08:12:00Z")
        Instant takenAt,
        @Schema(example = "-25.965312")
        BigDecimal latitude,
        @Schema(example = "32.583121")
        BigDecimal longitude,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "2027-01-06T08:15:03Z")
        Instant uploadedAt) {

    static InspectionPhotoResponse from(InspectionPhoto p) {
        return new InspectionPhotoResponse(p.getId(), p.getSlot(), p.getFileId(), p.getSource(), p.getTakenAt(),
                p.getLatitude(), p.getLongitude(), p.getUploadedAt());
    }
}
