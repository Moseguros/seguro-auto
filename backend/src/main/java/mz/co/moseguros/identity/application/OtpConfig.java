package mz.co.moseguros.identity.application;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * Configuração do OTP do cliente. O arranque falha sem {@code MOSEGUROS_OTP_PEPPER} fora dos
 * perfis {@code local} e {@code test}, ou com {@code debug-header} ligado no perfil {@code prod}.
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(OtpProperties.class)
public class OtpConfig {

    /** Usado só em {@code local}/{@code test} quando nenhum pepper está configurado. */
    static final String DEVELOPMENT_PEPPER = "moseguros-development-otp-pepper";

    @Bean
    OtpCodeHasher otpCodeHasher(OtpProperties properties, Environment environment) {
        properties.check(environment);
        String pepper = properties.pepper();
        return new OtpCodeHasher(pepper == null || pepper.isBlank() ? DEVELOPMENT_PEPPER : pepper);
    }
}
