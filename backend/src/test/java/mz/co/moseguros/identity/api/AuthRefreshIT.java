package mz.co.moseguros.identity.api;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import mz.co.moseguros.SecurityTestConfiguration;
import mz.co.moseguros.SecurityTestConfiguration.IdentityFixtures;
import mz.co.moseguros.SecurityTestConfiguration.MutableClock;
import mz.co.moseguros.TestcontainersConfiguration;
import mz.co.moseguros.identity.IdentityApi;
import mz.co.moseguros.identity.TokenPair;
import mz.co.moseguros.identity.application.SessionService;
import mz.co.moseguros.identity.domain.RefreshToken.RevokedReason;
import mz.co.moseguros.identity.domain.UserAccount;
import mz.co.moseguros.organization.OrganizationType;
import mz.co.moseguros.organization.domain.Organization;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.test.context.ActiveProfiles;

/**
 * {@code POST /auth/refresh} e {@code POST /auth/logout}: rotação, reutilização, limite da
 * sessão de staff, logout e pedidos simultâneos.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestcontainersConfiguration.class, SecurityTestConfiguration.class})
@ActiveProfiles("test")
class AuthRefreshIT {

    @LocalServerPort
    int port;

    @Autowired
    IdentityFixtures fixtures;

    @Autowired
    IdentityApi identityApi;

    @Autowired
    SessionService sessions;

    @Autowired
    MutableClock clock;

    @Autowired
    JwtEncoder encoder;

    JdbcTemplate owner;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        owner = fixtures.owner();
    }

    @AfterEach
    void resetClock() {
        clock.reset();
    }

    @Test
    void refreshRotatesTheTokenAndExtendsTheCustomerSession() {
        UserAccount customer = fixtures.customer("Ana Mussa");
        TokenPair login = identityApi.issueTokens(customer, "JUnit");

        Response response = refresh(login.refreshToken());

        response.then().statusCode(200)
                .body("expiresIn", equalTo(900))
                .body("refreshToken", not(equalTo(login.refreshToken())))
                .body("accessToken", not(equalTo(login.accessToken())));
        String newRefresh = response.path("refreshToken");
        String newAccess = response.path("accessToken");
        given().auth().oauth2(newAccess).get("/api/v1/me").then().statusCode(200)
                .body("id", equalTo(customer.getId().toString()));

        Map<String, Object> old = row(login.refreshToken());
        Map<String, Object> next = row(newRefresh);
        assertThat(old.get("revoked_reason")).isEqualTo("ROTATED");
        assertThat(old.get("replaced_by_id")).isEqualTo(next.get("id"));
        assertThat(next.get("family_id")).isEqualTo(old.get("family_id"));
        assertThat(next.get("revoked_at")).isNull();
        assertThat(next.get("session_expires_at")).isNull();
        Instant expiresAt = ((Timestamp) next.get("expires_at")).toInstant();
        assertThat(expiresAt).isBetween(Instant.now().plus(Duration.ofDays(60)).minusSeconds(60),
                Instant.now().plus(Duration.ofDays(60)).plusSeconds(60));
    }

    @Test
    void reuseRevokesTheWholeFamily() {
        UserAccount customer = fixtures.customer("Bruno");
        TokenPair login = identityApi.issueTokens(customer, null);
        String second = refresh(login.refreshToken()).then().statusCode(200).extract().path("refreshToken");

        refresh(login.refreshToken()).then().statusCode(401)
                .contentType("application/problem+json")
                .body("code", equalTo("REFRESH_TOKEN_REUSED"));

        assertThat(row(second).get("revoked_reason")).isEqualTo("REUSE_DETECTED");
        refresh(second).then().statusCode(401).body("code", equalTo("REFRESH_TOKEN_INVALID"));
        refresh(login.refreshToken()).then().statusCode(401).body("code", equalTo("REFRESH_TOKEN_REUSED"));
        assertThat(owner.queryForObject(
                "select count(*) from identity.refresh_token where family_id = ? and revoked_at is null",
                Integer.class, row(second).get("family_id"))).isZero();
    }

    @Test
    void staffSessionDoesNotGoBeyondTwelveHours() {
        Organization insurer = fixtures.organization(OrganizationType.INSURER, "Seg Doze");
        UserAccount staff = fixtures.staff("Carla", insurer.getId(), "INSURER_ADMIN");
        Instant loginAt = clock.instant();
        TokenPair login = identityApi.issueTokens(staff, null);
        Instant sessionEnd = ((Timestamp) row(login.refreshToken()).get("session_expires_at")).toInstant();
        assertThat(Duration.between(loginAt, sessionEnd)).isBetween(Duration.ofHours(12).minusSeconds(5),
                Duration.ofHours(12).plusSeconds(5));

        clock.advance(Duration.ofHours(11));
        String next = refresh(login.refreshToken()).then().statusCode(200).extract().path("refreshToken");
        Map<String, Object> nextRow = row(next);
        assertThat(nextRow.get("expires_at")).isEqualTo(nextRow.get("session_expires_at"));
        assertThat(((Timestamp) nextRow.get("expires_at")).toInstant()).isEqualTo(sessionEnd);

        clock.advance(Duration.ofHours(1).plusSeconds(1));
        refresh(next).then().statusCode(401).body("code", equalTo("REFRESH_TOKEN_INVALID"));
    }

    @Test
    void expiredCustomerTokenIsInvalid() {
        TokenPair login = identityApi.issueTokens(fixtures.customer("Dina"), null);
        clock.advance(Duration.ofDays(60).plusSeconds(1));

        refresh(login.refreshToken()).then().statusCode(401).body("code", equalTo("REFRESH_TOKEN_INVALID"));
    }

    @Test
    void unknownTokenOrInactiveAccountIsInvalid() {
        refresh("nao-existe").then().statusCode(401).body("code", equalTo("REFRESH_TOKEN_INVALID"));

        UserAccount customer = fixtures.customer("Eva");
        TokenPair login = identityApi.issueTokens(customer, null);
        fixtures.setAccountStatus(customer.getId(), "DISABLED");
        refresh(login.refreshToken()).then().statusCode(401).body("code", equalTo("REFRESH_TOKEN_INVALID"));
    }

    @Test
    void blankTokenIsAValidationError() {
        given().contentType(ContentType.JSON).body(Map.of("refreshToken", ""))
                .post("/api/v1/auth/refresh")
                .then().statusCode(400)
                .body("code", equalTo("VALIDATION_ERROR"))
                .body("errors[0].field", equalTo("refreshToken"))
                .body("errors[0].code", equalTo("NOT_BLANK"));
    }

    @Test
    void suspendedOrganizationRevokesTheStaffSession() {
        Organization broker = fixtures.organization(OrganizationType.BROKER, "Corretor Susp");
        UserAccount staff = fixtures.staff("Filipe", broker.getId(), "ORG_ADMIN");
        TokenPair login = identityApi.issueTokens(staff, null);
        fixtures.suspendOrganization(broker.getId());

        refresh(login.refreshToken()).then().statusCode(401).body("code", equalTo("REFRESH_TOKEN_INVALID"));
        assertThat(row(login.refreshToken()).get("revoked_reason")).isEqualTo("ORG_SUSPENDED");
    }

    @Test
    void logoutRevokesTheFamily() {
        TokenPair login = identityApi.issueTokens(fixtures.customer("Gil"), null);
        String second = refresh(login.refreshToken()).then().statusCode(200).extract().path("refreshToken");

        given().contentType(ContentType.JSON).body(Map.of("refreshToken", second))
                .post("/api/v1/auth/logout").then().statusCode(204);

        assertThat(row(second).get("revoked_reason")).isEqualTo("LOGOUT");
        refresh(second).then().statusCode(401).body("code", equalTo("REFRESH_TOKEN_INVALID"));
    }

    @Test
    void logoutAlwaysAnswersNoContent() {
        given().contentType(ContentType.JSON).body(Map.of("refreshToken", "desconhecido"))
                .post("/api/v1/auth/logout").then().statusCode(204);
        given().contentType(ContentType.JSON).body("{}")
                .post("/api/v1/auth/logout").then().statusCode(204);
        given().contentType(ContentType.JSON).post("/api/v1/auth/logout").then().statusCode(204);
    }

    @Test
    void concurrentRefreshesWithTheSameTokenGiveOneSuccessAndOneReuse() throws Exception {
        TokenPair login = identityApi.issueTokens(fixtures.customer("Helena"), null);
        CountDownLatch start = new CountDownLatch(1);
        Callable<Response> call = () -> {
            start.await();
            return refresh(login.refreshToken());
        };
        ExecutorService pool = Executors.newFixedThreadPool(2);
        try {
            List<Future<Response>> futures = new ArrayList<>(List.of(pool.submit(call), pool.submit(call)));
            start.countDown();
            List<Response> responses = new ArrayList<>();
            for (Future<Response> future : futures) {
                responses.add(future.get(30, TimeUnit.SECONDS));
            }
            assertThat(responses).extracting(Response::statusCode).containsExactlyInAnyOrder(200, 401);
            Response failed = responses.stream().filter(r -> r.statusCode() == 401).findFirst().orElseThrow();
            assertThat(failed.<String>path("code")).isEqualTo("REFRESH_TOKEN_REUSED");
        } finally {
            pool.shutdownNow();
        }
        assertThat(owner.queryForObject("select count(*) from identity.refresh_token where family_id = ?",
                Integer.class, row(login.refreshToken()).get("family_id"))).isEqualTo(2);
    }

    @Test
    void refreshIgnoresAnExpiredBearerHeader() {
        UserAccount customer = fixtures.customer("Ivo");
        TokenPair login = identityApi.issueTokens(customer, null);
        Instant past = Instant.now().minus(Duration.ofHours(1));
        String expired = encoder.encode(JwtEncoderParameters.from(
                JwsHeader.with(SignatureAlgorithm.RS256).keyId("dev").build(),
                JwtClaimsSet.builder().issuer("https://api.moseguros.co.mz").audience(List.of("moseguros-api"))
                        .subject(customer.getId().toString()).issuedAt(past).expiresAt(past.plusSeconds(900))
                        .claim("type", "CUSTOMER").build())).getTokenValue();
        given().auth().oauth2(expired).get("/api/v1/me").then().statusCode(401);

        given().auth().oauth2(expired).contentType(ContentType.JSON)
                .body(Map.of("refreshToken", login.refreshToken()))
                .post("/api/v1/auth/refresh")
                .then().statusCode(200);
    }

    @Test
    void revokeAllForUserRevokesEverySession() {
        UserAccount customer = fixtures.customer("Joana");
        TokenPair first = identityApi.issueTokens(customer, "telemovel");
        TokenPair second = identityApi.issueTokens(customer, "browser");

        assertThat(sessions.revokeAllForUser(customer.getId(), RevokedReason.ACCESS_RESET)).isEqualTo(2);

        assertThat(row(first.refreshToken()).get("revoked_reason")).isEqualTo("ACCESS_RESET");
        refresh(second.refreshToken()).then().statusCode(401).body("code", equalTo("REFRESH_TOKEN_INVALID"));
    }

    private Response refresh(String refreshToken) {
        return given().contentType(ContentType.JSON).body(Map.of("refreshToken", refreshToken))
                .post("/api/v1/auth/refresh");
    }

    private Map<String, Object> row(String refreshToken) {
        return owner.queryForMap("select * from identity.refresh_token where token_hash = encode(sha256(convert_to(?, 'UTF8')), 'hex')",
                refreshToken);
    }
}
