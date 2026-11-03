package mz.co.moseguros.identity.api;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import mz.co.moseguros.identity.domain.CustomerProfileRules.Changes;
import mz.co.moseguros.identity.domain.IdDocType;
import mz.co.moseguros.shared.domain.Province;

/**
 * Dados pessoais do cliente. Os erros de validação vêm todos juntos em {@code errors[]}
 * ({@code REQUIRED}, {@code INVALID_NAME}, {@code UNDERAGE}, {@code INVALID_DATE},
 * {@code INVALID_DOCUMENT_NUMBER}, {@code INVALID_NUIT}, {@code SIZE}).
 */
public record UpdateMyProfileRequest(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, minLength = 2, maxLength = 120,
                description = "Letras, espaços, apóstrofo e hífen; pelo menos duas palavras",
                example = "Ana Maria Mussa")
        String fullName,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "Pelo menos 18 anos, no máximo 120",
                example = "1990-05-14")
        LocalDate birthDate,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        IdDocType idDocType,
        @Schema(description = "Maiúsculas e dígitos, 5–20 (espaços ignorados). Obrigatório se ainda não existir "
                + "ou se idDocType mudar; omitido mantém o actual.", example = "110100123345A")
        String idDocNumber,
        @Schema(description = "9 dígitos. Obrigatório se ainda não existir; omitido mantém o actual.",
                example = "123456789")
        String nuit,
        @Schema(maxLength = 300, example = "Av. Julius Nyerere, 1234")
        String address,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        Province province,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED,
                description = "Versão lida; diferente da gravada → 409 CONCURRENT_MODIFICATION")
        @NotNull
        Long version) {

    Changes toChanges() {
        return new Changes(fullName, birthDate, idDocType, idDocNumber, nuit, address, province);
    }

    /** Sem o documento nem o NUIT, para nunca irem para um log. */
    @Override
    public String toString() {
        return "UpdateMyProfileRequest[idDocType=" + idDocType + ", province=" + province + ", version=" + version
                + "]";
    }
}
