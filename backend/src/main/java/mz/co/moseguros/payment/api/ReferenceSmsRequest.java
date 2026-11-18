package mz.co.moseguros.payment.api;

import io.swagger.v3.oas.annotations.media.Schema;
import mz.co.moseguros.shared.domain.Msisdn;

/** Corpo de {@code sendReferenceSms}: telemóvel moçambicano de qualquer operadora. */
public record ReferenceSmsRequest(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, type = "string", example = "841234567") Msisdn msisdn) {
}
