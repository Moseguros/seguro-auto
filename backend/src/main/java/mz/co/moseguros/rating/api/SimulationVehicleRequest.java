package mz.co.moseguros.rating.api;

import io.swagger.v3.oas.annotations.media.Schema;
import mz.co.moseguros.rating.domain.QuoteRequestValidator.VehicleInput;
import mz.co.moseguros.shared.domain.Money;
import mz.co.moseguros.shared.domain.Province;
import mz.co.moseguros.shared.domain.VehicleCategory;
import mz.co.moseguros.shared.domain.VehicleUsage;

/**
 * Veículo do simulador (S3-02 §12): os campos do mediador sem matrícula. Só {@code year},
 * {@code category}, {@code usage} e {@code provinceOfCirculation} são obrigatórios (o gestor testa
 * riscos parciais); os limites dos valores mantêm-se.
 */
public record SimulationVehicleRequest(
        @Schema(maxLength = 60)
        String make,
        @Schema(maxLength = 60)
        String model,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "2024")
        Integer year,
        @Schema(description = "1–30 000; em TRAILER tem de faltar")
        Integer engineCc,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        VehicleCategory category,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        VehicleUsage usage,
        @Schema(description = "TRAILER: 0; MOTORCYCLE: 1–3; outras: 1–100")
        Integer seats,
        @Schema(description = "1–100 000")
        Integer grossWeightKg,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        Province provinceOfCirculation,
        @Schema(description = "Valor comercial (> 0); obrigatório para danos próprios")
        Money commercialValue,
        @Schema(description = "Localizador instalado (defeito false)")
        Boolean hasTracker) {

    VehicleInput toInput() {
        return new VehicleInput(null, make, model, year, engineCc, category, usage, seats, grossWeightKg,
                provinceOfCirculation, commercialValue, hasTracker);
    }
}
