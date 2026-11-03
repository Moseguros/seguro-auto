package mz.co.moseguros.vehicle.application;

import java.util.List;
import java.util.UUID;

import mz.co.moseguros.document.FileRef;
import mz.co.moseguros.vehicle.domain.Vehicle;

/**
 * Veículo como é mostrado ao dono: o livrete actual (ou {@code null}) e os condutores
 * {@code ACTIVE}, o habitual primeiro.
 */
public record VehicleDetails(Vehicle vehicle, FileRef registrationDoc, List<DriverLink> drivers) {

    /** Condutor associado ao veículo. */
    public record DriverLink(UUID driverId, String fullName, boolean habitual) {
    }
}
