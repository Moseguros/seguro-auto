package mz.co.moseguros.rating.api;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

/** Tarifa a validar contra as coberturas de uma versão, sem gravar. */
public record ValidateRatingRulesRequest(
        @NotNull
        @Schema(type = "object", requiredMode = Schema.RequiredMode.REQUIRED,
                description = "Tarifa no formato rating-v1 (schemas/rating-v1.json)")
        JsonNode rating) {
}
