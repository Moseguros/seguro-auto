package mz.co.moseguros.organization.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import mz.co.moseguros.organization.ChargeRule;
import mz.co.moseguros.organization.ChargeType;
import mz.co.moseguros.shared.error.FieldErrorCodes;
import mz.co.moseguros.shared.error.Problems.FieldError;
import mz.co.moseguros.shared.json.JsonSchemas;

/**
 * Encargos da seguradora em JSON ({@code schemas/charges-v1.json}, S3-01 §4 e §6): validação e
 * conversão em {@link ChargeRule}. Em {@code PERCENT} o {@code value} é a percentagem em texto
 * ({@code "2.5"} = 2,5 %); em {@code FIXED}, centavos.
 */
public final class InsurerCharges {

    public static final String SCHEMA = "charges-v1.json";
    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);

    private InsurerCharges() {
    }

    /**
     * Schema e depois as regras ({@code DUPLICATE_CHARGE}, {@code PERCENT} acima de 100 →
     * {@code VALUE_OUT_OF_RANGE}). {@code field} é o ponteiro JSON dentro da lista
     * ({@code /1/code}). Vazio = válido.
     */
    public static List<FieldError> validate(JsonNode charges) {
        if (charges == null || charges.isNull() || charges.isMissingNode()) {
            return List.of(new FieldError("", FieldErrorCodes.REQUIRED, "charges is required"));
        }
        List<FieldError> errors = new ArrayList<>(JsonSchemas.validate(SCHEMA, charges).stream()
                .map(v -> new FieldError(v.pointer(), FieldErrorCodes.SCHEMA_VIOLATION, v.message()))
                .toList());
        if (!errors.isEmpty()) {
            return errors;
        }
        Set<String> codes = new HashSet<>();
        for (int i = 0; i < charges.size(); i++) {
            JsonNode charge = charges.get(i);
            if (!codes.add(charge.get("code").asText())) {
                errors.add(new FieldError("/" + i + "/code", FieldErrorCodes.DUPLICATE_CHARGE, "Charge code repeated"));
            }
            if (ChargeType.PERCENT.name().equals(charge.get("type").asText())
                    && new BigDecimal(charge.get("value").asText()).compareTo(HUNDRED) > 0) {
                errors.add(new FieldError("/" + i + "/value", FieldErrorCodes.VALUE_OUT_OF_RANGE,
                        "A percentage cannot be above 100"));
            }
        }
        return errors;
    }

    /** Lista gravada (já validada) → encargos, pela mesma ordem. */
    public static List<ChargeRule> parse(JsonNode charges) {
        List<ChargeRule> rules = new ArrayList<>();
        if (charges == null || !charges.isArray()) {
            return rules;
        }
        for (JsonNode c : charges) {
            ChargeType type = ChargeType.valueOf(c.get("type").asText());
            String code = c.get("code").asText();
            String labelPt = c.path("labelPt").asText(null);
            String labelEn = c.path("labelEn").asText(null);
            rules.add(type == ChargeType.PERCENT
                    ? ChargeRule.percent(code, labelPt, labelEn, new BigDecimal(c.get("value").asText()))
                    : ChargeRule.fixed(code, labelPt, labelEn, c.get("value").asLong()));
        }
        return rules;
    }

    /** Encargos → JSON de {@code charges-v1.json}. */
    public static ArrayNode toJson(List<ChargeRule> rules) {
        ArrayNode array = JsonNodeFactory.instance.arrayNode();
        for (ChargeRule r : rules) {
            ObjectNode c = array.addObject();
            c.put("code", r.code());
            c.put("labelPt", r.labelPt());
            c.put("labelEn", r.labelEn());
            c.put("type", r.type().name());
            if (r.type() == ChargeType.PERCENT) {
                c.put("value", r.percent().toPlainString());
            } else {
                c.put("value", r.fixedAmount());
            }
        }
        return array;
    }
}
