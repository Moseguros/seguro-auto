package mz.co.moseguros.proposal.api;

import java.util.Set;
import java.util.UUID;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import mz.co.moseguros.proposal.application.InspectionReviewService;
import mz.co.moseguros.proposal.domain.InspectionMode;
import mz.co.moseguros.proposal.domain.InspectionStatus;
import mz.co.moseguros.shared.web.PageRequests;
import mz.co.moseguros.shared.web.PageResponse;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Revisão das vistorias pela seguradora (S4-03 §6; permissão {@code inspection:review}). Vistoria de
 * outra seguradora ou de uma proposta ainda não submetida → 404 {@code INSPECTION_NOT_FOUND}.
 */
@RestController
@RequestMapping("/api/v1/insurer/inspections")
@Tag(name = ProposalTag.NAME, description = ProposalTag.DESCRIPTION)
public class InsurerInspectionController {

    private static final Set<String> SORTS = Set.of("submittedAt", "createdAt");
    private static final String DECISION_RULES = " Proposta fora de UNDER_REVIEW → 409 PROPOSAL_NOT_UNDER_REVIEW; "
            + "version da vistoria diferente → 409 CONCURRENT_MODIFICATION; vistoria fora de SUBMITTED → 409 "
            + "INVALID_STATE_TRANSITION.";

    private final InspectionReviewService service;

    public InsurerInspectionController(InspectionReviewService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(operationId = "listInsurerInspections", summary = "Lista as vistorias da seguradora",
            description = "Permissão inspection:review. Só vistorias de propostas já submetidas. status por defeito "
                    + "SUBMITTED; mode opcional. Ordenação por submittedAt (defeito, asc) ou createdAt.")
    public PageResponse<InsurerInspectionSummaryResponse> listInsurerInspections(
            @RequestParam(required = false) InspectionStatus status,
            @RequestParam(required = false) InspectionMode mode,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @Parameter(description = "submittedAt ou createdAt, com ,asc ou ,desc", example = "submittedAt,asc")
            @RequestParam(required = false) String sort) {
        return PageResponse.of(service.list(status, mode,
                        PageRequests.of(page, size, sort, SORTS, Sort.by(Sort.Direction.ASC, "submittedAt"))),
                InsurerInspectionSummaryResponse::from);
    }

    @GetMapping("/{id}")
    @Operation(operationId = "getInsurerInspection", summary = "Consulta uma vistoria",
            description = "Permissão inspection:review. As fotografias activas (fileId para GET /api/v1/files/{id}) "
                    + "e a version para aprovar ou recusar.")
    public InspectionResponse getInsurerInspection(@PathVariable UUID id) {
        return InspectionResponse.from(service.get(id));
    }

    @PostMapping("/{id}/approve")
    @Operation(operationId = "approveInspection", summary = "Aprova uma vistoria",
            description = "Permissão inspection:review. SUBMITTED → APPROVED. mode = ON_SITE regista uma vistoria "
                    + "presencial e exige notes (REQUIRED); SELF_PHOTOS (ou o modo da vistoria, sem mode) exige os "
                    + "seis ângulos (409 INSPECTION_PHOTOS_MISSING); notes até 1000 (TOO_LONG)." + DECISION_RULES)
    public InspectionResponse approveInspection(@PathVariable UUID id, @RequestBody ApproveInspectionRequest request) {
        return InspectionResponse.from(service.approve(id, request.mode(), request.notes(), request.version()));
    }

    @PostMapping("/{id}/reject")
    @Operation(operationId = "rejectInspection", summary = "Recusa uma vistoria",
            description = "Permissão inspection:review. SUBMITTED → REJECTED com reason 10–1000 (REQUIRED, TOO_SHORT, "
                    + "TOO_LONG) e os ângulos a repetir (INVALID_SLOT, DUPLICATED). A proposta continua UNDER_REVIEW: "
                    + "para o cliente refazer, pede-se informação (S4-05) ou recusa-se a proposta." + DECISION_RULES)
    public InspectionResponse rejectInspection(@PathVariable UUID id, @RequestBody RejectInspectionRequest request) {
        return InspectionResponse.from(service.reject(id, request.reason(), request.retakeSlots(),
                request.version()));
    }
}
