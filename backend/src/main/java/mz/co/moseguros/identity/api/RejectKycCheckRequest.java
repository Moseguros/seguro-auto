package mz.co.moseguros.identity.api;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import mz.co.moseguros.identity.domain.KycRejectionReason;

/**
 * Recusa de uma identidade. {@code reasonCode} obrigatório ({@code REQUIRED}; valor desconhecido →
 * {@code INVALID_VALUE}); {@code reason} 10–500 caracteres ({@code TOO_SHORT}, {@code TOO_LONG}),
 * obrigatório com {@code OTHER} ({@code REQUIRED}).
 */
public record RejectKycCheckRequest(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, implementation = KycRejectionReason.class)
        String reasonCode,
        @Schema(minLength = 10, maxLength = 500, example = "O nome no documento é diferente do perfil.")
        String reason,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull
        Long version) {
}
