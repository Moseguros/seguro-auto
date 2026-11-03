package mz.co.moseguros.identity.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import mz.co.moseguros.identity.application.PasswordResetService;
import mz.co.moseguros.identity.application.SessionService;
import mz.co.moseguros.identity.application.StaffLoginService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Login de staff, recuperação de palavra-passe, renovação e fim de sessão (públicos; o cabeçalho
 * {@code Authorization} é ignorado). O login dos clientes é por OTP ({@code OtpController}).
 */
@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = IdentityTag.NAME, description = IdentityTag.DESCRIPTION)
public class AuthController {

    private final SessionService sessions;
    private final StaffLoginService staffLogin;
    private final PasswordResetService passwordReset;

    public AuthController(SessionService sessions, StaffLoginService staffLogin, PasswordResetService passwordReset) {
        this.sessions = sessions;
        this.staffLogin = staffLogin;
        this.passwordReset = passwordReset;
    }

    @PostMapping("/login")
    @SecurityRequirements
    @Operation(operationId = "loginStaff", summary = "Login de staff",
            description = "E-mail, palavra-passe e código TOTP. Qualquer um errado → 401 INVALID_CREDENTIALS (sem "
                    + "dizer qual). À 5.ª falha seguida a conta bloqueia 15 minutos: 403 ACCOUNT_LOCKED com "
                    + "retryAfterSeconds e Retry-After. Credenciais certas numa conta desactivada → 403 "
                    + "ACCOUNT_DISABLED; numa organização suspensa → 403 ORGANIZATION_SUSPENDED. A sessão dura 12 h.")
    public TokenResponse login(@Valid @RequestBody StaffLoginRequest request, HttpServletRequest http) {
        return TokenResponse.from(staffLogin.login(request.email(), request.password(), request.totpCode(),
                http.getHeader(HttpHeaders.USER_AGENT)));
    }

    @PostMapping("/password/forgot")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @SecurityRequirements
    @Operation(operationId = "forgotPassword", summary = "Pede a recuperação da palavra-passe de staff",
            description = "Responde sempre 202, sem corpo. Se o e-mail for de uma conta de staff activa ou bloqueada, "
                    + "envia um link válido 30 minutos (no máximo 3 e-mails por conta por hora); os pedidos "
                    + "anteriores por usar deixam de funcionar.")
    public void forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        passwordReset.forgot(request.email());
    }

    @PostMapping("/password/reset")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @SecurityRequirements
    @Operation(operationId = "resetPassword", summary = "Define a palavra-passe nova",
            description = "Com o token do link. Token desconhecido, expirado, usado ou substituído → 400 "
                    + "RESET_TOKEN_INVALID; palavra-passe fraca → 400 PASSWORD_TOO_WEAK (campo newPassword). "
                    + "Desbloqueia a conta e termina todas as sessões; o TOTP não muda.")
    public void resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        passwordReset.reset(request.token(), request.newPassword());
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
