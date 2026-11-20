package mz.co.moseguros.policy.application;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import java.util.UUID;

import io.restassured.RestAssured;
import mz.co.moseguros.SecurityTestConfiguration;
import mz.co.moseguros.SecurityTestConfiguration.IdentityFixtures;
import mz.co.moseguros.SecurityTestConfiguration.MutableClock;
import mz.co.moseguros.TestcontainersConfiguration;
import mz.co.moseguros.identity.IdentityApi;
import mz.co.moseguros.payment.PaymentFlowSupport.Seg;
import mz.co.moseguros.payment.application.IntentStateService;
import mz.co.moseguros.policy.PolicyTestSupport;
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
 * Quem descarrega os PDF da apólice ({@code GET /api/v1/files/{id}}, dono {@code POLICY}, S6-02 §8): o
 * cliente tomador e a seguradora com {@code policy:read}; os outros recebem 404 (nunca 403), sem token
 * também; um token inválido dá 401.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestcontainersConfiguration.class, SecurityTestConfiguration.class})
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PolicyFileAccessIT {

    static final String FILES = "/api/v1/files/{id}";

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
    Seg other;
    Customer owner;
    UUID certificate;
    UUID schedule;

    @BeforeAll
    void setUpAll() throws Exception {
        RestAssured.port = port;
        support = new PolicyTestSupport(fixtures, identityApi, clock, tx, states);
        insurer = support.setUp("Seguradora Ficheiros");
        other = support.insurer("Outra Seguradora Ficheiros");
        owner = support.payments().customer();
        UUID policyId = support.issued(owner, insurer);
        UUID[] files = support.awaitDocuments(policyId);
        certificate = files[0];
        schedule = files[1];
    }

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        clock.reset();
    }

    @Test
    void theOwnerDownloadsTheDocuments() {
        for (UUID file : new UUID[] {certificate, schedule}) {
            given().auth().oauth2(owner.token()).get(FILES, file)
                    .then().statusCode(200)
                    .contentType("application/pdf")
                    .header("Cache-Control", "private, no-store");
        }
        given().auth().oauth2(owner.token()).queryParam("download", true).get(FILES, certificate)
                .then().statusCode(200)
                .header("Content-Disposition", org.hamcrest.Matchers.startsWith("attachment"));
    }

    @Test
    void anotherCustomerGetsNotFound() {
        Customer stranger = support.payments().customer();
        given().auth().oauth2(stranger.token()).get(FILES, certificate)
                .then().statusCode(404).body("code", equalTo("FILE_NOT_FOUND"));
    }

    @Test
    void theInsurerStaffWithPolicyReadDownloads() {
        given().auth().oauth2(insurer.underwriter()).get(FILES, certificate)
                .then().statusCode(200).contentType("application/pdf");
        given().auth().oauth2(insurer.finance()).get(FILES, schedule)
                .then().statusCode(200).contentType("application/pdf");
    }

    @Test
    void theStaffOfAnotherInsurerGetsNotFound() {
        given().auth().oauth2(other.underwriter()).get(FILES, certificate)
                .then().statusCode(404).body("code", equalTo("FILE_NOT_FOUND"));
    }

    @Test
    void aProductManagerWithoutPolicyReadGetsNotFound() {
        String productManager = support.payments().staffToken(insurer.id(), "PRODUCT_MANAGER");
        given().auth().oauth2(productManager).get(FILES, certificate)
                .then().statusCode(404).body("code", equalTo("FILE_NOT_FOUND"));
    }

    @Test
    void anAnonymousRequestGetsNotFound() {
        given().get(FILES, certificate).then().statusCode(404).body("code", equalTo("FILE_NOT_FOUND"));
    }

    @Test
    void anInvalidTokenIsUnauthorized() {
        given().auth().oauth2("not-a-valid-token").get(FILES, certificate).then().statusCode(401);
    }
}
