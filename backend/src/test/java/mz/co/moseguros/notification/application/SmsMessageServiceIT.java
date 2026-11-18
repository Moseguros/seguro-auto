package mz.co.moseguros.notification.application;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import java.util.UUID;

import mz.co.moseguros.SecurityTestConfiguration;
import mz.co.moseguros.SecurityTestConfiguration.IdentityFixtures;
import mz.co.moseguros.TestcontainersConfiguration;
import mz.co.moseguros.notification.DeliveryResult;
import mz.co.moseguros.notification.DeliveryStatus;
import mz.co.moseguros.notification.FakeOtpDeliveryChannels;
import mz.co.moseguros.notification.NotificationApi;
import mz.co.moseguros.shared.domain.Channel;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

/**
 * {@code NotificationApi.sendSms} (S5-02 §10): grava {@code outbound_message} com o {@code purpose}, o
 * canal SMS e o destino mascarado; o texto vem do modelo; uma falha do fornecedor dá {@code FAILED} sem
 * excepção.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestcontainersConfiguration.class, SecurityTestConfiguration.class})
@ActiveProfiles("test")
class SmsMessageServiceIT {

    @Autowired
    NotificationApi notifications;

    @Autowired
    FakeOtpDeliveryChannels channels;

    @Autowired
    IdentityFixtures fixtures;

    @AfterEach
    void tearDown() {
        channels.reset();
    }

    @Test
    void sendSmsRecordsTheMessageWithThePurposeAndAMaskedDestination() {
        UUID correlation = UUID.randomUUID();
        DeliveryResult result = notifications.sendSms("+258841234567", "proposal.paid.sms",
                new Object[] {"2 999,00 MT", "PR-2027-000123", "Seguradora Demo"}, "pt", "PAYMENT", correlation);

        assertThat(result.status()).isEqualTo(DeliveryStatus.SENT);
        FakeOtpDeliveryChannels.Sent sent = channels.lastTo("+258841234567");
        assertThat(sent.channel()).isEqualTo(Channel.SMS);
        assertThat(sent.text()).isEqualTo("MoSeguros: recebemos o pagamento de 2 999,00 MT da proposta "
                + "PR-2027-000123 (Seguradora Demo). A apólice vai ser emitida em breve.");
        Map<String, Object> row = fixtures.owner().queryForMap("""
                select channel, purpose, destination_masked, status from notification.outbound_message
                 where correlation_id = ?""", correlation);
        assertThat(row).containsEntry("channel", "SMS").containsEntry("purpose", "PAYMENT")
                .containsEntry("status", "SENT");
        assertThat((String) row.get("destination_masked")).doesNotContain("841234567").endsWith("4567");

        notifications.sendSms("+258841234567", "proposal.paid.sms", new Object[] {"MZN 2,999.00", "PR-1", "X"}, "en",
                "PAYMENT", UUID.randomUUID());
        assertThat(channels.lastTo("+258841234567").text()).startsWith("MoSeguros: we received the payment of");
    }

    @Test
    void providerFailureIsRecordedWithoutAnException() {
        channels.mode(Channel.SMS, FakeOtpDeliveryChannels.Mode.FAIL);
        UUID correlation = UUID.randomUUID();
        DeliveryResult result = notifications.sendSms("+258851234567", "proposal.paid.sms",
                new Object[] {"1", "2", "3"}, "pt", "PAYMENT", correlation);

        assertThat(result.status()).isEqualTo(DeliveryStatus.FAILED);
        assertThat(result.failureReason()).isNotBlank();
        assertThat(fixtures.owner().queryForObject("select status from notification.outbound_message "
                + "where correlation_id = ?", String.class, correlation)).isEqualTo("FAILED");
        assertThat(channels.sentTo("+258851234567")).isEmpty();
    }
}
