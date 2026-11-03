package mz.co.moseguros.catalog.api;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

/** Todos os parâmetros da plataforma (públicos por natureza). */
public record PublicPlatformSettingsResponse(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        List<PublicPlatformSettingResponse> settings) {
}
