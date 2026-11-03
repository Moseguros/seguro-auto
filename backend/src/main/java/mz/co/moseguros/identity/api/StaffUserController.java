package mz.co.moseguros.identity.api;

import java.util.Set;
import java.util.UUID;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import mz.co.moseguros.identity.application.StaffUserService;
import mz.co.moseguros.identity.domain.AccountStatus;
import mz.co.moseguros.shared.web.PageRequests;
import mz.co.moseguros.shared.web.PageResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Utilizadores de staff: da plataforma ({@code /admin}) e de uma organização. Permissões
 * {@code org-user:read} e {@code org-user:manage}; o âmbito {@code ORG} só vê a sua organização.
 */
@RestController
@RequestMapping("/api/v1")
@Tag(name = IdentityTag.NAME, description = IdentityTag.DESCRIPTION)
public class StaffUserController {

    static final Set<String> SORTS = Set.of("createdAt", "email", "displayName", "lastLoginAt");

    private final StaffUserService service;

    public StaffUserController(StaffUserService service) {
        this.service = service;
    }

    @GetMapping("/organizations/{orgId}/users")
    @Operation(operationId = "listOrganizationUsers", summary = "Lista o staff de uma organização",
            description = "Permissão org-user:read. A plataforma vê qualquer organização; o staff de uma organização "
                    + "só a sua (outra → 404 ORGANIZATION_NOT_FOUND). Filtros status e q (parte do nome ou do "
                    + "e-mail). Ordenação por createdAt, email, displayName ou lastLoginAt.")
    public PageResponse<StaffUserResponse> listOrganizationUsers(
            @PathVariable UUID orgId,
            @RequestParam(required = false) AccountStatus status,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @Parameter(example = "createdAt,desc") @RequestParam(required = false) String sort) {
        return PageResponse.of(service.listForOrganization(orgId, status, q, pageable(page, size, sort)),
                StaffUserResponse::from);
    }

    @GetMapping("/admin/users")
    @Operation(operationId = "listPlatformUsers", summary = "Lista o staff da plataforma",
            description = "Permissão org-user:read e âmbito da plataforma (senão 403 FORBIDDEN). Filtros status e q. "
                    + "Ordenação por createdAt, email, displayName ou lastLoginAt.")
    public PageResponse<StaffUserResponse> listPlatformUsers(
            @RequestParam(required = false) AccountStatus status,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @Parameter(example = "createdAt,desc") @RequestParam(required = false) String sort) {
        return PageResponse.of(service.listForPlatform(status, q, pageable(page, size, sort)),
                StaffUserResponse::from);
    }

    @GetMapping("/users/{id}")
    @Operation(operationId = "getStaffUser", summary = "Consulta um utilizador de staff",
            description = "Permissão org-user:read. Fora do âmbito → 404 USER_NOT_FOUND.")
    public StaffUserResponse getStaffUser(@PathVariable UUID id) {
        return StaffUserResponse.from(service.get(id));
    }

    @PutMapping("/users/{id}/roles")
    @Operation(operationId = "updateUserRoles", summary = "Altera os perfis de um utilizador de staff",
            description = "Permissão org-user:manage. Vale no login ou na renovação seguinte do token. O próprio → 400 "
                    + "CANNOT_MODIFY_SELF; perfil não permitido → 400 ROLE_NOT_ALLOWED_FOR_ORGANIZATION; retirar o "
                    + "último gestor activo → 400 LAST_MANAGER; conta por activar → 409 INVALID_STATE_TRANSITION.")
    public StaffUserResponse updateUserRoles(@PathVariable UUID id, @Valid @RequestBody UpdateUserRolesRequest request) {
        return StaffUserResponse.from(service.updateRoles(id, request.roleCodes(), request.version()));
    }

    @PostMapping("/users/{id}/disable")
    @Operation(operationId = "disableUser", summary = "Desactiva um utilizador de staff",
            description = "Permissão org-user:manage. ACTIVE ou LOCKED → DISABLED e revoga as sessões. O próprio → 400 "
                    + "CANNOT_MODIFY_SELF; último gestor activo → 400 LAST_MANAGER; outro estado → 409 "
                    + "INVALID_STATE_TRANSITION.")
    public StaffUserResponse disableUser(@PathVariable UUID id) {
        return StaffUserResponse.from(service.disable(id));
    }

    @PostMapping("/users/{id}/enable")
    @Operation(operationId = "enableUser", summary = "Reactiva um utilizador de staff",
            description = "Permissão org-user:manage. DISABLED → ACTIVE; outro estado → 409 INVALID_STATE_TRANSITION.")
    public StaffUserResponse enableUser(@PathVariable UUID id) {
        return StaffUserResponse.from(service.enable(id));
    }

    @PostMapping("/users/{id}/unlock")
    @Operation(operationId = "unlockUser", summary = "Desbloqueia um utilizador de staff",
            description = "Permissão org-user:manage. LOCKED → ACTIVE; outro estado → 409 INVALID_STATE_TRANSITION.")
    public StaffUserResponse unlockUser(@PathVariable UUID id) {
        return StaffUserResponse.from(service.unlock(id));
    }

    @PostMapping("/users/{id}/reset-access")
    @Operation(operationId = "resetUserAccess", summary = "Repõe o acesso de um utilizador de staff",
            description = "Permissão org-user:manage. Para quem perdeu o telemóvel do TOTP ou não consegue entrar: a "
                    + "conta volta a PENDING_ACTIVATION sem palavra-passe nem TOTP, as sessões são revogadas e é "
                    + "enviado um convite novo com os perfis actuais. Só ACTIVE ou LOCKED (senão 409 "
                    + "INVALID_STATE_TRANSITION); o próprio → 400 CANNOT_MODIFY_SELF.")
    public InvitationResponse resetUserAccess(@PathVariable UUID id) {
        return InvitationResponse.from(service.resetAccess(id));
    }

    private static Pageable pageable(Integer page, Integer size, String sort) {
        return PageRequests.of(page, size, sort, SORTS, Sort.by(Sort.Direction.DESC, "createdAt"));
    }
}
