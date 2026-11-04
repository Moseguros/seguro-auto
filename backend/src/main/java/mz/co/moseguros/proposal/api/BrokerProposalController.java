package mz.co.moseguros.proposal.api;

import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import mz.co.moseguros.proposal.ProposalStatus;
import mz.co.moseguros.proposal.application.BrokerProposalService;
import mz.co.moseguros.proposal.application.ProposalDetails;
import mz.co.moseguros.proposal.application.ProposalJson;
import mz.co.moseguros.shared.web.PageRequests;
import mz.co.moseguros.shared.web.PageResponse;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Propostas criadas pelo mediador (corretor ou agente) em nome do cliente (S4-06 §5). Todos os endpoints
 * exigem {@code proposal:create-for-client} e um utilizador de um corretor ou agente (senão 403
 * {@code FORBIDDEN}). Os passos seguintes (declarações, adequação, vistoria, assinatura assistida e
 * submissão) usam os endpoints partilhados {@code /api/v1/proposals/{id}/…}.
 */
@RestController
@RequestMapping("/api/v1/broker/proposals")
@Tag(name = ProposalTag.NAME, description = ProposalTag.DESCRIPTION)
public class BrokerProposalController {

    static final Set<String> SORTS = Set.of("createdAt");
    static final Sort DEFAULT_SORT = Sort.by(Sort.Direction.DESC, "createdAt");

    private final BrokerProposalService service;
    private final ProposalJson json;

    public BrokerProposalController(BrokerProposalService service, ProposalJson json) {
        this.service = service;
        this.json = json;
    }

    @PostMapping
    @Operation(operationId = "createProposalForClient", summary = "Cria uma proposta a partir da cotação do mediador",
            description = "Permissão proposal:create-for-client (corretor ou agente). Por esta ordem: cotação não "
                    + "visível, do cliente ou de outra organização → 404 QUOTE_NOT_FOUND; já usada numa proposta → 409 "
                    + "PROPOSAL_ALREADY_EXISTS; QUOTE_NOT_AVAILABLE, QUOTE_ALREADY_USED, QUOTE_EXPIRED (409); "
                    + "seguradora não activa → 409 INSURER_NOT_ACTIVE; vehicle ou driver em falta → 400 REQUIRED; "
                    + "campos usados na tarifa diferentes dos da cotação → 409 QUOTE_DATA_CHANGED (fields, ex.: "
                    + "vehicle.year); conta do cliente desactivada → 409 CLIENT_ACCOUNT_DISABLED; perfil do cliente "
                    + "completo e policyholder enviado → 400 NOT_ALLOWED (policyholder); perfil incompleto sem "
                    + "policyholder → 400 REQUIRED (policyholder), com as regras do perfil (errors[].field "
                    + "policyholder.*); regras do veículo e do condutor do S2-03 (errors[].field vehicle.* e driver.*; "
                    + "VEHICLE_ALREADY_REGISTERED com a mesma matrícula e outro chassis, VEHICLE_LIMIT_REACHED, "
                    + "DRIVER_LIMIT_REACHED). Regista o veículo e o condutor na conta do cliente (reutiliza os iguais), "
                    + "grava a proposta DRAFT com channel = BROKER e a cotação passa a USED. Qualquer erro desfaz tudo. "
                    + "O cliente vê a proposta em listMyProposals e recebe um e-mail se tiver e-mail verificado.")
    public ResponseEntity<ProposalResponse> createProposalForClient(@RequestBody CreateBrokerProposalRequest request) {
        ProposalDetails details = service.create(new BrokerProposalService.CreateCommand(request.quoteId(),
                request.vehicle() == null ? null : request.vehicle().toNewVehicle(),
                request.driver() == null ? null : request.driver().toNewDriver(),
                request.policyholder() == null ? null : request.policyholder().toPersonalData()));
        ProposalResponse body = ProposalResponse.from(details, json);
        return ResponseEntity.created(URI.create("/api/v1/proposals/" + body.id())).body(body);
    }

    @GetMapping
    @Operation(operationId = "listBrokerProposals", summary = "Lista as propostas da organização do mediador",
            description = "Permissão proposal:create-for-client (corretor ou agente). Propostas criadas por qualquer "
                    + "utilizador da organização; status repetível (vazio = todos); q = número exacto ou parte do nome "
                    + "do tomador. Ordenação por createdAt (defeito createdAt,desc).")
    public PageResponse<BrokerProposalSummaryResponse> listBrokerProposals(
            @Parameter(description = "Estados a incluir (repetível); vazio = todos")
            @RequestParam(required = false) List<ProposalStatus> status,
            @Parameter(description = "Número exacto ou parte do nome do tomador", example = "PR-2027-000123")
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @Parameter(description = "createdAt, com ,asc ou ,desc", example = "createdAt,desc")
            @RequestParam(required = false) String sort) {
        return PageResponse.of(service.list(status, q, PageRequests.of(page, size, sort, SORTS, DEFAULT_SORT)),
                row -> BrokerProposalSummaryResponse.from(row, json));
    }

    @PutMapping("/{id}")
    @Operation(operationId = "updateBrokerProposal", summary = "Altera a data de início ou o tomador",
            description = "Permissão proposal:create-for-client (corretor ou agente). Só em DRAFT ou INFO_REQUESTED "
                    + "(senão 409 PROPOSAL_NOT_EDITABLE); version diferente → 409 CONCURRENT_MODIFICATION; proposta de "
                    + "outra organização → 404 PROPOSAL_NOT_FOUND. startDate entre hoje e hoje + 60 dias "
                    + "(START_DATE_OUT_OF_RANGE); policyholder só com policyholderSource = BROKER (senão 400 "
                    + "NOT_ALLOWED), com as regras do perfil (idDocNumber e nuit omitidos mantêm os actuais); sem "
                    + "nenhum dos dois → 400 REQUIRED (startDate). Com policyholderSource = PROFILE o tomador é copiado "
                    + "de novo do perfil. Aumenta contentRevision, invalida a assinatura e renova expiresAt. O veículo e "
                    + "o condutor não mudam (mudar o risco exige nova cotação).")
    public ProposalResponse updateBrokerProposal(@PathVariable UUID id,
                                                 @RequestBody UpdateBrokerProposalRequest request) {
        return ProposalResponse.from(service.update(id, request.startDate(),
                request.policyholder() == null ? null : request.policyholder().toPersonalData(),
                request.version()), json);
    }
}
