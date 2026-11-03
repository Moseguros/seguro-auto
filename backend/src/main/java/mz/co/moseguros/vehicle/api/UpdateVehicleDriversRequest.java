package mz.co.moseguros.vehicle.api;

import java.util.List;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import mz.co.moseguros.vehicle.domain.VehicleRules.DriverAssignment;

/**
 * Nova lista de condutores do veículo (substitui a actual). Vazia é permitida; senão até 5, sem
 * repetidos ({@code DUPLICATED}) e com exactamente um habitual ({@code HABITUAL_DRIVER_REQUIRED},
 * {@code MULTIPLE_HABITUAL_DRIVERS}).
 */
public record UpdateVehicleDriversRequest(
        @ArraySchema(schema = @Schema(implementation = VehicleDriverAssignment.class), maxItems = 5,
                arraySchema = @Schema(requiredMode = Schema.RequiredMode.REQUIRED))
        List<VehicleDriverAssignment> drivers,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED,
                description = "Versão do veículo lida; diferente da gravada → 409 CONCURRENT_MODIFICATION")
        @NotNull
        Long version) {

    List<DriverAssignment> toAssignments() {
        return drivers == null ? null : drivers.stream()
                .map(d -> d == null ? null : new DriverAssignment(d.driverId(), d.habitual()))
                .toList();
    }

    /** Um condutor da lista. */
    public record VehicleDriverAssignment(
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED,
                    description = "Condutor activo do cliente; senão 404 DRIVER_NOT_FOUND")
            UUID driverId,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            Boolean habitual) {
    }
}
