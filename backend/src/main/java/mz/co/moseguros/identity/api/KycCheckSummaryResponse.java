package mz.co.moseguros.identity.api;

import java.time.Instant;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import mz.co.moseguros.identity.domain.IdDocType;
import mz.co.moseguros.identity.domain.KycCheck;
import mz.co.moseguros.identity.domain.KycCheckStatus;

/** Linha da fila de verificações de identidade da seguradora. */
public record KycCheckSummaryResponse(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        UUID id,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "Ana Maria Mussa",
                description = "Cópia do perfil no momento do envio")
        String fullName,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        IdDocType idDocType,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        KycCheckStatus status,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        Instant submittedAt,
        Instant reviewedAt) {

    static KycCheckSummaryResponse from(KycCheck check) {
        return new KycCheckSummaryResponse(check.getId(), check.getFullName(), check.getIdDocType(), check.getStatus(),
                check.getSubmittedAt(), check.getReviewedAt());
    }
}
