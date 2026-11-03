package mz.co.moseguros.organization.api;

import java.util.List;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Esquema OpenAPI do {@code value} de um encargo ({@code ChargeValue}): texto em {@code PERCENT}
 * ou inteiro (centavos) em {@code FIXED}. O {@code JsonNode} do Jackson não tem esquema próprio, e
 * sem isto o springdoc juntaria todos os {@code JsonNode} do contrato num só.
 */
@Configuration(proxyBeanMethods = false)
class ChargeValueSchema {

    static final String NAME = "ChargeValue";
    static final String REF = "#/components/schemas/" + NAME;

    @Bean
    OpenApiCustomizer chargeValueSchemaCustomizer() {
        return openApi -> {
            if (openApi.getComponents() == null) {
                openApi.setComponents(new Components());
            }
            Schema<Object> value = new Schema<>();
            value.setDescription("PERCENT: percentagem em texto (\"2.5\" = 2,5 %); FIXED: centavos (inteiro)");
            value.setOneOf(List.of(new StringSchema().pattern("^(0|[1-9][0-9]?)(\\.[0-9]{1,4})?$"),
                    new IntegerSchema().format("int64").minimum(java.math.BigDecimal.ZERO)
                            .maximum(java.math.BigDecimal.valueOf(100_000_000L))));
            openApi.getComponents().addSchemas(NAME, value);
        };
    }
}
