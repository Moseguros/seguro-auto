package mz.co.moseguros.catalog.api;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

/** Publicação de uma versão {@code DRAFT}; {@code version} é a da versão lida antes. */
public record PublishProductVersionRequest(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull
        Long version) {
}
