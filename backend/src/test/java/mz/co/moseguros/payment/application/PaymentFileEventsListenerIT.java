package mz.co.moseguros.payment.application;

import static mz.co.moseguros.payment.PaymentMessagesSupport.newFileSha;
import static mz.co.moseguros.payment.PaymentMessagesSupport.polled;
import static mz.co.moseguros.payment.PaymentMessagesSupport.processed;
import static mz.co.moseguros.payment.PaymentMessagesSupport.rejected;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import mz.co.moseguros.SecurityTestConfiguration;
import mz.co.moseguros.SecurityTestConfiguration.IdentityFixtures;
import mz.co.moseguros.TestcontainersConfiguration;
import mz.co.moseguros.organization.domain.Organization;
import mz.co.moseguros.organization.OrganizationType;
import mz.co.moseguros.payment.PaymentMessagesSupport;
import mz.co.moseguros.paymentcontracts.PaymentFileProcessed;
import mz.co.moseguros.paymentcontracts.PaymentFileRejected;
import mz.co.moseguros.paymentcontracts.PaymentSourcePolled;
import mz.co.moseguros.paymentcontracts.PaymentsTopology;
import mz.co.moseguros.paymentcontracts.PollErrorCode;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

/**
 * Ficheiros processados e rejeitados e resultados das leituras das fontes (S5-10 §4), publicados em
 * {@code payments.events} como o worker os publica.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestcontainersConfiguration.class, SecurityTestConfiguration.class})
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PaymentFileEventsListenerIT {

    @Autowired
    IdentityFixtures fixtures;

    @Autowired
    RabbitTemplate rabbit;

    @Autowired
    ObjectMapper mapper;

    PaymentMessagesSupport messages;
    JdbcTemplate owner;
    UUID insurerId;
    UUID sourceId;
    UUID otherInsurerId;
    UUID otherSourceId;

    @BeforeAll
    void setUpAll() {
        owner = fixtures.owner();
        messages = new PaymentMessagesSupport(rabbit, mapper, owner);
        insurerId = insurer("Seguradora Ficheiros");
        sourceId = source(insurerId);
        otherInsurerId = insurer("Seguradora Ficheiros B");
        otherSourceId = source(otherInsurerId);
    }

    UUID insurer(String name) {
        Organization org = fixtures.organization(OrganizationType.INSURER, name + " " + UUID.randomUUID()
                .toString().substring(0, 4));
        return org.getId();
    }

    /** Fonte {@code BANK_REFERENCE} activa (como dono). */
    UUID source(UUID insurer) {
        UUID id = UUID.randomUUID();
        String entity = String.valueOf(10000 + (int) (Math.random() * 79999));
        owner.update("""
                insert into payment.insurer_payment_method
                  (id, created_at, created_by, updated_at, updated_by, version, insurer_id, method, enabled, enabled_at,
                   environment, display_name, settings, secret_refs, intent_validity_minutes, sort_order)
                values (?, now(), 'test', now(), 'test', 0, ?, 'BANK_REFERENCE', true, now(), 'SANDBOX', 'Ref',
                        ?::jsonb, '{}'::jsonb, 30, 1)""",
                id, insurer, "{\"entity\":\"" + entity + "\",\"fileFormat\":\"MEPS\"}");
        return id;
    }

    Map<String, Object> source() {
        return owner.queryForMap("select version, last_poll_at, last_poll_outcome, last_poll_error, "
                + "last_poll_success_at from payment.insurer_payment_method where id = ?", sourceId);
    }

    @Test
    void processedAndRejectedFilesAreRecordedOnce() {
        Instant at = Instant.now().truncatedTo(ChronoUnit.MILLIS);
        String sha = newFileSha();
        PaymentFileProcessed ok = processed(insurerId, sourceId, sha, 3, 750_000, at);
        String badSha = newFileSha();
        PaymentFileRejected bad = rejected(insurerId, sourceId, badSha, at);

        messages.publish(ok);
        messages.publish(bad);
        messages.publish(ok);

        assertThat(messages.awaitInbound(ok.messageId()).get("outcome")).isEqualTo("RECORDED");
        assertThat(messages.awaitInbound(bad.messageId()).get("outcome")).isEqualTo("RECORDED");
        Map<String, Object> f = owner.queryForMap("select * from payment.file_import where message_id = ?",
                ok.messageId());
        assertThat(f.get("status")).isEqualTo("PROCESSED");
        assertThat(f.get("insurer_id")).isEqualTo(insurerId);
        assertThat(f.get("source_id")).isEqualTo(sourceId);
        assertThat(f.get("sha256")).isEqualTo(sha);
        assertThat(f.get("file_format")).isEqualTo("MEPS");
        assertThat(f.get("format_file_id")).isEqualTo("202701151");
        assertThat(f.get("record_count")).isEqualTo(3);
        assertThat(f.get("total_amount")).isEqualTo(750_000L);
        assertThat(f.get("total_fees")).isEqualTo(3_000L);
        assertThat(((Timestamp) f.get("occurred_at")).toInstant()).isEqualTo(at);
        Map<String, Object> r = owner.queryForMap("select * from payment.file_import where message_id = ?",
                bad.messageId());
        assertThat(r.get("status")).isEqualTo("REJECTED");
        assertThat(r.get("reason")).isEqualTo("TRAILER_TOTAL_MISMATCH");
        assertThat(r.get("detail")).isEqualTo("Total do rodapé errado");
        assertThat(r.get("record_count")).isNull();

        awaitQueueEmpty();
        assertThat(owner.queryForObject("select count(*) from payment.file_import where sha256 = ?", Long.class, sha))
                .isEqualTo(1);
        assertThat(owner.queryForObject("select count(*) from payment.inbound_message where message_id = ?",
                Long.class, ok.messageId())).isEqualTo(1);
    }

    @Test
    void pollsUpdateTheSourceAndOutOfOrderOrForeignOnesAreIgnored() {
        long versionBefore = ((Number) source().get("version")).longValue();
        Instant t1 = Instant.now().truncatedTo(ChronoUnit.MILLIS).minusSeconds(300);
        PaymentSourcePolled ok = polled(insurerId, sourceId, t1, PaymentSourcePolled.OUTCOME_OK, null);
        messages.publish(ok);
        assertThat(messages.awaitInbound(ok.messageId()).get("outcome")).isEqualTo("RECORDED");
        Map<String, Object> s = source();
        assertThat(((Timestamp) s.get("last_poll_at")).toInstant()).isEqualTo(t1);
        assertThat(s.get("last_poll_outcome")).isEqualTo("OK");
        assertThat(s.get("last_poll_error")).isNull();
        assertThat(((Timestamp) s.get("last_poll_success_at")).toInstant()).isEqualTo(ok.finishedAt());
        // A leitura não muda a versão da configuração (quem a edita não recebe CONCURRENT_MODIFICATION).
        assertThat(((Number) s.get("version")).longValue()).isEqualTo(versionBefore);

        Instant t2 = t1.plusSeconds(60);
        PaymentSourcePolled error = polled(insurerId, sourceId, t2, PaymentSourcePolled.OUTCOME_ERROR,
                PollErrorCode.SFTP_UNREACHABLE);
        messages.publish(error);
        assertThat(messages.awaitInbound(error.messageId()).get("outcome")).isEqualTo("RECORDED");
        s = source();
        assertThat(((Timestamp) s.get("last_poll_at")).toInstant()).isEqualTo(t2);
        assertThat(s.get("last_poll_outcome")).isEqualTo("ERROR");
        assertThat(s.get("last_poll_error")).isEqualTo("SFTP_UNREACHABLE");
        assertThat(((Timestamp) s.get("last_poll_success_at")).toInstant()).isEqualTo(ok.finishedAt());

        PaymentSourcePolled older = polled(insurerId, sourceId, t1.minusSeconds(60), PaymentSourcePolled.OUTCOME_OK,
                null);
        messages.publish(older);
        assertThat(messages.awaitInbound(older.messageId()).get("outcome")).isEqualTo("IGNORED");
        assertThat(((Timestamp) source().get("last_poll_at")).toInstant()).isEqualTo(t2);

        // A fonte da outra seguradora numa mensagem desta seguradora: ignorada, nenhuma das duas muda.
        PaymentSourcePolled foreign = polled(insurerId, otherSourceId, t2.plusSeconds(60),
                PaymentSourcePolled.OUTCOME_OK, null);
        messages.publish(foreign);
        assertThat(messages.awaitInbound(foreign.messageId()).get("outcome")).isEqualTo("IGNORED");
        assertThat(owner.queryForObject("select last_poll_at from payment.insurer_payment_method where id = ?",
                Timestamp.class, otherSourceId)).isNull();
        assertThat(((Timestamp) source().get("last_poll_at")).toInstant()).isEqualTo(t2);
    }

    private void awaitQueueEmpty() {
        await().pollDelay(Duration.ofMillis(500)).atMost(PaymentMessagesSupport.WAIT).until(() ->
                rabbit.execute(c -> c.queueDeclarePassive(PaymentsTopology.PAYMENT_FILE_EVENTS_QUEUE)
                        .getMessageCount()) == 0);
    }
}
