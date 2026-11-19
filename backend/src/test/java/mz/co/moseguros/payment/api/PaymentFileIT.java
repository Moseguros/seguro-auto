package mz.co.moseguros.payment.api;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import io.restassured.RestAssured;
import mz.co.moseguros.SecurityTestConfiguration;
import mz.co.moseguros.SecurityTestConfiguration.IdentityFixtures;
import mz.co.moseguros.SecurityTestConfiguration.MutableClock;
import mz.co.moseguros.TestcontainersConfiguration;
import mz.co.moseguros.identity.IdentityApi;
import mz.co.moseguros.payment.PaymentFlowSupport;
import mz.co.moseguros.payment.PaymentFlowSupport.Seg;
import mz.co.moseguros.payment.PaymentMessagesSupport;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

/**
 * Ficheiros de movimentos e estado da fonte da seguradora (S5-10 §7): contagens de confirmados e de excepções
 * por ficheiro, estado das leituras e isolamento entre seguradoras. Os dados são gravados como dono; o
 * PaymentFileEventsListenerIT cobre o consumo.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestcontainersConfiguration.class, SecurityTestConfiguration.class})
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PaymentFileIT {

    static final String FILES = "/api/v1/insurer/payment-files";
    static final String SOURCE = "/api/v1/insurer/payment-file-source";

    @LocalServerPort
    int port;

    @Autowired
    IdentityFixtures fixtures;

    @Autowired
    IdentityApi identityApi;

    @Autowired
    MutableClock clock;

    PaymentFlowSupport support;
    JdbcTemplate owner;
    Seg a;
    Seg b;
    Seg none;
    String entityA;
    UUID sourceA;
    UUID sourceB;

    @BeforeAll
    void setUpAll() {
        RestAssured.port = port;
        support = new PaymentFlowSupport(fixtures, identityApi, clock);
        owner = support.owner();
        a = support.insurer("Seguradora Ficheiros A");
        entityA = support.enable(a.id(), "BANK_REFERENCE", null, null, 1, "Referência");
        sourceA = sourceOf(a.id());
        b = support.insurer("Seguradora Ficheiros B");
        support.enable(b.id(), "BANK_REFERENCE", null, null, 1, "Referência");
        sourceB = sourceOf(b.id());
        none = support.insurer("Seguradora Sem Referência");
    }

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    UUID sourceOf(UUID insurerId) {
        return owner.queryForObject("select id from payment.insurer_payment_method where insurer_id = ? "
                + "and method = 'BANK_REFERENCE'", UUID.class, insurerId);
    }

    UUID file(UUID insurerId, UUID sourceId, String sha, String status, Instant at) {
        UUID id = UUID.randomUUID();
        boolean processed = "PROCESSED".equals(status);
        owner.update("""
                insert into payment.file_import
                  (id, created_at, created_by, updated_at, updated_by, version, message_id, insurer_id, source_id,
                   file_name, sha256, file_format, format_file_id, status, record_count, total_amount, total_fees,
                   reason, detail, line_no, occurred_at)
                values (?, now(), 'test', now(), 'test', 0, ?, ?, ?, ?, ?, 'MEPS', '202701151', ?, ?, ?, ?, ?, ?, ?, ?)""",
                id, UUID.randomUUID().toString().replace("-", ""), insurerId, sourceId, "MEPS_" + sha.substring(0, 6)
                        + ".txt", sha, status, processed ? 3 : null, processed ? 900_000L : null,
                processed ? 4_500L : null, processed ? null : "PARSE_ERROR", processed ? null : "Linha 4 inválida",
                processed ? null : 4, Timestamp.from(at));
        return id;
    }

    void inbound(UUID insurerId, UUID sourceId, String sha, String outcome) {
        owner.update("""
                insert into payment.inbound_message
                  (id, created_at, created_by, updated_at, updated_by, version, message_id, message_type, insurer_id,
                   source_id, file_sha256, received_at, outcome)
                values (?, now(), 'test', now(), 'test', 1, ?, 'ReferencePaymentReceived', ?, ?, ?, now(), ?)""",
                UUID.randomUUID(), UUID.randomUUID().toString().replace("-", ""), insurerId, sourceId, sha, outcome);
    }

    @Test
    void filesCarryTheConfirmedAndExceptionCounts() {
        Instant at = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        String sha = PaymentMessagesSupport.newFileSha();
        UUID processed = file(a.id(), sourceA, sha, "PROCESSED", at.minusSeconds(3_600));
        inbound(a.id(), sourceA, sha, "CONFIRMED");
        inbound(a.id(), sourceA, sha, "CONFIRMED");
        inbound(a.id(), sourceA, sha, "EXCEPTION");
        UUID rejected = file(a.id(), sourceA, PaymentMessagesSupport.newFileSha(), "REJECTED", at);
        UUID ofB = file(b.id(), sourceB, PaymentMessagesSupport.newFileSha(), "PROCESSED", at);

        given().auth().oauth2(a.admin()).queryParam("size", 100).get(FILES).then().statusCode(200)
                .body("content.id", hasItem(processed.toString()))
                .body("content.id", hasItem(rejected.toString()))
                .body("content.id", not(hasItem(ofB.toString())))
                .body("content.find { it.id == '" + processed + "' }.confirmedCount", equalTo(2))
                .body("content.find { it.id == '" + processed + "' }.exceptionCount", equalTo(1))
                .body("content.find { it.id == '" + processed + "' }.recordCount", equalTo(3))
                .body("content.find { it.id == '" + processed + "' }.totalAmount.amount", equalTo(900_000))
                .body("content.find { it.id == '" + processed + "' }.totalFees.amount", equalTo(4_500))
                .body("content.find { it.id == '" + processed + "' }.sha256", equalTo(sha))
                .body("content.find { it.id == '" + rejected + "' }.reason", equalTo("PARSE_ERROR"))
                .body("content.find { it.id == '" + rejected + "' }.lineNo", equalTo(4))
                .body("content.find { it.id == '" + rejected + "' }.confirmedCount", equalTo(0));

        given().auth().oauth2(a.finance()).queryParam("status", "REJECTED").queryParam("size", 100).get(FILES).then()
                .statusCode(200)
                .body("content.id", hasItem(rejected.toString()))
                .body("content.id", not(hasItem(processed.toString())));
        given().auth().oauth2(b.admin()).queryParam("size", 100).get(FILES).then().statusCode(200)
                .body("content.id", contains(ofB.toString()));
    }

    @Test
    void sourceStatusShowsTheLastPolls() {
        Instant pollAt = Instant.now().truncatedTo(ChronoUnit.SECONDS).minusSeconds(120);
        owner.update("update payment.insurer_payment_method set last_poll_at = ?, last_poll_outcome = 'ERROR', "
                + "last_poll_error = 'SFTP_AUTH_FAILED', last_poll_success_at = ?, stale_since = ? where id = ?",
                Timestamp.from(pollAt), Timestamp.from(pollAt.minusSeconds(1_800)), Timestamp.from(pollAt), sourceA);

        given().auth().oauth2(a.admin()).get(SOURCE).then().statusCode(200)
                .body("configured", equalTo(true))
                .body("enabled", equalTo(true))
                .body("entity", equalTo(entityA))
                .body("fileFormat", equalTo("MEPS"))
                .body("staleAfterMinutes", equalTo(15))
                .body("lastPollAt", equalTo(pollAt.toString()))
                .body("lastPollOutcome", equalTo("ERROR"))
                .body("lastPollError", equalTo("SFTP_AUTH_FAILED"))
                .body("lastSuccessAt", equalTo(pollAt.minusSeconds(1_800).toString()))
                .body("staleSince", equalTo(pollAt.toString()));
        given().auth().oauth2(b.admin()).get(SOURCE).then().statusCode(200)
                .body("configured", equalTo(true))
                .body("lastPollAt", nullValue());
        given().auth().oauth2(none.admin()).get(SOURCE).then().statusCode(200)
                .body("configured", equalTo(false))
                .body("entity", nullValue());
    }

    @Test
    void permissions() {
        given().auth().oauth2(support.customer().token()).get(FILES).then().statusCode(403);
        given().auth().oauth2(support.customer().token()).get(SOURCE).then().statusCode(403);
        given().auth().oauth2(a.underwriter()).get(FILES).then().statusCode(403);
        given().get(FILES).then().statusCode(401);
        given().auth().oauth2(support.staffToken(null, "PLATFORM_ADMIN")).get(FILES).then().statusCode(403);
    }
}
