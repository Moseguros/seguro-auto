package mz.co.moseguros.payment.api;

import static io.restassured.RestAssured.given;
import static mz.co.moseguros.payment.PaymentFlowSupport.INSURER_PAYMENTS;
import static mz.co.moseguros.payment.PaymentFlowSupport.id;
import static mz.co.moseguros.payment.PaymentFlowSupport.inSystem;
import static mz.co.moseguros.payment.PaymentFlowSupport.payMpesa;
import static mz.co.moseguros.payment.PaymentFlowSupport.payReference;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.notNullValue;

import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import mz.co.moseguros.SecurityTestConfiguration;
import mz.co.moseguros.SecurityTestConfiguration.IdentityFixtures;
import mz.co.moseguros.SecurityTestConfiguration.MutableClock;
import mz.co.moseguros.TestcontainersConfiguration;
import mz.co.moseguros.identity.IdentityApi;
import mz.co.moseguros.payment.PaymentFlowSupport;
import mz.co.moseguros.payment.PaymentFlowSupport.Seg;
import mz.co.moseguros.payment.application.IntentStateService;
import mz.co.moseguros.payment.application.PaymentProperties;
import mz.co.moseguros.payment.domain.ConfirmedBy;
import mz.co.moseguros.rating.QuoteTestSupport.Customer;
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
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Pagamentos na seguradora (S5-02 §7): lista com filtros, detalhe e exportação CSV só dos seus;
 * isolamento entre seguradoras, permissões e o limite da exportação.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestcontainersConfiguration.class, SecurityTestConfiguration.class})
