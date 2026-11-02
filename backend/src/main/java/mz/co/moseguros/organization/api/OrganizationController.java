package mz.co.moseguros.organization.api;

import java.net.URI;
import java.util.Set;
import java.util.UUID;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import mz.co.moseguros.organization.OrganizationStatus;
import mz.co.moseguros.organization.OrganizationType;
import mz.co.moseguros.organization.application.OrganizationService;
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

/**
 * Organizações (plataforma): permissões {@code organization:read} e {@code organization:manage}.
 */
@RestController
@RequestMapping("/api/v1/organizations")
@Tag(name = OrganizationTag.NAME, description = OrganizationTag.DESCRIPTION)
public class OrganizationController {

    static final Set<String> SORTS = Set.of("createdAt", "legalName");

    private final OrganizationService service;

    public OrganizationController(OrganizationService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(operationId = "createOrganization", summary = "Cria uma organização",
            description = "Permissão organization:manage. O primeiro gestor é convidado a seguir com "
                    + "inviteOrganizationUser. insurerProfile é obrigatório em INSURER e proibido nos outros tipos.")
    public ResponseEntity<OrganizationResponse> create(@Valid @RequestBody CreateOrganizationRequest request) {
        OrganizationResponse body = OrganizationResponse.from(service.create(request.type(), request.toData()));
        return ResponseEntity.created(URI.create("/api/v1/organizations/" + body.id())).body(body);
    }

    @GetMapping
    @Operation(operationId = "listOrganizations", summary = "Lista as organizações",
            description = "Permissão organization:read. q procura na denominação e no nome comercial (sem "
                    + "diferenciar maiúsculas) ou no NUIT exacto. Ordenação por createdAt ou legalName.")
    public PageResponse<OrganizationSummaryResponse> list(
            @RequestParam(required = false) OrganizationType type,
            @RequestParam(required = false) OrganizationStatus status,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @Parameter(description = "createdAt ou legalName, com ,asc ou ,desc", example = "createdAt,desc")
            @RequestParam(required = false) String sort) {
        return PageResponse.of(service.list(type, status, q,
                        PageRequests.of(page, size, sort, SORTS, Sort.by(Sort.Direction.DESC, "createdAt"))),
                OrganizationSummaryResponse::from);
    }

    @GetMapping("/{id}")
    @Operation(operationId = "getOrganization", summary = "Consulta uma organização",
            description = "Permissão organization:read. Não existe → 404 ORGANIZATION_NOT_FOUND.")
    public OrganizationResponse get(@PathVariable UUID id) {
        return OrganizationResponse.from(service.get(id));
    }

    @PutMapping("/{id}")
    @Operation(operationId = "updateOrganization", summary = "Altera uma organização",
            description = "Permissão organization:manage. O tipo não muda; version diferente da gravada → 409 "
                    + "CONCURRENT_MODIFICATION.")
    public OrganizationResponse update(@PathVariable UUID id, @Valid @RequestBody UpdateOrganizationRequest request) {
        return OrganizationResponse.from(service.update(id, request.toData(), request.version()));
    }

    @PostMapping("/{id}/suspend")
    @Operation(operationId = "suspendOrganization", summary = "Suspende uma organização",
            description = "Permissão organization:manage. Revoga as sessões dos utilizadores da organização. Já "
                    + "suspensa → 409 INVALID_STATE_TRANSITION.")
    public OrganizationResponse suspend(@PathVariable UUID id,
                                        @Valid @RequestBody SuspendOrganizationRequest request) {
        return OrganizationResponse.from(service.suspend(id, request.reason()));
    }

    @PostMapping("/{id}/reactivate")
    @Operation(operationId = "reactivateOrganization", summary = "Reactiva uma organização suspensa",
            description = "Permissão organization:manage. Já activa → 409 INVALID_STATE_TRANSITION.")
    public OrganizationResponse reactivate(@PathVariable UUID id) {
        return OrganizationResponse.from(service.reactivate(id));
    }
}
