package mz.co.moseguros.rating.api;

import java.net.URI;
import java.util.Set;
import java.util.UUID;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import mz.co.moseguros.rating.application.QuoteQueryService;
import mz.co.moseguros.rating.application.QuoteService;
import mz.co.moseguros.shared.web.PageRequests;
import mz.co.moseguros.shared.web.PageResponse;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Cotações do cliente (S3-02 §5 e §11): pedir para um veículo e um condutor seus, consultar um
 * pedido (também pelo mediador que o fez) e listar os seus pedidos (incluindo os que um mediador
 * fez em nome dele).
 */
@RestController
@RequestMapping("/api/v1")
@Tag(name = RatingTag.NAME, description = RatingTag.DESCRIPTION)
public class QuoteController {

    static final Set<String> SORTS = Set.of("createdAt");
    static final Sort DEFAULT_SORT = Sort.by(Sort.Direction.DESC, "createdAt");

    private final QuoteService service;
    private final QuoteQueryService queries;

    public QuoteController(QuoteService service, QuoteQueryService queries) {
        this.service = service;
        this.queries = queries;
    }

    @PostMapping("/quotes")
    @Operation(operationId = "createQuoteRequest", summary = "Pede cotações para um veículo do cliente",
            description = "Permissão self:quotes. Uma cotação por produto publicado compatível de cada seguradora "
                    + "activa (OFFERED, ou NOT_AVAILABLE com unavailableReason), em menos de 3 s, ordenadas por "
                    + "preço; valem 15 dias. Veículo ou condutor de outro cliente → 404 VEHICLE_NOT_FOUND / "
                    + "DRIVER_NOT_FOUND. Danos próprios sem valor comercial → 400 VEHICLE_VALUE_REQUIRED. Mais de 30 "
                    + "pedidos numa hora → 429 QUOTE_RATE_LIMITED (Retry-After). Sem produtos compatíveis → quotes: [].")
    public ResponseEntity<QuoteRequestResponse> create(@RequestBody CreateQuoteRequest request) {
        QuoteRequestResponse body = QuoteRequestResponse.from(service.createForCustomer(request.toCommand()));
        return ResponseEntity.created(URI.create("/api/v1/quotes/" + body.id())).body(body);
    }

    @GetMapping("/quotes/{id}")
    @Operation(operationId = "getQuoteRequest", summary = "Consulta um pedido de cotações",
            description = "Permissão self:quotes ou quote:create-for-client. O cliente vê os seus (também os do "
                    + "mediador em nome dele); o mediador os da sua organização. Outro → 404 QUOTE_REQUEST_NOT_FOUND. "
                    + "status é o estado efectivo (EXPIRED depois da validade ou da data de início).")
    public QuoteRequestResponse get(@PathVariable UUID id) {
        return QuoteRequestResponse.from(queries.get(id));
    }

    @GetMapping("/me/quotes")
    @Operation(operationId = "listMyQuoteRequests", summary = "Lista os pedidos de cotações do cliente",
            description = "Permissão self:quotes. Inclui os pedidos de um mediador em nome do cliente "
                    + "(origin = BROKER). offeredCount e bestTotal contam só as cotações ainda OFFERED. Ordenação "
                    + "por createdAt (defeito createdAt,desc).")
    public PageResponse<QuoteRequestSummaryResponse> listMine(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @Parameter(description = "createdAt, com ,asc ou ,desc", example = "createdAt,desc")
            @RequestParam(required = false) String sort) {
        return PageResponse.of(queries.listMine(PageRequests.of(page, size, sort, SORTS, DEFAULT_SORT)),
                QuoteRequestSummaryResponse::from);
    }
}
