package mz.co.moseguros.organization.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import mz.co.moseguros.organization.RatingMode;
import mz.co.moseguros.organization.application.RatingIntegrationService.RatingIntegration;

/**
 * Ligação à tarifação de uma seguradora (S3-02 §9).
 *
 * @param ratingEndpointUrl serviço externo ({@code null} se nunca foi configurado)
 * @param ratingSecretRef   referência do segredo do HMAC ({@code config:<chave>}), nunca o segredo
 * @param version           bloqueio optimista do perfil da seguradora
 */
public record RatingIntegrationResponse(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        RatingMode ratingMode,
        @JsonInclude(JsonInclude.Include.ALWAYS)
        @Schema(nullable = true, example = "https://cotacoes.seguradora.co.mz/moseguros")
        String ratingEndpointUrl,
        @JsonInclude(JsonInclude.Include.ALWAYS)
        @Schema(nullable = true, example = "config:demox")
        String ratingSecretRef,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        long version) {

    static RatingIntegrationResponse from(RatingIntegration i) {
        return new RatingIntegrationResponse(i.ratingMode(), i.ratingEndpointUrl(), i.ratingSecretRef(), i.version());
    }
}
