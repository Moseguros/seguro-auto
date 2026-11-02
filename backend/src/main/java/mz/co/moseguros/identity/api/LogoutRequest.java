package mz.co.moseguros.identity.api;

import io.swagger.v3.oas.annotations.media.Schema;

/** Pedido de fim de sessão. Um token desconhecido ou vazio não é erro. */
public record LogoutRequest(
        @Schema(description = "Refresh token da sessão a terminar")
        String refreshToken) {

    @Override
    public String toString() {
        return "LogoutRequest[refreshToken=***]";
    }
}
