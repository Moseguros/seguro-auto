package mz.co.moseguros.document;

import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import mz.co.moseguros.document.application.FileStoreService;
import mz.co.moseguros.document.application.PdfRenderService;
import org.springframework.stereotype.Service;

/**
 * Interface pública do módulo Documentos (modelo transversal §3): guarda ficheiros e devolve o
 * seu conteúdo ao módulo dono. Quem pode ler pelo download ({@code GET /api/v1/files/{id}}) é
 * decidido pela {@link FileAccessPolicy} do dono. Não há {@code delete}: os ficheiros nunca
 * ligados são limpos ao fim de 30 dias. {@link #renderPdf} gera PDFs a partir de modelos (S4-04).
 *
 * <p>Todos os métodos correm no âmbito de quem chama (o RLS aplica-se).
 */
@Service
public class DocumentApi {

    private final FileStoreService files;
    private final PdfRenderService pdfs;

    public DocumentApi(FileStoreService files, PdfRenderService pdfs) {
        this.files = files;
        this.pdfs = pdfs;
    }

    /**
     * Gera um PDF a partir do modelo {@code templateCode} (registado em {@code PdfTemplates}, textos em
     * {@code messages/pdf_{pt,en}.properties}), guarda-o ({@code application/pdf}) e liga-o ao dono do
     * {@code target}, tudo numa só chamada (S4-04 §1, decisão D5). Tem de correr numa transacção
     * ({@code MANDATORY}): o {@code markLinked} é desfeito se ela fizer rollback (o ficheiro fica órfão
     * e é limpo ao fim de 30 dias). O {@code sha256} do {@link FileRef} é o dos bytes guardados.
     *
     * <p>Variável {@code fmt} nos modelos: {@code money}, {@code date}, {@code dateTime}, {@code percent}.
     *
     * @param model  variáveis do modelo (nunca registadas em log)
     * @param locale {@code pt} ou {@code en} (outra → {@code pt})
     * @throws IllegalArgumentException {@code target} nulo (um PDF sem dono não teria
     *                                  {@link FileAccessPolicy}) ou {@code ownerType} sem política
     * @throws mz.co.moseguros.shared.error.DomainException 500 {@code PDF_TEMPLATE_NOT_FOUND} ou
     *                                                      {@code PDF_RENDER_FAILED}
     */
    public FileRef renderPdf(String templateCode, Map<String, Object> model, Locale locale, PdfTarget target) {
        if (target == null) {
            throw new IllegalArgumentException("The PDF target (owner of the file) is required");
        }
        return pdfs.render(templateCode, model, locale, target);
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
