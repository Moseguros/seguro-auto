package mz.co.moseguros.verification.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mz.co.moseguros.verification.application.VerificationService;
import mz.co.moseguros.verification.application.VerificationService.CaptchaInfo;
import mz.co.moseguros.shared.web.ClientIpResolver;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Verificação pública (S6-03 §2): sem autenticação ({@code /api/v1/public/**} está nos caminhos públicos,
 * o {@code Authorization} é ignorado). 60 pedidos por minuto e por IP (código e matrícula juntos).
 */
@RestController
@RequestMapping("/api/v1/public/verifications")
@Tag(name = VerificationTag.NAME, description = VerificationTag.DESCRIPTION)
@SecurityRequirements
public class PublicVerificationController {

    static final String CAPTCHA_HEADER = "X-Captcha-Token";
    static final String NO_STORE = "no-store";

    private final VerificationService verifications;
    private final ClientIpResolver clientIp;

    public PublicVerificationController(VerificationService verifications, ClientIpResolver clientIp) {
        this.verifications = verifications;
        this.clientIp = clientIp;
    }

    @GetMapping("/config")
    @Operation(operationId = "getVerificationConfig", summary = "CAPTCHA da verificação por matrícula",
            description = "Público, sem limite de pedidos. captchaProvider TURNSTILE (com captchaSiteKey) ou NONE "
                    + "(sem CAPTCHA; o cabeçalho X-Captcha-Token deixa de ser exigido).")
    public VerificationConfigResponse getVerificationConfig(HttpServletResponse response) {
        response.setHeader(HttpHeaders.CACHE_CONTROL, NO_STORE);
        CaptchaInfo info = verifications.captchaInfo();
        return new VerificationConfigResponse(info.provider(), info.siteKey());
    }

    @GetMapping("/{token}")
    @Operation(operationId = "verifyCertificateToken", summary = "Verifica o seguro pelo código do QR",
            description = "Público, sem CAPTCHA. O status é o da apólice agora; certificateStatus diz se o "
                    + "certificado do código ainda é o actual. Código desconhecido ou com formato errado → 200 "
                    + "NOT_FOUND. Nunca devolve dados do tomador nem os números da apólice e do certificado. "
                    + "60 pedidos por minuto e por IP → 429 RATE_LIMITED (Retry-After).")
    public PublicVerificationResponse verifyCertificateToken(
            @Parameter(description = "Código do QR do certificado (22 caracteres)") @PathVariable String token,
            HttpServletRequest request, HttpServletResponse response) {
        response.setHeader(HttpHeaders.CACHE_CONTROL, NO_STORE);
        return PublicVerificationResponse.from(verifications.verifyTokenPublic(token, clientIp.resolve(request)));
    }

    @GetMapping
    @Operation(operationId = "verifyPlate", summary = "Verifica o seguro pela matrícula",
            description = "Público, com CAPTCHA: cabeçalho X-Captcha-Token obrigatório quando o fornecedor não é "
                    + "NONE (sem ele → 400 CAPTCHA_REQUIRED; recusado → 400 CAPTCHA_INVALID; fornecedor "
                    + "indisponível → 503 CAPTCHA_UNAVAILABLE). Matrícula em falta ou inválida → 400 "
                    + "VALIDATION_ERROR (plate, INVALID_PLATE); aceita espaços, hífenes e minúsculas. "
                    + "60 pedidos por minuto e por IP → 429 RATE_LIMITED (Retry-After).")
    public PublicVerificationResponse verifyPlate(
            @Parameter(description = "Matrícula", example = "ABC-123-MC") @RequestParam(required = false) String plate,
            @Parameter(description = "Token do widget do CAPTCHA") @RequestHeader(name = CAPTCHA_HEADER,
                    required = false) String captchaToken,
            HttpServletRequest request, HttpServletResponse response) {
        response.setHeader(HttpHeaders.CACHE_CONTROL, NO_STORE);
        return PublicVerificationResponse.from(
                verifications.verifyPlatePublic(plate, captchaToken, clientIp.resolve(request)));
    }
}
