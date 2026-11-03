package mz.co.moseguros.vehicle;

import java.time.Clock;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.error.ErrorCodes;
import mz.co.moseguros.vehicle.application.VehicleSnapshots;
import mz.co.moseguros.vehicle.domain.Driver;
import mz.co.moseguros.vehicle.domain.Vehicle;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Interface pública do módulo Veículos (modelo transversal §3). Todos os métodos correm no âmbito
 * de quem chama: o RLS aplica-se (o âmbito {@code ORG} não vê veículos nem condutores; os módulos
 * seguintes guardam snapshots tirados no âmbito do cliente).
 */
@Service
public class VehicleApi {

    private final VehicleSnapshots snapshots;
    private final Clock clock;

    public VehicleApi(VehicleSnapshots snapshots, Clock clock) {
        this.snapshots = snapshots;
        this.clock = clock;
    }

    /** Veículo {@code ACTIVE} do cliente indicado; vazio se não existir, não for dele ou estiver retirado. */
    @Transactional(readOnly = true)
    public Optional<VehicleView> findOwned(UUID vehicleId, UUID ownerUserId) {
        return snapshots.findVehicle(vehicleId)
                .filter(v -> v.getOwnerUserId().equals(ownerUserId) && v.isActive())
                .map(v -> view(v, snapshots.habitualDriver(v.getId())));
    }

    /**
     * Cópia dos dados do veículo agora (qualquer estado).
     *
     * @throws DomainException {@code VEHICLE_NOT_FOUND} se não existir ou não for visível
     */
    @Transactional(readOnly = true)
    public VehicleSnapshot snapshot(UUID vehicleId) {
        Vehicle v = snapshots.findVehicle(vehicleId)
                .orElseThrow(() -> DomainException.of(ErrorCodes.VEHICLE_NOT_FOUND, "Vehicle not found"));
        return new VehicleSnapshot(v.getId(), v.getOwnerUserId(), v.getPlate().value(), v.getChassisNumber(),
                v.getMake(), v.getModel(), v.getYear(), v.getEngineCc(), v.getCategory(), v.getSeats(),
                v.getGrossWeightKg(), v.getUsage(), v.getProvinceOfCirculation(), v.getCommercialValue(),
                v.hasTracker(), v.isInatroVerified(), snapshots.habitualDriver(v.getId()).orElse(null),
                clock.instant());
    }

    /**
     * Cópia dos dados do condutor agora (qualquer estado), sem o número da carta em claro.
     *
     * @throws DomainException {@code DRIVER_NOT_FOUND} se não existir ou não for visível
     */
    @Transactional(readOnly = true)
    public DriverSnapshot driverSnapshot(UUID driverId) {
        Driver d = snapshots.findDriver(driverId)
                .orElseThrow(() -> DomainException.of(ErrorCodes.DRIVER_NOT_FOUND, "Driver not found"));
        return new DriverSnapshot(d.getId(), d.getOwnerUserId(), d.getFullName(), d.getBirthDate(),
                d.licenceNumberMasked(), d.getLicenceNumberHash(), List.copyOf(d.getLicenceCategories()),
                d.getLicenceIssueDate(), d.isOwner(), clock.instant());
    }

    private static VehicleView view(Vehicle v, Optional<UUID> habitualDriverId) {
        return new VehicleView(v.getId(), v.getOwnerUserId(), v.getPlate().value(), v.getPlateFormatCode(),
                v.getChassisNumber(), v.getMake(), v.getModel(), v.getYear(), v.getEngineCc(), v.getCategory(),
                v.getSeats(), v.getGrossWeightKg(), v.getUsage(), v.getProvinceOfCirculation(), v.getCommercialValue(),
                v.hasTracker(), v.isInatroVerified(), v.getRegistrationDocFileId(), habitualDriverId.orElse(null),
                v.getStatus().name(), v.getVersion());
    }
}
