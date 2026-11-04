package mz.co.moseguros.rating.domain.tariff;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import mz.co.moseguros.catalog.CapitalType;
import mz.co.moseguros.catalog.CoverageCode;
import mz.co.moseguros.catalog.DeductibleType;
import mz.co.moseguros.catalog.ProductType;
import mz.co.moseguros.organization.ChargeRule;
import mz.co.moseguros.rating.domain.tariff.RatingInput.RequestedCoverage;
import mz.co.moseguros.shared.domain.Province;
import mz.co.moseguros.shared.domain.VehicleCategory;
import mz.co.moseguros.shared.domain.VehicleUsage;

/**
 * Ficheiros de {@code src/test/resources/rating/reference/} (cópias de
 * {@code docs/tasks/S3-rating-cases.md}) e conversões para os tipos do avaliador.
 */
public final class ReferenceData {

    public static final ObjectMapper MAPPER = new ObjectMapper();
    public static final Map<String, String> TARIFF_BY_PRODUCT = Map.of(
            "DEMO-RC", "tariff-rc.json",
            "DEMO-AUTO", "tariff-dp.json",
            "DEMO-TEMP", "tariff-temp.json");

    private ReferenceData() {
    }

    public static JsonNode json(String file) {
        try (InputStream in = ReferenceData.class.getResourceAsStream("/rating/reference/" + file)) {
            if (in == null) {
                throw new IllegalArgumentException("Missing reference file: " + file);
            }
            return MAPPER.readTree(in);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static JsonNode tariff(String product) {
        return json(TARIFF_BY_PRODUCT.get(product));
    }

    public static ProductDefinition product(String code) {
        for (JsonNode p : json("products.json")) {
            if (p.get("code").asText().equals(code)) {
                List<CoverageDefinition> coverages = new ArrayList<>();
                for (JsonNode c : p.get("coverages")) {
                    coverages.add(coverage(c));
                }
                return new ProductDefinition(ProductType.valueOf(p.get("type").asText()), coverages);
            }
        }
        throw new IllegalArgumentException("Unknown product: " + code);
    }

    static CoverageDefinition coverage(JsonNode c) {
        List<CoverageCode> requires = new ArrayList<>();
        c.get("requiresCodes").forEach(r -> requires.add(CoverageCode.valueOf(r.asText())));
        List<Long> options = null;
        if (c.hasNonNull("capitalOptions")) {
            options = new ArrayList<>();
            for (JsonNode o : c.get("capitalOptions")) {
                options.add(o.asLong());
            }
        }
        return new CoverageDefinition(CoverageCode.valueOf(c.get("code").asText()), c.get("mandatory").asBoolean(),
                requires, CapitalType.valueOf(c.get("capitalType").asText()), longOrNull(c, "capital"), options,
                DeductibleType.valueOf(c.get("deductibleType").asText()), longOrNull(c, "deductibleAmount"),
                c.hasNonNull("deductiblePercent") ? new BigDecimal(c.get("deductiblePercent").asText()) : null,
                longOrNull(c, "deductibleMinAmount"));
    }

    public static List<ChargeRule> charges() {
        List<ChargeRule> rules = new ArrayList<>();
        for (JsonNode c : json("charges.json")) {
            String code = c.get("code").asText();
            String pt = c.get("labelPt").asText();
            String en = c.get("labelEn").asText();
            rules.add("PERCENT".equals(c.get("type").asText())
                    ? ChargeRule.percent(code, pt, en, new BigDecimal(c.get("value").asText()))
                    : ChargeRule.fixed(code, pt, en, c.get("value").asLong()));
        }
        return rules;
    }

    /** Pedido de um caso de {@code cases.json}. */
    public static RatingInput input(JsonNode testCase) {
        ProductDefinition product = product(testCase.get("product").asText());
        JsonNode v = testCase.get("vehicle");
        RatedVehicle vehicle = new RatedVehicle(null, null, null, null, v.get("year").asInt(), null,
                VehicleCategory.valueOf(v.get("category").asText()), VehicleUsage.valueOf(v.get("usage").asText()),
                null, null, Province.valueOf(v.get("provinceOfCirculation").asText()), longOrNull(v, "commercialValue"),
                v.get("hasTracker").asBoolean());
        JsonNode d = testCase.get("driver");
        RatedDriver driver = new RatedDriver(null, LocalDate.parse(d.get("birthDate").asText()),
                LocalDate.parse(d.get("licenceIssueDate").asText()), List.of());
        List<RequestedCoverage> requested = new ArrayList<>();
        for (JsonNode r : testCase.get("requestedCoverages")) {
            requested.add(new RequestedCoverage(CoverageCode.valueOf(r.get("code").asText()), longOrNull(r, "capital")));
        }
        return new RatingInput(product.type(), requested, vehicle, driver,
                LocalDate.parse(testCase.get("startDate").asText()), testCase.get("durationDays").asInt());
    }

    static Long longOrNull(JsonNode node, String field) {
        return node.hasNonNull(field) ? node.get(field).asLong() : null;
    }
}
