package mz.co.moseguros.policy.application;

import java.util.UUID;

import mz.co.moseguros.vehicle.VehicleRemovalGuard;
import org.springframework.stereotype.Component;

/**
 * Um veículo com apólice {@code ACTIVE} ou {@code SUSPENDED} em vigor não pode ser retirado (S6-01 §9.1;
 * decisão D29): {@code DELETE /me/vehicles/{id}} → 409 {@code VEHICLE_REMOVAL_BLOCKED}. Corre no âmbito
 * de quem retira, dentro da transacção do {@code DELETE}; a consulta corre em {@code SYSTEM}
 * ({@link PolicySystemLookups}), só pelo {@code vehicle_id}, e só sai {@code true}/{@code false}. O S7-06
 * estende a regra aos veículos das frotas.
 */
@Component
public class PolicyVehicleRemovalGuard implements VehicleRemovalGuard {

    private final PolicySystemLookups lookups;

    public PolicyVehicleRemovalGuard(PolicySystemLookups lookups) {
        this.lookups = lookups;
    }

    @Override
    public boolean blocksRemoval(UUID vehicleId) {
        return lookups.vehicleHasPolicyInForce(vehicleId);
    }
}
