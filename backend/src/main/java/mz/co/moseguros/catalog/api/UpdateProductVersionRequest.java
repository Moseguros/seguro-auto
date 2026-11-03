package mz.co.moseguros.catalog.api;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import mz.co.moseguros.shared.domain.VehicleCategory;
import mz.co.moseguros.shared.domain.VehicleUsage;

/**
 * Elegibilidade e notas de uma versão {@code DRAFT}. As listas podem estar vazias enquanto a
 * versão está em preparação (a publicação exige-as).
 *
 * @param version {@code version} da versão lida antes da alteração
 */
public record UpdateProductVersionRequest(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull
        List<VehicleCategory> vehicleCategories,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull
        List<VehicleUsage> usages,
        @Schema(description = "O que mudou nesta versão")
        @Size(max = 1000)
        String changeNotes,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull
        Long version) {
}
