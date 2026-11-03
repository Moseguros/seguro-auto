package mz.co.moseguros.identity.api;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Login de staff: e-mail, palavra-passe e código da aplicação de autenticação. */
public record StaffLoginRequest(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "ana.machava@seguradora.co.mz")
        @NotBlank @Size(max = 254)
        String email,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, format = "password")
        @NotBlank @Size(max = 1024)
        String password,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "123456")
        @NotBlank @Size(max = 16)
        String totpCode) {

    @Override
    public String toString() {
        return "StaffLoginRequest[email=***, password=***, totpCode=***]";
    }
}
