package mz.co.moseguros.payment.api;

import java.time.Instant;

import io.swagger.v3.oas.annotations.media.Schema;
import mz.co.moseguros.payment.domain.TestResult;

/**
 * Resultado do teste de uma forma de pagamento.
 *
 * @param message código curto (ex.: {@code CONFIG_VALID}, {@code SECRET_MISSING}, {@code TIMEOUT}),
 *                nunca dados secretos
 */
public record PaymentMethodTestResponse(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED) TestResult.Status result,
        @Schema(nullable = true) String message,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED) Instant testedAt) {
}
