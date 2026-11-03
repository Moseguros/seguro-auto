package mz.co.moseguros.vehicle.domain;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import mz.co.moseguros.shared.persistence.BaseEntity;

/**
 * Condutor de um veículo ({@code vehicle.vehicle_driver}); no máximo um habitual por veículo. A
 * lista de um veículo é substituída por inteiro ({@code PUT /me/vehicles/{id}/drivers}).
 */
@Entity
@Table(schema = "vehicle", name = "vehicle_driver")
public class VehicleDriver extends BaseEntity {

    @Column(name = "vehicle_id", nullable = false, updatable = false)
    private UUID vehicleId;

    @Column(name = "driver_id", nullable = false, updatable = false)
    private UUID driverId;

    @Column(name = "is_habitual", nullable = false)
    private boolean habitual;

    protected VehicleDriver() {
    }

    public VehicleDriver(UUID vehicleId, UUID driverId, boolean habitual) {
        this.vehicleId = vehicleId;
        this.driverId = driverId;
        this.habitual = habitual;
    }

    public UUID getVehicleId() {
        return vehicleId;
    }

    public UUID getDriverId() {
        return driverId;
    }

    public boolean isHabitual() {
        return habitual;
    }
}
