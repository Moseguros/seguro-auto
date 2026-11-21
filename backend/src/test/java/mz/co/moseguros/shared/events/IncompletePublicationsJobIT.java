package mz.co.moseguros.shared.events;

import static mz.co.moseguros.payment.PaymentFlowSupport.AMOUNT;
import static mz.co.moseguros.payment.PaymentFlowSupport.id;
import static mz.co.moseguros.payment.PaymentFlowSupport.inSystem;
import static mz.co.moseguros.payment.PaymentFlowSupport.payReference;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.restassured.RestAssured;
import mz.co.moseguros.SecurityTestConfiguration;
import mz.co.moseguros.SecurityTestConfiguration.IdentityFixtures;
import mz.co.moseguros.SecurityTestConfiguration.MutableClock;
import mz.co.moseguros.TestcontainersConfiguration;
import mz.co.moseguros.identity.IdentityApi;
import mz.co.moseguros.payment.PaymentFlowSupport.Seg;
import mz.co.moseguros.payment.application.IntentStateService;
import mz.co.moseguros.policy.PolicyTestSupport;
import mz.co.moseguros.proposal.ProposalPaid;
import mz.co.moseguros.rating.QuoteTestSupport.Customer;
import mz.co.moseguros.shared.domain.Ids;
import mz.co.moseguros.shared.domain.Money;
import net.javacrumbs.shedlock.core.LockConfiguration;
import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.core.SimpleLock;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * {@code IncompletePublicationsJob} (S6-01 §2): uma publicação incompleta há mais de 5 minutos é reenviada
 * e o listener emite a apólice; o <i>gauge</i> conta-a antes do reenvio; o lock do ShedLock (o mesmo
 * {@code LockProvider} do {@code SchedulerLockConfig}, com {@code moseguros.jobs.enabled=false}) não deixa
 * duas execuções em simultâneo.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestcontainersConfiguration.class, SecurityTestConfiguration.class})
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class IncompletePublicationsJobIT {

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
    IncompletePublicationsJob job;

    @Autowired
    LockProvider lockProvider;

    @Autowired
    MeterRegistry meters;

    PolicyTestSupport support;
    Seg insurer;

    @BeforeAll
    void setUpAll() throws Exception {
        RestAssured.port = port;
        support = new PolicyTestSupport(fixtures, identityApi, clock, tx, states);
        insurer = support.setUp("Seguradora Reenvio");
    }

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        clock.reset();
    }

    int incomplete(UUID proposal) {
        Integer n = support.owner().queryForObject("""
                select count(*) from public.event_publication
                 where listener_id like '%ProposalPaidListener%' and serialized_event like ?
                   and completion_date is null""", Integer.class, "%" + proposal + "%");
        return n == null ? 0 : n;
    }

    @Test
    void anIncompletePublicationIsResubmittedAndThePolicyIssued() {
        Customer c = support.payments().customer();
        UUID proposal = support.payments().accepted(c.id(), insurer);
        UUID intentId = id(payReference(c.token(), proposal));
        support.owner().update("""
                update payment.payment_intent set status = 'SUCCEEDED', succeeded_at = now(),
                       confirmed_by = 'REFERENCE_FILE' where id = ?""", intentId);
        // a versão do produto não existe: a emissão falha com uma excepção e a publicação fica incompleta
        UUID productVersion = support.owner().queryForObject(
                "select product_version_id from proposal.proposal where id = ?", UUID.class, proposal);
        support.owner().update("""
                update proposal.proposal set status = 'PAID', paid_at = now(), payment_intent_id = ?,
                       expires_at = null, product_version_id = ? where id = ?""", intentId, UUID.randomUUID(),
                proposal);
        Instant now = Instant.now();
        inSystem(tx, () -> {
            publisher.publishEvent(new ProposalPaid(Ids.newId(), now, proposal, insurer.id(), c.id(), intentId,
                    Money.ofCentavos(AMOUNT), now));
            return null;
        });
        await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> assertThat(incomplete(proposal)).isOne());
        await().during(Duration.ofSeconds(1)).atMost(Duration.ofSeconds(5))
                .untilAsserted(() -> assertThat(support.policyCount(proposal)).isZero());

        // recente: não conta nem é reenviada
        job.resubmit();
        assertThat(incomplete(proposal)).isOne();
        assertThat(support.policyCount(proposal)).isZero();

        // corrigida e com mais de 5 minutos
        support.owner().update("update proposal.proposal set product_version_id = ? where id = ?", productVersion,
                proposal);
        support.owner().update("""
                update public.event_publication set publication_date = now() - interval '10 minutes'
                 where listener_id like '%ProposalPaidListener%' and serialized_event like ?
                   and completion_date is null""", "%" + proposal + "%");

        long counted = job.resubmit();
        assertThat(counted).as("counted before the resubmission").isPositive();
        Gauge gauge = meters.find(IncompletePublicationsJob.GAUGE).gauge();
        assertThat(gauge).isNotNull();
        assertThat(gauge.value()).isEqualTo((double) counted);
        assertThat(job.incomplete()).isEqualTo(counted);

        UUID policyId = support.awaitPolicy(proposal);
        assertThat(support.policyRow(policyId).get("status")).isEqualTo("ACTIVE");
        await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> assertThat(incomplete(proposal)).isZero());
        assertThat(support.proposalRow(proposal)).containsEntry("status", "CONVERTED");
    }

    @Test
    void twoInstancesNeverRunAtTheSameTime() {
        LockConfiguration config = new LockConfiguration(Instant.now(), IncompletePublicationsJob.LOCK,
                Duration.ofMinutes(4), Duration.ZERO);
        Optional<SimpleLock> first = lockProvider.lock(config);
        assertThat(first).isPresent();
        try {
            assertThat(lockProvider.lock(config)).as("second instance").isEmpty();
        } finally {
            first.get().unlock();
        }
        Optional<SimpleLock> again = lockProvider.lock(config);
        assertThat(again).as("after unlock").isPresent();
        again.get().unlock();
        assertThat(support.owner().queryForObject("select count(*) from shared.shedlock where name = ?",
                Integer.class, IncompletePublicationsJob.LOCK)).isOne();
    }
}
