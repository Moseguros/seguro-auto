package mz.co.moseguros.identity.api;

import java.util.List;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import mz.co.moseguros.identity.application.MeService.Me;
import mz.co.moseguros.organization.OrganizationType;
import mz.co.moseguros.shared.domain.Msisdn;

/**
 * Utilizador autenticado.
 *
 * @param profileComplete só para clientes; omitido para staff
 */
public record MeResponse(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        UUID id,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, allowableValues = {"CUSTOMER", "STAFF"})
        String type,
        @Schema(description = "Staff: nome da conta; cliente: nome completo do perfil")
        String displayName,
        Msisdn msisdn,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        boolean msisdnVerified,
        String email,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        boolean emailVerified,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "pt")
        String preferredLanguage,
        @Schema(description = "Organização do staff; omitida para clientes e staff da plataforma")
        MeOrganization organization,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        List<String> roles,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        List<String> permissions,
        @Schema(description = "Só clientes: o perfil tem todos os dados obrigatórios")
        Boolean profileComplete) {

    static MeResponse from(Me me) {
        MeOrganization organization = me.organization() == null ? null
                : new MeOrganization(me.organization().id(), me.organization().type(),
                        me.organization().displayName());
        return new MeResponse(me.id(), me.type().name(), me.displayName(), me.msisdn(), me.msisdnVerified(),
                me.email(), me.emailVerified(), me.preferredLanguage(), organization, me.roles(), me.permissions(),
                me.profileComplete());
    }

    /** Organização do staff. */
    public record MeOrganization(
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            UUID id,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            OrganizationType type,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            String displayName) {
    }
}
