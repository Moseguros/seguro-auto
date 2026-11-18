package mz.co.moseguros.payment.api;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import mz.co.moseguros.payment.IntentView;
import mz.co.moseguros.payment.PaymentStatus;
import mz.co.moseguros.payment.application.PaymentQueryService;
import mz.co.moseguros.shared.web.PageRequests;
import mz.co.moseguros.shared.web.PageResponse;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** Pagamentos do próprio cliente (S5-02 §7; {@code self:payments}). */
@RestController
@RequestMapping("/api/v1/me/payments")
@Tag(name = PaymentTag.NAME, description = PaymentTag.DESCRIPTION)
public class MyPaymentsController {

    private static final Set<String> SORTS = Set.of("createdAt");

    private final PaymentQueryService queries;

    public MyPaymentsController(PaymentQueryService queries) {
        this.queries = queries;
    }

    @GetMapping
    @Operation(operationId = "listMyPayments", summary = "Lista os pagamentos do cliente",
            description = "Permissão self:payments. As intenções do próprio cliente, as mais recentes primeiro; "
                    + "status repetível (vazio = todos).")
    public PageResponse<IntentView> listMyPayments(
            @RequestParam(required = false) List<PaymentStatus> status,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @Parameter(description = "createdAt, com ,asc ou ,desc", example = "createdAt,desc")
            @RequestParam(required = false) String sort) {
        Set<PaymentStatus> statuses = status == null || status.isEmpty() ? EnumSet.noneOf(PaymentStatus.class)
                : EnumSet.copyOf(status);
        return PageResponse.of(queries.mine(statuses,
                PageRequests.of(page, size, sort, SORTS, Sort.by(Sort.Direction.DESC, "createdAt"))), v -> v);
    }
}
