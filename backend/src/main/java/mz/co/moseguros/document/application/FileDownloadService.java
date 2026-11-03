package mz.co.moseguros.document.application;

import java.util.UUID;

import mz.co.moseguros.document.FileAccessPolicy;
import mz.co.moseguros.document.FileContent;
import mz.co.moseguros.document.FileRef;
import mz.co.moseguros.document.application.DocumentSystemLookups.FoundFile;
import mz.co.moseguros.shared.security.CurrentUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Download de um ficheiro ({@code GET /api/v1/files/{id}}, S2-01 §5):
 *
 * <ol>
 *   <li>lê a linha em {@code SYSTEM} ({@link DocumentSystemLookups}); limpa conta como inexistente;</li>
 *   <li>sem linha, sem política para o {@code owner_type}, ou {@code canRead = false} → 404
 *       {@code FILE_NOT_FOUND} (nunca 403: não se revela que o ficheiro existe);</li>
 *   <li>conteúdo em falta no armazenamento → 500 {@code FILE_STORAGE_ERROR}.</li>
 * </ol>
 * A política corre no âmbito de quem pede, nesta transacção só de leitura.
 */
@Service
public class FileDownloadService {

    private final DocumentSystemLookups lookups;
    private final FileAccessPolicyRegistry policies;
    private final FileStoreService store;
    private final CurrentUser currentUser;

    public FileDownloadService(DocumentSystemLookups lookups, FileAccessPolicyRegistry policies,
                               FileStoreService store, CurrentUser currentUser) {
        this.lookups = lookups;
        this.policies = policies;
        this.store = store;
        this.currentUser = currentUser;
    }

    @Transactional(readOnly = true)
    public FileContent download(UUID fileId) {
        FoundFile found = lookups.find(fileId).orElseThrow(FileStoreService::notFound);
        FileRef ref = found.ref();
        FileAccessPolicy policy = policies.find(ref.ownerType()).orElseThrow(FileStoreService::notFound);
        if (!policy.canRead(ref.ownerType(), ref.ownerId(), currentUser)) {
            throw FileStoreService.notFound();
        }
        return new FileContent(ref, store.read(ref.id(), found.storageKey()));
    }
}
