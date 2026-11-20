package mz.co.moseguros.policy.api;

import static io.restassured.RestAssured.given;
import static mz.co.moseguros.policy.PolicyTestSupport.INSURER_POLICIES;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
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

/**
 * {@code /insurer/policies} (S6-01 §10): {@code policy:read}, filtros e isolamento entre seguradoras.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestcontainersConfiguration.class, SecurityTestConfiguration.class})
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class InsurerPolicyControllerIT {

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
    Seg a;
    Seg b;
    Customer customer;
    UUID issued;
    UUID active;
    UUID expired;
    UUID ofB;
    String activePlate;

    @BeforeAll
    void setUpAll() throws Exception {
        RestAssured.port = port;
        support = new PolicyTestSupport(fixtures, identityApi, clock, tx, states);
        a = support.setUp("Seguradora Apólices A");
        b = support.insurer("Seguradora Apólices B");
        customer = support.payments().customer();
        issued = support.issued(customer, a);
        support.awaitDocuments(issued);
        Instant now = Instant.now();
        activePlate = PolicyTestSupport.plate();
        active = support.insert(Row.of(a.id(), customer.id(), activePlate, "ACTIVE", now.minus(Duration.ofDays(2)),
                now.plus(Duration.ofDays(300))).named("Joana Teste Filtro"));
        expired = support.insert(Row.of(a.id(), customer.id(), PolicyTestSupport.plate(), "EXPIRED",
                now.minus(Duration.ofDays(400)), now.minus(Duration.ofDays(35))).named("Carlos Expirado"));
        ofB = support.insert(Row.of(b.id(), customer.id(), activePlate, "ACTIVE", now.minus(Duration.ofDays(1)),
                now.plus(Duration.ofDays(300))).named("Joana Teste Filtro"));
    }

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    List<String> ids(String token, Map<String, Object> query) {
        return given().auth().oauth2(token).queryParams(query).queryParam("size", 100).get(INSURER_POLICIES)
                .then().statusCode(200).extract().jsonPath().getList("content.id", String.class);
    }

    @Test
    void requiresPolicyRead() {
        given().get(INSURER_POLICIES).then().statusCode(401);
        String productManager = support.payments().staffToken(a.id(), "PRODUCT_MANAGER");
        given().auth().oauth2(productManager).get(INSURER_POLICIES).then().statusCode(403);
        given().auth().oauth2(productManager).get(INSURER_POLICIES + "/{id}", issued).then().statusCode(403);
        given().auth().oauth2(customer.token()).get(INSURER_POLICIES).then().statusCode(403);
        given().auth().oauth2(customer.token()).get(INSURER_POLICIES + "/{id}", issued).then().statusCode(403);
        for (String role : List.of("UNDERWRITER", "CLAIMS_HANDLER", "FINANCE")) {
            given().auth().oauth2(support.payments().staffToken(a.id(), role)).get(INSURER_POLICIES)
                    .then().statusCode(200);
        }
    }

    @Test
    void listsOnlyTheInsurersPolicies() {
        assertThat(ids(a.admin(), Map.of())).containsExactlyInAnyOrder(issued.toString(), active.toString(),
                expired.toString());
        assertThat(ids(b.admin(), Map.of())).containsExactly(ofB.toString());

        JsonPath page = given().auth().oauth2(a.admin()).queryParam("q", activePlate).get(INSURER_POLICIES)
                .then().statusCode(200).extract().jsonPath();
        assertThat(page.getMap("content[0]")).containsEntry("id", active.toString())
                .containsEntry("status", "ACTIVE").containsEntry("coverStatus", "COVERED")
                .containsEntry("policyholderName", "Joana Teste Filtro").containsEntry("plate", activePlate)
                .containsEntry("productName", "RC Teste").containsEntry("coverageType", "MTPL_ONLY")
                .containsKey("startAt").containsKey("endAt").containsKey("issuedAt");
        assertThat(page.getLong("content[0].totalPremium.amount")).isEqualTo(299_900L);
    }

    @Test
    void filtersByStatusNumberPlateAndName() {
        assertThat(ids(a.admin(), Map.of("status", "EXPIRED"))).containsExactly(expired.toString());
        assertThat(ids(a.admin(), Map.of("status", "ACTIVE"))).containsExactlyInAnyOrder(issued.toString(),
                active.toString());
        String number = (String) support.policyRow(issued).get("number");
        assertThat(ids(a.admin(), Map.of("q", number))).containsExactly(issued.toString());
        assertThat(ids(a.admin(), Map.of("q", number.toLowerCase()))).containsExactly(issued.toString());
        // matrícula sem hífens: normalizada pelos formatos activos
        assertThat(ids(a.admin(), Map.of("q", activePlate.replace("-", " ").toLowerCase())))
                .containsExactly(active.toString());
        assertThat(ids(a.admin(), Map.of("q", "joana teste"))).containsExactly(active.toString());
        assertThat(ids(a.admin(), Map.of("q", "Expirado"))).containsExactly(expired.toString());
        assertThat(ids(a.admin(), Map.of("q", "%"))).isEmpty();
        assertThat(ids(a.admin(), Map.of("productType", "MTPL_ONLY"))).hasSize(3);
        assertThat(ids(a.admin(), Map.of("productType", "COMPREHENSIVE"))).isEmpty();
        LocalDate today = LocalDate.now(ZoneId.of("Africa/Maputo"));
        assertThat(ids(a.admin(), Map.of("issuedFrom", today.toString(), "issuedTo", today.toString())))
                .hasSize(3);
        assertThat(ids(a.admin(), Map.of("issuedTo", today.minusDays(1).toString()))).isEmpty();
        given().auth().oauth2(a.admin()).queryParam("sort", "plate").get(INSURER_POLICIES).then().statusCode(400);
    }

    @Test
    void readsTheDetailWithTheTimelineAndTheProposal() {
        Map<String, Object> proposal = support.owner().queryForMap(
                "select p.id, p.number from proposal.proposal p join policy.policy x on x.proposal_id = p.id "
                        + "where x.id = ?", issued);
        JsonPath detail = given().auth().oauth2(a.underwriter()).get(INSURER_POLICIES + "/{id}", issued)
                .then().statusCode(200).extract().jsonPath();
        assertThat(detail.getString("id")).isEqualTo(issued.toString());
        assertThat(detail.getString("insurer.name")).isEqualTo(a.name());
        assertThat(detail.getList("events.type", String.class)).containsExactlyInAnyOrder("ISSUED",
                "CERTIFICATE_ISSUED", "DOCUMENTS_READY");
        assertThat(detail.getList("events.actorType", String.class)).containsOnly("SYSTEM");
        assertThat(detail.getString("proposal.id")).isEqualTo(proposal.get("id").toString());
        assertThat(detail.getString("proposal.number")).isEqualTo(proposal.get("number"));
        assertThat(detail.getString("certificate.verificationUrl")).startsWith("https://verificar.moseguros.test/v/");
    }

    @Test
    void thePoliciesOfAnotherInsurerAreNotFound() {
        given().auth().oauth2(b.admin()).get(INSURER_POLICIES + "/{id}", issued)
                .then().statusCode(404).body("code", equalTo("POLICY_NOT_FOUND"));
        given().auth().oauth2(a.admin()).get(INSURER_POLICIES + "/{id}", ofB)
                .then().statusCode(404).body("code", equalTo("POLICY_NOT_FOUND"));
        given().auth().oauth2(b.finance()).get(INSURER_POLICIES + "/{id}", ofB).then().statusCode(200);
        // um mediador (ORG sem seguradora) não vê apólices neste sprint
        String broker = support.payments().broker("Corretora Apólices").token();
        given().auth().oauth2(broker).get(INSURER_POLICIES).then().statusCode(403);
    }
}
