package mz.co.moseguros.notification.application;

import static mz.co.moseguros.notification.NotificationTestSupport.await;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Duration;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import mz.co.moseguros.SecurityTestConfiguration;
import mz.co.moseguros.SecurityTestConfiguration.IdentityFixtures;
import mz.co.moseguros.TestcontainersConfiguration;
import mz.co.moseguros.identity.IdentityApi;
import mz.co.moseguros.notification.FakeOtpDeliveryChannels;
import mz.co.moseguros.notification.NotificationApi;
import mz.co.moseguros.notification.NotificationCategory;
import mz.co.moseguros.notification.NotificationTestSupport;
import mz.co.moseguros.notification.NotificationTestSupport.Client;
import mz.co.moseguros.shared.domain.Money;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * {@code NotificationApi.notify} (S6-04 §4): linha {@code INBOX} e a do primeiro canal, envio depois do commit
 * em menos de 5 s, rollback de quem chama, parâmetros em falta, conta desactivada e categorias obrigatórias.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestcontainersConfiguration.class, SecurityTestConfiguration.class})
@ActiveProfiles("test")
class NotifyFlowIT {

    @Autowired
    NotificationApi notifications;

    @Autowired
    FakeOtpDeliveryChannels channels;

    @Autowired
    IdentityFixtures fixtures;

    @Autowired
    IdentityApi identityApi;

    @Autowired
    TransactionTemplate tx;

    NotificationTestSupport support;

    @BeforeEach
    void setUp() {
        support = new NotificationTestSupport(fixtures, identityApi);
    }

    @AfterEach
    void tearDown() {
        channels.reset();
    }

    @Test
    void createsTheInboxAndTheFirstChannelAndSendsAfterTheCommit() {
        Client c = support.customer();
        UUID proposalId = UUID.randomUUID();
        Instant before = Instant.now();
        UUID id = tx.execute(status -> notifications.notify(c.id(), "PROPOSAL_ACCEPTED", NotificationTestSupport.accepted(proposalId),
                NotificationCategory.PROPOSAL));

        Map<String, Object> inbox = support.inbox(id);
        assertThat(inbox).containsEntry("status", "DELIVERED").containsEntry("template_code", "PROPOSAL_ACCEPTED")
                .containsEntry("category", "PROPOSAL").containsEntry("locale", "pt")
                .containsEntry("title", "Proposta aceite");
        assertThat((String) inbox.get("body")).isEqualTo("A Seguradora Exemplo aceitou a proposta PR-2027-000999. "
                + "Pague 4 663,00 MT para emitir a apólice.");
        assertThat(inbox.get("data").toString()).contains("moseguros://proposal/" + proposalId)
                .contains("/cliente/propostas/" + proposalId);
        assertThat(inbox.get("params").toString()).contains("/cliente/propostas/" + proposalId)
                .contains("\"siteUrl\"");

        List<Map<String, Object>> rows = support.external(id);
        assertThat(rows).hasSize(1);
        assertThat(rows.get(0)).containsEntry("channel", "WHATSAPP");

        await(() -> !channels.sentTo(c.phone()).isEmpty());
        assertThat(Duration.between(before, Instant.now())).isLessThan(Duration.ofSeconds(5));
        FakeOtpDeliveryChannels.Sent sent = channels.lastTo(c.phone());
        assertThat(sent.channel()).hasToString("WHATSAPP");
        assertThat(sent.params()).containsEntry("template", "moseguros_proposal_accepted")
                .containsEntry("p1", "PR-2027-000999").containsEntry("p2", "Seguradora Exemplo")
                .containsEntry("p3", "4 663,00 MT");
        await(() -> "SENT".equals(support.external(id).get(0).get("status")));
        Map<String, Object> row = support.external(id).get(0);
        assertThat(row).containsEntry("provider", "mock-whatsapp").containsEntry("attempts", 1);
        assertThat((String) row.get("destination_masked")).doesNotContain(c.phone().substring(4, 9));
        assertThat(row.get("provider_message_id")).isEqualTo(sent.providerMessageId());
    }

    @Test
    void rollbackOfTheCallerLeavesNothing() throws Exception {
        Client c = support.customer();
        UUID[] id = new UUID[1];
        tx.executeWithoutResult(status -> {
            id[0] = notifications.notify(c.id(), "PROPOSAL_EXPIRED", expired(), NotificationCategory.PROPOSAL);
            status.setRollbackOnly();
        });
        Thread.sleep(500);
        assertThat(support.all(id[0])).isEmpty();
        assertThat(channels.sentTo(c.phone())).isEmpty();
    }

