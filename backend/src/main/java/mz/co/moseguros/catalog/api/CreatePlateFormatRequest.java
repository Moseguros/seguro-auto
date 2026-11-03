package mz.co.moseguros.catalog.api;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import mz.co.moseguros.catalog.application.PlateFormatService.Data;

/**
 * Novo formato de matrícula. As regras de cada campo estão na ficha S2-02 §4
 * ({@code INVALID_CODE}, {@code OUT_OF_RANGE}, {@code PATTERN_NOT_ANCHORED}, {@code INVALID_PATTERN},
 * {@code PATTERN_WITHOUT_GROUPS}, {@code TEMPLATE_GROUP_UNKNOWN}, {@code EXAMPLE_DOES_NOT_MATCH}).
 */
public record CreatePlateFormatRequest(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "STANDARD",
                description = "^[A-Z0-9_]{2,20}$; não muda depois de criado")
        @NotBlank
        String code,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "Actual (ABC-123-MC)", description = "2–80 caracteres")
        @NotBlank
        String label,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "^(?<l>[A-Z]{3})(?<n>\\d{3})(?<p>[A-Z]{2})$",
                description = "Expressão regular Java (≤ 200), com ^ e $ e pelo menos um grupo nomeado; aplica-se ao "
                        + "valor normalizado (maiúsculas, sem espaços nem hífens)")
        @NotBlank
        String pattern,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "{l}-{n}-{p}",
                description = "Grupos do padrão como {nome}, letras, dígitos, espaço e -")
        @NotBlank @Size(max = 100)
        String displayTemplate,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "ABC-123-MC",
                description = "Valor já formatado que o formato aceita")
        @NotBlank @Size(max = 20)
        String example,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "10", description = "0–999: ordem de teste")
        @NotNull
        Integer sortOrder) {

    Data toData() {
        return new Data(code, label, pattern, displayTemplate, example, sortOrder);
    }
}
