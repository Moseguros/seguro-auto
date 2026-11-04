package mz.co.moseguros.proposal.api;

import io.swagger.v3.oas.annotations.media.Schema;
import mz.co.moseguros.proposal.domain.InspectionMode;

/** Aprovação da vistoria ({@code approveInspection}, S4-03 §6). */
public record ApproveInspectionRequest(
        @Schema(description = "ON_SITE regista uma vistoria presencial (notes obrigatório); SELF_PHOTOS exige os seis "
                + "ângulos; sem ele vale o modo da vistoria")
        InspectionMode mode,
        @Schema(maxLength = 1000, example = "Vistoria feita na Matola em 08/01.")
        String notes,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED,
                description = "Versão da vistoria; diferente da gravada → 409 CONCURRENT_MODIFICATION")
        Long version) {
}
