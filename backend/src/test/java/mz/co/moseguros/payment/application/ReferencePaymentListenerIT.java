package mz.co.moseguros.payment.application;

import static mz.co.moseguros.payment.PaymentFlowSupport.inSystem;
import static mz.co.moseguros.payment.PaymentFlowSupport.payReference;
import static mz.co.moseguros.payment.PaymentMessagesSupport.newFileSha;
import static mz.co.moseguros.payment.PaymentMessagesSupport.payment;
import static mz.co.moseguros.payment.PaymentMessagesSupport.text;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import mz.co.moseguros.SecurityTestConfiguration;
import mz.co.moseguros.SecurityTestConfiguration.IdentityFixtures;
import mz.co.moseguros.SecurityTestConfiguration.MutableClock;
import mz.co.moseguros.TestcontainersConfiguration;
import mz.co.moseguros.identity.IdentityApi;
import mz.co.moseguros.payment.FailureCode;
import mz.co.moseguros.payment.PaymentEventsRecorder;
import mz.co.moseguros.payment.PaymentFlowSupport;
import mz.co.moseguros.payment.PaymentFlowSupport.Seg;
import mz.co.moseguros.payment.PaymentMessagesSupport;
import mz.co.moseguros.payment.PaymentSucceeded;
import mz.co.moseguros.payment.application.reference.Ponto24CheckDigits;
import mz.co.moseguros.paymentcontracts.MessageHeaders;
import mz.co.moseguros.paymentcontracts.MessageIds;
import mz.co.moseguros.paymentcontracts.PaymentsTopology;
import mz.co.moseguros.paymentcontracts.ReferencePaymentReceived;
import mz.co.moseguros.proposal.ProposalPaid;
import mz.co.moseguros.rating.QuoteTestSupport.Customer;
import org.aopalliance.intercept.MethodInterceptor;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.aop.framework.Advised;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.dao.TransientDataAccessResourceException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Consumo de {@code ReferencePaymentReceived} e reconciliação (S5-10 §2, §3), com Postgres e RabbitMQ reais: as
 * mensagens são publicadas pelo {@code RabbitTemplate} do teste em {@code payments.events}, como o worker as
 * publica.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestcontainersConfiguration.class, SecurityTestConfiguration.class})
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ReferencePaymentListenerIT {

    @LocalServerPort
    int port;

    @Autowired
    IdentityFixtures fixtures;

    @Autowired
    IdentityApi identityApi;

    @Autowired
    MutableClock clock;

    @Autowired
    RabbitTemplate rabbit;

    @Autowired
    AmqpAdmin amqpAdmin;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    PaymentEventsRecorder events;

    @Autowired
    IntentStateService states;

    @Autowired
    TransactionTemplate tx;

    @Autowired
    ReferenceReconciliationService reconciliation;

    PaymentFlowSupport support;
    PaymentMessagesSupport messages;
    Seg insurer;
    Seg other;
    String entity;
    UUID sourceId;
    Customer customer;

    /** Falhas técnicas a injectar por {@code messageId} (o "bean de teste que lança"), e as chamadas feitas. */
    final Map<String, AtomicInteger> failuresLeft = new ConcurrentHashMap<>();
    final Map<String, AtomicInteger> calls = new ConcurrentHashMap<>();
    final MethodInterceptor faults = invocation -> {
        if (invocation.getMethod().getName().equals("handle")
                && invocation.getArguments()[0] instanceof ReferencePaymentReceived m) {
            calls.computeIfAbsent(m.messageId(), k -> new AtomicInteger()).incrementAndGet();
            AtomicInteger left = failuresLeft.get(m.messageId());
            if (left != null && left.getAndDecrement() > 0) {
                throw new TransientDataAccessResourceException("Simulated database failure");
            }
        }
        return invocation.proceed();
    };

    @BeforeAll
    void setUpAll() throws Exception {
        RestAssured.port = port;
        support = new PaymentFlowSupport(fixtures, identityApi, clock);
        support.createTemplate();
        messages = new PaymentMessagesSupport(rabbit, mapper, support.owner());
        insurer = support.insurer("Seguradora Reconciliação");
        entity = support.enable(insurer.id(), "BANK_REFERENCE", null, null, 1, "Referência");
        sourceId = support.owner().queryForObject("select id from payment.insurer_payment_method where insurer_id = ?"
                + " and method = 'BANK_REFERENCE'", UUID.class, insurer.id());
        other = support.insurer("Seguradora Outra Reconciliação");
        support.enable(other.id(), "BANK_REFERENCE", null, null, 1, "Referência");
        customer = support.customer();
        ((Advised) reconciliation).addAdvice(0, faults);
    }

    @AfterAll
    void tearDownAll() {
        ((Advised) reconciliation).removeAdvice(faults);
    }

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        clock.reset();
    }

    /** Proposta aceite paga por referência: devolve a intenção, a referência e o prazo. */
    record Issued(UUID proposalId, UUID intentId, String reference, Instant validUntil) {
    }

    Issued issue() {
        UUID proposal = support.accepted(customer.id(), insurer);
        Response r = payReference(customer.token(), proposal);
        r.then().statusCode(202);
        return new Issued(proposal, UUID.fromString(r.path("id")), r.path("nextAction.reference"),
                Instant.parse(r.path("nextAction.validUntil")));
    }

    ReferencePaymentReceived paid(Issued i, long amount, Instant at) {
        return payment(insurer.id(), sourceId, newFileSha(), 2, entity, i.reference(), amount, at);
    }

    Map<String, Object> exceptionFor(ReferencePaymentReceived m) {
        Map<String, Object> inbound = messages.awaitInbound(m.messageId());
        assertThat(inbound.get("outcome")).isEqualTo("EXCEPTION");
        Map<String, Object> e = messages.exceptionOf(m.messageId());
        assertThat(e.get("id")).isEqualTo(inbound.get("exception_id"));
        assertThat(e.get("insurer_id")).isEqualTo(m.insurerId());
        assertThat(e.get("status")).isEqualTo("OPEN");
        return e;
    }

    @Test
    @Order(1)
    void activeReferenceWithTheRightAmountConfirmsThePayment() {
        Issued i = issue();
        Instant paidAt = clock.instant().minusSeconds(60).truncatedTo(ChronoUnit.MILLIS);
        ReferencePaymentReceived m = paid(i, PaymentFlowSupport.AMOUNT, paidAt);

        messages.publish(m);

        Map<String, Object> inbound = messages.awaitInbound(m.messageId());
        assertThat(inbound.get("outcome")).isEqualTo("CONFIRMED");
        assertThat(inbound.get("intent_id")).isEqualTo(i.intentId());
        assertThat(inbound.get("file_sha256")).isEqualTo(m.fileSha256());
        Map<String, Object> ref = support.owner().queryForMap(
                "select status, paid_at, message_id, bank_transaction_id from payment.reference_payment "
                        + "where intent_id = ?", i.intentId());
        assertThat(ref.get("status")).isEqualTo("PAID");
        assertThat(ref.get("message_id")).isEqualTo(m.messageId());
        Map<String, Object> intent = support.intentRow(i.intentId());
        assertThat(intent.get("status")).isEqualTo("SUCCEEDED");
        assertThat(intent.get("confirmed_by")).isEqualTo("REFERENCE_FILE");
        assertThat(((java.sql.Timestamp) intent.get("succeeded_at")).toInstant()).isEqualTo(paidAt);
        assertThat(support.owner().queryForObject("select count(*) from payment.payment_event where intent_id = ? "
                + "and type = 'REFERENCE_PAID'", Long.class, i.intentId())).isEqualTo(1);
        await().atMost(PaymentMessagesSupport.WAIT).untilAsserted(() ->
                assertThat(support.proposalRow(i.proposalId()).get("status")).isEqualTo("PAID"));
        await().atMost(PaymentMessagesSupport.WAIT).untilAsserted(() ->
                assertThat(events.of(ProposalPaid.class, e -> e.proposalId().equals(i.proposalId()))).hasSize(1));
    }

    @Test
    @Order(2)
    void theSameMessageTwiceIsHandledOnce() {
        Issued i = issue();
        ReferencePaymentReceived m = paid(i, PaymentFlowSupport.AMOUNT, clock.instant());

        messages.publish(m);
        messages.awaitInbound(m.messageId());
        messages.publish(m);
        // A segunda passa pela idempotência: espera-se que a fila fique vazia antes de contar.
        await().atMost(PaymentMessagesSupport.WAIT).until(() ->
                rabbit.execute(c -> c.queueDeclarePassive(PaymentsTopology.REFERENCE_PAYMENTS_QUEUE).getMessageCount())
                        == 0);
        await().pollDelay(Duration.ofSeconds(1)).atMost(PaymentMessagesSupport.WAIT).untilAsserted(() ->
                assertThat(events.of(PaymentSucceeded.class, e -> e.intentId().equals(i.intentId()))).hasSize(1));
        assertThat(support.owner().queryForObject("select count(*) from payment.inbound_message where message_id = ?",
                Long.class, m.messageId())).isEqualTo(1);
        assertThat(support.owner().queryForObject("select count(*) from payment.payment_exception where intent_id = ?",
                Long.class, i.intentId())).isZero();
    }

    @Test
    @Order(3)
    void twoRecordsOfTheSameReferenceInOneFileConfirmThenAlreadyPaid() {
        Issued i = issue();
        String sha = newFileSha();
        // Como no exemplo oficial MEPS (00620400869 às 09:26 e às 09:29): duas linhas, messageId diferentes.
        ReferencePaymentReceived first = payment(insurer.id(), sourceId, sha, 4, entity, i.reference(),
                PaymentFlowSupport.AMOUNT, clock.instant().minusSeconds(180));
        ReferencePaymentReceived second = payment(insurer.id(), sourceId, sha, 5, entity, i.reference(),
                PaymentFlowSupport.AMOUNT, clock.instant());
        assertThat(first.messageId()).isNotEqualTo(second.messageId());

        messages.publish(first);
        messages.publish(second);

        assertThat(messages.awaitInbound(first.messageId()).get("outcome")).isEqualTo("CONFIRMED");
        Map<String, Object> e = exceptionFor(second);
        assertThat(e.get("type")).isEqualTo("ALREADY_PAID");
        assertThat(e.get("intent_id")).isEqualTo(i.intentId());
        assertThat(e.get("line_no")).isEqualTo(5);
        assertThat(e.get("source_id")).isEqualTo(sourceId);
    }

    @Test
    @Order(4)
    void unknownReferenceSaysWhetherTheCheckDigitsAreValid() {
        long amount = 123_400L;
        String valid = Ponto24CheckDigits.reference(entity, 9_999_000 + (int) (Math.random() * 999), 1, amount);
        String invalid = valid.substring(0, 9) + (valid.endsWith("00") ? "01" : "00");
        assertThat(Ponto24CheckDigits.isValid(entity, invalid, amount)).isFalse();

        ReferencePaymentReceived notIssued = payment(insurer.id(), sourceId, newFileSha(), 2, entity, valid, amount,
                clock.instant());
        ReferencePaymentReceived badDigits = payment(insurer.id(), sourceId, newFileSha(), 2, entity, invalid, amount,
                clock.instant());
        messages.publish(notIssued);
        messages.publish(badDigits);

        Map<String, Object> e1 = exceptionFor(notIssued);
        assertThat(e1.get("type")).isEqualTo("UNKNOWN_REFERENCE");
        assertThat(e1.get("detail")).isEqualTo("NOT_ISSUED");
        assertThat(e1.get("intent_id")).isNull();
        assertThat(e1.get("amount")).isEqualTo(amount);
        Map<String, Object> e2 = exceptionFor(badDigits);
        assertThat(e2.get("type")).isEqualTo("UNKNOWN_REFERENCE");
        assertThat(e2.get("detail")).isEqualTo("INVALID_CHECK_DIGITS");
    }

    @Test
    @Order(5)
    void aDifferentAmountIsAnAmountMismatchAndTheIntentStaysPending() {
        Issued i = issue();
        ReferencePaymentReceived m = paid(i, PaymentFlowSupport.AMOUNT - 10_000, clock.instant());

        messages.publish(m);

        Map<String, Object> e = exceptionFor(m);
        assertThat(e.get("type")).isEqualTo("AMOUNT_MISMATCH");
        assertThat(e.get("intent_id")).isEqualTo(i.intentId());
        assertThat(support.intentRow(i.intentId()).get("status")).isEqualTo("PENDING");
        assertThat(support.referenceStatus(i.intentId())).isEqualTo("ACTIVE");
    }

    @Test
    @Order(6)
    void expiredCancelledLateAndPaidReferencesBecomeExceptions() {
        Issued expired = issue();
        inSystem(tx, () -> states.expire(expired.intentId(), "test"));
        Issued cancelled = issue();
        inSystem(tx, () -> states.cancel(cancelled.intentId(), FailureCode.SUPERSEDED, "test"));
        Issued late = issue();
        Issued paidTwice = issue();

        ReferencePaymentReceived onExpired = paid(expired, PaymentFlowSupport.AMOUNT, clock.instant());
        ReferencePaymentReceived onCancelled = paid(cancelled, PaymentFlowSupport.AMOUNT, clock.instant());
        ReferencePaymentReceived afterValidity = paid(late, PaymentFlowSupport.AMOUNT,
                late.validUntil().plusSeconds(60));
        ReferencePaymentReceived firstPayment = paid(paidTwice, PaymentFlowSupport.AMOUNT, clock.instant());
        ReferencePaymentReceived secondPayment = paid(paidTwice, PaymentFlowSupport.AMOUNT, clock.instant());

        messages.publish(onExpired);
        messages.publish(onCancelled);
        messages.publish(afterValidity);
        messages.publish(firstPayment);
        assertThat(messages.awaitInbound(firstPayment.messageId()).get("outcome")).isEqualTo("CONFIRMED");
        messages.publish(secondPayment);

        assertThat(exceptionFor(onExpired).get("type")).isEqualTo("EXPIRED_REFERENCE");
        assertThat(exceptionFor(onCancelled).get("type")).isEqualTo("CANCELLED_REFERENCE");
        assertThat(exceptionFor(afterValidity).get("type")).isEqualTo("EXPIRED_REFERENCE");
        assertThat(support.referenceStatus(late.intentId())).isEqualTo("ACTIVE");
        assertThat(support.intentRow(late.intentId()).get("status")).isEqualTo("PENDING");
        assertThat(exceptionFor(secondPayment).get("type")).isEqualTo("ALREADY_PAID");
        assertThat(support.intentRow(expired.intentId()).get("status")).isEqualTo("EXPIRED");
    }

    @Test
    @Order(7)
    void aReferenceOfAnotherInsurerIsUnknownForTheInsurerOfTheMessage() {
        Issued i = issue();
        UUID otherSource = support.owner().queryForObject("select id from payment.insurer_payment_method "
                + "where insurer_id = ? and method = 'BANK_REFERENCE'", UUID.class, other.id());
        ReferencePaymentReceived m = payment(other.id(), otherSource, newFileSha(), 2, entity, i.reference(),
                PaymentFlowSupport.AMOUNT, clock.instant());

        messages.publish(m);

        Map<String, Object> e = exceptionFor(m);
        assertThat(e.get("type")).isEqualTo("UNKNOWN_REFERENCE");
        assertThat(e.get("insurer_id")).isEqualTo(other.id());
        assertThat(e.get("intent_id")).isNull();
        assertThat(support.intentRow(i.intentId()).get("status")).isEqualTo("PENDING");
    }

    @Test
    @Order(8)
    void invalidJsonAndAMismatchedInsurerGoToTheDlqWithoutRetries() throws Exception {
        amqpAdmin.purgeQueue(PaymentsTopology.REFERENCE_PAYMENTS_DLQ, false);
        long start = System.currentTimeMillis();
        String badJson = MessageIds.sha256Hex("bad-json-" + UUID.randomUUID());
        messages.send(PaymentsTopology.referencePaidKey(insurer.id()), badJson,
                MessageHeaders.typeOf(ReferencePaymentReceived.class), text("{not json"));

        ReferencePaymentReceived valid = payment(insurer.id(), sourceId, newFileSha(), 2, entity, "00000010199",
                10_000, clock.instant());
        UUID wrongInsurer = UUID.randomUUID();
        messages.send(PaymentsTopology.referencePaidKey(wrongInsurer), valid.messageId(),
                MessageHeaders.typeOf(ReferencePaymentReceived.class), messages.json(valid));

        ObjectNode unknownType = (ObjectNode) mapper.readTree(messages.json(valid));
        String unknownTypeId = MessageIds.sha256Hex("unknown-type-" + UUID.randomUUID());
        unknownType.put("messageId", unknownTypeId);
        messages.send(PaymentsTopology.referencePaidKey(insurer.id()), unknownTypeId, "SomethingElse",
                mapper.writeValueAsBytes(unknownType));

        // Com as repetições demorariam mais de 31 s (1 + 2 + 4 + 8 + 16): chegam logo à .dlq.
        for (String id : new String[] {badJson, valid.messageId(), unknownTypeId}) {
            Message dead = messages.receiveById(PaymentsTopology.REFERENCE_PAYMENTS_DLQ, id, Duration.ofSeconds(25));
            assertThat(dead).as(id).isNotNull();
        }
        assertThat(System.currentTimeMillis() - start).isLessThan(30_000);
        assertThat(calls.get(valid.messageId())).isNull();
        assertThat(support.owner().queryForObject("select count(*) from payment.inbound_message where message_id = ?",
                Long.class, valid.messageId())).isZero();
    }

    @Test
    @Order(9)
    void aTransientFailureOnceIsConfirmedOnTheSecondAttempt() {
        Issued i = issue();
        ReferencePaymentReceived m = paid(i, PaymentFlowSupport.AMOUNT, clock.instant());
        failuresLeft.put(m.messageId(), new AtomicInteger(1));

        messages.publish(m);

        assertThat(messages.awaitInbound(m.messageId()).get("outcome")).isEqualTo("CONFIRMED");
        assertThat(calls.get(m.messageId()).get()).isEqualTo(2);
        assertThat(support.intentRow(i.intentId()).get("status")).isEqualTo("SUCCEEDED");
    }

    @Test
    @Order(10)
    void sixTransientFailuresGoToTheDlqAfterSixAttempts() {
        Issued i = issue();
        ReferencePaymentReceived m = paid(i, PaymentFlowSupport.AMOUNT, clock.instant());
        failuresLeft.put(m.messageId(), new AtomicInteger(6));

        messages.publish(m);

        // 1 + 5 novas tentativas, com 1 s, 2 s, 4 s, 8 s e 16 s de espera (31 s).
        Message dead = messages.receiveById(PaymentsTopology.REFERENCE_PAYMENTS_DLQ, m.messageId(),
                Duration.ofSeconds(60));
        assertThat(dead).isNotNull();
        assertThat(calls.get(m.messageId()).get()).isEqualTo(6);
        assertThat(support.owner().queryForObject("select count(*) from payment.inbound_message where message_id = ?",
                Long.class, m.messageId())).isZero();
        assertThat(support.intentRow(i.intentId()).get("status")).isEqualTo("PENDING");
    }
}
