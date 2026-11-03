package mz.co.moseguros.organization.api;

import java.time.Instant;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import mz.co.moseguros.organization.OrganizationStatus;
import mz.co.moseguros.organization.OrganizationType;
import mz.co.moseguros.organization.domain.Organization;
import mz.co.moseguros.shared.domain.Nuit;

/** Linha da lista de organizações. */
public record OrganizationSummaryResponse(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        UUID id,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        OrganizationType type,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        String displayName,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        Nuit nuit,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        OrganizationStatus status,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        Instant createdAt) {

    static OrganizationSummaryResponse from(Organization o) {
        return new OrganizationSummaryResponse(o.getId(), o.getType(), o.displayName(), o.getNuit(), o.getStatus(),
                o.getCreatedAt());
    }
}
