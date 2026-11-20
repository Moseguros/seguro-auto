package mz.co.moseguros.verification.api;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import mz.co.moseguros.SecurityTestConfiguration;
import mz.co.moseguros.SecurityTestConfiguration.IdentityFixtures;
import mz.co.moseguros.SecurityTestConfiguration.MutableClock;
import mz.co.moseguros.TestcontainersConfiguration;
import mz.co.moseguros.identity.IdentityApi;
import mz.co.moseguros.identity.domain.UserAccount;
import mz.co.moseguros.organization.OrganizationType;
import mz.co.moseguros.payment.PaymentFlowSupport.Seg;
import mz.co.moseguros.payment.application.IntentStateService;
import mz.co.moseguros.policy.PolicyTestSupport;
import mz.co.moseguros.policy.PolicyTestSupport.Row;
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
 * Verificação pelos agentes da polícia (S6-03 §2): {@code verification:police} (perfil
 * {@code POLICE_OFFICER}), sem CAPTCHA, com os números da apólice e do certificado e o veículo; o
 * histórico só com as consultas do próprio agente.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestcontainersConfiguration.class, SecurityTestConfiguration.class})
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PoliceVerificationIT {

    static final String BASE = "/api/v1/police/verifications";
    static final String HISTORY = "/api/v1/police/check-history";

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
    UUID police;
    String plate;
    String token;
    String policyNumber;
    String holderName;

    @BeforeAll
    void seed() {
        RestAssured.port = port;
        support = new PolicyTestSupport(fixtures, identityApi, clock, tx, states);
        insurer = support.insurer("Seguradora Polícia");
        police = fixtures.organization(OrganizationType.POLICE, "PRM Maputo").getId();
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        plate = PolicyTestSupport.plate();
        holderName = "Albertina Tomadora Privada";
        UUID policy = support.insert(Row.of(insurer.id(), support.payments().customer().id(), plate, "ACTIVE",
                now.minus(Duration.ofDays(3)), now.plus(Duration.ofDays(360))).named(holderName));
        token = PolicyTestSupport.token();
        support.certificate(policy, "VALID", token);
        policyNumber = support.owner().queryForObject("select number from policy.policy where id = ?",
                String.class, policy);
    }

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    String officer() {
        return token(fixtures.staff("Agente " + UUID.randomUUID().toString().substring(0, 4), police,
                "POLICE_OFFICER"));
    }

    String token(UserAccount account) {
        return identityApi.issueTokens(account, null).accessToken();
    }

    @Test
    void requiresVerificationPolice() {
        given().queryParam("plate", plate).get(BASE).then().statusCode(401);
        given().get(BASE + "/{token}", token).then().statusCode(401);
        given().get(HISTORY).then().statusCode(401);

        String customer = support.payments().customer().token();
        String insurerStaff = support.payments().staffToken(insurer.id(), "INSURER_ADMIN");
        String platformAdmin = token(fixtures.staff("Admin", null, "PLATFORM_ADMIN"));
        for (String other : List.of(customer, insurerStaff, platformAdmin)) {
            given().auth().oauth2(other).queryParam("plate", plate).get(BASE).then().statusCode(403);
            given().auth().oauth2(other).get(BASE + "/{token}", token).then().statusCode(403);
            given().auth().oauth2(other).get(HISTORY).then().statusCode(403);
        }
    }

    @Test
    void anOfficerSeesThePolicyAndVehicleWithoutCaptchaOrPersonalData() {
        String officer = officer();
        String json = given().auth().oauth2(officer).queryParam("plate", plate.replace("-", ""))
                .get(BASE).then().statusCode(200)
                .header("Cache-Control", containsString("no-store"))
                .body("status", equalTo("VALID"))
                .body("plateMasked", equalTo(plate.replaceAll("\\d", "*")))
                .body("insurerName", equalTo(insurer.name()))
                .body("policyNumber", equalTo(policyNumber))
                .body("certificateNumber", org.hamcrest.Matchers.startsWith("CT-"))
                .body("vehicleMake", equalTo("Toyota"))
                .body("vehicleModel", equalTo("Corolla"))
                .body("certificateStatus", nullValue())
                .extract().asString();
        assertThat(json).doesNotContain(holderName).doesNotContain("Albertina").doesNotContain(token);

        given().auth().oauth2(officer).get(BASE + "/{token}", token).then().statusCode(200)
                .body("status", equalTo("VALID"))
                .body("policyNumber", equalTo(policyNumber))
                .body("certificateStatus", equalTo("CURRENT"))
                .body("vehicleMake", equalTo("Toyota"));

        given().auth().oauth2(officer).queryParam("plate", "???").get(BASE).then().statusCode(400)
                .body("errors[0].field", equalTo("plate")).body("errors[0].code", equalTo("INVALID_PLATE"));
        given().auth().oauth2(officer).get(BASE + "/{token}", PolicyTestSupport.token()).then().statusCode(200)
                .body("status", equalTo("NOT_FOUND")).body("policyNumber", nullValue());
    }

    @Test
    void historyShowsOnlyTheOfficersOwnChecks() {
        UserAccount agent1 = fixtures.staff("Agente Um", police, "POLICE_OFFICER");
        UserAccount agent2 = fixtures.staff("Agente Dois", police, "POLICE_OFFICER");
        String t1 = token(agent1);
        String t2 = token(agent2);

        given().auth().oauth2(t1).queryParam("plate", plate).get(BASE).then().statusCode(200);
        given().auth().oauth2(t1).get(BASE + "/{token}", "formato-errado").then().statusCode(200);
        given().auth().oauth2(t2).get(BASE + "/{token}", token).then().statusCode(200);

        JsonPath mine = given().auth().oauth2(t1).get(HISTORY).then().statusCode(200)
                .header("Cache-Control", containsString("no-store")).extract().jsonPath();
        assertThat(mine.getInt("totalElements")).isEqualTo(2);
        assertThat(mine.getList("content.subjectType", String.class)).containsExactly("TOKEN", "PLATE");
        assertThat(mine.getList("content.result", String.class)).containsExactly("NOT_FOUND", "VALID");
        assertThat(mine.getString("content[0].plateMasked")).isNull();
        assertThat(mine.getString("content[1].plateMasked")).isEqualTo(plate.replaceAll("\\d", "*"));
        assertThat(mine.getString("content[0].id")).isNotBlank();

        JsonPath theirs = given().auth().oauth2(t2).get(HISTORY).then().statusCode(200).extract().jsonPath();
        assertThat(theirs.getInt("totalElements")).isEqualTo(1);
        assertThat(theirs.getList("content.subjectType", String.class)).containsExactly("TOKEN");

        // filtros por data (Africa/Maputo, inclusivas)
        LocalDate today = LocalDate.now(ZoneId.of("Africa/Maputo"));
        given().auth().oauth2(t1).queryParam("from", today.toString()).queryParam("to", today.toString())
                .get(HISTORY).then().statusCode(200).body("totalElements", equalTo(2));
        given().auth().oauth2(t1).queryParam("from", today.minusDays(10).toString())
                .queryParam("to", today.minusDays(5).toString()).get(HISTORY).then().statusCode(200)
                .body("totalElements", equalTo(0));
        given().auth().oauth2(t1).queryParam("from", today.toString()).queryParam("to", today.minusDays(1).toString())
                .get(HISTORY).then().statusCode(400).body("errors[0].field", equalTo("from"));

        // o check_log grava o agente e a organização
        assertThat(support.owner().queryForList("select checker_org_id from verification.check_log "
                + "where checker_user_id = ?", UUID.class, agent1.getId())).hasSize(2).containsOnly(police);
        assertThat(support.owner().queryForList("select checker_type from verification.check_log "
                + "where checker_user_id = ?", String.class, agent1.getId())).containsOnly("POLICE");
    }
}
