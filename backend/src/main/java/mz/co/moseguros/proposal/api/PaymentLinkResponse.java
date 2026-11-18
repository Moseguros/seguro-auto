package mz.co.moseguros.proposal.api;

import java.time.Instant;

import io.swagger.v3.oas.annotations.media.Schema;

/** Resposta de {@code sendProposalPaymentLink}: o telemóvel do cliente, mascarado. */
public record PaymentLinkResponse(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "+258*****4567") String sentTo,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED) Instant sentAt) {
}
