package mz.co.moseguros.identity.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import mz.co.moseguros.identity.application.CustomerProfileService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Perfil do cliente autenticado (só clientes, {@code self:profile}; staff → 403).
 */
@RestController
@RequestMapping("/api/v1/me/profile")
@Tag(name = IdentityTag.NAME, description = IdentityTag.DESCRIPTION)
public class CustomerProfileController {

    private final CustomerProfileService service;

    public CustomerProfileController(CustomerProfileService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(operationId = "getMyProfile", summary = "Perfil do cliente",
            description = "O documento e o NUIT vêm mascarados; os campos por preencher são omitidos.")
    public CustomerProfileResponse getMyProfile() {
        return CustomerProfileResponse.from(service.getMyProfile());
    }

    @PutMapping
    @Operation(operationId = "updateMyProfile", summary = "Altera o perfil do cliente",
            description = "Erros de validação → 400 VALIDATION_ERROR com errors[]; version antiga → 409 "
                    + "CONCURRENT_MODIFICATION.")
    public CustomerProfileResponse updateMyProfile(@Valid @RequestBody UpdateMyProfileRequest request) {
        return CustomerProfileResponse.from(service.updateMyProfile(request.toChanges(), request.version()));
    }
}
