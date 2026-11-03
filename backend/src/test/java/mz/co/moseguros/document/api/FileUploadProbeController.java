package mz.co.moseguros.document.api;

import java.io.IOException;
import java.util.UUID;

import io.swagger.v3.oas.annotations.Hidden;
import mz.co.moseguros.document.DocumentApi;
import mz.co.moseguros.document.FileClassification;
import mz.co.moseguros.document.FileRef;
import mz.co.moseguros.document.StoreFileCommand;
import mz.co.moseguros.document.application.TestFileAccessPolicy;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * Endpoint de envio só para testes (perfil {@code test}, fora do contrato): os módulos donos
 * criam os seus (S2-03, S2-04).
 */
@Hidden
@Profile("test")
@RestController
@RequestMapping("/api/v1/test-probes/files")
public class FileUploadProbeController {

    private final DocumentApi documents;

    public FileUploadProbeController(DocumentApi documents) {
        this.documents = documents;
    }

    @PostMapping(consumes = "multipart/form-data")
    public FileRef upload(@RequestPart("file") MultipartFile file) throws IOException {
        return documents.store(new StoreFileCommand(TestFileAccessPolicy.OWNER_TYPE, UUID.randomUUID(), "UPLOAD",
                FileClassification.PERSONAL, null, file.getOriginalFilename(), file.getContentType(), null,
                file.getBytes()));
    }
}
