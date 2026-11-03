package mz.co.moseguros.identity.api;

import java.util.UUID;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import mz.co.moseguros.identity.application.OtpService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Login do cliente por código OTP (público): opções, pedido, consulta do desafio e validação.
 */
@RestController
@RequestMapping("/api/v1/auth/otp")
@Tag(name = IdentityTag.NAME, description = IdentityTag.DESCRIPTION)
public class OtpController {

    /** Cabeçalho com o código, só no perfil {@code local} ({@code moseguros.security.otp.debug-header}). */
    static final String DEBUG_HEADER = "X-Debug-Otp";

    private final OtpService otpService;

    public OtpController(OtpService otpService) {
        this.otpService = otpService;
    }

    @GetMapping("/options")
    @SecurityRequirements
    @Operation(operationId = "getOtpOptions", summary = "Opções do login por código",
            description = "Tipos de identificador e canais activos, conforme as definições actuais.")
    public OtpOptionsResponse options() {
        return OtpOptionsResponse.from(otpService.options());
    }

    @PostMapping("/request")
    @SecurityRequirements
    @Operation(operationId = "requestOtp", summary = "Pede um código",
            description = "Envia um código pelo canal escolhido (ou o por defeito) e, se falhar, pelos outros canais "
                    + "compatíveis. A resposta é igual para identificadores novos e existentes.")
    @ApiResponse(responseCode = "202", description = "Código enviado",
            content = @Content(schema = @Schema(implementation = OtpRequestResponse.class)))
    public ResponseEntity<OtpRequestResponse> request(@Valid @RequestBody RequestOtpRequest request) {
        OtpService.RequestResult result = otpService.request(request.identifier(), request.identifierType(),
                request.channel());
        ResponseEntity.BodyBuilder response = ResponseEntity.status(HttpStatus.ACCEPTED);
        if (result.debugCode() != null) {
            response.header(DEBUG_HEADER, result.debugCode());
        }
        return response.body(OtpRequestResponse.from(result));
    }

    @GetMapping("/challenges/{challengeId}")
    @SecurityRequirements
    @Operation(operationId = "getOtpChallenge", summary = "Estado do envio do código",
            description = "Canal e estado de entrega do código activo (muda se houver alternativa de canal). "
                    + "Desconhecido → 404 OTP_CHALLENGE_NOT_FOUND.")
    public OtpChallengeResponse challenge(@PathVariable UUID challengeId) {
        return OtpChallengeResponse.from(otpService.challenge(challengeId));
    }

    @PostMapping("/verify")
    @SecurityRequirements
    @Operation(operationId = "verifyOtp", summary = "Valida o código e entra",
            description = "Abre a sessão (e cria a conta de cliente, se ainda não existir).")
    public OtpVerifyResponse verify(@Valid @RequestBody VerifyOtpRequest request, HttpServletRequest http) {
        OtpService.LoginResult result = otpService.verify(request.challengeId(), request.code(),
                http.getHeader(HttpHeaders.USER_AGENT));
        return OtpVerifyResponse.from(result);
    }
}
