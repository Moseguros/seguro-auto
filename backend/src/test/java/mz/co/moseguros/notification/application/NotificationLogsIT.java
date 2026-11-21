package mz.co.moseguros.notification.application;

import static mz.co.moseguros.notification.NotificationTestSupport.await;
import static org.assertj.core.api.Assertions.assertThat;

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
import mz.co.moseguros.shared.domain.Channel;
import mz.co.moseguros.shared.logging.SensitiveDataMasker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Logs das notificações (S6-04, critérios de aceitação): nenhum tem o token push, o telemóvel completo nem o
 * e-mail completo, também com falhas e alternativas; o {@code SensitiveDataMasker} tapa {@code "token":"…"}.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestcontainersConfiguration.class, SecurityTestConfiguration.class})
@ActiveProfiles("test")
@ExtendWith(OutputCaptureExtension.class)
class NotificationLogsIT {

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

    @AfterEach
    void tearDown() {
        channels.reset();
    }

    @Test
    void logsNeverHaveTheTokenPhoneOrEmail(CapturedOutput output) {
        NotificationTestSupport support = new NotificationTestSupport(fixtures, identityApi);
        Client c = support.customer();
        String email = support.verifiedEmail(c.id());
        String token = "secret-push-token-" + UUID.randomUUID();
        String invalidToken = "invalid-" + UUID.randomUUID();
        support.device(c.id(), token);
        support.device(c.id(), invalidToken);
        channels.mode(Channel.WHATSAPP, FakeOtpDeliveryChannels.Mode.FAIL_PERMANENT);

        UUID id = tx.execute(s -> notifications.notify(c.id(), "PAYMENT_FAILED",
                Map.of("paymentReference", "PG-2027-0000003", "amount", mz.co.moseguros.shared.domain.Money.ofCentavos(1)),
                NotificationCategory.PAYMENT));
        await(() -> support.external(id).stream().filter(r -> "SENT".equals(r.get("status"))).count() == 2);

        String logs = output.getAll();
        assertThat(logs).contains(id.toString());
        assertThat(logs).doesNotContain(token).doesNotContain(invalidToken).doesNotContain(email)
                .doesNotContain(c.phone()).doesNotContain(c.phone().substring(1));
        assertThat(SensitiveDataMasker.mask("{\"token\":\"" + token + "\",\"title\":\"x\"}"))
                .isEqualTo("{\"token\":\"***\",\"title\":\"x\"}");
    }
}
