package mz.co.moseguros.organization.api;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import mz.co.moseguros.organization.RatingMode;

/**
 * Nova ligação à tarifação de uma seguradora (S3-02 §9).
 *
 * @param ratingEndpointUrl obrigatório em {@code EXTERNAL}; em {@code INTERNAL}, em falta mantém o gravado
 * @param ratingSecretRef   obrigatório em {@code EXTERNAL}; {@code config:<chave>}
 * @param version           a {@code version} lida (senão 409 {@code CONCURRENT_MODIFICATION})
 */
public record UpdateRatingIntegrationRequest(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        RatingMode ratingMode,
        @Schema(maxLength = 500, example = "https://cotacoes.seguradora.co.mz/moseguros")
        String ratingEndpointUrl,
        @Schema(maxLength = 200, example = "config:demox")
        String ratingSecretRef,
        @NotNull
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        Long version) {
}
