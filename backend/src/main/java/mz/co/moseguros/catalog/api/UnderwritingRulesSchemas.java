package mz.co.moseguros.catalog.api;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.media.ObjectSchema;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Esquema OpenAPI das regras de aceitação ({@code UnderwritingRules}): um objecto JSON no formato
 * {@code schemas/underwriting-v1.json} (JSON Schema 2020-12, que o OpenAPI 3.0 não exprime), como o
 * {@code RatingRules} do S3-01. Sem isto o springdoc juntaria todos os {@code JsonNode} do contrato.
 */
@Configuration(proxyBeanMethods = false)
class UnderwritingRulesSchemas {

    static final String RULES = "UnderwritingRules";
    static final String RULES_REF = "#/components/schemas/" + RULES;

    @Bean
    OpenApiCustomizer underwritingRulesSchemaCustomizer() {
        return openApi -> {
            if (openApi.getComponents() == null) {
                openApi.setComponents(new Components());
            }
            ObjectSchema rules = new ObjectSchema();
            rules.setDescription("Regras de aceitação no formato underwriting-v1 (backend/src/main/resources/schemas/"
                    + "underwriting-v1.json): schemaVersion = 1 e autoAccept { enabled, simplifiedKycMaxPremium, "
                    + "liability { requireAllDeclarationsNegative }, ownDamage { maxVehicleValue, "
                    + "requireAllDeclarationsNegative } }. Dinheiro em centavos.");
            openApi.getComponents().addSchemas(RULES, rules);
        };
    }
}
