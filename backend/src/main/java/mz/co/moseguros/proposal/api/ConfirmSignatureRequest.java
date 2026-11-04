package mz.co.moseguros.proposal.api;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;

/** Confirmação da assinatura com o código recebido ({@code confirmProposalSignature}, S4-04 §7). */
public record ConfirmSignatureRequest(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "O challengeId do pedido em curso")
        UUID challengeId,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "123456")
        String code) {

    @Override
    public String toString() {
        return "ConfirmSignatureRequest[challengeId=" + challengeId + ", code=***]";
    }
}
