package mz.co.moseguros.paymentworker.config;

import java.nio.file.Path;
import java.time.Duration;
import java.util.Objects;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.unit.DataSize;

/**
 * Configuração do worker ({@code moseguros.worker}, S5-09 §2).
 *
 * @param backendBaseUrl         endereço do backend (token de serviço e fontes)
 * @param clientId               cliente de serviço no backend
 * @param clientSecret           segredo do cliente de serviço; vazio só em {@code local} e {@code test}
 * @param sourcesRefreshInterval intervalo entre pedidos das fontes ao backend
 * @param pollInterval           intervalo entre o fim de uma leitura de uma fonte e o início da seguinte
 *                               (mínimo {@code PT10S}, {@link StartupChecks})
 * @param workDir                pasta dos ficheiros descarregados (apagados no fim de cada leitura)
 * @param maxFileSize            ficheiros maiores são rejeitados sem descarregar ({@code FILE_TOO_LARGE})
 * @param maxFilesPerRun         ficheiros tratados por leitura de uma fonte
 * @param lockAtMostFor          ShedLock: tempo máximo do trinco de uma fonte
 * @param lockAtLeastFor         ShedLock: tempo mínimo do trinco de uma fonte
 * @param publishConfirmTimeout  espera pela confirmação do RabbitMQ de cada mensagem
 * @param allowUnknownHostKeys   aceita servidores SFTP sem impressão digital configurada (só
 *                               {@code local} e {@code test})
 */
@ConfigurationProperties(prefix = "moseguros.worker")
public record WorkerProperties(
        String backendBaseUrl,
        String clientId,
        String clientSecret,
        Duration sourcesRefreshInterval,
        Duration pollInterval,
        Path workDir,
        DataSize maxFileSize,
        int maxFilesPerRun,
        Duration lockAtMostFor,
        Duration lockAtLeastFor,
        Duration publishConfirmTimeout,
        boolean allowUnknownHostKeys) {

    public WorkerProperties {
        Objects.requireNonNull(backendBaseUrl, "moseguros.worker.backend-base-url");
        Objects.requireNonNull(clientId, "moseguros.worker.client-id");
        Objects.requireNonNull(sourcesRefreshInterval, "moseguros.worker.sources-refresh-interval");
        Objects.requireNonNull(pollInterval, "moseguros.worker.poll-interval");
        Objects.requireNonNull(workDir, "moseguros.worker.work-dir");
        Objects.requireNonNull(maxFileSize, "moseguros.worker.max-file-size");
        Objects.requireNonNull(lockAtMostFor, "moseguros.worker.lock-at-most-for");
        Objects.requireNonNull(lockAtLeastFor, "moseguros.worker.lock-at-least-for");
        Objects.requireNonNull(publishConfirmTimeout, "moseguros.worker.publish-confirm-timeout");
        if (maxFilesPerRun < 1) {
            throw new IllegalArgumentException("moseguros.worker.max-files-per-run must be at least 1");
        }
    }

    @Override
    public String toString() {
        return "WorkerProperties[backendBaseUrl=" + backendBaseUrl + ", clientId=" + clientId
                + ", clientSecret=***, pollInterval=" + pollInterval + ", workDir=" + workDir + "]";
    }
}
