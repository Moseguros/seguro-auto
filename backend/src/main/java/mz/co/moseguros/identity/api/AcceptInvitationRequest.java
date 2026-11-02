package mz.co.moseguros.identity.api;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

/** Palavra-passe escolhida pelo convidado. */
public record AcceptInvitationRequest(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, format = "password",
                description = "12 a 128 caracteres, sem a parte local do e-mail e fora das mais comuns "
                        + "(400 PASSWORD_TOO_WEAK com os motivos em errors[])")
        @NotNull
        String password) {

    @Override
    public String toString() {
        return "AcceptInvitationRequest[password=***]";
    }
}
