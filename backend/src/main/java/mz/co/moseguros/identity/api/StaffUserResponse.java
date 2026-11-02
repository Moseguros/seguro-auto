package mz.co.moseguros.identity.api;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import mz.co.moseguros.identity.application.StaffUserService.StaffUserView;
import mz.co.moseguros.identity.domain.AccountStatus;

/**
 * Utilizador de staff.
 *
 * @param status {@code LOCKED} só enquanto o bloqueio não passou; depois disso, {@code ACTIVE}
 */
public record StaffUserResponse(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        UUID id,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        String email,
        @Schema(description = "Omitido se ainda não tiver nome")
        String displayName,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED,
                description = "LOCKED só enquanto o bloqueio não passou")
        AccountStatus status,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        List<String> roles,
        @Schema(description = "Omitido se nunca entrou")
        Instant lastLoginAt,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        Instant createdAt,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "Para a alteração de perfis")
        long version) {

    static StaffUserResponse from(StaffUserView view) {
        return new StaffUserResponse(view.id(), view.email(), view.displayName(), view.status(), view.roles(),
                view.lastLoginAt(), view.createdAt(), view.version());
    }
}
