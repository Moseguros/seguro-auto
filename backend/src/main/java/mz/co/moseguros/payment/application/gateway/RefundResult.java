package mz.co.moseguros.payment.application.gateway;

import com.fasterxml.jackson.databind.JsonNode;

/** Resultado de um reembolso no fornecedor (S7; não usado no S5). */
public record RefundResult(boolean accepted, String providerTxId, JsonNode maskedRawResponse) {
}
