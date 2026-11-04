package mz.co.moseguros.vehicle;

import mz.co.moseguros.shared.domain.Money;
import mz.co.moseguros.shared.domain.Province;
import mz.co.moseguros.shared.domain.VehicleCategory;
import mz.co.moseguros.shared.domain.VehicleUsage;

/**
 * Veículo a registar na conta de um cliente por outro módulo ({@link VehicleApi#registerForCustomer},
 * S4-06 §3), ainda por validar: as regras e os códigos são os do pedido de veículo do S2-03. Os campos
 * numéricos obrigatórios aceitam {@code null} para o erro sair como {@code REQUIRED}.
 */
public record NewVehicle(String plate, String chassisNumber, String make, String model, Integer year,
                         Integer engineCc, VehicleCategory category, Integer seats, Integer grossWeightKg,
                         VehicleUsage usage, Province provinceOfCirculation, Money commercialValue,
                         Boolean hasTracker) {
}
