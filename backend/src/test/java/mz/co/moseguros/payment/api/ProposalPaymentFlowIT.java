package mz.co.moseguros.payment.api;

import static io.restassured.RestAssured.given;
import static mz.co.moseguros.payment.PaymentFlowSupport.AMOUNT;
import static mz.co.moseguros.payment.PaymentFlowSupport.body;
import static mz.co.moseguros.payment.PaymentFlowSupport.id;
import static mz.co.moseguros.payment.PaymentFlowSupport.inSystem;
import static mz.co.moseguros.payment.PaymentFlowSupport.intent;
import static mz.co.moseguros.payment.PaymentFlowSupport.methods;
import static mz.co.moseguros.payment.PaymentFlowSupport.pay;
import static mz.co.moseguros.payment.PaymentFlowSupport.payMpesa;
import static mz.co.moseguros.payment.PaymentFlowSupport.payReference;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.matchesPattern;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import mz.co.moseguros.SecurityTestConfiguration;
import mz.co.moseguros.SecurityTestConfiguration.IdentityFixtures;
import mz.co.moseguros.SecurityTestConfiguration.MutableClock;
import mz.co.moseguros.TestcontainersConfiguration;
import mz.co.moseguros.identity.IdentityApi;
import mz.co.moseguros.notification.FakeOtpDeliveryChannels;
import mz.co.moseguros.payment.PaymentEventsRecorder;
import mz.co.moseguros.payment.PaymentFlowSupport;
import mz.co.moseguros.payment.PaymentFlowSupport.Broker;
import mz.co.moseguros.payment.PaymentFlowSupport.Seg;
import mz.co.moseguros.payment.PaymentSucceeded;
import mz.co.moseguros.payment.ReferenceGenerated;
import mz.co.moseguros.payment.TestGateway;
import mz.co.moseguros.payment.application.IntentStateService;
import mz.co.moseguros.payment.application.reference.ReferenceGenerator;
import mz.co.moseguros.payment.domain.ConfirmedBy;
import mz.co.moseguros.proposal.ProposalPaid;
import mz.co.moseguros.rating.QuoteTestSupport.Customer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Import;
import org.springframework.modulith.test.EnableScenarios;
import org.springframework.modulith.test.Scenario;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Pagamento de uma proposta de ponta a ponta (S5-02 §4, §7, §9): formas de pagamento, entidade e
 * referência (com o gerador real do S5-07, {@code Ponto24ReferenceGenerator}), M-Pesa (com o
 * {@code TestGateway}), confirmação → {@code PaymentSucceeded} → proposta {@code PAID} →
 * {@code ProposalPaid}, excepções, link do mediador, isolamento e permissões.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestcontainersConfiguration.class, SecurityTestConfiguration.class})
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@EnableScenarios
@ExtendWith(OutputCaptureExtension.class)
class ProposalPaymentFlowIT {

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
    PaymentEventsRecorder events;

    @Autowired
    ApplicationEventPublisher publisher;

    @Autowired
    TestGateway gateway;

