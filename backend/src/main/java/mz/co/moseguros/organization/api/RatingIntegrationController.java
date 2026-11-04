package mz.co.moseguros.organization.api;

import java.util.UUID;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import mz.co.moseguros.organization.application.RatingIntegrationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Ligação da plataforma à tarifação de uma seguradora (S3-02 §9): permissão
 * {@code organization:manage} (administrador da plataforma). Não há segredos nestes campos: a
 * referência aponta para o cofre.
 */
@RestController
@RequestMapping("/api/v1/organizations/{id}/rating-integration")
@Tag(name = OrganizationTag.NAME, description = OrganizationTag.DESCRIPTION)
public class RatingIntegrationController {

    private final RatingIntegrationService service;

    public RatingIntegrationController(RatingIntegrationService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(operationId = "getRatingIntegration", summary = "Consulta a ligação à tarifação de uma seguradora",
            description = "Permissão organization:manage. Organização inexistente → 404 ORGANIZATION_NOT_FOUND; "
                    + "que não é seguradora → 400 ORGANIZATION_NOT_INSURER.")
    public RatingIntegrationResponse get(@PathVariable UUID id) {
        return RatingIntegrationResponse.from(service.get(id));
    }

    @PutMapping
    @Operation(operationId = "updateRatingIntegration", summary = "Altera a ligação à tarifação de uma seguradora",
            description = "Permissão organization:manage. EXTERNAL exige ratingEndpointUrl (https://, até 500 "
                    + "caracteres: INVALID_URL) e ratingSecretRef (config:<chave>: INVALID_SECRET_REF); em falta → "
                    + "REQUIRED. INTERNAL mantém os outros dois campos gravados. version diferente → 409 "
                    + "CONCURRENT_MODIFICATION. Auditado (RATING_INTEGRATION.UPDATE).")
    public RatingIntegrationResponse update(@PathVariable UUID id,
                                            @Valid @RequestBody UpdateRatingIntegrationRequest request) {
        return RatingIntegrationResponse.from(service.update(id, request.ratingMode(), request.ratingEndpointUrl(),
                request.ratingSecretRef(), request.version()));
    }
}
