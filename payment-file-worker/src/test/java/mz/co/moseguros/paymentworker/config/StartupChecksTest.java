package mz.co.moseguros.paymentworker.config;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.nio.file.Path;
import java.time.Duration;

import mz.co.moseguros.paymentworker.config.VaultConfig.VaultProperties;
import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.util.unit.DataSize;

/** Arranque (S5-09 §2): com o perfil {@code prod} cada condição impede o arranque. */
class StartupChecksTest {

    static WorkerProperties worker(String secret, Duration pollInterval, boolean allowUnknown) {
        return new WorkerProperties("http://backend:8080", "payment-file-worker", secret, Duration.ofMinutes(1),
                pollInterval, Path.of("target"), DataSize.ofMegabytes(20), 50, Duration.ofMinutes(30),
                Duration.ofSeconds(10), Duration.ofSeconds(10), allowUnknown);
    }

    static final WorkerProperties VALID = worker("um-segredo-com-mais-de-32-caracteres-xx", Duration.ofSeconds(60),
            false);
    static final VaultProperties VAULT = new VaultProperties("https://vault:8200", "s.token");

    static MockEnvironment profile(String profile) {
        MockEnvironment env = new MockEnvironment();
        env.setActiveProfiles(profile);
        return env;
    }

    @Test
    void validConfigurationStartsInProd() {
        assertThatCode(() -> StartupChecks.check(VALID, VAULT, profile("prod"))).doesNotThrowAnyException();
    }

    @Test
    void emptyClientSecretDoesNotStartInProd() {
        assertThatThrownBy(() -> StartupChecks.check(worker("", Duration.ofSeconds(60), false), VAULT,
                profile("prod")))
                .isInstanceOf(IllegalStateException.class).hasMessageContaining("client-secret");
        assertThatThrownBy(() -> StartupChecks.check(worker(null, Duration.ofSeconds(60), false), VAULT,
                profile("prod")))
                .isInstanceOf(IllegalStateException.class).hasMessageContaining("client-secret");
    }

    @Test
    void emptyVaultTokenDoesNotStartInProd() {
        assertThatThrownBy(() -> StartupChecks.check(VALID, new VaultProperties("https://vault:8200", " "),
                profile("prod")))
                .isInstanceOf(IllegalStateException.class).hasMessageContaining("vault.token");
    }

    @Test
    void unknownHostKeysDoNotStartInProd() {
        assertThatThrownBy(() -> StartupChecks.check(worker("um-segredo-com-mais-de-32-caracteres-xx",
                Duration.ofSeconds(60), true), VAULT, profile("prod")))
                .isInstanceOf(IllegalStateException.class).hasMessageContaining("allow-unknown-host-keys");
    }

    @Test
    void shortPollIntervalDoesNotStartInAnyProfile() {
        WorkerProperties shortPoll = worker("um-segredo-com-mais-de-32-caracteres-xx", Duration.ofSeconds(9), false);
        for (String p : new String[] {"prod", "local", "test"}) {
            assertThatThrownBy(() -> StartupChecks.check(shortPoll, VAULT, profile(p)))
                    .isInstanceOf(IllegalStateException.class).hasMessageContaining("poll-interval");
        }
    }

    @Test
    void localAndTestAcceptDevelopmentValues() {
        WorkerProperties dev = worker("", Duration.ofSeconds(10), true);
        VaultProperties noToken = new VaultProperties("http://localhost:8200", "");
        assertThatCode(() -> StartupChecks.check(dev, noToken, profile("local"))).doesNotThrowAnyException();
        assertThatCode(() -> StartupChecks.check(dev, noToken, profile("test"))).doesNotThrowAnyException();
    }

    @Test
    void onlyTheTestProfileCanShortenThePollIntervalExplicitly() {
        WorkerProperties shortPoll = worker("", Duration.ofSeconds(2), true);
        MockEnvironment test = profile("test");
        test.setProperty("moseguros.worker.test-allow-short-poll-interval", "true");
        assertThatCode(() -> StartupChecks.check(shortPoll, VAULT, test)).doesNotThrowAnyException();
        MockEnvironment prod = profile("prod");
        prod.setProperty("moseguros.worker.test-allow-short-poll-interval", "true");
        assertThatThrownBy(() -> StartupChecks.check(shortPoll, VAULT, prod))
                .isInstanceOf(IllegalStateException.class);
    }
}
