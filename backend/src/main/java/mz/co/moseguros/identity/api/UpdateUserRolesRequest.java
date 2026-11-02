package mz.co.moseguros.identity.api;

import java.util.List;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Perfis novos de um utilizador de staff (substituem os actuais). */
public record UpdateUserRolesRequest(
        @ArraySchema(minItems = 1, schema = @Schema(example = "UNDERWRITER"),
                arraySchema = @Schema(requiredMode = Schema.RequiredMode.REQUIRED,
                        description = "Perfis permitidos no tipo da organização (400 ROLE_NOT_ALLOWED_FOR_ORGANIZATION)"))
        @NotNull @Size(min = 1, max = 20)
        List<@NotBlank String> roleCodes,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED,
                description = "Versão lida; diferente da gravada → 409 CONCURRENT_MODIFICATION")
        @NotNull
        Long version) {
}
