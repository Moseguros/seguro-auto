package mz.co.moseguros.organization.api;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Motivo da suspensão. */
public record SuspendOrganizationRequest(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank @Size(max = 500)
        String reason) {
}
