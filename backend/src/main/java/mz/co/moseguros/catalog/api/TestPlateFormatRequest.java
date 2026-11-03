package mz.co.moseguros.catalog.api;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Experiência de um padrão e de um modelo com um valor (não grava). */
public record TestPlateFormatRequest(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "^(?<l>[A-Z]{2})(?<n>\\d{4})$")
        @NotBlank
        String pattern,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "{l}-{n}")
        @NotBlank @Size(max = 100)
        String displayTemplate,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "ab 1234",
                description = "Valor como o utilizador o escreveria")
        @NotNull @Size(max = 100)
        String value) {
}
