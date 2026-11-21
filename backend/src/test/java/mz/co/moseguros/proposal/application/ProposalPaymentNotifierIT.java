package mz.co.moseguros.proposal.application;

import static mz.co.moseguros.payment.PaymentFlowSupport.AMOUNT;
import static mz.co.moseguros.payment.PaymentFlowSupport.id;
import static mz.co.moseguros.payment.PaymentFlowSupport.inSystem;
import static mz.co.moseguros.payment.PaymentFlowSupport.payReference;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import io.restassured.RestAssured;
import mz.co.moseguros.SecurityTestConfiguration;
import mz.co.moseguros.SecurityTestConfiguration.IdentityFixtures;
import mz.co.moseguros.SecurityTestConfiguration.MutableClock;
import mz.co.moseguros.TestcontainersConfiguration;
import mz.co.moseguros.identity.IdentityApi;
import mz.co.moseguros.identity.domain.UserAccount;
import mz.co.moseguros.notification.FakeOtpDeliveryChannels;
import mz.co.moseguros.payment.PaymentEventsRecorder;
import mz.co.moseguros.payment.PaymentFlowSupport;
import mz.co.moseguros.payment.PaymentFlowSupport.Broker;
import mz.co.moseguros.payment.PaymentFlowSupport.Seg;
import mz.co.moseguros.payment.PaymentMethod;
import mz.co.moseguros.payment.PaymentPurpose;
import mz.co.moseguros.payment.PaymentSucceeded;
import mz.co.moseguros.payment.PaymentTargetType;
import mz.co.moseguros.payment.application.IntentStateService;
import mz.co.moseguros.payment.domain.ConfirmedBy;
import mz.co.moseguros.proposal.domain.ProposalRepository;
import mz.co.moseguros.rating.QuoteTestSupport.Customer;
import mz.co.moseguros.shared.domain.Money;
import mz.co.moseguros.shared.tenant.TenantContext;
import mz.co.moseguros.shared.tenant.TenantSnapshot;
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
 * Aviso de pagamento recebido (S5-02 §9.1): e-mail ao cliente (verificado; sem ele, SMS), aos
 * utilizadores activos do mediador e da seguradora, na língua de cada um, com {@code purpose = PAYMENT};
 * nada com rollback, com evento repetido ou com excepção.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestcontainersConfiguration.class, SecurityTestConfiguration.class})
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ProposalPaymentNotifierIT {

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
    FakeOtpDeliveryChannels channels;

    @Autowired
    PaymentEventsRecorder events;

    @Autowired
    ProposalPaymentNotifier notifier;

    @Autowired
    ProposalRepository proposals;

    @Autowired
    org.springframework.context.ApplicationEventPublisher publisher;

    PaymentFlowSupport support;
    Seg insurer;
    String adminEmail;
    String financeEmail;
    String underwriterEmail;
    String disabledFinanceEmail;

    @BeforeAll
    void setUpAll() throws Exception {
        RestAssured.port = port;
        support = new PaymentFlowSupport(fixtures, identityApi, clock);
        support.createTemplate();
        insurer = support.insurer("Seguradora Avisos");
        support.enableBoth(insurer.id());
        UserAccount admin = fixtures.staff("Gestor Avisos", insurer.id(), "INSURER_ADMIN");
        UserAccount finance = fixtures.staff("Financeiro Avisos", insurer.id(), "FINANCE");
        UserAccount underwriter = fixtures.staff("Subscritor Avisos", insurer.id(), "UNDERWRITER");
        UserAccount disabled = fixtures.staff("Financeiro Inactivo", insurer.id(), "FINANCE");
        fixtures.setAccountStatus(disabled.getId(), "DISABLED");
        support.preferredLanguage(finance.getId(), "en");
        adminEmail = admin.getEmail();
        financeEmail = finance.getEmail();
        underwriterEmail = underwriter.getEmail();
        disabledFinanceEmail = disabled.getEmail();
    }

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        clock.reset();
        channels.reset();
    }

    UUID pay(Customer c, UUID proposal) {
        UUID intentId = id(payReference(c.token(), proposal));
        inSystem(tx, () -> states.markSucceeded(intentId, null, ConfirmedBy.REFERENCE_FILE, null, "test"));
        await().atMost(Duration.ofSeconds(10)).untilAsserted(() ->
                assertThat(support.proposalRow(proposal).get("status")).isIn("PAID", "CONVERTED")); // S6-01
        return intentId;
    }

    String subject(FakeOtpDeliveryChannels.Sent sent) {
        return sent.subject();
    }

    @Test
    void brokerProposalPaidNotifiesTheCustomerTheBrokerAndTheInsurer() {
        Broker broker = support.broker("Corretora Avisos");
        UserAccount otherBroker = fixtures.staff("Agente Avisos", broker.id(), "AGENT");
        UserAccount disabledBroker = fixtures.staff("Mediador Inactivo", broker.id(), "BROKER");
        fixtures.setAccountStatus(disabledBroker.getId(), "DISABLED");
        Customer c = support.customer();
        String customerEmail = support.verifiedEmail(c.id());
        UUID proposal = support.proposal(c.id(), insurer, "ACCEPTED", clock.instant().plus(Duration.ofDays(15)),
                AMOUNT, broker);
        String number = support.owner().queryForObject("select number from proposal.proposal where id = ?",
                String.class, proposal);

        UUID intentId = pay(c, proposal);

        await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> {
            assertThat(channels.sentTo(customerEmail)).hasSize(1);
            assertThat(channels.sentTo(broker.user().getEmail())).hasSize(1);
            assertThat(channels.sentTo(otherBroker.getEmail())).hasSize(1);
            assertThat(channels.sentTo(adminEmail)).hasSize(1);
            assertThat(channels.sentTo(financeEmail)).hasSize(1);
            assertThat(messagesFor(intentId)).isEqualTo(7);
        });
        FakeOtpDeliveryChannels.Sent toCustomer = channels.lastTo(customerEmail);
        assertThat(subject(toCustomer)).isEqualTo("Pagamento recebido — proposta " + number);
        assertThat(toCustomer.text()).contains("2 999,00 MT").contains(insurer.name())
                .contains("Entidade e referência");
        assertThat(channels.lastTo(broker.user().getEmail()).text()).contains(number).contains("Cliente Pagador");
        assertThat(channels.lastTo(adminEmail).text()).contains("/seguradora/pagamentos/" + intentId);
        FakeOtpDeliveryChannels.Sent toFinance = channels.lastTo(financeEmail);
        assertThat(subject(toFinance)).isEqualTo("Payment received — proposal " + number);
        assertThat(toFinance.text()).contains("MZN 2,999.00").contains("Entity and reference");
        assertThat(channels.sentTo(underwriterEmail)).isEmpty();
        assertThat(channels.sentTo(disabledFinanceEmail)).isEmpty();
        assertThat(channels.sentTo(disabledBroker.getEmail())).isEmpty();
        // S6-04: o WhatsApp das notificações (PAYMENT_REFERENCE_GENERATED, PAYMENT_SUCCEEDED) vai para o telemóvel
        assertThat(channels.sentTo(c.account().getMsisdn().e164())).as("e-mail, not SMS")
                .noneMatch(s -> s.channel() == mz.co.moseguros.shared.domain.Channel.SMS);

        List<String> purposes = support.owner().queryForList("""
                select purpose from notification.outbound_message where correlation_id = ?""", String.class, intentId);
        assertThat(purposes).hasSize(7).containsOnly("PAYMENT"); // cliente, 2 do mediador, 4 da seguradora

        // o mesmo evento outra vez: não envia de novo
        PaymentSucceeded original = events.of(PaymentSucceeded.class, e -> e.intentId().equals(intentId)).get(0);
        inSystem(tx, () -> {
            publisher.publishEvent(new PaymentSucceeded(UUID.randomUUID(), Instant.now(), original.intentId(),
                    original.reference(), original.insurerId(), original.payerUserId(), original.targetType(),
                    original.targetId(), original.purpose(), original.method(), original.amount(), original.paidAt()));
            return null;
        });
        await().during(Duration.ofSeconds(2)).atMost(Duration.ofSeconds(5)).untilAsserted(() ->
                assertThat(support.owner().queryForObject("select count(*) from notification.outbound_message "
                        + "where correlation_id = ?", Integer.class, intentId)).isEqualTo(7));
    }

    @Test
    void customerWithoutVerifiedEmailGetsAnSmsAndNoBrokerIsNotified() {
        Customer c = support.customer();
        UUID proposal = support.accepted(c.id(), insurer);

        UUID intentId = pay(c, proposal);

        String msisdn = c.account().getMsisdn().e164();
        await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> assertThat(smsTo(msisdn)).hasSize(1)); // S6-04: só o SMS (as notificações vão por WhatsApp)
        assertThat(smsTo(msisdn).get(0).text()).startsWith("MoSeguros: recebemos o pagamento de 2 999,00 MT");
        await().atMost(Duration.ofSeconds(5)).untilAsserted(() -> assertThat(support.owner().queryForList("""
                select channel from notification.outbound_message where correlation_id = ?""", String.class, intentId))
                .containsExactlyInAnyOrder("SMS", "EMAIL", "EMAIL", "EMAIL", "EMAIL"));
    }

    @Test
    void nothingIsSentWhenTheTransactionRollsBack() {
        Customer c = support.customer();
        String email = support.verifiedEmail(c.id());
        UUID proposal = support.accepted(c.id(), insurer);
        PaymentSucceeded event = new PaymentSucceeded(UUID.randomUUID(), Instant.now(), UUID.randomUUID(),
                "PG-2026-9999999", insurer.id(), c.id(), PaymentTargetType.PROPOSAL, proposal,
                PaymentPurpose.NEW_POLICY, PaymentMethod.MPESA, Money.ofCentavos(AMOUNT), Instant.now());

        TenantSnapshot previous = TenantContext.current();
        TenantContext.set(TenantSnapshot.system(null));
        try {
            tx.executeWithoutResult(status -> {
                notifier.paid(proposals.findById(proposal).orElseThrow(), event);
                status.setRollbackOnly();
            });
        } finally {
            TenantContext.set(previous);
        }
        assertThat(channels.sentTo(email)).isEmpty();
        assertThat(messagesFor(event.intentId())).isZero();
    }

    List<FakeOtpDeliveryChannels.Sent> smsTo(String msisdn) {
        return channels.sentTo(msisdn).stream().filter(s -> s.channel() == mz.co.moseguros.shared.domain.Channel.SMS)
                .toList();
    }

    /** Mensagens registadas com esta intenção como {@code correlationId}. */
    int messagesFor(UUID intentId) {
        return support.owner().queryForObject("select count(*) from notification.outbound_message "
                + "where correlation_id = ?", Integer.class, intentId);
    }

    @Test
    void anExceptionDoesNotNotifyAnyone() {
        Customer c = support.customer();
        String email = support.verifiedEmail(c.id());
        UUID proposal = support.accepted(c.id(), insurer);
        UUID intentId = id(payReference(c.token(), proposal));
        support.owner().update("update proposal.proposal set status = 'EXPIRED', expires_at = null where id = ?",
                proposal);
        inSystem(tx, () -> states.markSucceeded(intentId, null, ConfirmedBy.REFERENCE_FILE, null, "test"));
        await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> assertThat(support.owner().queryForObject(
                "select count(*) from payment.payment_exception where intent_id = ?", Integer.class, intentId))
                .isEqualTo(1));
        await().during(Duration.ofSeconds(1)).atMost(Duration.ofSeconds(3)).untilAsserted(() -> {
            assertThat(channels.sentTo(email)).isEmpty();
            assertThat(messagesFor(intentId)).isZero();
        });
    }
}
