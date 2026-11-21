package mz.co.moseguros.verification.api;

import io.swagger.v3.oas.annotations.media.Schema;
import mz.co.moseguros.shared.captcha.CaptchaProvider;

/** CAPTCHA da verificação por matrícula, para a web (S6-03 §2). */
public record VerificationConfigResponse(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED) CaptchaProvider captchaProvider,
        @Schema(description = "Chave pública do widget; omitida com NONE", example = "0x4AAAAAAA")
        String captchaSiteKey) {
}
