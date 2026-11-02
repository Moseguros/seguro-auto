package mz.co.moseguros.identity.application;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

/**
 * Arranque com as propriedades do OTP: pepper obrigatório fora de {@code local}/{@code test} e
 * cabeçalho de depuração proibido em {@code prod}. Inclui o {@link OtpCodeHasher}.
 */
class OtpConfigTest {

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withUserConfiguration(OtpConfig.class);

    @Test
    void prodWithoutPepperFailsWithClearMessage() {
        runner.withInitializer(ctx -> ctx.getEnvironment().setActiveProfiles("prod"))
                .run(ctx -> {
                    assertThat(ctx).hasFailed();
                    assertThat(ctx.getStartupFailure()).rootCause()
                            .isInstanceOf(IllegalStateException.class)
                            .hasMessageContaining("MOSEGUROS_OTP_PEPPER");
                });
    }

    @Test
    void debugHeaderWithProdFailsEvenWithPepper() {
        runner.withInitializer(ctx -> ctx.getEnvironment().setActiveProfiles("prod"))
                .withPropertyValues("moseguros.security.otp.pepper=prod-secret",
                        "moseguros.security.otp.debug-header=true")
                .run(ctx -> {
                    assertThat(ctx).hasFailed();
                    assertThat(ctx.getStartupFailure()).rootCause()
                            .hasMessageContaining("debug-header must not be enabled");
                });
    }

    @Test
    void prodWithPepperStartsAndLocalOrTestDoNotNeedOne() {
        runner.withInitializer(ctx -> ctx.getEnvironment().setActiveProfiles("prod"))
                .withPropertyValues("moseguros.security.otp.pepper=prod-secret")
                .run(ctx -> assertThat(ctx).hasNotFailed().hasSingleBean(OtpCodeHasher.class));
        for (String profile : new String[] {"local", "test"}) {
            runner.withInitializer(ctx -> ctx.getEnvironment().setActiveProfiles(profile))
                    .withPropertyValues("moseguros.security.otp.debug-header=true")
                    .run(ctx -> assertThat(ctx).hasNotFailed());
        }
        runner.withInitializer(ctx -> ctx.getEnvironment().setActiveProfiles("staging"))
                .run(ctx -> assertThat(ctx).hasFailed());
    }

    @Test
    void hasherGeneratesDigitsAndComparesHashes() {
        OtpCodeHasher hasher = new OtpCodeHasher("pepper");
        UUID id = UUID.randomUUID();
        String code = hasher.generate(8);

        assertThat(code).matches("\\d{8}");
        String hash = hasher.hash(id, code);
        assertThat(hash).hasSize(64).matches("[0-9a-f]+");
        assertThat(hasher.matches(id, code, hash)).isTrue();
        assertThat(hasher.matches(UUID.randomUUID(), code, hash)).isFalse();
        assertThat(hasher.matches(id, code, new OtpCodeHasher("other").hash(id, code))).isFalse();
        assertThat(hasher.matches(id, null, hash)).isFalse();
        assertThat(hasher.toString()).doesNotContain("pepper=pepper");
        assertThat(new OtpProperties("segredo", false).toString()).doesNotContain("segredo");
    }
}
