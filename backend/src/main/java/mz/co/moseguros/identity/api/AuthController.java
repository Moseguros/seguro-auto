package mz.co.moseguros.identity.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import mz.co.moseguros.identity.application.SessionService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Sessões: renovação e fim de sessão (públicos; o cabeçalho {@code Authorization} é ignorado).
 * O login (OTP e staff) chega no S1-03 e no S1-05.
 */
@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = IdentityTag.NAME, description = IdentityTag.DESCRIPTION)
public class AuthController {

    private final SessionService sessions;

    public AuthController(SessionService sessions) {
        this.sessions = sessions;
    }

    @PostMapping("/refresh")
    @SecurityRequirements
    @Operation(operationId = "refreshToken", summary = "Renova a sessão",
            description = "Troca o refresh token por um par novo (rotação). Um token já usado revoga a sessão "
                    + "inteira (401 REFRESH_TOKEN_REUSED); outro token recusado dá 401 REFRESH_TOKEN_INVALID.")
    public TokenResponse refresh(@Valid @RequestBody RefreshTokenRequest request, HttpServletRequest http) {
        return TokenResponse.from(sessions.refresh(request.refreshToken(), http.getHeader(HttpHeaders.USER_AGENT)));
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @SecurityRequirements
    @Operation(operationId = "logoutSession", summary = "Termina a sessão",
            description = "Revoga a sessão do refresh token. Responde 204 mesmo que o token seja desconhecido.")
    public void logout(@RequestBody(required = false) LogoutRequest request) {
        sessions.logout(request == null ? null : request.refreshToken());
    }
}
