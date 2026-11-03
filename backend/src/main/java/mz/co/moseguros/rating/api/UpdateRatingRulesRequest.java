package mz.co.moseguros.rating.api;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

/**
 * Nova tarifa de uma versão {@code DRAFT}.
 *
 * @param rating  tarifa no formato {@code rating-v1} (validada pelo schema e pelas regras do S3-01 §7)
 * @param version a {@code version} lida da versão (senão 409 {@code CONCURRENT_MODIFICATION})
 */
public record UpdateRatingRulesRequest(
        @NotNull
        @Schema(type = "object", requiredMode = Schema.RequiredMode.REQUIRED,
                description = "Tarifa no formato rating-v1 (schemas/rating-v1.json)")
        JsonNode rating,
        @NotNull
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        Long version) {
}
