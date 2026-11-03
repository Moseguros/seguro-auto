package mz.co.moseguros.vehicle.api;

import io.swagger.v3.oas.annotations.media.Schema;
import mz.co.moseguros.shared.domain.VehicleCategory;
import mz.co.moseguros.vehicle.application.RegistryLookupService.RegistryResult;
import mz.co.moseguros.vehicle.infrastructure.RegistryVehicleData;

/** Dados de uma matrícula no registo automóvel, para pré-preencher o formulário. */
public record RegistryLookupResponse(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "AAA-100-MC")
        String plate,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "JTDBR32E720012345")
        String chassisNumber,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "Toyota")
        String make,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "Corolla")
        String model,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "2018")
        int year,
        @Schema(example = "1598")
        Integer engineCc,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        VehicleCategory category,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "5")
        int seats,
        Integer grossWeightKg,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "Origem dos dados (MOCK até haver acesso "
                + "ao INATRO)", example = "MOCK")
        String source) {

    static RegistryLookupResponse from(RegistryResult result) {
        RegistryVehicleData d = result.data();
        return new RegistryLookupResponse(d.plate(), d.chassisNumber(), d.make(), d.model(), d.year(), d.engineCc(),
                d.category(), d.seats(), d.grossWeightKg(), result.source());
    }
}