    @Test
    void missingParameterOrUnknownCodeIsAProgrammingError() {
        Client c = support.customer();
        assertThatThrownBy(() -> notifications.notify(c.id(), "PROPOSAL_ACCEPTED",
                Map.of("proposalNumber", "PR-1", "insurerName", "X"), NotificationCategory.PROPOSAL))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("amount");
        assertThatThrownBy(() -> notifications.notify(c.id(), "NO_SUCH_TEMPLATE", Map.of(),
                NotificationCategory.PROPOSAL)).isInstanceOf(IllegalArgumentException.class);
        assertThat(support.byUser(c.id(), "PROPOSAL_ACCEPTED")).isEmpty();
    }

    @Test
    void disabledAccountRecordsNothing() throws Exception {
        Client c = support.customer();
        fixtures.setAccountStatus(c.id(), "DISABLED");
        UUID id = tx.execute(status -> notifications.notify(c.id(), "PROPOSAL_EXPIRED", expired(),
                NotificationCategory.PROPOSAL));
        assertThat(id).isNotNull();
        Thread.sleep(300);
        assertThat(support.all(id)).isEmpty();
        assertThat(channels.sentTo(c.phone())).isEmpty();
    }

    @Test
    void mandatoryCategorySendsPushAndAnExternalChannelInTheCustomersLanguage() {
        Client c = support.customer();
        support.language(c.id(), "en");
        String email = support.verifiedEmail(c.id());
        support.unverifyPhone(c.id());
        UUID device = support.device(c.id(), "token-" + UUID.randomUUID());
        UUID intentId = UUID.randomUUID();
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("paymentReference", "PG-2027-0000001");
        params.put("amount", Money.ofCentavos(299_900));
        params.put("insurerName", "Insurer X");
        params.put("webPath", "/cliente/pagamentos/" + intentId);
        params.put("deepLink", "moseguros://payment/" + intentId);
        UUID id = tx.execute(status -> notifications.notify(c.id(), "PAYMENT_SUCCEEDED", params,
                NotificationCategory.PAYMENT));

        assertThat(support.inbox(id)).containsEntry("locale", "en").containsEntry("title", "Payment received");
        List<Map<String, Object>> rows = support.external(id);
        assertThat(rows).extracting(r -> r.get("channel")).containsExactlyInAnyOrder("PUSH", "EMAIL");
        assertThat(rows.stream().filter(r -> "PUSH".equals(r.get("channel"))).findFirst().orElseThrow())
                .containsEntry("device_id", device);
        await(() -> !channels.sentTo(email).isEmpty()
                && channels.pushes().stream().anyMatch(p -> p.data().get("notificationId").equals(id.toString())));
        FakeOtpDeliveryChannels.Sent mail = channels.lastTo(email);
        assertThat(mail.subject()).isEqualTo("Payment received");
        assertThat(mail.text()).startsWith("Hello,\n\nWe received payment PG-2027-0000001 of MZN 2,999.00 to Insurer X.")
                .contains("/cliente/pagamentos/" + intentId).endsWith("The MoSeguros team");
        FakeOtpDeliveryChannels.Push push = channels.pushes().stream()
                .filter(p -> p.data().get("notificationId").equals(id.toString())).findFirst().orElseThrow();
        assertThat(push.title()).isEqualTo("Payment received");
        assertThat(push.data()).containsEntry("deepLink", "moseguros://payment/" + intentId);
    }

    @Test
    void mandatoryCategoryWithoutContactsOnlyHasTheInbox() {
        Client c = support.customer();
        support.unverifyPhone(c.id());
        UUID id = tx.execute(status -> notifications.notify(c.id(), "PAYMENT_FAILED",
                Map.of("paymentReference", "PG-2027-0000002", "amount", Money.ofCentavos(100)),
                NotificationCategory.PAYMENT));
        assertThat(support.all(id)).extracting(r -> r.get("channel")).containsExactly("INBOX");
        assertThat((String) support.inbox(id).get("params").toString()).contains("/cliente/notificacoes");
    }

    static Map<String, Object> expired() {
        return Map.of("proposalNumber", "PR-2027-000998");
    }
}
