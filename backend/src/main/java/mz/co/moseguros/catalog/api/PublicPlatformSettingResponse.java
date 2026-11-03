package mz.co.moseguros.catalog.api;

import io.swagger.v3.oas.annotations.media.Schema;
import mz.co.moseguros.catalog.domain.PlatformSetting;
import mz.co.moseguros.catalog.domain.SettingValueType;

/**
 * Parâmetro da plataforma na lista pública.
 *
 * @param value em texto; {@code MONEY} em centavos
 */
public record PublicPlatformSettingResponse(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "MTPL_MIN_CAPITAL")
        String key,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        SettingValueType valueType,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "300000000")
        String value) {

    static PublicPlatformSettingResponse from(PlatformSetting s) {
        return new PublicPlatformSettingResponse(s.getKey(), s.getValueType(), s.getValue());
    }
}
