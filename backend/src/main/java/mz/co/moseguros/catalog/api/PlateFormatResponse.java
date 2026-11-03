package mz.co.moseguros.catalog.api;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import mz.co.moseguros.catalog.domain.PlateFormatEntity;

/** Formato de matrícula, para o administrador. */
public record PlateFormatResponse(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        UUID id,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "STANDARD")
        String code,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "Actual (ABC-123-MC)")
        String label,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "^(?<l>[A-Z]{3})(?<n>\\d{3})(?<p>[A-Z]{2})$")
        String pattern,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "{l}-{n}-{p}")
        String displayTemplate,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "ABC-123-MC")
        String example,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "10")
        int sortOrder,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        boolean active,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        long version) {

    static PlateFormatResponse from(PlateFormatEntity f) {
        return new PlateFormatResponse(f.getId(), f.getCode(), f.getLabel(), f.getPattern(), f.getDisplayTemplate(),
                f.getExample(), f.getSortOrder(), f.isActive(), f.getVersion());
    }
}
