package mz.co.moseguros.identity.api;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import mz.co.moseguros.identity.domain.OtpChallenge.IdentifierType;
import mz.co.moseguros.shared.domain.Channel;

/** Pedido de um código de login. */
public record RequestOtpRequest(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED,
                description = "Telemóvel (841234567, +258841234567) ou e-mail", example = "841234567")
        @NotBlank
        String identifier,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "MSISDN")
        @NotNull
        IdentifierType identifierType,
        @Schema(description = "Canal escolhido pelo cliente (só se customerCanChoose); sem ele usa-se o canal "
                + "por defeito")
        Channel channel) {

    @Override
    public String toString() {
        return "RequestOtpRequest[identifier=***, identifierType=" + identifierType + ", channel=" + channel + "]";
    }
}
