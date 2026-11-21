package mz.co.moseguros.payment.application;

import static mz.co.moseguros.payment.PaymentFlowSupport.id;
import static mz.co.moseguros.payment.PaymentFlowSupport.inSystem;
import static mz.co.moseguros.payment.PaymentFlowSupport.payReference;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
import mz.co.moseguros.payment.domain.ConfirmedBy;
import mz.co.moseguros.rating.QuoteTestSupport.Customer;
import mz.co.moseguros.shared.domain.Channel;
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
 * Avisos de pagamento (S6-04 §9): {@code ReferenceGenerated} → {@code PAYMENT_REFERENCE_GENERATED},
 * {@code PaymentSucceeded} → {@code PAYMENT_SUCCEEDED} além dos avisos do S5-02 (D36) e {@code PaymentFailed}
 * com {@code EXPIRED} → {@code PAYMENT_FAILED}, para o pagador, com {@code webPath} e {@code deepLink}.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestcontainersConfiguration.class, SecurityTestConfiguration.class})
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PaymentNotificationsIT {

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

    PaymentFlowSupport support;
    NotificationTestSupport notifications;
    Seg insurer;

    @BeforeAll
    void setUpAll() throws Exception {
        RestAssured.port = port;
        support = new PaymentFlowSupport(fixtures, identityApi, clock);
        support.createTemplate();
        notifications = new NotificationTestSupport(fixtures, identityApi);
        insurer = support.insurer("Seguradora Avisos Pagamento");
        support.enableBoth(insurer.id());
    }

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        clock.reset();
        channels.reset();
    }

    @Test
    void referenceAndPaymentReceivedNotifyThePayerAndTheD36NoticesStay() {
        Customer c = support.customer();
        String msisdn = c.account().getMsisdn().e164();
        UUID proposal = support.accepted(c.id(), insurer);
        UUID intentId = id(payReference(c.token(), proposal));

        Map<String, Object> reference = awaitInbox(c.id(), "PAYMENT_REFERENCE_GENERATED", intentId);
        Map<String, Object> ref = support.owner().queryForMap(
                "select entity, reference from payment.reference_payment where intent_id = ?", intentId);
        assertThat((String) reference.get("body")).contains("Entidade " + ref.get("entity"))
                .contains("referência " + ref.get("reference")).contains("2 999,00 MT");
        assertThat(reference).containsEntry("category", "PAYMENT");

        inSystem(tx, () -> states.markSucceeded(intentId, null, ConfirmedBy.REFERENCE_FILE, null, "test"));
        Map<String, Object> succeeded = awaitInbox(c.id(), "PAYMENT_SUCCEEDED", intentId);
        String number = support.owner().queryForObject("select reference from payment.payment_intent where id = ?",
                String.class, intentId);
        assertThat((String) succeeded.get("body")).isEqualTo("Recebemos o pagamento " + number
                + " de 2 999,00 MT.");

        // WhatsApp das duas notificações e o SMS proposal.paid.sms do S5-02 (D36: sem e-mail verificado)
        await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> {
            List<FakeOtpDeliveryChannels.Sent> sent = channels.sentTo(msisdn);
            assertThat(sent).filteredOn(s -> s.channel() == Channel.WHATSAPP).extracting(s -> s.params().get("template"))
                    .containsExactlyInAnyOrder("moseguros_payment_reference", "moseguros_payment_succeeded");
            assertThat(sent).filteredOn(s -> s.channel() == Channel.SMS).extracting(FakeOtpDeliveryChannels.Sent::text)
                    .anyMatch(t -> t.startsWith("MoSeguros: recebemos o pagamento de 2 999,00 MT"));
        });
        FakeOtpDeliveryChannels.Sent whatsapp = channels.sentTo(msisdn).stream()
                .filter(s -> "moseguros_payment_succeeded".equals(s.params().get("template"))).findFirst().orElseThrow();
        assertThat(whatsapp.params()).containsEntry("p1", number).containsEntry("p2", "2 999,00 MT")
                .containsEntry("p3", insurer.name());
        await().atMost(Duration.ofSeconds(5)).untilAsserted(() -> assertThat(support.owner().queryForList(
                "select channel from notification.outbound_message where correlation_id = ? and purpose = 'PAYMENT'",
                String.class, intentId)).contains("SMS", "EMAIL"));
    }

    @Test
    void expiredReferenceNotifiesPaymentFailed() {
        Customer c = support.customer();
        String email = support.verifiedEmail(c.id());
        notifications.unverifyPhone(c.id());
        UUID proposal = support.accepted(c.id(), insurer);
        UUID intentId = id(payReference(c.token(), proposal));
        awaitInbox(c.id(), "PAYMENT_REFERENCE_GENERATED", intentId);

        inSystem(tx, () -> states.expire(intentId, "test"));

        Map<String, Object> failed = awaitInbox(c.id(), "PAYMENT_FAILED", intentId);
        String number = support.owner().queryForObject("select reference from payment.payment_intent where id = ?",
                String.class, intentId);
        assertThat((String) failed.get("body")).isEqualTo("O pagamento " + number
                + " de 2 999,00 MT não foi concluído. Pode tentar de novo.");
        await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> assertThat(channels.sentTo(email))
                .extracting(FakeOtpDeliveryChannels.Sent::subject).contains("Pagamento não concluído"));
        FakeOtpDeliveryChannels.Sent mail = channels.sentTo(email).stream()
                .filter(s -> "Pagamento não concluído".equals(s.subject())).findFirst().orElseThrow();
        assertThat(mail.text()).contains("/cliente/pagamentos/" + intentId);
    }

    private Map<String, Object> awaitInbox(UUID userId, String code, UUID intentId) {
        await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> assertThat(inbox(userId, code)).isNotNull());
        Map<String, Object> inbox = inbox(userId, code);
        assertThat(inbox.get("data").toString()).contains("moseguros://payment/" + intentId)
                .contains("/cliente/pagamentos/" + intentId);
        return inbox;
    }

    private Map<String, Object> inbox(UUID userId, String code) {
        return notifications.byUser(userId, code).stream().filter(r -> "INBOX".equals(r.get("channel"))).findFirst()
                .orElse(null);
    }
}
