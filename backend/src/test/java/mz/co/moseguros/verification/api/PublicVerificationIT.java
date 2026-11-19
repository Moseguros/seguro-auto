package mz.co.moseguros.verification.api;

import static io.restassured.RestAssured.given;
import static mz.co.moseguros.payment.PaymentFlowSupport.id;
import static mz.co.moseguros.payment.PaymentFlowSupport.inSystem;
import static mz.co.moseguros.payment.PaymentFlowSupport.payReference;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import mz.co.moseguros.SecurityTestConfiguration;
import mz.co.moseguros.SecurityTestConfiguration.IdentityFixtures;
import mz.co.moseguros.SecurityTestConfiguration.MutableClock;
import mz.co.moseguros.TestcontainersConfiguration;
import mz.co.moseguros.identity.IdentityApi;
import mz.co.moseguros.payment.PaymentFlowSupport.Seg;
import mz.co.moseguros.payment.application.IntentStateService;
import mz.co.moseguros.payment.domain.ConfirmedBy;
import mz.co.moseguros.policy.PolicyStatus;
import mz.co.moseguros.policy.PolicyTestSupport;
import mz.co.moseguros.policy.PolicyTestSupport.Row;
import mz.co.moseguros.rating.QuoteTestSupport.Customer;
import mz.co.moseguros.shared.captcha.FakeCaptchaVerifier;
import mz.co.moseguros.verification.domain.VerificationResult;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Verificação pública (S6-03 §2 a §6): por código e por matrícula, com o CAPTCHA falso do perfil
 * {@code test} ({@link FakeCaptchaVerifier}); a resposta mínima, o registo em {@code check_log} sem a
 * matrícula nem o código e a cache retirada por {@code PolicyIssued}.
 *
 * <p>Todos os pedidos vêm de 127.0.0.1 e contam para o mesmo limite de 60 por minuto: cada teste faz poucos
 * pedidos (os limites estão no {@code VerificationRateLimitIT}).
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestcontainersConfiguration.class, SecurityTestConfiguration.class})
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(OutputCaptureExtension.class)
class PublicVerificationIT {

    static final String BASE = "/api/v1/public/verifications";
    static final String CAPTCHA = "X-Captcha-Token";

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

    PolicyTestSupport support;
    Seg insurer;
    UUID holder;
    Instant now;
    String activePlate;
    String activeToken;
    String holderName;
    UUID active;

    @BeforeAll
    void seed() throws Exception {
        RestAssured.port = port;
        support = new PolicyTestSupport(fixtures, identityApi, clock, tx, states);
        insurer = support.setUp("Seguradora Verificação");
        holder = support.payments().customer().id();
        now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        activePlate = PolicyTestSupport.plate();
        holderName = "Joaquina Tomadora Secreta";
        active = support.insert(Row.of(insurer.id(), holder, activePlate, "ACTIVE", now.minus(Duration.ofDays(10)),
                now.plus(Duration.ofDays(355))).named(holderName));
        activeToken = PolicyTestSupport.token();
        support.certificate(active, "VALID", activeToken);
    }

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    UUID policy(String plate, String status, Duration startFromNow, Duration endFromNow) {
        return support.insert(Row.of(insurer.id(), holder, plate, status, now.plus(startFromNow),
                now.plus(endFromNow)));
    }

    Response byToken(String token) {
        return given().get(BASE + "/{token}", token);
    }

    Response byPlate(String plate, String captcha) {
        var request = given().queryParam("plate", plate);
        if (captcha != null) {
            request.header(CAPTCHA, captcha);
        }
        return request.get(BASE);
    }

    static String masked(String plate) {
        return plate.replaceAll("\\d", "*");
    }

    @Test
    void configShowsTheCaptchaProvider() {
        given().get(BASE + "/config").then().statusCode(200)
                .header("Cache-Control", containsString("no-store"))
                .body("captchaProvider", equalTo("TURNSTILE"))
                .body("captchaSiteKey", equalTo(FakeCaptchaVerifier.SITE_KEY));
    }

