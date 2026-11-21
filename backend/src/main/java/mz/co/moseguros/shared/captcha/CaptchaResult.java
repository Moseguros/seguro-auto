package mz.co.moseguros.shared.captcha;

/** Resultado da confirmação de um token de CAPTCHA (S6-03 §7). */
public enum CaptchaResult {
    /** Token confirmado pelo fornecedor. */
    VALID,
    /** Token recusado, expirado ou de outro anfitrião. */
    INVALID,
    /** O fornecedor não respondeu (tempo esgotado, erro de rede, 5xx ou circuito aberto): falha fechada. */
    UNAVAILABLE
}
