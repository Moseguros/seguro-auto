package mz.co.moseguros.identity.domain;

import java.util.Objects;
import java.util.UUID;

/**
 * Contexto de um código de assinatura ({@code identity.otp_challenge.sign_context}, só com
 * {@code purpose = SIGN}; S4-04 §4): o documento assinado e os dados da mensagem. Fica no desafio para
 * a alternativa de canal (síncrona e por recibo) gerar um código novo ligado ao mesmo documento e
 * reenviar a mesma mensagem. Sem dados pessoais.
 *
 * @param documentHash   SHA-256 do documento; entra no hash do código
 * @param totalAmount    prémio total em centavos (MZN)
 * @param assistedByName mediador (S4-06) ou {@code null}
 */
public record OtpSignContext(UUID documentId, String documentHash, String documentRef, String insurerName,
                             long totalAmount, String assistedByName) {

    public OtpSignContext {
        Objects.requireNonNull(documentId, "documentId");
        Objects.requireNonNull(documentHash, "documentHash");
        Objects.requireNonNull(documentRef, "documentRef");
        Objects.requireNonNull(insurerName, "insurerName");
    }
}
