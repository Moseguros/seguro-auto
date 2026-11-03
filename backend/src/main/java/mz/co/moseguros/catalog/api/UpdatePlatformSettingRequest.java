package mz.co.moseguros.catalog.api;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

/**
 * Novo valor de um parâmetro.
 *
 * @param value em texto, conforme o tipo: {@code MONEY} centavos &gt; 0, {@code INTEGER},
 *              {@code BOOLEAN} ({@code true}/{@code false}) ou {@code STRING} (1–500); senão
 *              {@code INVALID_VALUE}
 */
public record UpdatePlatformSettingRequest(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "300000000")
        @NotNull
        String value,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED,
                description = "Versão lida; diferente da gravada → 409 CONCURRENT_MODIFICATION")
        @NotNull
        Long version) {
}
