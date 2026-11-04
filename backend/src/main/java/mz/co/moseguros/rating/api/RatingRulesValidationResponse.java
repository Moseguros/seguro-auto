package mz.co.moseguros.rating.api;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import mz.co.moseguros.shared.error.Problems.FieldError;

/**
 * Resultado de {@code validateRatingRules}.
 *
 * @param errors todos os erros (vazia quando {@code valid})
 */
public record RatingRulesValidationResponse(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        boolean valid,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        List<RatingRuleError> errors) {

    static RatingRulesValidationResponse of(List<FieldError> errors) {
        return new RatingRulesValidationResponse(errors.isEmpty(),
                errors.stream().map(e -> new RatingRuleError(e.field(), e.code(), e.message())).toList());
    }

    /**
     * Erro da tarifa.
     *
     * @param field   ponteiro JSON dentro de {@code rating}
     * @param code    código de {@code FieldErrorCodes} (ex.: {@code VALUE_OUT_OF_RANGE})
     * @param message mensagem técnica em inglês (a do validador, em {@code SCHEMA_VIOLATION})
     */
    public record RatingRuleError(
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "/coverages/0/base/rows/3/value")
            String field,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "VALUE_OUT_OF_RANGE")
            String code,
            String message) {
    }
}
