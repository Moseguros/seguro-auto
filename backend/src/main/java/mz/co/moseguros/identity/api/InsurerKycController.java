package mz.co.moseguros.identity.api;

import java.util.Set;
import java.util.UUID;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import mz.co.moseguros.identity.application.KycReviewService;
import mz.co.moseguros.identity.domain.KycCheckStatus;
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
 * Revisão das verificações de identidade enviadas à seguradora do utilizador (S4-01 §7; permissão
 * {@code kyc:review}). Uma verificação de outra seguradora dá 404 {@code KYC_NOT_FOUND}.
 */
@RestController
@RequestMapping("/api/v1/insurer/kyc")
@Tag(name = IdentityTag.NAME, description = IdentityTag.DESCRIPTION)
public class InsurerKycController {

    private static final Set<String> SORTS = Set.of("submittedAt", "reviewedAt", "fullName");

    private final KycReviewService service;

    public InsurerKycController(KycReviewService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(operationId = "listInsurerKycChecks", summary = "Lista as verificações de identidade da seguradora",
            description = "Permissão kyc:review. status por defeito PENDING; q procura no nome (sem diferenciar "
                    + "maiúsculas). Ordenação por submittedAt (defeito, asc: mais antigas primeiro), reviewedAt ou "
                    + "fullName.")
    public PageResponse<KycCheckSummaryResponse> listInsurerKycChecks(
            @RequestParam(required = false) KycCheckStatus status,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @Parameter(description = "submittedAt, reviewedAt ou fullName, com ,asc ou ,desc",
                    example = "submittedAt,asc")
            @RequestParam(required = false) String sort) {
        return PageResponse.of(service.list(status, q,
                        PageRequests.of(page, size, sort, SORTS, Sort.by(Sort.Direction.ASC, "submittedAt"))),
                KycCheckSummaryResponse::from);
    }

    @GetMapping("/{id}")
    @Operation(operationId = "getInsurerKycCheck", summary = "Consulta uma verificação de identidade",
            description = "Permissão kyc:review. Os dados do cliente são os do envio, com o número do documento em "
                    + "claro; files tem os ids para GET /api/v1/files/{id}. Fica registado na auditoria (KYC.VIEW).")
    public InsurerKycCheckResponse getInsurerKycCheck(@PathVariable UUID id) {
        return InsurerKycCheckResponse.from(service.get(id));
    }

    @PostMapping("/{id}/verify")
    @Operation(operationId = "verifyKycCheck", summary = "Verifica uma identidade",
            description = "Permissão kyc:review. PENDING → VERIFIED. Outro estado → 409 INVALID_STATE_TRANSITION; "
                    + "version diferente → 409 CONCURRENT_MODIFICATION. O cliente recebe um e-mail se o tiver "
                    + "verificado.")
    public InsurerKycCheckResponse verifyKycCheck(@PathVariable UUID id,
                                                  @Valid @RequestBody VerifyKycCheckRequest request) {
        return InsurerKycCheckResponse.from(service.verify(id, request.version()));
    }

    @PostMapping("/{id}/reject")
    @Operation(operationId = "rejectKycCheck", summary = "Recusa uma identidade",
            description = "Permissão kyc:review. PENDING → REJECTED. reasonCode obrigatório; reason 10–500 "
                    + "caracteres, obrigatório com OTHER (errors[].code REQUIRED, TOO_SHORT, TOO_LONG). 409 como em "
                    + "verifyKycCheck. O cliente recebe um e-mail com o motivo se tiver o e-mail verificado.")
    public InsurerKycCheckResponse rejectKycCheck(@PathVariable UUID id,
                                                  @Valid @RequestBody RejectKycCheckRequest request) {
        return InsurerKycCheckResponse.from(service.reject(id, request.reasonCode(), request.reason(),
                request.version()));
    }
}
