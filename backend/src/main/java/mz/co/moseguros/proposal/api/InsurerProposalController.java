package mz.co.moseguros.proposal.api;

import java.time.LocalDate;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import mz.co.moseguros.catalog.ProductType;
import mz.co.moseguros.proposal.ProposalStatus;
import mz.co.moseguros.proposal.api.ProposalDecisionRequests.AcceptProposalRequest;
import mz.co.moseguros.proposal.api.ProposalDecisionRequests.RejectProposalRequest;
import mz.co.moseguros.proposal.api.ProposalDecisionRequests.RequestProposalInfoRequest;
import mz.co.moseguros.proposal.application.DecisionService;
import mz.co.moseguros.proposal.application.ProposalJson;
import mz.co.moseguros.proposal.domain.ProposalChannel;
import mz.co.moseguros.proposal.domain.SlaStatus;
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
 * Propostas na seguradora (S4-05 §7; âmbito {@code ORG} de seguradora): fila com o SLA, consulta e as
 * decisões do subscritor. Só propostas já submetidas da própria seguradora; as outras → 404
 * {@code PROPOSAL_NOT_FOUND}.
 */
@RestController
@RequestMapping("/api/v1/insurer/proposals")
@Tag(name = ProposalTag.NAME, description = ProposalTag.DESCRIPTION)
public class InsurerProposalController {

    private static final Set<String> SORTS = Set.of("submittedAt", "slaDueAt");
    private static final String DECISION_RULES = " Só em UNDER_REVIEW (senão 409 PROPOSAL_NOT_UNDER_REVIEW); version "
            + "diferente → 409 CONCURRENT_MODIFICATION; o cliente é avisado por e-mail (se o tiver verificado).";

    private final DecisionService service;
    private final ProposalJson json;

    public InsurerProposalController(DecisionService service, ProposalJson json) {
        this.service = service;
        this.json = json;
    }

    @GetMapping
    @Operation(operationId = "listInsurerProposals", summary = "Lista as propostas submetidas à seguradora",
            description = "Permissão proposal:read. status repetível (por defeito UNDER_REVIEW); slaStatus (ON_TIME, "
                    + "ALERT, OVERDUE; só propostas UNDER_REVIEW); productType; channel; q = número exacto ou parte do "
                    + "nome do tomador; submittedFrom e submittedTo em datas de Africa/Maputo (inclusivas). Ordenação "
                    + "por submittedAt (defeito, asc) ou slaDueAt.")
    public PageResponse<InsurerProposalSummaryResponse> listInsurerProposals(
            @RequestParam(required = false) List<ProposalStatus> status,
            @RequestParam(required = false) SlaStatus slaStatus,
            @RequestParam(required = false) ProductType productType,
            @RequestParam(required = false) ProposalChannel channel,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate submittedFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate submittedTo,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @Parameter(description = "submittedAt ou slaDueAt, com ,asc ou ,desc", example = "submittedAt,asc")
            @RequestParam(required = false) String sort) {
        Set<ProposalStatus> statuses = status == null || status.isEmpty() ? EnumSet.noneOf(ProposalStatus.class)
                : EnumSet.copyOf(status);
        DecisionService.Filter filter = new DecisionService.Filter(statuses, slaStatus, productType, channel, q,
                submittedFrom, submittedTo);
        return PageResponse.of(service.list(filter,
                        PageRequests.of(page, size, sort, SORTS, Sort.by(Sort.Direction.ASC, "submittedAt"))),
                row -> InsurerProposalSummaryResponse.from(row, json));
    }

    @GetMapping("/{id}")
    @Operation(operationId = "getInsurerProposal", summary = "Consulta uma proposta submetida",
            description = "Permissão proposal:read. A proposta com o condutor e o veículo completos, o KYC, o "
                    + "registo das regras de aceitação (underwriting), o SLA e o histórico das assinaturas.")
    public InsurerProposalResponse getInsurerProposal(@PathVariable UUID id) {
        return InsurerProposalResponse.from(service.get(id), json);
    }

    @PostMapping("/{id}/accept")
    @Operation(operationId = "acceptProposal", summary = "Aceita uma proposta",
            description = "Permissão proposal:decide. UNDER_REVIEW → ACCEPTED (acceptanceMode = MANUAL), com o prazo "
                    + "para pagar (expiresAt). Com vistoria, tem de estar APPROVED (409 INSPECTION_NOT_APPROVED); KYC "
                    + "VERIFIED ou total abaixo do limiar do KYC simplificado (409 KYC_NOT_VERIFIED). note até 1000 "
                    + "(TOO_LONG)." + DECISION_RULES)
    public InsurerProposalResponse acceptProposal(@PathVariable UUID id, @RequestBody AcceptProposalRequest request) {
        return InsurerProposalResponse.from(service.accept(id, request.note(), request.version()), json);
    }

    @PostMapping("/{id}/reject")
    @Operation(operationId = "rejectProposal", summary = "Recusa uma proposta",
            description = "Permissão proposal:decide. UNDER_REVIEW → REJECTED. reasonCode obrigatório (REQUIRED, "
                    + "INVALID_VALUE); reason 10–1000 (REQUIRED, TOO_SHORT, TOO_LONG); o motivo vai no e-mail ao "
                    + "cliente." + DECISION_RULES)
    public InsurerProposalResponse rejectProposal(@PathVariable UUID id, @RequestBody RejectProposalRequest request) {
        return InsurerProposalResponse.from(service.reject(id, request.reasonCode(), request.reason(),
                request.version()), json);
    }

    @PostMapping("/{id}/request-info")
    @Operation(operationId = "requestProposalInfo", summary = "Pede informação ao cliente",
            description = "Permissão proposal:decide. UNDER_REVIEW → INFO_REQUESTED, com o prazo do cliente "
                    + "(expiresAt). message 10–1000 (REQUIRED, TOO_SHORT, TOO_LONG); items 1+ sem repetidos (REQUIRED, "
                    + "INVALID_VALUE, ANSWER_DUPLICATED). A assinatura deixa de valer (o cliente assina de novo); com o "
                    + "item INSPECTION a vistoria volta a PENDING." + DECISION_RULES)
    public InsurerProposalResponse requestProposalInfo(@PathVariable UUID id,
                                                       @RequestBody RequestProposalInfoRequest request) {
        return InsurerProposalResponse.from(service.requestInfo(id, request.message(), request.items(),
                request.version()), json);
    }
}
