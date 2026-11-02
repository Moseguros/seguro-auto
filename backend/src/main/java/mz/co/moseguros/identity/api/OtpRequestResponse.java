package mz.co.moseguros.identity.api;

import java.time.Instant;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import mz.co.moseguros.identity.application.OtpService;
import mz.co.moseguros.shared.domain.Channel;

/** Código enviado. */
public record OtpRequestResponse(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED,
                description = "Usar na validação e na consulta do estado (não muda com a alternativa de canal)")
        UUID challengeId,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "Canal efectivamente usado")
        Channel channel,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "60")
        int resendAfterSeconds,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        Instant expiresAt,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "O canal mudou por falha do primeiro")
        boolean fallbackUsed) {

    static OtpRequestResponse from(OtpService.RequestResult result) {
        return new OtpRequestResponse(result.challengeId(), result.channel(), result.resendAfterSeconds(),
                result.expiresAt(), result.fallbackUsed());
    }
}
