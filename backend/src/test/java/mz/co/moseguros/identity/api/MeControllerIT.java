package mz.co.moseguros.identity.api;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

import java.security.KeyPair;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import io.restassured.RestAssured;
import mz.co.moseguros.SecurityTestConfiguration;
import mz.co.moseguros.SecurityTestConfiguration.IdentityFixtures;
import mz.co.moseguros.TestcontainersConfiguration;
import mz.co.moseguros.identity.IdentityApi;
import mz.co.moseguros.identity.domain.UserAccount;
import mz.co.moseguros.organization.OrganizationType;
import mz.co.moseguros.organization.domain.Organization;
import mz.co.moseguros.shared.security.TestKeys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.test.context.ActiveProfiles;

/**
 * {@code GET /me}, a validação dos tokens de acesso e o JWKS público.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestcontainersConfiguration.class, SecurityTestConfiguration.class})
@ActiveProfiles("test")
class MeControllerIT {

    @LocalServerPort
    int port;

    @Autowired
    IdentityFixtures fixtures;

    @Autowired
    IdentityApi identityApi;

    @Autowired
    JwtEncoder encoder;

    @Autowired
    RSAKey signingKey;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Test
    void withoutTokenIsUnauthorized() {
        given().get("/api/v1/me")
                .then().statusCode(401)
                .contentType("application/problem+json")
                .body("code", equalTo("UNAUTHORIZED"));
    }

    @Test
    void customer() {
        UserAccount customer = fixtures.customer("Ana Mussa");
        String token = identityApi.issueTokens(customer, null).accessToken();

        given().auth().oauth2(token).get("/api/v1/me")
                .then().statusCode(200)
                .body("id", equalTo(customer.getId().toString()))
                .body("type", equalTo("CUSTOMER"))
                .body("displayName", equalTo("Ana Mussa"))
                .body("msisdn", equalTo(customer.getMsisdn().e164()))
                .body("msisdnVerified", equalTo(true))
                .body("$", not(hasKey("email")))
                .body("emailVerified", equalTo(false))
                .body("preferredLanguage", equalTo("pt"))
                .body("$", not(hasKey("organization")))
                .body("roles", contains("CUSTOMER"))
                .body("permissions", contains("self:profile", "self:vehicles"))
                .body("profileComplete", equalTo(false));
    }

    @Test
    void insurerStaff() {
        Organization insurer = fixtures.organization(OrganizationType.INSURER, "Seguradora Me");
        UserAccount staff = fixtures.staff("Bruno Staff", insurer.getId(), "INSURER_ADMIN");
        String token = identityApi.issueTokens(staff, null).accessToken();

        given().auth().oauth2(token).get("/api/v1/me")
                .then().statusCode(200)
                .body("id", equalTo(staff.getId().toString()))
                .body("type", equalTo("STAFF"))
                .body("displayName", equalTo("Bruno Staff"))
                .body("email", equalTo(staff.getEmail()))
                .body("$", not(hasKey("msisdn")))
                .body("organization.id", equalTo(insurer.getId().toString()))
                .body("organization.type", equalTo("INSURER"))
                .body("organization.displayName", equalTo("Seguradora Me"))
                .body("roles", contains("INSURER_ADMIN"))
                .body("permissions", contains("org-user:manage", "org-user:read", "product:manage", "product:read"))
                .body("$", not(hasKey("profileComplete")));
    }

    @Test
    void platformAdmin() {
        UserAccount admin = fixtures.staff("Admin", null, "PLATFORM_ADMIN");
        String token = identityApi.issueTokens(admin, null).accessToken();

        given().auth().oauth2(token).get("/api/v1/me")
                .then().statusCode(200)
                .body("type", equalTo("STAFF"))
                .body("organization", nullValue())
                .body("roles", contains("PLATFORM_ADMIN"));
    }

    @Test
    void tokenSignedWithAnotherKeyIsRejected() throws Exception {
        UserAccount customer = fixtures.customer("Carla");
        KeyPair other = TestKeys.rsa();
        RSAKey otherKey = new RSAKey.Builder((RSAPublicKey) other.getPublic()).privateKey(other.getPrivate())
                .keyID(signingKey.getKeyID()).keyUse(KeyUse.SIGNATURE).algorithm(JWSAlgorithm.RS256).build();
        String forged = token(new NimbusJwtEncoder(new ImmutableJWKSet<>(new JWKSet(otherKey))), customer,
                Instant.now(), "https://api.moseguros.co.mz", "moseguros-api");

        given().auth().oauth2(forged).get("/api/v1/me")
                .then().statusCode(401).body("code", equalTo("UNAUTHORIZED"));
    }

    @Test
    void expiredTokenIsRejected() {
        UserAccount customer = fixtures.customer("Dina");
        String expired = token(encoder, customer, Instant.now().minus(Duration.ofMinutes(16)),
                "https://api.moseguros.co.mz", "moseguros-api");

        given().auth().oauth2(expired).get("/api/v1/me")
                .then().statusCode(401).body("code", equalTo("UNAUTHORIZED"));
    }

    @Test
    void tokenWithinTheClockSkewIsAccepted() {
        UserAccount customer = fixtures.customer("Eva");
        String almostExpired = token(encoder, customer, Instant.now().minus(Duration.ofMinutes(15)).plusSeconds(-10),
                "https://api.moseguros.co.mz", "moseguros-api");

        given().auth().oauth2(almostExpired).get("/api/v1/me").then().statusCode(200);
    }

    @Test
    void wrongIssuerOrAudienceIsRejected() {
        UserAccount customer = fixtures.customer("Filipa");
        given().auth().oauth2(token(encoder, customer, Instant.now(), "https://outro.example", "moseguros-api"))
                .get("/api/v1/me").then().statusCode(401);
        given().auth().oauth2(token(encoder, customer, Instant.now(), "https://api.moseguros.co.mz", "outra-api"))
                .get("/api/v1/me").then().statusCode(401);
    }

    @Test
    void jwksIsPublicAndHasOnlyThePublicKey() {
        given().header("Authorization", "Bearer lixo").get("/.well-known/jwks.json")
                .then().statusCode(200)
                .body("keys.size()", equalTo(1))
                .body("keys[0].kid", equalTo(signingKey.getKeyID()))
                .body("keys[0].kty", equalTo("RSA"))
                .body("keys[0].alg", equalTo("RS256"))
                .body("keys[0].use", equalTo("sig"))
                .body("keys[0].n", equalTo(signingKey.getModulus().toString()))
                .body("keys[0].e", equalTo("AQAB"))
                .body("keys[0]", not(hasKey("d")));
    }

    // --- PUT /me (S1-06) ---

    @Test
    void customerChangesTheLanguageAndTheNameIsIgnored() {
        UserAccount customer = fixtures.customer("Ana Mussa");
        String token = identityApi.issueTokens(customer, null).accessToken();

        given().auth().oauth2(token).contentType("application/json")
                .body(Map.of("displayName", "Outro Nome", "preferredLanguage", "en"))
                .put("/api/v1/me")
                .then().statusCode(200)
                .body("preferredLanguage", equalTo("en"))
                .body("displayName", equalTo("Ana Mussa"));
        assertThat(fixtures.owner().queryForObject(
                "select coalesce(display_name, '-') from identity.user_account where id = ?", String.class,
                customer.getId())).isEqualTo("-");
    }

    @Test
    void staffChangesTheNameAndTheLanguage() {
        UserAccount staff = fixtures.staff("Bruno Staff", null, "PLATFORM_ADMIN");
        String token = identityApi.issueTokens(staff, null).accessToken();

        given().auth().oauth2(token).contentType("application/json")
                .body(Map.of("displayName", " Bruno Novo ", "preferredLanguage", "en"))
                .put("/api/v1/me")
                .then().statusCode(200)
                .body("displayName", equalTo("Bruno Novo"))
                .body("preferredLanguage", equalTo("en"));
        given().auth().oauth2(token).contentType("application/json")
                .body(Map.of("preferredLanguage", "pt"))
                .put("/api/v1/me")
                .then().statusCode(200)
                .body("displayName", equalTo("Bruno Novo"))
                .body("preferredLanguage", equalTo("pt"));
    }

    @Test
    void updateMeValidation() {
        UserAccount staff = fixtures.staff("Bruno Staff", null, "PLATFORM_ADMIN");
        String token = identityApi.issueTokens(staff, null).accessToken();

        given().auth().oauth2(token).contentType("application/json")
                .body(Map.of("preferredLanguage", "fr"))
                .put("/api/v1/me")
                .then().statusCode(400)
                .body("code", equalTo("VALIDATION_ERROR"))
                .body("errors[0].field", equalTo("preferredLanguage"));
        given().auth().oauth2(token).contentType("application/json")
                .body(Map.of("displayName", "B", "preferredLanguage", "pt"))
                .put("/api/v1/me")
                .then().statusCode(400)
                .body("errors[0].field", equalTo("displayName"))
                .body("errors[0].code", equalTo("SIZE"));
        given().contentType("application/json").body(Map.of("preferredLanguage", "pt")).put("/api/v1/me")
                .then().statusCode(401);
    }

    private static String token(JwtEncoder encoder, UserAccount account, Instant issuedAt, String issuer,
                                String audience) {
        return encoder.encode(JwtEncoderParameters.from(
                JwsHeader.with(SignatureAlgorithm.RS256).keyId("dev").build(),
                JwtClaimsSet.builder().issuer(issuer).audience(List.of(audience))
                        .subject(account.getId().toString()).issuedAt(issuedAt)
                        .expiresAt(issuedAt.plus(Duration.ofMinutes(15)))
                        .claim("type", "CUSTOMER").claim("roles", List.of("CUSTOMER"))
                        .claim("permissions", List.of("self:profile")).build())).getTokenValue();
    }
}
