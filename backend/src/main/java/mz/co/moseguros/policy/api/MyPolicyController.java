package mz.co.moseguros.policy.api;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import mz.co.moseguros.policy.PolicyStatus;
import mz.co.moseguros.policy.application.PolicyQueryService;
import mz.co.moseguros.shared.web.PageRequests;
import mz.co.moseguros.shared.web.PageResponse;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** Apólices do próprio cliente (S6-01 §10; {@code self:policies}, âmbito {@code CUSTOMER}). */
@RestController
@RequestMapping("/api/v1/me/policies")
@Tag(name = PolicyTag.NAME, description = PolicyTag.DESCRIPTION)
public class MyPolicyController {

    static final Set<String> SORTS = Set.of("issuedAt", "startAt", "endAt");

    private final PolicyQueryService queries;

    public MyPolicyController(PolicyQueryService queries) {
        this.queries = queries;
    }

    @GetMapping
    @Operation(operationId = "listMyPolicies", summary = "Lista as apólices do cliente",
            description = "Permissão self:policies. As apólices do próprio cliente; status repetível (vazio = "
                    + "todas). Ordenação issuedAt (defeito, desc), startAt ou endAt.")
    public PageResponse<MyPolicySummaryResponse> listMyPolicies(
            @RequestParam(required = false) List<PolicyStatus> status,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @Parameter(description = "issuedAt, startAt ou endAt, com ,asc ou ,desc", example = "issuedAt,desc")
            @RequestParam(required = false) String sort) {
        Set<PolicyStatus> statuses = status == null || status.isEmpty() ? EnumSet.noneOf(PolicyStatus.class)
                : EnumSet.copyOf(status);
        return PageResponse.of(queries.mine(statuses,
                        PageRequests.of(page, size, sort, SORTS, Sort.by(Sort.Direction.DESC, "issuedAt"))),
                MyPolicySummaryResponse::from);
    }

    @GetMapping("/{id}")
    @Operation(operationId = "getMyPolicy", summary = "Consulta uma apólice do cliente",
            description = "Permissão self:policies. A apólice com as coberturas, as prestações e o certificado "
                    + "(o código de verificação só vai dentro de verificationUrl). De outro cliente → 404 "
                    + "POLICY_NOT_FOUND.")
    public PolicyDetailResponse getMyPolicy(@PathVariable UUID id) {
        return PolicyDetailResponse.from(queries.myPolicy(id));
    }
}
