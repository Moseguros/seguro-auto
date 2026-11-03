package mz.co.moseguros.catalog.api;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

/**
 * Lista completa das coberturas de uma versão {@code DRAFT} (substitui a actual). A ordem da lista
 * é a ordem de apresentação.
 *
 * @param coverages no máximo 12
 * @param version   {@code version} da versão lida antes da alteração
 */
public record ReplaceCoveragesRequest(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull
        List<CoverageRequest> coverages,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull
        Long version) {
}
