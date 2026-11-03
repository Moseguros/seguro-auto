package mz.co.moseguros.shared.crypto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.core.io.ClassPathResource;

/** Índice cego (S2-03 §6): determinista, depende da chave e da finalidade, e obrigatório no arranque. */
class BlindIndexTest {

    private static final String DATA_KEY = newKey();
    private static final String KEY_A = newKey();
    private static final String KEY_B = newKey();

    private static BlindIndex index(String key) {
        return new BlindIndex(new CryptoProperties(Map.of(1, DATA_KEY), 1, key));
    }

    @Test
    void sameValueGivesTheSameHash() {
        BlindIndex index = index(KEY_A);
        String hash = index.hash("licence", "MAP1234567");
        assertThat(hash).hasSize(64).matches("[0-9a-f]+");
        assertThat(index.hash("licence", "MAP1234567")).isEqualTo(hash);
        assertThat(index(KEY_A).hash("licence", "MAP1234567")).isEqualTo(hash);
        assertThat(index.hash("licence", "MAP1234568")).isNotEqualTo(hash);
    }

    @Test
    void differentKeysOrPurposesGiveDifferentHashes() {
        assertThat(index(KEY_A).hash("licence", "MAP1234567")).isNotEqualTo(index(KEY_B).hash("licence", "MAP1234567"));
        assertThat(index(KEY_A).hash("licence", "MAP1234567")).isNotEqualTo(index(KEY_A).hash("other", "MAP1234567"));
    }

    @Test
    void invalidKeyIsRejected() {
        assertThatThrownBy(() -> index(null)).isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("MOSEGUROS_BLIND_INDEX_KEY");
        assertThatThrownBy(() -> index("c2hvcnQ=")).isInstanceOf(IllegalStateException.class);
        assertThat(new CryptoProperties(Map.of(1, DATA_KEY), 1, "not base64!").isBlindIndexKeyValid()).isFalse();
        assertThat(new CryptoProperties(Map.of(1, DATA_KEY), 1, KEY_A).isBlindIndexKeyValid()).isTrue();
        assertThat(index(KEY_A).toString()).doesNotContain(KEY_A);
    }

    /**
     * Arranque com as propriedades do {@code application.yml} e do perfil: em {@code prod} sem
     * {@code MOSEGUROS_BLIND_INDEX_KEY} falha; {@code local} e {@code test} trazem uma chave fixa.
     */
    @Test
    void startupWithoutKeyInProdFails() {
        String defaultKey = yaml("application.yml").getProperty("moseguros.crypto.blind-index-key");
        assertThat(defaultKey).isEqualTo("${MOSEGUROS_BLIND_INDEX_KEY:}");
        assertThat(yaml("application-prod.yml").getProperty("moseguros.crypto.blind-index-key")).isNull();

        ApplicationContextRunner runner = new ApplicationContextRunner()
                .withUserConfiguration(BlindIndex.class)
                .withPropertyValues("moseguros.crypto.data-keys.1=" + DATA_KEY,
                        "moseguros.crypto.current-key-version=1");

        runner.withInitializer(ctx -> ctx.getEnvironment().setActiveProfiles("prod"))
                .withPropertyValues("moseguros.crypto.blind-index-key=")
                .run(ctx -> {
                    assertThat(ctx).hasFailed();
                    assertThat(ctx.getStartupFailure()).hasStackTraceContaining("blind-index-key");
                });

        for (String profile : new String[] {"local", "test"}) {
            String key = yaml("application-" + profile + ".yml").getProperty("moseguros.crypto.blind-index-key");
            assertThat(key).as(profile).isNotBlank();
            runner.withInitializer(ctx -> ctx.getEnvironment().setActiveProfiles(profile))
                    .withPropertyValues("moseguros.crypto.blind-index-key=" + key)
                    .run(ctx -> assertThat(ctx).hasNotFailed().hasSingleBean(BlindIndex.class));
        }
    }

    private static java.util.Properties yaml(String name) {
        YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
        factory.setResources(new ClassPathResource(name));
        return factory.getObject();
    }

    private static String newKey() {
        byte[] key = new byte[32];
        new SecureRandom().nextBytes(key);
        return Base64.getEncoder().encodeToString(key);
    }
}
