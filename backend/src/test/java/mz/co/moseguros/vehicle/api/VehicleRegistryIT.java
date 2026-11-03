package mz.co.moseguros.vehicle.api;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.not;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import mz.co.moseguros.SecurityTestConfiguration;
import mz.co.moseguros.SecurityTestConfiguration.IdentityFixtures;
import mz.co.moseguros.TestcontainersConfiguration;
import mz.co.moseguros.identity.IdentityApi;
import mz.co.moseguros.identity.domain.UserAccount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

/** {@code GET /me/vehicles/registry-lookup} com o registo simulado (S2-03 §5). */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestcontainersConfiguration.class, SecurityTestConfiguration.class})
@ActiveProfiles("test")
class VehicleRegistryIT {

    @LocalServerPort
    int port;

    @Autowired
    IdentityFixtures fixtures;

    @Autowired
    IdentityApi identityApi;

    String token;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        UserAccount customer = fixtures.customer("Ana Cliente");
        token = identityApi.issueTokens(customer, null).accessToken();
    }

    private Response lookup(String plate) {
        return given().auth().oauth2(token).queryParam("plate", plate).get("/api/v1/me/vehicles/registry-lookup");
    }

    @Test
    void knownPlateReturnsTheRegistryData() {
        lookup("aaa 300 mc").then().statusCode(200)
                .body("plate", equalTo("AAA-300-MC"))
                .body("chassisNumber", equalTo("WDB9340321L123456"))
                .body("make", equalTo("Mercedes-Benz"))
                .body("model", equalTo("Actros"))
                .body("year", equalTo(2012))
                .body("engineCc", equalTo(11946))
                .body("category", equalTo("HEAVY"))
                .body("seats", equalTo(2))
                .body("grossWeightKg", equalTo(40000))
                .body("source", equalTo("MOCK"));
        lookup("AAA-100-MC").then().statusCode(200)
                .body("category", equalTo("LIGHT_PASSENGER"))
                .body("$", not(hasKey("grossWeightKg")));
        lookup("MLA-12-34").then().statusCode(200).body("make", equalTo("Nissan"));
    }

    @Test
    void unknownPlateIs404AndUnavailableRegistryIs503() {
        lookup("ZZZ-999-MC").then().statusCode(404).body("code", equalTo("VEHICLE_REGISTRY_NOT_FOUND"));
        lookup("AAA-999-MC").then().statusCode(503).body("code", equalTo("VEHICLE_REGISTRY_UNAVAILABLE"));
    }

    @Test
    void invalidPlateIs400AndNothingIsSaved() {
        lookup("XYZ").then().statusCode(400)
                .body("code", equalTo("VALIDATION_ERROR"))
                .body("errors[0].field", equalTo("plate"))
                .body("errors[0].code", equalTo("INVALID_PLATE"));
        given().auth().oauth2(token).get("/api/v1/me/vehicles/registry-lookup").then().statusCode(400)
                .body("errors[0].field", equalTo("plate"));
        assertThat(given().auth().oauth2(token).get("/api/v1/me/vehicles").then().statusCode(200)
                .extract().<Integer>path("totalElements")).isZero();
    }
}
