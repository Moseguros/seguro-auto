package mz.co.moseguros.organization.api;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import mz.co.moseguros.organization.application.OrganizationService.Data;
import mz.co.moseguros.shared.domain.Nuit;
import mz.co.moseguros.shared.domain.Province;

/** Alteração de uma organização: os campos da criação excepto {@code type}, mais {@code version}. */
public record UpdateOrganizationRequest(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank @Size(min = 2, max = 200)
        String legalName,
        @Size(max = 120)
        String tradeName,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull
        Nuit nuit,
        @Size(max = 40)
        String licenseNumber,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank
        String contactEmail,
        @Pattern(regexp = "^\\+?[0-9]{9,15}$")
        String contactPhone,
        @Size(max = 300)
        String address,
        Province province,
        @Valid
        InsurerProfileRequest insurerProfile,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED,
                description = "Versão lida; diferente da gravada → 409 CONCURRENT_MODIFICATION")
        @NotNull
        Long version) {

    Data toData() {
        return new Data(legalName, tradeName, nuit, licenseNumber, contactEmail, contactPhone, address, province,
                insurerProfile == null ? null : insurerProfile.toData());
    }
}
