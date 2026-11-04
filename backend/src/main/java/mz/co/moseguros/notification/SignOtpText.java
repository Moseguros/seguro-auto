package mz.co.moseguros.notification;

import java.util.Objects;

/**
 * Dados da mensagem com o código de assinatura de uma proposta ({@link NotificationApi#sendSignOtp},
 * S4-04 §4).
 *
 * @param documentRef    número da proposta ({@code PR-2027-000123})
 * @param insurerName    nome da seguradora
 * @param totalFormatted prémio total já formatado na língua da mensagem, como no PDF
 * @param assistedByName nome do mediador que preparou a proposta (S4-06) ou {@code null}
 */
public record SignOtpText(String documentRef, String insurerName, String totalFormatted, String assistedByName) {

    public SignOtpText {
        Objects.requireNonNull(documentRef, "documentRef");
        Objects.requireNonNull(insurerName, "insurerName");
        Objects.requireNonNull(totalFormatted, "totalFormatted");
    }

    /** Assinatura assistida por um mediador: usa os textos {@code otp.sign-assisted.*}. */
    public boolean assisted() {
        return assistedByName != null && !assistedByName.isBlank();
    }
}
