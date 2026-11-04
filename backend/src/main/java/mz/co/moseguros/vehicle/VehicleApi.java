package mz.co.moseguros.vehicle;

import java.time.Clock;
import java.util.Optional;
import java.util.UUID;

import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.error.ErrorCodes;
import mz.co.moseguros.vehicle.application.VehicleSnapshots;
import mz.co.moseguros.vehicle.application.VehicleSystemLookups;
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
    private final VehicleSystemLookups systemLookups;
    private final Clock clock;

    public VehicleApi(VehicleSnapshots snapshots, VehicleSystemLookups systemLookups, Clock clock) {
        this.snapshots = snapshots;
        this.systemLookups = systemLookups;
        this.clock = clock;
    }

    /**
     * Regista o veículo e o condutor na conta do cliente indicado, a pedido de um mediador (S4-06 §3),
     * na transacção de quem chama e em {@code SYSTEM} (o âmbito {@code ORG} não escreve nas tabelas do
     * cliente). Mesmas regras e códigos dos pedidos do S2-03, com {@code errors[].field} prefixado
     * ({@code vehicle.chassisNumber}, {@code driver.licenceNumber}); reutiliza o condutor {@code ACTIVE}
     * com a mesma carta e o veículo {@code ACTIVE} com a mesma matrícula e o mesmo chassis; liga o
     * condutor ao veículo (habitual se o veículo não tiver habitual). Auditoria com
     * {@code actorType = STAFF}. <strong>Quem chama é responsável por só registar para um cliente com
     * quem tem uma relação legítima</strong> (o cliente da cotação do mediador).
     *
     * @throws DomainException 400 {@code VALIDATION_ERROR}; 409 {@code VEHICLE_ALREADY_REGISTERED} (mesma
     *                         matrícula, outro chassis), {@code VEHICLE_LIMIT_REACHED},
     *                         {@code DRIVER_LIMIT_REACHED}, {@code OWNER_DRIVER_ALREADY_EXISTS}
     */
    public RegisteredVehicle registerForCustomer(UUID ownerUserId, NewVehicle vehicle, NewDriver driver) {
        return systemLookups.registerForCustomer(ownerUserId, vehicle, driver);
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
        return snapshots.vehicleSnapshot(v, clock.instant());
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
        return snapshots.driverSnapshot(d, clock.instant());
    }

    private static VehicleView view(Vehicle v, Optional<UUID> habitualDriverId) {
        return new VehicleView(v.getId(), v.getOwnerUserId(), v.getPlate().value(), v.getPlateFormatCode(),
                v.getChassisNumber(), v.getMake(), v.getModel(), v.getYear(), v.getEngineCc(), v.getCategory(),
                v.getSeats(), v.getGrossWeightKg(), v.getUsage(), v.getProvinceOfCirculation(), v.getCommercialValue(),
                v.hasTracker(), v.isInatroVerified(), v.getRegistrationDocFileId(), habitualDriverId.orElse(null),
                v.getStatus().name(), v.getVersion());
    }
}
