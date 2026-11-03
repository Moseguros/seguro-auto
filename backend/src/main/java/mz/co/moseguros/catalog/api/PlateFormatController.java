package mz.co.moseguros.catalog.api;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import mz.co.moseguros.catalog.application.PlateFormatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Formatos de matrícula: lista pública dos activos (sem o padrão) e administração
 * ({@code reference:manage}). Uma alteração passa a valer nos pedidos sem reiniciar a aplicação.
 */
@RestController
@Tag(name = CatalogTag.NAME, description = CatalogTag.DESCRIPTION)
public class PlateFormatController {

    private static final String ADMIN = "/api/v1/admin/plate-formats";

    private final PlateFormatService service;

    public PlateFormatController(PlateFormatService service) {
        this.service = service;
    }

    @GetMapping("/api/v1/plate-formats")
    @Operation(operationId = "listActivePlateFormats", summary = "Lista os formatos de matrícula aceites",
            description = "Público. Só os activos, pela ordem de teste, com rótulo e exemplo (sem o padrão).")
    public PublicPlateFormatsResponse listActive() {
        return new PublicPlateFormatsResponse(service.listActive().stream()
                .map(PublicPlateFormatResponse::from)
                .toList());
    }

    @GetMapping(ADMIN)
    @Operation(operationId = "listPlateFormats", summary = "Lista os formatos de matrícula (administração)",
            description = "Permissão reference:manage. Todos, incluindo os inactivos, por sortOrder.")
    public PlateFormatsResponse list() {
        return new PlateFormatsResponse(service.list().stream().map(PlateFormatResponse::from).toList());
    }

    @PostMapping(ADMIN)
    @Operation(operationId = "createPlateFormat", summary = "Cria um formato de matrícula",
            description = "Permissão reference:manage. Passa a ser aceite nos pedidos sem reiniciar. Código repetido "
                    + "→ 409 PLATE_FORMAT_CODE_ALREADY_EXISTS.")
    public ResponseEntity<PlateFormatResponse> create(@Valid @RequestBody CreatePlateFormatRequest request) {
        PlateFormatResponse body = PlateFormatResponse.from(service.create(request.toData()));
        return ResponseEntity.created(URI.create(ADMIN + "/" + body.id())).body(body);
    }

    @PutMapping(ADMIN + "/{id}")
    @Operation(operationId = "updatePlateFormat", summary = "Altera um formato de matrícula",
            description = "Permissão reference:manage. O código não muda; version diferente → 409 "
                    + "CONCURRENT_MODIFICATION; id desconhecido → 404 PLATE_FORMAT_NOT_FOUND.")
    public PlateFormatResponse update(@PathVariable UUID id, @Valid @RequestBody UpdatePlateFormatRequest request) {
        return PlateFormatResponse.from(service.update(id, request.toData(), request.version()));
    }

    @PostMapping(ADMIN + "/{id}/activate")
    @Operation(operationId = "activatePlateFormat", summary = "Activa um formato de matrícula",
            description = "Permissão reference:manage. Já activo → 409 INVALID_STATE_TRANSITION.")
    public PlateFormatResponse activate(@PathVariable UUID id) {
        return PlateFormatResponse.from(service.activate(id));
    }

    @PostMapping(ADMIN + "/{id}/deactivate")
    @Operation(operationId = "deactivatePlateFormat", summary = "Desactiva um formato de matrícula",
            description = "Permissão reference:manage. Deixa de ser aceite em pedidos novos; as matrículas gravadas "
                    + "não mudam. Já inactivo → 409 INVALID_STATE_TRANSITION; último activo → 409 "
                    + "LAST_ACTIVE_PLATE_FORMAT.")
    public PlateFormatResponse deactivate(@PathVariable UUID id) {
        return PlateFormatResponse.from(service.deactivate(id));
    }

    @PostMapping(ADMIN + "/test")
    @Operation(operationId = "testPlateFormat", summary = "Experimenta um padrão com um valor",
            description = "Permissão reference:manage. Não grava. As regras de pattern e displayTemplate aplicam-se "
                    + "(VALIDATION_ERROR).")
    public TestPlateFormatResponse test(@Valid @RequestBody TestPlateFormatRequest request) {
        Optional<String> formatted = service.test(request.pattern(), request.displayTemplate(), request.value());
        return new TestPlateFormatResponse(formatted.isPresent(), formatted.orElse(null));
    }
}
