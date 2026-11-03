package mz.co.moseguros.organization.api;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.nullValue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import mz.co.moseguros.SecurityTestConfiguration;
import mz.co.moseguros.SecurityTestConfiguration.IdentityFixtures;
import mz.co.moseguros.TestcontainersConfiguration;
import mz.co.moseguros.catalog.CatalogTestSupport;
import mz.co.moseguros.catalog.CatalogTestSupport.Insurer;
import mz.co.moseguros.identity.IdentityApi;
import mz.co.moseguros.organization.OrganizationType;
import mz.co.moseguros.organization.application.RatingIntegrationService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.AopTestUtils;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * Ligação da plataforma à tarifação de uma seguradora (S3-02 §9): mudar para {@code EXTERNAL},
 * campos obrigatórios e formatos, {@code http://} só com {@code allow-http}, organização que não é
 * seguradora, permissões, concorrência e auditoria.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestcontainersConfiguration.class, SecurityTestConfiguration.class})
@ActiveProfiles("test")
class RatingIntegrationIT {

    static final String PATH = "/api/v1/organizations/{id}/rating-integration";

    @LocalServerPort
    int port;

    @Autowired
    IdentityFixtures fixtures;

    @Autowired
    IdentityApi identityApi;

    @Autowired
    RatingIntegrationService service;

