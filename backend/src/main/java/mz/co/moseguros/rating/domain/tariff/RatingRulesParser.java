package mz.co.moseguros.rating.domain.tariff;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import com.fasterxml.jackson.databind.JsonNode;
import mz.co.moseguros.catalog.CoverageCode;
import mz.co.moseguros.shared.domain.Province;
import mz.co.moseguros.shared.domain.VehicleCategory;
import mz.co.moseguros.shared.domain.VehicleUsage;

/**
 * Lê uma tarifa em JSON ({@code rating-v1.json}) para {@link RatingRules}. Espera um documento
 * que passou o schema: qualquer estrutura inesperada lança {@link InvalidTariffException} (na
 * avaliação é {@code TARIFF_INVALID}). Os decimais lêem-se do texto (sem {@code double}).
 */
public final class RatingRulesParser {

    private RatingRulesParser() {
    }

    /** A tarifa não tem a forma de {@code rating-v1.json}. */
    public static class InvalidTariffException extends IllegalArgumentException {

        public InvalidTariffException(String message) {
            super(message);
        }

        public InvalidTariffException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    /** @throws InvalidTariffException se o documento não tiver a forma esperada */
    public static RatingRules parse(JsonNode rating) {
        if (rating == null || !rating.isObject()) {
            throw new InvalidTariffException("The tariff must be a JSON object");
        }
        try {
            String schemaVersion = text(rating, "schemaVersion");
            if (!RatingRules.SCHEMA_VERSION.equals(schemaVersion)) {
                throw new InvalidTariffException("Unsupported schemaVersion: " + schemaVersion);
            }
            List<RatingRules.DurationFactor> durations = new ArrayList<>();
            for (JsonNode d : array(rating.get("durationFactors"), true)) {
                durations.add(new RatingRules.DurationFactor(integer(d.get("days")), decimal(d.get("factor"))));
            }
            List<CoverageTariff> coverages = new ArrayList<>();
            for (JsonNode c : array(rating.get("coverages"), false)) {
                coverages.add(coverage(c));
            }
            return new RatingRules(schemaVersion, durations, coverages);
        } catch (InvalidTariffException e) {
            throw e;
        } catch (RuntimeException e) {
            throw new InvalidTariffException("Unreadable tariff: " + e.getMessage(), e);
        }
    }

    private static CoverageTariff coverage(JsonNode c) {
        CoverageCode code = CoverageCode.valueOf(text(c, "coverageCode"));
        JsonNode base = required(c, "base");
        BaseType type = BaseType.valueOf(text(base, "type"));
        List<TariffRow> baseRows = new ArrayList<>();
        for (JsonNode row : array(base.get("rows"), false)) {
            BigDecimal value = type == BaseType.AMOUNT ? amount(row.get("value")) : decimal(row.get("value"));
            baseRows.add(new TariffRow(optionalText(row, "label"), condition(row.get("when")), value));
        }
        List<FactorTable> factors = new ArrayList<>();
        for (JsonNode f : array(c.get("factors"), true)) {
            List<TariffRow> rows = new ArrayList<>();
            for (JsonNode row : array(f.get("rows"), false)) {
                rows.add(new TariffRow(optionalText(row, "label"), condition(row.get("when")), decimal(row.get("value"))));
            }
            factors.add(new FactorTable(text(f, "code"), optionalText(f, "label"), rows));
        }
        return new CoverageTariff(code, new BaseTable(type, baseRows), factors);
    }

    static Condition condition(JsonNode when) {
        if (when == null || !when.isObject()) {
            throw new InvalidTariffException("Row without a 'when' object");
        }
        return new Condition(
                set(when.get("vehicleCategory"), VehicleCategory.class, VehicleCategory::valueOf),
                set(when.get("vehicleUsage"), VehicleUsage.class, VehicleUsage::valueOf),
                set(when.get("province"), Province.class, Province::valueOf),
                when.has("hasTracker") ? bool(when.get("hasTracker")) : null,
                intRange(when.get("vehicleAgeYears")),
                intRange(when.get("engineCc")),
                intRange(when.get("seats")),
                intRange(when.get("grossWeightKg")),
                intRange(when.get("driverAgeYears")),
                intRange(when.get("licenceYears")),
                intRange(when.get("durationDays")),
                amountRange(when.get("capital")),
                amountRange(when.get("vehicleValue")));
    }

    private static <E extends Enum<E>> Set<E> set(JsonNode node, Class<E> type, Function<String, E> valueOf) {
        if (node == null) {
            return null;
        }
        Set<E> values = EnumSet.noneOf(type);
        for (JsonNode v : array(required(node, "in"), false)) {
            if (!v.isTextual()) {
                throw new InvalidTariffException("Set values must be strings");
            }
            values.add(valueOf.apply(v.asText()));
        }
        return values;
    }

    private static IntRange intRange(JsonNode node) {
        if (node == null) {
            return null;
        }
        return new IntRange(node.has("gte") ? integer(node.get("gte")) : null,
                node.has("lt") ? integer(node.get("lt")) : null);
    }

    private static AmountRange amountRange(JsonNode node) {
        if (node == null) {
            return null;
        }
        return new AmountRange(node.has("gte") ? longValue(node.get("gte")) : null,
                node.has("lt") ? longValue(node.get("lt")) : null);
    }

    private static Iterable<JsonNode> array(JsonNode node, boolean optional) {
        if (node == null && optional) {
            return List.of();
        }
        if (node == null || !node.isArray()) {
            throw new InvalidTariffException("Expected an array");
        }
        return node;
    }

    private static JsonNode required(JsonNode parent, String field) {
        JsonNode node = parent.get(field);
        if (node == null || node.isNull()) {
            throw new InvalidTariffException("Missing field: " + field);
        }
        return node;
    }

    private static String text(JsonNode parent, String field) {
        JsonNode node = required(parent, field);
        if (!node.isTextual()) {
            throw new InvalidTariffException("Field must be a string: " + field);
        }
        return node.asText();
    }

    private static String optionalText(JsonNode parent, String field) {
        JsonNode node = parent.get(field);
        return node == null || node.isNull() ? null : node.asText();
    }

    private static boolean bool(JsonNode node) {
        if (!node.isBoolean()) {
            throw new InvalidTariffException("Expected a boolean");
        }
        return node.booleanValue();
    }

    private static int integer(JsonNode node) {
        if (node == null || !node.canConvertToExactIntegral() || !node.canConvertToInt()) {
            throw new InvalidTariffException("Expected an integer");
        }
        return node.intValue();
    }

    private static long longValue(JsonNode node) {
        if (node == null || !node.canConvertToExactIntegral() || !node.canConvertToLong()) {
            throw new InvalidTariffException("Expected an integer amount");
        }
        return node.longValue();
    }

    /** Centavos (inteiro) → {@code BigDecimal} de escala 0. */
    private static BigDecimal amount(JsonNode node) {
        return BigDecimal.valueOf(longValue(node));
    }

    /** Decimal em texto ({@code "0.025"}) → {@code BigDecimal} exacto. */
    private static BigDecimal decimal(JsonNode node) {
        if (node == null || !node.isTextual()) {
            throw new InvalidTariffException("Decimals must be strings");
        }
        return new BigDecimal(node.asText());
    }
}
