package mz.co.moseguros.organization.api;

import java.time.Instant;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import mz.co.moseguros.organization.OrganizationStatus;
import mz.co.moseguros.organization.OrganizationType;
import mz.co.moseguros.organization.application.OrganizationService.Details;
import mz.co.moseguros.organization.domain.InsurerProfile;
import mz.co.moseguros.organization.domain.Organization;
import mz.co.moseguros.shared.domain.Nuit;
import mz.co.moseguros.shared.domain.Province;

/** Organização. */
public record OrganizationResponse(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        UUID id,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        OrganizationType type,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        String legalName,
        String tradeName,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "tradeName, se existir, senão legalName")
        String displayName,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        Nuit nuit,
        String licenseNumber,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        OrganizationStatus status,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        String contactEmail,
        String contactPhone,
        String address,
        Province province,
        @Schema(description = "Só nas seguradoras")
        InsurerProfileResponse insurerProfile,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        long version,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        Instant createdAt,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        Instant updatedAt) {

    static OrganizationResponse from(Details details) {
        Organization o = details.organization();
        InsurerProfile p = details.insurerProfile();
        return new OrganizationResponse(o.getId(), o.getType(), o.getLegalName(), o.getTradeName(), o.displayName(),
                o.getNuit(), o.getLicenseNumber(), o.getStatus(), o.getContactEmail(), o.getContactPhone(),
                o.getAddress(), o.getProvince(),
                p == null ? null : new InsurerProfileResponse(p.getShortCode(), p.getComplaintsEmail(),
                        p.getOmbudsmanOrgId()),
                o.getVersion(), o.getCreatedAt(), o.getUpdatedAt());
    }

    /** Perfil de seguradora. */
    public record InsurerProfileResponse(
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            String shortCode,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            String complaintsEmail,
            UUID ombudsmanOrganizationId) {
    }
}
