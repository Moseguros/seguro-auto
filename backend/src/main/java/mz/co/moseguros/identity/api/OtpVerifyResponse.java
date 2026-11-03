package mz.co.moseguros.identity.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import mz.co.moseguros.identity.application.OtpService;

/** Sessão aberta por um código válido. */
public record OtpVerifyResponse(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "JWT de acesso (Bearer), válido 15 minutos")
        String accessToken,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "Refresh token opaco")
        String refreshToken,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "900")
        long expiresIn,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "A conta foi criada agora")
        @JsonProperty("isNewUser")
        boolean isNewUser) {

    static OtpVerifyResponse from(OtpService.LoginResult result) {
        return new OtpVerifyResponse(result.tokens().accessToken(), result.tokens().refreshToken(),
                result.tokens().expiresIn(), result.newUser());
    }

    @Override
    public String toString() {
        return "OtpVerifyResponse[accessToken=***, refreshToken=***, expiresIn=" + expiresIn + ", isNewUser="
                + isNewUser + "]";
    }
}
