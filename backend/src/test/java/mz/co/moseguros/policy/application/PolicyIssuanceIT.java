package mz.co.moseguros.policy.application;

import static mz.co.moseguros.payment.PaymentFlowSupport.AMOUNT;
import static mz.co.moseguros.payment.PaymentFlowSupport.id;
import static mz.co.moseguros.payment.PaymentFlowSupport.inSystem;
import static mz.co.moseguros.payment.PaymentFlowSupport.payMpesa;
import static mz.co.moseguros.payment.PaymentFlowSupport.payReference;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.restassured.RestAssured;
import mz.co.moseguros.SecurityTestConfiguration;
import mz.co.moseguros.SecurityTestConfiguration.IdentityFixtures;
import mz.co.moseguros.SecurityTestConfiguration.MutableClock;
import mz.co.moseguros.TestcontainersConfiguration;
import mz.co.moseguros.identity.IdentityApi;
import mz.co.moseguros.payment.PaymentEventsRecorder;
import mz.co.moseguros.payment.PaymentFlowSupport.Seg;
import mz.co.moseguros.payment.application.IntentStateService;
import mz.co.moseguros.payment.domain.ConfirmedBy;
import mz.co.moseguros.policy.PolicyEventsRecorder;
import mz.co.moseguros.policy.PolicyTestSupport;
import mz.co.moseguros.policy.PolicyTestSupport.Paid;
import mz.co.moseguros.proposal.ProposalPaid;
import mz.co.moseguros.rating.QuoteTestSupport.Customer;
import mz.co.moseguros.shared.domain.Ids;
import mz.co.moseguros.shared.domain.Money;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Emissão automática (S6-01 §5): do pagamento (M-Pesa e referência) à apólice {@code ACTIVE}, sem acção
 * de pessoas; idempotência (repetido e em simultâneo); as falhas de negócio (pagamento inválido, proposta
 * não paga, montante diferente); auditoria e logs sem o código de verificação.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestcontainersConfiguration.class, SecurityTestConfiguration.class})
@ActiveProfiles("test")
@TestPropertySource(properties = mz.co.moseguros.payment.TestGateway.PROPERTY)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(OutputCaptureExtension.class)
class PolicyIssuanceIT {

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
    PolicyEventsRecorder policyEvents;

    @Autowired
    PaymentEventsRecorder paymentEvents;

    @Autowired
    MeterRegistry meters;

    PolicyTestSupport support;
    Seg insurer;

