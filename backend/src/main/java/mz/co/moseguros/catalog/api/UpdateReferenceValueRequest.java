package mz.co.moseguros.catalog.api;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import mz.co.moseguros.catalog.application.ReferenceValueService.Data;

/**
 * Alteração de um valor de referência: o código e a marca não mudam.
 *
 * @param sortOrder 0–9999; omitido = 100
 */
public record UpdateReferenceValueRequest(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "Hilux")
        @NotBlank @Size(max = 120)
        String labelPt,
        @Size(max = 120)
        String labelEn,
        @Schema(example = "100")
        Integer sortOrder,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED,
                description = "Versão lida; diferente da gravada → 409 CONCURRENT_MODIFICATION")
        @NotNull
        Long version) {

    Data toData() {
        return new Data(null, labelPt, labelEn, null, sortOrder);
    }
}
