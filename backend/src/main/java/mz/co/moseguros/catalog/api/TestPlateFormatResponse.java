package mz.co.moseguros.catalog.api;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Resultado de {@code testPlateFormat}.
 *
 * @param formatted valor formatado; ausente se não corresponder
 */
public record TestPlateFormatResponse(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        boolean matches,
        @Schema(example = "AB-1234")
        String formatted) {
}
