package mz.co.moseguros.policy.api;

import java.time.Instant;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import mz.co.moseguros.policy.CoverStatus;
import mz.co.moseguros.policy.CoverageType;
import mz.co.moseguros.policy.PolicyStatus;
import mz.co.moseguros.policy.application.PolicyQueryService.CustomerSummary;
import mz.co.moseguros.policy.domain.Policy;

/**
 * Apólice na lista do cliente ({@code listMyPolicies}, S6-01 §10).
 *
 * @param coverStatus      {@code coverAt(agora)}
 * @param certificateReady o certificado válido já tem o PDF (S6-02)
 */
public record MyPolicySummaryResponse(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED) UUID id,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "AP-2027-000123") String number,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED) PolicyStatus status,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED) CoverStatus coverStatus,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED) PolicyRefs.InsurerRef insurer,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "RC Automóvel") String productName,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED) CoverageType coverageType,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED) PolicyRefs.VehicleRef vehicle,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED) Instant startAt,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED) Instant endAt,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED) boolean certificateReady) {

    static MyPolicySummaryResponse from(CustomerSummary s) {
        Policy p = s.policy();
        return new MyPolicySummaryResponse(p.getId(), p.getNumber(), p.getStatus(), p.coverAt(s.now()),
                new PolicyRefs.InsurerRef(p.getInsurerId(), s.insurerName()), p.getProductName(), p.getCoverageType(),
                PolicyRefs.VehicleRef.of(p, false), p.getStartAt(), p.getEndAt(), s.certificateReady());
    }
}
