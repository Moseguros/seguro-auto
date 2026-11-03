package mz.co.moseguros.catalog.api;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import mz.co.moseguros.catalog.application.PlateFormatService.Data;

/** Alteração de um formato de matrícula: os campos da criação excepto {@code code}, mais {@code version}. */
public record UpdatePlateFormatRequest(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "Actual (ABC-123-MC)")
        @NotBlank
        String label,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "^(?<l>[A-Z]{3})(?<n>\\d{3})(?<p>[A-Z]{2})$")
        @NotBlank
        String pattern,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "{l}-{n}-{p}")
        @NotBlank @Size(max = 100)
        String displayTemplate,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "ABC-123-MC")
        @NotBlank @Size(max = 20)
        String example,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "10")
        @NotNull
        Integer sortOrder,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED,
                description = "Versão lida; diferente da gravada → 409 CONCURRENT_MODIFICATION")
        @NotNull
        Long version) {

    Data toData() {
        return new Data(null, label, pattern, displayTemplate, example, sortOrder);
    }
}
