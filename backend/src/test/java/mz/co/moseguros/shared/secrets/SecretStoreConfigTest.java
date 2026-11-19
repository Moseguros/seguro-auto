package mz.co.moseguros.shared.secrets;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

/**
 * Teste de contexto do cofre de segredos (S5-01 §2): fora dos perfis {@code local} e {@code test},
 * {@code in-memory} ou token vazio impedem o arranque, com uma mensagem clara.
 */
class SecretStoreConfigTest {

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withUserConfiguration(SecretStoreConfig.class);

    @Test
    void prodWithInMemoryStoreFailsToStartWithClearMessage() {
        runner.withInitializer(ctx -> ctx.getEnvironment().setActiveProfiles("prod"))
                .withPropertyValues("moseguros.secrets.store=in-memory")
                .run(ctx -> {
                    assertThat(ctx).hasFailed();
                    assertThat(ctx.getStartupFailure()).rootCause()
                            .isInstanceOf(IllegalStateException.class)
                            .hasMessageContaining("moseguros.secrets.store=in-memory")
                            .hasMessageContaining("only allowed with the local or test profile");
                });
    }

    @Test
    void stagingAndProdWithoutTokenFail() {
        for (String profile : new String[] {"staging", "prod"}) {
            runner.withInitializer(ctx -> ctx.getEnvironment().setActiveProfiles(profile))
                    .withPropertyValues("moseguros.secrets.store=vault", "moseguros.secrets.vault.token=")
                    .run(ctx -> {
                        assertThat(ctx).hasFailed();
                        assertThat(ctx.getStartupFailure()).rootCause()
                                .hasMessageContaining("moseguros.secrets.vault.token (VAULT_TOKEN) is required");
                    });
        }
    }

    @Test
    void testAndLocalMayUseTheInMemoryStore() {
        for (String profile : new String[] {"test", "local"}) {
            runner.withInitializer(ctx -> ctx.getEnvironment().setActiveProfiles(profile))
                    .withPropertyValues("moseguros.secrets.store=in-memory")
                    .run(ctx -> {
                        assertThat(ctx).hasNotFailed();
                        assertThat(ctx.getBean(SecretStore.class)).isInstanceOf(InMemorySecretStore.class);
                    });
        }
    }

    @Test
    void prodWithTokenUsesVaultWithoutConnectingAtStartup() {
        runner.withInitializer(ctx -> ctx.getEnvironment().setActiveProfiles("prod"))
                .withPropertyValues("moseguros.secrets.vault.uri=http://127.0.0.1:1",
                        "moseguros.secrets.vault.token=um-token")
                .run(ctx -> {
                    assertThat(ctx).hasNotFailed();
                    SecretStore store = ctx.getBean(SecretStore.class);
                    assertThat(store).isInstanceOf(VaultSecretStore.class);
                    assertThat(store.toString()).doesNotContain("um-token");
                    assertThat(ctx.getBean(SecretStoreProperties.class).toString()).doesNotContain("um-token");
                });
    }

    @Test
    void defaultStoreIsVault() {
        runner.withInitializer(ctx -> ctx.getEnvironment().setActiveProfiles("prod"))
                .run(ctx -> {
                    assertThat(ctx).hasFailed();
                    assertThat(ctx.getStartupFailure()).rootCause().hasMessageContaining("VAULT_TOKEN");
                });
    }
}
