package mz.co.moseguros.proposal.api;

import java.util.List;
import java.util.Set;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import mz.co.moseguros.proposal.ProposalStatus;
import mz.co.moseguros.proposal.application.ProposalQueryService;
import mz.co.moseguros.shared.web.PageRequests;
import mz.co.moseguros.shared.web.PageResponse;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** Propostas do cliente autenticado (S4-02 §9, {@code listMyProposals}). */
@RestController
@RequestMapping("/api/v1/me")
@Tag(name = ProposalTag.NAME, description = ProposalTag.DESCRIPTION)
public class MyProposalController {

    static final Set<String> SORTS = Set.of("createdAt");
    static final Sort DEFAULT_SORT = Sort.by(Sort.Direction.DESC, "createdAt");

    private final ProposalQueryService queries;

    public MyProposalController(ProposalQueryService queries) {
        this.queries = queries;
    }

    @GetMapping("/proposals")
    @Operation(operationId = "listMyProposals", summary = "Lista as propostas do cliente",
            description = "Permissão self:proposals. Inclui as que um mediador criou em nome do cliente (S4-06). "
                    + "Filtro status repetível; ordenação por createdAt (defeito createdAt,desc).")
    public PageResponse<ProposalSummaryResponse> listMine(
            @Parameter(description = "Estados a incluir (repetível); vazio = todos")
            @RequestParam(required = false) List<ProposalStatus> status,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @Parameter(description = "createdAt, com ,asc ou ,desc", example = "createdAt,desc")
            @RequestParam(required = false) String sort) {
        return PageResponse.of(queries.listMine(status, PageRequests.of(page, size, sort, SORTS, DEFAULT_SORT)),
                ProposalSummaryResponse::from);
    }
}
