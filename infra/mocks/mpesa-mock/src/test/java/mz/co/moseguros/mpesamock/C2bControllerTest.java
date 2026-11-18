package mz.co.moseguros.mpesamock;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.JsonNode;
import java.net.http.HttpRequest;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class C2bControllerTest extends MpesaMockTestSupport {

    @ParameterizedTest(name = "…{0} → {1} ({2})")
    @CsvSource({
        "258840000000, INS-0,    201, Completed",
        "258841234001, INS-5,    401, Cancelled",
        "258841234002, INS-2006, 422, Failed",
        "258841234003, INS-0,    201, Completed",
        "258841234004, INS-10,   409, Cancelled",
        "258851234005, INS-6,    401, Failed",
        "258841234567, INS-0,    201, Completed",
    })
    void behaviourDependsOnTheLastThreeDigits(String msisdn, String code, int httpStatus, String status) {
        String reference = nextReference();

        Response response = c2b(msisdn, reference);

        assertThat(response.status()).isEqualTo(httpStatus);
        assertThat(response.code()).isEqualTo(code);
        JsonNode body = response.json();
        assertThat(body.fieldNames()).toIterable().containsExactly("output_ConversationID", "output_ResponseCode",
                "output_ResponseDesc", "output_ThirdPartyReference", "output_TransactionID");
        assertThat(body.path("output_ConversationID").asText()).matches("[0-9a-f]{32}");
        assertThat(body.path("output_TransactionID").asText()).matches("[0-9A-Z]{10}");
        assertThat(body.path("output_ThirdPartyReference").asText()).isEqualTo(reference);

        JsonNode registered = transactions().get(0);
        assertThat(registered.path("status").asText()).isEqualTo(status);
        assertThat(registered.path("responseCode").asText()).isEqualTo(code);
        assertThat(registered.path("transactionId").asText()).isEqualTo(body.path("output_TransactionID").asText());
        assertThat(registered.path("conversationId").asText()).isEqualTo(body.path("output_ConversationID").asText());
        assertThat(registered.path("thirdPartyReference").asText()).isEqualTo(reference);
        assertThat(registered.path("transactionReference").asText()).isEqualTo("T12344C");
        assertThat(registered.path("msisdn").asText()).isEqualTo(msisdn);
        assertThat(registered.path("amount").asText()).isEqualTo("10");
        assertThat(registered.path("createdAt").isTextual()).isTrue();
        assertThat(registered.path("completedAt").isTextual()).isTrue();
    }

    @Test
    void successWaitsForTheConfiguredDelay() {
        setDelays(0, 1, 0, 0);
        long start = System.nanoTime();

        Response response = c2b("258840000000", nextReference());

        assertThat(response.code()).isEqualTo("INS-0");
        assertThat((System.nanoTime() - start) / 1_000_000).isGreaterThanOrEqualTo(900);
    }

    @Test
    void tokenEncryptedWithAnotherKeyIsInvalidApiKey() throws NoSuchAlgorithmException {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        String otherToken = token(generator.generateKeyPair().getPublic(), API_KEY);

        Response response = c2b(c2bBody("258841234567", nextReference()), otherToken, ORIGIN);

        assertThat(response.status()).isEqualTo(401);
        assertThat(response.code()).isEqualTo("INS-2");
        assertThat(response.field("output_TransactionID")).isEmpty();
        assertThat(transactions()).isEmpty();
    }

    @Test
    void wrongApiKeyMissingOrMalformedTokenIsInvalidApiKey() {
        Map<String, String> body = c2bBody("258841234567", nextReference());
        assertThat(c2b(body, token(MOCK_PUBLIC_KEY, "outra-chave"), ORIGIN).code()).isEqualTo("INS-2");
        assertThat(c2b(body, null, ORIGIN).code()).isEqualTo("INS-2");
        assertThat(c2b(body, "não-é-base64!", ORIGIN).code()).isEqualTo("INS-2");
        assertThat(transactions()).isEmpty();
    }

    @Test
    void wrongOrMissingOriginIsNotAuthorized() {
        Map<String, String> body = c2bBody("258841234567", nextReference());

        Response wrong = c2b(body, TOKEN, "example.com");
        Response missing = c2b(body, TOKEN, null);

        assertThat(wrong.status()).isEqualTo(401);
        assertThat(wrong.code()).isEqualTo("INS-26");
        assertThat(missing.code()).isEqualTo("INS-26");
        assertThat(transactions()).isEmpty();
    }

    @Test
    void missingFieldIsNotAllParametersProvided() {
        Map<String, String> body = c2bBody("258841234567", nextReference());
        body.remove("input_Amount");

        Response response = c2b(body, TOKEN, ORIGIN);

        assertThat(response.status()).isEqualTo(422);
        assertThat(response.code()).isEqualTo("INS-20");
        assertThat(response.field("output_TransactionID")).isEmpty();
        assertThat(transactions()).isEmpty();
    }

    @Test
    void invalidFormatsAreParameterValidationFailures() {
        assertThat(c2b("258861234567", nextReference()).code()).isEqualTo("INS-21");
        assertThat(c2b("841234567", nextReference()).code()).isEqualTo("INS-21");
        assertThat(c2b("258841234567", "REF-COM-HIFEN").code()).isEqualTo("INS-21");
        assertThat(c2b("258841234567", "A".repeat(21)).code()).isEqualTo("INS-21");

        Map<String, String> zero = c2bBody("258841234567", nextReference());
        zero.put("input_Amount", "0");
        assertThat(c2b(zero, TOKEN, ORIGIN).code()).isEqualTo("INS-21");
        Map<String, String> threeDecimals = c2bBody("258841234567", nextReference());
        threeDecimals.put("input_Amount", "10.123");
        assertThat(c2b(threeDecimals, TOKEN, ORIGIN).code()).isEqualTo("INS-21");
        Map<String, String> badReference = c2bBody("258841234567", nextReference());
        badReference.put("input_TransactionReference", "T 1");
        assertThat(c2b(badReference, TOKEN, ORIGIN).code()).isEqualTo("INS-21");
        assertThat(transactions()).isEmpty();
    }

    @Test
    void acceptsAmountWithTwoDecimals() {
        Map<String, String> body = c2bBody("258841234567", nextReference());
        body.put("input_Amount", "2999.50");

        assertThat(c2b(body, TOKEN, ORIGIN).code()).isEqualTo("INS-0");
    }

    @Test
    void wrongServiceProviderCodeIsInvalidShortcode() {
        Map<String, String> body = c2bBody("258841234567", nextReference());
        body.put("input_ServiceProviderCode", "999999");

        Response response = c2b(body, TOKEN, ORIGIN);

        assertThat(response.status()).isEqualTo(400);
        assertThat(response.code()).isEqualTo("INS-13");
        assertThat(transactions()).isEmpty();
    }

    @Test
    void repeatedThirdPartyReferenceIsDuplicate() {
        String reference = nextReference();
        assertThat(c2b("258841234567", reference).code()).isEqualTo("INS-0");

        Response repeated = c2b("258841234568", reference);

        assertThat(repeated.status()).isEqualTo(409);
        assertThat(repeated.code()).isEqualTo("INS-10");
        assertThat(transactions()).hasSize(2);
        assertThat(transactions().get(0).path("status").asText()).isEqualTo("Cancelled");
    }

    @Test
    void referenceRejectedForValidationCanBeUsedAgain() {
        String reference = nextReference();
        Map<String, String> wrongCode = c2bBody("258841234567", reference);
        wrongCode.put("input_ServiceProviderCode", "999999");
        assertThat(c2b(wrongCode, TOKEN, ORIGIN).code()).isEqualTo("INS-13");

        assertThat(c2b("258841234567", reference).code()).isEqualTo("INS-0");
    }

    @Test
    void pathWithoutTrailingSlashAlsoWorks() {
        Response response = send(withAuth(HttpRequest.newBuilder(
                        uri(C2B_PORT, "/ipg/v1x/c2bPayment/singleStage")), TOKEN, ORIGIN)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(
                        json(c2bBody("258841234567", nextReference())))));

        assertThat(response.code()).isEqualTo("INS-0");
    }

    @Test
    void resetClearsTransactionsAndUsedReferences() {
        String reference = nextReference();
        c2b("258841234567", reference);

        resetMock();

        assertThat(transactions()).isEmpty();
        assertThat(c2b("258841234567", reference).code()).isEqualTo("INS-0");
    }
}
