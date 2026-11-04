package mz.co.moseguros.rating.domain.tariff;

import java.time.LocalDate;
import java.time.Period;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import mz.co.moseguros.shared.domain.Province;
import mz.co.moseguros.shared.domain.VehicleCategory;
import mz.co.moseguros.shared.domain.VehicleUsage;

/**
 * Variáveis de tarifação de uma cotação (S3-01 §8), calculadas uma vez a partir do veículo, do
 * condutor, da data de início {@code D} e da duração. As idades contam-se em {@code D}, nunca na
 * data de hoje. {@code null} = variável ausente (uma condição sobre ela não se cumpre). O
 * {@code capital} não está aqui: é o da cobertura que está a ser avaliada.
 */
public record RatingVariables(VehicleCategory vehicleCategory, VehicleUsage vehicleUsage, Province province,
                              Boolean hasTracker, Integer vehicleAgeYears, Integer engineCc, Integer seats,
                              Integer grossWeightKg, Integer driverAgeYears, Integer licenceYears,
                              Integer durationDays, Long vehicleValue) {

    public static RatingVariables of(RatedVehicle vehicle, RatedDriver driver, LocalDate startDate, int durationDays) {
        Objects.requireNonNull(vehicle, "vehicle");
        Objects.requireNonNull(startDate, "startDate");
        LocalDate birthDate = driver == null ? null : driver.birthDate();
        LocalDate licenceIssueDate = driver == null ? null : driver.licenceIssueDate();
        return new RatingVariables(vehicle.category(), vehicle.usage(), vehicle.provinceOfCirculation(),
                vehicle.hasTracker(), Math.max(0, startDate.getYear() - vehicle.year()), vehicle.engineCc(),
                vehicle.seats(), vehicle.grossWeightKg(), years(birthDate, startDate), years(licenceIssueDate, startDate),
                durationDays, vehicle.commercialValue());
    }

    private static Integer years(LocalDate from, LocalDate on) {
        return from == null ? null : Math.max(0, Period.between(from, on).getYears());
    }

    /** Só as variáveis presentes, pela ordem da decomposição (S3-01 §10); enums pelo nome. */
    public Map<String, Object> toMap() {
        Map<String, Object> map = new LinkedHashMap<>();
        put(map, "vehicleCategory", vehicleCategory == null ? null : vehicleCategory.name());
        put(map, "vehicleUsage", vehicleUsage == null ? null : vehicleUsage.name());
        put(map, "province", province == null ? null : province.name());
        put(map, "hasTracker", hasTracker);
        put(map, "vehicleAgeYears", vehicleAgeYears);
        put(map, "engineCc", engineCc);
        put(map, "seats", seats);
        put(map, "grossWeightKg", grossWeightKg);
        put(map, "driverAgeYears", driverAgeYears);
        put(map, "licenceYears", licenceYears);
        put(map, "durationDays", durationDays);
        put(map, "vehicleValue", vehicleValue);
        return map;
    }

    private static void put(Map<String, Object> map, String key, Object value) {
        if (value != null) {
            map.put(key, value);
        }
    }
}
