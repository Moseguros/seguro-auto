package mz.co.moseguros.shared.captcha;

import java.util.Optional;

/**
 * {@code moseguros.captcha.provider: none} (S6-03 §7): sem CAPTCHA, todos os tokens são válidos e o
 * cabeçalho deixa de ser exigido. Só para {@code local} e {@code test}; com {@code prod} a aplicação não
 * arranca e com {@code staging} regista um {@code WARN} ({@link CaptchaConfig}).
 */
public class AlwaysValidCaptchaVerifier implements CaptchaVerifier {

    @Override
    public CaptchaProvider provider() {
        return CaptchaProvider.NONE;
    }

    @Override
    public Optional<String> siteKey() {
        return Optional.empty();
    }

    @Override
    public CaptchaResult verify(String token, String clientIp) {
        return CaptchaResult.VALID;
    }
}
