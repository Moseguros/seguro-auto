package mz.co.moseguros.verification.api;

import java.time.Instant;

import io.swagger.v3.oas.annotations.media.Schema;
import mz.co.moseguros.policy.CoverageType;
import mz.co.moseguros.verification.application.VerificationService.Outcome;
import mz.co.moseguros.verification.domain.SubjectType;
import mz.co.moseguros.verification.domain.VerificationResult;

/**
 * Resposta mínima da verificação pública (S6-03 §2), calculada no momento. Nunca tem dados do tomador nem
 * o número da apólice ou do certificado. Em {@code NOT_FOUND} só {@code status}, {@code plateMasked} (só
 * nas consultas por matrícula) e {@code checkedAt}.
 */
@Schema(description = "Resultado da verificação pública; sem dados pessoais do tomador")
public record PublicVerificationResponse(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED) VerificationResult status,
        @Schema(description = "Matrícula com os dígitos trocados por *", example = "ABC-***-MC") String plateMasked,
        @Schema(example = "Seguradora Demo") String insurerName,
        Instant validFrom,
        @Schema(description = "Fim da cobertura (exclusivo)") Instant validTo,
        CoverageType coverageType,
        @Schema(description = "Só nas consultas por código: se o certificado mostrado ainda é o actual")
        CertificateCheckStatus certificateStatus,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED) Instant checkedAt) {

    static PublicVerificationResponse from(Outcome o) {
        return o.policy()
                .map(p -> new PublicVerificationResponse(o.status(), o.plateMasked(), o.insurerName(), p.startAt(),
                        p.endAt(), p.coverageType(), certificateStatus(o), o.checkedAt()))
                .orElseGet(() -> new PublicVerificationResponse(o.status(), o.plateMasked(), null, null, null,
                        null, null, o.checkedAt()));
    }

    static CertificateCheckStatus certificateStatus(Outcome o) {
        return o.subjectType() == SubjectType.TOKEN
                ? o.policy().map(p -> CertificateCheckStatus.of(p.certificateStatus())).orElse(null) : null;
    }
}
