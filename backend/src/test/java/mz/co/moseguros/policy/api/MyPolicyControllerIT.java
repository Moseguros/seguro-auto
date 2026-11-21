package mz.co.moseguros.policy.api;

import static io.restassured.RestAssured.given;
import static mz.co.moseguros.policy.PolicyTestSupport.MY_POLICIES;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import mz.co.moseguros.SecurityTestConfiguration;
import mz.co.moseguros.SecurityTestConfiguration.IdentityFixtures;
import mz.co.moseguros.SecurityTestConfiguration.MutableClock;
import mz.co.moseguros.TestcontainersConfiguration;
import mz.co.moseguros.identity.IdentityApi;
import mz.co.moseguros.payment.PaymentFlowSupport.Seg;
import mz.co.moseguros.payment.application.IntentStateService;
import mz.co.moseguros.policy.PolicyTestSupport;
import mz.co.moseguros.policy.PolicyTestSupport.Row;
import mz.co.moseguros.rating.QuoteTestSupport.Customer;
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

/** {@code /me/policies} (S6-01 §10): só as apólices do próprio cliente; staff → 403. */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestcontainersConfiguration.class, SecurityTestConfiguration.class})
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MyPolicyControllerIT {

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
    Customer a;
    Customer b;
    UUID issued;
    UUID expired;
    UUID ofB;

    @BeforeAll
    void setUpAll() throws Exception {
        RestAssured.port = port;
        support = new PolicyTestSupport(fixtures, identityApi, clock, tx, states);
        insurer = support.setUp("Seguradora Apólices Cliente");
        a = support.payments().customer();
        b = support.payments().customer();
        issued = support.issued(a, insurer);
        support.awaitDocuments(issued);
        Instant now = Instant.now();
        expired = support.insert(Row.of(insurer.id(), a.id(), PolicyTestSupport.plate(), "EXPIRED",
                now.minus(Duration.ofDays(400)), now.minus(Duration.ofDays(35))));
        ofB = support.insert(Row.of(insurer.id(), b.id(), PolicyTestSupport.plate(), "ACTIVE",
                now.minus(Duration.ofDays(1)), now.plus(Duration.ofDays(300))));
    }

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Test
    void requiresACustomerToken() {
        given().get(MY_POLICIES).then().statusCode(401);
        String staff = support.payments().staffToken(insurer.id(), "INSURER_ADMIN");
        given().auth().oauth2(staff).get(MY_POLICIES).then().statusCode(403);
        given().auth().oauth2(staff).get(MY_POLICIES + "/{id}", issued).then().statusCode(403);
    }

    @Test
    void listsOnlyTheCustomersOwnPolicies() {
        JsonPath page = given().auth().oauth2(a.token()).get(MY_POLICIES)
                .then().statusCode(200).extract().jsonPath();
        assertThat(page.getList("content.id", String.class))
                .containsExactlyInAnyOrder(issued.toString(), expired.toString());
        assertThat(page.getLong("totalElements")).isEqualTo(2);
        assertThat(page.getInt("page")).isZero();
        assertThat(page.getInt("size")).isEqualTo(20);

        Map<String, Object> row = support.policyRow(issued);
        Map<String, Object> first = page.getMap("content.find { it.id == '" + issued + "' }");
        assertThat(first).containsEntry("number", row.get("number")).containsEntry("status", "ACTIVE")
                .containsEntry("coverageType", "MTPL_ONLY").containsEntry("certificateReady", true)
                .containsKey("coverStatus").containsKey("productName").containsKey("startAt").containsKey("endAt");
        assertThat(page.getString("content.find { it.id == '" + issued + "' }.insurer.id"))
                .isEqualTo(insurer.id().toString());
        assertThat(page.getString("content.find { it.id == '" + issued + "' }.insurer.name"))
                .isEqualTo(insurer.name());
        assertThat(page.getString("content.find { it.id == '" + issued + "' }.vehicle.plate"))
                .isEqualTo(row.get("plate"));
        assertThat(page.getString("content.find { it.id == '" + expired + "' }.coverStatus")).isEqualTo("ENDED");

        // filtro por estado
        given().auth().oauth2(a.token()).queryParam("status", "EXPIRED").get(MY_POLICIES)
                .then().statusCode(200).body("content.id", equalTo(List.of(expired.toString())));
        // a do cliente B nunca aparece
        given().auth().oauth2(a.token()).queryParam("size", 100).get(MY_POLICIES)
                .then().statusCode(200).body("content.id", not(hasItem(ofB.toString())));
    }

    @Test
    void readsTheDetailWithInstalmentsAndCertificate() {
        Map<String, Object> row = support.policyRow(issued);
        String token = support.owner().queryForObject(
                "select verification_token from policy.certificate where policy_id = ?", String.class, issued);
        JsonPath detail = given().auth().oauth2(a.token()).get(MY_POLICIES + "/{id}", issued)
                .then().statusCode(200).extract().jsonPath();
        assertThat(detail.getString("number")).isEqualTo(row.get("number"));
        assertThat(detail.getString("status")).isEqualTo("ACTIVE");
        assertThat(detail.getString("product.type")).isEqualTo("MTPL_ONLY");
        assertThat(detail.getString("product.versionId")).isEqualTo(row.get("product_version_id").toString());
        assertThat(detail.getString("renewalType")).isEqualTo("ANNUAL_RENEWABLE");
        assertThat(detail.getString("paymentPlan")).isEqualTo("FULL");
        assertThat(detail.getLong("totalPremium.amount")).isEqualTo(299_900L);
        assertThat(detail.getString("totalPremium.currency")).isEqualTo("MZN");
        assertThat(detail.getString("policyholderName")).isEqualTo(row.get("policyholder_name"));
        assertThat(detail.getString("vehicle.plate")).isEqualTo(row.get("plate"));
        assertThat(detail.getList("coverages.code", String.class)).contains("MTPL");
        assertThat(detail.getList("instalments")).hasSize(1);
        assertThat(detail.getString("instalments[0].status")).isEqualTo("PAID");
        assertThat(detail.getInt("instalments[0].seq")).isEqualTo(1);
        assertThat(detail.getString("instalments[0].dueDate")).matches("\\d{4}-\\d{2}-\\d{2}");
        assertThat(detail.getString("certificate.number")).matches("^CT-\\d{4}-\\d{7}$");
        assertThat(detail.getString("certificate.status")).isEqualTo("VALID");
        assertThat(detail.getString("certificate.verificationUrl"))
                .isEqualTo("https://verificar.moseguros.test/v/" + token);
        assertThat(detail.getString("certificate.fileId")).isEqualTo(support.owner().queryForObject(
                "select pdf_file_id from policy.certificate where policy_id = ?", UUID.class, issued).toString());
        assertThat(detail.getList("documents.type", String.class)).startsWith("CERTIFICATE", "SCHEDULE");
        assertThat(detail.getString("issuedAt")).isNotNull();
        // só a seguradora vê a linha temporal e a proposta; o código só vai no URL
        assertThat(detail.getList("events")).isNull();
        assertThat(detail.getMap("proposal")).isNull();
        assertThat(detail.prettify().split(token, -1)).hasSize(2);
    }

    @Test
    void thePolicyOfAnotherCustomerIsNotFound() {
        given().auth().oauth2(a.token()).get(MY_POLICIES + "/{id}", ofB)
                .then().statusCode(404).body("code", equalTo("POLICY_NOT_FOUND"));
        given().auth().oauth2(a.token()).get(MY_POLICIES + "/{id}", UUID.randomUUID())
                .then().statusCode(404).body("code", equalTo("POLICY_NOT_FOUND"));
        given().auth().oauth2(b.token()).get(MY_POLICIES + "/{id}", ofB).then().statusCode(200)
                .body("certificate", equalTo(null));
    }
}
