package mz.co.moseguros.rating.domain.tariff;

import static mz.co.moseguros.rating.domain.tariff.TariffFixtures.json;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.function.Consumer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import mz.co.moseguros.catalog.CoverageCode;
import mz.co.moseguros.catalog.ProductType;
import mz.co.moseguros.shared.error.FieldErrorCodes;
import mz.co.moseguros.shared.error.Problems.FieldError;
import org.junit.jupiter.api.Test;

/**
 * Validação da tarifa (S3-01 §6 e §7): para cada código de campo, um caso inválido (com o
 * ponteiro certo) e o caso válido mais próximo.
 */
class RatingRulesValidatorTest {

    private static final ProductDefinition AUTO = ReferenceData.product("DEMO-AUTO");
    private static final ProductDefinition RC = ReferenceData.product("DEMO-RC");
    private static final ProductDefinition TEMP = ReferenceData.product("DEMO-TEMP");

    private static ObjectNode dp() {
        return (ObjectNode) ReferenceData.tariff("DEMO-AUTO");
    }

    private static ObjectNode temp() {
        return (ObjectNode) ReferenceData.tariff("DEMO-TEMP");
    }

    private static ObjectNode at(JsonNode root, String pointer) {
        return (ObjectNode) root.at(pointer);
    }

    private static List<String> errors(JsonNode rating, ProductDefinition product) {
        return RatingRulesValidator.validate(rating, product).stream().map(e -> e.field() + " " + e.code()).toList();
    }

    private static List<String> errorsAfter(ObjectNode rating, ProductDefinition product, Consumer<ObjectNode> change) {
        change.accept(rating);
        return errors(rating, product);
    }

    @Test
    void theReferenceTariffsAreValid() {
        assertThat(errors(dp(), AUTO)).isEmpty();
        assertThat(errors(ReferenceData.tariff("DEMO-RC"), RC)).isEmpty();
        assertThat(errors(temp(), TEMP)).isEmpty();
    }

    @Test
    void schemaViolationsUseThePointerAndTheValidatorMessage() {
        List<FieldError> errors = RatingRulesValidator.validate(json("{'schemaVersion':'rating-v1','coverages':"
                + "[{'coverageCode':'MTPL','base':{'type':'AMOUNT','rows':[{'when':{},'value':'450000'}]}}],"
                + "'extra':1}"), RC);
        assertThat(errors).isNotEmpty().allSatisfy(e -> {
            assertThat(e.code()).isEqualTo(FieldErrorCodes.SCHEMA_VIOLATION);
            assertThat(e.message()).isNotBlank();
        });
        assertThat(errors).extracting(FieldError::field).contains("", "/coverages/0/base/rows/0/value");

        // decimais têm de ser texto; dinheiro tem de ser inteiro
        ObjectNode decimalAsNumber = dp();
        at(decimalAsNumber, "/coverages/1/base/rows/0").put("value", 0.0004);
        assertThat(errors(decimalAsNumber, AUTO)).containsExactly("/coverages/1/base/rows/0/value SCHEMA_VIOLATION");

        ObjectNode wrongVariable = dp();
        at(wrongVariable, "/coverages/0/factors/0/rows/0/when").putObject("age").put("lt", 25);
        assertThat(errors(wrongVariable, AUTO)).containsExactly("/coverages/0/factors/0/rows/0/when SCHEMA_VIOLATION");

        assertThat(errors(NullNode.getInstance(), RC)).containsExactly(" REQUIRED");
        assertThat(errors(null, RC)).containsExactly(" REQUIRED");
    }

    @Test
    void everyCoverageOfTheVersionNeedsATariff() {
        assertThat(errorsAfter(dp(), AUTO, r -> r.withArray("coverages").remove(3)))
                .containsExactly("/coverages COVERAGE_TARIFF_MISSING");
        List<FieldError> errors = RatingRulesValidator.validate(ReferenceData.tariff("DEMO-RC"), AUTO);
        assertThat(errors).extracting(FieldError::message).containsExactly("Missing tariff for coverage OD_COLLISION",
                "Missing tariff for coverage OD_FIRE", "Missing tariff for coverage OD_NATURE",
                "Missing tariff for coverage OD_THEFT");
    }

