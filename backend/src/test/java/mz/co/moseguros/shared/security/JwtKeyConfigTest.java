package mz.co.moseguros.shared.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.security.KeyPair;
import java.security.interfaces.RSAPublicKey;
import java.time.Clock;

import com.nimbusds.jose.jwk.RSAKey;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

/**
 * Teste de contexto da chave de assinatura: sem {@code MOSEGUROS_JWT_PRIVATE_KEY} só os perfis
 * {@code local} e {@code test} arrancam (com um par gerado); a chave PEM é lida e a pública
 * deriva-se dela.
 */
class JwtKeyConfigTest {

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withUserConfiguration(JwtKeyConfig.class)
            .withBean(Clock.class, Clock::systemUTC)
            .withPropertyValues(
                    "moseguros.security.jwt.issuer=https://api.moseguros.co.mz",
                    "moseguros.security.jwt.audience=moseguros-api",
                    "moseguros.security.jwt.access-token-ttl=PT15M",
                    "moseguros.security.jwt.customer-refresh-ttl=P60D",
                    "moseguros.security.jwt.staff-refresh-ttl=PT12H",
                    "moseguros.security.jwt.key-id=kid-1",
                    "moseguros.security.jwt.private-key=");

    @Test
    void prodWithoutPrivateKeyFailsToStartWithClearMessage() {
        runner.withInitializer(ctx -> ctx.getEnvironment().setActiveProfiles("prod"))
                .run(ctx -> {
                    assertThat(ctx).hasFailed();
                    assertThat(ctx.getStartupFailure()).rootCause()
                            .isInstanceOf(IllegalStateException.class)
                            .hasMessageContaining("MOSEGUROS_JWT_PRIVATE_KEY")
                            .hasMessageContaining("moseguros.security.jwt.private-key is required");
                });
    }

    @Test
    void stagingWithoutPrivateKeyAlsoFails() {
        runner.withInitializer(ctx -> ctx.getEnvironment().setActiveProfiles("staging"))
                .run(ctx -> assertThat(ctx).hasFailed());
    }

    @Test
    void localAndTestGenerateAKeyPair() {
        for (String profile : new String[] {"local", "test"}) {
            runner.withInitializer(ctx -> ctx.getEnvironment().setActiveProfiles(profile))
                    .run(ctx -> {
                        assertThat(ctx).hasNotFailed();
                        RSAKey key = ctx.getBean(RSAKey.class);
                        assertThat(key.getKeyID()).isEqualTo("kid-1");
                        assertThat(key.isPrivate()).isTrue();
                        assertThat(key.getModulus().decode()).hasSizeGreaterThanOrEqualTo(256);
                    });
        }
    }

    @Test
    void prodWithPemKeyStartsAndDerivesThePublicKey() {
        KeyPair keyPair = TestKeys.rsa();
        runner.withInitializer(ctx -> ctx.getEnvironment().setActiveProfiles("prod"))
                .withPropertyValues("moseguros.security.jwt.private-key=" + TestKeys.pem(keyPair))
                .run(ctx -> {
                    assertThat(ctx).hasNotFailed();
                    RSAKey key = ctx.getBean(RSAKey.class);
                    RSAPublicKey expected = (RSAPublicKey) keyPair.getPublic();
                    assertThat(key.toRSAPublicKey().getModulus()).isEqualTo(expected.getModulus());
                    assertThat(key.toRSAPublicKey().getPublicExponent()).isEqualTo(expected.getPublicExponent());
                    assertThat(key.getAlgorithm().getName()).isEqualTo("RS256");
                    assertThat(key.getKeyUse().identifier()).isEqualTo("sig");
                });
    }

    @Test
    void invalidPemIsRejected() {
        assertThatThrownBy(() -> JwtKeyConfig.fromPem("-----BEGIN PRIVATE KEY-----\nnot-a-key\n-----END PRIVATE KEY-----"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("not a valid RSA private key");
    }
}
