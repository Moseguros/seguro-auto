package mz.co.moseguros.payment.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import mz.co.moseguros.payment.application.PaymentMethodConfigService;
import mz.co.moseguros.payment.application.PaymentMethodConfigService.TestOutcome;
import mz.co.moseguros.payment.application.PaymentMethodConfigService.UpdateCommand;
import mz.co.moseguros.shared.domain.Money;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Formas de pagamento da seguradora do utilizador (S5-01 §5): M-Pesa e entidade e referência
 * (ADR-007). Permissão {@code payment:configure} e staff de uma seguradora; a seguradora é sempre a
 * do token. Os segredos são enviados uma vez e nunca devolvidos.
 */
@RestController
@RequestMapping("/api/v1/insurer/payment-methods")
@Tag(name = PaymentTag.NAME, description = PaymentTag.DESCRIPTION)
public class InsurerPaymentMethodController {

    private static final String METHOD_DESCRIPTION = "MPESA ou BANK_REFERENCE (outro → 404 PAYMENT_METHOD_NOT_FOUND)";

    private final PaymentMethodConfigService service;

    public InsurerPaymentMethodController(PaymentMethodConfigService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(operationId = "listInsurerPaymentMethods", summary = "Lista as formas de pagamento da seguradora",
            description = "Permissão payment:configure, staff de uma seguradora (outro âmbito → 403 FORBIDDEN). Sempre "
                    + "os dois métodos, pela ordem MPESA, BANK_REFERENCE; os que ainda não foram gravados aparecem com "
                    + "configured = false e os valores por defeito. Nunca devolve valores secretos.")
    public PaymentMethodsResponse list() {
        return new PaymentMethodsResponse(service.list().stream().map(PaymentMethodResponse::from).toList());
    }

    @PutMapping("/{method}")
    @Operation(operationId = "updateInsurerPaymentMethod", summary = "Grava os dados de uma forma de pagamento",
            description = "Permissão payment:configure. Cria a linha se não existir (desactivada). settings é validado "
                    + "pelo JSON Schema do método sem exigir os obrigatórios (só ao activar) e pelas regras da ficha "
                    + "S5-01 §4 (400 VALIDATION_ERROR com errors[].field = settings.<caminho>, secrets.<campo>, "
                    + "maxAmount…). Os segredos vão só para o cofre (cofre indisponível → 503 SECRET_STORE_UNAVAILABLE, "
                    + "nada gravado); campo ausente mantém o segredo. version diferente da gravada (ou presente na "
                    + "criação) → 409 CONCURRENT_MODIFICATION.")
    public PaymentMethodResponse update(
            @Parameter(description = METHOD_DESCRIPTION, schema = @Schema(type = "string")) @PathVariable String method,
            @Valid @RequestBody UpdatePaymentMethodRequest request) {
        UpdateCommand command = new UpdateCommand(request.environment(), request.displayName(), request.settings(),
                request.secrets(), amount(request.minAmount()), amount(request.maxAmount()),
                request.intentValidityMinutes(), request.sortOrder(), request.version());
        return PaymentMethodResponse.from(service.update(method, command));
    }

    @PostMapping("/{method}/enable")
    @Operation(operationId = "enableInsurerPaymentMethod", summary = "Activa uma forma de pagamento",
            description = "Permissão payment:configure. Sem linha → 404 PAYMENT_METHOD_NOT_FOUND; já activa → 409 "
                    + "INVALID_STATE_TRANSITION; dados incompletos (obrigatórios do schema, regras da secção 4 ou "
                    + "segredo obrigatório em falta) → 409 PAYMENT_METHOD_INCOMPLETE com errors[]; version diferente → "
                    + "409 CONCURRENT_MODIFICATION.")
    public PaymentMethodResponse enable(
            @Parameter(description = METHOD_DESCRIPTION, schema = @Schema(type = "string")) @PathVariable String method,
            @Valid @RequestBody PaymentMethodVersionRequest request) {
        return PaymentMethodResponse.from(service.enable(method, request.version()));
    }

    @PostMapping("/{method}/disable")
    @Operation(operationId = "disableInsurerPaymentMethod", summary = "Desactiva uma forma de pagamento",
            description = "Permissão payment:configure. Já inactiva → 409 INVALID_STATE_TRANSITION. As intenções já "
                    + "criadas seguem o seu curso e as referências emitidas continuam a poder ser pagas.")
    public PaymentMethodResponse disable(
            @Parameter(description = METHOD_DESCRIPTION, schema = @Schema(type = "string")) @PathVariable String method,
            @Valid @RequestBody PaymentMethodVersionRequest request) {
        return PaymentMethodResponse.from(service.disable(method, request.version()));
    }

    @PostMapping("/{method}/test")
    @Operation(operationId = "testInsurerPaymentMethod", summary = "Testa uma forma de pagamento",
            description = "Permissão payment:configure. Usa os dados gravados (grave antes de testar); sem linha → 404 "
                    + "PAYMENT_METHOD_NOT_FOUND. BANK_REFERENCE valida a configuração e a existência dos segredos SFTP "
                    + "no cofre, sem ligar ao SFTP (CONFIG_VALID, SETTINGS_INVALID, SECRET_MISSING); sem tester para o "
                    + "método → NOT_SUPPORTED. Até 15 s (TIMEOUT).")
    public PaymentMethodTestResponse test(
            @Parameter(description = METHOD_DESCRIPTION, schema = @Schema(type = "string")) @PathVariable String method) {
        TestOutcome outcome = service.test(method);
        return new PaymentMethodTestResponse(outcome.result(), outcome.message(), outcome.testedAt());
    }

    private static Long amount(Money money) {
        return money == null ? null : money.amount();
    }
}
