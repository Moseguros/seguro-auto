package mz.co.moseguros.proposal.api;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.media.ObjectSchema;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Esquemas OpenAPI dos blocos da proposta que as tarefas seguintes preenchem ({@code signature},
 * S4-04; {@code decision}, S4-05): nesta fase vêm sempre a {@code null}. Cada um tem o seu nome, para o
 * springdoc não os juntar com os outros {@code JsonNode} do contrato.
 */
@Configuration(proxyBeanMethods = false)
class ProposalSchemas {

    static final String SIGNATURE = "ProposalSignature";
    static final String SIGNATURE_REF = "#/components/schemas/" + SIGNATURE;
    static final String DECISION = "ProposalDecision";
    static final String DECISION_REF = "#/components/schemas/" + DECISION;

    @Bean
    OpenApiCustomizer proposalSchemasCustomizer() {
        return openApi -> {
            if (openApi.getComponents() == null) {
                openApi.setComponents(new Components());
            }
            ObjectSchema signature = new ObjectSchema();
            signature.setDescription("Assinatura da proposta (S4-04); null antes de existir.");
            signature.setAdditionalProperties(true);
            openApi.getComponents().addSchemas(SIGNATURE, signature);

            ObjectSchema decision = new ObjectSchema();
            decision.setDescription("Decisão da seguradora (S4-05); null antes de existir.");
            decision.setAdditionalProperties(true);
            openApi.getComponents().addSchemas(DECISION, decision);
        };
    }
}