@ActiveProfiles("test")
@TestPropertySource(properties = mz.co.moseguros.payment.TestGateway.PROPERTY)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class InsurerPaymentIT {

    @LocalServerPort
    int port;

    @Autowired
    IdentityFixtures fixtures;

    @Autowired
    IdentityApi identityApi;

    @Autowired
    MutableClock clock;

    @Autowired
    IntentStateService states;

    @Autowired
    TransactionTemplate tx;

    @Autowired
    PaymentProperties properties;

    PaymentFlowSupport support;
    Seg a;
    Seg b;
    UUID referenceA;
    UUID mpesaA;
    UUID paidA;
    UUID intentB;
    String paidLabel;

    @BeforeAll
    void setUpAll() throws Exception {
        RestAssured.port = port;
        support = new PaymentFlowSupport(fixtures, identityApi, clock);
        support.createTemplate();
        a = support.insurer("Seguradora A Pagamentos");
        b = support.insurer("Seguradora B Pagamentos");
        support.enableBoth(a.id());
        support.enableBoth(b.id());
        Customer c = support.customer();
        referenceA = id(payReference(c.token(), support.accepted(c.id(), a)));
        mpesaA = id(payMpesa(c.token(), support.accepted(c.id(), a), "841234567"));
        UUID paidProposal = support.accepted(c.id(), a);
        paidA = id(payReference(c.token(), paidProposal));
        inSystem(tx, () -> states.markSucceeded(paidA, null, ConfirmedBy.REFERENCE_FILE, null, "test"));
        paidLabel = support.owner().queryForObject("select target_label from payment.payment_intent where id = ?",
                String.class, paidA);
        intentB = id(payReference(c.token(), support.accepted(c.id(), b)));
        await().atMost(Duration.ofSeconds(10)).untilAsserted(() ->
                assertThat(support.intentRow(mpesaA).get("status")).isEqualTo("PENDING"));
        // uma intenção criada às 00:30 de 18/11/2026 em Maputo (22:30 UTC do dia 17)
        support.owner().update("update payment.payment_intent set created_at = ? where id = ?",
                Timestamp.from(Instant.parse("2026-11-17T22:30:00Z")), referenceA);
    }

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        clock.reset();
    }

    static Response list(String token, Object... params) {
        var request = given().auth().oauth2(token);
        for (int i = 0; i + 1 < params.length; i += 2) {
            request = request.queryParam((String) params[i], params[i + 1]);
        }
        return request.get(INSURER_PAYMENTS);
    }

    @Test
    void listsAndFiltersOnlyTheInsurersOwnPayments() {
        list(a.finance()).then().statusCode(200).body("totalElements", equalTo(3))
                .body("content.id", containsInAnyOrder(referenceA.toString(), mpesaA.toString(), paidA.toString()));
        list(b.admin()).then().statusCode(200).body("content.id", contains(intentB.toString()));

        list(a.finance(), "status", "SUCCEEDED").then().body("content.id", contains(paidA.toString()))
                .body("content[0].succeededAt", notNullValue()).body("content[0].targetLabel", equalTo(paidLabel));
        given().auth().oauth2(a.finance()).queryParam("status", "PENDING").queryParam("status", "SUCCEEDED")
                .get(INSURER_PAYMENTS).then().body("totalElements", equalTo(3));
        list(a.finance(), "method", "MPESA").then().body("content.id", contains(mpesaA.toString()))
                .body("content[0].providerTxId", notNullValue());
        list(a.finance(), "from", "2026-11-18", "to", "2026-11-18").then()
                .body("content.id", hasItem(referenceA.toString()));
        list(a.finance(), "to", "2026-11-17").then().body("totalElements", equalTo(0));
        list(a.finance(), "q", paidLabel).then().body("content.id", contains(paidA.toString()));
        String pg = support.owner().queryForObject("select reference from payment.payment_intent where id = ?",
                String.class, mpesaA);
        list(a.finance(), "q", pg).then().body("content.id", contains(mpesaA.toString()));
        list(a.finance(), "sort", "amount,asc").then().statusCode(200);
        list(a.finance(), "sort", "status").then().statusCode(400).body("errors[0].code", equalTo("INVALID_SORT"));
    }

    @Test
    void detailShowsTheHistoryAndTheReferenceAndOtherInsurersGet404() {
        given().auth().oauth2(a.finance()).get(INSURER_PAYMENTS + "/{id}", paidA).then().statusCode(200)
                .body("status", equalTo("SUCCEEDED"))
                .body("confirmedBy", equalTo("REFERENCE_FILE"))
                .body("purpose", equalTo("NEW_POLICY"))
                .body("target.type", equalTo("PROPOSAL"))
                .body("bankReference.status", equalTo("PAID"))
                .body("bankReference.entity", notNullValue())
                .body("events.type", hasItem("STATUS_CHANGED"))
                .body("events[0].type", equalTo("CREATED"))
                .body("events[0].rawPayload", org.hamcrest.Matchers.nullValue());
        given().auth().oauth2(a.finance()).get(INSURER_PAYMENTS + "/{id}", mpesaA).then().statusCode(200)
                .body("payerMsisdnMasked", equalTo("+258*****4567"));
        given().auth().oauth2(a.finance()).get(INSURER_PAYMENTS + "/{id}", intentB).then().statusCode(404)
                .body("code", equalTo("PAYMENT_INTENT_NOT_FOUND"));
    }

    @Test
    void exportsACsvOfTheInsurersOwnPayments() {
        Response csv = given().auth().oauth2(a.finance()).queryParam("from", "2026-01-01")
                .queryParam("to", "2030-12-31").get(INSURER_PAYMENTS + "/export");
        csv.then().statusCode(200).contentType(org.hamcrest.Matchers.startsWith("text/csv"))
                .header("Content-Disposition", equalTo("attachment; filename=\"pagamentos-2026-01-01-2030-12-31.csv\""));
        byte[] bytes = csv.asByteArray();
        assertThat(bytes[0]).isEqualTo((byte) 0xEF);
        assertThat(bytes[1]).isEqualTo((byte) 0xBB);
        assertThat(bytes[2]).isEqualTo((byte) 0xBF);
        List<String> lines = new String(bytes, StandardCharsets.UTF_8).substring(1).lines().toList();
        assertThat(lines.get(0))
                .isEqualTo("reference;createdAt;method;status;amount;targetLabel;providerTxId;succeededAt;failureCode");
        assertThat(lines).hasSize(4);
        String referenceLine = lines.stream().filter(l -> l.contains(";2026-11-18 00:30:00;")).findFirst()
                .orElseThrow();
        assertThat(referenceLine).contains(";BANK_REFERENCE;PENDING;2999,00;");
        String paidLine = lines.stream().filter(l -> l.contains(";SUCCEEDED;")).findFirst().orElseThrow();
        assertThat(paidLine.split(";", -1)).hasSize(9);
        assertThat(paidLine.split(";", -1)[7]).matches("^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$");
        assertThat(String.join("\n", lines)).doesNotContain(intentB.toString())
                .doesNotContain(support.owner().queryForObject("select reference from payment.payment_intent "
                        + "where id = ?", String.class, intentB));
        assertThat(support.owner().queryForObject("""
                select after_state::text from audit.audit_event
                 where action = 'PAYMENT.EXPORT' and insurer_id = ? order by occurred_at desc limit 1""",
                String.class, a.id())).contains("\"rows\": 3").contains("2026-01-01");
    }

    @Test
    void exportAboveTheLimitIsRejected() {
        int previous = properties.getExportMaxRows();
        properties.setExportMaxRows(2);
        try {
            given().auth().oauth2(a.finance()).get(INSURER_PAYMENTS + "/export").then().statusCode(400)
                    .body("code", equalTo("EXPORT_TOO_LARGE"));
            given().auth().oauth2(b.finance()).get(INSURER_PAYMENTS + "/export").then().statusCode(200);
        } finally {
            properties.setExportMaxRows(previous);
        }
    }

    @Test
    void onlyStaffWithPaymentReadCanSeeThePayments() {
        list(a.underwriter()).then().statusCode(403);
        given().auth().oauth2(a.underwriter()).get(INSURER_PAYMENTS + "/{id}", paidA).then().statusCode(403);
        given().auth().oauth2(a.underwriter()).get(INSURER_PAYMENTS + "/export").then().statusCode(403);
        Customer c = support.customer();
        list(c.token()).then().statusCode(403);
        given().get(INSURER_PAYMENTS).then().statusCode(401);
    }
}