    @Test
    void activePolicyByTokenIsValidWithoutPersonalData(CapturedOutput output) {
        Response response = byToken(activeToken);
        response.then().statusCode(200)
                .header("Cache-Control", containsString("no-store"))
                .body("status", equalTo("VALID"))
                .body("plateMasked", equalTo(masked(activePlate)))
                .body("insurerName", equalTo(insurer.name()))
                .body("validFrom", equalTo(now.minus(Duration.ofDays(10)).toString()))
                .body("validTo", equalTo(now.plus(Duration.ofDays(355)).toString()))
                .body("coverageType", equalTo("MTPL_ONLY"))
                .body("certificateStatus", equalTo("CURRENT"))
                .body("checkedAt", org.hamcrest.Matchers.notNullValue());
        String json = response.asString();
        String number = support.owner().queryForObject("select number from policy.policy where id = ?",
                String.class, active);
        assertThat(json).doesNotContain(holderName).doesNotContain("Joaquina").doesNotContain(number)
                .doesNotContain(activeToken).doesNotContain(activePlate).doesNotContain("policyNumber")
                .doesNotContain("certificateNumber").doesNotContain("CT-");

        // por matrícula: o mesmo resultado, sem certificateStatus
        Response plate = byPlate(activePlate, "ok");
        plate.then().statusCode(200).body("status", equalTo("VALID"))
                .body("plateMasked", equalTo(masked(activePlate)))
                .body("insurerName", equalTo(insurer.name()))
                .body("certificateStatus", nullValue());
        assertThat(plate.asString()).doesNotContain(holderName).doesNotContain(number).doesNotContain(activePlate);

        // os logs não têm a matrícula completa nem o código (§4 regra 4)
        assertThat(output.getAll()).doesNotContain(activePlate).doesNotContain(activeToken)
                .contains("plateMasked=" + masked(activePlate));
    }

    @Test
    void expiredAndFutureByToken() {
        String expiredToken = PolicyTestSupport.token();
        support.certificate(policy(PolicyTestSupport.plate(), "EXPIRED", Duration.ofDays(-400),
                Duration.ofDays(-35)), "VALID", expiredToken);
        String futureToken = PolicyTestSupport.token();
        support.certificate(policy(PolicyTestSupport.plate(), "ACTIVE", Duration.ofDays(5), Duration.ofDays(370)),
                "VALID", futureToken);

        byToken(expiredToken).then().statusCode(200).body("status", equalTo("EXPIRED"))
                .body("certificateStatus", equalTo("CURRENT"));
        byToken(futureToken).then().statusCode(200).body("status", equalTo("NOT_YET_VALID"));
    }

    @Test
    void revokedCertificateOfAnActivePolicyShowsTheCurrentPolicyStatus() {
        String plate = PolicyTestSupport.plate();
        UUID policy = policy(plate, "ACTIVE", Duration.ofDays(-30), Duration.ofDays(300));
        String revoked = PolicyTestSupport.token();
        support.certificate(policy, "REVOKED", revoked);

        byToken(revoked).then().statusCode(200)
                .body("status", equalTo("VALID"))
                .body("certificateStatus", equalTo("REVOKED"))
                .body("plateMasked", equalTo(masked(plate)));
    }

    @Test
    void unknownOrMalformedTokenIsNotFound() {
        String unknown = PolicyTestSupport.token();
        JsonPath body = byToken(unknown).then().statusCode(200).extract().jsonPath();
        assertThat(body.getString("status")).isEqualTo("NOT_FOUND");
        assertThat(body.getMap("$")).containsOnlyKeys("status", "checkedAt");

        byToken("curto").then().statusCode(200).body("status", equalTo("NOT_FOUND"))
                .body("plateMasked", nullValue()).body("insurerName", nullValue());
    }

    @Test
    void plateRequiresAValidCaptchaBeforeReadingThePlate() {
        byPlate(activePlate, null).then().statusCode(400).body("code", equalTo("CAPTCHA_REQUIRED"));
        byPlate(activePlate, "invalid").then().statusCode(400).body("code", equalTo("CAPTCHA_INVALID"));
        byPlate(activePlate, "down").then().statusCode(503).body("code", equalTo("CAPTCHA_UNAVAILABLE"));
        // o CAPTCHA é verificado antes da matrícula
        byPlate("???", null).then().statusCode(400).body("code", equalTo("CAPTCHA_REQUIRED"));
        byPlate("???", "ok").then().statusCode(400).body("code", equalTo("VALIDATION_ERROR"))
                .body("errors[0].field", equalTo("plate")).body("errors[0].code", equalTo("INVALID_PLATE"));
        given().header(CAPTCHA, "ok").get(BASE).then().statusCode(400)
                .body("code", equalTo("VALIDATION_ERROR")).body("errors[0].code", equalTo("INVALID_PLATE"));
    }

