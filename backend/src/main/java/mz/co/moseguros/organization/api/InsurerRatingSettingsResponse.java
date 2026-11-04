package mz.co.moseguros.organization.api;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import mz.co.moseguros.organization.RatingMode;
import mz.co.moseguros.organization.application.InsurerRatingSettingsService.RatingSettings;

/**
 * Definições de tarifação da seguradora (S3-01 §4).
 *
 * @param charges encargos pela ordem em que se calculam (todos sobre o prémio comercial)
 * @param version bloqueio optimista do perfil da seguradora
 */
public record InsurerRatingSettingsResponse(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        RatingMode ratingMode,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        List<ChargeDto> charges,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        long version) {

    static InsurerRatingSettingsResponse from(RatingSettings settings) {
        return new InsurerRatingSettingsResponse(settings.ratingMode(),
                settings.charges().stream().map(ChargeDto::from).toList(), settings.version());
    }
}
