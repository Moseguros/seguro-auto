package mz.co.moseguros.vehicle.api;

import java.net.URI;
import java.util.Set;
import java.util.UUID;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import mz.co.moseguros.shared.web.PageRequests;
import mz.co.moseguros.shared.web.PageResponse;
import mz.co.moseguros.vehicle.application.DriverService;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Condutores do cliente autenticado (só clientes, {@code self:vehicles}; staff → 403). Um condutor
 * de outro cliente ou retirado → 404 {@code DRIVER_NOT_FOUND}.
 */
@RestController
@RequestMapping("/api/v1/me/drivers")
@Tag(name = VehicleTag.NAME, description = VehicleTag.DESCRIPTION)
public class MyDriverController {

    static final Set<String> SORTS = Set.of("createdAt");

    private final DriverService drivers;

    public MyDriverController(DriverService drivers) {
        this.drivers = drivers;
    }

    @GetMapping
    @Operation(operationId = "listMyDrivers", summary = "Lista os condutores do cliente",
            description = "Só os activos. Ordenação por createdAt (defeito, desc).")
    public PageResponse<DriverResponse> list(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @Parameter(description = "createdAt, com ,asc ou ,desc", example = "createdAt,desc")
            @RequestParam(required = false) String sort) {
        return PageResponse.of(drivers.listMine(PageRequests.of(page, size, sort, SORTS,
                Sort.by(Sort.Direction.DESC, "createdAt"))), DriverResponse::from);
    }

    @PostMapping
    @Operation(operationId = "createMyDriver", summary = "Regista um condutor",
            description = "Erros de validação → 400 VALIDATION_ERROR com errors[]; carta já registada → 409 "
                    + "DRIVER_ALREADY_REGISTERED; segundo isOwner → 409 OWNER_DRIVER_ALREADY_EXISTS; mais de 10 → "
                    + "409 DRIVER_LIMIT_REACHED.")
    public ResponseEntity<DriverResponse> create(@RequestBody CreateDriverRequest request) {
        DriverResponse body = DriverResponse.from(drivers.create(request.toData()));
        return ResponseEntity.created(URI.create("/api/v1/me/drivers/" + body.id())).body(body);
    }

    @GetMapping("/{id}")
    @Operation(operationId = "getMyDriver", summary = "Condutor do cliente")
    public DriverResponse get(@PathVariable UUID id) {
        return DriverResponse.from(drivers.getMine(id));
    }

    @PutMapping("/{id}")
    @Operation(operationId = "updateMyDriver", summary = "Altera um condutor",
            description = "Mesmas regras do registo; licenceNumber omitido mantém o actual. version antiga → 409 "
                    + "CONCURRENT_MODIFICATION.")
    public DriverResponse update(@PathVariable UUID id, @Valid @RequestBody UpdateDriverRequest request) {
        return DriverResponse.from(drivers.update(id, request.toData(), request.version()));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(operationId = "deleteMyDriver", summary = "Retira um condutor",
            description = "Passa a REMOVED e sai dos veículos activos. Habitual de um veículo activo → 409 "
                    + "DRIVER_IS_HABITUAL.")
    public void delete(@PathVariable UUID id) {
        drivers.delete(id);
    }
}
