package mz.co.moseguros.vehicle.application;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import mz.co.moseguros.vehicle.domain.Driver;
import mz.co.moseguros.vehicle.domain.DriverRepository;
import mz.co.moseguros.vehicle.domain.RecordStatus;
import mz.co.moseguros.vehicle.domain.Vehicle;
import mz.co.moseguros.vehicle.domain.VehicleDriver;
import mz.co.moseguros.vehicle.domain.VehicleDriverRepository;
import mz.co.moseguros.vehicle.domain.VehicleRepository;
import org.springframework.stereotype.Component;

/**
 * Leituras do {@code VehicleApi}, no âmbito de quem chama (RLS), sem verificar permissões: quem
 * chama é outro módulo, que já decidiu se o pedido é permitido.
 */
@Component
public class VehicleSnapshots {

    private final VehicleRepository vehicles;
    private final DriverRepository drivers;
    private final VehicleDriverRepository vehicleDrivers;

    public VehicleSnapshots(VehicleRepository vehicles, DriverRepository drivers,
                            VehicleDriverRepository vehicleDrivers) {
        this.vehicles = vehicles;
        this.drivers = drivers;
        this.vehicleDrivers = vehicleDrivers;
    }

    /** Veículo em qualquer estado, se visível. */
    public Optional<Vehicle> findVehicle(UUID vehicleId) {
        return vehicles.findById(vehicleId);
    }

    /** Condutor em qualquer estado, se visível. */
    public Optional<Driver> findDriver(UUID driverId) {
        return drivers.findById(driverId);
    }

    /** Condutor habitual {@code ACTIVE} do veículo, se houver. */
    public Optional<UUID> habitualDriver(UUID vehicleId) {
        List<UUID> habitual = vehicleDrivers.findByVehicleIdIn(Set.of(vehicleId)).stream()
                .filter(VehicleDriver::isHabitual)
                .map(VehicleDriver::getDriverId)
                .toList();
        if (habitual.isEmpty()) {
            return Optional.empty();
        }
        return drivers.findByIdInAndStatus(habitual, RecordStatus.ACTIVE).stream()
                .map(Driver::getId)
                .findFirst();
    }
}
