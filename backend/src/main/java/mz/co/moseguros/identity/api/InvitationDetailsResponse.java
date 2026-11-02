package mz.co.moseguros.identity.api;

import java.time.Instant;

import io.swagger.v3.oas.annotations.media.Schema;
import mz.co.moseguros.identity.application.ActivationService.InvitationDetails;
import mz.co.moseguros.organization.OrganizationType;

/** Convite visto pelo convidado (ecrã de activação). */
public record InvitationDetailsResponse(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        String email,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        String fullName,
        @Schema(description = "Omitida nos convites da plataforma")
        InvitationOrganization organization,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        Instant expiresAt) {

    static InvitationDetailsResponse from(InvitationDetails details) {
        InvitationOrganization organization = details.organizationName() == null ? null
                : new InvitationOrganization(details.organizationName(), details.organizationType());
        return new InvitationDetailsResponse(details.email(), details.fullName(), organization, details.expiresAt());
    }

    /** Organização do convite. */
    public record InvitationOrganization(
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            String displayName,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            OrganizationType type) {
    }
}
