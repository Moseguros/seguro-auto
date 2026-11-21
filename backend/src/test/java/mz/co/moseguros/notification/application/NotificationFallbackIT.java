package mz.co.moseguros.notification.application;

import static mz.co.moseguros.notification.NotificationTestSupport.await;
import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import mz.co.moseguros.SecurityTestConfiguration;
import mz.co.moseguros.SecurityTestConfiguration.IdentityFixtures;
import mz.co.moseguros.SecurityTestConfiguration.MutableClock;
import mz.co.moseguros.TestcontainersConfiguration;
import mz.co.moseguros.identity.IdentityApi;
import mz.co.moseguros.notification.FakeOtpDeliveryChannels;
import mz.co.moseguros.notification.FakeOtpDeliveryChannels.Mode;
import mz.co.moseguros.notification.NotificationApi;
import mz.co.moseguros.notification.NotificationCategory;
import mz.co.moseguros.notification.NotificationTestSupport;
import mz.co.moseguros.notification.NotificationTestSupport.Client;
import mz.co.moseguros.shared.domain.Channel;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Tentativas e alternativa (S6-04 §4): 3 tentativas aos 0 s, 30 s e 2 min com falha temporária, alternativa
 * imediata com falha definitiva, recibo {@code FAILED} até 10 min, token push inválido e linhas presas em
 * {@code SENDING}.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestcontainersConfiguration.class, SecurityTestConfiguration.class})
@ActiveProfiles("test")
class NotificationFallbackIT {

    @Autowired
    NotificationApi notifications;

    @Autowired
    NotificationDispatchJob job;

    @Autowired
    DeliveryReceiptService receipts;

    @Autowired
    FakeOtpDeliveryChannels channels;

    @Autowired
    IdentityFixtures fixtures;

    @Autowired
    IdentityApi identityApi;

