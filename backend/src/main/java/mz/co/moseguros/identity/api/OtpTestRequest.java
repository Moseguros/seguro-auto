package mz.co.moseguros.identity.api;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import mz.co.moseguros.identity.domain.OtpChallenge.IdentifierType;
import mz.co.moseguros.shared.domain.Channel;

/** Envio de um código de teste pelo administrador. */
public record OtpTestRequest(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "841234567")
        @NotBlank
        String identifier,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "MSISDN")
        @NotNull
        IdentifierType identifierType,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "WHATSAPP")
        @NotNull
        Channel channel) {

    @Override
    public String toString() {
        return "OtpTestRequest[identifier=***, identifierType=" + identifierType + ", channel=" + channel + "]";
    }
}
