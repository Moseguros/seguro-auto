package mz.co.moseguros.rating.api;

import java.net.URI;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import mz.co.moseguros.rating.application.QuoteQueryService;
import mz.co.moseguros.rating.application.QuoteService;
import mz.co.moseguros.shared.web.PageRequests;
import mz.co.moseguros.shared.web.PageResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Cotações do mediador (corretor ou agente) em nome de um cliente (S3-02 §6 e §11).
 */
@RestController
@RequestMapping("/api/v1/broker/quotes")
@Tag(name = RatingTag.NAME, description = RatingTag.DESCRIPTION)
public class BrokerQuoteController {

    private final QuoteService service;
    private final QuoteQueryService queries;

    public BrokerQuoteController(QuoteService service, QuoteQueryService queries) {
        this.service = service;
        this.queries = queries;
    }

    @PostMapping
    @Operation(operationId = "createBrokerQuoteRequest", summary = "Pede cotações em nome de um cliente",
            description = "Permissão quote:create-for-client (corretor ou agente). Dados do veículo e do condutor "
                    + "escritos à mão (só o que a tarifa usa) e o telemóvel do cliente: a conta do cliente é "
                    + "encontrada ou criada (telemóvel por verificar), sem mensagem nenhuma. O cliente vê o pedido em "
                    + "listMyQuoteRequests com origin = BROKER. Mesmas regras e respostas de createQuoteRequest.")
    public ResponseEntity<QuoteRequestResponse> create(@RequestBody CreateBrokerQuoteRequest request) {
        QuoteRequestResponse body = QuoteRequestResponse.from(service.createForBroker(request.toCommand()));
        return ResponseEntity.created(URI.create("/api/v1/quotes/" + body.id())).body(body);
    }

    @GetMapping
    @Operation(operationId = "listBrokerQuoteRequests", summary = "Lista os pedidos de cotações da organização",
            description = "Permissão quote:create-for-client. Pedidos de todos os utilizadores da organização do "
                    + "mediador; clientMsisdn (opcional) filtra pelo telemóvel do cliente (INVALID_MSISDN). "
                    + "Ordenação por createdAt (defeito createdAt,desc).")
    public PageResponse<QuoteRequestSummaryResponse> list(
            @Parameter(description = "Telemóvel do cliente", example = "+258841234567")
            @RequestParam(required = false) String clientMsisdn,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @Parameter(description = "createdAt, com ,asc ou ,desc", example = "createdAt,desc")
            @RequestParam(required = false) String sort) {
        return PageResponse.of(queries.listForBroker(clientMsisdn,
                        PageRequests.of(page, size, sort, QuoteController.SORTS, QuoteController.DEFAULT_SORT)),
                QuoteRequestSummaryResponse::from);
    }
}
