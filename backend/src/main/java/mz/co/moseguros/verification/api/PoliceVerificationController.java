package mz.co.moseguros.verification.api;

import java.time.LocalDate;
import java.util.Set;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mz.co.moseguros.shared.web.ClientIpResolver;
import mz.co.moseguros.shared.web.PageRequests;
import mz.co.moseguros.shared.web.PageResponse;
import mz.co.moseguros.verification.application.VerificationService;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Verificação pelos agentes da polícia (S6-03 §2): permissão {@code verification:police} (perfil
 * {@code POLICE_OFFICER}), sem CAPTCHA, 300 pedidos por minuto por agente; e o histórico das suas consultas.
 */
@RestController
@RequestMapping("/api/v1/police")
@Tag(name = VerificationTag.NAME, description = VerificationTag.DESCRIPTION)
public class PoliceVerificationController {

    static final Set<String> SORTS = Set.of("checkedAt");

    private final VerificationService verifications;
    private final ClientIpResolver clientIp;

    public PoliceVerificationController(VerificationService verifications, ClientIpResolver clientIp) {
        this.verifications = verifications;
        this.clientIp = clientIp;
    }

    @GetMapping("/verifications")
    @Operation(operationId = "verifyPlateAsPolice", summary = "Verifica o seguro pela matrícula (polícia)",
            description = "Permissão verification:police; sem CAPTCHA. A resposta pública mais policyNumber, "
                    + "certificateNumber, vehicleMake e vehicleModel; sem dados pessoais do tomador. Matrícula "
                    + "inválida → 400 VALIDATION_ERROR (plate, INVALID_PLATE). 300 pedidos por minuto → 429 "
                    + "RATE_LIMITED (Retry-After).")
    public PoliceVerificationResponse verifyPlateAsPolice(
            @Parameter(description = "Matrícula", example = "ABC-123-MC") @RequestParam(required = false) String plate,
            HttpServletRequest request, HttpServletResponse response) {
        response.setHeader(HttpHeaders.CACHE_CONTROL, PublicVerificationController.NO_STORE);
        return PoliceVerificationResponse.from(verifications.verifyPlateAsPolice(plate, clientIp.resolve(request)));
    }

    @GetMapping("/verifications/{token}")
    @Operation(operationId = "verifyCertificateTokenAsPolice", summary = "Verifica o seguro pelo código do QR "
            + "(polícia)", description = "Permissão verification:police. Como verifyCertificateToken, mais "
                    + "policyNumber, certificateNumber, vehicleMake e vehicleModel. 300 pedidos por minuto → 429 "
                    + "RATE_LIMITED.")
    public PoliceVerificationResponse verifyCertificateTokenAsPolice(
            @Parameter(description = "Código do QR do certificado (22 caracteres)") @PathVariable String token,
            HttpServletRequest request, HttpServletResponse response) {
        response.setHeader(HttpHeaders.CACHE_CONTROL, PublicVerificationController.NO_STORE);
        return PoliceVerificationResponse.from(verifications.verifyTokenAsPolice(token, clientIp.resolve(request)));
    }

    @GetMapping("/check-history")
    @Operation(operationId = "listMyPoliceChecks", summary = "Lista as consultas do próprio agente",
            description = "Permissão verification:police. Só as consultas do agente; from e to em datas de "
                    + "Africa/Maputo, inclusivas (por defeito os últimos 30 dias; from depois de to → 400 "
                    + "VALIDATION_ERROR from OUT_OF_RANGE). Ordenação checkedAt (desc).")
    public PageResponse<PoliceCheckResponse> listMyPoliceChecks(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @Parameter(description = "checkedAt, com ,asc ou ,desc", example = "checkedAt,desc")
            @RequestParam(required = false) String sort,
            HttpServletResponse response) {
        response.setHeader(HttpHeaders.CACHE_CONTROL, PublicVerificationController.NO_STORE);
        return PageResponse.of(verifications.myChecks(from, to,
                        PageRequests.of(page, size, sort, SORTS, Sort.by(Sort.Direction.DESC, "checkedAt"))),
                PoliceCheckResponse::from);
    }
}
