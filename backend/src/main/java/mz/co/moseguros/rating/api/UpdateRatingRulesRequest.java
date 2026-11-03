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
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, ref = RatingRulesSchema.REF)
        JsonNode rating,
        @NotNull
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        Long version) {
}
