package mz.co.moseguros.payment.api;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

/** Activação ou desactivação; {@code version} é a da forma de pagamento lida antes. */
public record PaymentMethodVersionRequest(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull
        Long version) {
}
