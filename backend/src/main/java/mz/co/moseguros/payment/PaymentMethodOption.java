package mz.co.moseguros.payment;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Forma de pagamento que o cliente pode escolher ({@link PaymentApi#availableMethods}).
 *
 * @param displayName nome configurado pela seguradora (ex.: "M-Pesa Seguradora Demo"); sem ele o
 *                    frontend usa a tradução de {@code paymentMethod.<MÉTODO>}
 */
public record PaymentMethodOption(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED) PaymentMethod method,
        String displayName,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED) int sortOrder) {
}
