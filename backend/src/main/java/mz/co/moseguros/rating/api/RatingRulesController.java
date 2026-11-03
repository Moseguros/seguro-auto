package mz.co.moseguros.rating.api;

import java.util.UUID;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import mz.co.moseguros.rating.application.RatingRulesService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Tarifa interna das versões de produto da seguradora do utilizador (S3-01 §5). Versão não visível
 * (de outra seguradora ou inexistente) → 404 {@code PRODUCT_VERSION_NOT_FOUND}; cliente ou
 * mediador → 403. Corpo acima de 256 KB → 400 {@code RATING_RULES_TOO_LARGE}.
 */
@RestController
@RequestMapping("/api/v1/insurer/product-versions/{id}/rating")
@Tag(name = RatingTag.NAME, description = RatingTag.DESCRIPTION)
public class RatingRulesController {

    private final RatingRulesService service;

    public RatingRulesController(RatingRulesService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(operationId = "getRatingRules", summary = "Consulta a tarifa de uma versão de produto",
            description = "Permissão rating:manage ou product:read. rating é null quando a versão ainda não tem "
                    + "tarifa; coverages são as coberturas da versão; editable = versão DRAFT.")
    public RatingRulesResponse get(@PathVariable UUID id) {
        return RatingRulesResponse.from(service.get(id));
    }

    @PutMapping
    @Operation(operationId = "updateRatingRules", summary = "Grava a tarifa de uma versão em preparação",
            description = "Permissão rating:manage. Só versões DRAFT (senão 409 PRODUCT_VERSION_NOT_EDITABLE); "
                    + "version diferente → 409 CONCURRENT_MODIFICATION. Validada por rating-v1.json e pelas regras do "
                    + "S3-01 §7: 400 VALIDATION_ERROR com errors[].field = ponteiro JSON dentro de rating. Mais de "
                    + "256 KB → 400 RATING_RULES_TOO_LARGE.")
    public RatingRulesResponse update(@PathVariable UUID id, @Valid @RequestBody UpdateRatingRulesRequest request) {
        return RatingRulesResponse.from(service.update(id, request.rating(), request.version()));
    }

    @PostMapping("/validate")
    @Operation(operationId = "validateRatingRules", summary = "Valida uma tarifa sem a gravar",
            description = "Permissão rating:manage. As mesmas validações de updateRatingRules, em qualquer estado da "
                    + "versão; todos os erros em errors[] (field = ponteiro JSON dentro de rating). Não grava.")
    public RatingRulesValidationResponse validate(@PathVariable UUID id,
                                                  @Valid @RequestBody ValidateRatingRulesRequest request) {
        return RatingRulesValidationResponse.of(service.validate(id, request.rating()));
    }
}
