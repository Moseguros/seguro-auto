package mz.co.moseguros.payment.api;

import java.time.LocalDate;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import mz.co.moseguros.payment.PaymentMethod;
import mz.co.moseguros.payment.PaymentStatus;
import mz.co.moseguros.payment.application.PaymentQueryService;
import mz.co.moseguros.shared.web.PageRequests;
import mz.co.moseguros.shared.web.PageResponse;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Pagamentos da seguradora (S5-02 §7; {@code payment:read}, âmbito {@code ORG} de seguradora): lista,
 * detalhe e exportação CSV. Só os da própria seguradora; os de outra → 404.
 */
@RestController
@RequestMapping("/api/v1/insurer/payments")
@Tag(name = PaymentTag.NAME, description = PaymentTag.DESCRIPTION)
public class InsurerPaymentController {

    static final String CSV = "text/csv";
    private static final Set<String> SORTS = Set.of("createdAt", "amount");
    private static final String FILTERS = " Filtros: status repetível; method; from e to em datas de Africa/Maputo "
            + "(inclusivas, sobre createdAt); q igual à referência PG-…, ao número do alvo ou ao id do fornecedor.";

    private final PaymentQueryService queries;

    public InsurerPaymentController(PaymentQueryService queries) {
        this.queries = queries;
    }

    @GetMapping
    @Operation(operationId = "listInsurerPayments", summary = "Lista os pagamentos da seguradora",
            description = "Permissão payment:read." + FILTERS + " Ordenação createdAt (defeito, desc) ou amount.")
    public PageResponse<InsurerPaymentSummary> listInsurerPayments(
            @RequestParam(required = false) List<PaymentStatus> status,
            @RequestParam(required = false) PaymentMethod method,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @Parameter(description = "createdAt ou amount, com ,asc ou ,desc", example = "createdAt,desc")
            @RequestParam(required = false) String sort) {
        return PageResponse.of(queries.insurerList(filter(status, method, from, to, q),
                        PageRequests.of(page, size, sort, SORTS, Sort.by(Sort.Direction.DESC, "createdAt"))),
                InsurerPaymentSummary::from);
    }

    @GetMapping(value = "/export", produces = CSV)
    @Operation(operationId = "exportInsurerPayments", summary = "Exporta os pagamentos da seguradora em CSV",
            description = "Permissão payment:read. Os mesmos filtros da lista." + FILTERS + " CSV UTF-8 com BOM, "
                    + "separador ;, datas em Africa/Maputo (yyyy-MM-dd HH:mm:ss), amount em meticais com vírgula "
                    + "decimal. Mais de 10 000 linhas → 400 EXPORT_TOO_LARGE.")
    @ApiResponse(responseCode = "200", description = "Ficheiro CSV",
            content = @Content(mediaType = CSV, schema = @Schema(type = "string", format = "binary")))
    public ResponseEntity<byte[]> exportInsurerPayments(
            @RequestParam(required = false) List<PaymentStatus> status,
            @RequestParam(required = false) PaymentMethod method,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) String q) {
        byte[] csv = queries.insurerExport(filter(status, method, from, to, q));
        return ResponseEntity.ok()
                .contentType(new MediaType("text", "csv", java.nio.charset.StandardCharsets.UTF_8))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName(from, to) + "\"")
                .body(csv);
    }

    @GetMapping("/{id}")
    @Operation(operationId = "getInsurerPayment", summary = "Consulta um pagamento da seguradora",
            description = "Permissão payment:read. O pagamento com o alvo, o telemóvel mascarado, a referência "
                    + "bancária, o histórico e as excepções. De outra seguradora → 404 PAYMENT_INTENT_NOT_FOUND.")
    public InsurerPaymentDetail getInsurerPayment(@PathVariable UUID id) {
        return InsurerPaymentDetail.from(queries.insurerDetail(id));
    }

    /** {@code pagamentos-{from}-{to}.csv}; sem datas, {@code pagamentos.csv}. */
    static String fileName(LocalDate from, LocalDate to) {
        StringBuilder name = new StringBuilder("pagamentos");
        if (from != null || to != null) {
            name.append('-').append(from == null ? "inicio" : from).append('-').append(to == null ? "fim" : to);
        }
        return name.append(".csv").toString();
    }

    private static PaymentQueryService.Filter filter(List<PaymentStatus> status, PaymentMethod method,
                                                     LocalDate from, LocalDate to, String q) {
        Set<PaymentStatus> statuses = status == null || status.isEmpty() ? EnumSet.noneOf(PaymentStatus.class)
                : EnumSet.copyOf(status);
        return new PaymentQueryService.Filter(statuses, method, from, to, q);
    }
}
