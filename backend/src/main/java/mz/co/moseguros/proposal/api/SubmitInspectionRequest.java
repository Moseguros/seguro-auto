package mz.co.moseguros.proposal.api;

import io.swagger.v3.oas.annotations.media.Schema;

/** Submissão da vistoria ({@code submitInspection}, S4-03 §5). */
public record SubmitInspectionRequest(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, minimum = "0", maximum = "2000000",
                description = "Quilometragem; fora de 0–2 000 000 → OUT_OF_RANGE", example = "84500")
        Integer odometerKm,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED,
                description = "Versão da proposta; diferente da gravada → 409 CONCURRENT_MODIFICATION")
        Long version) {
}
