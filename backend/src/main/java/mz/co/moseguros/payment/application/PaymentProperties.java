package mz.co.moseguros.payment.application;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Parâmetros do núcleo de pagamentos ({@code moseguros.payment}, S5-02).
 */
@Configuration
@ConfigurationProperties("moseguros.payment")
public class PaymentProperties {

    /** Linhas máximas da exportação CSV da seguradora (acima → 400 {@code EXPORT_TOO_LARGE}). */
    private int exportMaxRows = 10_000;

    /** Threads do envio assíncrono (M-Pesa). */
    private int dispatchThreads = 4;

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

    public int getDispatchThreads() {
        return dispatchThreads;
    }

    public void setDispatchThreads(int dispatchThreads) {
        this.dispatchThreads = dispatchThreads;
    }
}
