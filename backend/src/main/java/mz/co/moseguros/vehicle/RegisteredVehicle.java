package mz.co.moseguros.vehicle;

/**
 * Veículo e condutor registados (novos ou reutilizados) na conta do cliente por
 * {@link VehicleApi#registerForCustomer} (S4-06 §3).
 *
 * @param vehicleCreated o veículo foi criado nesta chamada
 * @param driverCreated  o condutor foi criado nesta chamada
 */
public record RegisteredVehicle(VehicleSnapshot vehicle, DriverSnapshot driver, boolean vehicleCreated,
                                boolean driverCreated) {
}
