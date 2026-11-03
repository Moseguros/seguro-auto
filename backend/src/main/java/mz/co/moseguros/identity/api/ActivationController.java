package mz.co.moseguros.identity.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import mz.co.moseguros.identity.application.ActivationService;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Activação de staff pelo convidado (públicos): consultar o convite, definir a palavra-passe e
 * o TOTP, e activar com o primeiro código.
 */
@RestController
@RequestMapping("/api/v1/auth/invitations/{token}")
@Tag(name = IdentityTag.NAME, description = IdentityTag.DESCRIPTION)
public class ActivationController {

    private final ActivationService service;

    public ActivationController(ActivationService service) {
        this.service = service;
    }

    @GetMapping
    @SecurityRequirements
    @Operation(operationId = "getInvitationByToken", summary = "Consulta um convite pelo token",
            description = "Token desconhecido, convite expirado, revogado ou já aceite → 404 INVITATION_INVALID.")
    public InvitationDetailsResponse get(@PathVariable String token) {
        return InvitationDetailsResponse.from(service.view(token));
    }

    @PostMapping("/accept")
    @SecurityRequirements
    @Operation(operationId = "acceptInvitation", summary = "Define a palavra-passe e o TOTP",
            description = "Aplica a política de palavras-passe (400 PASSWORD_TOO_WEAK) e devolve um segredo TOTP "
                    + "novo. Pode repetir-se até à activação: cada chamada substitui a palavra-passe e o segredo.")
    public TotpSetupResponse accept(@PathVariable String token, @Valid @RequestBody AcceptInvitationRequest request) {
        return TotpSetupResponse.from(service.accept(token, request.password()));
    }

    @PostMapping("/activate")
    @SecurityRequirements
    @Operation(operationId = "activateInvitation", summary = "Activa a conta com o primeiro código TOTP",
            description = "Sem accept antes → 400 INVITATION_NOT_ACCEPTED. Código errado → 400 TOTP_INVALID; ao 5.º "
                    + "o convite é revogado (404 INVITATION_INVALID). Certo → conta activa e sessão iniciada.")
    public TokenResponse activate(@PathVariable String token, @Valid @RequestBody ActivateInvitationRequest request,
                                  HttpServletRequest http) {
        return TokenResponse.from(service.activate(token, request.totpCode(), http.getHeader(HttpHeaders.USER_AGENT)));
    }
}