    @Autowired
    ReferenceGenerator referenceGenerator;

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
        insurer = support.insurer("Seguradora Pagamentos");
        entity = support.enableBoth(insurer.id());
    }

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        clock.reset();
        gateway.reset();
        channels.reset();
    }

    UUID confirm(UUID intentId) {
        inSystem(tx, () -> states.markSucceeded(intentId, null, ConfirmedBy.REFERENCE_FILE, null, "test"));
        return intentId;
    }

    void awaitStatus(UUID intentId, String status) {
        await().atMost(Duration.ofSeconds(10)).untilAsserted(() ->
                assertThat(support.intentRow(intentId).get("status")).isEqualTo(status));
    }

    // --- Formas de pagamento ---

    @Test
    void methodsAreTheActiveOnesWithinTheLimitsBySortOrder() {
        Customer c = support.customer();
        UUID proposal = support.accepted(c.id(), insurer);
        methods(c.token(), proposal).then().statusCode(200)
                .body("proposalId", equalTo(proposal.toString()))
                .body("insurer.id", equalTo(insurer.id().toString()))
                .body("amount.amount", equalTo((int) AMOUNT))
                .body("paid", equalTo(false))
                .body("methods.method", contains("MPESA", "BANK_REFERENCE"))
                .body("methods[0].displayName", equalTo("M-Pesa Teste"))
                .body("activeIntent", nullValue());

        // outra seguradora: M-Pesa com máximo abaixo do montante, referência desactivada
        Seg other = support.insurer("Seguradora Limites");
        support.enable(other.id(), "MPESA", null, AMOUNT - 1, 1, null);
        support.enable(other.id(), "BANK_REFERENCE", null, null, 2, null);
        support.owner().update("update payment.insurer_payment_method set enabled = false where insurer_id = ? "
                + "and method = 'BANK_REFERENCE'", other.id());
        UUID limited = support.accepted(c.id(), other);
        methods(c.token(), limited).then().statusCode(200).body("methods", hasSize(0));
        pay(c.token(), limited, UUID.randomUUID().toString(), body("MPESA", "841234567", "APP")).then()
                .statusCode(409).body("code", equalTo("PAYMENT_METHOD_NOT_AVAILABLE"));
        support.owner().update("update payment.insurer_payment_method set max_amount = null where insurer_id = ?",
                other.id());
        methods(c.token(), limited).then().statusCode(200).body("methods.method", contains("MPESA"));
    }

    @Test
    void proposalsThatAreNotAcceptedCannotBePaid() {
        Customer c = support.customer();
        UUID underReview = support.proposal(c.id(), insurer, "UNDER_REVIEW", null, AMOUNT, null);
        UUID draft = support.proposal(c.id(), insurer, "DRAFT", clock.instant().plus(Duration.ofDays(15)), AMOUNT,
                null);
        methods(c.token(), underReview).then().statusCode(409).body("code", equalTo("PAYMENT_TARGET_NOT_PAYABLE"));
        methods(c.token(), draft).then().statusCode(409).body("code", equalTo("PAYMENT_TARGET_NOT_PAYABLE"));
        payReference(c.token(), underReview).then().statusCode(409).body("code", equalTo("PAYMENT_TARGET_NOT_PAYABLE"));
        // prazo a acabar: menos de 5 minutos
        UUID closing = support.proposal(c.id(), insurer, "ACCEPTED", clock.instant().plus(Duration.ofMinutes(3)),
                AMOUNT, null);
        payReference(c.token(), closing).then().statusCode(409).body("code", equalTo("PAYMENT_TARGET_NOT_PAYABLE"));
    }

    // --- Entidade e referência ---

    @Test
    void bankReferenceGivesTheEntityAndReferenceAndIsReusedUntilTheAmountChanges() {
        Customer c = support.customer();
        UUID proposal = support.accepted(c.id(), insurer);
        Instant deadline = ((Timestamp) support.proposalRow(proposal).get("expires_at")).toInstant();

        Response first = payReference(c.token(), proposal);
        first.then().statusCode(202)
                .header("Location", equalTo("/api/v1/payment-intents/" + first.path("id")))
                .body("status", equalTo("PENDING"))
                .body("method", equalTo("BANK_REFERENCE"))
                .body("purpose", equalTo("NEW_POLICY"))
                .body("reference", matchesPattern("^PG-\\d{4}-\\d{7}$"))
                .body("target.type", equalTo("PROPOSAL"))
                .body("target.label", matchesPattern("^PR-9999-\\d{6}$"))
                .body("insurer.displayName", equalTo(insurer.name()))
                .body("amount.amount", equalTo((int) AMOUNT))
                .body("nextAction.type", equalTo("PAY_BY_REFERENCE"))
                .body("nextAction.entity", equalTo(entity))
                .body("nextAction.reference", matchesPattern("^\\d{11}$"))
                .body("nextAction.amount.amount", equalTo((int) AMOUNT));
        UUID intentId = id(first);
        // gerador real (S5-07): a referência valida com a entidade e o montante da proposta
        String firstReference = first.path("nextAction.reference");
        assertThat(referenceGenerator.isValid(entity, firstReference, AMOUNT)).isTrue();
        assertThat(referenceGenerator.isValid(entity, firstReference, AMOUNT + 1)).isFalse();
        assertThat(Instant.parse(first.path("nextAction.validUntil"))).isEqualTo(deadline);
        assertThat(Instant.parse(first.path("expiresAt"))).isEqualTo(deadline);
        assertThat(events.of(ReferenceGenerated.class, e -> e.intentId().equals(intentId))).singleElement()
                .satisfies(e -> {
                    assertThat(e.entity()).isEqualTo(entity);
                    assertThat(e.reference()).isEqualTo(first.path("nextAction.reference"));
                    assertThat(e.amount().amount()).isEqualTo(AMOUNT);
                });
        assertThat(support.referenceStatus(intentId)).isEqualTo("ACTIVE");
        methods(c.token(), proposal).then().statusCode(200).body("activeIntent.id", equalTo(intentId.toString()));

        // segundo pedido (outra chave): a mesma intenção
        payReference(c.token(), proposal).then().statusCode(202).body("id", equalTo(intentId.toString()))
                .body("nextAction.reference", equalTo(first.path("nextAction.reference")));
        assertThat(support.owner().queryForObject("select count(*) from payment.payment_intent where target_id = ?",
                Integer.class, proposal)).isEqualTo(1);

        // o montante do alvo mudou: a referência antiga é cancelada e sai outra
        support.owner().update("update proposal.proposal set total_amount = ? where id = ?", AMOUNT + 10_000, proposal);
        Response second = payReference(c.token(), proposal);
        second.then().statusCode(202).body("amount.amount", equalTo((int) AMOUNT + 10_000));
        assertThat(id(second)).isNotEqualTo(intentId);
        assertThat(second.<String>path("nextAction.reference")).isNotEqualTo(first.path("nextAction.reference"));
        assertThat(referenceGenerator.isValid(entity, second.path("nextAction.reference"), AMOUNT + 10_000)).isTrue();
        assertThat(support.intentRow(intentId)).containsEntry("status", "CANCELLED")
                .containsEntry("failure_code", "SUPERSEDED");
        assertThat(support.referenceStatus(intentId)).isEqualTo("CANCELLED");

        // auditoria
        assertThat(support.owner().queryForList("select action from audit.audit_event where entity_id = ? "
                + "order by occurred_at", String.class, intentId.toString()))
                .contains("PAYMENT_INTENT.CREATE", "PAYMENT_INTENT.STATUS_CHANGE");
    }

    @Test
    void anEntityWithoutInvoiceNumbersLeftGives503AndNoIntent() {
        Seg exhausted = support.insurer("Seguradora Esgotada");
        String exhaustedEntity = support.enable(exhausted.id(), "BANK_REFERENCE", null, null, 1, null);
        support.owner().update("delete from payment.reference_invoice_sequence where entity = ?", exhaustedEntity);
        support.owner().update("insert into payment.reference_invoice_sequence (entity, last_value) values (?, ?)",
                exhaustedEntity, 9_999_999);
        Customer c = support.customer();
        UUID proposal = support.accepted(c.id(), exhausted);

        payReference(c.token(), proposal).then().statusCode(503)
                .body("code", equalTo("REFERENCE_GENERATION_FAILED"));
        assertThat(support.owner().queryForObject("select count(*) from payment.payment_intent where target_id = ?",
                Integer.class, proposal)).isZero();
        assertThat(support.owner().queryForObject(
                "select last_value from payment.reference_invoice_sequence where entity = ?", Integer.class,
                exhaustedEntity)).isEqualTo(9_999_999);
    }

    // --- Confirmação ---

    @Test
    void confirmedPaymentMakesTheProposalPaidAndPublishesProposalPaid(Scenario scenario) {
        Customer c = support.customer();
        String email = support.verifiedEmail(c.id());
        UUID proposal = support.accepted(c.id(), insurer);
        UUID intentId = id(payReference(c.token(), proposal).then().statusCode(202).extract().response());

        TenantContextScope.system(() -> scenario.stimulate(() -> {
                    states.markSucceeded(intentId, null, ConfirmedBy.REFERENCE_FILE, null, "test");
                })
                .andWaitAtMost(Duration.ofSeconds(10))
                .andWaitForStateChange(() -> support.proposalRow(proposal).get("status"), "PAID"::equals)
                .andVerify(status -> assertThat(status).isEqualTo("PAID")));

        Map<String, Object> row = support.proposalRow(proposal);
        assertThat(row.get("payment_intent_id")).isEqualTo(intentId);
        assertThat(row.get("paid_at")).isNotNull();
        assertThat(row.get("expires_at")).isNull();
        assertThat(events.of(PaymentSucceeded.class, e -> e.intentId().equals(intentId))).singleElement()
                .satisfies(e -> {
                    assertThat(e.targetId()).isEqualTo(proposal);
                    assertThat(e.amount().amount()).isEqualTo(AMOUNT);
                });
        await().atMost(Duration.ofSeconds(10)).untilAsserted(() ->
                assertThat(events.of(ProposalPaid.class, e -> e.proposalId().equals(proposal))).singleElement()
                        .satisfies(e -> {
                            assertThat(e.paymentIntentId()).isEqualTo(intentId);
                            assertThat(e.customerUserId()).isEqualTo(c.id());
                            assertThat(e.amount().amount()).isEqualTo(AMOUNT);
                        }));
        assertThat(support.referenceStatus(intentId)).isEqualTo("PAID");
        // aviso ao cliente (canal falso)
        await().atMost(Duration.ofSeconds(10)).untilAsserted(() ->
                assertThat(channels.sentTo(email)).isNotEmpty());

        // nova tentativa: já paga
        payReference(c.token(), proposal).then().statusCode(409).body("code", equalTo("PAYMENT_TARGET_ALREADY_PAID"));
        methods(c.token(), proposal).then().statusCode(200).body("paid", equalTo(true)).body("methods", hasSize(0));
        intent(c.token(), intentId).then().statusCode(200).body("status", equalTo("SUCCEEDED"))
                .body("succeededAt", notNullValue()).body("nextAction.type", equalTo("NONE"));

        // o mesmo PaymentSucceeded outra vez: nenhum segundo ProposalPaid
        PaymentSucceeded original = events.of(PaymentSucceeded.class, e -> e.intentId().equals(intentId)).get(0);
        inSystem(tx, () -> {
            publisher.publishEvent(original);
            return null;
        });
        await().during(Duration.ofSeconds(2)).atMost(Duration.ofSeconds(5)).untilAsserted(() ->
                assertThat(events.of(ProposalPaid.class, e -> e.proposalId().equals(proposal))).hasSize(1));
        assertThat(support.owner().queryForObject("select count(*) from payment.payment_exception where intent_id = ?",
                Integer.class, intentId)).isZero();
    }

    @Test
    void paymentForAnExpiredProposalBecomesAnException() {
        Customer c = support.customer();
        UUID proposal = support.accepted(c.id(), insurer);
        UUID intentId = id(payReference(c.token(), proposal).then().statusCode(202).extract().response());
        support.owner().update("update proposal.proposal set status = 'EXPIRED', expires_at = null where id = ?",
                proposal);

        confirm(intentId);

        await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> assertThat(support.owner().queryForList("""
                        select type, detail, amount from payment.payment_exception where intent_id = ?""", intentId))
                .singleElement().satisfies(r -> {
                    assertThat(r).containsEntry("type", "TARGET_NOT_PAYABLE").containsEntry("detail",
                            "PROPOSAL_EXPIRED");
                    assertThat(((Number) r.get("amount")).longValue()).isEqualTo(AMOUNT);
                }));
        assertThat(support.proposalRow(proposal)).containsEntry("status", "EXPIRED").containsEntry("paid_at", null);
        assertThat(events.of(ProposalPaid.class, e -> e.proposalId().equals(proposal))).isEmpty();
    }

    // --- M-Pesa ---

    @Test
    void mpesaNeedsAnMpesaNumberAndBlocksANewPaymentWhileInProgress(CapturedOutput output) {
        Customer c = support.customer();
        UUID proposal = support.accepted(c.id(), insurer);
        Response notMpesa = payMpesa(c.token(), proposal, "861234567");
        notMpesa.then().statusCode(400).body("code", equalTo("VALIDATION_ERROR"))
                .body("errors[0].field", equalTo("payerMsisdn")).body("errors[0].code", equalTo("MSISDN_NOT_MPESA"));
        pay(c.token(), proposal, UUID.randomUUID().toString(), body("MPESA", null, "APP")).then().statusCode(400)
                .body("errors[0].field", equalTo("payerMsisdn")).body("errors[0].code", equalTo("REQUIRED"));

        Response mpesa = payMpesa(c.token(), proposal, "841234567");
        mpesa.then().statusCode(202).body("method", equalTo("MPESA"))
                .body("payerMsisdnMasked", equalTo("+258*****4567"))
                .body("nextAction.type", equalTo("AWAIT_CUSTOMER_CONFIRMATION"))
                .body("nextAction.timeoutAt", notNullValue());
        UUID intentId = id(mpesa);
        awaitStatus(intentId, "PENDING");
        assertThat(gateway.initiated()).contains(intentId);
        assertThat(support.intentRow(intentId).get("dispatched_at")).isNotNull();

        payMpesa(c.token(), proposal, "851234567").then().statusCode(409).body("code", equalTo("PAYMENT_IN_PROGRESS"))
                .body("intentId", equalTo(intentId.toString()));
        payReference(c.token(), proposal).then().statusCode(409).body("code", equalTo("PAYMENT_IN_PROGRESS"));

        // os registos nunca têm o telemóvel completo
        assertThat(output.getAll()).doesNotContain("258841234567").doesNotContain("841234567");
        // o histórico tem a resposta mascarada do fornecedor
        assertThat(support.owner().queryForList("select raw_payload::text from payment.payment_event "
                + "where intent_id = ? and type = 'PROVIDER_RESPONSE'", String.class, intentId))
                .singleElement().asString().contains("+258*****4567").doesNotContain("841234567");
    }

    @Test
    void paidByMpesaCancelsTheActiveReference() {
        Customer c = support.customer();
        UUID proposal = support.accepted(c.id(), insurer);
        UUID referenceIntent = id(payReference(c.token(), proposal).then().statusCode(202).extract().response());
        UUID mpesaIntent = id(payMpesa(c.token(), proposal, "841234567").then().statusCode(202).extract()
                .response());
        awaitStatus(mpesaIntent, "PENDING");

        inSystem(tx, () -> states.markSucceeded(mpesaIntent, null, ConfirmedBy.PROVIDER, "MPESA-TX-1", "test"));

        assertThat(support.intentRow(referenceIntent)).containsEntry("status", "CANCELLED")
                .containsEntry("failure_code", "SUPERSEDED");
        assertThat(support.referenceStatus(referenceIntent)).isEqualTo("CANCELLED");
        assertThat(support.intentRow(mpesaIntent)).containsEntry("status", "SUCCEEDED")
                .containsEntry("confirmed_by", "PROVIDER").containsEntry("provider_tx_id", "MPESA-TX-1");
        await().atMost(Duration.ofSeconds(10)).untilAsserted(() ->
                assertThat(support.proposalRow(proposal).get("payment_intent_id")).isEqualTo(mpesaIntent));
    }

    @Test
    void unknownMethodsAreValidationErrors() {
        Customer c = support.customer();
        UUID proposal = support.accepted(c.id(), insurer);
        for (String method : List.of("EMOLA", "CARD")) {
            pay(c.token(), proposal, UUID.randomUUID().toString(), body(method, null, "WEB")).then().statusCode(400)
                    .body("code", equalTo("VALIDATION_ERROR"));
        }
        pay(c.token(), proposal, UUID.randomUUID().toString(), body("BANK_REFERENCE", null, "USSD")).then()
                .statusCode(400).body("code", equalTo("VALIDATION_ERROR"));
    }

    // --- Isolamento e permissões ---

    @Test
    void customersOnlySeeAndPayTheirOwnAndStaffIsForbidden() {
        Customer a = support.customer();
        Customer b = support.customer();
        UUID proposal = support.accepted(a.id(), insurer);
        UUID intentId = id(payReference(a.token(), proposal).then().statusCode(202).extract().response());

        intent(b.token(), intentId).then().statusCode(404).body("code", equalTo("PAYMENT_INTENT_NOT_FOUND"));
        payReference(b.token(), proposal).then().statusCode(404).body("code", equalTo("PROPOSAL_NOT_FOUND"));
        methods(b.token(), proposal).then().statusCode(404).body("code", equalTo("PROPOSAL_NOT_FOUND"));
        payReference(insurer.finance(), proposal).then().statusCode(403);
        intent(insurer.admin(), intentId).then().statusCode(403);
        payReference(null, proposal).then().statusCode(401);
        intent(null, intentId).then().statusCode(401);
        intent(a.token(), intentId).then().statusCode(200).body("payerUserId", equalTo(a.id().toString()));
    }

    // --- Link do mediador ---

    @Test
    void brokerSendsThePaymentLinkToTheCustomer() {
        Broker broker = support.broker("Corretora Link");
        Broker other = support.broker("Corretora Outra");
        Customer c = support.customer();
        UUID proposal = support.proposal(c.id(), insurer, "ACCEPTED", clock.instant().plus(Duration.ofDays(15)),
                AMOUNT, broker);
        String msisdn = c.account().getMsisdn().e164();

        given().auth().oauth2(broker.token()).post("/api/v1/broker/proposals/{id}/payment-link", proposal).then()
                .statusCode(202).body("sentTo", equalTo(c.account().getMsisdn().masked())).body("sentAt", notNullValue());
        FakeOtpDeliveryChannels.Sent sms = channels.lastTo(msisdn);
        assertThat(sms.text()).contains("/cliente/propostas/" + proposal + "/pagar?origem=mediador")
                .contains(insurer.name());
        assertThat(support.owner().queryForObject("""
                select count(*) from notification.outbound_message
                 where correlation_id = ? and purpose = 'PAYMENT' and channel = 'SMS'""", Integer.class, proposal))
                .isEqualTo(1);
        assertThat(support.owner().queryForObject("""
                select after_state::text from audit.audit_event
                 where action = 'PROPOSAL.PAYMENT_LINK_SENT' and entity_id = ?""", String.class, proposal.toString()))
                .contains(c.account().getMsisdn().masked()).doesNotContain(c.account().getMsisdn().national());

        given().auth().oauth2(other.token()).post("/api/v1/broker/proposals/{id}/payment-link", proposal).then()
                .statusCode(404).body("code", equalTo("PROPOSAL_NOT_FOUND"));
        given().auth().oauth2(c.token()).post("/api/v1/broker/proposals/{id}/payment-link", proposal).then()
                .statusCode(403);
        given().auth().oauth2(broker.token()).post("/api/v1/broker/proposals/{id}/payment-link", proposal).then()
                .statusCode(202);
        given().auth().oauth2(broker.token()).post("/api/v1/broker/proposals/{id}/payment-link", proposal).then()
                .statusCode(202);
        given().auth().oauth2(broker.token()).post("/api/v1/broker/proposals/{id}/payment-link", proposal).then()
                .statusCode(429).body("code", equalTo("PAYMENT_LINK_RATE_LIMITED"))
                .body("retryAfterSeconds", notNullValue()).header("Retry-After", notNullValue());

        UUID notAccepted = support.proposal(c.id(), insurer, "UNDER_REVIEW", null, AMOUNT, broker);
        given().auth().oauth2(broker.token()).post("/api/v1/broker/proposals/{id}/payment-link", notAccepted).then()
                .statusCode(409).body("code", equalTo("PAYMENT_TARGET_NOT_PAYABLE"));
        support.owner().update("update identity.user_account set msisdn = null, email = ? where id = ?",
                "sem-telemovel" + PaymentFlowSupport.digits(6) + "@teste.co.mz", c.id());
        UUID noPhone = support.proposal(c.id(), insurer, "ACCEPTED", clock.instant().plus(Duration.ofDays(15)),
                AMOUNT, broker);
        given().auth().oauth2(broker.token()).post("/api/v1/broker/proposals/{id}/payment-link", noPhone).then()
                .statusCode(409).body("code", equalTo("CUSTOMER_HAS_NO_MSISDN"));
    }

    /** Corre o bloco com o {@code TenantContext} em {@code SYSTEM} (o estímulo do cenário). */
    static final class TenantContextScope {

        static void system(Runnable work) {
            mz.co.moseguros.shared.tenant.TenantSnapshot previous =
                    mz.co.moseguros.shared.tenant.TenantContext.current();
            mz.co.moseguros.shared.tenant.TenantContext.set(mz.co.moseguros.shared.tenant.TenantSnapshot.system(null));
            try {
                work.run();
            } finally {
                mz.co.moseguros.shared.tenant.TenantContext.set(previous);
            }
        }
    }
}
