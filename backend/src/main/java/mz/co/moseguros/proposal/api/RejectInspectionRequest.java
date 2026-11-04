package mz.co.moseguros.proposal.api;

import java.util.List;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import mz.co.moseguros.proposal.domain.PhotoSlot;

/** Recusa da vistoria ({@code rejectInspection}, S4-03 §6). */
public record RejectInspectionRequest(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, minLength = 10, maxLength = 1000,
                example = "A fotografia do chassis está ilegível.")
        String reason,
        @ArraySchema(schema = @Schema(implementation = PhotoSlot.class),
                arraySchema = @Schema(description = "Ângulos a repetir, sem repetidos (INVALID_SLOT, DUPLICATED)"))
        List<String> retakeSlots,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED,
                description = "Versão da vistoria; diferente da gravada → 409 CONCURRENT_MODIFICATION")
        Long version) {
}
