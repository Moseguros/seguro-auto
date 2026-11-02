package mz.co.moseguros.identity.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import mz.co.moseguros.identity.application.AccountService;
import mz.co.moseguros.identity.application.MeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Utilizador autenticado: consulta e preferências da conta.
 */
@RestController
@RequestMapping("/api/v1/me")
@Tag(name = IdentityTag.NAME, description = IdentityTag.DESCRIPTION)
public class MeController {

    private final MeService meService;
    private final AccountService accountService;

    public MeController(MeService meService, AccountService accountService) {
        this.meService = meService;
        this.accountService = accountService;
    }

    @GetMapping
    @Operation(operationId = "getMe", summary = "Utilizador autenticado")
    public MeResponse getMe() {
        return MeResponse.from(meService.getMe());
    }

    @PutMapping
    @Operation(operationId = "updateMe", summary = "Altera as preferências da conta",
            description = "Língua preferida e, só para staff, o nome (para clientes o nome está no perfil e "
                    + "displayName é ignorado).")
    public MeResponse updateMe(@Valid @RequestBody UpdateMeRequest request) {
        accountService.updateMe(request.displayName(), request.preferredLanguage());
        return MeResponse.from(meService.getMe());
    }
}
