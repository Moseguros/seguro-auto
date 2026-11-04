package mz.co.moseguros.identity;

import java.util.Objects;
import java.util.UUID;

import mz.co.moseguros.shared.domain.Channel;
import mz.co.moseguros.shared.domain.Money;

/**
 * Pedido de um código para assinar um documento ({@link IdentityApi#requestSignOtp}, S4-04 §4).
 *
 * @param signerUserId     conta do signatário (o cliente dono da proposta)
 * @param documentId       ficheiro assinado ({@code document.file})
 * @param documentHash     SHA-256 do ficheiro, em hexadecimal minúsculo; entra no hash do código
 * @param documentRef      número do documento, mostrado na mensagem ({@code PR-2027-000123})
 * @param insurerName      seguradora, mostrada na mensagem
 * @param total            prémio total, mostrado na mensagem
 * @param assistedByName   mediador que preparou a proposta (S4-06) ou {@code null}
 * @param preferredChannel canal escolhido pelo cliente ou {@code null} (o canal por defeito)
 */
public record SignOtpRequest(UUID signerUserId, UUID documentId, String documentHash, String documentRef,
                             String insurerName, Money total, String assistedByName, Channel preferredChannel) {

    public SignOtpRequest {
        Objects.requireNonNull(signerUserId, "signerUserId");
        Objects.requireNonNull(documentId, "documentId");
        Objects.requireNonNull(documentHash, "documentHash");
        Objects.requireNonNull(documentRef, "documentRef");
        Objects.requireNonNull(insurerName, "insurerName");
        Objects.requireNonNull(total, "total");
        if (!documentHash.matches("[0-9a-f]{64}")) {
            throw new IllegalArgumentException("documentHash must be a lowercase hexadecimal SHA-256");
        }
    }
}
