package mz.co.moseguros.payment.api;

import java.util.List;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

/** As duas formas de pagamento da seguradora, pela ordem {@code MPESA}, {@code BANK_REFERENCE}. */
public record PaymentMethodsResponse(
        @ArraySchema(arraySchema = @Schema(requiredMode = Schema.RequiredMode.REQUIRED))
        List<PaymentMethodResponse> methods) {
}
