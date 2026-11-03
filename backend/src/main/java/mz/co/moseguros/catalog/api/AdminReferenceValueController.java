package mz.co.moseguros.catalog.api;

import java.net.URI;
import java.util.Set;
import java.util.UUID;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import mz.co.moseguros.catalog.application.ReferenceValueService;
import mz.co.moseguros.catalog.domain.ReferenceDomain;
import mz.co.moseguros.shared.web.PageRequests;
import mz.co.moseguros.shared.web.PageResponse;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** Tabelas de referência (administração): permissão {@code reference:manage}. */
@RestController
@RequestMapping("/api/v1/admin/reference-domains/{domain}/values")
@Tag(name = CatalogTag.NAME, description = CatalogTag.DESCRIPTION)
public class AdminReferenceValueController {

    static final Set<String> SORTS = Set.of("sortOrder", "labelPt", "createdAt");

    private final ReferenceValueService service;

    public AdminReferenceValueController(ReferenceValueService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(operationId = "listAdminReferenceValues", summary = "Lista os valores de um domínio (administração)",
            description = "Permissão reference:manage. Inclui os inactivos. q procura no código e nos rótulos (sem "
                    + "diferenciar maiúsculas). Ordenação por sortOrder (defeito, asc), labelPt ou createdAt.")
    public PageResponse<ReferenceValueResponse> list(
            @Parameter(schema = @Schema(implementation = ReferenceDomain.class)) @PathVariable String domain,
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) String parentCode,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @Parameter(description = "sortOrder, labelPt ou createdAt, com ,asc ou ,desc", example = "sortOrder,asc")
            @RequestParam(required = false) String sort) {
        return PageResponse.of(service.list(domain, active, parentCode, q,
                        PageRequests.of(page, size, sort, SORTS, Sort.by(Sort.Direction.ASC, "sortOrder"))),
                ReferenceValueResponse::from);
    }

    @PostMapping
    @Operation(operationId = "createReferenceValue", summary = "Cria um valor de referência",
            description = "Permissão reference:manage. code ^[A-Z0-9_]{1,40}$ (INVALID_CODE); parentCode obrigatório "
                    + "em VEHICLE_MODEL (REQUIRED, PARENT_NOT_FOUND) e proibido nos outros (NOT_ALLOWED); código "
                    + "repetido no domínio → 409 REFERENCE_CODE_ALREADY_EXISTS.")
    public ResponseEntity<ReferenceValueResponse> create(
            @Parameter(schema = @Schema(implementation = ReferenceDomain.class)) @PathVariable String domain,
            @Valid @RequestBody CreateReferenceValueRequest request) {
        ReferenceValueResponse body = ReferenceValueResponse.from(service.create(domain, request.toData()));
        return ResponseEntity.created(URI.create("/api/v1/admin/reference-domains/" + body.domain() + "/values/"
                + body.id())).body(body);
    }

    @PutMapping("/{id}")
    @Operation(operationId = "updateReferenceValue", summary = "Altera um valor de referência",
            description = "Permissão reference:manage. O código e a marca não mudam; version diferente da gravada → "
                    + "409 CONCURRENT_MODIFICATION.")
    public ReferenceValueResponse update(
            @Parameter(schema = @Schema(implementation = ReferenceDomain.class)) @PathVariable String domain,
            @PathVariable UUID id, @Valid @RequestBody UpdateReferenceValueRequest request) {
        return ReferenceValueResponse.from(service.update(domain, id, request.toData(), request.version()));
    }

    @PostMapping("/{id}/activate")
    @Operation(operationId = "activateReferenceValue", summary = "Activa um valor de referência",
            description = "Permissão reference:manage. Já activo → 409 INVALID_STATE_TRANSITION.")
    public ReferenceValueResponse activate(
            @Parameter(schema = @Schema(implementation = ReferenceDomain.class)) @PathVariable String domain,
            @PathVariable UUID id) {
        return ReferenceValueResponse.from(service.activate(domain, id));
    }

    @PostMapping("/{id}/deactivate")
    @Operation(operationId = "deactivateReferenceValue", summary = "Desactiva um valor de referência",
            description = "Permissão reference:manage. Sai das listas públicas; os registos que já o usam não mudam. "
                    + "Já inactivo → 409 INVALID_STATE_TRANSITION.")
    public ReferenceValueResponse deactivate(
            @Parameter(schema = @Schema(implementation = ReferenceDomain.class)) @PathVariable String domain,
            @PathVariable UUID id) {
        return ReferenceValueResponse.from(service.deactivate(domain, id));
    }
}
