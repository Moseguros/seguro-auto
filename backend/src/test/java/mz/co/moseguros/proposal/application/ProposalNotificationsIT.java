package mz.co.moseguros.proposal.application;

import static mz.co.moseguros.payment.PaymentFlowSupport.inSystem;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import io.restassured.RestAssured;
import mz.co.moseguros.SecurityTestConfiguration;
import mz.co.moseguros.SecurityTestConfiguration.IdentityFixtures;
import mz.co.moseguros.SecurityTestConfiguration.MutableClock;
import mz.co.moseguros.TestcontainersConfiguration;
import mz.co.moseguros.identity.IdentityApi;
import mz.co.moseguros.notification.FakeOtpDeliveryChannels;
import mz.co.moseguros.notification.NotificationTestSupport;
import mz.co.moseguros.payment.PaymentFlowSupport;
import mz.co.moseguros.payment.PaymentFlowSupport.Seg;
import mz.co.moseguros.proposal.AcceptanceMode;
import mz.co.moseguros.proposal.ProposalAccepted;
import mz.co.moseguros.proposal.ProposalExpired;
import mz.co.moseguros.proposal.ProposalInfoRequested;
import mz.co.moseguros.proposal.ProposalRejected;
import mz.co.moseguros.proposal.ProposalStatus;
import mz.co.moseguros.rating.QuoteTestSupport.Customer;
import mz.co.moseguros.shared.domain.Channel;
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
 * Avisos das decisões da proposta (S6-04 §9): cada evento gera a notificação certa para o cliente da proposta,
 * com o {@code webPath} e o {@code deepLink}; o e-mail {@code proposal.accepted} do S4-05 já não sai.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestcontainersConfiguration.class, SecurityTestConfiguration.class})
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ProposalNotificationsIT {

    @LocalServerPort
    int port;

    @Autowired
    IdentityFixtures fixtures;

    @Autowired
    IdentityApi identityApi;

    @Autowired
    MutableClock clock;

    @Autowired
    TransactionTemplate tx;

    @Autowired
    FakeOtpDeliveryChannels channels;

    @Autowired
    ApplicationEventPublisher publisher;

    PaymentFlowSupport support;
    NotificationTestSupport notifications;
    Seg insurer;

    @BeforeAll
    void setUpAll() throws Exception {
        RestAssured.port = port;
        support = new PaymentFlowSupport(fixtures, identityApi, clock);
        support.createTemplate();
        notifications = new NotificationTestSupport(fixtures, identityApi);
        insurer = support.insurer("Seguradora Avisos Proposta");
    }

    @BeforeEach
    void setUp() {
        channels.reset();
    }

    @Test
    void acceptedRejectedInfoRequestedAndExpiredNotifyTheCustomer() {
        check("PROPOSAL_ACCEPTED", id -> new ProposalAccepted(UUID.randomUUID(), Instant.now(), id, insurer.id(),
                customerOf(id), null, AcceptanceMode.MANUAL), "MoSeguros: a proposta %s foi aceite pela %s. Pague "
                + "2 999,00 MT na app ou em ");
        check("PROPOSAL_REJECTED", id -> new ProposalRejected(UUID.randomUUID(), Instant.now(), id, insurer.id(),
                customerOf(id), null, "RISK_NOT_ACCEPTED"), "MoSeguros: a %2$s recusou a proposta %1$s.");
        check("PROPOSAL_INFO_REQUESTED", id -> new ProposalInfoRequested(UUID.randomUUID(), Instant.now(), id,
                insurer.id(), customerOf(id), null, List.of("INSPECTION")),
                "MoSeguros: a %2$s pede mais informação sobre a proposta %1$s.");
        check("PROPOSAL_EXPIRED", id -> new ProposalExpired(UUID.randomUUID(), Instant.now(), id, insurer.id(),
                customerOf(id), null, ProposalStatus.ACCEPTED), "MoSeguros: a proposta %s expirou.");
    }

    @Test
    void theAcceptedEmailOfS405IsNoLongerSent() {
        Customer c = support.customer();
        String email = support.verifiedEmail(c.id());
        UUID proposal = support.accepted(c.id(), insurer);
        publish(new ProposalAccepted(UUID.randomUUID(), Instant.now(), proposal, insurer.id(), c.id(), null,
                AcceptanceMode.AUTOMATIC));
        await().atMost(Duration.ofSeconds(10)).untilAsserted(() ->
                assertThat(notifications.byUser(c.id(), "PROPOSAL_ACCEPTED")).hasSize(2)); // inbox + WhatsApp
        await().during(Duration.ofSeconds(1)).atMost(Duration.ofSeconds(3)).untilAsserted(() ->
                assertThat(channels.sentTo(email)).isEmpty());
        assertThat(support.owner().queryForObject("""
                select count(*) from notification.outbound_message where correlation_id = ? and purpose = 'PROPOSAL'""",
                Integer.class, proposal)).isZero();
    }

    private void check(String code, Function<UUID, Object> event, String smsStart) {
        Customer c = support.customer();
        UUID proposal = support.accepted(c.id(), insurer);
        String number = support.owner().queryForObject("select number from proposal.proposal where id = ?",
                String.class, proposal);
        publish(event.apply(proposal));

        await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> {
            List<Map<String, Object>> rows = notifications.byUser(c.id(), code);
            assertThat(rows).extracting(r -> r.get("channel")).containsExactlyInAnyOrder("INBOX", "WHATSAPP");
            assertThat(rows).filteredOn(r -> "WHATSAPP".equals(r.get("channel"))).extracting(r -> r.get("status"))
                    .containsExactly("SENT");
        });
        Map<String, Object> inbox = notifications.byUser(c.id(), code).stream()
                .filter(r -> "INBOX".equals(r.get("channel"))).findFirst().orElseThrow();
        assertThat(inbox).containsEntry("category", "PROPOSAL");
        assertThat(inbox.get("data").toString()).contains("moseguros://proposal/" + proposal)
                .contains("/cliente/propostas/" + proposal);
        assertThat((String) inbox.get("body")).contains(number);
        FakeOtpDeliveryChannels.Sent sent = channels.lastTo(c.account().getMsisdn().e164());
        assertThat(sent.channel()).isEqualTo(Channel.WHATSAPP);
        assertThat(sent.text()).startsWith(String.format(smsStart, number, insurer.name()));
        assertThat(sent.params()).containsEntry("template", "moseguros_" + code.toLowerCase()
                .replace("proposal_", "proposal_")).containsEntry("p1", number);
    }

    private UUID customerOf(UUID proposalId) {
        return support.owner().queryForObject("select customer_user_id from proposal.proposal where id = ?",
                UUID.class, proposalId);
    }

    private void publish(Object event) {
        inSystem(tx, () -> {
            publisher.publishEvent(event);
            return null;
        });
    }
}
