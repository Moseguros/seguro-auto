package mz.co.moseguros.proposal.api;

import java.net.URI;
import java.util.UUID;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import mz.co.moseguros.payment.InitiatedVia;
import mz.co.moseguros.payment.IntentView;
import mz.co.moseguros.proposal.application.ProposalPaymentService;
import mz.co.moseguros.shared.error.FieldErrorCodes;
import mz.co.moseguros.shared.error.FieldValidationException;
import mz.co.moseguros.shared.idempotency.IdempotencyKeys;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Pagamento de uma proposta pelo cliente (S5-02 §7; {@code self:payments}): formas de pagamento da
 * seguradora e criação da intenção. O dinheiro vai directamente para a conta da seguradora.
 */
@RestController
@RequestMapping("/api/v1/proposals/{id}")
@Tag(name = ProposalTag.NAME, description = ProposalTag.DESCRIPTION)
public class ProposalPaymentController {

    private final ProposalPaymentService service;

    public ProposalPaymentController(ProposalPaymentService service) {
        this.service = service;
    }

    @GetMapping("/payment-methods")
    @Operation(operationId = "listProposalPaymentMethods", summary = "Formas de pagamento de uma proposta aceite",
            description = "Permissão self:payments. As formas de pagamento activas da seguradora, dentro dos limites "
                    + "do montante, por sortOrder, e a intenção em curso (activeIntent) para retomar. Proposta paga → "
                    + "paid = true sem métodos; fora de ACCEPTED → 409 PAYMENT_TARGET_NOT_PAYABLE; de outro cliente → "
                    + "404 PROPOSAL_NOT_FOUND.")
    public ProposalPaymentMethodsResponse listProposalPaymentMethods(@PathVariable UUID id) {
        return ProposalPaymentMethodsResponse.from(service.methods(id));
    }

    @PostMapping("/payment-intents")
    @Operation(operationId = "createProposalPaymentIntent", summary = "Paga uma proposta aceite",
            description = "Permissão self:payments. Cabeçalho Idempotency-Key obrigatório (UUID; a mesma chave devolve "
                    + "a mesma resposta durante 24 h; outro pedido com a mesma chave → 422 IDEMPOTENCY_KEY_REUSED). "
                    + "MPESA exige payerMsisdn a começar por 84 ou 85 (REQUIRED, MSISDN_NOT_MPESA); BANK_REFERENCE "
                    + "devolve já a entidade e a referência (nextAction PAY_BY_REFERENCE) e reutiliza a referência "
                    + "activa com o mesmo montante. Erros: 404 PROPOSAL_NOT_FOUND; 409 PAYMENT_TARGET_NOT_PAYABLE, "
                    + "PAYMENT_TARGET_ALREADY_PAID, PAYMENT_METHOD_NOT_AVAILABLE, PAYMENT_IN_PROGRESS (intentId).")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "202", description = "Intenção criada",
            useReturnTypeSchema = true)
    public ResponseEntity<IntentView> createProposalPaymentIntent(
            @PathVariable UUID id,
            @Parameter(in = ParameterIn.HEADER, name = IdempotencyKeys.HEADER, required = true,
                    schema = @Schema(type = "string", format = "uuid"))
            @RequestHeader(name = IdempotencyKeys.HEADER, required = false) String idempotencyKey,
            @RequestBody CreateProposalPaymentIntentRequest request) {
        if (request == null || request.method() == null) {
            throw new FieldValidationException("method", FieldErrorCodes.REQUIRED, "method is required");
        }
        if (request.channel() == null) {
            throw new FieldValidationException("channel", FieldErrorCodes.REQUIRED, "channel is required");
        }
        IntentView view = service.createIntent(id, request.method(), request.payerMsisdn(),
                InitiatedVia.valueOf(request.channel().name()), idempotencyKey);
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .location(URI.create("/api/v1/payment-intents/" + view.id()))
                .body(view);
    }
}
