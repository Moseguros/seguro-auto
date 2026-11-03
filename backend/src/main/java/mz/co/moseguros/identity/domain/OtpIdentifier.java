package mz.co.moseguros.identity.domain;

import java.util.Objects;

import mz.co.moseguros.identity.domain.OtpChallenge.IdentifierType;
import mz.co.moseguros.shared.domain.Channel;
import mz.co.moseguros.shared.domain.Emails;
import mz.co.moseguros.shared.domain.Msisdn;
import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.error.FieldValidationException;

/**
 * Identificador de quem pede um código: telemóvel ({@code value} = {@code 258XXXXXXXXX}) ou
 * e-mail normalizado. {@link #toString()} é mascarado.
 *
 * @param type  tipo do identificador
 * @param value valor normalizado, gravado em {@code otp_challenge.identifier}
 */
public record OtpIdentifier(IdentifierType type, String value) {

    public OtpIdentifier {
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(value, "value");
    }

    /**
     * Normaliza o identificador recebido num pedido.
     *
     * @throws FieldValidationException no campo {@code field}, com {@code INVALID_MSISDN} ou
     *                                  {@code INVALID_EMAIL}
     */
    public static OtpIdentifier parse(String raw, IdentifierType type, String field) {
        Objects.requireNonNull(type, "type");
        try {
            return switch (type) {
                case MSISDN -> new OtpIdentifier(type, Msisdn.of(raw).value());
                case EMAIL -> new OtpIdentifier(type, Emails.normalize(raw));
            };
        } catch (FieldValidationException e) {
            throw e;
        } catch (DomainException e) {
            throw FieldValidationException.of(field, e);
        }
    }

    /** Telemóvel → SMS ou WhatsApp; e-mail → e-mail. Nunca há canal de um tipo para o outro. */
    public static boolean compatible(IdentifierType type, Channel channel) {
        return switch (type) {
            case MSISDN -> channel == Channel.SMS || channel == Channel.WHATSAPP;
            case EMAIL -> channel == Channel.EMAIL;
        };
    }

    public boolean accepts(Channel channel) {
        return compatible(type, channel);
    }

    /** Para onde se envia: telemóvel em E.164 ({@code +258…}) ou o e-mail. */
    public String destination() {
        return type == IdentifierType.MSISDN ? "+" + value : value;
    }

    public Msisdn msisdn() {
        return type == IdentifierType.MSISDN ? Msisdn.of(value) : null;
    }

    public String email() {
        return type == IdentifierType.EMAIL ? value : null;
    }

    /** {@code +258*****4567} ou {@code a***@exemplo.co.mz}. */
    public String masked() {
        if (type == IdentifierType.MSISDN) {
            return Msisdn.of(value).masked();
        }
        int at = value.indexOf('@');
        return value.charAt(0) + "***" + value.substring(at);
    }

    @Override
    public String toString() {
        return type + ":" + masked();
    }
}
