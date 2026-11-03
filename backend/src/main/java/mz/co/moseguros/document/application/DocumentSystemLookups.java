package mz.co.moseguros.document.application;

import java.util.Optional;
import java.util.UUID;

import mz.co.moseguros.document.FileRef;
import mz.co.moseguros.document.domain.StoredFileRepository;
import mz.co.moseguros.shared.tenant.RunAsSystem;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Leituras do {@code document} que têm de ver todas as linhas ({@code SYSTEM}): o download lê o
 * ficheiro sem filtro por âmbito porque quem decide é a {@code FileAccessPolicy} do dono, e o
 * pedido pode ser anónimo (ex.: documentos de produtos publicados). Nunca devolve nada a um
 * utilizador sem essa decisão.
 */
@Component
public class DocumentSystemLookups {

    private final StoredFileRepository files;

    public DocumentSystemLookups(StoredFileRepository files) {
        this.files = files;
    }

    /** Ficheiro com o id, se existir e ainda tiver conteúdo ({@code purged_at} nulo). */
    @RunAsSystem
    @Transactional(readOnly = true)
    public Optional<FoundFile> find(UUID fileId) {
        return files.findById(fileId)
                .filter(file -> !file.isPurged())
                .map(file -> new FoundFile(FileStoreService.toRef(file), file.getStorageKey()));
    }

    /** Metadados e chave de armazenamento (a chave nunca sai do módulo). */
    public record FoundFile(FileRef ref, String storageKey) {
    }
}
