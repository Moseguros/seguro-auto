package mz.co.moseguros.organization.api;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import mz.co.moseguros.organization.OrganizationType;
import mz.co.moseguros.organization.application.OrganizationService.Data;
import mz.co.moseguros.shared.domain.Nuit;
import mz.co.moseguros.shared.domain.Province;

/** Nova organização. O primeiro gestor é convidado a seguir ({@code inviteOrganizationUser}). */
public record CreateOrganizationRequest(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull
        OrganizationType type,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "Seguradora Exemplo, SA")
        @NotBlank @Size(min = 2, max = 200)
        String legalName,
        @Schema(example = "Seguradora Exemplo")
        @Size(max = 120)
        String tradeName,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "Único (409 NUIT_ALREADY_IN_USE)")
        @NotNull
        Nuit nuit,
        @Schema(description = "Número de autorização da ASFPM; obrigatório para INSURER, BROKER e AGENT "
                + "(400 LICENSE_NUMBER_REQUIRED)")
        @Size(max = 40)
        String licenseNumber,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "geral@seguradora.co.mz")
        @NotBlank
        String contactEmail,
        @Schema(example = "+258211234567", description = "Só dígitos, com + opcional, sem espaços")
        @Pattern(regexp = "^\\+?[0-9]{9,15}$")
        String contactPhone,
        @Size(max = 300)
        String address,
        Province province,
        @Valid
        InsurerProfileRequest insurerProfile) {

    Data toData() {
        return new Data(legalName, tradeName, nuit, licenseNumber, contactEmail, contactPhone, address, province,
                insurerProfile == null ? null : insurerProfile.toData());
    }
}
