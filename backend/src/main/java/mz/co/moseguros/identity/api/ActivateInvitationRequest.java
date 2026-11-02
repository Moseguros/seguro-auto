package mz.co.moseguros.identity.api;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/** Primeiro código da aplicação de autenticação. */
public record ActivateInvitationRequest(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "123456")
        @NotBlank
        String totpCode) {

    @Override
    public String toString() {
        return "ActivateInvitationRequest[totpCode=***]";
    }
}
