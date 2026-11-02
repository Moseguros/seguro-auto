package mz.co.moseguros.identity.api;

import java.time.Instant;

import io.swagger.v3.oas.annotations.media.Schema;
import mz.co.moseguros.identity.application.OtpService;
import mz.co.moseguros.identity.domain.OtpChallenge.DeliveryStatus;
import mz.co.moseguros.shared.domain.Channel;

/** Estado do código activo de um pedido. */
public record OtpChallengeResponse(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        Channel channel,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        DeliveryStatus deliveryStatus,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        boolean fallbackUsed,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        Instant expiresAt) {

    static OtpChallengeResponse from(OtpService.ChallengeView view) {
        return new OtpChallengeResponse(view.channel(), view.deliveryStatus(), view.fallbackUsed(), view.expiresAt());
    }
}
