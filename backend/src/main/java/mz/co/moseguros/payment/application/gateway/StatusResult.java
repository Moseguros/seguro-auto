package mz.co.moseguros.payment.application.gateway;

import com.fasterxml.jackson.databind.JsonNode;
import mz.co.moseguros.payment.FailureCode;

/** Resultado de uma consulta de estado (S5-04). */
public record StatusResult(Outcome outcome, FailureCode failureCode, String providerTxId,
                           JsonNode maskedRawResponse) {

    /** Resultado da consulta. */
    public enum Outcome { SUCCEEDED, FAILED, STILL_UNKNOWN }

    public static StatusResult stillUnknown() {
        return new StatusResult(Outcome.STILL_UNKNOWN, null, null, null);
    }
}
