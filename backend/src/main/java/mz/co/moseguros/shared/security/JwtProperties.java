package mz.co.moseguros.shared.security;

import java.time.Duration;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Tokens JWT da plataforma ({@code moseguros.security.jwt}, ADR-003).
 *
 * @param issuer             {@code iss} dos tokens emitidos e validados
 * @param audience           {@code aud} dos tokens emitidos e validados
 * @param accessTokenTtl     validade do token de acesso (15 min)
 * @param customerRefreshTtl validade deslizante do refresh token de cliente (cada renovação dá mais)
 * @param staffRefreshTtl    duração absoluta da sessão de staff, contada desde o login
 * @param keyId              {@code kid} da chave de assinatura
 * @param privateKey         chave privada RSA 2048 em PEM PKCS#8; vazia só nos perfis {@code local} e {@code test}
 * @param internalAudience   {@code aud} dos tokens de serviço ({@code /internal/**}, S5-09), diferente de
 *                           {@code audience}: um token de serviço não serve na API e vice-versa
 */
@Validated
@ConfigurationProperties(prefix = "moseguros.security.jwt")
public record JwtProperties(
        @NotBlank String issuer,
        @NotBlank String audience,
        @NotNull Duration accessTokenTtl,
        @NotNull Duration customerRefreshTtl,
        @NotNull Duration staffRefreshTtl,
        @NotBlank String keyId,
        String privateKey,
        @NotBlank String internalAudience) {
}
