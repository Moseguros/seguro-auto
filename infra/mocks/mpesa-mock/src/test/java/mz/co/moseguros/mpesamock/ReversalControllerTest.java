package mz.co.moseguros.mpesamock;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.jupiter.api.Test;

class ReversalControllerTest extends MpesaMockTestSupport {

    @Test
    void reversingACompletedTransactionMarksItReversed() {
        String transactionId = c2b("258841234567", nextReference()).field("output_TransactionID");

        Response response = reversal(reversalBody(transactionId));

        assertThat(response.status()).isEqualTo(200);
        assertThat(response.code()).isEqualTo("INS-0");
        assertThat(response.field("output_TransactionID")).matches("[0-9A-Z]{10}").isNotEqualTo(transactionId);
        assertThat(query(transactionId, nextReference()).field("output_ResponseTransactionStatus"))
                .isEqualTo("Reversed");
        assertThat(transactions().get(0).path("operation").asText()).isEqualTo("REVERSAL");
        assertThat(transactions().get(0).path("originalTransactionId").asText()).isEqualTo(transactionId);
    }

    @Test
    void partialReversalUpToTheOriginalAmount() {
        String transactionId = c2b("258841234567", nextReference()).field("output_TransactionID");
        Map<String, String> tooMuch = reversalBody(transactionId);
        tooMuch.put("input_ReversalAmount", "10.01");
        Map<String, String> partial = reversalBody(transactionId);
        partial.put("input_ReversalAmount", "4.50");

        assertThat(reversal(tooMuch).code()).isEqualTo("INS-15");
        assertThat(reversal(partial).code()).isEqualTo("INS-0");
        assertThat(transactions().get(0).path("amount").asText()).isEqualTo("4.50");
    }

    @Test
    void unknownOrNotCompletedTransactionIsInvalidTransactionId() {
        String failed = c2b("258841234005", nextReference()).field("output_TransactionID");
        String completed = c2b("258841234567", nextReference()).field("output_TransactionID");
        assertThat(reversal(reversalBody(completed)).code()).isEqualTo("INS-0");

        assertThat(reversal(reversalBody("ZZZZZZZZZZ")).code()).isEqualTo("INS-18");
        assertThat(reversal(reversalBody(failed)).code()).isEqualTo("INS-18");
        assertThat(reversal(reversalBody(completed)).code()).isEqualTo("INS-18");
    }

    @Test
    void wrongSecurityCredentialIsRejected() {
        String transactionId = c2b("258841234567", nextReference()).field("output_TransactionID");
        Map<String, String> body = reversalBody(transactionId);
        body.put("input_SecurityCredential", "errada");

        Response response = reversal(body);

        assertThat(response.status()).isEqualTo(400);
        assertThat(response.code()).isEqualTo("INS-25");
        assertThat(query(transactionId, nextReference()).field("output_ResponseTransactionStatus"))
                .isEqualTo("Completed");
    }

    @Test
    void wrongInitiatorIsRejected() {
        Map<String, String> body = reversalBody(c2b("258841234567", nextReference()).field("output_TransactionID"));
        body.put("input_InitiatorIdentifier", "outro");

        assertThat(reversal(body).code()).isEqualTo("INS-24");
    }

    @Test
    void missingFieldIsNotAllParametersProvided() {
        Map<String, String> body = reversalBody("ZZZZZZZZZZ");
        body.remove("input_SecurityCredential");

        Response response = reversal(body);

        assertThat(response.status()).isEqualTo(422);
        assertThat(response.code()).isEqualTo("INS-20");
    }
}
