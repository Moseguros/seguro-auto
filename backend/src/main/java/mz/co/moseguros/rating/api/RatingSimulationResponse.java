package mz.co.moseguros.rating.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import mz.co.moseguros.rating.QuoteStatus;
import mz.co.moseguros.rating.UnavailableReason;
import mz.co.moseguros.rating.domain.tariff.EvaluationResult;
import mz.co.moseguros.rating.domain.tariff.EvaluationResult.Offered;
import mz.co.moseguros.rating.domain.tariff.EvaluationResult.Unavailable;

/**
 * Resultado do simulador (S3-02 §12): {@code OFFERED} com a decomposição completa (bases,
 * factores, encargos e arredondamento) ou {@code NOT_AVAILABLE} com o motivo.
 */
@JsonInclude(JsonInclude.Include.ALWAYS)
public record RatingSimulationResponse(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        QuoteStatus status,
        @Schema(nullable = true)
        UnavailableReason unavailableReason,
        @Schema(ref = QuoteSchemas.UNAVAILABLE_DETAIL_REF, nullable = true)
        JsonNode unavailableDetail,
        @Schema(ref = QuoteSchemas.PREMIUM_BREAKDOWN_REF, nullable = true)
        JsonNode breakdown) {

    static RatingSimulationResponse from(EvaluationResult result, ObjectMapper mapper) {
        if (result instanceof Unavailable u) {
            return new RatingSimulationResponse(QuoteStatus.NOT_AVAILABLE, u.reason(), mapper.valueToTree(u.detail()),
                    null);
        }
        return new RatingSimulationResponse(QuoteStatus.OFFERED, null, null, ((Offered) result).breakdown().toJson());
    }
}
