package mz.co.moseguros.identity.api;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import mz.co.moseguros.identity.application.ContactService.ContactView;
import mz.co.moseguros.identity.domain.OtpChallenge.IdentifierType;

/** Contactos do cliente (só os que existem). */
public record ContactsResponse(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        List<ContactResponse> contacts) {

    static ContactsResponse from(List<ContactView> views) {
        return new ContactsResponse(views.stream()
                .map(view -> new ContactResponse(view.type(), view.value(), view.verified()))
                .toList());
    }

    /** Um contacto, completo. */
    public record ContactResponse(
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            IdentifierType type,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "Telemóvel em E.164 ou e-mail",
                    example = "+258841234567")
            String value,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            boolean verified) {

        @Override
        public String toString() {
            return "ContactResponse[type=" + type + ", verified=" + verified + "]";
        }
    }
}
