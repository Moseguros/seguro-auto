package mz.co.moseguros.policy.api;

import java.time.Instant;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import mz.co.moseguros.policy.CoverStatus;
import mz.co.moseguros.policy.CoverageType;
import mz.co.moseguros.policy.PolicyStatus;
import mz.co.moseguros.policy.application.PolicyQueryService.InsurerSummary;
import mz.co.moseguros.policy.domain.Policy;
import mz.co.moseguros.shared.domain.Money;

/** Apólice na lista da seguradora ({@code listInsurerPolicies}, S6-01 §10). */
public record InsurerPolicySummaryResponse(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED) UUID id,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "AP-2027-000123") String number,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED) PolicyStatus status,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED) CoverStatus coverStatus,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "Ana Maria Mussa") String policyholderName,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "ABC-123-MC") String plate,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "RC Automóvel") String productName,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED) CoverageType coverageType,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED) Instant startAt,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED) Instant endAt,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED) Money totalPremium,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED) Instant issuedAt) {

    static InsurerPolicySummaryResponse from(InsurerSummary s) {
        Policy p = s.policy();
        return new InsurerPolicySummaryResponse(p.getId(), p.getNumber(), p.getStatus(), p.coverAt(s.now()),
                p.getPolicyholderName(), p.getPlate(), p.getProductName(), p.getCoverageType(), p.getStartAt(),
                p.getEndAt(), Money.ofCentavos(p.getTotalAmount()), p.getIssuedAt());
    }
}