    @BeforeAll
    void setUpAll() throws Exception {
        RestAssured.port = port;
        support = new PolicyTestSupport(fixtures, identityApi, clock, tx, states);
        insurer = support.setUp("Seguradora Emissão");
    }

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        clock.reset();
    }

    // --- Fluxo completo ---

    @Test
    void referencePaymentIssuesThePolicyAutomatically(CapturedOutput output) {
        Customer c = support.payments().customer();
        UUID proposal = support.payments().accepted(c.id(), insurer);
        UUID intentId = id(payReference(c.token(), proposal));
        // início pedido ontem (00:00 de Maputo); o banco recebeu o pagamento há 2 horas
        Instant requestedStart = LocalDate.now(PolicyTestSupportZone.MAPUTO).minusDays(1)
                .atStartOfDay(PolicyTestSupportZone.MAPUTO).toInstant();
        shiftProposalStart(proposal, requestedStart);
        Instant requestedEnd = owner().queryForObject("select end_at from proposal.proposal where id = ?",
                Timestamp.class, proposal).toInstant();
        Instant bankTime = Instant.now().minus(Duration.ofHours(2)).truncatedTo(ChronoUnit.SECONDS);

        Instant before = Instant.now();
        inSystem(tx, () -> states.markSucceeded(intentId, bankTime, ConfirmedBy.REFERENCE_FILE, null, "test"));
        UUID policyId = support.awaitPolicy(proposal);
        assertThat(Duration.between(before, Instant.now())).isLessThan(Duration.ofSeconds(5));

        Map<String, Object> policy = support.policyRow(policyId);
        assertThat(policy.get("status")).isEqualTo("ACTIVE");
        assertThat((String) policy.get("number")).matches("^AP-\\d{4}-\\d{6}$");
        assertThat(policy.get("insurer_id")).isEqualTo(insurer.id());
        assertThat(policy.get("payment_intent_id")).isEqualTo(intentId);
        assertThat(policy.get("policyholder_user_id")).isEqualTo(c.id());
        assertThat(policy.get("payment_plan")).isEqualTo("FULL");
        assertThat(policy.get("coverage_type")).isEqualTo("MTPL_ONLY");
        assertThat(policy.get("renewal_type")).isEqualTo("ANNUAL_RENEWABLE");
        assertThat(((Number) policy.get("total_amount")).longValue()).isEqualTo(AMOUNT);
        assertThat((String) policy.get("plate")).matches("^[A-Z]{3}-\\d{3}-[A-Z]{2}$");
        // regra 3: começa no pagamento e mantém a duração pedida
        assertThat(((Timestamp) policy.get("start_at")).toInstant()).isEqualTo(bankTime);
        assertThat(((Timestamp) policy.get("end_at")).toInstant())
                .isEqualTo(requestedEnd.plus(Duration.between(requestedStart, bankTime)));

        List<Map<String, Object>> instalments = owner().queryForList(
                "select seq, amount, status, payment_intent_id, paid_at from policy.instalment where policy_id = ?",
                policyId);
        assertThat(instalments).singleElement().satisfies(i -> {
            assertThat(i).containsEntry("seq", 1).containsEntry("status", "PAID")
                    .containsEntry("payment_intent_id", intentId);
            assertThat(((Number) i.get("amount")).longValue()).isEqualTo(AMOUNT);
            assertThat(((Timestamp) i.get("paid_at")).toInstant()).isEqualTo(bankTime);
        });

        Map<String, Object> certificate = owner().queryForMap(
                "select id, number, status, verification_token, valid_from, valid_to from policy.certificate "
                        + "where policy_id = ?", policyId);
        String token = (String) certificate.get("verification_token");
        assertThat((String) certificate.get("number")).matches("^CT-\\d{4}-\\d{7}$");
        assertThat(certificate.get("status")).isEqualTo("VALID");
        assertThat(token).hasSize(22).matches("^[A-Za-z0-9_-]{22}$");
        assertThat(certificate.get("valid_from")).isEqualTo(policy.get("start_at"));
        assertThat(certificate.get("valid_to")).isEqualTo(policy.get("end_at"));

        // DOCUMENTS_READY (S6-02) é gravado depois, pelo policy-documents
        assertThat(owner().queryForList("select type from policy.policy_event where policy_id = ? "
                + "and type <> 'DOCUMENTS_READY' order by type", String.class, policyId))
                .containsExactly("CERTIFICATE_ISSUED", "ISSUED");
        assertThat(owner().queryForList("select distinct actor_type || ':' || actor_id from policy.policy_event "
                + "where policy_id = ? and type <> 'DOCUMENTS_READY'", String.class, policyId))
                .containsExactly("SYSTEM:policy-issuance");

        Map<String, Object> converted = support.proposalRow(proposal);
        assertThat(converted).containsEntry("status", "CONVERTED").containsEntry("policy_id", policyId)
                .containsEntry("payment_intent_id", intentId);
        assertThat(((Timestamp) converted.get("paid_at")).toInstant()).isEqualTo(bankTime);
        assertThat(owner().queryForObject("select count(*) from audit.audit_event where action = 'PROPOSAL.CONVERT' "
                + "and entity_id = ?", Integer.class, proposal.toString())).isOne();

        await().atMost(Duration.ofSeconds(5)).untilAsserted(() ->
                assertThat(policyEvents.issued(e -> e.proposalId().equals(proposal))).singleElement()
                        .satisfies(e -> {
                            assertThat(e.policyId()).isEqualTo(policyId);
                            assertThat(e.policyNumber()).isEqualTo(policy.get("number"));
                            assertThat(e.certificateId()).isEqualTo(certificate.get("id"));
                            assertThat(e.plate()).isEqualTo(policy.get("plate"));
                            assertThat(e.policyholderUserId()).isEqualTo(c.id());
                            assertThat(e.startAt()).isEqualTo(bankTime);
                        }));

        // auditoria sem o código de verificação
        Map<String, Object> policyAudit = owner().queryForMap("""
                select actor_type, actor_id, insurer_id, before_state::text as b, after_state::text as a
                  from audit.audit_event where action = 'POLICY.ISSUE' and entity_id = ?""", policyId.toString());
        assertThat(policyAudit).containsEntry("actor_type", "SYSTEM").containsEntry("actor_id", "policy-issuance")
                .containsEntry("insurer_id", insurer.id());
        assertThat(policyAudit.get("b")).isNull();
        assertThat((String) policyAudit.get("a")).contains((String) policy.get("number"), proposal.toString(),
                intentId.toString(), "MTPL_ONLY", (String) policy.get("plate"), "FULL", "299900");
        Map<String, Object> certificateAudit = owner().queryForMap("""
                select actor_type, after_state::text as a from audit.audit_event
                 where action = 'CERTIFICATE.ISSUE' and entity_id = ?""", certificate.get("id").toString());
        assertThat(certificateAudit).containsEntry("actor_type", "SYSTEM");
        assertThat((String) certificateAudit.get("a")).contains((String) certificate.get("number"),
                policyId.toString()).doesNotContain(token);
        assertThat(owner().queryForObject("select count(*) from audit.audit_event where after_state::text like ?",
                Integer.class, "%" + token + "%")).isZero();
        // nem nos logs
        assertThat(output.getAll()).contains("Policy issued").doesNotContain(token);

        // métricas
        assertThat(meters.find(PolicyMetrics.ISSUED).tag("productType", "MTPL_ONLY").counter()).isNotNull();
        Timer latency = meters.find(PolicyMetrics.LATENCY).timer();
        assertThat(latency).isNotNull();
        assertThat(latency.count()).isPositive();
    }

    @Test
    void mpesaPaymentIssuesThePolicyAutomatically() {
        Customer c = support.payments().customer();
        UUID proposal = support.payments().accepted(c.id(), insurer);
        UUID intentId = id(payMpesa(c.token(), proposal, "841234567"));
        await().atMost(Duration.ofSeconds(10)).untilAsserted(() ->
                assertThat(support.payments().intentRow(intentId).get("status")).isEqualTo("PENDING"));

        inSystem(tx, () -> states.markSucceeded(intentId, null, ConfirmedBy.PROVIDER, "TX-POLICY", "test"));
        UUID policyId = support.awaitPolicy(proposal);

        Map<String, Object> policy = support.policyRow(policyId);
        assertThat(policy).containsEntry("status", "ACTIVE").containsEntry("payment_intent_id", intentId);
        // pagamento antes do início pedido: as datas da proposta ficam iguais
        Map<String, Object> dates = owner().queryForMap("select start_at, end_at from proposal.proposal where id = ?",
                proposal);
        assertThat(policy.get("start_at")).isEqualTo(dates.get("start_at"));
        assertThat(policy.get("end_at")).isEqualTo(dates.get("end_at"));
        assertThat(support.proposalRow(proposal)).containsEntry("status", "CONVERTED")
                .containsEntry("policy_id", policyId);
    }

    // --- Idempotência ---

    @Test
    void theSameEventDeliveredTwiceIssuesASinglePolicy() {
        Customer c = support.payments().customer();
        Paid paid = support.paid(c, insurer);
        UUID policyId = support.awaitPolicy(paid.proposalId());
        await().atMost(Duration.ofSeconds(5)).untilAsserted(() ->
                assertThat(policyEvents.issued(e -> e.proposalId().equals(paid.proposalId()))).hasSize(1));

        ProposalPaid original = paymentEvents.of(ProposalPaid.class, e -> e.proposalId().equals(paid.proposalId()))
                .get(0);
        publish(original);
        awaitListenerDone(paid.proposalId(), 2);

        assertThat(support.policyCount(paid.proposalId())).isOne();
        assertThat(support.proposalRow(paid.proposalId())).containsEntry("policy_id", policyId);
        assertThat(policyEvents.issued(e -> e.proposalId().equals(paid.proposalId()))).hasSize(1);
    }

    @Test
    void twoSimultaneousDeliveriesIssueASinglePolicy() {
        Customer c = support.payments().customer();
        Fixture f = paidBySql(c, insurer);
        ProposalPaid event = event(f.proposalId(), f.intentId(), c.id());
        inSystem(tx, () -> {
            publisher.publishEvent(event);
            publisher.publishEvent(new ProposalPaid(Ids.newId(), event.occurredAt(), event.proposalId(),
                    event.insurerId(), event.customerUserId(), event.paymentIntentId(), event.amount(),
                    event.paidAt()));
            return null;
        });

        awaitListenerDone(f.proposalId(), 2);
        assertThat(support.policyCount(f.proposalId())).isOne();
        assertThat(support.proposalRow(f.proposalId())).containsEntry("status", "CONVERTED");
        assertThat(policyEvents.issued(e -> e.proposalId().equals(f.proposalId()))).hasSize(1);
    }

    // --- Falhas de negócio ---

    @Test
    void anUnconfirmedPaymentNeverIssuesAPolicy() {
        Customer c = support.payments().customer();
        UUID proposal = support.payments().accepted(c.id(), insurer);
        UUID intentId = id(payReference(c.token(), proposal)); // PENDING
        markProposalPaid(proposal, intentId);
        assertRejected(proposal, intentId, c.id(), "PAYMENT_NOT_VALID");
    }

    @Test
    void aPaymentOfAnotherInsurerNeverIssuesAPolicy() {
        Customer c = support.payments().customer();
        Fixture f = paidBySql(c, insurer);
        Seg other = support.insurer("Seguradora Outra Emissão");
        owner().update("update payment.payment_intent set insurer_id = ? where id = ?", other.id(), f.intentId());
        assertRejected(f.proposalId(), f.intentId(), c.id(), "PAYMENT_NOT_VALID");
    }

    @Test
    void aPaymentOfAnotherTargetNeverIssuesAPolicy() {
        Customer c = support.payments().customer();
        Fixture other = paidBySql(c, insurer);
        UUID proposal = support.payments().accepted(c.id(), insurer);
        markProposalPaid(proposal, other.intentId());
        assertRejected(proposal, other.intentId(), c.id(), "PAYMENT_NOT_VALID");
        // a outra proposta não é afectada (o seu evento nunca foi publicado)
        assertThat(support.policyCount(other.proposalId())).isZero();
    }

    @Test
    void anUnknownPaymentNeverIssuesAPolicy() {
        Customer c = support.payments().customer();
        Fixture f = paidBySql(c, insurer);
        assertRejected(f.proposalId(), UUID.randomUUID(), c.id(), "PAYMENT_NOT_VALID");
    }

    @Test
    void aProposalThatIsNotPaidNeverGetsAPolicy() {
        Customer c = support.payments().customer();
        UUID proposal = support.payments().accepted(c.id(), insurer);
        UUID intentId = id(payReference(c.token(), proposal));
        succeedBySql(intentId);
        assertRejected(proposal, intentId, c.id(), "PROPOSAL_NOT_PAID");
        assertThat(support.proposalRow(proposal).get("status")).isEqualTo("ACCEPTED");
    }

    @Test
    void anAmountDifferentFromTheFirstInstalmentNeverIssuesAPolicy() {
        Customer c = support.payments().customer();
        Fixture f = paidBySql(c, insurer);
        owner().update("update proposal.proposal set total_amount = ? where id = ?", AMOUNT + 100_000, f.proposalId());
        assertRejected(f.proposalId(), f.intentId(), c.id(), "AMOUNT_MISMATCH");
        assertThat(support.proposalRow(f.proposalId()).get("status")).as("stays PAID").isEqualTo("PAID");
    }

    // --- Apoio ---

    record Fixture(UUID proposalId, UUID intentId) {
    }

    /** Proposta {@code PAID} e intenção {@code SUCCEEDED} gravadas como dono, sem publicar nada. */
    Fixture paidBySql(Customer c, Seg seg) {
        UUID proposal = support.payments().accepted(c.id(), seg);
        UUID intentId = id(payReference(c.token(), proposal));
        succeedBySql(intentId);
        markProposalPaid(proposal, intentId);
        return new Fixture(proposal, intentId);
    }

    void succeedBySql(UUID intentId) {
        owner().update("""
                update payment.payment_intent set status = 'SUCCEEDED', succeeded_at = now(),
                       confirmed_by = 'REFERENCE_FILE' where id = ?""", intentId);
    }

    void markProposalPaid(UUID proposal, UUID intentId) {
        owner().update("""
                update proposal.proposal set status = 'PAID', paid_at = now(), payment_intent_id = ?,
                       expires_at = null where id = ?""", intentId, proposal);
    }

    void shiftProposalStart(UUID proposal, Instant start) {
        owner().update("""
                update proposal.proposal set start_at = ?, end_at = end_at - (start_at - ?) where id = ?""",
                Timestamp.from(start), Timestamp.from(start), proposal);
    }

    ProposalPaid event(UUID proposal, UUID intentId, UUID customerId) {
        Instant now = Instant.now();
        return new ProposalPaid(Ids.newId(), now, proposal, insurer.id(), customerId, intentId,
                Money.ofCentavos(AMOUNT), now);
    }

    void publish(ProposalPaid event) {
        inSystem(tx, () -> {
            publisher.publishEvent(new ProposalPaid(Ids.newId(), event.occurredAt(), event.proposalId(),
                    event.insurerId(), event.customerUserId(), event.paymentIntentId(), event.amount(),
                    event.paidAt()));
            return null;
        });
    }

    void assertRejected(UUID proposal, UUID intentId, UUID customerId, String reason) {
        double before = failures(reason);
        inSystem(tx, () -> {
            publisher.publishEvent(event(proposal, intentId, customerId));
            return null;
        });
        awaitListenerDone(proposal, 1);
        assertThat(support.policyCount(proposal)).isZero();
        assertThat(failures(reason)).isEqualTo(before + 1);
        assertThat(policyEvents.issued(e -> e.proposalId().equals(proposal))).isEmpty();
    }

    double failures(String reason) {
        Counter counter = meters.find(PolicyMetrics.FAILURES).tag("reason", reason).counter();
        return counter == null ? 0 : counter.count();
    }

    /** Espera que o {@code ProposalPaidListener} tenha concluído {@code count} entregas desta proposta. */
    void awaitListenerDone(UUID proposal, int count) {
        await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> assertThat(owner().queryForObject("""
                select count(*) from public.event_publication
                 where listener_id like '%ProposalPaidListener%' and serialized_event like ?
                   and completion_date is not null""", Integer.class, "%" + proposal + "%")).isEqualTo(count));
        assertThat(owner().queryForObject("""
                select count(*) from public.event_publication
                 where listener_id like '%ProposalPaidListener%' and serialized_event like ?
                   and completion_date is null""", Integer.class, "%" + proposal + "%"))
                .as("no unhandled exception").isZero();
    }

    org.springframework.jdbc.core.JdbcTemplate owner() {
        return support.owner();
    }

    /** Fuso de Maputo para as datas do teste. */
    static final class PolicyTestSupportZone {
        static final java.time.ZoneId MAPUTO = java.time.ZoneId.of("Africa/Maputo");

        private PolicyTestSupportZone() {
        }
    }
}
