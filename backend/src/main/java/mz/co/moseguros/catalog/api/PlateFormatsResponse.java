package mz.co.moseguros.catalog.api;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

/** Todos os formatos de matrícula, por {@code sortOrder}. */
public record PlateFormatsResponse(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        List<PlateFormatResponse> formats) {
}
