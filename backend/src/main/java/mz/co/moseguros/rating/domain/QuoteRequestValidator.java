package mz.co.moseguros.rating.domain;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import mz.co.moseguros.catalog.CoverageCode;
import mz.co.moseguros.catalog.ProductType;
import mz.co.moseguros.rating.domain.tariff.RatedDriver;
import mz.co.moseguros.rating.domain.tariff.RatedVehicle;
import mz.co.moseguros.rating.domain.tariff.RatingInput.RequestedCoverage;
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
import mz.co.moseguros.shared.reference.ReferenceValueLookup;

/**
 * Regras do pedido de cotação (S3-02 §3), comuns ao cliente, ao mediador e ao simulador, e as
 * regras dos dados do veículo e do condutor escritos à mão (§6 e §12). Os erros de campo juntam-se
 * numa lista ({@link Errors}) e saem todos juntos em 400 {@code VALIDATION_ERROR}; o limite por
 * hora (regra 7) é do serviço, porque precisa da BD.
 */
public final class QuoteRequestValidator {

    /** Fuso em que se conta "hoje". */
    public static final ZoneId ZONE = ZoneId.of("Africa/Maputo");
    /** Durações de um produto {@code TEMPORARY}; os outros tipos só aceitam 365. */
    public static final Set<Integer> TEMPORARY_DURATIONS = Set.of(7, 14, 30, 60, 365);
    public static final int FULL_YEAR = 365;
    /** Tipos de produto que se podem cotar no S3 ({@code BORDER} e {@code FLEET} ficam de fora). */
    public static final Set<ProductType> QUOTABLE_TYPES =
            EnumSet.of(ProductType.MTPL_ONLY, ProductType.COMPREHENSIVE, ProductType.TEMPORARY);
    /** {@code OD_THEFT} exige estas três (regra fixa do catálogo do S2). */
    public static final Set<CoverageCode> THEFT_REQUIRES =
            EnumSet.of(CoverageCode.OD_COLLISION, CoverageCode.OD_FIRE, CoverageCode.OD_NATURE);

    public static final int MIN_YEAR = 1900;
    public static final int TEXT_MAX = 60;
    public static final int ENGINE_CC_MAX = 30_000;
    public static final int GROSS_WEIGHT_MAX = 100_000;
    public static final long COMMERCIAL_VALUE_MAX = 100_000_000_000L;
    public static final int MAX_AGE = 100;
    public static final int MAX_LICENCE_CATEGORIES = 12;

    private QuoteRequestValidator() {
    }

    /** Erros de campo acumulados; {@link #throwIfAny()} lança 400 {@code VALIDATION_ERROR} com todos. */
    public static final class Errors {

        private final List<FieldError> list = new ArrayList<>();

        public void add(String field, String code, String message) {
            list.add(new FieldError(field, code, message));
        }

        public void required(String field) {
            add(field, FieldErrorCodes.REQUIRED, "Required");
        }

        public List<FieldError> list() {
            return List.copyOf(list);
        }

        public boolean isEmpty() {
            return list.isEmpty();
        }

        public void throwIfAny() {
            if (!list.isEmpty()) {
                throw DomainException.of(ErrorCodes.VALIDATION_ERROR, "Request validation failed")
                        .withProperty("errors", list());
            }
        }
    }

    // --- Termos do pedido (regras 1 a 5) ---

    /**
     * Cobertura pedida.
     *
     * @param capital capital pedido ({@code null} = o menor oferecido); só conta em {@code OPTIONS}
     *                e {@code PER_PERSON}
     */
    public record CoverageInput(CoverageCode code, Money capital) {
    }

    /** Termos pedidos, ainda por validar. */
    public record TermsInput(ProductType productType, Integer durationDays, LocalDate startDate,
                             List<CoverageInput> coverages) {
    }

    /** Termos válidos, com a data de fim calculada (regra 4) e o capital em centavos. */
    public record Terms(ProductType productType, int durationDays, LocalDate startDate, LocalDate endDate,
                        List<RequestedCoverage> coverages) {

        /** Há alguma cobertura de danos próprios ({@code OD_*}). */
        public boolean hasOwnDamage() {
            return coverages.stream().anyMatch(c -> c.code().isOwnDamage());
        }
    }

