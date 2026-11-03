package mz.co.moseguros.document.infrastructure.storage;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

/**
 * Configuração do armazenamento de ficheiros ({@code moseguros.files}, S2-01 §3).
 *
 * @param storage {@code filesystem} ou {@code s3}; sem valor a aplicação não arranca
 */
@ConfigurationProperties("moseguros.files")
public record StorageProperties(Kind storage, Filesystem filesystem, S3 s3) {

    public enum Kind { FILESYSTEM, S3 }

    /** @param baseDir pasta onde ficam os ficheiros */
    public record Filesystem(String baseDir) {
    }

    /**
     * @param bucket          obrigatório ({@code MOSEGUROS_S3_BUCKET})
     * @param region          obrigatória ({@code MOSEGUROS_S3_REGION})
     * @param endpoint        opcional, para serviços compatíveis com S3 ({@code MOSEGUROS_S3_ENDPOINT})
     * @param pathStyleAccess {@code false} por defeito ({@code MOSEGUROS_S3_PATH_STYLE})
     */
    public record S3(String bucket, String region, String endpoint, @DefaultValue("false") boolean pathStyleAccess) {
    }
}
