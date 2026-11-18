package mz.co.moseguros.shared.idempotency;

import static mz.co.moseguros.payment.PaymentFlowSupport.body;
import static mz.co.moseguros.payment.PaymentFlowSupport.pay;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import mz.co.moseguros.SecurityTestConfiguration;
import mz.co.moseguros.SecurityTestConfiguration.IdentityFixtures;
import mz.co.moseguros.SecurityTestConfiguration.MutableClock;
import mz.co.moseguros.TestcontainersConfiguration;
import mz.co.moseguros.identity.IdentityApi;
import mz.co.moseguros.payment.PaymentFlowSupport;
import mz.co.moseguros.payment.PaymentFlowSupport.Seg;
import mz.co.moseguros.rating.QuoteTestSupport.Customer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

/**
 * Idempotência partilhada (S5-02 §5) no pedido de pagamento de uma proposta: mesma chave, outro corpo,
 * pedidos simultâneos, erro no primeiro pedido, expiração (24 h), cabeçalho em falta ou inválido e
 * chaves de utilizadores diferentes. O registo fica em {@code shared.idempotency_record}.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestcontainersConfiguration.class, SecurityTestConfiguration.class})
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class IdempotencyServiceIT {

    @LocalServerPort
    int port;

    @Autowired
    IdentityFixtures fixtures;

    @Autowired
    IdentityApi identityApi;

    @Autowired
    MutableClock clock;

    PaymentFlowSupport support;
    Seg insurer;

    @BeforeAll
    void setUpAll() throws Exception {
        RestAssured.port = port;
        support = new PaymentFlowSupport(fixtures, identityApi, clock);
        support.createTemplate();
        insurer = support.insurer("Seguradora Idempotência");
        support.enableBoth(insurer.id());
    }

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        clock.reset();
    }

    @AfterEach
    void tearDown() {
        clock.reset();
    }

    int intents(UUID proposalId) {
        return support.owner().queryForObject("select count(*) from payment.payment_intent where target_id = ?",
                Integer.class, proposalId);
    }

    Map<String, Object> reference() {
        return body("BANK_REFERENCE", null, "WEB");
    }

    @Test
    void sameKeyReturnsTheSameBodyAndCreatesOneIntent() {
        Customer c = support.customer();
        UUID proposal = support.accepted(c.id(), insurer);
        String key = UUID.randomUUID().toString();
        Response first = pay(c.token(), proposal, key, reference());
        first.then().statusCode(202);
        Response second = pay(c.token(), proposal, key, reference());
        second.then().statusCode(202);
        assertThat(second.asString()).isEqualTo(first.asString());
        assertThat(intents(proposal)).isEqualTo(1);

        Map<String, Object> record = support.owner().queryForMap("""
                select operation, response_status, resource_id, request_hash,
                       extract(epoch from expires_at - created_at)::bigint as ttl
                  from shared.idempotency_record where user_id = ? and idempotency_key = ?::uuid""", c.id(), key);
        assertThat(record).containsEntry("operation", "CREATE_PAYMENT_INTENT").containsEntry("response_status", 202)
                .containsEntry("resource_id", UUID.fromString(first.path("id")));
        assertThat((String) record.get("request_hash")).matches("^[0-9a-f]{64}$");
        assertThat(((Number) record.get("ttl")).longValue()).isEqualTo(24 * 3600L);
        assertThat(support.owner().queryForObject("select to_regclass('payment.idempotency_record') is null",
                Boolean.class)).isTrue();
    }

    @Test
    void sameKeyWithAnotherBodyIsRejected() {
        Customer c = support.customer();
        UUID proposal = support.accepted(c.id(), insurer);
        String key = UUID.randomUUID().toString();
        pay(c.token(), proposal, key, reference()).then().statusCode(202);
        pay(c.token(), proposal, key, body("BANK_REFERENCE", null, "APP")).then().statusCode(422)
                .body("code", equalTo("IDEMPOTENCY_KEY_REUSED"));
        UUID otherProposal = support.accepted(c.id(), insurer);
        pay(c.token(), otherProposal, key, reference()).then().statusCode(422)
                .body("code", equalTo("IDEMPOTENCY_KEY_REUSED"));
        assertThat(intents(otherProposal)).isZero();
    }

    @Test
    void concurrentRequestsWithTheSameKeyCreateOneIntent() throws Exception {
        Customer c = support.customer();
        UUID proposal = support.accepted(c.id(), insurer);
        String key = UUID.randomUUID().toString();
        CountDownLatch start = new CountDownLatch(1);
        Callable<Response> call = () -> {
            start.await();
            return pay(c.token(), proposal, key, reference());
        };
        ExecutorService pool = Executors.newFixedThreadPool(2);
        try {
            List<Future<Response>> futures = List.of(pool.submit(call), pool.submit(call));
            start.countDown();
            Response a = futures.get(0).get();
            Response b = futures.get(1).get();
            assertThat(a.statusCode()).isEqualTo(202);
            assertThat(b.statusCode()).isEqualTo(202);
            assertThat(a.asString()).isEqualTo(b.asString());
        } finally {
            pool.shutdownNow();
        }
        assertThat(intents(proposal)).isEqualTo(1);
    }

    @Test
    void anErrorInTheFirstRequestLetsTheKeyBeUsedAgain() {
        Customer c = support.customer();
        UUID proposal = support.proposal(c.id(), insurer, "UNDER_REVIEW", null, PaymentFlowSupport.AMOUNT, null);
        String key = UUID.randomUUID().toString();
        pay(c.token(), proposal, key, reference()).then().statusCode(409)
                .body("code", equalTo("PAYMENT_TARGET_NOT_PAYABLE"));
        assertThat(support.owner().queryForObject("select count(*) from shared.idempotency_record where "
                + "idempotency_key = ?::uuid", Integer.class, key)).isZero();
        support.owner().update("update proposal.proposal set status = 'ACCEPTED', expires_at = now() + interval "
                + "'15 days' where id = ?", proposal);
        pay(c.token(), proposal, key, reference()).then().statusCode(202);
        assertThat(intents(proposal)).isEqualTo(1);
    }

    @Test
    void aKeyOlderThan24HoursCreatesANewIntent() {
        Customer c = support.customer();
        UUID proposal = support.accepted(c.id(), insurer);
        String key = UUID.randomUUID().toString();
        Response first = pay(c.token(), proposal, key, reference());
        first.then().statusCode(202);
        // a referência da primeira já não conta (o montante mudou), para a segunda ser outra intenção
        support.owner().update("update proposal.proposal set total_amount = total_amount + 100 where id = ?",
                proposal);

        clock.advance(Duration.ofHours(25));
        String token = support.token(c.account());
        Response second = pay(token, proposal, key, reference());
        second.then().statusCode(202);
        assertThat(second.<String>path("id")).isNotEqualTo(first.path("id"));
        assertThat(intents(proposal)).isEqualTo(2);
        assertThat(support.owner().queryForObject("select count(*) from shared.idempotency_record where user_id = ? "
                + "and idempotency_key = ?::uuid", Integer.class, c.id(), key)).isEqualTo(1);
    }

    @Test
    void theHeaderIsRequiredAndMustBeAUuid() {
        Customer c = support.customer();
        UUID proposal = support.accepted(c.id(), insurer);
        pay(c.token(), proposal, null, reference()).then().statusCode(400)
                .body("code", equalTo("IDEMPOTENCY_KEY_REQUIRED"));
        pay(c.token(), proposal, "abc", reference()).then().statusCode(400)
                .body("code", equalTo("VALIDATION_ERROR"))
                .body("errors[0].field", equalTo("Idempotency-Key"))
                .body("errors[0].code", equalTo("INVALID_UUID"));
        assertThat(intents(proposal)).isZero();
    }

    @Test
    void theSameKeyFromAnotherUserDoesNotCollide() {
        Customer a = support.customer();
        Customer b = support.customer();
        UUID proposalA = support.accepted(a.id(), insurer);
        UUID proposalB = support.accepted(b.id(), insurer);
        String key = UUID.randomUUID().toString();
        Response ra = pay(a.token(), proposalA, key, reference());
        ra.then().statusCode(202);
        Response rb = pay(b.token(), proposalB, key, reference());
        rb.then().statusCode(202);
        assertThat(rb.<String>path("id")).isNotEqualTo(ra.path("id"));
        assertThat(support.owner().queryForObject("select count(*) from shared.idempotency_record where "
                + "idempotency_key = ?::uuid", Integer.class, key)).isEqualTo(2);
    }
}
