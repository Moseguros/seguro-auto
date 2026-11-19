package mz.co.moseguros.payment.api;

import static io.restassured.RestAssured.given;
import static mz.co.moseguros.payment.PaymentFlowSupport.AMOUNT;
import static mz.co.moseguros.payment.PaymentFlowSupport.digits;
import static mz.co.moseguros.payment.PaymentFlowSupport.inSystem;
import static mz.co.moseguros.payment.PaymentFlowSupport.payReference;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.List;
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
import mz.co.moseguros.payment.PaymentFlowSupport;
import mz.co.moseguros.payment.PaymentFlowSupport.Seg;
import mz.co.moseguros.payment.application.IntentStateService;
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
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Excepções de pagamento da seguradora (S5-10 §6): fila, detalhe, candidatas e resoluções, com isolamento entre
 * seguradoras e permissões. As excepções são gravadas directamente (como dono): o ReferencePaymentListenerIT
 * cobre a sua criação a partir dos ficheiros.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestcontainersConfiguration.class, SecurityTestConfiguration.class})
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PaymentExceptionIT {

    static final String BASE = "/api/v1/insurer/payment-exceptions";

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

    PaymentFlowSupport support;
    Seg a;
    Seg b;
    String entityA;
    String entityB;
    UUID mpesaConfigA;
    Customer customer;

    @BeforeAll
    void setUpAll() throws Exception {
        RestAssured.port = port;
        support = new PaymentFlowSupport(fixtures, identityApi, clock);
        support.createTemplate();
        a = support.insurer("Seguradora Excepções A");
        entityA = support.enableBoth(a.id());
        mpesaConfigA = support.owner().queryForObject("select id from payment.insurer_payment_method "
                + "where insurer_id = ? and method = 'MPESA'", UUID.class, a.id());
        b = support.insurer("Seguradora Excepções B");
        entityB = support.enable(b.id(), "BANK_REFERENCE", null, null, 1, "Referência B");
        customer = support.customer();
    }

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        clock.reset();
    }

    // --- Dados ---

    record Issued(UUID proposalId, UUID intentId, String reference) {
    }

    Issued issue(Seg insurer) {
        UUID proposal = support.accepted(customer.id(), insurer);
        Response r = payReference(customer.token(), proposal);
        r.then().statusCode(202);
        return new Issued(proposal, UUID.fromString(r.path("id")), r.path("nextAction.reference"));
    }

    Issued expiredIntent(Seg insurer) {
        Issued i = issue(insurer);
        inSystem(tx, () -> states.expire(i.intentId(), "test"));
        return i;
    }

    /** Excepção gravada como dono. */
    UUID exception(Seg insurer, String type, UUID intentId, Long amount, String reference) {
        UUID id = UUID.randomUUID();
        support.owner().update("""
                insert into payment.payment_exception
                  (id, created_at, created_by, updated_at, updated_by, version, insurer_id, type, intent_id, message_id,
                   source_id, file_name, line_no, entity, reference, amount, paid_at, bank_transaction_id, status)
                values (?, now(), 'test', now(), 'test', 0, ?, ?, ?, ?, null, ?, 2, ?, ?, ?, ?, 'BANKTX1', 'OPEN')""",
                id, insurer.id(), type, intentId, "TARGET_NOT_PAYABLE".equals(type)
                        || "UNRESOLVED_STATUS".equals(type) ? null : UUID.randomUUID().toString().replace("-", ""),
                "MEPS_test_" + digits(6) + ".txt", insurer == a ? entityA : entityB,
                reference == null ? "0" + digits(10) : reference, amount,
                Timestamp.from(clock.instant().minusSeconds(600).truncatedTo(ChronoUnit.MILLIS)));
        return id;
    }

    UUID exception(Seg insurer, String type) {
        return exception(insurer, type, null, AMOUNT, null);
    }

    /** Intenção M-Pesa em {@code UNKNOWN} da seguradora A (como dono). */
    UUID unknownMpesaIntent(UUID proposalId) {
        UUID id = UUID.randomUUID();
        support.owner().update("""
                insert into payment.payment_intent
                  (id, created_at, created_by, updated_at, updated_by, version, reference, insurer_id, payer_user_id,
                   purpose, target_type, target_id, target_label, amount, method, method_config_id, payer_msisdn, status,
                   idempotency_key, expires_at, initiated_by_user_id, initiated_via, dispatched_at, unknown_since,
                   next_query_at)
                values (?, now(), 'test', now(), 'test', 0, ?, ?, ?, 'NEW_POLICY', 'PROPOSAL', ?, 'PR-9999-000001', ?,
                        'MPESA', ?, '258841234567', 'UNKNOWN', ?, now() + interval '1 hour', ?, 'APP', now(), now(),
                        now() + interval '1 hour')""",
                id, "PG-9999-" + digits(7), a.id(), customer.id(), proposalId, AMOUNT, mpesaConfigA, UUID.randomUUID(),
                customer.id());
        return id;
    }

    long version(String token, UUID id) {
        return ((Number) given().auth().oauth2(token).get(BASE + "/{id}", id).then().statusCode(200).extract()
                .path("version")).longValue();
    }

    Response resolve(String token, UUID id, String resolution, UUID intentId, Long version) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("resolution", resolution);
        if (intentId != null) {
            body.put("intentId", intentId);
        }
        body.put("note", "Decisão do financeiro.");
        body.put("version", version);
        return given().auth().oauth2(token).contentType(ContentType.JSON).body(body)
                .post(BASE + "/{id}/resolve", id);
    }

    Response resolve(UUID id, String resolution, UUID intentId) {
        return resolve(a.finance(), id, resolution, intentId, version(a.finance(), id));
    }

    void awaitProposal(UUID proposalId, String status) {
        await().atMost(Duration.ofSeconds(15)).untilAsserted(() ->
                assertThat(support.proposalRow(proposalId).get("status")).isEqualTo(status));
    }

    // --- Tabela de resoluções ---

    @Test
    void refundAndIgnoredAreAllowedWhereTheTableSaysAndOnlyRecordTheDecision() {
        for (String type : List.of("AMOUNT_MISMATCH", "UNKNOWN_REFERENCE", "EXPIRED_REFERENCE", "CANCELLED_REFERENCE",
                "ALREADY_PAID", "TARGET_NOT_PAYABLE")) {
            for (String resolution : List.of("REFUND", "IGNORED")) {
                UUID id = exception(a, type);
                resolve(id, resolution, null).then().statusCode(200)
                        .body("status", equalTo("RESOLVED"))
                        .body("resolution", equalTo(resolution))
                        .body("allowedResolutions", empty())
                        .body("note", equalTo("Decisão do financeiro."));
            }
        }
    }

    @Test
    void oneForbiddenResolutionPerTypeIsResolutionNotAllowed() {
        Map<String, String> forbidden = Map.of(
                "AMOUNT_MISMATCH", "MARK_SUCCEEDED",
                "UNKNOWN_REFERENCE", "MARK_FAILED",
                "EXPIRED_REFERENCE", "MARK_SUCCEEDED",
                "CANCELLED_REFERENCE", "MARK_FAILED",
                "ALREADY_PAID", "LINKED",
                "TARGET_NOT_PAYABLE", "LINKED",
                "UNRESOLVED_STATUS", "REFUND");
        forbidden.forEach((type, resolution) -> {
            UUID id = exception(a, type);
            resolve(id, resolution, UUID.randomUUID()).then().statusCode(400)
                    .body("code", equalTo("RESOLUTION_NOT_ALLOWED"));
            assertThat(support.owner().queryForObject("select status from payment.payment_exception where id = ?",
                    String.class, id)).isEqualTo("OPEN");
        });
    }

    @Test
    void detailListsTheAllowedResolutions() {
        UUID id = exception(a, "AMOUNT_MISMATCH");
        given().auth().oauth2(a.admin()).get(BASE + "/{id}", id).then().statusCode(200)
                .body("type", equalTo("AMOUNT_MISMATCH"))
                .body("status", equalTo("OPEN"))
                .body("amount.amount", equalTo((int) AMOUNT))
                .body("amount.currency", equalTo("MZN"))
                .body("bankTransactionId", equalTo("BANKTX1"))
                .body("allowedResolutions", containsInAnyOrder("LINKED", "REFUND", "IGNORED"));
        UUID unresolved = exception(a, "UNRESOLVED_STATUS");
        given().auth().oauth2(a.admin()).get(BASE + "/{id}", unresolved).then().statusCode(200)
                .body("allowedResolutions", containsInAnyOrder("MARK_SUCCEEDED", "MARK_FAILED"));
    }

    @Test
    void linkedIsAllowedForTheFourBankTypes() {
        for (String type : List.of("AMOUNT_MISMATCH", "UNKNOWN_REFERENCE", "EXPIRED_REFERENCE",
                "CANCELLED_REFERENCE")) {
            Issued i = expiredIntent(a);
            UUID id = exception(a, type, null, AMOUNT, null);
            resolve(id, "LINKED", i.intentId()).then().statusCode(200)
                    .body("resolution", equalTo("LINKED"))
                    .body("linkedIntent.id", equalTo(i.intentId().toString()))
                    .body("linkedIntent.status", equalTo("SUCCEEDED"));
        }
    }

    @Test
    void linkingAnUnknownReferenceToAnExpiredIntentPaysTheAcceptedProposal() {
        Issued i = expiredIntent(a);
        UUID id = exception(a, "UNKNOWN_REFERENCE", null, AMOUNT + 5_000, null);

        resolve(id, "LINKED", i.intentId()).then().statusCode(200);

        Map<String, Object> intent = support.intentRow(i.intentId());
        assertThat(intent.get("status")).isEqualTo("SUCCEEDED");
        assertThat(intent.get("confirmed_by")).isEqualTo("MANUAL");
        Map<String, Object> ref = support.owner().queryForMap("select status, bank_transaction_id from "
                + "payment.reference_payment where intent_id = ?", i.intentId());
        assertThat(ref.get("status")).isEqualTo("PAID");
        assertThat(ref.get("bank_transaction_id")).isEqualTo("BANKTX1");
        assertThat(support.owner().queryForObject("select count(*) from payment.payment_event where intent_id = ? "
                + "and type = 'MANUAL_RESOLUTION'", Long.class, i.intentId())).isEqualTo(1);
        awaitProposal(i.proposalId(), "PAID");
        Map<String, Object> audit = support.owner().queryForMap("select actor_type, actor_id, before_state::text as b, "
                + "after_state::text as a from audit.audit_event where action = 'PAYMENT_EXCEPTION.RESOLVE' "
                + "and entity_id = ?", id.toString());
        assertThat(audit.get("actor_type")).isEqualTo("STAFF");
        assertThat((String) audit.get("b")).contains("OPEN");
        assertThat((String) audit.get("a")).contains("RESOLVED").contains("LINKED");
    }

    @Test
    void linkingToAnIntentOfAnExpiredProposalCreatesTargetNotPayable() {
        Issued i = expiredIntent(a);
        support.setProposalStatus(i.proposalId(), "EXPIRED");
        UUID id = exception(a, "UNKNOWN_REFERENCE", null, AMOUNT, null);

        resolve(id, "LINKED", i.intentId()).then().statusCode(200);

        await().atMost(Duration.ofSeconds(15)).untilAsserted(() -> assertThat(support.owner().queryForObject(
                "select count(*) from payment.payment_exception where intent_id = ? and type = 'TARGET_NOT_PAYABLE'",
                Long.class, i.intentId())).isEqualTo(1));
        assertThat(support.proposalRow(i.proposalId()).get("status")).isEqualTo("EXPIRED");
    }

    @Test
    void linkingRulesRejectLowerAmountsForeignIntentsAndPaidTargets() {
        Issued i = expiredIntent(a);
        UUID lower = exception(a, "AMOUNT_MISMATCH", i.intentId(), AMOUNT - 1, null);
        resolve(lower, "LINKED", i.intentId()).then().statusCode(400).body("code", equalTo("LINK_AMOUNT_INSUFFICIENT"));

        Issued foreign = issue(b);
        UUID unknown = exception(a, "UNKNOWN_REFERENCE");
        resolve(unknown, "LINKED", foreign.intentId()).then().statusCode(400)
                .body("code", equalTo("LINK_INTENT_INVALID"));
        UUID mpesa = unknownMpesaIntent(support.accepted(customer.id(), a));
        resolve(unknown, "LINKED", mpesa).then().statusCode(400).body("code", equalTo("LINK_INTENT_INVALID"));

        resolve(unknown, "LINKED", null).then().statusCode(400)
                .body("code", equalTo("VALIDATION_ERROR"))
                .body("errors.field", hasItem("intentId"));

        // Um alvo já pago: a primeira associação paga a proposta; uma segunda intenção do mesmo alvo não pode.
        Issued target = expiredIntent(a);
        Response second = payReference(customer.token(), target.proposalId());
        second.then().statusCode(202);
        UUID secondIntent = UUID.fromString(second.path("id"));
        resolve(exception(a, "UNKNOWN_REFERENCE"), "LINKED", target.intentId()).then().statusCode(200);
        resolve(exception(a, "UNKNOWN_REFERENCE"), "LINKED", secondIntent).then().statusCode(409)
                .body("code", equalTo("PAYMENT_TARGET_ALREADY_PAID"));
    }

    @Test
    void markSucceededAndMarkFailedResolveAnUnresolvedMpesaIntent() {
        UUID proposal = support.accepted(customer.id(), a);
        UUID intent = unknownMpesaIntent(proposal);
        UUID id = exception(a, "UNRESOLVED_STATUS", intent, AMOUNT, null);
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("resolution", "MARK_SUCCEEDED");
        body.put("providerTxId", "MPESA-PORTAL-123");
        body.put("note", "Confirmado no portal M-Pesa.");
        body.put("version", version(a.finance(), id));
        given().auth().oauth2(a.finance()).contentType(ContentType.JSON).body(body).post(BASE + "/{id}/resolve", id)
                .then().statusCode(200).body("resolution", equalTo("MARK_SUCCEEDED"));
        Map<String, Object> row = support.intentRow(intent);
        assertThat(row.get("status")).isEqualTo("SUCCEEDED");
        assertThat(row.get("confirmed_by")).isEqualTo("MANUAL");
        assertThat(row.get("provider_tx_id")).isEqualTo("MPESA-PORTAL-123");
        awaitProposal(proposal, "PAID");

        UUID failing = unknownMpesaIntent(support.accepted(customer.id(), a));
        UUID failId = exception(a, "UNRESOLVED_STATUS", failing, AMOUNT, null);
        resolve(failId, "MARK_FAILED", null).then().statusCode(200);
        Map<String, Object> failed = support.intentRow(failing);
        assertThat(failed.get("status")).isEqualTo("FAILED");
        assertThat(failed.get("failure_code")).isEqualTo("MANUAL");
    }

    @Test
    void resolvingTwiceOrWithAnOldVersionIsAConflict() {
        UUID id = exception(a, "ALREADY_PAID");
        long v = version(a.finance(), id);
        resolve(a.finance(), id, "IGNORED", null, v).then().statusCode(200);
        resolve(a.finance(), id, "REFUND", null, v + 1).then().statusCode(409)
                .body("code", equalTo("INVALID_STATE_TRANSITION"));

        UUID other = exception(a, "ALREADY_PAID");
        resolve(a.finance(), other, "IGNORED", null, version(a.finance(), other) + 7).then().statusCode(409)
                .body("code", equalTo("CONCURRENT_MODIFICATION"));
    }

    @Test
    void theRequestIsValidated() {
        UUID id = exception(a, "ALREADY_PAID");
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("note", "abc");
        given().auth().oauth2(a.finance()).contentType(ContentType.JSON).body(body).post(BASE + "/{id}/resolve", id)
                .then().statusCode(400)
                .body("code", equalTo("VALIDATION_ERROR"))
                .body("errors.field", containsInAnyOrder("resolution", "note", "version"))
                .body("errors.find { it.field == 'note' }.code", equalTo("TOO_SHORT"));
    }

    // --- Lista, candidatas e isolamento ---

    @Test
    void theListFiltersByStatusTypeAndQuery() {
        String ref = "0" + digits(10);
        UUID open = exception(a, "AMOUNT_MISMATCH", null, AMOUNT, ref);
        UUID closed = exception(a, "AMOUNT_MISMATCH", null, AMOUNT, ref);
        resolve(closed, "IGNORED", null).then().statusCode(200);

        given().auth().oauth2(a.admin()).queryParam("q", ref).get(BASE).then().statusCode(200)
                .body("content.id", contains(open.toString()))
                .body("content[0].reference", equalTo(ref))
                .body("content[0].entity", equalTo(entityA));
        given().auth().oauth2(a.admin()).queryParam("q", ref).queryParam("status", "").get(BASE).then()
                .statusCode(200).body("totalElements", equalTo(2));
        given().auth().oauth2(a.admin()).queryParam("q", ref).queryParam("status", "RESOLVED").get(BASE).then()
                .statusCode(200).body("content.id", contains(closed.toString()));
        given().auth().oauth2(a.admin()).queryParam("q", ref).queryParam("type", "ALREADY_PAID").get(BASE).then()
                .statusCode(200).body("totalElements", equalTo(0));
    }

    @Test
    void candidatesComeFromTheInsurerWithTheSameReferenceFirst() {
        Issued sameRef = expiredIntent(a);
        Issued sameAmount = issue(a);
        Issued foreign = issue(b);
        UUID id = exception(a, "UNKNOWN_REFERENCE", null, AMOUNT, sameRef.reference());

        Response r = given().auth().oauth2(a.finance()).get(BASE + "/{id}/candidates", id);
        r.then().statusCode(200)
                .body("candidates[0].id", equalTo(sameRef.intentId().toString()))
                .body("candidates[0].matchesReference", equalTo(true))
                .body("candidates[0].matchesAmount", equalTo(true))
                .body("candidates[0].bankReference", equalTo(sameRef.reference()))
                .body("candidates.id", hasItem(sameAmount.intentId().toString()))
                .body("candidates.id", not(hasItem(foreign.intentId().toString())))
                .body("candidates.status", everyItem(org.hamcrest.Matchers.in(List.of("PENDING", "EXPIRED",
                        "CANCELLED"))));
        assertThat(r.<List<?>>path("candidates")).hasSizeLessThanOrEqualTo(20);

        given().auth().oauth2(a.finance()).get(BASE + "/{id}/candidates", exception(a, "ALREADY_PAID")).then()
                .statusCode(200).body("candidates", empty());
    }

    @Test
    void anotherInsurerCannotListReadOrResolve() {
        UUID ofA = exception(a, "ALREADY_PAID");

        given().auth().oauth2(b.admin()).queryParam("status", "").queryParam("size", 100).get(BASE).then()
                .statusCode(200).body("content.id", not(hasItem(ofA.toString())));
        given().auth().oauth2(b.admin()).get(BASE + "/{id}", ofA).then().statusCode(404)
                .body("code", equalTo("PAYMENT_EXCEPTION_NOT_FOUND"));
        given().auth().oauth2(b.finance()).get(BASE + "/{id}/candidates", ofA).then().statusCode(404);
        resolve(b.finance(), ofA, "IGNORED", null, 0L).then().statusCode(404)
                .body("code", equalTo("PAYMENT_EXCEPTION_NOT_FOUND"));
        assertThat(support.owner().queryForObject("select status from payment.payment_exception where id = ?",
                String.class, ofA)).isEqualTo("OPEN");
    }

    @Test
    void permissions() {
        UUID id = exception(a, "ALREADY_PAID");
        given().auth().oauth2(a.admin()).get(BASE).then().statusCode(200);
        given().auth().oauth2(a.admin()).get(BASE + "/{id}", id).then().statusCode(200);
        resolve(a.admin(), id, "IGNORED", null, 0L).then().statusCode(403);
        given().auth().oauth2(a.admin()).get(BASE + "/{id}/candidates", id).then().statusCode(403);
        given().auth().oauth2(a.underwriter()).get(BASE).then().statusCode(403);
        given().auth().oauth2(customer.token()).get(BASE).then().statusCode(403);
        given().auth().oauth2(customer.token()).get(BASE + "/{id}", id).then().statusCode(403);
        given().get(BASE).then().statusCode(401);
        resolve(a.finance(), id, "IGNORED", null, 0L).then().statusCode(200);
        assertThat(support.owner().queryForObject("select count(*) from audit.audit_event "
                + "where action = 'PAYMENT_EXCEPTION.RESOLVE' and entity_id = ?", Long.class, id.toString()))
                .isEqualTo(1);
        assertThat(Instant.now()).isNotNull();
    }
}
