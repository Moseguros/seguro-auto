package mz.co.moseguros.policy.api;

import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import mz.co.moseguros.policy.domain.Policy;

/** Referências partilhadas pelas respostas das apólices. */
public final class PolicyRefs {

    private PolicyRefs() {
    }

    /** A seguradora da apólice. */
    @Schema(name = "PolicyInsurerRef")
    public record InsurerRef(
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED) UUID id,
            @Schema(example = "Seguradora Demo") String name) {
    }

    /** O veículo seguro (do snapshot da proposta). */
    @Schema(name = "PolicyVehicleRef")
    public record VehicleRef(
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED) UUID id,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "ABC-123-MC") String plate,
            @Schema(example = "Toyota") String make,
            @Schema(example = "Corolla") String model,
            @Schema(example = "2018") Integer year) {

        static VehicleRef of(Policy p, boolean withYear) {
            JsonNode s = p.getVehicleSnapshot();
            Integer year = withYear && s.hasNonNull("year") ? s.get("year").asInt() : null;
            return new VehicleRef(p.getVehicleId(), p.getPlate(), s.path("make").asText(null),
                    s.path("model").asText(null), year);
        }
    }
}
