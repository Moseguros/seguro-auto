package mz.co.moseguros.document.infrastructure.storage;

import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Pattern;

import mz.co.moseguros.document.domain.AllowedContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

/**
 * Perfil {@code local}: copia os ficheiros de {@code classpath:db/dev/files/*} para o
 * armazenamento em {@code dev-seed/<nome>}, se ainda lá não estiverem, para as linhas de
 * {@code document.file} dos dados de demonstração terem conteúdo. Não toca na BD.
 */
@Component
@Profile("local")
public class DevSeedFilesRunner implements ApplicationRunner {

    static final String LOCATION = "classpath:db/dev/files/*";
    static final String KEY_PREFIX = "dev-seed/";
    private static final Pattern NAME = Pattern.compile("^[a-z0-9._-]{1,100}$");
    private static final Logger log = LoggerFactory.getLogger(DevSeedFilesRunner.class);

    private final FileStorage storage;

    public DevSeedFilesRunner(FileStorage storage) {
        this.storage = storage;
    }

    @Override
    public void run(ApplicationArguments args) throws IOException {
        int copied = 0;
        for (Resource resource : new PathMatchingResourcePatternResolver().getResources(LOCATION)) {
            String name = resource.getFilename();
            if (name == null || !NAME.matcher(name).matches() || !resource.isReadable()) {
                continue;
            }
            String key = KEY_PREFIX + name;
            if (storage.exists(key)) {
                continue;
            }
            String extension = name.substring(name.lastIndexOf('.') + 1);
            String contentType = AllowedContentType.fromExtension(extension)
                    .map(AllowedContentType::mimeType)
                    .orElse("application/octet-stream");
            try (InputStream in = resource.getInputStream()) {
                storage.put(key, in, resource.contentLength(), contentType);
            }
            copied++;
        }
        log.info("Dev seed files copied to storage: {}", copied);
    }
}
