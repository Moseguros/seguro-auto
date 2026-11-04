package mz.co.moseguros.organization.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.NullNode;
import mz.co.moseguros.organization.ChargeRule;
import mz.co.moseguros.organization.ChargeType;
import org.junit.jupiter.api.Test;

/** Encargos da seguradora (S3-01 §4 e §6): schema {@code charges-v1.json} e regras. */
class InsurerChargesTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static JsonNode json(String text) throws Exception {
        return MAPPER.readTree(text.replace('\'', '"'));
    }

    private static List<String> errors(String text) throws Exception {
        return InsurerCharges.validate(json(text)).stream().map(e -> e.field() + " " + e.code()).toList();
    }

    private static final String DEMO = "[{'code':'DEMO_STAMP','labelPt':'Imposto de selo (provisório)',"
            + "'labelEn':'Stamp duty (provisional)','type':'PERCENT','value':'2.5'},"
            + "{'code':'DEMO_FEE','labelPt':'Taxa fixa (provisória)','labelEn':'Fixed fee (provisional)',"
            + "'type':'FIXED','value':5000}]";

    @Test
    void theDemoChargesAreValidAndRoundTrip() throws Exception {
        assertThat(errors(DEMO)).isEmpty();
        assertThat(errors("[]")).isEmpty();
        List<ChargeRule> rules = InsurerCharges.parse(json(DEMO));
        assertThat(rules).containsExactly(
                ChargeRule.percent("DEMO_STAMP", "Imposto de selo (provisório)", "Stamp duty (provisional)",
                        new BigDecimal("2.5")),
                ChargeRule.fixed("DEMO_FEE", "Taxa fixa (provisória)", "Fixed fee (provisional)", 5000));
        assertThat(MAPPER.readTree(InsurerCharges.toJson(rules).toString())).isEqualTo(json(DEMO));
        assertThat(InsurerCharges.parse(null)).isEmpty();
    }

    @Test
    void aRepeatedCodeIsADuplicateCharge() throws Exception {
        assertThat(errors("[{'code':'A1','labelPt':'a','labelEn':'a','type':'FIXED','value':1},"
                + "{'code':'A1','labelPt':'b','labelEn':'b','type':'PERCENT','value':'1'}]"))
                .containsExactly("/1/code DUPLICATE_CHARGE");
        assertThat(errors("[{'code':'A1','labelPt':'a','labelEn':'a','type':'FIXED','value':1},"
                + "{'code':'A2','labelPt':'b','labelEn':'b','type':'PERCENT','value':'1'}]")).isEmpty();
    }

    @Test
    void aPercentAbove100IsOutOfRange() throws Exception {
        assertThat(errors("[{'code':'A1','labelPt':'a','labelEn':'a','type':'PERCENT','value':'99.9999'}]")).isEmpty();
        // o padrão do schema só aceita até dois algarismos inteiros (≤ 99,9999): acima disso já é
        // SCHEMA_VIOLATION e a regra VALUE_OUT_OF_RANGE fica como segunda defesa (questão no S3-01)
        assertThat(errors("[{'code':'A1','labelPt':'a','labelEn':'a','type':'PERCENT','value':'100'}]"))
                .containsExactly("/0/value SCHEMA_VIOLATION");
        assertThat(errors("[{'code':'A1','labelPt':'a','labelEn':'a','type':'PERCENT','value':'100.5'}]"))
                .containsExactly("/0/value SCHEMA_VIOLATION");
    }

    @Test
    void theSchemaIsChecked() throws Exception {
        assertThat(errors("[{'code':'A1','labelPt':'a','labelEn':'a','type':'FIXED','value':'5000'}]"))
                .containsExactly("/0/value SCHEMA_VIOLATION");
        assertThat(errors("[{'code':'a1','labelPt':'a','labelEn':'a','type':'FIXED','value':1}]"))
                .containsExactly("/0/code SCHEMA_VIOLATION");
        assertThat(errors("{}")).containsExactly(" SCHEMA_VIOLATION");
        assertThat(InsurerCharges.validate(NullNode.getInstance())).extracting(e -> e.code()).containsExactly("REQUIRED");
        assertThat(InsurerCharges.parse(json(DEMO)).get(1).type()).isEqualTo(ChargeType.FIXED);
    }
}
