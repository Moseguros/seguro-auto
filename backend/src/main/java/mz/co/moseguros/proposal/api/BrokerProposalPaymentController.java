package mz.co.moseguros.proposal.api;

import java.util.UUID;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import mz.co.moseguros.proposal.application.ProposalPaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Link de pagamento enviado pelo mediador ao cliente (S5-02 §7; {@code payment:link-for-client}). O
 * cliente entra com OTP e paga como qualquer cliente; o mediador nunca recebe dinheiro.
 */
@RestController
@RequestMapping("/api/v1/broker/proposals/{id}")
@Tag(name = ProposalTag.NAME, description = ProposalTag.DESCRIPTION)
public class BrokerProposalPaymentController {

    private final ProposalPaymentService service;

    public BrokerProposalPaymentController(ProposalPaymentService service) {
        this.service = service;
    }

    @PostMapping("/payment-link")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Operation(operationId = "sendProposalPaymentLink", summary = "Envia ao cliente o link de pagamento",
            description = "Permissão payment:link-for-client. SMS ao telemóvel do cliente com o link "
                    + "{web}/cliente/propostas/{id}/pagar?origem=mediador. Só propostas deste mediador (senão 404 "
                    + "PROPOSAL_NOT_FOUND) em ACCEPTED (409 PAYMENT_TARGET_NOT_PAYABLE); cliente sem telemóvel → 409 "
                    + "CUSTOMER_HAS_NO_MSISDN; no máximo 3 envios por proposta em 24 h (429 PAYMENT_LINK_RATE_LIMITED, "
                    + "retryAfterSeconds).")
    public PaymentLinkResponse sendProposalPaymentLink(@PathVariable UUID id) {
        ProposalPaymentService.LinkSent sent = service.sendPaymentLink(id);
        return new PaymentLinkResponse(sent.sentTo(), sent.sentAt());
    }
}
