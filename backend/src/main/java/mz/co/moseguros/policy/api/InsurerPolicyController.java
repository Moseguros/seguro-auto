package mz.co.moseguros.policy.api;

import java.time.LocalDate;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import mz.co.moseguros.policy.PolicyStatus;
import mz.co.moseguros.policy.application.PolicyQueryService;
import mz.co.moseguros.shared.web.PageRequests;
import mz.co.moseguros.shared.web.PageResponse;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Apólices da seguradora (S6-01 §10; {@code policy:read}, âmbito {@code ORG} de seguradora). Só as da
 * própria seguradora; as de outra → 404.
 */
@RestController
@RequestMapping("/api/v1/insurer/policies")
@Tag(name = PolicyTag.NAME, description = PolicyTag.DESCRIPTION)
public class InsurerPolicyController {

    private final PolicyQueryService queries;

    public InsurerPolicyController(PolicyQueryService queries) {
        this.queries = queries;
    }

    @GetMapping
    @Operation(operationId = "listInsurerPolicies", summary = "Lista as apólices da seguradora",
            description = "Permissão policy:read. Filtros: status repetível; productType; issuedFrom e issuedTo "
                    + "em datas de Africa/Maputo (inclusivas, sobre issuedAt); q = número AP-… exacto, matrícula "
                    + "ou parte do nome do tomador. Ordenação issuedAt (defeito, desc), startAt ou endAt.")
    public PageResponse<InsurerPolicySummaryResponse> listInsurerPolicies(
            @RequestParam(required = false) List<PolicyStatus> status,
            @Parameter(schema = @Schema(allowableValues = {"MTPL_ONLY", "COMPREHENSIVE", "TEMPORARY", "BORDER",
                    "FLEET"}))
            @RequestParam(required = false) String productType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate issuedFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate issuedTo,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @Parameter(description = "issuedAt, startAt ou endAt, com ,asc ou ,desc", example = "issuedAt,desc")
            @RequestParam(required = false) String sort) {
        Set<PolicyStatus> statuses = status == null || status.isEmpty() ? EnumSet.noneOf(PolicyStatus.class)
                : EnumSet.copyOf(status);
        return PageResponse.of(queries.insurerList(new PolicyQueryService.Filter(statuses, productType, issuedFrom,
                        issuedTo, q),
                        PageRequests.of(page, size, sort, MyPolicyController.SORTS,
                                Sort.by(Sort.Direction.DESC, "issuedAt"))),
                InsurerPolicySummaryResponse::from);
    }

    @GetMapping("/{id}")
    @Operation(operationId = "getInsurerPolicy", summary = "Consulta uma apólice da seguradora",
            description = "Permissão policy:read. O detalhe da apólice com a linha temporal (events) e a "
                    + "proposta de origem. De outra seguradora → 404 POLICY_NOT_FOUND.")
    public PolicyDetailResponse getInsurerPolicy(@PathVariable UUID id) {
        return PolicyDetailResponse.from(queries.insurerPolicy(id));
    }
}
