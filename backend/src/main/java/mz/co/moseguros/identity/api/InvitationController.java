package mz.co.moseguros.identity.api;

import java.util.Set;
import java.util.UUID;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import mz.co.moseguros.identity.application.InvitationService;
import mz.co.moseguros.identity.domain.InvitationStatus;
import mz.co.moseguros.shared.web.PageRequests;
import mz.co.moseguros.shared.web.PageResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Convites de staff: da plataforma ({@code /admin}) e de uma organização. Permissões
 * {@code org-user:read} e {@code org-user:manage}; o âmbito {@code ORG} só vê a sua organização.
 */
@RestController
@RequestMapping("/api/v1")
@Tag(name = IdentityTag.NAME, description = IdentityTag.DESCRIPTION)
public class InvitationController {

    static final Set<String> SORTS = Set.of("createdAt", "expiresAt", "email");

    private final InvitationService service;

    public InvitationController(InvitationService service) {
        this.service = service;
    }

    @PostMapping("/admin/users")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(operationId = "invitePlatformUser", summary = "Convida staff da plataforma",
            description = "Permissão org-user:manage e âmbito da plataforma (senão 403 FORBIDDEN). Só perfis sem "
                    + "organização (excepto CUSTOMER). Convites pendentes para o mesmo e-mail são substituídos.")
    public InvitationResponse invitePlatformUser(@Valid @RequestBody InviteUserRequest request) {
        return InvitationResponse.from(service.invitePlatformUser(request.fullName(), request.email(),
                request.roleCodes()));
    }

    @PostMapping("/organizations/{orgId}/users")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(operationId = "inviteOrganizationUser", summary = "Convida staff de uma organização",
            description = "Permissão org-user:manage. A plataforma convida para qualquer organização; o staff de "
                    + "uma organização só para a sua (outra → 404 ORGANIZATION_NOT_FOUND). Organização suspensa → "
                    + "409 ORGANIZATION_NOT_ACTIVE. E-mail de outra conta de staff → 409 EMAIL_ALREADY_IN_USE.")
    public InvitationResponse inviteOrganizationUser(@PathVariable UUID orgId,
                                                     @Valid @RequestBody InviteUserRequest request) {
        return InvitationResponse.from(service.inviteOrganizationUser(orgId, request.fullName(), request.email(),
                request.roleCodes()));
    }

    @GetMapping("/organizations/{orgId}/invitations")
    @Operation(operationId = "listOrganizationInvitations", summary = "Lista os convites de uma organização",
            description = "Permissão org-user:read. Ordenação por createdAt, expiresAt ou email.")
    public PageResponse<InvitationResponse> listOrganizationInvitations(
            @PathVariable UUID orgId,
            @RequestParam(required = false) InvitationStatus status,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @Parameter(example = "createdAt,desc") @RequestParam(required = false) String sort) {
        return PageResponse.of(service.listForOrganization(orgId, status, pageable(page, size, sort)),
                InvitationResponse::from);
    }

    @GetMapping("/admin/invitations")
    @Operation(operationId = "listPlatformInvitations", summary = "Lista os convites da plataforma",
            description = "Permissão org-user:read e âmbito da plataforma. Ordenação por createdAt, expiresAt ou email.")
    public PageResponse<InvitationResponse> listPlatformInvitations(
            @RequestParam(required = false) InvitationStatus status,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @Parameter(example = "createdAt,desc") @RequestParam(required = false) String sort) {
        return PageResponse.of(service.listForPlatform(status, pageable(page, size, sort)), InvitationResponse::from);
    }

    @PostMapping("/invitations/{id}/resend")
    @Operation(operationId = "resendInvitation", summary = "Reenvia um convite",
            description = "Permissão org-user:manage. Gera um token novo (o anterior deixa de funcionar) válido por "
                    + "7 dias. Só PENDING ou EXPIRED, senão 409 INVALID_STATE_TRANSITION.")
    public InvitationResponse resend(@PathVariable UUID id) {
        return InvitationResponse.from(service.resend(id));
    }

    @PostMapping("/invitations/{id}/revoke")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(operationId = "revokeInvitation", summary = "Revoga um convite",
            description = "Permissão org-user:manage. Só PENDING ou EXPIRED, senão 409 INVALID_STATE_TRANSITION.")
    public void revoke(@PathVariable UUID id) {
        service.revoke(id);
    }

    private static Pageable pageable(Integer page, Integer size, String sort) {
        return PageRequests.of(page, size, sort, SORTS, Sort.by(Sort.Direction.DESC, "createdAt"));
    }
}
