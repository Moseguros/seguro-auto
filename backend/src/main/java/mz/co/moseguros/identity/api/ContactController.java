package mz.co.moseguros.identity.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import mz.co.moseguros.identity.application.ContactService;
import mz.co.moseguros.identity.application.OtpService;
import mz.co.moseguros.identity.domain.OtpChallenge.IdentifierType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Contactos do cliente autenticado (só clientes, {@code self:profile}; staff → 403): telemóvel e
 * e-mail, confirmados por código OTP.
 */
@RestController
@RequestMapping("/api/v1/me/contacts")
@Tag(name = IdentityTag.NAME, description = IdentityTag.DESCRIPTION)
public class ContactController {

    private final ContactService service;

    public ContactController(ContactService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(operationId = "listMyContacts", summary = "Contactos do cliente")
    public ContactsResponse listMyContacts() {
        return ContactsResponse.from(service.listMyContacts());
    }

    @PostMapping
    @Operation(operationId = "startContactVerification", summary = "Envia um código para um contacto novo",
            description = "Acrescenta ou troca o telemóvel ou o e-mail: envia um código com as regras de canal, "
                    + "alternativa e limites do login. Para reenviar, chama-se de novo. Contacto de outra conta → "
                    + "409 CONTACT_ALREADY_IN_USE; igual ao actual e verificado → 409 CONTACT_ALREADY_VERIFIED.")
    @ApiResponse(responseCode = "202", description = "Código enviado",
            content = @Content(schema = @Schema(implementation = OtpRequestResponse.class)))
    public ResponseEntity<OtpRequestResponse> startContactVerification(
            @Valid @RequestBody StartContactVerificationRequest request) {
        OtpService.RequestResult result = service.startVerification(request.type(), request.value(),
                request.channel());
        ResponseEntity.BodyBuilder response = ResponseEntity.status(HttpStatus.ACCEPTED);
        if (result.debugCode() != null) {
            response.header(OtpController.DEBUG_HEADER, result.debugCode());
        }
        return response.body(OtpRequestResponse.from(result));
    }

    @PostMapping("/verify")
    @Operation(operationId = "verifyContact", summary = "Confirma o código e grava o contacto",
            description = "Mesmas validações do verifyOtp. O contacto substitui o anterior do mesmo tipo, "
                    + "verificado. Desafio de outra finalidade ou de outro cliente → 404 OTP_CHALLENGE_NOT_FOUND.")
    public ContactsResponse verifyContact(@Valid @RequestBody VerifyContactRequest request) {
        return ContactsResponse.from(service.verify(request.challengeId(), request.code()));
    }

    @DeleteMapping("/{type}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(operationId = "removeMyContact", summary = "Remove um contacto",
            description = "A conta não pode ficar sem nenhum contacto verificado → 400 LAST_CONTACT.")
    @ApiResponse(responseCode = "204", description = "Removido (ou já não existia)")
    public void removeMyContact(@PathVariable IdentifierType type) {
        service.removeMyContact(type);
    }
}
