package mz.co.moseguros.mpesamock;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.JsonNode;
import java.time.Duration;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.Test;

class QueryControllerTest extends MpesaMockTestSupport {

    @Test
    void queryByTransactionIdAndByOriginalThirdPartyReference() {
        String reference = nextReference();
        Response payment = c2b("258841234002", reference);
        String transactionId = payment.field("output_TransactionID");
        String queryReference = nextReference();

        Response byId = query(transactionId, queryReference);
        Response byReference = query(reference, nextReference());

        assertThat(byId.status()).isEqualTo(200);
        assertThat(byId.code()).isEqualTo("INS-0");
        JsonNode body = byId.json();
        assertThat(body.fieldNames()).toIterable().containsExactly("output_ConversationID", "output_ResponseCode",
                "output_ResponseDesc", "output_ResponseTransactionStatus", "output_ThirdPartyReference");
        assertThat(body.path("output_ResponseTransactionStatus").asText()).isEqualTo("Failed");
        assertThat(body.path("output_ThirdPartyReference").asText()).isEqualTo(queryReference);
        assertThat(byReference.field("output_ResponseTransactionStatus")).isEqualTo("Failed");
    }

    @Test
    void slowNumberIsPendingBeforeTheDelayAndCompletedAfter() {
        setDelays(0, 0, 2, 0);
        String reference = nextReference();

        CompletableFuture<Response> payment = CompletableFuture.supplyAsync(() -> c2b("258841234003", reference));
        waitUntilRegistered(reference);

        assertThat(query(reference, nextReference()).field("output_ResponseTransactionStatus")).isEqualTo("Pending");
        assertThat(payment).isNotDone();

        Response result = payment.join();
        assertThat(result.code()).isEqualTo("INS-0");
        assertThat(query(result.field("output_TransactionID"), nextReference())
                .field("output_ResponseTransactionStatus")).isEqualTo("Completed");
    }

    @Test
    void waitingPaymentDoesNotBlockOtherRequests() {
        setDelays(0, 0, 3, 0);
        String slowReference = nextReference();
        CompletableFuture<Response> slow = CompletableFuture.supplyAsync(() -> c2b("258841234003", slowReference));
        waitUntilRegistered(slowReference);

        long start = System.nanoTime();
        assertThat(c2b("258841234567", nextReference()).code()).isEqualTo("INS-0");
        assertThat(Duration.ofNanos(System.nanoTime() - start)).isLessThan(Duration.ofSeconds(2));
        assertThat(slow).isNotDone();
        slow.join();
    }

    @Test
    void unknownReferenceIsNotApplicable() {
        Response response = query("DESCONHECIDA1", nextReference());

        assertThat(response.code()).isEqualTo("INS-0");
        assertThat(response.field("output_ResponseTransactionStatus")).isEqualTo("N/A");
    }

    @Test
    void missingParameterIsNotAllParametersProvided() {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("input_QueryReference", "ABC");
        params.put("input_ServiceProviderCode", SERVICE_PROVIDER_CODE);

        Response response = query(params);

        assertThat(response.status()).isEqualTo(422);
        assertThat(response.code()).isEqualTo("INS-20");
    }

    @Test
    void wrongServiceProviderCodeIsInvalidShortcode() {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("input_QueryReference", "ABC");
        params.put("input_ServiceProviderCode", "999999");
        params.put("input_ThirdPartyReference", nextReference());

        assertThat(query(params).code()).isEqualTo("INS-13");
    }

    @Test
    void queryNeedsTheSameAuthentication() {
        Response response = send(withAuth(java.net.http.HttpRequest.newBuilder(uri(QUERY_PORT,
                        "/ipg/v1x/queryTransactionStatus/?input_QueryReference=A&input_ServiceProviderCode=171717"
                                + "&input_ThirdPartyReference=B")), "lixo", ORIGIN).GET());

        assertThat(response.status()).isEqualTo(401);
        assertThat(response.code()).isEqualTo("INS-2");
    }

    private static void waitUntilRegistered(String reference) {
        Instant deadline = Instant.now().plusSeconds(5);
        while (Instant.now().isBefore(deadline)) {
            for (JsonNode t : transactions()) {
                if (reference.equals(t.path("thirdPartyReference").asText())) {
                    return;
                }
            }
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
        throw new AssertionError("transacção " + reference + " não foi registada");
    }
}
