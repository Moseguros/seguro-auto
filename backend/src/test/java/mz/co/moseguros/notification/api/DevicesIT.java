package mz.co.moseguros.notification.api;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import mz.co.moseguros.SecurityTestConfiguration;
import mz.co.moseguros.SecurityTestConfiguration.IdentityFixtures;
import mz.co.moseguros.TestcontainersConfiguration;
import mz.co.moseguros.identity.IdentityApi;
import mz.co.moseguros.notification.NotificationTestSupport;
import mz.co.moseguros.notification.NotificationTestSupport.Client;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

/** Dispositivos para push (S6-04 §8): registar, actualizar, reatribuir, remover; nunca devolve o token. */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestcontainersConfiguration.class, SecurityTestConfiguration.class})
@ActiveProfiles("test")
class DevicesIT {

    static final String BASE = "/api/v1/me/devices";

    @LocalServerPort
    int port;

    @Autowired
    IdentityFixtures fixtures;

    @Autowired
    IdentityApi identityApi;

    NotificationTestSupport support;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        support = new NotificationTestSupport(fixtures, identityApi);
    }

    @Test
    void registersUpdatesReassignsAndRemoves() {
        Client a = support.customer();
        Client b = support.customer();
        String token = "fcm-" + UUID.randomUUID();

        String body = register(a, token, "ANDROID", "1.0.0+12").then().statusCode(200)
                .body("platform", equalTo("ANDROID")).body("active", equalTo(true))
                .extract().asString();
        assertThat(body).doesNotContain(token).doesNotContain("token");
        UUID first = UUID.fromString(io.restassured.path.json.JsonPath.from(body).getString("id"));

        // o mesmo cliente regista de novo: a mesma linha, actualizada
        register(a, token, "ANDROID", "1.0.1+13").then().statusCode(200).body("id", equalTo(first.toString()));
        assertThat(row(first)).containsEntry("app_version", "1.0.1+13").containsEntry("active", true);

        // outro cliente com o mesmo token: o de A fica REASSIGNED e cria-se um novo para B
        String second = register(b, token, "IOS", null).then().statusCode(200).extract().path("id");
        assertThat(second).isNotEqualTo(first.toString());
        assertThat(row(first)).containsEntry("active", false).containsEntry("deactivated_reason", "REASSIGNED");
        assertThat(row(UUID.fromString(second))).containsEntry("active", true).containsEntry("platform", "IOS")
                .containsEntry("user_id", b.id());

        // A não remove o de B
        given().auth().oauth2(a.token()).delete(BASE + "/{id}", second).then().statusCode(404)
                .body("code", equalTo("DEVICE_NOT_FOUND"));
        given().auth().oauth2(b.token()).delete(BASE + "/{id}", second).then().statusCode(204);
        assertThat(row(UUID.fromString(second))).containsEntry("active", false)
                .containsEntry("deactivated_reason", "UNREGISTERED");

        // A volta a registar o token: a sua linha antiga reactiva-se
        register(a, token, "ANDROID", "1.0.2").then().statusCode(200).body("id", equalTo(first.toString()));
        List<Map<String, Object>> active = support.owner().queryForList(
                "select id from notification.device where token = ? and active", token);
        assertThat(active).hasSize(1);
    }

    @Test
    void validatesTheRequestAndRejectsStaff() {
        Client a = support.customer();
        register(a, "", "ANDROID", null).then().statusCode(400).body("errors[0].field", equalTo("token"));
        register(a, "x".repeat(513), "ANDROID", null).then().statusCode(400).body("errors[0].field", equalTo("token"));
        given().auth().oauth2(a.token()).contentType(ContentType.JSON).body(Map.of("token", "t-1"))
                .post(BASE).then().statusCode(400).body("errors[0].field", equalTo("platform"));
        register(a, "t-2", "ANDROID", "1".repeat(31)).then().statusCode(400)
                .body("errors[0].field", equalTo("appVersion"));
        given().contentType(ContentType.JSON).body(Map.of("token", "t", "platform", "ANDROID")).post(BASE)
                .then().statusCode(401);
        given().auth().oauth2(support.platformAdminToken()).contentType(ContentType.JSON)
                .body(Map.of("token", "t-3", "platform", "ANDROID")).post(BASE).then().statusCode(403);
    }

    private io.restassured.response.Response register(Client c, String token, String platform, String appVersion) {
        Map<String, Object> body = new java.util.LinkedHashMap<>();
        body.put("token", token);
        body.put("platform", platform);
        if (appVersion != null) {
            body.put("appVersion", appVersion);
        }
        return given().auth().oauth2(c.token()).contentType(ContentType.JSON).body(body).post(BASE);
    }

    private Map<String, Object> row(UUID id) {
        return support.owner().queryForMap("select * from notification.device where id = ?", id);
    }
}
