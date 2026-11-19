package mz.co.moseguros.paymentworker.config;

import java.time.Duration;

import mz.co.moseguros.paymentworker.config.VaultConfig.VaultProperties;
import mz.co.moseguros.paymentworker.parser.ParserRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.stereotype.Component;

/**
 * Verificações do arranque (S5-09 §2). Fora dos perfis {@code local} e {@code test} a aplicação não
 * arranca com: {@code client-secret} vazio, {@code vault.token} vazio, ou
 * {@code allow-unknown-host-keys = true}. Em todos os perfis: {@code poll-interval} menor que
 * {@code PT10S} não arranca. Não há leitores simulados: uma fonte com um formato sem leitor dá
 * {@code UNSUPPORTED_FORMAT} na leitura.
 */
@Component
public class StartupChecks {

    public static final Duration MIN_POLL_INTERVAL = Duration.ofSeconds(10);

    private static final Logger log = LoggerFactory.getLogger(StartupChecks.class);

    public StartupChecks(WorkerProperties worker, VaultProperties vault, Environment environment,
                         ParserRegistry parsers) {
        check(worker, vault, environment);
        log.info("payment-file-worker: backend {}, poll interval {}, parsers {}", worker.backendBaseUrl(),
                worker.pollInterval(), parsers.formats());
    }

    /** Lança {@link IllegalStateException} na primeira condição que impede o arranque. */
    public static void check(WorkerProperties worker, VaultProperties vault, Environment environment) {
        if (worker.pollInterval().compareTo(MIN_POLL_INTERVAL) < 0 && !testOverride(environment)) {
            throw new IllegalStateException("moseguros.worker.poll-interval must be at least " + MIN_POLL_INTERVAL
                    + " (was " + worker.pollInterval() + ")");
        }
        if (environment.acceptsProfiles(Profiles.of("local", "test"))) {
            return;
        }
        if (worker.clientSecret() == null || worker.clientSecret().isBlank()) {
            throw new IllegalStateException("moseguros.worker.client-secret is required outside the 'local' and "
                    + "'test' profiles (WORKER_CLIENT_SECRET)");
        }
        if (vault.token() == null || vault.token().isBlank()) {
            throw new IllegalStateException("moseguros.vault.token is required outside the 'local' and 'test' "
                    + "profiles (VAULT_TOKEN)");
        }
        if (worker.allowUnknownHostKeys()) {
            throw new IllegalStateException("moseguros.worker.allow-unknown-host-keys must be false outside the "
                    + "'local' and 'test' profiles");
        }
    }

    /**
     * Só os testes de integração encurtam o intervalo (o critério do S5-09 lê um ficheiro novo em menos de
     * {@code poll-interval} + 5 s com {@code poll-interval} de 2 s): propriedade
     * {@code moseguros.worker.test-allow-short-poll-interval} com o perfil {@code test}.
     */
    private static boolean testOverride(Environment environment) {
        return environment.acceptsProfiles(Profiles.of("test"))
                && environment.getProperty("moseguros.worker.test-allow-short-poll-interval", Boolean.class, false);
    }
}
