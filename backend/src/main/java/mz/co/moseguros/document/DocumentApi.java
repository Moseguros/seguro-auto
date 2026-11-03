package mz.co.moseguros.document;

import java.util.UUID;

import mz.co.moseguros.document.application.FileStoreService;
import org.springframework.stereotype.Service;

/**
 * Interface pública do módulo Documentos (modelo transversal §3): guarda ficheiros e devolve o
 * seu conteúdo ao módulo dono. Quem pode ler pelo download ({@code GET /api/v1/files/{id}}) é
 * decidido pela {@link FileAccessPolicy} do dono. Não há {@code delete}: os ficheiros nunca
 * ligados são limpos ao fim de 30 dias. ({@code renderPdf} chega no S4.)
 *
 * <p>Todos os métodos correm no âmbito de quem chama (o RLS aplica-se).
 */
@Service
public class DocumentApi {

    private final FileStoreService files;

    public DocumentApi(FileStoreService files) {
        this.files = files;
    }

    /**
     * Valida (tamanho, tipo pelo conteúdo) e guarda o ficheiro numa transacção própria
     * ({@code REQUIRES_NEW}), ainda por ligar. Erros: {@code FILE_EMPTY} (400),
     * {@code FILE_TOO_LARGE} (413), {@code FILE_TYPE_NOT_ALLOWED} (415). Um {@code ownerType} sem
     * {@link FileAccessPolicy} é um erro de programação ({@link IllegalArgumentException}).
     */
    public FileRef store(StoreFileCommand cmd) {
        return files.store(cmd);
    }

    /**
     * Liga o ficheiro ao dono, na transacção de quem chama (a mesma em que grava a referência ao
     * ficheiro). Idempotente. Ficheiro não visível → {@code FILE_NOT_FOUND}.
     */
    public void markLinked(UUID fileId) {
        files.markLinked(fileId);
    }

    /**
     * Conteúdo do ficheiro; quem recebe fecha-o. Não verifica a {@link FileAccessPolicy}.
     * Inexistente, não visível ou limpo → {@code FILE_NOT_FOUND}.
     */
    public FileContent open(UUID fileId) {
        return files.open(fileId);
    }

    /** Metadados do ficheiro; mesmas regras de {@link #open}. */
    public FileRef metadata(UUID fileId) {
        return files.metadata(fileId);
    }
}