    @Autowired
    MutableClock clock;

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
        clock.reset();
    }

    @Test
    void temporaryFailuresAreRetriedThreeTimesThenTheNextChannelIsUsed() {
        Client c = support.customer();
        String email = support.verifiedEmail(c.id());
        support.preference(c.id(), "PROPOSAL", "WHATSAPP", false);
        channels.mode(Channel.SMS, Mode.FAIL);
        UUID id = notify(c);

        await(() -> attempts(id, "SMS") == 1 && "QUEUED".equals(row(id, "SMS").get("status")));
        Instant next = ((Timestamp) row(id, "SMS").get("next_attempt_at")).toInstant();
        assertThat(Duration.between(clock.instant(), next)).isBetween(Duration.ofSeconds(25), Duration.ofSeconds(31));
        assertThat(row(id, "SMS")).containsEntry("last_error", "HTTP 503");

        job.run(); // ainda não está na hora
        assertThat(attempts(id, "SMS")).isEqualTo(1);

        clock.advance(Duration.ofSeconds(31));
        job.run();
        assertThat(attempts(id, "SMS")).isEqualTo(2);
        next = ((Timestamp) row(id, "SMS").get("next_attempt_at")).toInstant();
        assertThat(Duration.between(clock.instant(), next)).isBetween(Duration.ofSeconds(115), Duration.ofSeconds(121));

        clock.advance(Duration.ofSeconds(121));
        job.run();
        assertThat(row(id, "SMS")).containsEntry("status", "FAILED").containsEntry("attempts", 3);
        await(() -> !channels.sentTo(email).isEmpty());
        assertThat(row(id, "EMAIL")).containsEntry("status", "SENT");
        assertThat(support.external(id)).extracting(r -> r.get("channel")).containsExactly("SMS", "EMAIL");
    }

    @Test
    void permanentFailureFallsBackAtOnce() {
        Client c = support.customer();
        channels.mode(Channel.WHATSAPP, Mode.FAIL_PERMANENT);
        UUID id = notify(c);

        await(() -> support.external(id).size() == 2 && "SENT".equals(row(id, "SMS").get("status")));
        assertThat(row(id, "WHATSAPP")).containsEntry("status", "FAILED").containsEntry("attempts", 1);
        assertThat(channels.lastTo(c.phone()).channel()).isEqualTo(Channel.SMS);
        assertThat(channels.lastTo(c.phone()).text()).startsWith("MoSeguros: a proposta PR-2027-000998 expirou");
    }

    @Test
    void failedReceiptWithinTenMinutesFallsBack() {
        Client c = support.customer();
        UUID id = notify(c);
        await(() -> "SENT".equals(row(id, "WHATSAPP").get("status")));
        String providerId = (String) row(id, "WHATSAPP").get("provider_message_id");

        receipts.receive("mock-whatsapp", "test-webhook-secret", providerId,
                DeliveryReceiptService.ReceiptStatus.FAILED, "no route");

        assertThat(row(id, "WHATSAPP")).containsEntry("status", "FAILED").containsEntry("last_error", "receipt: no route");
        await(() -> support.external(id).size() == 2 && "SENT".equals(row(id, "SMS").get("status")));
    }

    @Test
    void deliveredReceiptMarksTheRowAndALateFailureDoesNotFallBack() {
        Client c = support.customer();
        UUID id = notify(c);
        await(() -> "SENT".equals(row(id, "WHATSAPP").get("status")));
        String providerId = (String) row(id, "WHATSAPP").get("provider_message_id");
        receipts.receive("mock-whatsapp", "test-webhook-secret", providerId,
                DeliveryReceiptService.ReceiptStatus.DELIVERED, null);
        assertThat(row(id, "WHATSAPP")).containsEntry("status", "DELIVERED");

        UUID late = notify(c);
        await(() -> "SENT".equals(row(late, "WHATSAPP").get("status")));
        clock.advance(Duration.ofMinutes(11));
        receipts.receive("mock-whatsapp", "test-webhook-secret", (String) row(late, "WHATSAPP")
                .get("provider_message_id"), DeliveryReceiptService.ReceiptStatus.FAILED, null);
        assertThat(row(late, "WHATSAPP")).containsEntry("status", "FAILED");
        assertThat(support.external(late)).hasSize(1);
    }

    @Test
    void invalidPushTokenDeactivatesTheDeviceAndFallsBack() {
        Client c = support.customer();
        UUID device = support.device(c.id(), "invalid-" + UUID.randomUUID());
        UUID id = notify(c);

        await(() -> support.external(id).size() == 2 && "SENT".equals(row(id, "WHATSAPP").get("status")));
        assertThat(row(id, "PUSH")).containsEntry("status", "FAILED").containsEntry("device_id", device);
        assertThat(support.owner().queryForMap("select active, deactivated_reason from notification.device where id = ?",
                device)).containsEntry("active", false).containsEntry("deactivated_reason", "INVALID_TOKEN");
    }

    @Test
    void rowStuckInSendingGoesBackToTheQueue() {
        Client c = support.customer();
        channels.mode(Channel.WHATSAPP, Mode.FAIL);
        UUID id = notify(c);
        await(() -> attempts(id, "WHATSAPP") == 1 && "QUEUED".equals(row(id, "WHATSAPP").get("status")));
        channels.reset();
        UUID rowId = (UUID) row(id, "WHATSAPP").get("id");
        support.owner().update("""
                update notification.message set status = 'SENDING', next_attempt_at = null,
                       updated_at = now() - interval '3 minutes' where id = ?""", rowId);

        job.run();

        assertThat(row(id, "WHATSAPP")).containsEntry("status", "SENT").containsEntry("attempts", 2);
    }

    private UUID notify(Client c) {
        return tx.execute(status -> notifications.notify(c.id(), "PROPOSAL_EXPIRED",
                Map.of("proposalNumber", "PR-2027-000998"), NotificationCategory.PROPOSAL));
    }

    private Map<String, Object> row(UUID notificationId, String channel) {
        List<Map<String, Object>> rows = support.external(notificationId).stream()
                .filter(r -> channel.equals(r.get("channel"))).toList();
        if (rows.isEmpty()) {
            return Map.of();
        }
        return rows.get(rows.size() - 1);
    }

    private int attempts(UUID notificationId, String channel) {
        Object attempts = row(notificationId, channel).get("attempts");
        return attempts == null ? 0 : ((Number) attempts).intValue();
    }
}
