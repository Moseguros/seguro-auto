package mz.co.moseguros.vehicle.domain;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

import mz.co.moseguros.shared.domain.Matricula;
import mz.co.moseguros.shared.domain.Money;
import mz.co.moseguros.shared.domain.PlateParser;
import mz.co.moseguros.shared.domain.Province;
import mz.co.moseguros.shared.domain.VehicleCategory;
import mz.co.moseguros.shared.domain.VehicleUsage;
import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.error.ErrorCodes;
import mz.co.moseguros.shared.error.FieldErrorCodes;
import mz.co.moseguros.shared.error.Problems.FieldError;

/**
 * Regras do pedido de veículo (S2-03 §2). {@link #validate} junta um erro por campo, com os códigos
 * de {@link FieldErrorCodes}, e devolve os dados já normalizados.
 */
public final class VehicleRules {

    /** Fuso em que se conta o ano actual. */
    public static final ZoneId ZONE = ZoneId.of("Africa/Maputo");
    public static final int MIN_YEAR = 1900;
    public static final int TEXT_MAX = 60;
    public static final int ENGINE_CC_MAX = 30_000;
    public static final int GROSS_WEIGHT_MAX = 100_000;
    public static final long COMMERCIAL_VALUE_MAX = 100_000_000_000L;
    /** Máximo de veículos {@code ACTIVE} por cliente. */
    public static final int MAX_ACTIVE_VEHICLES = 20;
    /** Máximo de condutores na lista de um veículo. */
    public static final int MAX_DRIVERS_PER_VEHICLE = 5;

    private static final Pattern CHASSIS = Pattern.compile("^[A-Z0-9]{5,30}$");
    private static final Pattern CHASSIS_SEPARATORS = Pattern.compile("[\\s-]");

    private VehicleRules() {
    }

    /** Dados pedidos pelo cliente, ainda por validar ({@code POST}/{@code PUT}). */
    public record VehicleData(String plate, String chassisNumber, String make, String model, Integer year,
                              Integer engineCc, VehicleCategory category, Integer seats, Integer grossWeightKg,
                              VehicleUsage usage, Province provinceOfCirculation, Money commercialValue,
                              Boolean hasTracker) {
    }

    /** Dados válidos e normalizados. */
    public record ValidVehicle(Matricula plate, String chassisNumber, String make, String model, int year,
                               Integer engineCc, VehicleCategory category, int seats, Integer grossWeightKg,
                               VehicleUsage usage, Province provinceOfCirculation, Money commercialValue,
                               boolean hasTracker) {
    }

    /**
     * Valida e normaliza.
     *
     * @param now instante actual: o ano máximo é o ano de {@code now} em {@link #ZONE} mais um
     * @throws DomainException 400 {@code VALIDATION_ERROR} com um erro por campo em {@code errors}
     */
    public static ValidVehicle validate(VehicleData data, PlateParser plates, Instant now) {
        List<FieldError> errors = new ArrayList<>();

        Matricula plate = null;
        if (isBlank(data.plate())) {
            errors.add(required("plate"));
        } else {
            try {
                plate = plates.parse(data.plate());
            } catch (DomainException e) {
                errors.add(new FieldError("plate", FieldErrorCodes.INVALID_PLATE, "Plate does not match any active format"));
            }
        }

        String chassis = normalizeChassis(data.chassisNumber());
        if (chassis == null) {
            errors.add(required("chassisNumber"));
        } else if (!CHASSIS.matcher(chassis).matches()) {
            errors.add(new FieldError("chassisNumber", FieldErrorCodes.INVALID_CHASSIS,
                    "Must be 5-30 upper-case letters or digits"));
        }

        String make = text("make", data.make(), errors);
        String model = text("model", data.model(), errors);

        int maxYear = LocalDate.ofInstant(now, ZONE).getYear() + 1;
        if (data.year() == null) {
            errors.add(required("year"));
        } else if (data.year() < MIN_YEAR || data.year() > maxYear) {
            errors.add(new FieldError("year", FieldErrorCodes.INVALID_YEAR,
                    "Must be between " + MIN_YEAR + " and " + maxYear));
        }

        VehicleCategory category = data.category();
        if (category == null) {
            errors.add(required("category"));
        }

        Integer engineCc = data.engineCc();
        if (category == VehicleCategory.TRAILER) {
            if (engineCc != null) {
                errors.add(new FieldError("engineCc", FieldErrorCodes.NOT_ALLOWED, "Trailers have no engine"));
            }
        } else if (engineCc == null) {
            if (category != null) {
                errors.add(required("engineCc"));
            }
        } else if (engineCc < 1 || engineCc > ENGINE_CC_MAX) {
            errors.add(outOfRange("engineCc", 1, ENGINE_CC_MAX));
        }

        Integer seats = data.seats();
        if (seats == null) {
            errors.add(required("seats"));
        } else if (category != null) {
            int min = category == VehicleCategory.TRAILER ? 0 : 1;
            int max = switch (category) {
                case TRAILER -> 0;
                case MOTORCYCLE -> 3;
                default -> 100;
            };
            if (seats < min || seats > max) {
                errors.add(outOfRange("seats", min, max));
            }
        }

        Integer grossWeight = data.grossWeightKg();
        if (grossWeight == null) {
            if (category == VehicleCategory.HEAVY || category == VehicleCategory.TRAILER) {
                errors.add(required("grossWeightKg"));
            }
        } else if (grossWeight < 1 || grossWeight > GROSS_WEIGHT_MAX) {
            errors.add(outOfRange("grossWeightKg", 1, GROSS_WEIGHT_MAX));
        }

        if (data.usage() == null) {
            errors.add(required("usage"));
        }
        if (data.provinceOfCirculation() == null) {
            errors.add(required("provinceOfCirculation"));
        }

        // A moeda (só MZN) já é validada ao ler o pedido: Money recusa outra (INVALID_CURRENCY).
        Money value = data.commercialValue();
        if (value != null && (value.amount() <= 0 || value.amount() > COMMERCIAL_VALUE_MAX)) {
            errors.add(new FieldError("commercialValue", FieldErrorCodes.OUT_OF_RANGE,
                    "Must be between 1 and " + COMMERCIAL_VALUE_MAX + " centavos"));
        }

        if (!errors.isEmpty()) {
            throw DomainException.of(ErrorCodes.VALIDATION_ERROR, "Request validation failed")
                    .withProperty("errors", errors);
        }
        return new ValidVehicle(plate, chassis, make, model, data.year(), engineCc, category, seats, grossWeight,
                data.usage(), data.provinceOfCirculation(), value, Boolean.TRUE.equals(data.hasTracker()));
    }

