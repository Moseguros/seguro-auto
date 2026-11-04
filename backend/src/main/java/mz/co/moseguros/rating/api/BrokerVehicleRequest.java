package mz.co.moseguros.rating.api;

import io.swagger.v3.oas.annotations.media.Schema;
import mz.co.moseguros.rating.domain.QuoteRequestValidator.VehicleInput;
import mz.co.moseguros.shared.domain.Money;
import mz.co.moseguros.shared.domain.Province;
import mz.co.moseguros.shared.domain.VehicleCategory;
import mz.co.moseguros.shared.domain.VehicleUsage;

/**
 * Veículo escrito pelo mediador (S3-02 §6): só o que a tarifa usa. As regras são as do S2-03, com
 * a matrícula, a marca, o modelo e a cilindrada opcionais. Sem chassis.
 */
public record BrokerVehicleRequest(
        @Schema(description = "Opcional; num dos formatos activos (INVALID_PLATE)", example = "ABC-123-MC")
        String plate,
        @Schema(maxLength = 60, example = "Toyota")
        String make,
        @Schema(maxLength = 60, example = "Corolla")
        String model,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "De 1900 ao próximo ano", example = "2024")
        Integer year,
        @Schema(description = "1–30 000; em TRAILER tem de faltar", example = "1600")
        Integer engineCc,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        VehicleCategory category,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        VehicleUsage usage,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "TRAILER: 0; MOTORCYCLE: 1–3; outras: 1–100",
                example = "5")
        Integer seats,
        @Schema(description = "1–100 000; obrigatório em HEAVY e TRAILER")
        Integer grossWeightKg,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        Province provinceOfCirculation,
        @Schema(description = "Valor comercial (> 0); obrigatório para danos próprios (VEHICLE_VALUE_REQUIRED)")
        Money commercialValue,
        @Schema(description = "Localizador instalado (defeito false)")
        Boolean hasTracker) {

    VehicleInput toInput() {
        return new VehicleInput(plate, make, model, year, engineCc, category, usage, seats, grossWeightKg,
                provinceOfCirculation, commercialValue, hasTracker);
    }
}
