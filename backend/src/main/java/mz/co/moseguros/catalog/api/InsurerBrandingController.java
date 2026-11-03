package mz.co.moseguros.catalog.api;

import java.io.IOException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import mz.co.moseguros.catalog.application.InsurerLogoService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/** Logótipo da seguradora do utilizador (S2-04 §7.1): leitura {@code product:read}, escrita {@code product:manage}. */
@RestController
@RequestMapping("/api/v1/insurer/branding")
@Tag(name = CatalogTag.NAME, description = CatalogTag.DESCRIPTION)
public class InsurerBrandingController {

    private final InsurerLogoService logos;

    public InsurerBrandingController(InsurerLogoService logos) {
        this.logos = logos;
    }

    @GetMapping
    @Operation(operationId = "getInsurerBranding", summary = "Consulta o logótipo da seguradora",
            description = "Permissão product:read. logoFileId é null sem logótipo; o ficheiro é público "
                    + "(GET /api/v1/files/{logoFileId}).")
    public InsurerBrandingResponse get() {
        return new InsurerBrandingResponse(logos.logoFileId());
    }

    @PostMapping(value = "/logo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(operationId = "uploadInsurerLogo", summary = "Envia o logótipo da seguradora",
            description = "Permissão product:manage. PNG ou JPEG (senão 415 FILE_TYPE_NOT_ALLOWED) até 1 MB (senão "
                    + "400 VALIDATION_ERROR com errors[0] = { field: file, code: OUT_OF_RANGE }). O ficheiro anterior "
                    + "fica guardado.")
    public InsurerBrandingResponse upload(@RequestPart("file") MultipartFile file) throws IOException {
        return new InsurerBrandingResponse(logos.upload(file.getBytes(), file.getOriginalFilename(),
                file.getContentType()));
    }

    @DeleteMapping("/logo")
    @Operation(operationId = "removeInsurerLogo", summary = "Retira o logótipo da seguradora",
            description = "Permissão product:manage. Sem logótipo → 204 na mesma (idempotente).")
    public ResponseEntity<Void> remove() {
        logos.remove();
        return ResponseEntity.noContent().build();
    }
}
