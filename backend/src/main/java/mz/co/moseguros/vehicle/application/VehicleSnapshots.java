package mz.co.moseguros.vehicle.application;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import mz.co.moseguros.vehicle.DriverSnapshot;
import mz.co.moseguros.vehicle.VehicleSnapshot;
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

    /** Cópia dos dados do veículo (com o condutor habitual activo, se houver). */
    public VehicleSnapshot vehicleSnapshot(Vehicle v, Instant takenAt) {
        return new VehicleSnapshot(v.getId(), v.getOwnerUserId(), v.getPlate().value(), v.getChassisNumber(),
                v.getMake(), v.getModel(), v.getYear(), v.getEngineCc(), v.getCategory(), v.getSeats(),
                v.getGrossWeightKg(), v.getUsage(), v.getProvinceOfCirculation(), v.getCommercialValue(),
                v.hasTracker(), v.isInatroVerified(), habitualDriver(v.getId()).orElse(null), takenAt);
    }

    /** Cópia dos dados do condutor, sem o número da carta em claro. */
    public DriverSnapshot driverSnapshot(Driver d, Instant takenAt) {
        return new DriverSnapshot(d.getId(), d.getOwnerUserId(), d.getFullName(), d.getBirthDate(),
                d.licenceNumberMasked(), d.getLicenceNumberHash(), List.copyOf(d.getLicenceCategories()),
                d.getLicenceIssueDate(), d.isOwner(), takenAt);
    }
}
