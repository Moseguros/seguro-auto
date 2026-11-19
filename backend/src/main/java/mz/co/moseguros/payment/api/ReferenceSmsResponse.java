package mz.co.moseguros.payment.api;

import io.swagger.v3.oas.annotations.media.Schema;

/** Resposta de {@code sendReferenceSms}: o telemóvel, mascarado. */
public record ReferenceSmsResponse(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "+258*****4567") String sentTo) {
}
