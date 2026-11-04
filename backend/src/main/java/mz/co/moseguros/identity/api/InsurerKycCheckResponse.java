package mz.co.moseguros.identity.api;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import mz.co.moseguros.identity.application.KycReviewService.ReviewDetails;
import mz.co.moseguros.identity.domain.IdDocType;
import mz.co.moseguros.identity.domain.KycCheck;
import mz.co.moseguros.identity.domain.KycCheckStatus;
import mz.co.moseguros.identity.domain.KycRejectionReason;

/**
 * Verificação de identidade vista pelo subscritor (S4-01 §7). {@code customer} é a cópia guardada no
 * envio (não o perfil actual) e traz o número do documento <strong>em claro</strong>, para o comparar
 * com a fotografia: só este recurso o devolve.
 */
public record InsurerKycCheckResponse(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        UUID id,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        KycCheckStatus status,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        KycCustomerResponse customer,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        KycFilesResponse files,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        Instant submittedAt,
        Instant reviewedAt,
        KycRejectionReason rejectionReasonCode,
        String rejectionReason,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED,
                description = "Outras verificações do mesmo cliente enviadas a esta seguradora, da mais recente para a "
                        + "mais antiga")
        List<KycPreviousCheckResponse> previousChecks,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "Enviar em verify/reject")
        long version) {

    static InsurerKycCheckResponse from(ReviewDetails details) {
        KycCheck check = details.check();
        return new InsurerKycCheckResponse(check.getId(), check.getStatus(),
                new KycCustomerResponse(check.getUserId(), check.getFullName(), check.getBirthDate(),
                        check.getIdDocType(), check.getIdDocNumber(),
                        details.msisdn() == null ? null : details.msisdn().masked()),
                new KycFilesResponse(check.getDocFrontFileId(), check.getDocBackFileId(), check.getSelfieFileId()),
                check.getSubmittedAt(), check.getReviewedAt(), check.getRejectionReasonCode(),
                check.getRejectionReason(),
                details.previous().stream().map(KycPreviousCheckResponse::from).toList(),
                check.getVersion());
    }

    /** Dados do cliente copiados do perfil no envio. */
    public record KycCustomerResponse(
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            UUID userId,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "Ana Maria Mussa")
            String fullName,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "1990-05-14")
            LocalDate birthDate,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            IdDocType idDocType,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "110100123456A",
                    description = "Em claro, para comparar com a fotografia")
            String idDocNumber,
            @Schema(example = "+258*****4567", description = "Omitido se a conta só tem e-mail")
            String msisdnMasked) {
    }

    /** Ficheiros para descarregar em {@code GET /api/v1/files/{id}}. */
    public record KycFilesResponse(
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            UUID docFrontFileId,
            @Schema(description = "Omitido no passaporte")
            UUID docBackFileId,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            UUID selfieFileId) {
    }

    /** Verificação anterior do mesmo cliente. */
    public record KycPreviousCheckResponse(
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            UUID id,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            KycCheckStatus status,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            Instant submittedAt,
            KycRejectionReason rejectionReasonCode) {

        static KycPreviousCheckResponse from(KycCheck check) {
            return new KycPreviousCheckResponse(check.getId(), check.getStatus(), check.getSubmittedAt(),
                    check.getRejectionReasonCode());
        }
    }
}
