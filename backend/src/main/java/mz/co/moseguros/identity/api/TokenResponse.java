package mz.co.moseguros.identity.api;

import io.swagger.v3.oas.annotations.media.Schema;
import mz.co.moseguros.identity.TokenPair;

/** Par de tokens de uma sessão. */
public record TokenResponse(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED,
                description = "JWT de acesso (Bearer), válido 15 minutos")
        String accessToken,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED,
                description = "Refresh token opaco; cada renovação devolve um novo")
        String refreshToken,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED,
                description = "Validade do token de acesso, em segundos", example = "900")
        long expiresIn) {

    static TokenResponse from(TokenPair pair) {
        return new TokenResponse(pair.accessToken(), pair.refreshToken(), pair.expiresIn());
    }

    @Override
    public String toString() {
        return "TokenResponse[accessToken=***, refreshToken=***, expiresIn=" + expiresIn + "]";
    }
}
