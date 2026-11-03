package mz.co.moseguros.catalog.api;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

/** Todos os parâmetros da plataforma, para o administrador. */
public record PlatformSettingsResponse(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        List<PlatformSettingResponse> settings) {
}
