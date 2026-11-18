package mz.co.moseguros.payment.api;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.media.ObjectSchema;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Esquemas OpenAPI dos objectos livres das formas de pagamento (S5-01 §4): os dados não secretos
 * ({@code settings}, validados pelo JSON Schema do método, que o OpenAPI 3.0 não exprime) e o
 * próprio JSON Schema ({@code settingsSchema}). Sem isto o springdoc juntaria todos os
 * {@code JsonNode} do contrato num só.
 */
@Configuration(proxyBeanMethods = false)
class PaymentMethodSchemas {

    static final String SETTINGS = "PaymentMethodSettings";
    static final String SETTINGS_REF = "#/components/schemas/" + SETTINGS;
    static final String SETTINGS_SCHEMA = "PaymentMethodSettingsSchema";
    static final String SETTINGS_SCHEMA_REF = "#/components/schemas/" + SETTINGS_SCHEMA;

    @Bean
    OpenApiCustomizer paymentMethodSchemasCustomizer() {
        return openApi -> {
            if (openApi.getComponents() == null) {
                openApi.setComponents(new Components());
            }
            ObjectSchema settings = new ObjectSchema();
            settings.setDescription("Dados não secretos da forma de pagamento, no formato do JSON Schema do método "
                    + "(backend/src/main/resources/schemas/payment-method-mpesa.json ou "
                    + "payment-method-bank_reference.json; devolvido em settingsSchema). Os segredos nunca estão aqui.");
            openApi.getComponents().addSchemas(SETTINGS, settings);
            ObjectSchema schema = new ObjectSchema();
            schema.setDescription("JSON Schema (draft 2020-12) dos settings do método; a web gera o formulário a "
                    + "partir dele.");
            openApi.getComponents().addSchemas(SETTINGS_SCHEMA, schema);
        };
    }
}
