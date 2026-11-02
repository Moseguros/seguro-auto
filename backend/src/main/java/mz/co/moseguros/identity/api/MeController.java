package mz.co.moseguros.identity.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import mz.co.moseguros.identity.application.MeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Utilizador autenticado. {@code PUT /me} chega no S1-06.
 */
@RestController
@RequestMapping("/api/v1/me")
@Tag(name = IdentityTag.NAME, description = IdentityTag.DESCRIPTION)
public class MeController {

    private final MeService meService;

    public MeController(MeService meService) {
        this.meService = meService;
    }

    @GetMapping
    @Operation(operationId = "getMe", summary = "Utilizador autenticado")
    public MeResponse getMe() {
        return MeResponse.from(meService.getMe());
    }
}
