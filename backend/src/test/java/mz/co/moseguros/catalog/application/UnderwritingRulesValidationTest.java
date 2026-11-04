package mz.co.moseguros.catalog.application;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import mz.co.moseguros.proposal.domain.UnderwritingRules;
import mz.co.moseguros.shared.error.Problems.FieldError;
import org.junit.jupiter.api.Test;

/** O schema {@code underwriting-v1.json} (S4-05 §1) e os valores por defeito. */
class UnderwritingRulesValidationTest {

    static final ObjectMapper MAPPER = new ObjectMapper();

    static ObjectNode defaults() {
        return (ObjectNode) UnderwritingRulesService.defaults();
    }

    @Test
    void defaultsAreValidAndTheSameInBothModules() {
        assertThat(UnderwritingRulesService.validate(defaults())).isEmpty();
        assertThat(UnderwritingRules.parse(defaults(), UnderwritingRules.Source.DEFAULT))
                .isEqualTo(UnderwritingRules.DEFAULTS);
        assertThat(defaults().at("/autoAccept/simplifiedKycMaxPremium/amount").asLong()).isEqualTo(1_000_000L);
        assertThat(defaults().at("/autoAccept/ownDamage/maxVehicleValue/amount").asLong()).isEqualTo(150_000_000L);
    }

    @Test
    void unknownFieldIsInvalid() {
        ObjectNode rules = defaults();
        ((ObjectNode) rules.get("autoAccept")).put("maxPremium", 10);
        List<FieldError> errors = UnderwritingRulesService.validate(rules);
        assertThat(errors).isNotEmpty();
        assertThat(errors).allSatisfy(e -> assertThat(e.code()).isEqualTo("INVALID_RULES"));
        assertThat(errors).extracting(FieldError::field).contains("rules.autoAccept");

        ObjectNode top = defaults();
        top.put("extra", true);
        assertThat(UnderwritingRulesService.validate(top)).extracting(FieldError::field).contains("rules");
    }

    @Test
    void otherSchemaVersionIsInvalid() {
        ObjectNode rules = defaults();
        rules.put("schemaVersion", 2);
        assertThat(UnderwritingRulesService.validate(rules)).extracting(FieldError::field)
                .contains("rules.schemaVersion");
    }

    @Test
    void negativeOrZeroAmountsAreInvalid() {
        ObjectNode rules = defaults();
        ((ObjectNode) rules.at("/autoAccept/simplifiedKycMaxPremium")).put("amount", -1);
        assertThat(UnderwritingRulesService.validate(rules)).extracting(FieldError::field)
                .contains("rules.autoAccept.simplifiedKycMaxPremium.amount");

        ObjectNode zero = defaults();
        ((ObjectNode) zero.at("/autoAccept/ownDamage/maxVehicleValue")).put("amount", 0);
        assertThat(UnderwritingRulesService.validate(zero)).extracting(FieldError::field)
                .contains("rules.autoAccept.ownDamage.maxVehicleValue.amount");
    }

    @Test
    void missingFieldsWrongTypesAndOtherCurrencyAreInvalid() throws Exception {
        JsonNode missing = MAPPER.readTree("{\"schemaVersion\":1,\"autoAccept\":{\"enabled\":true}}");
        assertThat(UnderwritingRulesService.validate(missing)).isNotEmpty();
        ObjectNode currency = defaults();
        ((ObjectNode) currency.at("/autoAccept/simplifiedKycMaxPremium")).put("currency", "USD");
        assertThat(UnderwritingRulesService.validate(currency)).extracting(FieldError::field)
                .contains("rules.autoAccept.simplifiedKycMaxPremium.currency");
        ObjectNode type = defaults();
        ((ObjectNode) type.get("autoAccept")).put("enabled", "yes");
        assertThat(UnderwritingRulesService.validate(type)).extracting(FieldError::field)
                .contains("rules.autoAccept.enabled");
        assertThat(UnderwritingRulesService.validate(null)).extracting(FieldError::code).containsExactly("REQUIRED");
    }

    @Test
    void pointersBecomeRequestFields() {
        assertThat(UnderwritingRulesService.field("")).isEqualTo("rules");
        assertThat(UnderwritingRulesService.field("/autoAccept/enabled")).isEqualTo("rules.autoAccept.enabled");
        assertThat(UnderwritingRulesService.field("/a/0/b")).isEqualTo("rules.a[0].b");
    }

    @Test
    void storedRulesAreReadFromTheProductOrDefaulted() throws Exception {
        assertThat(UnderwritingRules.fromProductRules(MAPPER.readTree("{\"rating\":{}}")))
                .isSameAs(UnderwritingRules.DEFAULTS);
        ObjectNode product = MAPPER.createObjectNode();
        ObjectNode rules = defaults();
        ((ObjectNode) rules.get("autoAccept")).put("enabled", false);
        product.set("underwriting", rules);
        UnderwritingRules parsed = UnderwritingRules.fromProductRules(product);
        assertThat(parsed.enabled()).isFalse();
        assertThat(parsed.source()).isEqualTo(UnderwritingRules.Source.PRODUCT);
    }
}