    @Test
    void aTariffForACoverageTheProductDoesNotHave() {
        assertThat(errors(dp(), RC)).containsExactly(
                "/coverages/2/coverageCode COVERAGE_NOT_IN_PRODUCT",
                "/coverages/3/coverageCode COVERAGE_NOT_IN_PRODUCT",
                "/coverages/4/coverageCode COVERAGE_NOT_IN_PRODUCT",
                "/coverages/5/coverageCode COVERAGE_NOT_IN_PRODUCT");
    }

    @Test
    void aRepeatedCoverage() {
        assertThat(errorsAfter(dp(), AUTO, r -> r.withArray("coverages").add(r.get("coverages").get(1).deepCopy())))
                .containsExactly("/coverages/6/coverageCode DUPLICATE_COVERAGE");
    }

    @Test
    void theBaseTypeMustSuitTheCapitalType() {
        // VTPL (OPTIONS) com taxa sobre o valor do veículo
        assertThat(errorsAfter(dp(), AUTO, r -> at(r, "/coverages/1/base").put("type", "RATE_OF_VEHICLE_VALUE")))
                .containsExactly("/coverages/1/base/type BASE_TYPE_NOT_ALLOWED");
        // OD_FIRE (VEHICLE_VALUE) com taxa sobre o capital
        assertThat(errorsAfter(dp(), AUTO, r -> at(r, "/coverages/3/base").put("type", "RATE_OF_CAPITAL")))
                .containsExactly("/coverages/3/base/type BASE_TYPE_NOT_ALLOWED");
        // AMOUNT serve para qualquer capital; RATE_OF_CAPITAL serve para FIXED
        assertThat(errorsAfter(dp(), AUTO, r -> {
            at(r, "/coverages/3/base").put("type", "AMOUNT");
            at(r, "/coverages/3/base/rows/0").put("value", 1000);
        })).isEmpty();
        assertThat(errorsAfter(dp(), AUTO, r -> {
            at(r, "/coverages/0/base").put("type", "RATE_OF_CAPITAL");
            r.at("/coverages/0/base/rows").forEach(row -> ((ObjectNode) row).put("value", "0.001"));
        })).isEmpty();
    }

    @Test
    void ratesFactorsAndDurationFactorsHaveLimits() {
        assertThat(errorsAfter(dp(), AUTO, r -> at(r, "/coverages/2/base/rows/1").put("value", "1.5")))
                .containsExactly("/coverages/2/base/rows/1/value VALUE_OUT_OF_RANGE");
        assertThat(errorsAfter(dp(), AUTO, r -> at(r, "/coverages/2/base/rows/1").put("value", "0")))
                .containsExactly("/coverages/2/base/rows/1/value VALUE_OUT_OF_RANGE");
        assertThat(errorsAfter(dp(), AUTO, r -> at(r, "/coverages/2/base/rows/1").put("value", "1"))).isEmpty();

        assertThat(errorsAfter(dp(), AUTO, r -> at(r, "/coverages/0/factors/1/rows/0").put("value", "100.5")))
                .containsExactly("/coverages/0/factors/1/rows/0/value VALUE_OUT_OF_RANGE");
        assertThat(errorsAfter(dp(), AUTO, r -> at(r, "/coverages/0/factors/1/rows/0").put("value", "0.000")))
                .containsExactly("/coverages/0/factors/1/rows/0/value VALUE_OUT_OF_RANGE");
        assertThat(errorsAfter(dp(), AUTO, r -> at(r, "/coverages/0/factors/1/rows/0").put("value", "100"))).isEmpty();

        assertThat(errorsAfter(temp(), TEMP, r -> at(r, "/durationFactors/1").put("factor", "1.2")))
                .containsExactly("/durationFactors/1/factor VALUE_OUT_OF_RANGE");
        assertThat(errorsAfter(temp(), TEMP, r -> at(r, "/durationFactors/1").put("factor", "0")))
                .containsExactly("/durationFactors/1/factor VALUE_OUT_OF_RANGE");
        assertThat(errorsAfter(temp(), TEMP, r -> at(r, "/durationFactors/1").put("factor", "1"))).isEmpty();
    }

