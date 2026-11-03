package mz.co.moseguros.vehicle.api;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import mz.co.moseguros.shared.domain.Money;
import mz.co.moseguros.shared.domain.Province;
import mz.co.moseguros.shared.domain.VehicleCategory;
import mz.co.moseguros.shared.domain.VehicleUsage;
import mz.co.moseguros.vehicle.domain.VehicleRules.VehicleData;

/** Todos os campos de {@link CreateVehicleRequest}, com as mesmas regras, mais {@code version}. */
public record UpdateVehicleRequest(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "AAA-100-MC")
        String plate,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "JTDBR32E720012345")
        String chassisNumber,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, maxLength = 60, example = "Toyota")
        String make,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, maxLength = 60, example = "Corolla")
        String model,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "2018")
        Integer year,
        @Schema(description = "Obrigatório excepto em TRAILER, onde tem de faltar", example = "1598")
        Integer engineCc,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        VehicleCategory category,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "5")
        Integer seats,
        @Schema(description = "Obrigatório em HEAVY e TRAILER")
        Integer grossWeightKg,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        VehicleUsage usage,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        Province provinceOfCirculation,
        Money commercialValue,
        Boolean hasTracker,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED,
                description = "Versão lida; diferente da gravada → 409 CONCURRENT_MODIFICATION")
        @NotNull
        Long version) {

    VehicleData toData() {
        return new VehicleData(plate, chassisNumber, make, model, year, engineCc, category, seats, grossWeightKg,
                usage, provinceOfCirculation, commercialValue, hasTracker);
    }
}
