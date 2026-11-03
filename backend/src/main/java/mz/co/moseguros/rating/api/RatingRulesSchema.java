package mz.co.moseguros.rating.api;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.media.ObjectSchema;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Esquema OpenAPI da tarifa ({@code RatingRules}): um objecto JSON no formato
 * {@code schemas/rating-v1.json} (JSON Schema 2020-12, que o OpenAPI 3.0 não exprime). O
 * {@code JsonNode} do Jackson não tem esquema próprio, e sem isto o springdoc juntaria todos os
 * {@code JsonNode} do contrato num só.
 */
@Configuration(proxyBeanMethods = false)
class RatingRulesSchema {

    static final String NAME = "RatingRules";
    static final String REF = "#/components/schemas/" + NAME;

    @Bean
    OpenApiCustomizer ratingRulesSchemaCustomizer() {
        return openApi -> {
            if (openApi.getComponents() == null) {
                openApi.setComponents(new Components());
            }
            ObjectSchema rating = new ObjectSchema();
            rating.setDescription("Tarifa interna no formato rating-v1 (backend/src/main/resources/schemas/"
                    + "rating-v1.json): schemaVersion, durationFactors e coverages (base e factores). Decimais em "
                    + "texto, dinheiro em centavos.");
            openApi.getComponents().addSchemas(NAME, rating);
        };
    }
}
