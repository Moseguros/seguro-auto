package mz.co.moseguros.proposal.api;

import java.time.Instant;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import mz.co.moseguros.proposal.application.SignatureService;
import mz.co.moseguros.shared.domain.Channel;

/** Código de assinatura enviado e o PDF que ele assina ({@code requestProposalSignature}, S4-04 §7). */
public record SignatureRequestResponse(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "Enviar na confirmação")
        UUID challengeId,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "Canal efectivamente usado")
        Channel channel,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "+258*****4567")
        String destinationMasked,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "60")
        int resendAfterSeconds,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        Instant expiresAt,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "O canal mudou por falha do primeiro")
        boolean fallbackUsed,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED,
                description = "PDF da proposta a assinar; descarrega-se em GET /api/v1/files/{documentFileId}")
        UUID documentFileId,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "SHA-256 do PDF, hexadecimal minúsculo",
                example = "9f2c4b7e0d1a3c5e7f9b2d4a6c8e0f1b3d5a7c9e1f3b5d7a9c1e3f5b7d9a1c3e")
        String documentHash) {

    static SignatureRequestResponse from(SignatureService.SignRequest r) {
        return new SignatureRequestResponse(r.challenge().challengeId(), r.challenge().channel(),
                r.challenge().destinationMasked(), r.challenge().resendAfterSeconds(), r.challenge().expiresAt(),
                r.challenge().fallbackUsed(), r.documentFileId(), r.documentHash());
    }
}
