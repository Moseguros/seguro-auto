package mz.co.moseguros.organization.api;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

/**
 * Novos encargos da seguradora. {@code charges} é validado por {@code schemas/charges-v1.json} e
 * pelas regras do S3-01 §4 (erros com {@code field} = ponteiro JSON dentro de {@code charges}).
 *
 * @param version a {@code version} lida (senão 409 {@code CONCURRENT_MODIFICATION})
 */
public record UpdateInsurerRatingSettingsRequest(
        @NotNull
        @ArraySchema(schema = @Schema(implementation = ChargeDto.class), maxItems = 10,
                arraySchema = @Schema(requiredMode = Schema.RequiredMode.REQUIRED))
        JsonNode charges,
        @NotNull
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        Long version) {
}
