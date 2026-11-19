package mz.co.moseguros.payment.application;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Parâmetros do núcleo de pagamentos ({@code moseguros.payment}, S5-02, S5-04).
 */
@Configuration
@ConfigurationProperties("moseguros.payment")
public class PaymentProperties {

    /** Linhas máximas da exportação CSV da seguradora (acima → 400 {@code EXPORT_TOO_LARGE}). */
    private int exportMaxRows = 10_000;

    /** Calendário das consultas de estado das intenções {@code UNKNOWN} (S5-04 §4). */
    private final StatusQuery statusQuery = new StatusQuery();

    /** Fontes de ficheiros de movimentos (S5-10 §5). */
    private final FileSource fileSource = new FileSource();

    public FileSource getFileSource() {
        return fileSource;
    }

    /** {@code moseguros.payment.file-source}. */
    public static class FileSource {

        /**
         * Tempo sem uma leitura com sucesso (ligação ao SFTP) a partir do qual a fonte está parada e há um
         * alerta {@code PAYMENT_SOURCE_STALE}. Mede a ligação, não a chegada de ficheiros.
         */
        private Duration staleAfter = Duration.ofMinutes(15);

        public Duration getStaleAfter() {
            return staleAfter;
        }

        public void setStaleAfter(Duration staleAfter) {
            if (staleAfter == null || staleAfter.compareTo(Duration.ofMinutes(1)) < 0) {
                throw new IllegalArgumentException("file-source.stale-after must be at least 1 minute");
            }
            this.staleAfter = staleAfter;
        }
    }

    public int getExportMaxRows() {
        return exportMaxRows;
    }

    public void setExportMaxRows(int exportMaxRows) {
        if (exportMaxRows < 1) {
            throw new IllegalArgumentException("export-max-rows must be at least 1");
        }
        this.exportMaxRows = exportMaxRows;
    }

    public StatusQuery getStatusQuery() {
        return statusQuery;
    }

    /**
     * {@code moseguros.payment.status-query} (S5-04 §4): as primeiras consultas a {@code delays}
     * contados desde {@code unknown_since}; depois a cada {@code repeat-interval}; passado
     * {@code unknown_since + give-up-after} sem resposta final, deixa de consultar e abre
     * {@code UNRESOLVED_STATUS}.
     */
    public static class StatusQuery {

        private List<Duration> delays = List.of(Duration.ofSeconds(30), Duration.ofSeconds(60),
                Duration.ofSeconds(120), Duration.ofSeconds(300));
        private Duration repeatInterval = Duration.ofMinutes(15);
        private Duration giveUpAfter = Duration.ofHours(24);

        public List<Duration> getDelays() {
            return delays;
        }

        public void setDelays(List<Duration> delays) {
            if (delays == null || delays.isEmpty()) {
                throw new IllegalArgumentException("status-query.delays needs at least one value");
            }
            Duration previous = Duration.ZERO;
            for (Duration d : delays) {
                if (d == null || d.isNegative() || d.isZero() || d.compareTo(previous) <= 0) {
                    throw new IllegalArgumentException("status-query.delays must be positive and increasing");
                }
                previous = d;
            }
            this.delays = List.copyOf(delays);
        }

        public Duration getRepeatInterval() {
            return repeatInterval;
        }

        public void setRepeatInterval(Duration repeatInterval) {
            if (repeatInterval == null || repeatInterval.isNegative() || repeatInterval.isZero()) {
                throw new IllegalArgumentException("status-query.repeat-interval must be positive");
            }
            this.repeatInterval = repeatInterval;
        }

        public Duration getGiveUpAfter() {
            return giveUpAfter;
        }

        public void setGiveUpAfter(Duration giveUpAfter) {
            if (giveUpAfter == null || giveUpAfter.isNegative() || giveUpAfter.isZero()) {
                throw new IllegalArgumentException("status-query.give-up-after must be positive");
            }
            this.giveUpAfter = giveUpAfter;
        }

        /** Primeira consulta de uma intenção que acabou de ficar {@code UNKNOWN}. */
        public Instant firstQueryAt(Instant unknownSince) {
            return unknownSince.plus(delays.get(0));
        }

        /**
         * Próxima consulta depois de {@code attempts} consultas sem resposta final, feita a última em
         * {@code now}; {@code null} = desistir ({@code unknown_since + give-up-after} já passou).
         */
        public Instant nextQueryAt(Instant unknownSince, int attempts, Instant now) {
            Objects.requireNonNull(unknownSince, "unknownSince");
            Instant deadline = unknownSince.plus(giveUpAfter);
            if (!now.isBefore(deadline)) {
                return null;
            }
            Instant next = attempts < delays.size() ? unknownSince.plus(delays.get(attempts))
                    : now.plus(repeatInterval);
            if (next.isBefore(now)) {
                // Consulta atrasada (job parado): a seguinte já no próximo ciclo.
                next = now;
            }
            return next.isAfter(deadline) ? deadline : next;
        }
    }
}
