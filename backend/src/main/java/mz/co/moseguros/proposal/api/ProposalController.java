package mz.co.moseguros.proposal.api;

import java.net.URI;
import java.util.UUID;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import mz.co.moseguros.proposal.application.DeclarationService;
import mz.co.moseguros.proposal.application.ProposalDetails;
import mz.co.moseguros.proposal.application.ProposalJson;
import mz.co.moseguros.proposal.application.ProposalQueryService;
import mz.co.moseguros.proposal.application.ProposalService;
import mz.co.moseguros.proposal.application.SubmissionService;
import mz.co.moseguros.proposal.application.SuitabilityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Propostas do cliente (S4-02 §9): criação a partir de uma cotação, consulta, data de início,
 * declarações de risco, questionário de adequação e submissão (S4-05 §6). As escritas aceitam o cliente dono
 * ({@code self:proposals}) ou o mediador da proposta ({@code proposal:create-for-client}, S4-06); a
 * seguradora usa os seus endpoints ({@code InsurerProposalController}, S4-05).
 */
@RestController
@RequestMapping("/api/v1/proposals")
@Tag(name = ProposalTag.NAME, description = ProposalTag.DESCRIPTION)
public class ProposalController {

    private static final String WRITE_RULES = " Só em DRAFT ou INFO_REQUESTED (senão 409 PROPOSAL_NOT_EDITABLE); "
            + "version diferente → 409 CONCURRENT_MODIFICATION; proposta de outro cliente → 404 PROPOSAL_NOT_FOUND. "
            + "Aumenta contentRevision e renova expiresAt (+15 dias).";

    private final ProposalService proposals;
    private final ProposalQueryService queries;
    private final DeclarationService declarations;
    private final SuitabilityService suitability;
    private final SubmissionService submissions;
    private final ProposalJson json;

    public ProposalController(ProposalService proposals, ProposalQueryService queries,
                              DeclarationService declarations, SuitabilityService suitability,
                              SubmissionService submissions, ProposalJson json) {
        this.proposals = proposals;
        this.queries = queries;
        this.declarations = declarations;
        this.suitability = suitability;
        this.submissions = submissions;
        this.json = json;
    }

    @PostMapping
    @Operation(operationId = "createProposal", summary = "Cria uma proposta a partir de uma cotação",
            description = "Permissão self:proposals. Proposta DRAFT numerada (PR-AAAA-NNNNNN) com os dados da cotação "
                    + "congelados e o tomador do perfil; a cotação passa a USED. Cotação não visível ou de mediador → "
                    + "404 QUOTE_NOT_FOUND; já usada numa proposta → 409 PROPOSAL_ALREADY_EXISTS (proposalId); "
                    + "QUOTE_NOT_AVAILABLE, QUOTE_ALREADY_USED, QUOTE_EXPIRED (409); seguradora não activa → 409 "
                    + "INSURER_NOT_ACTIVE; perfil incompleto → 409 PROFILE_INCOMPLETE; veículo ou condutor mudados "
                    + "desde a cotação → 409 QUOTE_DATA_CHANGED (fields); removidos → 404 VEHICLE_NOT_FOUND / "
                    + "DRIVER_NOT_FOUND.")
    public ResponseEntity<ProposalResponse> create(@RequestBody CreateProposalRequest request) {
        ProposalResponse body = response(proposals.create(request.quoteId(), request.channel()));
        return ResponseEntity.created(URI.create("/api/v1/proposals/" + body.id())).body(body);
    }

    @GetMapping("/{id}")
    @Operation(operationId = "getProposal", summary = "Consulta uma proposta",
            description = "Permissão self:proposals (o cliente dono) ou proposal:create-for-client (o mediador da "
                    + "proposta, S4-06). Outra → 404 PROPOSAL_NOT_FOUND. A seguradora usa getInsurerProposal (S4-05).")
    public ProposalResponse get(@PathVariable UUID id) {
        return response(queries.get(id));
    }

