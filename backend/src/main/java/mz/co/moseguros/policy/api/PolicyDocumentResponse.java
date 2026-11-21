package mz.co.moseguros.policy.api;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import mz.co.moseguros.policy.application.PolicyQueryService.Document;

/**
 * Documento da apólice no detalhe (S6-02 §7). Descarrega-se por {@code GET /api/v1/files/{fileId}}
 * ({@code ?download=true} → {@code attachment}).
 *
 * @param fileId   ausente enquanto {@code status = PENDING}
 * @param fileName ausente só no certificado de uma apólice sem certificado válido
 */
@Schema(name = "PolicyDocument", description = "Documento da apólice; PENDING (sem fileId) enquanto não foi gerado")
public record PolicyDocumentResponse(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED) PolicyDocumentType type,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED) PolicyDocumentStatus status,
        UUID fileId,
        @Schema(example = "CT-2027-0001234.pdf") String fileName) {

    /** Tipo do documento; {@code CERTIFICATE} e {@code SCHEDULE} aparecem sempre. */
    @Schema(enumAsRef = true)
    public enum PolicyDocumentType {
        CERTIFICATE,
        SCHEDULE,
        GENERAL_CONDITIONS,
        PRE_CONTRACT_INFO,
        PROPOSAL
    }

    /** {@code PENDING} enquanto o PDF não foi gerado (só no certificado e nas condições particulares). */
    @Schema(enumAsRef = true)
    public enum PolicyDocumentStatus {
        READY,
        PENDING
    }

    static PolicyDocumentResponse from(Document d) {
        return new PolicyDocumentResponse(PolicyDocumentType.valueOf(d.type().name()),
                d.ready() ? PolicyDocumentStatus.READY : PolicyDocumentStatus.PENDING, d.fileId(), d.fileName());
    }
}
