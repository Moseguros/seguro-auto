package mz.co.moseguros.rating.domain.tariff;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import mz.co.moseguros.catalog.CapitalType;
import mz.co.moseguros.catalog.CoverageCode;
import mz.co.moseguros.catalog.ProductType;
import mz.co.moseguros.shared.error.FieldErrorCodes;
import mz.co.moseguros.shared.error.Problems.FieldError;
import mz.co.moseguros.shared.json.JsonSchemas;

/**
 * Validação de uma tarifa antes de a gravar (S3-01 §6 e §7): primeiro o JSON Schema
 * ({@code rating-v1.json}, erros {@code SCHEMA_VIOLATION} com a mensagem do validador) e, se
 * passar, as regras de negócio contra as coberturas da versão. Devolve todos os erros (não pára no
 * primeiro), com {@code field} = ponteiro JSON dentro de {@code rating}
 * (ex.: {@code /coverages/0/factors/1/rows/2/when/driverAgeYears}). Vazio = válida.
 *
 * <p>O limite de tamanho ({@link #MAX_BYTES}) é um erro de topo ({@code RATING_RULES_TOO_LARGE})
 * verificado antes de tudo com {@link #exceedsMaxSize}. Linhas que nunca se aplicam não são erro.
 */
public final class RatingRulesValidator {

