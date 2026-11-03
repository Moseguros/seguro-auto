package mz.co.moseguros.identity.api;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/** Preferências da conta autenticada. */
public record UpdateMeRequest(
        @Schema(description = "Só staff, 2–120 caracteres; omitido mantém o actual. Ignorado para clientes "
                + "(o nome está no perfil).", example = "Bruno Staff")
        String displayName,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, allowableValues = {"pt", "en"}, example = "pt")
        @NotBlank @Pattern(regexp = "pt|en")
        String preferredLanguage) {
}
