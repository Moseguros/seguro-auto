package mz.co.moseguros.identity.api;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/** Pedido de renovação da sessão. */
public record RefreshTokenRequest(
        @NotBlank
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED,
                description = "Refresh token recebido no login ou na última renovação")
        String refreshToken) {

    @Override
    public String toString() {
        return "RefreshTokenRequest[refreshToken=***]";
    }
}
