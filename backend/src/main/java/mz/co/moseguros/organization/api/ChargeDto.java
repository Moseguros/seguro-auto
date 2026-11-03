package mz.co.moseguros.organization.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import io.swagger.v3.oas.annotations.media.Schema;
import mz.co.moseguros.organization.ChargeRule;
import mz.co.moseguros.organization.ChargeType;

/**
 * Encargo da seguradora ({@code schemas/charges-v1.json}).
 *
 * @param value {@code PERCENT}: percentagem em texto ({@code "2.5"} = 2,5 %); {@code FIXED}:
 *              centavos (inteiro)
 */
@Schema(name = "Charge", description = "Encargo (imposto ou taxa) que a seguradora acrescenta ao prémio comercial")
public record ChargeDto(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, pattern = "^[A-Z][A-Z0-9_]{1,29}$",
                example = "DEMO_STAMP")
        String code,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, minLength = 1, maxLength = 80,
                example = "Imposto de selo (provisório)")
        String labelPt,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, minLength = 1, maxLength = 80,
                example = "Stamp duty (provisional)")
        String labelEn,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        ChargeType type,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, ref = ChargeValueSchema.REF)
        JsonNode value) {

    static ChargeDto from(ChargeRule rule) {
        JsonNode value = rule.type() == ChargeType.PERCENT
                ? JsonNodeFactory.instance.textNode(rule.percent().toPlainString())
                : JsonNodeFactory.instance.numberNode(rule.fixedAmount());
        return new ChargeDto(rule.code(), rule.labelPt(), rule.labelEn(), rule.type(), value);
    }
}
