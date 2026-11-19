package mz.co.moseguros.shared.captcha;

import java.util.Optional;

/**
 * Confirmação de um token de CAPTCHA (S6-03 §7; modelo transversal §7). A implementação escolhe-se com
 * {@code moseguros.captcha.provider} ({@link CaptchaConfig}).
 */
public interface CaptchaVerifier {

    /** {@link CaptchaProvider#TURNSTILE} ou {@link CaptchaProvider#NONE}. */
    CaptchaProvider provider();

    /** Chave pública do site (para o widget da web); vazia com {@link CaptchaProvider#NONE}. */
    Optional<String> siteKey();

    /**
     * Confirma o token junto do fornecedor.
     *
     * @param token    o token do widget (nunca vai para os logs)
     * @param clientIp IP do cliente ({@code remoteip}); pode ser {@code null}
     */
    CaptchaResult verify(String token, String clientIp);
}
