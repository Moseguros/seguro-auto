package mz.co.moseguros.rating.domain.tariff;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import mz.co.moseguros.shared.domain.Province;
import mz.co.moseguros.shared.domain.VehicleCategory;
import mz.co.moseguros.shared.domain.VehicleUsage;

/**
 * Condições de uma linha da tarifa ({@code when}). Uma linha aplica-se quando <b>todas</b> as
 * condições presentes se cumprem; {@code null} = sem condição sobre essa variável. Sem nenhuma
 * condição ({@code "when": {}}) aplica-se sempre. Uma condição sobre uma variável ausente não se
 * cumpre.
 */
public record Condition(Set<VehicleCategory> vehicleCategory, Set<VehicleUsage> vehicleUsage, Set<Province> province,
                        Boolean hasTracker, IntRange vehicleAgeYears, IntRange engineCc, IntRange seats,
                        IntRange grossWeightKg, IntRange driverAgeYears, IntRange licenceYears,
                        IntRange durationDays, AmountRange capital, AmountRange vehicleValue) {

    /** Intervalo de uma condição ({@link IntRange} ou {@link AmountRange}). */
    public sealed interface Range permits IntRange, AmountRange {
        boolean isInverted();
    }

    /** {@code "when": {}}. */
    public static final Condition ALWAYS = new Condition(null, null, null, null, null, null, null, null, null, null,
            null, null, null);

    /**
     * @param capital capital resolvido da cobertura que está a ser avaliada ({@code null} se não tiver)
     */
    public boolean matches(RatingVariables v, Long capital) {
        return in(vehicleCategory, v.vehicleCategory())
                && in(vehicleUsage, v.vehicleUsage())
                && in(province, v.province())
                && (hasTracker == null || hasTracker.equals(v.hasTracker()))
                && (vehicleAgeYears == null || vehicleAgeYears.contains(v.vehicleAgeYears()))
                && (engineCc == null || engineCc.contains(v.engineCc()))
                && (seats == null || seats.contains(v.seats()))
                && (grossWeightKg == null || grossWeightKg.contains(v.grossWeightKg()))
                && (driverAgeYears == null || driverAgeYears.contains(v.driverAgeYears()))
                && (licenceYears == null || licenceYears.contains(v.licenceYears()))
                && (durationDays == null || durationDays.contains(v.durationDays()))
                && (this.capital == null || this.capital.contains(capital))
                && (vehicleValue == null || vehicleValue.contains(v.vehicleValue()));
    }

    /** Intervalos presentes, pelo nome da variável no JSON (para os ponteiros dos erros). */
    public Map<String, Range> ranges() {
        Map<String, Range> ranges = new LinkedHashMap<>();
        put(ranges, "vehicleAgeYears", vehicleAgeYears);
        put(ranges, "engineCc", engineCc);
        put(ranges, "seats", seats);
        put(ranges, "grossWeightKg", grossWeightKg);
        put(ranges, "driverAgeYears", driverAgeYears);
        put(ranges, "licenceYears", licenceYears);
        put(ranges, "durationDays", durationDays);
        put(ranges, "capital", capital);
        put(ranges, "vehicleValue", vehicleValue);
        return ranges;
    }

    private static <T> boolean in(Set<T> allowed, T value) {
        return allowed == null || (value != null && allowed.contains(value));
    }

    private static void put(Map<String, Range> ranges, String name, Range range) {
        if (range != null) {
            ranges.put(name, range);
        }
    }
}
