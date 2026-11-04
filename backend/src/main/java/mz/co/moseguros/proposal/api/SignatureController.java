package mz.co.moseguros.proposal.api;

import java.util.UUID;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import mz.co.moseguros.proposal.application.ProposalJson;
import mz.co.moseguros.proposal.application.SignatureService;
import mz.co.moseguros.shared.web.ClientIpResolver;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Assinatura electrónica simples da proposta por OTP sobre o hash do PDF (S4-04 §7): pedido do código
 * (gera o PDF) e confirmação. Cliente dono ({@code self:proposals}) ou mediador da proposta
 * ({@code proposal:create-for-client}, S4-06).
 */
@RestController
@RequestMapping("/api/v1/proposals")
@Tag(name = ProposalTag.NAME, description = ProposalTag.DESCRIPTION)
public class SignatureController {

    /** Cabeçalho com o código, só no perfil {@code local} ({@code moseguros.security.otp.debug-header}, S1-03). */
    static final String DEBUG_HEADER = "X-Debug-Otp";

    private final SignatureService signatures;
    private final ProposalJson json;
    private final ClientIpResolver clientIp;

    public SignatureController(SignatureService signatures, ProposalJson json, ClientIpResolver clientIp) {
        this.signatures = signatures;
        this.json = json;
        this.clientIp = clientIp;
    }

    @PostMapping("/{id}/sign/request")
    @Operation(operationId = "requestProposalSignature", summary = "Gera o PDF da proposta e envia o código de assinatura",
            description = "Permissão self:proposals ou proposal:create-for-client. Por esta ordem: proposta em DRAFT ou "
                    + "INFO_REQUESTED (senão 409 PROPOSAL_NOT_EDITABLE); version diferente → 409 "
                    + "CONCURRENT_MODIFICATION; documentsAcknowledged ≠ true → 400 (campo documentsAcknowledged, "
                    + "MUST_BE_TRUE); passos em falta → 409 PROPOSAL_INCOMPLETE com missing (DECLARATIONS, SUITABILITY, "
                    + "SUITABILITY_ACKNOWLEDGEMENT, INSPECTION, START_DATE); sem telemóvel nem e-mail verificado → 409 "
                    + "SIGNER_CONTACT_MISSING. Gera o PDF (SHA-256 em documentHash) e envia ao tomador um código que só "
                    + "assina esse PDF; os limites do código são os do login (429 OTP_RATE_LIMITED / OTP_RESEND_TOO_SOON "
                    + "com Retry-After). Pedir de novo substitui o pedido anterior; não altera contentRevision. "
                    + "Proposta de outro cliente → 404 PROPOSAL_NOT_FOUND. Pedido pelo mediador da proposta (S4-06): "
                    + "assinatura assistida, o código vai para o telemóvel da conta do cliente com o nome do mediador "
                    + "na mensagem.")
    @ApiResponse(responseCode = "202", description = "Código enviado",
            content = @Content(schema = @Schema(implementation = SignatureRequestResponse.class)))
    public ResponseEntity<SignatureRequestResponse> request(@PathVariable UUID id,
                                                            @RequestBody RequestSignatureRequest request) {
        SignatureService.SignRequest result = signatures.request(id, request.documentsAcknowledged(),
                request.channel(), request.version());
        ResponseEntity.BodyBuilder response = ResponseEntity.status(HttpStatus.ACCEPTED);
        if (result.challenge().debugCode() != null) {
            response.header(DEBUG_HEADER, result.challenge().debugCode());
        }
        return response.body(SignatureRequestResponse.from(result));
    }

    @PostMapping("/{id}/sign/confirm")
    @Operation(operationId = "confirmProposalSignature", summary = "Confirma a assinatura com o código recebido",
            description = "Permissão self:proposals ou proposal:create-for-client. Proposta em DRAFT ou INFO_REQUESTED "
                    + "(senão 409 PROPOSAL_NOT_EDITABLE); sem pedido em curso ou com outro challengeId → 409 "
                    + "SIGN_REQUEST_NOT_FOUND; proposta alterada depois do pedido → 409 "
                    + "PROPOSAL_CHANGED_SINCE_SIGN_REQUEST (pedir de novo); erros do código como no login "
                    + "(OTP_INVALID com attemptsRemaining, OTP_EXPIRED, OTP_SUPERSEDED, OTP_ALREADY_USED, "
                    + "OTP_ATTEMPTS_EXCEEDED). Grava a assinatura ligada ao hash do PDF (bloco signature, "
                    + "checklist.signature = DONE) e renova expiresAt. Qualquer alteração posterior da proposta "
                    + "invalida a assinatura. Confirmada por um utilizador do mediador (S4-06) → signature.assisted = "
                    + "true; pelo próprio cliente → não assistida.")
    public ProposalResponse confirm(@PathVariable UUID id, @RequestBody ConfirmSignatureRequest request,
                                    HttpServletRequest http) {
        return ProposalResponse.from(signatures.confirm(id, request.challengeId(), request.code(),
                clientIp.resolve(http), http.getHeader(HttpHeaders.USER_AGENT)), json);
    }
}
