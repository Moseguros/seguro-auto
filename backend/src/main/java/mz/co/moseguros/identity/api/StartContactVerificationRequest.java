package mz.co.moseguros.identity.api;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import mz.co.moseguros.identity.domain.OtpChallenge.IdentifierType;
import mz.co.moseguros.shared.domain.Channel;

/** Contacto novo (ou o mesmo, para reenviar o código). */
public record StartContactVerificationRequest(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "EMAIL")
        @NotNull
        IdentifierType type,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED,
                description = "Telemóvel (841234567, +258841234567) ou e-mail", example = "ana@exemplo.co.mz")
        @NotBlank
        String value,
        @Schema(description = "Canal escolhido pelo cliente (só se customerCanChoose); sem ele usa-se o canal "
                + "por defeito")
        Channel channel) {

    @Override
    public String toString() {
        return "StartContactVerificationRequest[type=" + type + ", value=***, channel=" + channel + "]";
    }
}