    CatalogTestSupport support;
    Insurer insurer;
    String admin;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        support = new CatalogTestSupport(fixtures, identityApi);
        insurer = support.insurer("Seguradora Ligação " + UUID.randomUUID().toString().substring(0, 4));
        admin = support.staffToken(null, "PLATFORM_ADMIN");
    }

    @AfterEach
    void restoreAllowHttp() {
        allowHttp(true);
    }

    void allowHttp(boolean allow) {
        Object target = AopTestUtils.getTargetObject(service);
        ReflectionTestUtils.setField(target, "allowHttp", allow);
    }

    long version(UUID id) {
        return given().auth().oauth2(admin).get(PATH, id).then().statusCode(200).extract().jsonPath().getLong("version");
    }

    Response put(String token, UUID id, String mode, String url, String ref, long version) {
        Map<String, Object> body = new HashMap<>();
        body.put("ratingMode", mode);
        body.put("ratingEndpointUrl", url);
        body.put("ratingSecretRef", ref);
        body.put("version", version);
        return given().auth().oauth2(token).contentType(ContentType.JSON).body(body).put(PATH, id);
    }

    @Test
    void theAdminSwitchesToExternalAndBackKeepingTheFields() {
        given().auth().oauth2(admin).get(PATH, insurer.id()).then().statusCode(200)
                .body("ratingMode", equalTo("INTERNAL"))
                .body("ratingEndpointUrl", nullValue())
                .body("ratingSecretRef", nullValue());
        long v0 = version(insurer.id());
        put(admin, insurer.id(), "EXTERNAL", "https://cotacoes.seguradora.co.mz/moseguros", "config:demox", v0)
                .then().statusCode(200)
                .body("ratingMode", equalTo("EXTERNAL"))
                .body("ratingEndpointUrl", equalTo("https://cotacoes.seguradora.co.mz/moseguros"))
                .body("ratingSecretRef", equalTo("config:demox"))
                .body("version", equalTo((int) v0 + 1));
        Map<String, Object> row = support.owner().queryForMap("""
                select rating_mode, rating_endpoint_url, rating_secret_ref from organization.insurer_profile
                 where organization_id = ?""", insurer.id());
        assertThat(row.get("rating_mode")).isEqualTo("EXTERNAL");
        assertThat(row.get("rating_endpoint_url")).isEqualTo("https://cotacoes.seguradora.co.mz/moseguros");

        put(admin, insurer.id(), "INTERNAL", null, null, v0 + 1).then().statusCode(200)
                .body("ratingMode", equalTo("INTERNAL"))
                .body("ratingEndpointUrl", equalTo("https://cotacoes.seguradora.co.mz/moseguros"))
                .body("ratingSecretRef", equalTo("config:demox"));

        List<Map<String, Object>> audit = support.owner().queryForList("""
                select actor_type, insurer_id, entity_type, before_state ->> 'ratingMode' as before_mode,
                       after_state ->> 'ratingMode' as after_mode, after_state::text as after
                  from audit.audit_event where action = 'RATING_INTEGRATION.UPDATE' and insurer_id = ?
                 order by occurred_at, id""", insurer.id());
        assertThat(audit).hasSize(2);
        assertThat(audit.get(0).get("actor_type")).isEqualTo("STAFF");
        assertThat(audit.get(0).get("entity_type")).isEqualTo("InsurerProfile");
        assertThat(audit.get(0).get("before_mode")).isEqualTo("INTERNAL");
        assertThat(audit.get(0).get("after_mode")).isEqualTo("EXTERNAL");
        assertThat((String) audit.get(0).get("after")).contains("config:demox");
        assertThat(audit.get(1).get("after_mode")).isEqualTo("INTERNAL");
    }

    @Test
    void validation() {
        long v = version(insurer.id());
        put(admin, insurer.id(), "EXTERNAL", null, "config:demox", v).then().statusCode(400)
                .body("code", equalTo("VALIDATION_ERROR"))
                .body("errors[0].field", equalTo("ratingEndpointUrl"))
                .body("errors[0].code", equalTo("REQUIRED"));
        put(admin, insurer.id(), "EXTERNAL", "https://x.co.mz/r", null, v).then().statusCode(400)
                .body("errors[0].field", equalTo("ratingSecretRef"))
                .body("errors[0].code", equalTo("REQUIRED"));
        put(admin, insurer.id(), "EXTERNAL", "ftp://x.co.mz/r", "secret/demox", v).then().statusCode(400)
                .body("errors.code", hasItem("INVALID_URL"))
                .body("errors.code", hasItem("INVALID_SECRET_REF"));
        put(admin, insurer.id(), "EXTERNAL", "https://" + "x".repeat(495) + ".mz", "config:demox", v)
                .then().statusCode(400).body("errors[0].code", equalTo("INVALID_URL"));
        put(admin, insurer.id(), "EXTERNAL", "relativa/rating", "config:demox", v)
                .then().statusCode(400).body("errors[0].code", equalTo("INVALID_URL"));
        put(admin, insurer.id(), null, null, null, v).then().statusCode(400)
                .body("errors[0].field", equalTo("ratingMode"));

        allowHttp(false);
        put(admin, insurer.id(), "EXTERNAL", "http://cotacoes.seguradora.co.mz/moseguros", "config:demox", v)
                .then().statusCode(400)
                .body("errors[0].field", equalTo("ratingEndpointUrl"))
                .body("errors[0].code", equalTo("INVALID_URL"));
        allowHttp(true);
        put(admin, insurer.id(), "EXTERNAL", "http://localhost:9999/rating", "config:demox", v)
                .then().statusCode(200);

        put(admin, insurer.id(), "INTERNAL", null, null, v).then().statusCode(409)
                .body("code", equalTo("CONCURRENT_MODIFICATION"));
    }

    @Test
    void onlyInsurersAndOnlyThePlatform() {
        UUID broker = fixtures.organization(OrganizationType.BROKER, "Corretora Ligação").getId();
        given().auth().oauth2(admin).get(PATH, broker).then().statusCode(400)
                .body("code", equalTo("ORGANIZATION_NOT_INSURER"));
        put(admin, broker, "EXTERNAL", "https://x.co.mz/r", "config:demox", 0).then().statusCode(400)
                .body("code", equalTo("ORGANIZATION_NOT_INSURER"));
        given().auth().oauth2(admin).get(PATH, UUID.randomUUID()).then().statusCode(404)
                .body("code", equalTo("ORGANIZATION_NOT_FOUND"));

        given().auth().oauth2(insurer.manager()).get(PATH, insurer.id()).then().statusCode(403);
        put(insurer.manager(), insurer.id(), "EXTERNAL", "https://x.co.mz/r", "config:demox", 0)
                .then().statusCode(403);
        given().auth().oauth2(support.customerToken()).get(PATH, insurer.id()).then().statusCode(403);
        given().get(PATH, insurer.id()).then().statusCode(401);
    }
}
