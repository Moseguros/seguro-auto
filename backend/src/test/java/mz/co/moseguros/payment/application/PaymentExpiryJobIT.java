package mz.co.moseguros.payment.application;

import static mz.co.moseguros.payment.PaymentFlowSupport.id;
import static mz.co.moseguros.payment.PaymentFlowSupport.payMpesa;
import static mz.co.moseguros.payment.PaymentFlowSupport.payReference;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import java.time.Duration;
import java.util.UUID;

import io.restassured.RestAssured;
import mz.co.moseguros.SecurityTestConfiguration;
import mz.co.moseguros.SecurityTestConfiguration.IdentityFixtures;
import mz.co.moseguros.SecurityTestConfiguration.MutableClock;
import mz.co.moseguros.TestcontainersConfiguration;
import mz.co.moseguros.identity.IdentityApi;
import mz.co.moseguros.payment.PaymentEventsRecorder;
import mz.co.moseguros.payment.PaymentFailed;
import mz.co.moseguros.payment.PaymentFlowSupport;
import mz.co.moseguros.payment.PaymentFlowSupport.Seg;
import mz.co.moseguros.payment.PaymentStatus;
import mz.co.moseguros.rating.QuoteTestSupport.Customer;
import org.junit.jupiter.api.AfterEach;
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
 * Expiração das intenções (S5-02 §8): referência vencida → {@code EXPIRED} (a referência também) com
 * {@code PaymentFailed}; M-Pesa {@code CREATED} vencida expira; M-Pesa {@code PENDING} não é tocada.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestcontainersConfiguration.class, SecurityTestConfiguration.class})
@ActiveProfiles("test")
@TestPropertySource(properties = mz.co.moseguros.payment.TestGateway.PROPERTY)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PaymentExpiryJobIT {

    @LocalServerPort
    int port;

    @Autowired
    IdentityFixtures fixtures;

    @Autowired
    IdentityApi identityApi;

    @Autowired
    MutableClock clock;

    @Autowired
    PaymentExpiryJob job;

    @Autowired
    PaymentEventsRecorder events;

    PaymentFlowSupport support;
    Seg insurer;

    @BeforeAll
    void setUpAll() throws Exception {
        RestAssured.port = port;
        support = new PaymentFlowSupport(fixtures, identityApi, clock);
        support.createTemplate();
        insurer = support.insurer("Seguradora Expiração Pagamentos");
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

    void pastDue(UUID intentId) {
        support.owner().update("update payment.payment_intent set expires_at = now() - interval '1 minute' "
                + "where id = ?", intentId);
    }

    @Test
    void expiredReferencesAndUnsentMpesaExpireButPendingMpesaDoesNot() {
        Customer c = support.customer();
        UUID reference = id(payReference(c.token(), support.accepted(c.id(), insurer)));
        UUID pendingMpesa = id(payMpesa(c.token(), support.accepted(c.id(), insurer), "841234567"));
        UUID unsentMpesa = id(payMpesa(c.token(), support.accepted(c.id(), insurer), "851234567"));
        UUID freshReference = id(payReference(c.token(), support.accepted(c.id(), insurer)));
        await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> {
            assertThat(support.intentRow(pendingMpesa).get("status")).isEqualTo("PENDING");
            assertThat(support.intentRow(unsentMpesa).get("status")).isEqualTo("PENDING");
        });
        // uma intenção M-Pesa que ficou por enviar
        support.owner().update("update payment.payment_intent set status = 'CREATED', dispatched_at = null "
                + "where id = ?", unsentMpesa);
        pastDue(reference);
        pastDue(pendingMpesa);
        pastDue(unsentMpesa);

        int expired = job.expire();

        assertThat(expired).isGreaterThanOrEqualTo(2);
        assertThat(support.intentRow(reference)).containsEntry("status", "EXPIRED")
                .containsEntry("failure_code", "EXPIRED");
        assertThat(support.referenceStatus(reference)).isEqualTo("EXPIRED");
        assertThat(support.intentRow(unsentMpesa)).containsEntry("status", "EXPIRED");
        assertThat(support.intentRow(pendingMpesa)).containsEntry("status", "PENDING");
        assertThat(support.intentRow(freshReference)).containsEntry("status", "PENDING");
        assertThat(events.of(PaymentFailed.class, e -> e.intentId().equals(reference))).singleElement()
                .satisfies(e -> {
                    assertThat(e.status()).isEqualTo(PaymentStatus.EXPIRED);
                    assertThat(e.failureCode()).isEqualTo("EXPIRED");
                    assertThat(e.targetType().name()).isEqualTo("PROPOSAL");
                });
        assertThat(support.owner().queryForObject("""
                select actor_id from audit.audit_event
                 where action = 'PAYMENT_INTENT.STATUS_CHANGE' and entity_id = ? order by occurred_at desc limit 1""",
                String.class, reference.toString())).isEqualTo("PaymentExpiryJob");

        // segunda execução: nada de novo
        job.expire();
        assertThat(events.of(PaymentFailed.class, e -> e.intentId().equals(reference))).hasSize(1);
    }
}
