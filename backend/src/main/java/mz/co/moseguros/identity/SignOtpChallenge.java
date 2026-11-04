package mz.co.moseguros.identity;

import java.time.Instant;
import java.util.UUID;

import mz.co.moseguros.shared.domain.Channel;

/**
 * Código de assinatura enviado ({@link IdentityApi#requestSignOtp}, S4-04 §4).
 *
 * @param challengeId       desafio raiz (o que o cliente envia na confirmação)
 * @param channel           canal efectivamente usado (pode ser a alternativa)
 * @param destinationMasked {@code +258*****4567} ou {@code a***@exemplo.co.mz}
 * @param debugCode         o código, só com {@code moseguros.security.otp.debug-header} (perfil
 *                          {@code local}, cabeçalho {@code X-Debug-Otp}); {@code null} nos outros
 */
public record SignOtpChallenge(UUID challengeId, Channel channel, String destinationMasked, int resendAfterSeconds,
                               Instant expiresAt, boolean fallbackUsed, String debugCode) {

    @Override
    public String toString() {
        return "SignOtpChallenge[challengeId=" + challengeId + ", channel=" + channel + ", destinationMasked="
                + destinationMasked + ", fallbackUsed=" + fallbackUsed + ", debugCode=***]";
    }
}
