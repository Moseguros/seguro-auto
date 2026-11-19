package mz.co.moseguros.payment.api;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.notNullValue;

import java.sql.Timestamp;
import java.time.Instant;
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
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.RabbitMQContainer;

/**
 * Operações dos pagamentos do administrador da plataforma (S5-10 §8): fontes de todas as seguradoras e estado das
 * filas e do worker, com o RabbitMQ acessível e inacessível.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestcontainersConfiguration.class, SecurityTestConfiguration.class})
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AdminPaymentOperationsIT {

    static final String SOURCES = "/api/v1/admin/payment-file-sources";
    static final String WORKER = "/api/v1/admin/payment-worker-status";

    @LocalServerPort
    int port;

    @Autowired
    IdentityFixtures fixtures;

    @Autowired
    IdentityApi identityApi;

    @Autowired
    MutableClock clock;

    @Autowired
    CachingConnectionFactory connectionFactory;

    @Autowired
    RabbitMQContainer rabbitmq;

    PaymentFlowSupport support;
    JdbcTemplate owner;
    Seg a;
    Seg b;
    UUID sourceA;
    UUID sourceB;
    String admin;

    @BeforeAll
    void setUpAll() {
        RestAssured.port = port;
        support = new PaymentFlowSupport(fixtures, identityApi, clock);
        owner = support.owner();
        a = support.insurer("Seguradora Operações A");
        support.enable(a.id(), "BANK_REFERENCE", null, null, 1, "Referência");
        sourceA = sourceOf(a.id());
        b = support.insurer("Seguradora Operações B");
        support.enable(b.id(), "BANK_REFERENCE", null, null, 1, "Referência");
        sourceB = sourceOf(b.id());
        owner.update("update payment.insurer_payment_method set enabled = false where id = ?", sourceB);
        admin = support.staffToken(null, "PLATFORM_ADMIN");
    }

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    UUID sourceOf(UUID insurerId) {
        return owner.queryForObject("select id from payment.insurer_payment_method where insurer_id = ? "
                + "and method = 'BANK_REFERENCE'", UUID.class, insurerId);
    }

    @Test
    void platformAdminSeesTheSourcesOfAllInsurers() {
        String sha = PaymentMessagesSupport.newFileSha();
        for (String status : new String[] {"PROCESSED", "REJECTED"}) {
            boolean processed = "PROCESSED".equals(status);
            owner.update("""
                    insert into payment.file_import
                      (id, created_at, created_by, updated_at, updated_by, version, message_id, insurer_id, source_id,
                       file_name, sha256, file_format, status, record_count, total_amount, reason, occurred_at)
                    values (?, now(), 'test', now(), 'test', 0, ?, ?, ?, 'MEPS.txt', ?, 'MEPS', ?, ?, ?, ?, ?)""",
                    UUID.randomUUID(), UUID.randomUUID().toString(), a.id(), sourceA, sha, status,
                    processed ? 1 : null, processed ? 100L : null, processed ? null : "PARSE_ERROR",
                    Timestamp.from(Instant.now().minusSeconds(3_600)));
        }

        given().auth().oauth2(admin).get(SOURCES).then().statusCode(200)
                .body("sources.sourceId", hasItem(sourceA.toString()))
                .body("sources.sourceId", hasItem(sourceB.toString()))
                .body("sources.find { it.sourceId == '" + sourceA + "' }.insurer.id", equalTo(a.id().toString()))
                .body("sources.find { it.sourceId == '" + sourceA + "' }.insurer.displayName", equalTo(a.name()))
                .body("sources.find { it.sourceId == '" + sourceA + "' }.enabled", equalTo(true))
                .body("sources.find { it.sourceId == '" + sourceA + "' }.fileFormat", equalTo("MEPS"))
                .body("sources.find { it.sourceId == '" + sourceA + "' }.filesLast24h", equalTo(2))
                .body("sources.find { it.sourceId == '" + sourceA + "' }.rejectedLast24h", equalTo(1))
                .body("sources.find { it.sourceId == '" + sourceB + "' }.enabled", equalTo(false))
                .body("sources.find { it.sourceId == '" + sourceB + "' }.filesLast24h", equalTo(0));
    }

    @Test
    void platformAdminSeesTheQueues() {
        owner.update("""
                insert into payment.inbound_message
                  (id, created_at, created_by, updated_at, updated_by, version, message_id, message_type, insurer_id,
                   source_id, received_at, outcome)
                values (?, now(), 'test', now(), 'test', 1, ?, 'PaymentSourcePolled', ?, ?, now(), 'RECORDED')""",
                UUID.randomUUID(), UUID.randomUUID().toString(), a.id(), sourceA);

        given().auth().oauth2(admin).get(WORKER).then().statusCode(200)
                .body("brokerReachable", equalTo(true))
                .body("queues.name", containsInAnyOrder("core.reference-payments", "core.payment-file-events",
                        "core.reference-payments.dlq", "core.payment-file-events.dlq"))
                .body("queues.find { it.name == 'core.reference-payments' }.consumers", greaterThanOrEqualTo(1))
                .body("queues.find { it.name == 'core.payment-file-events' }.consumers", greaterThanOrEqualTo(1))
                .body("lastMessageAt", notNullValue());
    }

    @Test
    void anUnreachableBrokerIsReported() {
        // O Spring Boot liga-se pelos "addresses": aponta-os para uma porta fechada e repõe-nos no fim.
        try {
            connectionFactory.setAddresses("127.0.0.1:1");
            connectionFactory.resetConnection();
            given().auth().oauth2(admin).get(WORKER).then().statusCode(200)
                    .body("brokerReachable", equalTo(false))
                    .body("queues", empty());
        } finally {
            connectionFactory.setAddresses(rabbitmq.getHost() + ":" + rabbitmq.getAmqpPort());
            connectionFactory.resetConnection();
        }
    }

    @Test
    void onlyThePlatformAdminCanSeeOperations() {
        given().auth().oauth2(a.admin()).get(SOURCES).then().statusCode(403);
        given().auth().oauth2(a.finance()).get(WORKER).then().statusCode(403);
        given().auth().oauth2(support.customer().token()).get(WORKER).then().statusCode(403);
        given().get(SOURCES).then().statusCode(401);
    }
}
