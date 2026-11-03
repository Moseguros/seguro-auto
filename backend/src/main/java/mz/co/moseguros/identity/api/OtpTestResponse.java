package mz.co.moseguros.identity.api;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import mz.co.moseguros.identity.application.OtpService;
import mz.co.moseguros.identity.domain.OtpChallenge.DeliveryStatus;
import mz.co.moseguros.shared.domain.Channel;

/** Resultado de um envio de teste. */
public record OtpTestResponse(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        Channel channel,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "SENT ou FAILED")
        DeliveryStatus deliveryStatus,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "id da mensagem enviada")
        UUID messageId) {

    static OtpTestResponse from(OtpService.TestResult result) {
        return new OtpTestResponse(result.channel(), result.deliveryStatus(), result.messageId());
    }
}
