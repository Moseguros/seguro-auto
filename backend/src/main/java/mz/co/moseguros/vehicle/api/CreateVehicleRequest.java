package mz.co.moseguros.vehicle.api;

import io.swagger.v3.oas.annotations.media.Schema;
import mz.co.moseguros.shared.domain.Money;
import mz.co.moseguros.shared.domain.Province;
import mz.co.moseguros.shared.domain.VehicleCategory;
import mz.co.moseguros.shared.domain.VehicleUsage;
import mz.co.moseguros.vehicle.domain.VehicleRules.VehicleData;

/**
 * Dados do livrete de um veículo novo. Os erros de validação vêm todos juntos em {@code errors[]}
 * ({@code REQUIRED}, {@code INVALID_PLATE}, {@code INVALID_CHASSIS}, {@code INVALID_YEAR},
 * {@code OUT_OF_RANGE}, {@code NOT_ALLOWED}, {@code INVALID_CURRENCY}).
 */
public record CreateVehicleRequest(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED,
                description = "Num dos formatos activos (espaços, hífens e minúsculas aceites)", example = "AAA-100-MC")
        String plate,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED,
                description = "5–30 letras ou dígitos (espaços e hífens ignorados)", example = "JTDBR32E720012345")
        String chassisNumber,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, maxLength = 60, example = "Toyota")
        String make,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, maxLength = 60, example = "Corolla")
        String model,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED,
                description = "De 1900 ao próximo ano (em Africa/Maputo)", example = "2018")
        Integer year,
        @Schema(description = "1–30 000; obrigatório excepto em TRAILER, onde tem de faltar", example = "1598")
        Integer engineCc,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        VehicleCategory category,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED,
                description = "TRAILER: 0; MOTORCYCLE: 1–3; outras: 1–100", example = "5")
        Integer seats,
        @Schema(description = "1–100 000; obrigatório em HEAVY e TRAILER", example = "40000")
        Integer grossWeightKg,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        VehicleUsage usage,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        Province provinceOfCirculation,
        @Schema(description = "Valor comercial em centavos (MZN), > 0 e ≤ 100 000 000 000")
        Money commercialValue,
        @Schema(description = "Localizador instalado (defeito false)")
        Boolean hasTracker) {

    VehicleData toData() {
        return new VehicleData(plate, chassisNumber, make, model, year, engineCc, category, seats, grossWeightKg,
                usage, provinceOfCirculation, commercialValue, hasTracker);
    }
}
