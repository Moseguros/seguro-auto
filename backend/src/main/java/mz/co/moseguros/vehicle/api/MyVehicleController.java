package mz.co.moseguros.vehicle.api;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.util.Set;
import java.util.UUID;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import mz.co.moseguros.shared.web.PageRequests;
import mz.co.moseguros.shared.web.PageResponse;
import mz.co.moseguros.vehicle.application.RegistrationDocService;
import mz.co.moseguros.vehicle.application.RegistryLookupService;
import mz.co.moseguros.vehicle.application.VehicleService;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * Veículos do cliente autenticado (só clientes, {@code self:vehicles}; staff → 403). Um veículo de
 * outro cliente ou retirado → 404 {@code VEHICLE_NOT_FOUND}.
 */
@RestController
@RequestMapping("/api/v1/me/vehicles")
@Tag(name = VehicleTag.NAME, description = VehicleTag.DESCRIPTION)
public class MyVehicleController {

    static final Set<String> SORTS = Set.of("createdAt", "plate");

    private final VehicleService vehicles;
    private final RegistrationDocService registrationDocs;
    private final RegistryLookupService registry;

    public MyVehicleController(VehicleService vehicles, RegistrationDocService registrationDocs,
                               RegistryLookupService registry) {
        this.vehicles = vehicles;
        this.registrationDocs = registrationDocs;
        this.registry = registry;
    }

    @GetMapping
    @Operation(operationId = "listMyVehicles", summary = "Lista os veículos do cliente",
            description = "Só os activos. Ordenação por createdAt (defeito, desc) ou plate.")
    public PageResponse<VehicleResponse> list(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @Parameter(description = "createdAt ou plate, com ,asc ou ,desc", example = "createdAt,desc")
            @RequestParam(required = false) String sort) {
        return PageResponse.of(vehicles.listMine(PageRequests.of(page, size, sort, SORTS,
                Sort.by(Sort.Direction.DESC, "createdAt"))), VehicleResponse::from);
    }

    @PostMapping
    @Operation(operationId = "createMyVehicle", summary = "Regista um veículo",
            description = "Consulta o registo automóvel: inatroVerified = true se a matrícula existir com o mesmo "
                    + "chassis (a gravação nunca depende do registo). Erros de validação → 400 VALIDATION_ERROR com "
                    + "errors[]; matrícula já registada → 409 VEHICLE_ALREADY_REGISTERED; mais de 20 → 409 "
                    + "VEHICLE_LIMIT_REACHED.")
    public ResponseEntity<VehicleResponse> create(@RequestBody CreateVehicleRequest request) {
        VehicleResponse body = VehicleResponse.from(vehicles.create(request.toData()));
        return ResponseEntity.created(URI.create("/api/v1/me/vehicles/" + body.id())).body(body);
    }

    @GetMapping("/registry-lookup")
    @Operation(operationId = "lookupVehicleRegistry", summary = "Consulta uma matrícula no registo automóvel",
            description = "Para pré-preencher o formulário; não grava nada. Matrícula inválida → 400 VALIDATION_ERROR "
                    + "(plate/INVALID_PLATE); sem registo → 404 VEHICLE_REGISTRY_NOT_FOUND; registo indisponível → "
                    + "503 VEHICLE_REGISTRY_UNAVAILABLE.")
    public RegistryLookupResponse lookupRegistry(
            @Parameter(description = "Matrícula", example = "AAA-100-MC") @RequestParam String plate) {
        return RegistryLookupResponse.from(registry.lookup(plate));
    }

    @GetMapping("/{id}")
    @Operation(operationId = "getMyVehicle", summary = "Veículo do cliente")
    public VehicleResponse get(@PathVariable UUID id) {
        return VehicleResponse.from(vehicles.getMine(id));
    }

    @PutMapping("/{id}")
    @Operation(operationId = "updateMyVehicle", summary = "Altera um veículo",
            description = "Mesmas regras do registo; consulta de novo o registo automóvel se mudar a matrícula ou o "
                    + "chassis. version antiga → 409 CONCURRENT_MODIFICATION.")
    public VehicleResponse update(@PathVariable UUID id, @Valid @RequestBody UpdateVehicleRequest request) {
        return VehicleResponse.from(vehicles.update(id, request.toData(), request.version()));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(operationId = "deleteMyVehicle", summary = "Retira um veículo",
            description = "Passa a REMOVED (sem apagamento físico). Com uma apólice em vigor → 409 "
                    + "VEHICLE_REMOVAL_BLOCKED.")
    public void delete(@PathVariable UUID id) {
        vehicles.delete(id);
    }

    @PostMapping(path = "/{id}/registration-doc", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(operationId = "uploadMyVehicleRegistrationDoc", summary = "Envia a foto ou o PDF do livrete",
            description = "Parte file: JPEG, PNG, HEIC ou PDF até 10 MB. Passa a ser o livrete actual (o anterior "
                    + "fica no histórico). Vazio → 400 FILE_EMPTY; maior → 413 FILE_TOO_LARGE; outro tipo → 415 "
                    + "FILE_TYPE_NOT_ALLOWED; sem a parte → 400 VALIDATION_ERROR (file/REQUIRED).")
    public VehicleResponse uploadRegistrationDoc(@PathVariable UUID id, @RequestPart("file") MultipartFile file) {
        byte[] content;
        try {
            content = file.getBytes();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return VehicleResponse.from(registrationDocs.upload(id, file.getOriginalFilename(), file.getContentType(),
                content));
    }

    @PutMapping("/{id}/drivers")
    @Operation(operationId = "updateMyVehicleDrivers", summary = "Substitui os condutores do veículo",
            description = "Lista vazia permitida; senão até 5, sem repetidos e com exactamente um habitual. Condutor "
                    + "que não é do cliente ou foi retirado → 404 DRIVER_NOT_FOUND. version é a do veículo.")
    public VehicleResponse updateDrivers(@PathVariable UUID id,
                                         @Valid @RequestBody UpdateVehicleDriversRequest request) {
        return VehicleResponse.from(vehicles.updateDrivers(id, request.toAssignments(), request.version()));
    }
}
