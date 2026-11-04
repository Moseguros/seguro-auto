package mz.co.moseguros.rating;

import java.util.UUID;

import mz.co.moseguros.shared.domain.Province;
import mz.co.moseguros.shared.domain.VehicleCategory;
import mz.co.moseguros.shared.domain.VehicleUsage;

/**
 * Veículo a tarifar (S3-01 §8), copiado do {@code VehicleSnapshot} pela S3-02.
 *
 * @param commercialValue valor comercial em centavos ({@code Money.amount()}); {@code null} se desconhecido
 */
public record RatedVehicle(UUID vehicleId, String plate, String make, String model, int year, Integer engineCc,
                           VehicleCategory category, VehicleUsage usage, Integer seats, Integer grossWeightKg,
                           Province provinceOfCirculation, Long commercialValue, boolean hasTracker) {
}
