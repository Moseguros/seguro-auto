package mz.co.moseguros.payment.infrastructure.mpesa;

import java.security.SecureRandom;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import mz.co.moseguros.shared.domain.Msisdn;

/**
 * Corpos dos pedidos à API M-Pesa da Vodacom (S5-04 §1), com os nomes dos campos da API. Cada um
 * sabe dar a sua versão mascarada para o {@code payment_event} (telemóvel mascarado; nunca a
 * {@code input_SecurityCredential}).
 */
public final class MpesaRequests {

    /** Comprimento das {@code input_ThirdPartyReference} geradas. */
    public static final int THIRD_PARTY_REFERENCE_LENGTH = 12;
    private static final char[] ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
    private static final SecureRandom RANDOM = new SecureRandom();

    private MpesaRequests() {
    }

    /** {@value #THIRD_PARTY_REFERENCE_LENGTH} caracteres {@code [A-Z0-9]} aleatórios ({@code SecureRandom}). */
    public static String newThirdPartyReference() {
        char[] chars = new char[THIRD_PARTY_REFERENCE_LENGTH];
        for (int i = 0; i < chars.length; i++) {
            chars[i] = ALPHABET[RANDOM.nextInt(ALPHABET.length)];
        }
        return new String(chars);
    }

    /** {@code POST :c2b /ipg/v1x/c2bPayment/singleStage/}. */
    public record C2b(@JsonProperty("input_TransactionReference") String transactionReference,
                      @JsonProperty("input_CustomerMSISDN") String customerMsisdn,
                      @JsonProperty("input_Amount") String amount,
                      @JsonProperty("input_ThirdPartyReference") String thirdPartyReference,
                      @JsonProperty("input_ServiceProviderCode") String serviceProviderCode) {

        public C2b {
            Objects.requireNonNull(transactionReference, "transactionReference");
            Objects.requireNonNull(customerMsisdn, "customerMsisdn");
            Objects.requireNonNull(amount, "amount");
            Objects.requireNonNull(thirdPartyReference, "thirdPartyReference");
            Objects.requireNonNull(serviceProviderCode, "serviceProviderCode");
        }

        public ObjectNode masked() {
            ObjectNode node = JsonNodeFactory.instance.objectNode();
            node.put("input_TransactionReference", transactionReference);
            node.put("input_CustomerMSISDN", maskMsisdn(customerMsisdn));
            node.put("input_Amount", amount);
            node.put("input_ThirdPartyReference", thirdPartyReference);
            node.put("input_ServiceProviderCode", serviceProviderCode);
            return node;
        }

        @Override
        public String toString() {
            return masked().toString();
        }
    }

    /** {@code GET :query /ipg/v1x/queryTransactionStatus/} (parâmetros na query string). */
    public record Query(String queryReference, String serviceProviderCode, String thirdPartyReference) {

        public Query {
            Objects.requireNonNull(queryReference, "queryReference");
            Objects.requireNonNull(serviceProviderCode, "serviceProviderCode");
            Objects.requireNonNull(thirdPartyReference, "thirdPartyReference");
        }

        public Map<String, String> params() {
            Map<String, String> params = new LinkedHashMap<>();
            params.put("input_QueryReference", queryReference);
            params.put("input_ServiceProviderCode", serviceProviderCode);
            params.put("input_ThirdPartyReference", thirdPartyReference);
            return params;
        }

        public ObjectNode masked() {
            ObjectNode node = JsonNodeFactory.instance.objectNode();
            params().forEach(node::put);
            return node;
        }
    }

    /** {@code PUT :reversal /ipg/v1x/reversal/}. */
    public record Reversal(@JsonProperty("input_TransactionID") String transactionId,
                           @JsonProperty("input_SecurityCredential") String securityCredential,
                           @JsonProperty("input_InitiatorIdentifier") String initiatorIdentifier,
                           @JsonProperty("input_ThirdPartyReference") String thirdPartyReference,
                           @JsonProperty("input_ServiceProviderCode") String serviceProviderCode,
                           @JsonProperty("input_ReversalAmount") String reversalAmount) {

        public Reversal {
            Objects.requireNonNull(transactionId, "transactionId");
            Objects.requireNonNull(securityCredential, "securityCredential");
            Objects.requireNonNull(initiatorIdentifier, "initiatorIdentifier");
            Objects.requireNonNull(thirdPartyReference, "thirdPartyReference");
            Objects.requireNonNull(serviceProviderCode, "serviceProviderCode");
            Objects.requireNonNull(reversalAmount, "reversalAmount");
        }

        /** Sem a {@code input_SecurityCredential}. */
        public ObjectNode masked() {
            ObjectNode node = JsonNodeFactory.instance.objectNode();
            node.put("input_TransactionID", transactionId);
            node.put("input_InitiatorIdentifier", initiatorIdentifier);
            node.put("input_ThirdPartyReference", thirdPartyReference);
            node.put("input_ServiceProviderCode", serviceProviderCode);
            node.put("input_ReversalAmount", reversalAmount);
            return node;
        }

        @Override
        public String toString() {
            return masked().toString();
        }
    }

    /** {@code 258841234567 → +258*****4567} (a máscara do {@link Msisdn}). */
    static String maskMsisdn(String msisdn) {
        try {
            return Msisdn.of(msisdn).masked();
        } catch (RuntimeException e) {
            return "***";
        }
    }
}
