package mz.co.moseguros.verification.api;

import java.time.Instant;

import io.swagger.v3.oas.annotations.media.Schema;
import mz.co.moseguros.policy.CoverageType;
import mz.co.moseguros.verification.application.VerificationService.Outcome;
import mz.co.moseguros.verification.domain.VerificationResult;

/**
 * Resposta da verificação para os agentes da polícia (S6-03 §2): a pública mais o número da apólice e do
 * certificado e a marca e o modelo do veículo (para confirmar que a matrícula corresponde ao veículo).
 * Continua sem dados pessoais do tomador.
 */
@Schema(description = "Resultado da verificação para um agente da polícia; sem dados pessoais do tomador")
public record PoliceVerificationResponse(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED) VerificationResult status,
        @Schema(example = "ABC-***-MC") String plateMasked,
        String insurerName,
        Instant validFrom,
        @Schema(description = "Fim da cobertura (exclusivo)") Instant validTo,
        CoverageType coverageType,
        @Schema(description = "Só nas consultas por código: se o certificado mostrado ainda é o actual")
        CertificateCheckStatus certificateStatus,
        @Schema(example = "AP-2027-000123") String policyNumber,
        @Schema(description = "Certificado do código ou, por matrícula, o certificado em vigor")
        String certificateNumber,
        @Schema(example = "Toyota") String vehicleMake,
        @Schema(example = "Corolla") String vehicleModel,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED) Instant checkedAt) {

    static PoliceVerificationResponse from(Outcome o) {
        return o.policy()
                .map(p -> new PoliceVerificationResponse(o.status(), o.plateMasked(), o.insurerName(), p.startAt(),
                        p.endAt(), p.coverageType(), PublicVerificationResponse.certificateStatus(o), p.policyNumber(),
                        p.certificateNumber(), p.vehicleMake(), p.vehicleModel(), o.checkedAt()))
                .orElseGet(() -> new PoliceVerificationResponse(o.status(), o.plateMasked(), null, null, null, null,
                        null, null, null, null, null, o.checkedAt()));
    }
}
