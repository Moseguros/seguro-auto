package mz.co.moseguros.identity.api;

import java.util.List;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Convite de staff. */
public record InviteUserRequest(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "Ana Machava")
        @NotBlank @Size(min = 2, max = 120)
        String fullName,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "ana.machava@seguradora.co.mz")
        @NotBlank
        String email,
        @ArraySchema(minItems = 1, schema = @Schema(example = "INSURER_ADMIN"),
                arraySchema = @Schema(requiredMode = Schema.RequiredMode.REQUIRED,
                        description = "Perfis permitidos no tipo da organização (400 ROLE_NOT_ALLOWED_FOR_ORGANIZATION)"))
        @NotNull @Size(min = 1, max = 20)
        List<@NotBlank String> roleCodes) {
}
