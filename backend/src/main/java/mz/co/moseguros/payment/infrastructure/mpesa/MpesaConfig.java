package mz.co.moseguros.payment.infrastructure.mpesa;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;

/**
 * Regista o {@link MpesaProperties} e recusa o arranque com as propriedades só de testes fora dos
 * perfis {@code local}/{@code test} ({@code timeout-cap}) ou {@code test} ({@code adapter: test-stub}).
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(MpesaProperties.class)
public class MpesaConfig {

    public MpesaConfig(MpesaProperties properties, Environment environment) {
        properties.validate(environment.acceptsProfiles(Profiles.of("local", "test")),
                environment.acceptsProfiles(Profiles.of("test")));
    }
}
