package mz.co.moseguros.identity.api;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import mz.co.moseguros.identity.application.InvitationService.AssignableRoles;

/** Perfis que se podem atribuir numa organização ou na plataforma. */
public record AssignableRolesResponse(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "Perfis atribuíveis")
        List<String> roles,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED,
                description = "Perfis que fazem de alguém gestor (INSURER_ADMIN, ORG_ADMIN ou PLATFORM_ADMIN)")
        List<String> managerRoles,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED,
                description = "Perfis a dar ao primeiro gestor no ecrã de criação de organização")
        List<String> managerInviteRoles) {

    static AssignableRolesResponse from(AssignableRoles roles) {
        return new AssignableRolesResponse(roles.roles(), roles.managerRoles(), roles.managerInviteRoles());
    }
}
