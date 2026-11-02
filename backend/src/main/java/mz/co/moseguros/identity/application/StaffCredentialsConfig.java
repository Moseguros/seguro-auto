package mz.co.moseguros.identity.application;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Credenciais do staff (ADR-003): palavras-passe com Argon2id (parâmetros do Spring Security
 * 5.8) e propriedades dos convites e do primeiro administrador.
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(InvitationProperties.class)
public class StaffCredentialsConfig {

    @Bean
    PasswordEncoder staffPasswordEncoder() {
        return Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();
    }
}
