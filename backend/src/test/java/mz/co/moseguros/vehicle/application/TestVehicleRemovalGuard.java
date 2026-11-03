package mz.co.moseguros.vehicle.application;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import mz.co.moseguros.vehicle.VehicleRemovalGuard;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Guard de teste (perfil {@code test}): por defeito não bloqueia nada, como se não houvesse guards
 * (até ao S6-01); cada teste diz que veículos não podem ser retirados.
 */
@Component
@Profile("test")
public class TestVehicleRemovalGuard implements VehicleRemovalGuard {

    private final Set<UUID> blocked = ConcurrentHashMap.newKeySet();

    public void block(UUID vehicleId) {
        blocked.add(vehicleId);
    }

    public void unblock(UUID vehicleId) {
        blocked.remove(vehicleId);
    }

    @Override
    public boolean blocksRemoval(UUID vehicleId) {
        return blocked.contains(vehicleId);
    }
}
