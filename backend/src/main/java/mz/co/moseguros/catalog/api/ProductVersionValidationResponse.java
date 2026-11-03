package mz.co.moseguros.catalog.api;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import mz.co.moseguros.shared.error.Problems.FieldError;

/**
 * Resultado de {@code validateProductVersion}: as mesmas regras da publicação, sem gravar.
 *
 * @param errors todas as falhas (vazia quando {@code publishable})
 */
public record ProductVersionValidationResponse(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        boolean publishable,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        List<RuleError> errors) {

    static ProductVersionValidationResponse of(List<FieldError> errors) {
        return new ProductVersionValidationResponse(errors.isEmpty(),
                errors.stream().map(e -> new RuleError(e.field(), e.code())).toList());
    }

    /** Regra que falhou: {@code field} (ex.: {@code coverages[0].capital}) e {@code code} (ex.: {@code MTPL_REQUIRED}). */
    public record RuleError(
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "coverages")
            String field,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "MTPL_REQUIRED")
            String code) {
    }
}
