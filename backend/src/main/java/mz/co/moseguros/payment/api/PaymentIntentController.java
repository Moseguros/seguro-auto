package mz.co.moseguros.payment.api;

import java.util.Locale;
import java.util.UUID;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import mz.co.moseguros.payment.IntentView;
import mz.co.moseguros.payment.application.PaymentQueryService;
import mz.co.moseguros.payment.application.ReferenceDocumentService;
import mz.co.moseguros.shared.error.FieldErrorCodes;
import mz.co.moseguros.shared.error.FieldValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Intenções de pagamento do cliente (S5-02 §7; {@code self:payments}): acompanhar o estado e receber a
 * entidade e referência por SMS ou em PDF. A criação é no módulo dono do alvo
 * ({@code POST /proposals/{id}/payment-intents}).
 */
@RestController
@RequestMapping("/api/v1/payment-intents")
@Tag(name = PaymentTag.NAME, description = PaymentTag.DESCRIPTION)
public class PaymentIntentController {

    private final PaymentQueryService queries;
    private final ReferenceDocumentService references;

    public PaymentIntentController(PaymentQueryService queries, ReferenceDocumentService references) {
        this.queries = queries;
        this.references = references;
    }

    @GetMapping("/{id}")
    @Operation(operationId = "getPaymentIntent", summary = "Consulta uma intenção de pagamento",
            description = "Permissão self:payments. A intenção do próprio cliente, com o estado e a próxima acção "
                    + "(nextAction: confirmar no telemóvel, a consultar o estado, pagar por entidade e referência, ou "
                    + "nada). De outro cliente → 404 PAYMENT_INTENT_NOT_FOUND.")
    public IntentView getPaymentIntent(@PathVariable UUID id) {
        return queries.myIntent(id);
    }

    @PostMapping("/{id}/reference-sms")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Operation(operationId = "sendReferenceSms", summary = "Envia a entidade e referência por SMS",
            description = "Permissão self:payments. msisdn obrigatório (qualquer operadora). Intenção que não é "
                    + "BANK_REFERENCE → 409 NOT_A_REFERENCE_PAYMENT; referência não activa → 409 REFERENCE_NOT_ACTIVE; "
                    + "no máximo 3 envios por referência (429 REFERENCE_SMS_RATE_LIMITED). Língua da Accept-Language "
                    + "(pt ou en; outra → pt).")
    public ReferenceSmsResponse sendReferenceSms(@PathVariable UUID id, @RequestBody ReferenceSmsRequest request,
                                                 @Parameter(in = ParameterIn.HEADER, name = "Accept-Language",
                                                         hidden = true)
                                                 @RequestHeader(name = "Accept-Language", required = false)
                                                 String acceptLanguage) {
        if (request == null || request.msisdn() == null) {
            throw new FieldValidationException("msisdn", FieldErrorCodes.REQUIRED, "msisdn is required");
        }
        return new ReferenceSmsResponse(references.sendSms(id, request.msisdn(), language(acceptLanguage)).sentTo());
    }

    @PostMapping("/{id}/reference-document")
    @Operation(operationId = "createReferenceDocument", summary = "Gera o PDF da entidade e referência",
            description = "Permissão self:payments. Devolve o fileId do PDF (gerado uma vez), que se descarrega em "
                    + "GET /api/v1/files/{fileId}. Intenção que não é BANK_REFERENCE → 409 NOT_A_REFERENCE_PAYMENT. "
                    + "Língua da Accept-Language (pt ou en; outra → pt).")
    public ReferenceDocumentResponse createReferenceDocument(@PathVariable UUID id,
                                                             @Parameter(in = ParameterIn.HEADER,
                                                                     name = "Accept-Language", hidden = true)
                                                             @RequestHeader(name = "Accept-Language", required = false)
                                                             String acceptLanguage) {
        return new ReferenceDocumentResponse(references.document(id, Locale.forLanguageTag(language(acceptLanguage))));
    }

    /** {@code en} se a {@code Accept-Language} começar por inglês; senão {@code pt}. */
    static String language(String acceptLanguage) {
        if (acceptLanguage == null || acceptLanguage.isBlank()) {
            return "pt";
        }
        String first = acceptLanguage.split(",")[0].strip().toLowerCase(Locale.ROOT);
        return first.startsWith("en") ? "en" : "pt";
    }
}
