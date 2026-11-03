package mz.co.moseguros.catalog.api;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import mz.co.moseguros.catalog.ProductType;

/** Novo produto da seguradora (fica em {@code DRAFT}, com a versão 1 vazia). */
public record CreateProductRequest(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "AUTO-COMPLETO",
                description = "^[A-Z0-9][A-Z0-9_-]{1,29}$; único na seguradora; não muda depois de criado")
        @NotBlank @Pattern(regexp = "^[A-Z0-9][A-Z0-9_-]{1,29}$")
        String code,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "Auto Completo")
        @NotBlank @Size(min = 2, max = 120)
        String name,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull
        ProductType type,
        @Size(max = 1000)
        String description) {
}
