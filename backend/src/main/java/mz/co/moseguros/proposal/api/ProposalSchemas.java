package mz.co.moseguros.proposal.api;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.media.ObjectSchema;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Esquema OpenAPI do bloco da proposta que a tarefa seguinte preenche ({@code decision}, S4-05): nesta
 * fase vem sempre a {@code null}. Tem o seu nome, para o springdoc não o juntar com os outros
 * {@code JsonNode} do contrato. (O bloco {@code signature} passou a ter forma própria no S4-04:
 * {@code ProposalResponse.ProposalSignature}.)
 */
@Configuration(proxyBeanMethods = false)
class ProposalSchemas {

    static final String DECISION = "ProposalDecision";
    static final String DECISION_REF = "#/components/schemas/" + DECISION;

    @Bean
    OpenApiCustomizer proposalSchemasCustomizer() {
        return openApi -> {
            if (openApi.getComponents() == null) {
                openApi.setComponents(new Components());
            }
            ObjectSchema decision = new ObjectSchema();
            decision.setDescription("Decisão da seguradora (S4-05); null antes de existir.");
            decision.setAdditionalProperties(true);
            openApi.getComponents().addSchemas(DECISION, decision);
        };
    }
}
