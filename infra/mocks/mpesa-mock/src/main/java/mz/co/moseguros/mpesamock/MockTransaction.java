package mz.co.moseguros.mpesamock;

import java.time.Instant;

/**
 * Transacção registada pelo simulador. Os campos mutáveis (estado, código, conclusão) mudam no
 * fim da espera do C2B ou numa reversão; por isso são {@code volatile}.
 */
final class MockTransaction {

    /** Valores de {@code output_ResponseTransactionStatus}. */
    enum Status { Pending, Completed, Cancelled, Failed, Reversed }

    enum Operation { C2B, REVERSAL }

    private final Operation operation;
    private final String transactionId;
    private final String conversationId;
    private final String thirdPartyReference;
    private final String transactionReference;
    private final String msisdn;
    private final String amount;
    private final String originalTransactionId;
    private final Instant createdAt;
    private volatile Status status;
    private volatile String responseCode;
    private volatile Instant completedAt;

    MockTransaction(Operation operation, String transactionId, String conversationId, String thirdPartyReference,
            String transactionReference, String msisdn, String amount, String originalTransactionId,
            Instant createdAt, Status status, String responseCode) {
        this.operation = operation;
        this.transactionId = transactionId;
        this.conversationId = conversationId;
        this.thirdPartyReference = thirdPartyReference;
        this.transactionReference = transactionReference;
        this.msisdn = msisdn;
        this.amount = amount;
        this.originalTransactionId = originalTransactionId;
        this.createdAt = createdAt;
        this.status = status;
        this.responseCode = responseCode;
    }

    synchronized void complete(Status finalStatus, String code, Instant at) {
        this.status = finalStatus;
        this.responseCode = code;
        this.completedAt = at;
    }

    /** Passa {@code Completed → Reversed}; devolve {@code false} se já não estava {@code Completed}. */
    synchronized boolean reverse() {
        if (status != Status.Completed) {
            return false;
        }
        status = Status.Reversed;
        return true;
    }

    public Operation getOperation() {
        return operation;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getConversationId() {
        return conversationId;
    }

    public String getThirdPartyReference() {
        return thirdPartyReference;
    }

    public String getTransactionReference() {
        return transactionReference;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public String getAmount() {
        return amount;
    }

    public String getOriginalTransactionId() {
        return originalTransactionId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Status getStatus() {
        return status;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }
}