    /** Um condutor da lista de um veículo ({@code PUT /me/vehicles/{id}/drivers}). */
    public record DriverAssignment(UUID driverId, Boolean habitual) {
    }

    /**
     * Valida a lista de condutores de um veículo (S2-03 §3.5): vazia é permitida; senão no máximo
     * {@value #MAX_DRIVERS_PER_VEHICLE}, sem repetidos e com exactamente um habitual. Não verifica se
     * os condutores existem.
     *
     * @throws DomainException 400 {@code VALIDATION_ERROR} com um erro por campo em {@code errors}
     */
    public static List<DriverAssignment> validateDrivers(List<DriverAssignment> drivers) {
        List<FieldError> errors = new ArrayList<>();
        if (drivers == null) {
            errors.add(required("drivers"));
        } else if (drivers.size() > MAX_DRIVERS_PER_VEHICLE) {
            errors.add(outOfRange("drivers", 0, MAX_DRIVERS_PER_VEHICLE));
        } else {
            Set<UUID> seen = new HashSet<>();
            int habitual = 0;
            for (int i = 0; i < drivers.size(); i++) {
                DriverAssignment assignment = drivers.get(i);
                UUID driverId = assignment == null ? null : assignment.driverId();
                if (driverId == null) {
                    errors.add(required("drivers[" + i + "].driverId"));
                } else if (!seen.add(driverId)) {
                    errors.add(new FieldError("drivers[" + i + "].driverId", FieldErrorCodes.DUPLICATED,
                            "Repeated driver"));
                }
                if (assignment != null && Boolean.TRUE.equals(assignment.habitual())) {
                    habitual++;
                }
            }
            if (!drivers.isEmpty() && habitual == 0) {
                errors.add(new FieldError("drivers", FieldErrorCodes.HABITUAL_DRIVER_REQUIRED,
                        "Exactly one driver must be habitual"));
            } else if (habitual > 1) {
                errors.add(new FieldError("drivers", FieldErrorCodes.MULTIPLE_HABITUAL_DRIVERS,
                        "Exactly one driver must be habitual"));
            }
        }
        if (!errors.isEmpty()) {
            throw DomainException.of(ErrorCodes.VALIDATION_ERROR, "Request validation failed")
                    .withProperty("errors", errors);
        }
        return drivers.stream()
                .map(d -> new DriverAssignment(d.driverId(), Boolean.TRUE.equals(d.habitual())))
                .toList();
    }

    /** Maiúsculas, sem espaços nem hífens; vazio → {@code null}. */
    public static String normalizeChassis(String raw) {
        if (isBlank(raw)) {
            return null;
        }
        return CHASSIS_SEPARATORS.matcher(raw).replaceAll("").toUpperCase(Locale.ROOT);
    }

    private static String text(String field, String raw, List<FieldError> errors) {
        String value = raw == null ? null : raw.trim();
        if (value == null || value.isEmpty()) {
            errors.add(required(field));
            return null;
        }
        if (value.length() > TEXT_MAX) {
            errors.add(outOfRange(field, 1, TEXT_MAX));
        }
        return value;
    }

    private static FieldError required(String field) {
        return new FieldError(field, FieldErrorCodes.REQUIRED, "Required");
    }

    private static FieldError outOfRange(String field, long min, long max) {
        return new FieldError(field, FieldErrorCodes.OUT_OF_RANGE, "Must be between " + min + " and " + max);
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
