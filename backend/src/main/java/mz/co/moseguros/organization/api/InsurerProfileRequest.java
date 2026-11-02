package mz.co.moseguros.organization.api;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import mz.co.moseguros.organization.application.OrganizationService.InsurerData;

/** Perfil de seguradora: obrigatório se {@code type = INSURER}, proibido nos outros tipos. */
public record InsurerProfileRequest(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "SEGMZ",
                description = "Código curto único: 2 a 10 maiúsculas ou dígitos (409 SHORT_CODE_ALREADY_IN_USE)")
        @NotBlank @Pattern(regexp = "^[A-Z0-9]{2,10}$")
        String shortCode,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "reclamacoes@seguradora.co.mz")
        @NotBlank
        String complaintsEmail,
        @Schema(description = "Provedor do cliente: organização existente do tipo OMBUDSMAN (400 OMBUDSMAN_INVALID)")
        UUID ombudsmanOrganizationId) {

    InsurerData toData() {
        return new InsurerData(shortCode, complaintsEmail, ombudsmanOrganizationId);
    }
}
