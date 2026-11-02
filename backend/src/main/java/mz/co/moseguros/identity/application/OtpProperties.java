package mz.co.moseguros.identity.application;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;

/**
 * OTP do cliente ({@code moseguros.security.otp}).
 *
 * @param pepper      segredo do HMAC dos códigos ({@code MOSEGUROS_OTP_PEPPER}); valor fixo de
 *                    desenvolvimento só nos perfis {@code local} e {@code test}
 * @param debugHeader devolve o código no cabeçalho {@code X-Debug-Otp}; só ligado em
 *                    {@code application-local.yml}, nunca com o perfil {@code prod}
 */
@ConfigurationProperties(prefix = "moseguros.security.otp")
public record OtpProperties(String pepper, boolean debugHeader) {

    static final String MISSING_PEPPER_MESSAGE = "moseguros.security.otp.pepper is required outside the 'local' "
            + "and 'test' profiles: set MOSEGUROS_OTP_PEPPER to a long random secret";
    static final String DEBUG_HEADER_IN_PROD_MESSAGE = "moseguros.security.otp.debug-header must not be enabled "
            + "with the 'prod' profile (it returns the OTP code in the X-Debug-Otp header)";

    /** Falha o arranque se faltar o pepper fora de {@code local}/{@code test} ou se o cabeçalho de depuração estiver ligado em {@code prod}. */
    void check(Environment environment) {
        if (debugHeader && environment.acceptsProfiles(Profiles.of("prod"))) {
            throw new IllegalStateException(DEBUG_HEADER_IN_PROD_MESSAGE);
        }
        if ((pepper == null || pepper.isBlank()) && !environment.acceptsProfiles(Profiles.of("local", "test"))) {
            throw new IllegalStateException(MISSING_PEPPER_MESSAGE);
        }
    }

    @Override
    public String toString() {
        return "OtpProperties[pepper=***, debugHeader=" + debugHeader + "]";
    }
}
