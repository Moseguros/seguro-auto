package mz.co.moseguros.document.infrastructure.storage;

import java.nio.file.Path;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;

/**
 * Escolhe a implementação de {@link FileStorage} por {@code moseguros.files.storage}
 * ({@code filesystem} ou {@code s3}). Sem valor, ou com a configuração da implementação em
 * falta, a aplicação não arranca.
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(StorageProperties.class)
public class StorageConfig {

    @Bean
    FileStorage fileStorage(StorageProperties properties) {
        if (properties.storage() == null) {
            throw new IllegalStateException(
                    "moseguros.files.storage is required (filesystem or s3; MOSEGUROS_FILES_STORAGE)");
        }
        return switch (properties.storage()) {
            case FILESYSTEM -> {
                String baseDir = properties.filesystem() == null ? null : properties.filesystem().baseDir();
                if (baseDir == null || baseDir.isBlank()) {
                    throw new IllegalStateException(
                            "moseguros.files.filesystem.base-dir is required when moseguros.files.storage=filesystem");
                }
                yield new FilesystemFileStorage(Path.of(baseDir));
            }
            case S3 -> {
                if (properties.s3() == null) {
                    throw new IllegalStateException(
                            "moseguros.files.s3.bucket and moseguros.files.s3.region are required when"
                                    + " moseguros.files.storage=s3");
                }
                yield new S3FileStorage(S3FileStorage.client(properties.s3(), DefaultCredentialsProvider.builder().build()),
                        properties.s3().bucket());
            }
        };
    }
}
