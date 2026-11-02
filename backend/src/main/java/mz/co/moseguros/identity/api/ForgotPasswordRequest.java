package mz.co.moseguros.identity.api;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Pedido de recuperação de palavra-passe de staff. */
public record ForgotPasswordRequest(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "ana.machava@seguradora.co.mz")
        @NotBlank @Size(max = 254)
        String email) {

    @Override
    public String toString() {
        return "ForgotPasswordRequest[email=***]";
    }
}
