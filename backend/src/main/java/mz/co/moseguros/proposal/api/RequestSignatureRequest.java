package mz.co.moseguros.proposal.api;

import io.swagger.v3.oas.annotations.media.Schema;
import mz.co.moseguros.shared.domain.Channel;

/** Pedido do código de assinatura ({@code requestProposalSignature}, S4-04 §7). */
public record RequestSignatureRequest(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED,
                description = "O tomador leu as Condições Gerais e a Informação Pré-contratual (tem de ser true)")
        Boolean documentsAcknowledged,
        @Schema(description = "Canal escolhido (se as definições o permitirem); omitido = o canal por defeito")
        Channel channel,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED,
                description = "Versão lida; diferente da gravada → 409 CONCURRENT_MODIFICATION")
        Long version) {
}
