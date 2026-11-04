package mz.co.moseguros.catalog.api;

import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import mz.co.moseguros.catalog.application.UnderwritingRulesService;
import mz.co.moseguros.catalog.application.UnderwritingRulesService.UnderwritingRulesDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Regras de aceitação automática das propostas de uma versão de produto (S4-05 §2), em
 * {@code rules.underwriting}. Versão de outra seguradora ou inexistente → 404
 * {@code PRODUCT_VERSION_NOT_FOUND}.
 */
@RestController
@RequestMapping("/api/v1/insurer/product-versions/{id}/underwriting-rules")
@Tag(name = CatalogTag.NAME, description = CatalogTag.DESCRIPTION)
public class UnderwritingRulesController {

    private final UnderwritingRulesService service;

    public UnderwritingRulesController(UnderwritingRulesService service) {
        this.service = service;
    }

    /** Pedido de alteração. */
    public record UpdateUnderwritingRulesRequest(
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED, ref = UnderwritingRulesSchemas.RULES_REF)
            JsonNode rules,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "version da versão do produto")
            Long version) {
    }

    /** Regras gravadas ou, com {@code isDefault = true}, os valores por defeito. */
    public record UnderwritingRulesResponse(
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED, ref = UnderwritingRulesSchemas.RULES_REF)
            JsonNode rules,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED,
                    description = "true: a versão não tem regras próprias e valem as por defeito")
            boolean isDefault,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "version da versão do produto")
            long version) {

        static UnderwritingRulesResponse from(UnderwritingRulesDetails d) {
            return new UnderwritingRulesResponse(d.rules(), d.isDefault(), d.version());
        }
    }

    @GetMapping
    @Operation(operationId = "getUnderwritingRules", summary = "Consulta as regras de aceitação de uma versão",
            description = "Permissão product:read. Sem regras próprias devolve os valores por defeito (isDefault = "
                    + "true): aceitação automática ligada, KYC simplificado abaixo de 10 000 MT e danos próprios até "
                    + "1 500 000 MT com todas as declarações \"não\" (provisórios).")
    public UnderwritingRulesResponse getUnderwritingRules(@PathVariable UUID id) {
        return UnderwritingRulesResponse.from(service.get(id));
    }

    @PutMapping
    @Operation(operationId = "updateUnderwritingRules", summary = "Altera as regras de aceitação de uma versão",
            description = "Permissão product:manage. Só em DRAFT (senão 409 PRODUCT_VERSION_NOT_EDITABLE); version "
                    + "diferente → 409 CONCURRENT_MODIFICATION. As regras seguem o schema underwriting-v1 (campos "
                    + "desconhecidos recusados): erros → 400 VALIDATION_ERROR com errors[] = { field: rules.<caminho>, "
                    + "code: INVALID_RULES }.")
    public UnderwritingRulesResponse updateUnderwritingRules(@PathVariable UUID id,
                                                             @RequestBody UpdateUnderwritingRulesRequest request) {
        return UnderwritingRulesResponse.from(service.update(id, request.rules(), request.version()));
    }
}