    /**
     * Regras 1 a 5. Devolve {@code null} se houver erros (já acrescentados a {@code errors}).
     *
     * @param today           hoje em {@link #ZONE}
     * @param maxStartDays    janela da data de início ({@code max-start-days-ahead})
     * @param checkStartDate  {@code false} no simulador (qualquer data de início)
     */
    public static Terms validateTerms(TermsInput input, LocalDate today, int maxStartDays, boolean checkStartDate,
                                      Errors errors) {
        int before = errors.list().size();

        ProductType type = input.productType();
        boolean typeValid = false;
        if (type == null) {
            errors.required("productType");
        } else if (!QUOTABLE_TYPES.contains(type)) {
            errors.add("productType", FieldErrorCodes.NOT_ALLOWED, "BORDER and FLEET cannot be quoted yet");
        } else {
            typeValid = true;
        }

        Integer duration = input.durationDays();
        if (duration == null) {
            errors.required("durationDays");
        } else if (typeValid && !allowedDuration(type, duration)) {
            errors.add("durationDays", FieldErrorCodes.DURATION_NOT_ALLOWED,
                    type == ProductType.TEMPORARY ? "Must be 7, 14, 30, 60 or 365" : "Must be 365");
        }

        LocalDate start = input.startDate();
        if (start == null) {
            errors.required("startDate");
        } else if (checkStartDate && (start.isBefore(today) || start.isAfter(today.plusDays(maxStartDays)))) {
            errors.add("startDate", FieldErrorCodes.START_DATE_OUT_OF_RANGE,
                    "Must be between today and " + maxStartDays + " days from today (Africa/Maputo)");
        }

        List<RequestedCoverage> coverages = coverages(input.coverages(), typeValid ? type : null, errors);

        if (errors.list().size() > before) {
            return null;
        }
        return new Terms(type, duration, start, endDate(start, duration), coverages);
    }

    /** Regra 2. */
    public static boolean allowedDuration(ProductType type, int days) {
        return type == ProductType.TEMPORARY ? TEMPORARY_DURATIONS.contains(days) : days == FULL_YEAR;
    }

    /**
     * Regra 4: data de fim inclusiva. 365 dias → {@code startDate.plusYears(1).minusDays(1)}; outras
     * durações → {@code startDate.plusDays(durationDays − 1)}.
     */
    public static LocalDate endDate(LocalDate startDate, int durationDays) {
        return durationDays == FULL_YEAR
                ? startDate.plusYears(1).minusDays(1)
                : startDate.plusDays(durationDays - 1L);
    }

    /** Regra 5. {@code type} {@code null} quando o tipo é inválido (não se verificam as regras por tipo). */
    private static List<RequestedCoverage> coverages(List<CoverageInput> input, ProductType type, Errors errors) {
        List<RequestedCoverage> result = new ArrayList<>();
        if (input == null) {
            return result;
        }
        if (input.size() > CoverageCode.values().length) {
            errors.add("coverages", FieldErrorCodes.OUT_OF_RANGE,
                    "At most " + CoverageCode.values().length + " coverages");
            return result;
        }
        Set<CoverageCode> seen = EnumSet.noneOf(CoverageCode.class);
        int theftIndex = -1;
        for (int i = 0; i < input.size(); i++) {
            CoverageInput c = input.get(i);
            String field = "coverages[" + i + "]";
            if (c == null || c.code() == null) {
                errors.required(field + ".code");
                continue;
            }
            if (!seen.add(c.code())) {
                errors.add(field + ".code", FieldErrorCodes.DUPLICATE_COVERAGE, "Coverage repeated");
                continue;
            }
            if (c.code().isOwnDamage() && type != null && type != ProductType.COMPREHENSIVE) {
                errors.add(field + ".code", FieldErrorCodes.COVERAGE_NOT_ALLOWED_FOR_TYPE,
                        "Own damage coverages are only for COMPREHENSIVE");
            }
            if (c.code() == CoverageCode.OD_THEFT) {
                theftIndex = i;
            }
            Long capital = null;
            if (c.capital() != null) {
                if (c.capital().amount() <= 0) {
                    errors.add(field + ".capital", FieldErrorCodes.OUT_OF_RANGE, "Must be above 0");
                } else {
                    capital = c.capital().amount();
                }
            }
            result.add(new RequestedCoverage(c.code(), capital));
        }
        if (type == ProductType.COMPREHENSIVE && seen.stream().noneMatch(CoverageCode::isOwnDamage)) {
            errors.add("coverages", FieldErrorCodes.OWN_DAMAGE_REQUIRED, "Choose at least one own damage coverage");
        }
        if (theftIndex >= 0 && !seen.containsAll(THEFT_REQUIRES)) {
            errors.add("coverages[" + theftIndex + "].code", FieldErrorCodes.COVERAGE_REQUIRES,
                    "OD_THEFT requires OD_COLLISION, OD_FIRE and OD_NATURE");
        }
        return result;
    }

