package mz.co.moseguros.payment.infrastructure.mpesa;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Resposta da API M-Pesa (S5-04 §1). O adaptador decide sempre pelo {@code output_ResponseCode},
 * qualquer que seja o estado HTTP ({@code httpStatus} fica só para o histórico).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record MpesaResponse(@JsonProperty("output_ResponseCode") String responseCode,
                            @JsonProperty("output_ResponseDesc") String responseDesc,
                            @JsonProperty("output_TransactionID") String transactionId,
                            @JsonProperty("output_ConversationID") String conversationId,
                            @JsonProperty("output_ThirdPartyReference") String thirdPartyReference,
                            @JsonProperty("output_ResponseTransactionStatus") String transactionStatus,
                            int httpStatus) {

    public MpesaResponse withHttpStatus(int status) {
        return new MpesaResponse(responseCode, responseDesc, transactionId, conversationId, thirdPartyReference,
                transactionStatus, status);
    }

    /** {@code output_TransactionID} não vazio, ou {@code null}. */
    public String transactionIdOrNull() {
        return blankToNull(transactionId);
    }

    public String conversationIdOrNull() {
        return blankToNull(conversationId);
    }

    /** Os campos {@code output_*} presentes (não têm dados pessoais nem segredos). */
    public ObjectNode toJson() {
        ObjectNode node = JsonNodeFactory.instance.objectNode();
        put(node, "output_ResponseCode", responseCode);
        put(node, "output_ResponseDesc", responseDesc);
        put(node, "output_TransactionID", transactionId);
        put(node, "output_ConversationID", conversationId);
        put(node, "output_ThirdPartyReference", thirdPartyReference);
        put(node, "output_ResponseTransactionStatus", transactionStatus);
        node.put("httpStatus", httpStatus);
        return node;
    }

    private static void put(ObjectNode node, String name, String value) {
        if (value != null) {
            node.put(name, value);
        }
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value;
    }
}
