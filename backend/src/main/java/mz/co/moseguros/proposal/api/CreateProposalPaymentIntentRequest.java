package mz.co.moseguros.proposal.api;

import io.swagger.v3.oas.annotations.media.Schema;
import mz.co.moseguros.payment.PaymentMethod;
import mz.co.moseguros.shared.domain.Msisdn;

/**
 * Corpo de {@code createProposalPaymentIntent} (S5-02 §7).
 *
 * @param payerMsisdn obrigatório para {@code MPESA} (84 ou 85); ignorado em {@code BANK_REFERENCE}
 * @param channel     {@code WEB}, {@code APP} ou {@code BROKER_LINK} (página aberta pelo link do mediador)
 */
public record CreateProposalPaymentIntentRequest(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED) PaymentMethod method,
        @Schema(type = "string", example = "841234567") Msisdn payerMsisdn,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED) PaymentChannel channel) {

    /** Canal do pedido ({@code payment_intent.initiated_via}); o USSD tem o seu caminho (S12-02). */
    @Schema(name = "ProposalPaymentChannel", enumAsRef = true)
    public enum PaymentChannel { WEB, APP, BROKER_LINK }
}
