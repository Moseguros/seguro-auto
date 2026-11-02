package mz.co.moseguros.identity.api;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import mz.co.moseguros.identity.application.OtpSettingsService;
import mz.co.moseguros.shared.domain.Channel;

/**
 * Novas definições do OTP (todos os campos obrigatórios). As regras entre campos (canais vazios
 * ou repetidos, canal por defeito, ordem alternativa, limites) dão 400 {@code VALIDATION_ERROR}
 * com o código do campo.
 */
public record UpdateOtpSettingsRequest(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "[\"SMS\", \"WHATSAPP\", \"EMAIL\"]")
        List<Channel> enabledChannels,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "SMS")
        Channel defaultChannel,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull
        Boolean customerCanChoose,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "[\"SMS\", \"WHATSAPP\", \"EMAIL\"]")
        List<Channel> fallbackOrder,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "4 a 8", example = "6")
        @NotNull
        Integer codeLength,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "60 a 1800", example = "300")
        @NotNull
        Integer validitySeconds,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "1 a 10", example = "3")
        @NotNull
        Integer maxAttempts,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "15 a 600", example = "60")
        @NotNull
        Integer resendAfterSeconds,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "1 a 50", example = "5")
        @NotNull
        Integer maxRequestsPerHour,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "version lida no GET")
        @NotNull
        Long version) {

    OtpSettingsService.Update toUpdate() {
        return new OtpSettingsService.Update(enabledChannels, defaultChannel, customerCanChoose, fallbackOrder,
                codeLength, validitySeconds, maxAttempts, resendAfterSeconds, maxRequestsPerHour, version);
    }
}
