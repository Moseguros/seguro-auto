package mz.co.moseguros.catalog.api;

import java.time.Instant;

import io.swagger.v3.oas.annotations.media.Schema;
import mz.co.moseguros.catalog.domain.PlatformSetting;
import mz.co.moseguros.catalog.domain.SettingValueType;

/**
 * Parâmetro da plataforma para o administrador.
 *
 * @param value     em texto; {@code MONEY} em centavos
 * @param updatedBy id de quem alterou por último, ou {@code system}
 */
public record PlatformSettingResponse(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "MTPL_MIN_CAPITAL")
        String key,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        SettingValueType valueType,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "300000000")
        String value,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        long version,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        Instant updatedAt,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "system")
        String updatedBy) {

    static PlatformSettingResponse from(PlatformSetting s) {
        return new PlatformSettingResponse(s.getKey(), s.getValueType(), s.getValue(), s.getVersion(),
                s.getUpdatedAt(), s.getUpdatedBy());
    }
}
