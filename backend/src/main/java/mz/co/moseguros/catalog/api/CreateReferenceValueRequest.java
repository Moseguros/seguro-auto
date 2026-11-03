package mz.co.moseguros.catalog.api;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import mz.co.moseguros.catalog.application.ReferenceValueService.Data;

/**
 * Novo valor de referência.
 *
 * @param parentCode obrigatório em {@code VEHICLE_MODEL} (código de uma marca) e proibido nos outros domínios
 * @param sortOrder  0–9999; omitido = 100
 */
public record CreateReferenceValueRequest(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "TOYOTA_HILUX",
                description = "^[A-Z0-9_]{1,40}$; não muda depois de criado")
        @NotBlank
        String code,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "Hilux")
        @NotBlank @Size(max = 120)
        String labelPt,
        @Size(max = 120)
        String labelEn,
        @Schema(example = "TOYOTA")
        String parentCode,
        @Schema(example = "100")
        Integer sortOrder) {

    Data toData() {
        return new Data(code, labelPt, labelEn, parentCode, sortOrder);
    }
}
