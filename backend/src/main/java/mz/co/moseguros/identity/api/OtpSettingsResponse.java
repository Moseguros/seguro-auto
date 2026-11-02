package mz.co.moseguros.identity.api;

import java.time.Instant;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import mz.co.moseguros.identity.application.OtpSettingsService;
import mz.co.moseguros.shared.domain.Channel;

/** Definições do OTP do cliente. */
public record OtpSettingsResponse(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        List<Channel> enabledChannels,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        Channel defaultChannel,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        boolean customerCanChoose,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "Ordem da alternativa automática")
        List<Channel> fallbackOrder,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "6")
        int codeLength,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "300")
        int validitySeconds,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "3")
        int maxAttempts,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "60")
        int resendAfterSeconds,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "5")
        int maxRequestsPerHour,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "Enviar no PUT seguinte")
        long version,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        Instant updatedAt,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "id do utilizador ou 'system'")
        String updatedBy) {

    static OtpSettingsResponse from(OtpSettingsService.Snapshot s) {
        return new OtpSettingsResponse(s.enabledChannels(), s.defaultChannel(), s.customerCanChoose(),
                s.fallbackOrder(), s.codeLength(), s.validitySeconds(), s.maxAttempts(), s.resendAfterSeconds(),
                s.maxRequestsPerHour(), s.version(), s.updatedAt(), s.updatedBy());
    }
}
