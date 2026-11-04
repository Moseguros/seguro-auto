package mz.co.moseguros.proposal.api;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import mz.co.moseguros.identity.PersonalData;
import mz.co.moseguros.shared.domain.Province;

/**
 * Tomador preenchido pelo mediador quando o perfil do cliente está incompleto (S4-06 §5). Fica só na
 * proposta: o perfil do cliente não muda. Regras do perfil do cliente (S1-06); os erros vêm juntos em
 * {@code errors[]} com o campo prefixado ({@code policyholder.nuit}).
 */
public record BrokerPolicyholderRequest(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, minLength = 2, maxLength = 120,
                description = "Letras, espaços, apóstrofo e hífen; pelo menos duas palavras",
                example = "João Carlos Sitoe")
        String fullName,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "Pelo menos 18 anos, no máximo 120",
                example = "1991-09-09")
        LocalDate birthDate,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, allowableValues = {"BI", "PASSPORT", "DIRE"},
                example = "BI")
        String idDocType,
        @Schema(description = "Maiúsculas e dígitos, 5–20 (espaços ignorados). Obrigatório na criação; no "
                + "updateBrokerProposal, omitido mantém o actual (se o tipo não mudar).", example = "110100765432B")
        String idDocNumber,
        @Schema(description = "9 dígitos. Obrigatório na criação; no updateBrokerProposal, omitido mantém o actual.",
                example = "123456789")
        String nuit,
        @Schema(maxLength = 300, example = "Bairro da Machava, Q. 12")
        String address,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        Province province) {

    PersonalData toPersonalData() {
        return new PersonalData(fullName, birthDate, idDocType, idDocNumber, nuit, address, province);
    }

    /** Sem o documento nem o NUIT, para nunca irem para um log. */
    @Override
    public String toString() {
        return "BrokerPolicyholderRequest[idDocType=" + idDocType + ", province=" + province + "]";
    }
}
