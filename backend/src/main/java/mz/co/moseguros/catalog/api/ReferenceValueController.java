package mz.co.moseguros.catalog.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import mz.co.moseguros.catalog.application.ReferenceValueService;
import mz.co.moseguros.catalog.domain.ReferenceDomain;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** Tabelas de referência: lista pública (sem token) para os ecrãs. */
@RestController
@Tag(name = CatalogTag.NAME, description = CatalogTag.DESCRIPTION)
public class ReferenceValueController {

    private final ReferenceValueService service;

    public ReferenceValueController(ReferenceValueService service) {
        this.service = service;
    }

    @GetMapping("/api/v1/reference-domains/{domain}/values")
    @Operation(operationId = "listReferenceValues", summary = "Lista os valores activos de um domínio",
            description = "Público. Lista completa, não paginada, por sortOrder e labelPt. Em VEHICLE_MODEL exclui "
                    + "os modelos de marcas inactivas; parentCode filtra pela marca. Domínio desconhecido → 404 "
                    + "REFERENCE_DOMAIN_UNKNOWN.")
    public PublicReferenceValuesResponse list(
            @Parameter(schema = @Schema(implementation = ReferenceDomain.class)) @PathVariable String domain,
            @Parameter(description = "Só os valores com este pai (código da marca)", example = "TOYOTA")
            @RequestParam(required = false) String parentCode) {
        return new PublicReferenceValuesResponse(service.listPublic(domain, parentCode).stream()
                .map(PublicReferenceValueResponse::from)
                .toList());
    }
}
