package mz.co.moseguros.catalog.api;

import io.swagger.v3.oas.annotations.media.Schema;
import mz.co.moseguros.catalog.domain.PlateFormatEntity;

/** Formato de matrícula activo, para os ecrãs (sem o padrão). */
public record PublicPlateFormatResponse(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "STANDARD")
        String code,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "Actual (ABC-123-MC)")
        String label,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "ABC-123-MC")
        String example) {

    static PublicPlateFormatResponse from(PlateFormatEntity f) {
        return new PublicPlateFormatResponse(f.getCode(), f.getLabel(), f.getExample());
    }
}
