package mz.co.moseguros.catalog.api;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import mz.co.moseguros.catalog.ProductType;

/**
 * Alteração de um produto. O tipo só muda enquanto o produto nunca foi publicado.
 *
 * @param version {@code version} do produto lida antes da alteração
 */
public record UpdateProductRequest(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "Auto Completo")
        @NotBlank @Size(min = 2, max = 120)
        String name,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull
        ProductType type,
        @Size(max = 1000)
        String description,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull
        Long version) {
}
