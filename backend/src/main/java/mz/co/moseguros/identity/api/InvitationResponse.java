package mz.co.moseguros.identity.api;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import mz.co.moseguros.identity.application.InvitationService.InvitationView;
import mz.co.moseguros.identity.domain.InvitationStatus;

/**
 * Convite de staff.
 *
 * @param status calculado: {@code REVOKED} se revogado; {@code ACCEPTED} se activado; {@code EXPIRED}
 *               se passou a validade; senão {@code PENDING}
 */
public record InvitationResponse(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        UUID id,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        String email,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        String fullName,
        @Schema(description = "Omitido nos convites da plataforma")
        UUID organizationId,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        List<String> roleCodes,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        InvitationStatus status,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        Instant expiresAt,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        Instant createdAt) {

    static InvitationResponse from(InvitationView view) {
        return new InvitationResponse(view.id(), view.email(), view.fullName(), view.organizationId(),
                view.roleCodes(), view.status(), view.expiresAt(), view.createdAt());
    }
}
