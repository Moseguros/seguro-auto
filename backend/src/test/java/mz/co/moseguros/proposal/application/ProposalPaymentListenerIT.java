package mz.co.moseguros.proposal.application;

import static mz.co.moseguros.payment.PaymentFlowSupport.id;
import static mz.co.moseguros.payment.PaymentFlowSupport.inSystem;
import static mz.co.moseguros.payment.PaymentFlowSupport.payMpesa;
import static mz.co.moseguros.payment.PaymentFlowSupport.payReference;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.awaitility.Awaitility.await;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import io.restassured.RestAssured;
import mz.co.moseguros.SecurityTestConfiguration;
import mz.co.moseguros.SecurityTestConfiguration.IdentityFixtures;
import mz.co.moseguros.SecurityTestConfiguration.MutableClock;
import mz.co.moseguros.TestcontainersConfiguration;
import mz.co.moseguros.identity.IdentityApi;
import mz.co.moseguros.payment.PaymentEventsRecorder;
import mz.co.moseguros.payment.PaymentFlowSupport;
import mz.co.moseguros.payment.PaymentFlowSupport.Seg;
import mz.co.moseguros.payment.PaymentSucceeded;
import mz.co.moseguros.payment.application.IntentStateService;
import mz.co.moseguros.payment.domain.ConfirmedBy;
import mz.co.moseguros.proposal.ProposalPaid;
import mz.co.moseguros.rating.QuoteTestSupport.Customer;
import mz.co.moseguros.shared.domain.Ids;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * O lado do {@code proposal} (S5-02 §9): {@code ProposalPaymentListener} (paga, repetido, já paga com
 * outra intenção, noutro estado), {@code ProposalExpiryJob} com pagamentos e a migração
 * ({@code ck_proposal_status}, {@code ck_proposal_paid}).
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestcontainersConfiguration.class, SecurityTestConfiguration.class})
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ProposalPaymentListenerIT {

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
    ApplicationEventPublisher publisher;

    @Autowired
    PaymentEventsRecorder events;

    @Autowired
    ProposalExpiryJob expiryJob;

    PaymentFlowSupport support;
    Seg insurer;

    @BeforeAll
    void setUpAll() throws Exception {
        RestAssured.port = port;
        support = new PaymentFlowSupport(fixtures, identityApi, clock);
        support.createTemplate();
        insurer = support.insurer("Seguradora Ouvinte");
        support.enableBoth(insurer.id());
    }

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        clock.reset();
    }

    @AfterEach
    void tearDown() {
        clock.reset();
    }

    UUID paidIntent(Customer c, UUID proposal) {
        UUID intentId = id(payReference(c.token(), proposal));
        inSystem(tx, () -> states.markSucceeded(intentId, null, ConfirmedBy.REFERENCE_FILE, null, "test"));
        return intentId;
    }

    void republish(UUID intentId) {
        PaymentSucceeded original = events.of(PaymentSucceeded.class, e -> e.intentId().equals(intentId)).get(0);
        inSystem(tx, () -> {
            publisher.publishEvent(new PaymentSucceeded(Ids.newId(), Instant.now(), original.intentId(),
                    original.reference(), original.insurerId(), original.payerUserId(), original.targetType(),
                    original.targetId(), original.purpose(), original.method(), original.amount(), original.paidAt()));
            return null;
        });
    }

    List<Map<String, Object>> exceptions(UUID intentId) {
        return support.owner().queryForList("select type, detail from payment.payment_exception where intent_id = ?",
                intentId);
    }

    @Test
    void acceptedProposalBecomesPaidWithTheAuditAndProposalPaid() {
        Customer c = support.customer();
        UUID proposal = support.accepted(c.id(), insurer);
        UUID intentId = paidIntent(c, proposal);

        await().atMost(Duration.ofSeconds(10)).untilAsserted(() ->
                assertThat(support.proposalRow(proposal).get("status")).isEqualTo("PAID"));
        Map<String, Object> audit = support.owner().queryForMap("""
                select actor_type, actor_id, after_state::text as after from audit.audit_event
                 where action = 'PROPOSAL.PAID' and entity_id = ?""", proposal.toString());
        assertThat(audit).containsEntry("actor_type", "SYSTEM").containsEntry("actor_id", "ProposalPaymentListener");
        assertThat((String) audit.get("after")).contains(intentId.toString());
        await().atMost(Duration.ofSeconds(5)).untilAsserted(() ->
                assertThat(events.of(ProposalPaid.class, e -> e.proposalId().equals(proposal))).hasSize(1));

        // repetido (outro eventId, a mesma intenção): nada
        republish(intentId);
        await().during(Duration.ofSeconds(2)).atMost(Duration.ofSeconds(5)).untilAsserted(() -> {
            assertThat(events.of(ProposalPaid.class, e -> e.proposalId().equals(proposal))).hasSize(1);
            assertThat(exceptions(intentId)).isEmpty();
        });
    }

    @Test
    void aSecondPaymentOfAPaidProposalBecomesAnException() {
        Customer c = support.customer();
        UUID proposal = support.accepted(c.id(), insurer);
        // duas formas de pagamento em curso: a referência e o M-Pesa (que não é cancelado)
        UUID reference = id(payReference(c.token(), proposal));
        UUID mpesa = id(payMpesa(c.token(), proposal, "841234567"));
        await().atMost(Duration.ofSeconds(10)).untilAsserted(() ->
                assertThat(support.intentRow(mpesa).get("status")).isEqualTo("PENDING"));
        inSystem(tx, () -> states.markSucceeded(reference, null, ConfirmedBy.REFERENCE_FILE, null, "test"));
        await().atMost(Duration.ofSeconds(10)).untilAsserted(() ->
                assertThat(support.proposalRow(proposal).get("payment_intent_id")).isEqualTo(reference));
        assertThat(support.intentRow(mpesa).get("status")).as("M-Pesa is never cancelled").isEqualTo("PENDING");

        inSystem(tx, () -> states.markSucceeded(mpesa, null, ConfirmedBy.PROVIDER, "TX-LATE", "test"));
        await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> assertThat(exceptions(mpesa)).singleElement()
                .satisfies(r -> assertThat(r).containsEntry("type", "TARGET_NOT_PAYABLE")
                        .containsEntry("detail", "PROPOSAL_ALREADY_PAID")));
        assertThat(support.proposalRow(proposal).get("payment_intent_id")).isEqualTo(reference);

        // repetido: um só caso por intenção
        republish(mpesa);
        await().during(Duration.ofSeconds(2)).atMost(Duration.ofSeconds(5)).untilAsserted(() ->
                assertThat(exceptions(mpesa)).hasSize(1));
    }

    @Test
    void paymentOfARejectedProposalBecomesAnException() {
        Customer c = support.customer();
        UUID proposal = support.accepted(c.id(), insurer);
        UUID intentId = id(payReference(c.token(), proposal));
        support.owner().update("update proposal.proposal set status = 'REJECTED', expires_at = null where id = ?",
                proposal);
        inSystem(tx, () -> states.markSucceeded(intentId, null, ConfirmedBy.MANUAL, null, "test"));
        await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> assertThat(exceptions(intentId)).singleElement()
                .satisfies(r -> assertThat(r).containsEntry("detail", "PROPOSAL_REJECTED")));
        assertThat(support.proposalRow(proposal).get("status")).isEqualTo("REJECTED");
    }

    // --- ProposalExpiryJob (S4-02, alterado no S5-02) ---

    @Test
    void expiryJobSkipsPaidProposalsAndMpesaInProgressButNotReferences() {
        Customer c = support.customer();
        UUID paid = support.accepted(c.id(), insurer);
        paidIntent(c, paid);
        await().atMost(Duration.ofSeconds(10)).untilAsserted(() ->
                assertThat(support.proposalRow(paid).get("status")).isEqualTo("PAID"));
        UUID withMpesa = support.accepted(c.id(), insurer);
        UUID mpesa = id(payMpesa(c.token(), withMpesa, "841234567"));
        await().atMost(Duration.ofSeconds(10)).untilAsserted(() ->
                assertThat(support.intentRow(mpesa).get("status")).isEqualTo("PENDING"));
        UUID withReference = support.accepted(c.id(), insurer);
        UUID reference = id(payReference(c.token(), withReference));
        for (UUID p : List.of(withMpesa, withReference)) {
            support.owner().update("update proposal.proposal set expires_at = now() - interval '1 minute' where id = ?",
                    p);
        }

        expiryJob.expire();

        assertThat(support.proposalRow(paid).get("status")).isEqualTo("PAID");
        assertThat(support.proposalRow(withMpesa).get("status")).isEqualTo("ACCEPTED");
        assertThat(support.proposalRow(withMpesa).get("expires_at")).as("unchanged").isNotNull();
        assertThat(support.proposalRow(withReference).get("status")).isEqualTo("EXPIRED");
        assertThat(support.intentRow(reference).get("status")).isEqualTo("PENDING");
    }

    // --- Migração ---

    @Test
    void migrationRecreatesTheStatusConstraintAndAddsThePaidOne() {
        String status = support.owner().queryForObject("""
                select pg_get_constraintdef(oid) from pg_constraint
                 where conname = 'ck_proposal_status' and conrelid = 'proposal.proposal'::regclass""", String.class);
        for (String s : List.of("DRAFT", "UNDER_REVIEW", "INFO_REQUESTED", "ACCEPTED", "REJECTED", "EXPIRED", "PAID",
                "CONVERTED")) {
            assertThat(status).contains("'" + s + "'");
        }
        assertThat(status).doesNotContain("SUBMITTED");
        assertThat(support.owner().queryForObject("""
                select pg_get_constraintdef(oid) from pg_constraint
                 where conname = 'ck_proposal_paid' and conrelid = 'proposal.proposal'::regclass""", String.class))
                .contains("paid_at").contains("payment_intent_id");

        Customer c = support.customer();
        UUID proposal = support.accepted(c.id(), insurer);
        assertThatThrownBy(() -> support.setProposalStatus(proposal, "SUBMITTED"))
                .isInstanceOf(DataIntegrityViolationException.class);
        assertThatThrownBy(() -> support.setProposalStatus(proposal, "PAID"))
                .as("PAID without paid_at and payment_intent_id").isInstanceOf(DataIntegrityViolationException.class);
    }
}
