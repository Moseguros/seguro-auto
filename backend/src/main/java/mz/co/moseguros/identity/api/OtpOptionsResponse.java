package mz.co.moseguros.identity.api;

import java.util.List;
import java.util.Map;

import io.swagger.v3.oas.annotations.media.Schema;
import mz.co.moseguros.identity.application.OtpService;
import mz.co.moseguros.identity.domain.OtpChallenge.IdentifierType;
import mz.co.moseguros.shared.domain.Channel;

/** Opções do login por código. Um tipo sem canais activos não aparece. */
public record OtpOptionsResponse(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        List<IdentifierType> identifierTypes,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED,
                description = "Canais activos de cada tipo, pela ordem alternativa",
                example = "{\"MSISDN\": [\"SMS\", \"WHATSAPP\"], \"EMAIL\": [\"EMAIL\"]}")
        Map<IdentifierType, List<Channel>> channels,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "Canal por defeito de cada tipo",
                example = "{\"MSISDN\": \"SMS\", \"EMAIL\": \"EMAIL\"}")
        Map<IdentifierType, Channel> defaultChannels,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        boolean customerCanChoose,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "6")
        int codeLength,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "60")
        int resendAfterSeconds) {

    static OtpOptionsResponse from(OtpService.Options options) {
        return new OtpOptionsResponse(options.identifierTypes(), options.channels(), options.defaultChannels(),
                options.customerCanChoose(), options.codeLength(), options.resendAfterSeconds());
    }
}