    public static final String SCHEMA = "rating-v1.json";
    /** 256 KB. */
    public static final int MAX_BYTES = 262_144;

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);
    private static final int FULL_YEAR = 365;
    private static final Set<CapitalType> RATE_OF_CAPITAL_TYPES =
            EnumSet.of(CapitalType.FIXED, CapitalType.OPTIONS, CapitalType.PER_PERSON);

    private RatingRulesValidator() {
    }

    /** A tarifa serializada (UTF-8) tem mais de {@link #MAX_BYTES} bytes. */
    public static boolean exceedsMaxSize(JsonNode rating) {
        if (rating == null) {
            return false;
        }
        try {
            return MAPPER.writeValueAsString(rating).getBytes(StandardCharsets.UTF_8).length > MAX_BYTES;
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
    }

    public static List<FieldError> validate(JsonNode rating, ProductDefinition product) {
        if (rating == null || rating.isNull() || rating.isMissingNode()) {
            return List.of(new FieldError("", FieldErrorCodes.REQUIRED, "The tariff is required"));
        }
        List<FieldError> errors = new ArrayList<>(JsonSchemas.validate(SCHEMA, rating).stream()
                .map(v -> new FieldError(v.pointer(), FieldErrorCodes.SCHEMA_VIOLATION, v.message()))
                .toList());
        if (!errors.isEmpty()) {
            return errors;
        }
        RatingRules rules;
        try {
            rules = RatingRulesParser.parse(rating);
        } catch (RatingRulesParser.InvalidTariffException e) {
            return List.of(new FieldError("", FieldErrorCodes.SCHEMA_VIOLATION, e.getMessage()));
        }
        coverages(rules, product, errors);
        durations(rules, product.type(), errors);
        return errors;
    }

    private static void coverages(RatingRules rules, ProductDefinition product, List<FieldError> errors) {
        Set<CoverageCode> inTariff = EnumSet.noneOf(CoverageCode.class);
        rules.coverages().forEach(c -> inTariff.add(c.coverageCode()));
        for (CoverageDefinition c : product.coverages()) {
            if (!inTariff.contains(c.code())) {
                errors.add(new FieldError("/coverages", FieldErrorCodes.COVERAGE_TARIFF_MISSING,
                        "Missing tariff for coverage " + c.code().name()));
            }
        }

        Set<CoverageCode> seen = EnumSet.noneOf(CoverageCode.class);
        for (int i = 0; i < rules.coverages().size(); i++) {
            CoverageTariff tariff = rules.coverages().get(i);
            String p = "/coverages/" + i;
            var definition = product.coverage(tariff.coverageCode());
            if (definition.isEmpty()) {
                errors.add(new FieldError(p + "/coverageCode", FieldErrorCodes.COVERAGE_NOT_IN_PRODUCT,
                        "The product has no coverage " + tariff.coverageCode().name()));
            } else if (!seen.add(tariff.coverageCode())) {
                errors.add(new FieldError(p + "/coverageCode", FieldErrorCodes.DUPLICATE_COVERAGE,
                        "Coverage " + tariff.coverageCode().name() + " repeated"));
            }
            definition.ifPresent(d -> {
                if (!baseTypeAllowed(tariff.base().type(), d.capitalType())) {
                    errors.add(new FieldError(p + "/base/type", FieldErrorCodes.BASE_TYPE_NOT_ALLOWED,
                            tariff.base().type().name() + " is not allowed for capital type " + d.capitalType().name()));
                }
            });
            List<TariffRow> baseRows = tariff.base().rows();
            for (int r = 0; r < baseRows.size(); r++) {
                String rp = p + "/base/rows/" + r;
                if (tariff.base().type() != BaseType.AMOUNT && !inRange(baseRows.get(r).value(), BigDecimal.ONE)) {
                    errors.add(new FieldError(rp + "/value", FieldErrorCodes.VALUE_OUT_OF_RANGE,
                            "A rate must be above 0 and at most 1"));
                }
                ranges(rp, baseRows.get(r).when(), errors);
            }
            Set<String> factorCodes = new HashSet<>();
            for (int f = 0; f < tariff.factors().size(); f++) {
                FactorTable factor = tariff.factors().get(f);
                String fp = p + "/factors/" + f;
                if (FactorTable.BONUS_MALUS.equals(factor.code())) {
                    errors.add(new FieldError(fp + "/code", FieldErrorCodes.RESERVED_FACTOR_CODE,
                            "BONUS_MALUS is reserved"));
                } else if (!factorCodes.add(factor.code())) {
                    errors.add(new FieldError(fp + "/code", FieldErrorCodes.DUPLICATE_FACTOR,
                            "Factor " + factor.code() + " repeated in the coverage"));
                }
                for (int r = 0; r < factor.rows().size(); r++) {
                    String rp = fp + "/rows/" + r;
                    if (!inRange(factor.rows().get(r).value(), HUNDRED)) {
                        errors.add(new FieldError(rp + "/value", FieldErrorCodes.VALUE_OUT_OF_RANGE,
                                "A factor must be above 0 and at most 100"));
                    }
                    ranges(rp, factor.rows().get(r).when(), errors);
                }
            }
        }
    }

    private static void durations(RatingRules rules, ProductType type, List<FieldError> errors) {
        List<RatingRules.DurationFactor> durations = rules.durationFactors();
        if (type == ProductType.TEMPORARY && durations.isEmpty()) {
            errors.add(new FieldError("/durationFactors", FieldErrorCodes.DURATION_FACTORS_REQUIRED,
                    "A TEMPORARY product needs durationFactors"));
        }
        Set<Integer> days = new HashSet<>();
        for (int k = 0; k < durations.size(); k++) {
            RatingRules.DurationFactor d = durations.get(k);
            String p = "/durationFactors/" + k;
            if (!inRange(d.factor(), BigDecimal.ONE)) {
                errors.add(new FieldError(p + "/factor", FieldErrorCodes.VALUE_OUT_OF_RANGE,
                        "A duration factor must be above 0 and at most 1"));
            }
            if (!days.add(d.days())) {
                errors.add(new FieldError(p + "/days", FieldErrorCodes.DUPLICATE_DURATION,
                        "Duration " + d.days() + " repeated"));
            }
            if (d.days() == FULL_YEAR && d.factor().compareTo(BigDecimal.ONE) != 0) {
                errors.add(new FieldError(p + "/factor", FieldErrorCodes.DURATION_365_NOT_ONE,
                        "The 365-day factor must be 1"));
            }
            if (type != ProductType.TEMPORARY && d.days() != FULL_YEAR) {
                errors.add(new FieldError(p + "/days", FieldErrorCodes.DURATION_NOT_ALLOWED,
                        "Only 365 days are allowed for a " + type.name() + " product"));
            }
        }
    }

    private static void ranges(String rowPointer, Condition when, List<FieldError> errors) {
        for (Map.Entry<String, Condition.Range> e : when.ranges().entrySet()) {
            if (e.getValue().isInverted()) {
                errors.add(new FieldError(rowPointer + "/when/" + e.getKey(), FieldErrorCodes.INVALID_RANGE,
                        "gte must be lower than lt"));
            }
        }
    }

    private static boolean baseTypeAllowed(BaseType base, CapitalType capital) {
        return switch (base) {
            case AMOUNT -> true;
            case RATE_OF_VEHICLE_VALUE -> capital == CapitalType.VEHICLE_VALUE;
            case RATE_OF_CAPITAL -> RATE_OF_CAPITAL_TYPES.contains(capital);
        };
    }

    /** {@code 0 < value ≤ max}. */
    private static boolean inRange(BigDecimal value, BigDecimal max) {
        return value.signum() > 0 && value.compareTo(max) <= 0;
    }
}
