package mz.co.moseguros.identity;

import java.time.Instant;
import java.util.UUID;

import mz.co.moseguros.shared.domain.Channel;

/**
 * Código de assinatura validado ({@link IdentityApi#verifySignOtp}, S4-04 §4).
 *
 * @param challengeId       desafio que tinha o código (o último da cadeia, que pode ser a
 *                          alternativa de canal do desafio pedido)
 * @param channel           canal por onde o código foi enviado
 * @param destinationMasked {@code +258*****4567} ou {@code a***@exemplo.co.mz}
 */
public record SignOtpVerification(UUID challengeId, Channel channel, String destinationMasked, Instant verifiedAt) {
}
