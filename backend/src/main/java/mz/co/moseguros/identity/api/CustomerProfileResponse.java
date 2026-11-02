package mz.co.moseguros.identity.api;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import mz.co.moseguros.identity.application.CustomerProfileService.ProfileView;
import mz.co.moseguros.identity.domain.IdDocType;
import mz.co.moseguros.shared.domain.Province;

/** Perfil do cliente. Os campos por preencher são omitidos; o documento e o NUIT vêm mascarados. */
public record CustomerProfileResponse(
        @Schema(example = "Ana Maria Mussa")
        String fullName,
        @Schema(example = "1990-05-14")
        LocalDate birthDate,
        IdDocType idDocType,
        @Schema(description = "Todos os caracteres menos os últimos 4 trocados por *", example = "*********345A")
        String idDocNumberMasked,
        @Schema(example = "*****6789")
        String nuitMasked,
        @Schema(example = "Av. Julius Nyerere, 1234")
        String address,
        Province province,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED,
                description = "Tem nome, data de nascimento, documento, NUIT e província")
        boolean complete,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "Enviar na alteração")
        long version) {

    static CustomerProfileResponse from(ProfileView view) {
        return new CustomerProfileResponse(view.fullName(), view.birthDate(), view.idDocType(),
                view.idDocNumberMasked(), view.nuitMasked(), view.address(), view.province(), view.complete(),
                view.version());
    }
}
