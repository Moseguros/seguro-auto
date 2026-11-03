package mz.co.moseguros.document.api;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import mz.co.moseguros.document.FileContent;
import mz.co.moseguros.document.FileRef;
import mz.co.moseguros.document.application.FileDownloadService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Download de ficheiros (ADR-005): público ou autenticado. Quem pode ler é decidido pela
 * {@code FileAccessPolicy} do módulo dono; sem autorização → 404 {@code FILE_NOT_FOUND}.
 */
@RestController
@RequestMapping("/api/v1/files")
@Tag(name = DocumentTag.NAME, description = DocumentTag.DESCRIPTION)
public class FileController {

    static final String OCTET_STREAM = "application/octet-stream";

    private final FileDownloadService downloads;

    public FileController(FileDownloadService downloads) {
        this.downloads = downloads;
    }

    @GetMapping("/{id}")
    @Operation(operationId = "downloadFile", summary = "Descarrega um ficheiro",
            description = "Público ou autenticado: sem token o pedido é anónimo; com token inválido ou expirado "
                    + "dá 401. O módulo dono do ficheiro decide quem o lê; sem autorização, ficheiro "
                    + "inexistente ou já limpo → 404 FILE_NOT_FOUND (nunca 403). Content-Type é o tipo real do "
                    + "ficheiro (image/jpeg, image/png, image/heic ou application/pdf); download=true pede "
                    + "Content-Disposition attachment em vez de inline.")
    @ApiResponse(responseCode = "200", description = "Conteúdo do ficheiro",
            content = @Content(mediaType = OCTET_STREAM, schema = @Schema(type = "string", format = "binary")))
    public ResponseEntity<Resource> download(
            @PathVariable UUID id,
            @Parameter(description = "true → Content-Disposition attachment (guardar); false → inline (mostrar)")
            @RequestParam(defaultValue = "false") boolean download) {
        FileContent content = downloads.download(id);
        FileRef ref = content.ref();
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(ref.contentType()))
                .contentLength(ref.sizeBytes())
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition(download, ref.originalFilename()))
                .header(HttpHeaders.CACHE_CONTROL, "private, no-store")
                .header("X-Content-Type-Options", "nosniff")
                .body(new InputStreamResource(content.content()));
    }

    /**
     * {@code inline; filename="<ascii>"; filename*=UTF-8''<percent-encoded>} (ou {@code attachment});
     * {@code <ascii>} é o nome com os caracteres não ASCII trocados por {@code _}.
     */
    static String contentDisposition(boolean attachment, String filename) {
        StringBuilder ascii = new StringBuilder();
        filename.codePoints().forEach(cp -> ascii.append(
                cp >= 0x20 && cp < 0x7F && cp != '"' && cp != '\\' ? (char) cp : '_'));
        return (attachment ? "attachment" : "inline") + "; filename=\"" + ascii + "\"; filename*=UTF-8''"
                + percentEncode(filename);
    }

    /** RFC 5987 {@code attr-char}: letras, dígitos e {@code !#$&+-.^_`|~} ficam; o resto vai em {@code %XX}. */
    static String percentEncode(String value) {
        StringBuilder out = new StringBuilder();
        for (byte b : value.getBytes(StandardCharsets.UTF_8)) {
            int c = b & 0xFF;
            if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9')
                    || "!#$&+-.^_`|~".indexOf(c) >= 0) {
                out.append((char) c);
            } else {
                out.append('%').append(Character.toUpperCase(Character.forDigit(c >> 4, 16)))
                        .append(Character.toUpperCase(Character.forDigit(c & 0xF, 16)));
            }
        }
        return out.toString();
    }
}