    @Test
    void plateIsNormalisedAndUnknownPlateIsNotFound() {
        String plate = PolicyTestSupport.plate();
        policy(plate, "CANCELLED", Duration.ofDays(-30), Duration.ofDays(300));
        String written = plate.replace("-", " ").toLowerCase();
        byPlate(written, "ok").then().statusCode(200).body("status", equalTo("CANCELLED"))
                .body("plateMasked", equalTo(masked(plate)));

        String unknown = PolicyTestSupport.plate();
        JsonPath body = byPlate(unknown, "ok").then().statusCode(200).extract().jsonPath();
        assertThat(body.getString("status")).isEqualTo("NOT_FOUND");
        assertThat(body.getString("plateMasked")).isEqualTo(masked(unknown));
        assertThat(body.getMap("$")).containsOnlyKeys("status", "plateMasked", "checkedAt");
    }

    @Test
    void eachCheckIsLoggedWithoutThePlateOrTheToken() {
        Instant from = Instant.now().minusSeconds(1);
        String plate = PolicyTestSupport.plate();
        UUID policy = policy(plate, "ACTIVE", Duration.ofDays(-1), Duration.ofDays(300));
        String token = PolicyTestSupport.token();
        support.certificate(policy, "VALID", token);

        byPlate(plate, "ok").then().statusCode(200);
        byToken(token).then().statusCode(200);

        List<Map<String, Object>> rows = support.owner().queryForList(
                "select * from verification.check_log where policy_id = ? order by checked_at", policy);
        assertThat(rows).hasSize(2);
        for (Map<String, Object> row : rows) {
            for (Object value : row.values()) {
                if (value != null) {
                    assertThat(value.toString()).doesNotContain(plate).doesNotContain(token)
                            .doesNotContain(plate.replace("-", ""));
                }
            }
            assertThat((String) row.get("subject_hash")).matches("[0-9a-f]{64}");
            assertThat(row.get("plate_masked")).isEqualTo(masked(plate));
            assertThat(row.get("result")).isEqualTo("VALID");
            assertThat(row.get("checker_type")).isEqualTo("PUBLIC");
            assertThat(row.get("checker_user_id")).isNull();
            assertThat(row.get("insurer_id")).isEqualTo(insurer.id());
            assertThat(row.get("ip_prefix")).isIn("127.0.0.0/24", "0:0:0::/48");
            assertThat(((Timestamp) row.get("checked_at")).toInstant()).isAfter(from);
        }
        assertThat(rows).extracting(r -> r.get("subject_type")).containsExactly("PLATE", "TOKEN");
        assertThat(rows.get(0).get("subject_hash")).isNotEqualTo(rows.get(1).get("subject_hash"));
    }

    @Test
    void anIssuedPolicyReplacesACachedNotFound() {
        Customer customer = support.payments().customer();
        UUID proposal = support.payments().accepted(customer.id(), insurer);
        String plate = PolicyTestSupport.plate();
        support.owner().update("update proposal.proposal set vehicle_snapshot = jsonb_set(vehicle_snapshot, "
                + "'{plate}', to_jsonb(?::text)) where id = ?", plate, proposal);

        byPlate(plate, "ok").then().statusCode(200).body("status", equalTo("NOT_FOUND"));

        UUID intentId = id(payReference(customer.token(), proposal));
        inSystem(tx, () -> states.markSucceeded(intentId, null, ConfirmedBy.REFERENCE_FILE, null, "test"));
        UUID policyId = support.awaitPolicy(proposal);
        await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> assertThat(support.owner().queryForObject("""
                select count(*) from event_publication
                 where listener_id like '%PolicyCacheListener%' and serialized_event like ?
                   and completion_date is not null""", Long.class, "%" + policyId + "%")).isOne());

        Map<String, Object> row = support.policyRow(policyId);
        VerificationResult expected = VerificationResult.of(VerificationResult.coverAt(PolicyStatus.ACTIVE,
                ((Timestamp) row.get("start_at")).toInstant(), ((Timestamp) row.get("end_at")).toInstant(),
                Instant.now()));
        assertThat(expected).isIn(VerificationResult.VALID, VerificationResult.NOT_YET_VALID);
        byPlate(plate, "ok").then().statusCode(200).body("status", equalTo(expected.name()))
                .body("insurerName", equalTo(insurer.name()));
    }
}
