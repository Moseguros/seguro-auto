package mz.co.moseguros.identity.api;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.time.Duration;
import java.util.List;

import com.nimbusds.jose.jwk.RSAKey;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import mz.co.moseguros.SecurityTestConfiguration;
import mz.co.moseguros.SecurityTestConfiguration.IdentityFixtures;
import mz.co.moseguros.TestcontainersConfiguration;
import mz.co.moseguros.identity.IdentityApi;
import mz.co.moseguros.identity.domain.UserAccount;
import mz.co.moseguros.organization.OrganizationType;
import mz.co.moseguros.organization.domain.Organization;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.test.context.ActiveProfiles;

/**
 * Token de serviço do {@code payment-file-worker} (S5-09 §1): {@code client_credentials} com Basic, erros
 * do RFC 6749 e separação entre os tokens de serviço ({@code /internal/**}) e os de utilizador
 * ({@code /api/**}).
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestcontainersConfiguration.class, SecurityTestConfiguration.class})
@ActiveProfiles("test")
class ServiceTokenIT {

    static final String TOKEN = "/internal/v1/auth/token";
    static final String SOURCES = "/internal/v1/payment-file-sources";
    static final String CLIENT = "payment-file-worker";
    static final String SECRET = "test-worker-secret-only-for-tests-0001";

    @LocalServerPort
    int port;

    @Autowired
    IdentityFixtures fixtures;

    @Autowired
    IdentityApi identityApi;

    @Autowired
    RSAKey signingKey;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    static Response token(String clientId, String secret, String grantType, String scope) {
        var request = given().auth().preemptive().basic(clientId, secret)
                .contentType("application/x-www-form-urlencoded")
                .formParam("grant_type", grantType);
        if (scope != null) {
            request = request.formParam("scope", scope);
        }
        return request.post(TOKEN);
    }

    static String serviceToken() {
        return token(CLIENT, SECRET, "client_credentials", null).then().statusCode(200)
                .extract().path("access_token");
    }

    @Test
    void validCredentialsGiveAServiceToken() throws Exception {
        Response r = token(CLIENT, SECRET, "client_credentials", "payments:file-sources");
        r.then().statusCode(200)
                .header("Cache-Control", "no-store")
                .body("token_type", equalTo("Bearer"))
                .body("expires_in", equalTo(900))
                .body("scope", equalTo("payments:file-sources"));

        Jwt jwt = NimbusJwtDecoder.withPublicKey(signingKey.toRSAPublicKey()).build()
                .decode(r.path("access_token"));
        assertThat(jwt.getAudience()).containsExactly("moseguros-internal");
        assertThat(jwt.getClaimAsString("type")).isEqualTo("SERVICE");
        assertThat(jwt.getSubject()).isEqualTo(CLIENT);
        assertThat(jwt.getClaimAsString("scope")).isEqualTo("payments:file-sources");
        assertThat(jwt.getClaimAsStringList("permissions")).containsExactly("payments:file-sources");
        assertThat(jwt.getIssuer().toString()).isEqualTo("https://api.moseguros.co.mz");
        assertThat(Duration.between(jwt.getIssuedAt(), jwt.getExpiresAt())).isEqualTo(Duration.ofMinutes(15));
        assertThat(jwt.getId()).isNotBlank();
    }

    @Test
    void scopeIsOptional() {
        token(CLIENT, SECRET, "client_credentials", null).then().statusCode(200)
                .body("scope", equalTo("payments:file-sources"));
    }

    @Test
    void wrongSecretIsInvalidClient() {
        token(CLIENT, SECRET + "x", "client_credentials", null).then().statusCode(401)
                .header("WWW-Authenticate", "Basic realm=\"moseguros-internal\"")
                .contentType("application/json")
                .body("error", equalTo("invalid_client"));
        token("unknown-client", SECRET, "client_credentials", null).then().statusCode(401)
                .body("error", equalTo("invalid_client"));
    }

    @Test
    void withoutBasicIsInvalidClient() {
        given().contentType("application/x-www-form-urlencoded").formParam("grant_type", "client_credentials")
                .post(TOKEN)
                .then().statusCode(401)
                .body("error", equalTo("invalid_client"));
    }

    @Test
    void passwordGrantIsUnsupported() {
        token(CLIENT, SECRET, "password", null).then().statusCode(400)
                .body("error", equalTo("unsupported_grant_type"));
    }

    @Test
    void unknownScopeIsInvalid() {
        token(CLIENT, SECRET, "client_credentials", "x").then().statusCode(400)
                .body("error", equalTo("invalid_scope"));
    }

    @Test
    void malformedBodyIsInvalidRequest() {
        given().auth().preemptive().basic(CLIENT, SECRET).contentType("application/json").body("{}")
                .post(TOKEN)
                .then().statusCode(400)
                .body("error", equalTo("invalid_request"));
        given().auth().preemptive().basic(CLIENT, SECRET).contentType("application/x-www-form-urlencoded")
                .formParam("scope", "payments:file-sources")
                .post(TOKEN)
                .then().statusCode(400)
                .body("error", equalTo("invalid_request"));
    }

    @Test
    void serviceTokenIsRefusedOnTheApi() {
        given().auth().oauth2(serviceToken()).get("/api/v1/me")
                .then().statusCode(401)
                .contentType("application/problem+json");
    }

    @Test
    void serviceTokenOpensTheInternalSources() {
        given().auth().oauth2(serviceToken()).get(SOURCES)
                .then().statusCode(200);
    }

    @Test
    void userTokensAreRefusedOnInternalPaths() {
        UserAccount customer = fixtures.customer("Cliente Interno");
        given().auth().oauth2(identityApi.issueTokens(customer, null).accessToken()).get(SOURCES)
                .then().statusCode(401);

        Organization platformOrg = fixtures.organization(OrganizationType.INSURER, "Seg Interna");
        UserAccount staff = fixtures.staff("Admin Interno", null, "PLATFORM_ADMIN");
        given().auth().oauth2(identityApi.issueTokens(staff, null).accessToken()).get(SOURCES)
                .then().statusCode(401);
        UserAccount insurerAdmin = fixtures.staff("Admin Seg", platformOrg.getId(), "INSURER_ADMIN");
        given().auth().oauth2(identityApi.issueTokens(insurerAdmin, null).accessToken()).get(SOURCES)
                .then().statusCode(401);
    }

    @Test
    void withoutTokenIsUnauthorized() {
        given().get(SOURCES).then().statusCode(401).contentType("application/problem+json");
        given().auth().oauth2("not-a-jwt").get(SOURCES).then().statusCode(401);
    }

    @Test
    void tokenEndpointOnlyAcceptsPost() {
        assertThat(List.of(given().get(TOKEN).statusCode())).containsAnyOf(401, 405);
    }
}