    @PutMapping("/{id}")
    @Operation(operationId = "updateProposal", summary = "Altera a data de início da proposta",
            description = "Permissão self:proposals ou proposal:create-for-client. startDate entre hoje e hoje + 60 "
                    + "dias (Africa/Maputo; senão START_DATE_OUT_OF_RANGE); a duração e o prémio não mudam; o tomador é "
                    + "copiado de novo do perfil (perfil incompleto → 409 PROFILE_INCOMPLETE)." + WRITE_RULES)
    public ProposalResponse update(@PathVariable UUID id, @RequestBody UpdateProposalRequest request) {
        return response(proposals.update(id, request.startDate(), request.version()));
    }

    @PostMapping("/{id}/declarations")
    @Operation(operationId = "saveProposalDeclarations", summary = "Grava as declarações de risco",
            description = "Permissão self:proposals ou proposal:create-for-client. As cinco perguntas de "
                    + "api/proposal-questions.json, os campos de seguimento com resposta sim e "
                    + "truthfulnessConfirmed = true; erros juntos em errors[] (400 VALIDATION_ERROR)." + WRITE_RULES)
    public ProposalResponse saveDeclarations(@PathVariable UUID id, @RequestBody SaveDeclarationsRequest request) {
        return response(declarations.save(id, request.toInputs(), request.truthfulnessConfirmed(), request.version()));
    }

    @PostMapping("/{id}/suitability")
    @Operation(operationId = "saveProposalSuitability", summary = "Grava o questionário de adequação",
            description = "Permissão self:proposals ou proposal:create-for-client. Devolve suitability.result "
                    + "(SUITABLE ou WARNED) e os motivos; limpa uma confirmação anterior dos avisos; erros juntos em "
                    + "errors[] (400 VALIDATION_ERROR)." + WRITE_RULES)
    public ProposalResponse saveSuitability(@PathVariable UUID id, @RequestBody SaveSuitabilityRequest request) {
        return response(suitability.save(id, request.toInputs(), request.version()));
    }

    @PostMapping("/{id}/suitability/acknowledge")
    @Operation(operationId = "acknowledgeProposalSuitability", summary = "Confirma os avisos de adequação",
            description = "Permissão self:proposals ou proposal:create-for-client. O cliente leu os avisos e quer "
                    + "continuar: preenche suitability.acknowledgedAt. Sem WARNED → 409 SUITABILITY_NOT_WARNED."
                    + WRITE_RULES)
    public ProposalResponse acknowledgeSuitability(@PathVariable UUID id,
                                                   @RequestBody AcknowledgeSuitabilityRequest request) {
        return response(suitability.acknowledge(id, request.version()));
    }

    @PostMapping("/{id}/submit")
    @Operation(operationId = "submitProposal", summary = "Submete a proposta à seguradora",
            description = "Permissão self:proposals ou proposal:create-for-client. Só em DRAFT ou INFO_REQUESTED "
                    + "(senão 409 INVALID_STATE_TRANSITION); version diferente → 409 CONCURRENT_MODIFICATION; proposta "
                    + "incompleta ou sem assinatura válida → 409 PROPOSAL_INCOMPLETE (missing); seguradora não activa → "
                    + "409 INSURER_NOT_ACTIVE. As regras de aceitação do produto decidem logo: ACCEPTED (aceitação "
                    + "automática, expiresAt = prazo para pagar) ou UNDER_REVIEW (decision.insurerDueAt = prazo da "
                    + "seguradora). Depois de submetida, a proposta só se altera se a seguradora pedir informação.")
    public ProposalResponse submit(@PathVariable UUID id, @RequestBody SubmitProposalRequest request) {
        return response(submissions.submit(id, request.version()));
    }

    private ProposalResponse response(ProposalDetails details) {
        return ProposalResponse.from(details, json);
    }
}
