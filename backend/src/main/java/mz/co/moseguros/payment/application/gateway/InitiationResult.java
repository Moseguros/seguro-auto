package mz.co.moseguros.payment.application.gateway;

import java.util.Objects;

import com.fasterxml.jackson.databind.JsonNode;
import mz.co.moseguros.payment.FailureCode;

/**
 * Resultado do envio de uma intenção ao fornecedor (S5-02 §6), aplicado pelo {@code IntentStateService}.
 *
 * @param reference         só para entidade e referência ({@code PENDING})
 * @param maskedRawResponse resposta do fornecedor já mascarada (sem tokens nem segredos), ou {@code null}
 */
public record InitiationResult(Outcome outcome, FailureCode failureCode, String providerTxId,
                               String providerConversationId, String thirdPartyReference, ReferenceData reference,
                               JsonNode maskedRawResponse) {

    /** Resultado do envio. */
    public enum Outcome { PENDING, SUCCEEDED, FAILED, UNKNOWN }

    public InitiationResult {
        Objects.requireNonNull(outcome, "outcome");
        if (outcome == Outcome.FAILED && failureCode == null) {
            throw new IllegalArgumentException("A FAILED initiation needs a failureCode");
        }
    }

    public static InitiationResult pending(String providerTxId, String providerConversationId,
                                           String thirdPartyReference, JsonNode maskedRawResponse) {
        return new InitiationResult(Outcome.PENDING, null, providerTxId, providerConversationId, thirdPartyReference,
                null, maskedRawResponse);
    }

    public static InitiationResult reference(ReferenceData reference, JsonNode maskedRawResponse) {
        return new InitiationResult(Outcome.PENDING, null, null, null, null,
                Objects.requireNonNull(reference, "reference"), maskedRawResponse);
    }

    public static InitiationResult failed(FailureCode code, JsonNode maskedRawResponse) {
        return new InitiationResult(Outcome.FAILED, code, null, null, null, null, maskedRawResponse);
    }
}
