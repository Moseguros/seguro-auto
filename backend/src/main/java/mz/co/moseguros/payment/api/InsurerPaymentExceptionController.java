package mz.co.moseguros.payment.api;

import java.time.LocalDate;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import mz.co.moseguros.payment.api.PaymentExceptionDtos.PaymentExceptionCandidate;
import mz.co.moseguros.payment.api.PaymentExceptionDtos.PaymentExceptionCandidates;
import mz.co.moseguros.payment.api.PaymentExceptionDtos.PaymentExceptionDetail;
import mz.co.moseguros.payment.api.PaymentExceptionDtos.PaymentExceptionSummary;
import mz.co.moseguros.payment.api.PaymentExceptionDtos.ResolvePaymentExceptionRequest;
import mz.co.moseguros.payment.application.PaymentExceptionService;
import mz.co.moseguros.payment.domain.PaymentExceptionCase;
import mz.co.moseguros.payment.domain.PaymentExceptionType;
import mz.co.moseguros.shared.error.FieldErrorCodes;
import mz.co.moseguros.shared.error.FieldValidationException;
import mz.co.moseguros.shared.web.PageRequests;
import mz.co.moseguros.shared.web.PageResponse;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Excepções de pagamento da seguradora (S5-10 §6; âmbito {@code ORG} de uma seguradora): a fila, o detalhe, as
 * intenções candidatas e a resolução. Nenhuma resolução movimenta dinheiro: {@code REFUND} só regista que a
 * seguradora devolve o dinheiro fora da plataforma. As de outra seguradora → 404.
 */
@RestController
@RequestMapping("/api/v1/insurer/payment-exceptions")
@Tag(name = PaymentTag.NAME, description = PaymentTag.DESCRIPTION)
public class InsurerPaymentExceptionController {

    private static final Set<String> SORTS = Set.of("createdAt");

    private final PaymentExceptionService service;

    public InsurerPaymentExceptionController(PaymentExceptionService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(operationId = "listPaymentExceptions", summary = "Lista as excepções de pagamento da seguradora",
            description = "Permissão payment:read. Filtros: status (OPEN por defeito; vazio = todos), type "
                    + "repetível, from e to em datas de Africa/Maputo (inclusivas, sobre createdAt), q igual à "
                    + "referência ou ao nome do ficheiro. Ordenação createdAt (defeito desc).")
    public PageResponse<PaymentExceptionSummary> listPaymentExceptions(
            @Parameter(description = "OPEN (defeito), RESOLVED, ou vazio para todos")
            @RequestParam(required = false) String status,
            @RequestParam(required = false) List<PaymentExceptionType> type,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @Parameter(description = "createdAt, com ,asc ou ,desc", example = "createdAt,desc")
            @RequestParam(required = false) String sort) {
        Set<PaymentExceptionType> types = type == null || type.isEmpty() ? EnumSet.noneOf(PaymentExceptionType.class)
                : EnumSet.copyOf(type);
        PaymentExceptionService.Filter filter = new PaymentExceptionService.Filter(status(status), types, from, to, q);
        return PageResponse.of(service.list(filter, PageRequests.of(page, size, sort, SORTS,
                Sort.by(Sort.Direction.DESC, "createdAt").and(Sort.by("id")))), PaymentExceptionSummary::from);
    }

    @GetMapping("/{id}")
    @Operation(operationId = "getPaymentException", summary = "Consulta uma excepção de pagamento",
            description = "Permissão payment:read. Com as resoluções permitidas e a versão. De outra seguradora "
                    + "→ 404 PAYMENT_EXCEPTION_NOT_FOUND.")
    public PaymentExceptionDetail getPaymentException(@PathVariable UUID id) {
        return PaymentExceptionDetail.from(service.detail(id));
    }

    @GetMapping("/{id}/candidates")
    @Operation(operationId = "listPaymentExceptionCandidates",
            summary = "Lista as intenções a que a excepção pode ser associada",
            description = "Permissão payment:resolve-exception. Intenções BANK_REFERENCE da seguradora em PENDING, "
                    + "EXPIRED ou CANCELLED dos últimos 60 dias: mesma referência primeiro, depois o mesmo montante, "
                    + "depois as mais recentes; até 20. Tipo que não permite LINKED → lista vazia.")
    public PaymentExceptionCandidates listPaymentExceptionCandidates(@PathVariable UUID id) {
        return new PaymentExceptionCandidates(service.candidates(id).stream()
                .map(PaymentExceptionCandidate::from).toList());
    }

    @PostMapping("/{id}/resolve")
    @Operation(operationId = "resolvePaymentException", summary = "Resolve uma excepção de pagamento",
            description = "Permissão payment:resolve-exception. AMOUNT_MISMATCH, UNKNOWN_REFERENCE, "
                    + "EXPIRED_REFERENCE, CANCELLED_REFERENCE: LINKED, REFUND, IGNORED; ALREADY_PAID e "
                    + "TARGET_NOT_PAYABLE: REFUND, IGNORED; UNRESOLVED_STATUS: MARK_SUCCEEDED, MARK_FAILED. Outra → "
                    + "400 RESOLUTION_NOT_ALLOWED. LINKED: intenção de outra seguradora, de outro método ou noutro "
                    + "estado → 400 LINK_INTENT_INVALID; montante recebido menor → 400 LINK_AMOUNT_INSUFFICIENT; alvo "
                    + "já pago → 409 PAYMENT_TARGET_ALREADY_PAID. Já resolvida → 409 INVALID_STATE_TRANSITION; versão "
                    + "diferente → 409 CONCURRENT_MODIFICATION. REFUND só regista a decisão: a devolução faz-se fora "
                    + "da plataforma.")
    public PaymentExceptionDetail resolvePaymentException(@PathVariable UUID id,
                                                          @RequestBody ResolvePaymentExceptionRequest body) {
        return PaymentExceptionDetail.from(service.resolve(id, new PaymentExceptionService.ResolveCommand(
                body.resolution(), body.intentId(), body.providerTxId(), body.note(), body.version())));
    }

    /** {@code null} (ausente) → {@code OPEN}; vazio → todos. */
    private static PaymentExceptionCase.Status status(String raw) {
        if (raw == null) {
            return PaymentExceptionCase.Status.OPEN;
        }
        if (raw.isBlank()) {
            return null;
        }
        try {
            return PaymentExceptionCase.Status.valueOf(raw.strip().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new FieldValidationException("status", FieldErrorCodes.INVALID_VALUE, "Unknown status");
        }
    }
}
