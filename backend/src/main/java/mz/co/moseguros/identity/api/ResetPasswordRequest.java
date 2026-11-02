package mz.co.moseguros.identity.api;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Palavra-passe nova, com o token do link de recuperação. */
public record ResetPasswordRequest(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "Token do link enviado por e-mail")
        @NotBlank @Size(max = 128)
        String token,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, format = "password",
                description = "12 a 128 caracteres, sem a parte local do e-mail e fora das mais comuns "
                        + "(400 PASSWORD_TOO_WEAK com os motivos em errors[], campo newPassword)")
        @NotNull
        String newPassword) {

    @Override
    public String toString() {
        return "ResetPasswordRequest[token=***, newPassword=***]";
    }
}
