package mz.co.moseguros.rating.api;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.media.ObjectSchema;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Esquemas OpenAPI dos objectos JSON livres das cotações: o {@code unavailableDetail} de um motivo
 * ({@code UnavailableDetail}) e a decomposição do simulador ({@code PremiumBreakdown}, S3-01 §10).
 * Sem isto o springdoc juntaria todos os {@code JsonNode} do contrato num só.
 */
@Configuration(proxyBeanMethods = false)
class QuoteSchemas {

    static final String UNAVAILABLE_DETAIL = "UnavailableDetail";
    static final String UNAVAILABLE_DETAIL_REF = "#/components/schemas/" + UNAVAILABLE_DETAIL;
    static final String PREMIUM_BREAKDOWN = "PremiumBreakdown";
    static final String PREMIUM_BREAKDOWN_REF = "#/components/schemas/" + PREMIUM_BREAKDOWN;

    @Bean
    OpenApiCustomizer quoteSchemasCustomizer() {
        return openApi -> {
            if (openApi.getComponents() == null) {
                openApi.setComponents(new Components());
            }
            ObjectSchema detail = new ObjectSchema();
            detail.setDescription("Pormenores do motivo de uma cotação NOT_AVAILABLE (ex.: {\"coverageCode\": "
                    + "\"OD_COLLISION\", \"table\": \"BASE\"} ou {\"declineReason\": \"…\"}). Valores em texto, "
                    + "números ou listas de texto.");
            detail.setAdditionalProperties(true);
            openApi.getComponents().addSchemas(UNAVAILABLE_DETAIL, detail);

            ObjectSchema breakdown = new ObjectSchema();
            breakdown.setDescription("Decomposição do prémio (S3-01 §10): provider, variables, durationFactor, "
                    + "coverages (capitalAmount, deductible, base, factors, bonusMalusFactor, exactPremium, "
                    + "premiumAmount), commercialPremiumAmount, charges, subtotalAmount, roundingAdjustmentAmount e "
                    + "totalAmount. Decimais em texto, centavos em inteiro.");
            breakdown.setAdditionalProperties(true);
            openApi.getComponents().addSchemas(PREMIUM_BREAKDOWN, breakdown);
        };
    }
}