    @Test
    void rangesNeedGteBelowLt() {
        assertThat(errorsAfter(dp(), AUTO, r -> at(r, "/coverages/0/factors/0/rows/1/when/driverAgeYears")
                .put("gte", 70).put("lt", 70)))
                .containsExactly("/coverages/0/factors/0/rows/1/when/driverAgeYears INVALID_RANGE");
        assertThat(errorsAfter(dp(), AUTO, r -> at(r, "/coverages/1/base/rows/0/when/capital")
                .put("gte", 600000000)))
                .containsExactly("/coverages/1/base/rows/0/when/capital INVALID_RANGE");
        assertThat(errorsAfter(dp(), AUTO, r -> at(r, "/coverages/0/factors/0/rows/1/when/driverAgeYears")
                .put("gte", 69).put("lt", 70))).isEmpty();
    }

    @Test
    void factorCodesAreUniqueAndBonusMalusIsReserved() {
        assertThat(errorsAfter(dp(), AUTO, r -> at(r, "/coverages/2/factors/2").put("code", "DRIVER_AGE")))
                .containsExactly("/coverages/2/factors/2/code DUPLICATE_FACTOR");
        assertThat(errorsAfter(dp(), AUTO, r -> at(r, "/coverages/2/factors/2").put("code", "BONUS_MALUS")))
                .containsExactly("/coverages/2/factors/2/code RESERVED_FACTOR_CODE");
        // o mesmo código noutra cobertura não é repetido
        assertThat(errorsAfter(dp(), AUTO, r -> at(r, "/coverages/5/factors/0").put("code", "DRIVER_AGE"))).isEmpty();
    }

    @Test
    void durationRules() {
        assertThat(errorsAfter(temp(), TEMP, r -> at(r, "/durationFactors/1").put("days", 7)))
                .containsExactly("/durationFactors/1/days DUPLICATE_DURATION");
        assertThat(errorsAfter(temp(), TEMP, r -> at(r, "/durationFactors/4").put("factor", "0.9")))
                .containsExactly("/durationFactors/4/factor DURATION_365_NOT_ONE");
        assertThat(errorsAfter(temp(), TEMP, r -> at(r, "/durationFactors/4").put("factor", "1.000000"))).isEmpty();
        assertThat(errorsAfter(temp(), TEMP, r -> at(r, "/durationFactors/4").put("factor", "1.0"))).isEmpty();

        assertThat(errorsAfter(temp(), TEMP, r -> r.remove("durationFactors")))
                .containsExactly("/durationFactors DURATION_FACTORS_REQUIRED");

        ObjectNode rc = (ObjectNode) ReferenceData.tariff("DEMO-RC");
        ArrayNode durations = rc.putArray("durationFactors");
        durations.addObject().put("days", 30).put("factor", "0.2");
        durations.addObject().put("days", 365).put("factor", "1");
        assertThat(errors(rc, RC)).containsExactly("/durationFactors/0/days DURATION_NOT_ALLOWED");
        durations.remove(0);
        assertThat(errors(rc, RC)).isEmpty();
    }

    @Test
    void allErrorsAreReturned() {
        ObjectNode r = dp();
        at(r, "/coverages/2/base/rows/1").put("value", "2");
        at(r, "/coverages/2/factors/2").put("code", "BONUS_MALUS");
        r.withArray("coverages").remove(5);
        assertThat(errors(r, AUTO)).containsExactlyInAnyOrder("/coverages COVERAGE_TARIFF_MISSING",
                "/coverages/2/base/rows/1/value VALUE_OUT_OF_RANGE", "/coverages/2/factors/2/code RESERVED_FACTOR_CODE");
    }

    @Test
    void theSizeLimitIs256KiB() {
        assertThat(RatingRulesValidator.exceedsMaxSize(dp())).isFalse();
        assertThat(RatingRulesValidator.exceedsMaxSize(null)).isFalse();
        ObjectNode big = dp();
        ArrayNode rows = big.withArray("coverages").get(0).withArray("factors").get(0).withArray("rows");
        StringBuilder label = new StringBuilder();
        label.append("x".repeat(80));
        while (!RatingRulesValidator.exceedsMaxSize(big)) {
            rows.addObject().put("label", label.toString()).putObject("when");
        }
        assertThat(RatingRulesValidator.exceedsMaxSize(big)).isTrue();
    }

    @Test
    void theProductDefinitionComesFromTheCatalogView() {
        assertThat(AUTO.coverage(CoverageCode.OD_THEFT)).get()
                .satisfies(c -> assertThat(c.requiresCodes()).containsExactly(CoverageCode.OD_COLLISION,
                        CoverageCode.OD_FIRE, CoverageCode.OD_NATURE));
        assertThat(TEMP.type()).isEqualTo(ProductType.TEMPORARY);
    }
}
