package mz.co.moseguros.identity.api;

import java.time.Instant;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import mz.co.moseguros.identity.domain.IdDocType;
import mz.co.moseguros.identity.domain.KycCheck;
import mz.co.moseguros.identity.domain.KycCheckStatus;
import mz.co.moseguros.identity.domain.KycRejectionReason;

/**
 * Verificação de identidade vista pelo cliente. Sem os ids dos ficheiros (o cliente não precisa de
 * os ver de novo) e com o documento mascarado.
 */
public record KycCheckResponse(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        UUID id,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "Seguradora a quem foi enviada")
        UUID insurerId,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        KycCheckStatus status,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        IdDocType idDocType,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED,
                description = "Todos os caracteres menos os últimos 4 trocados por *", example = "*********345A")
        String idDocNumberMasked,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        Instant submittedAt,
        @Schema(description = "Omitido enquanto PENDING")
        Instant reviewedAt,
        @Schema(description = "Só em REJECTED")
        KycRejectionReason rejectionReasonCode,
        @Schema(description = "Texto livre do subscritor (só em REJECTED, se o escreveu)",
                example = "A fotografia da frente está desfocada.")
        String rejectionReason) {

    static KycCheckResponse from(KycCheck check) {
        return new KycCheckResponse(check.getId(), check.getInsurerId(), check.getStatus(), check.getIdDocType(),
                check.idDocNumberMasked(), check.getSubmittedAt(), check.getReviewedAt(),
                check.getRejectionReasonCode(), check.getRejectionReason());
    }
}
