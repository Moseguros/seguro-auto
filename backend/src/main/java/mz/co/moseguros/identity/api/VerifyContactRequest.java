package mz.co.moseguros.identity.api;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/** Código recebido no contacto novo. */
public record VerifyContactRequest(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "challengeId devolvido no pedido")
        @NotNull
        UUID challengeId,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "123456")
        @NotBlank
        String code) {

    @Override
    public String toString() {
        return "VerifyContactRequest[challengeId=" + challengeId + ", code=***]";
    }
}
