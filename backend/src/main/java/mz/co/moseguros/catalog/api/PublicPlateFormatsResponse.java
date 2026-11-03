package mz.co.moseguros.catalog.api;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

/** Formatos de matrícula activos, pela ordem de teste. */
public record PublicPlateFormatsResponse(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        List<PublicPlateFormatResponse> formats) {
}
