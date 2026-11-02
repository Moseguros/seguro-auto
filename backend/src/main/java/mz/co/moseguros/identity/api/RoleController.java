package mz.co.moseguros.identity.api;

import java.util.UUID;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import mz.co.moseguros.identity.application.InvitationService;
import mz.co.moseguros.shared.error.FieldErrorCodes;
import mz.co.moseguros.shared.error.FieldValidationException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Perfis que se podem atribuir a staff.
 */
@RestController
@RequestMapping("/api/v1/roles")
@Tag(name = IdentityTag.NAME, description = IdentityTag.DESCRIPTION)
public class RoleController {

    /** Âmbito sem organização. */
    public enum RoleScope { PLATFORM }

    private final InvitationService service;

    public RoleController(InvitationService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(operationId = "listAssignableRoles", summary = "Perfis atribuíveis",
            description = "Permissão org-user:read. Indicar organizationId ou scope=PLATFORM (um dos dois), com as "
                    + "regras de âmbito dos convites. Devolve também os perfis de gestor e os do primeiro gestor.")
    public AssignableRolesResponse list(
            @Parameter(description = "Organização (ou scope=PLATFORM)") @RequestParam(required = false)
            UUID organizationId,
            @Parameter(description = "PLATFORM (ou organizationId)") @RequestParam(required = false)
            RoleScope scope) {
        if ((organizationId == null) == (scope == null)) {
            throw new FieldValidationException(organizationId == null ? "organizationId" : "scope",
                    organizationId == null ? FieldErrorCodes.NOT_NULL : FieldErrorCodes.NOT_ALLOWED,
                    "Use either organizationId or scope=PLATFORM");
        }
        return AssignableRolesResponse.from(service.assignableRoles(organizationId));
    }
}
