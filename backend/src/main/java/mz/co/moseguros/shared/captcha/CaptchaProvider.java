package mz.co.moseguros.shared.captcha;

import io.swagger.v3.oas.annotations.media.Schema;

/** Fornecedor de CAPTCHA activo (S6-03 §7; modelo transversal §7). */
@Schema(enumAsRef = true)
public enum CaptchaProvider {
    /** Cloudflare Turnstile. */
    TURNSTILE,
    /** Sem CAPTCHA (só {@code local}/{@code test}; com {@code prod} a aplicação não arranca). */
    NONE
}
