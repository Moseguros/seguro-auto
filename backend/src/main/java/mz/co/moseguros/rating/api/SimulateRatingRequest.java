package mz.co.moseguros.rating.api;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import mz.co.moseguros.rating.application.RatingSimulationService.SimulationCommand;

/**
 * Simulação da tarifa de uma versão (S3-02 §12). O tipo de produto é o da versão; as regras do
 * pedido são as das cotações, excepto a janela da data de início e o limite por hora.
 *
 * @param rating  tarifa ainda não gravada (validada como em {@code updateRatingRules}); {@code null} = a gravada
 * @param charges encargos ainda não gravados (validados como em {@code updateInsurerRatingSettings});
 *                {@code null} = os gravados
 */
public record SimulateRatingRequest(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        SimulationVehicleRequest vehicle,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        RiskDriverRequest driver,
        @ArraySchema(arraySchema = @Schema(description = "Coberturas pedidas além das obrigatórias (pode ser vazia)"))
        List<RequestedCoverageRequest> coverages,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "Qualquer data", example = "2027-01-15")
        LocalDate startDate,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "365")
        Integer durationDays,
        @Schema(ref = RatingRulesSchema.REF, nullable = true)
        JsonNode rating,
        @ArraySchema(schema = @Schema(ref = "#/components/schemas/Charge"), maxItems = 10,
                arraySchema = @Schema(nullable = true))
        JsonNode charges) {

    SimulationCommand toCommand() {
        return new SimulationCommand(vehicle == null ? null : vehicle.toInput(),
                driver == null ? null : driver.toInput(), durationDays, startDate,
                RequestedCoverageRequest.toInputs(coverages), rating, charges);
    }
}