    /**
     * Regra 6: danos próprios pedidos para um veículo sem valor comercial.
     *
     * @throws DomainException 400 {@code VEHICLE_VALUE_REQUIRED}
     */
    public static void requireVehicleValue(Terms terms, Long commercialValue) {
        if (terms.hasOwnDamage() && commercialValue == null) {
            throw DomainException.of(ErrorCodes.VEHICLE_VALUE_REQUIRED,
                    "Own damage coverages need the commercial value of the vehicle");
        }
    }

    // --- Veículo e condutor escritos à mão (mediador e simulador) ---

    /** Veículo escrito à mão, ainda por validar. {@code plate} só no pedido do mediador. */
    public record VehicleInput(String plate, String make, String model, Integer year, Integer engineCc,
                               VehicleCategory category, VehicleUsage usage, Integer seats, Integer grossWeightKg,
                               Province provinceOfCirculation, Money commercialValue, Boolean hasTracker) {
    }

    /** Condutor escrito à mão, ainda por validar (sem nome nem número da carta). */
    public record DriverInput(LocalDate birthDate, LocalDate licenceIssueDate, List<String> licenceCategories) {
    }

    /**
     * Regras do veículo (S2-03 §3 com as excepções do S3-02 §6). Campos com o prefixo
     * {@code vehicle.}. Devolve {@code null} se houver erros.
     *
     * @param plates  {@code null} quando o pedido não aceita matrícula (simulador)
     * @param partial simulador: só {@code year}, {@code category}, {@code usage} e
     *                {@code provinceOfCirculation} são obrigatórios
     */
    public static RatedVehicle validateVehicle(VehicleInput input, PlateParser plates, Instant now, boolean partial,
                                               Errors errors) {
        int before = errors.list().size();
        if (input == null) {
            errors.required("vehicle");
            return null;
        }
        String plate = null;
        if (plates != null && input.plate() != null && !input.plate().isBlank()) {
            try {
                Matricula parsed = plates.parse(input.plate());
                plate = parsed.value();
            } catch (DomainException e) {
                errors.add("vehicle.plate", FieldErrorCodes.INVALID_PLATE, "Plate does not match any active format");
            }
        }
        String make = optionalText("vehicle.make", input.make(), errors);
        String model = optionalText("vehicle.model", input.model(), errors);

        int maxYear = LocalDate.ofInstant(now, ZONE).getYear() + 1;
        if (input.year() == null) {
            errors.required("vehicle.year");
        } else if (input.year() < MIN_YEAR || input.year() > maxYear) {
            errors.add("vehicle.year", FieldErrorCodes.INVALID_YEAR, "Must be between " + MIN_YEAR + " and " + maxYear);
        }

        VehicleCategory category = input.category();
        if (category == null) {
            errors.required("vehicle.category");
        }
        if (input.usage() == null) {
            errors.required("vehicle.usage");
        }
        if (input.provinceOfCirculation() == null) {
            errors.required("vehicle.provinceOfCirculation");
        }

        Integer engineCc = input.engineCc();
        if (engineCc != null) {
            if (category == VehicleCategory.TRAILER) {
                errors.add("vehicle.engineCc", FieldErrorCodes.NOT_ALLOWED, "Trailers have no engine");
            } else if (engineCc < 1 || engineCc > ENGINE_CC_MAX) {
                outOfRange(errors, "vehicle.engineCc", 1, ENGINE_CC_MAX);
            }
        }

        Integer seats = input.seats();
        if (seats == null) {
            if (!partial) {
                errors.required("vehicle.seats");
            }
        } else if (category != null) {
            int min = category == VehicleCategory.TRAILER ? 0 : 1;
            int max = switch (category) {
                case TRAILER -> 0;
                case MOTORCYCLE -> 3;
                default -> 100;
            };
            if (seats < min || seats > max) {
                outOfRange(errors, "vehicle.seats", min, max);
            }
        } else if (seats < 0 || seats > 100) {
            outOfRange(errors, "vehicle.seats", 0, 100);
        }

        Integer grossWeight = input.grossWeightKg();
        if (grossWeight == null) {
            if (!partial && (category == VehicleCategory.HEAVY || category == VehicleCategory.TRAILER)) {
                errors.required("vehicle.grossWeightKg");
            }
        } else if (grossWeight < 1 || grossWeight > GROSS_WEIGHT_MAX) {
            outOfRange(errors, "vehicle.grossWeightKg", 1, GROSS_WEIGHT_MAX);
        }

        Money value = input.commercialValue();
        if (value != null && (value.amount() <= 0 || value.amount() > COMMERCIAL_VALUE_MAX)) {
            errors.add("vehicle.commercialValue", FieldErrorCodes.OUT_OF_RANGE,
                    "Must be between 1 and " + COMMERCIAL_VALUE_MAX + " centavos");
        }

        if (errors.list().size() > before) {
            return null;
        }
        return new RatedVehicle(null, plate, make, model, input.year(), engineCc, category, input.usage(), seats,
                grossWeight, input.provinceOfCirculation(), value == null ? null : value.amount(),
                Boolean.TRUE.equals(input.hasTracker()));
    }

