package mz.co.moseguros.verification.api;

import java.time.Instant;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import mz.co.moseguros.verification.domain.CheckLog;
import mz.co.moseguros.verification.domain.SubjectType;
import mz.co.moseguros.verification.domain.VerificationResult;

/** Uma consulta do histórico do agente (S6-03 §2). */
public record PoliceCheckResponse(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED) UUID id,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED) Instant checkedAt,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED) SubjectType subjectType,
        @Schema(description = "Vazio quando um código não correspondeu a nenhuma apólice", example = "ABC-***-MC")
        String plateMasked,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED) VerificationResult result) {

    static PoliceCheckResponse from(CheckLog c) {
        return new PoliceCheckResponse(c.getId(), c.getCheckedAt(), c.getSubjectType(), c.getPlateMasked(),
                c.getResult());
    }
}
