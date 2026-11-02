package mz.co.moseguros.notification.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import mz.co.moseguros.notification.application.DeliveryReceiptService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Recibos de entrega enviados pelos fornecedores de SMS e WhatsApp (público; autenticado pelo
 * cabeçalho {@code X-Webhook-Secret}).
 */
@RestController
@RequestMapping("/api/v1/integrations/delivery-receipts")
@Tag(name = "Notification", description = "Envio de mensagens e recibos de entrega")
public class DeliveryReceiptController {

    static final String SECRET_HEADER = "X-Webhook-Secret";

    private final DeliveryReceiptService receipts;

    public DeliveryReceiptController(DeliveryReceiptService receipts) {
        this.receipts = receipts;
    }

    @PostMapping("/{provider}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @SecurityRequirements
    @Operation(operationId = "receiveDeliveryReceipt", summary = "Recibo de entrega de um fornecedor",
            description = "Actualiza o estado da mensagem. Segredo errado → 401 UNAUTHORIZED; fornecedor "
                    + "desconhecido → 404 NOT_FOUND; mensagem desconhecida → 204 (ignorado). Uma falha de um "
                    + "código OTP faz o backend tentar o canal seguinte.")
    public void receive(
            @Parameter(description = "Valor da coluna provider (mock-sms, mock-whatsapp)", example = "mock-sms")
            @PathVariable String provider,
            @Parameter(in = ParameterIn.HEADER, description = "Segredo partilhado com o fornecedor")
            @RequestHeader(name = SECRET_HEADER, required = false) String secret,
            @Valid @RequestBody DeliveryReceiptRequest request) {
        receipts.receive(provider, secret, request.providerMessageId(), request.status(), request.reason());
    }
}