    /**
     * Regras do condutor (S2-03 §3 com as excepções do S3-02 §6). Campos com o prefixo
     * {@code driver.}. Devolve {@code null} se houver erros.
     *
     * @param today hoje em {@link #ZONE}
     */
    public static RatedDriver validateDriver(DriverInput input, ReferenceValueLookup references, LocalDate today,
                                             Errors errors) {
        int before = errors.list().size();
        if (input == null) {
            errors.required("driver");
            return null;
        }
        LocalDate birthDate = input.birthDate();
        boolean birthDateValid = false;
        if (birthDate == null) {
            errors.required("driver.birthDate");
        } else if (!birthDate.isBefore(today) || Period.between(birthDate, today).getYears() > MAX_AGE) {
            errors.add("driver.birthDate", FieldErrorCodes.INVALID_DATE,
                    "Must be in the past and at most " + MAX_AGE + " years ago");
        } else {
            birthDateValid = true;
        }

        LocalDate issueDate = input.licenceIssueDate();
        if (issueDate != null && (issueDate.isAfter(today) || (birthDateValid && issueDate.isBefore(birthDate)))) {
            errors.add("driver.licenceIssueDate", FieldErrorCodes.LICENCE_ISSUE_DATE_INVALID,
                    "Must be on or after the birth date and not after today");
        }

        List<String> categories = new ArrayList<>();
        List<String> raw = input.licenceCategories();
        if (raw != null) {
            if (raw.size() > MAX_LICENCE_CATEGORIES) {
                errors.add("driver.licenceCategories", FieldErrorCodes.OUT_OF_RANGE,
                        "At most " + MAX_LICENCE_CATEGORIES + " categories");
            } else {
                Set<String> seen = new HashSet<>();
                for (int i = 0; i < raw.size(); i++) {
                    String field = "driver.licenceCategories[" + i + "]";
                    String code = raw.get(i) == null ? "" : raw.get(i).trim().toUpperCase(Locale.ROOT);
                    if (!seen.add(code)) {
                        errors.add(field, FieldErrorCodes.DUPLICATED, "Repeated category");
                    } else if (code.isEmpty() || !references.isActive(ReferenceValueLookup.LICENCE_CATEGORY, code)) {
                        errors.add(field, FieldErrorCodes.LICENCE_CATEGORY_UNKNOWN, "Unknown or inactive licence category");
                    }
                    categories.add(code);
                }
            }
        }

        if (errors.list().size() > before) {
            return null;
        }
        return new RatedDriver(null, birthDate, issueDate, List.copyOf(categories));
    }

    private static String optionalText(String field, String raw, Errors errors) {
        String value = raw == null ? null : raw.trim();
        if (value == null || value.isEmpty()) {
            return null;
        }
        if (value.length() > TEXT_MAX) {
            outOfRange(errors, field, 1, TEXT_MAX);
        }
        return value;
    }

    private static void outOfRange(Errors errors, String field, long min, long max) {
        errors.add(field, FieldErrorCodes.OUT_OF_RANGE, "Must be between " + min + " and " + max);
    }
}
