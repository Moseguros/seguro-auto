package mz.co.moseguros.payment.application.gateway;

import java.util.Objects;

import com.fasterxml.jackson.databind.JsonNode;
import mz.co.moseguros.payment.FailureCode;

/**
 * Resultado de uma consulta de estado (S5-04 §3 e §4).
 *
 * @param maskedRawResponse pedido e resposta já mascarados (gravados no {@code payment_event}
 *                          {@code STATUS_QUERY}), ou {@code null}
 */
public record StatusResult(Outcome outcome, FailureCode failureCode, String providerTxId,
                           JsonNode maskedRawResponse) {

    /** Resultado da consulta. */
    public enum Outcome { SUCCEEDED, FAILED, STILL_UNKNOWN }

    public StatusResult {
        Objects.requireNonNull(outcome, "outcome");
        if (outcome == Outcome.FAILED && failureCode == null) {
            throw new IllegalArgumentException("A FAILED status needs a failureCode");
        }
    }

    public static StatusResult stillUnknown() {
        return new StatusResult(Outcome.STILL_UNKNOWN, null, null, null);
    }

    public static StatusResult stillUnknown(JsonNode maskedRawResponse) {
        return new StatusResult(Outcome.STILL_UNKNOWN, null, null, maskedRawResponse);
    }
}
