package mz.co.moseguros.payment.api;

import static io.restassured.RestAssured.given;
import static mz.co.moseguros.payment.PaymentFlowSupport.INTENTS;
import static mz.co.moseguros.payment.PaymentFlowSupport.MY_PAYMENTS;
import static mz.co.moseguros.payment.PaymentFlowSupport.id;
import static mz.co.moseguros.payment.PaymentFlowSupport.payReference;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import mz.co.moseguros.SecurityTestConfiguration;
import mz.co.moseguros.SecurityTestConfiguration.IdentityFixtures;
import mz.co.moseguros.SecurityTestConfiguration.MutableClock;
import mz.co.moseguros.TestcontainersConfiguration;
import mz.co.moseguros.identity.IdentityApi;
import mz.co.moseguros.notification.FakeOtpDeliveryChannels;
import mz.co.moseguros.payment.PaymentFlowSupport;
import mz.co.moseguros.payment.PaymentFlowSupport.Seg;
import mz.co.moseguros.rating.QuoteTestSupport.Customer;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

/**
 * Pagamentos do cliente (S5-02 §7): a lista só com os seus, a referência por SMS (com o limite de 3) e
 * em PDF, que só o pagador descarrega.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestcontainersConfiguration.class, SecurityTestConfiguration.class})
@ActiveProfiles("test")
@TestPropertySource(properties = mz.co.moseguros.payment.TestGateway.PROPERTY)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MyPaymentsIT {

    @LocalServerPort
    int port;

    @Autowired
    IdentityFixtures fixtures;

    @Autowired
    IdentityApi identityApi;

    @Autowired
    MutableClock clock;

    @Autowired
    FakeOtpDeliveryChannels channels;

    PaymentFlowSupport support;
    Seg insurer;
    String entity;

    @BeforeAll
    void setUpAll() throws Exception {
        RestAssured.port = port;
        support = new PaymentFlowSupport(fixtures, identityApi, clock);
        support.createTemplate();
        insurer = support.insurer("Seguradora Cliente");
        entity = support.enableBoth(insurer.id());
    }

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        clock.reset();
        channels.reset();
    }

    static Response sms(String token, UUID intentId, String msisdn, String language) {
        var request = given().auth().oauth2(token).contentType(ContentType.JSON).body(Map.of("msisdn", msisdn));
        if (language != null) {
            request = request.header("Accept-Language", language);
        }
        return request.post(INTENTS + "/{id}/reference-sms", intentId);
    }

    static Response document(String token, UUID intentId) {
        return given().auth().oauth2(token).header("Accept-Language", "pt-MZ")
                .post(INTENTS + "/{id}/reference-document", intentId);
    }

    @Test
    void listMyPaymentsOnlyShowsTheCustomersOwn() {
        Customer a = support.customer();
        Customer b = support.customer();
        UUID first = id(payReference(a.token(), support.accepted(a.id(), insurer)));
        UUID second = id(payReference(a.token(), support.accepted(a.id(), insurer)));
        UUID other = id(payReference(b.token(), support.accepted(b.id(), insurer)));

        given().auth().oauth2(a.token()).get(MY_PAYMENTS).then().statusCode(200)
                .body("totalElements", equalTo(2))
                .body("content.id", containsInAnyOrder(first.toString(), second.toString()))
                .body("content[0].id", equalTo(second.toString())); // mais recente primeiro
        given().auth().oauth2(a.token()).queryParam("status", "SUCCEEDED").get(MY_PAYMENTS).then().statusCode(200)
                .body("totalElements", equalTo(0));
        given().auth().oauth2(b.token()).get(MY_PAYMENTS).then().statusCode(200)
                .body("content.id", containsInAnyOrder(other.toString()));
        given().auth().oauth2(insurer.finance()).get(MY_PAYMENTS).then().statusCode(403);
    }

    @Test
    void referenceSmsIsSentAtMostThreeTimes() {
        Customer c = support.customer();
        UUID intentId = id(payReference(c.token(), support.accepted(c.id(), insurer)));
        String reference = support.owner().queryForObject(
                "select reference from payment.reference_payment where intent_id = ?", String.class, intentId);

        sms(c.token(), intentId, "861234567", "en-GB,en;q=0.9").then().statusCode(202)
                .body("sentTo", equalTo("+258*****4567"));
        FakeOtpDeliveryChannels.Sent sent = channels.lastTo("+258861234567");
        assertThat(sent.text()).startsWith("MoSeguros: pay MZN 2,999.00 at entity " + entity)
                .contains(reference.substring(0, 3) + " " + reference.substring(3, 6))
                .contains(insurer.name());
        sms(c.token(), intentId, "841234567", null).then().statusCode(202);
        assertThat(channels.lastTo("+258841234567").text()).startsWith("MoSeguros: pague 2 999,00 MT na entidade");
        sms(c.token(), intentId, "841234567", "fr").then().statusCode(202);
        sms(c.token(), intentId, "841234567", null).then().statusCode(429)
                .body("code", equalTo("REFERENCE_SMS_RATE_LIMITED"));

        assertThat(support.owner().queryForObject("select sms_sent_count from payment.reference_payment "
                + "where intent_id = ?", Integer.class, intentId)).isEqualTo(3);
        assertThat(support.owner().queryForObject("""
                select count(*) from notification.outbound_message
                 where correlation_id = ? and purpose = 'PAYMENT' and channel = 'SMS'""", Integer.class, intentId))
                .isEqualTo(3);
        assertThat(support.owner().queryForList("select after_state::text from audit.audit_event where action = "
                + "'PAYMENT_INTENT.REFERENCE_SMS' and entity_id = ?", String.class, intentId.toString()))
                .hasSize(3).allSatisfy(s -> assertThat(s).contains("+258*****").doesNotContain("841234567"));

        // outro cliente: 404; um pagamento M-Pesa não é uma referência
        Customer other = support.customer();
        sms(other.token(), intentId, "841234567", null).then().statusCode(404)
                .body("code", equalTo("PAYMENT_INTENT_NOT_FOUND"));
        UUID mpesa = id(PaymentFlowSupport.payMpesa(c.token(), support.accepted(c.id(), insurer), "841234567"));
        sms(c.token(), mpesa, "841234567", null).then().statusCode(409)
                .body("code", equalTo("NOT_A_REFERENCE_PAYMENT"));
        document(c.token(), mpesa).then().statusCode(409).body("code", equalTo("NOT_A_REFERENCE_PAYMENT"));

        // referência que já não está activa
        support.owner().update("update payment.reference_payment set status = 'CANCELLED' where intent_id = ?",
                intentId);
        support.owner().update("update payment.reference_payment set sms_sent_count = 0 where intent_id = ?",
                intentId);
        sms(c.token(), intentId, "841234567", null).then().statusCode(409).body("code", equalTo("REFERENCE_NOT_ACTIVE"));
    }

    @Test
    void referenceDocumentIsAPdfOnlyThePayerDownloads() throws IOException {
        Customer c = support.customer();
        UUID intentId = id(payReference(c.token(), support.accepted(c.id(), insurer)));
        String reference = support.owner().queryForObject(
                "select reference from payment.reference_payment where intent_id = ?", String.class, intentId);

        Response doc = document(c.token(), intentId);
        doc.then().statusCode(200).body("fileId", notNullValue());
        String fileId = doc.path("fileId");
        document(c.token(), intentId).then().statusCode(200).body("fileId", equalTo(fileId));
        assertThat(support.owner().queryForObject("select document_file_id::text from payment.reference_payment "
                + "where intent_id = ?", String.class, intentId)).isEqualTo(fileId);

        byte[] pdf = given().auth().oauth2(c.token()).get("/api/v1/files/{id}", fileId).then().statusCode(200)
                .extract().asByteArray();
        String text;
        try (PDDocument document = Loader.loadPDF(pdf)) {
            text = new PDFTextStripper().getText(document);
        }
        String grouped = reference.substring(0, 3) + " " + reference.substring(3, 6) + " " + reference.substring(6, 9)
                + " " + reference.substring(9);
        assertThat(text).contains(entity).contains(grouped).contains("Pague exactamente este montante")
                .contains("A MoSeguros não recebe dinheiro").contains(insurer.name());

        Customer other = support.customer();
        given().auth().oauth2(other.token()).get("/api/v1/files/{id}", fileId).then().statusCode(404);
        given().auth().oauth2(insurer.finance()).get("/api/v1/files/{id}", fileId).then().statusCode(200);
        Seg otherInsurer = support.insurer("Outra Seguradora Ficheiros");
        given().auth().oauth2(otherInsurer.finance()).get("/api/v1/files/{id}", fileId).then().statusCode(404);
        document(other.token(), intentId).then().statusCode(404);
    }
}
