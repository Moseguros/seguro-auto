package mz.co.moseguros.vehicle.infrastructure;

/** O registo automóvel não respondeu ({@link VehicleRegistryPort#lookup}). */
public class VehicleRegistryUnavailableException extends RuntimeException {

    public VehicleRegistryUnavailableException(String message) {
        super(message);
    }

    public VehicleRegistryUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
