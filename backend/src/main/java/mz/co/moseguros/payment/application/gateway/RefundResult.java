package mz.co.moseguros.payment.application.gateway;

import java.util.Objects;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Resultado de uma reversão no fornecedor (S5-04 §7). Nenhum endpoint nem job a usa no S5: as
 * devoluções fazem-se sempre fora da plataforma (estornos com endpoint no S7).
 *
 * @param providerTxId      {@code output_TransactionID} da reversão ({@code SUCCEEDED})
 * @param responseCode      código do fornecedor ({@code output_ResponseCode}), ou {@code null} sem resposta
 * @param maskedRawResponse pedido e resposta já mascarados, ou {@code null}
 */
public record RefundResult(Outcome outcome, String providerTxId, String responseCode, JsonNode maskedRawResponse) {

    /** Resultado da reversão. */
    public enum Outcome { SUCCEEDED, FAILED, UNKNOWN }

    public RefundResult {
        Objects.requireNonNull(outcome, "outcome");
    }

    public boolean accepted() {
        return outcome == Outcome.SUCCEEDED;
    }
}
