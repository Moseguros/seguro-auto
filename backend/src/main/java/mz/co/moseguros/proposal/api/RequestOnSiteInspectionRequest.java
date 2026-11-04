package mz.co.moseguros.proposal.api;

import io.swagger.v3.oas.annotations.media.Schema;

/** Pedido de vistoria presencial ({@code requestOnSiteInspection}, S4-03 §5). */
public record RequestOnSiteInspectionRequest(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, minLength = 10, maxLength = 500,
                description = "Local e horas preferidas (REQUIRED, TOO_SHORT, TOO_LONG)",
                example = "Matola, Av. das Indústrias, dias úteis de manhã")
        String notes,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED,
                description = "Versão da proposta; diferente da gravada → 409 CONCURRENT_MODIFICATION")
        Long version) {
}
