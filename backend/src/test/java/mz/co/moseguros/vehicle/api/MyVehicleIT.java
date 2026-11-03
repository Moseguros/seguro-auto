package mz.co.moseguros.vehicle.api;

import static io.restassured.RestAssured.given;
import static mz.co.moseguros.vehicle.VehicleTestSupport.MOCK_CHASSIS;
import static mz.co.moseguros.vehicle.VehicleTestSupport.createVehicle;
import static mz.co.moseguros.vehicle.VehicleTestSupport.newVehicle;
import static mz.co.moseguros.vehicle.VehicleTestSupport.vehicle;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import mz.co.moseguros.SecurityTestConfiguration;
import mz.co.moseguros.SecurityTestConfiguration.IdentityFixtures;
import mz.co.moseguros.TestcontainersConfiguration;
import mz.co.moseguros.document.TestFiles;
import mz.co.moseguros.identity.IdentityApi;
import mz.co.moseguros.identity.domain.UserAccount;
import mz.co.moseguros.organization.OrganizationType;
import mz.co.moseguros.organization.domain.Organization;
import mz.co.moseguros.vehicle.application.TestVehicleRemovalGuard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

/**
 * {@code /me/vehicles} (S2-03 §2–3) com {@code moseguros_app_user} (RLS): registo automóvel,
 * normalização, repetidos, limite, remoção e guards, concorrência, isolamento entre clientes e
 * acesso de staff.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestcontainersConfiguration.class, SecurityTestConfiguration.class})
@ActiveProfiles("test")
class MyVehicleIT {

    @LocalServerPort
    int port;

    @Autowired
    IdentityFixtures fixtures;

    @Autowired
    IdentityApi identityApi;

    @Autowired
    TestVehicleRemovalGuard guard;

    JdbcTemplate owner;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        owner = fixtures.owner();
    }

    private String token(UserAccount account) {
        return identityApi.issueTokens(account, null).accessToken();
    }

    private String newCustomer() {
        return token(fixtures.customer("Ana Cliente"));
    }

    @Test
    void createWithTheRegistryChassisIsVerifiedAndReturnsTheVehicle() {
        UserAccount customer = fixtures.customer("Ana Cliente");
        Response response = createVehicle(token(customer), vehicle("AAA-100-MC", MOCK_CHASSIS));
        response.then().statusCode(201)
                .body("plate", equalTo("AAA-100-MC"))
                .body("chassisNumber", equalTo(MOCK_CHASSIS))
                .body("make", equalTo("Toyota"))
                .body("model", equalTo("Corolla"))
                .body("year", equalTo(2018))
                .body("engineCc", equalTo(1598))
                .body("category", equalTo("LIGHT_PASSENGER"))
                .body("seats", equalTo(5))
                .body("usage", equalTo("PRIVATE"))
                .body("provinceOfCirculation", equalTo("MAPUTO_CITY"))
                .body("commercialValue.amount", equalTo(85_000_000))
                .body("commercialValue.currency", equalTo("MZN"))
                .body("hasTracker", equalTo(false))
                .body("inatroVerified", equalTo(true))
                .body("drivers", hasSize(0))
                .body("version", equalTo(0))
                .body("$", not(hasKey("grossWeightKg")))
                .body("$", not(hasKey("registrationDoc")));
        String id = response.path("id");
        assertThat(response.header("Location")).endsWith("/api/v1/me/vehicles/" + id);

        Map<String, Object> row = owner.queryForMap("select owner_user_id, plate_format_code, inatro_verified_at, "
                + "status, removed_at from vehicle.vehicle where id = ?::uuid", id);
        assertThat(row.get("owner_user_id")).isEqualTo(customer.getId());
        assertThat(row.get("plate_format_code")).isEqualTo("STANDARD");
        assertThat(row.get("inatro_verified_at")).isNotNull();
        assertThat(row.get("status")).isEqualTo("ACTIVE");
        assertThat(row.get("removed_at")).isNull();
        assertThat(owner.queryForObject("select count(*) from audit.audit_event where action = 'VEHICLE.CREATE' "
                + "and entity_id = ? and actor_type = 'CUSTOMER'", Integer.class, id)).isEqualTo(1);
    }

    @Test
    void anotherChassisOrAnUnavailableRegistryIsSavedUnverified() {
        createVehicle(newCustomer(), vehicle("AAA-100-MC", "OTHERCHASSIS12345"))
                .then().statusCode(201).body("inatroVerified", equalTo(false));
        createVehicle(newCustomer(), vehicle("AAA-999-MC", "ANYCHASSIS12345"))
                .then().statusCode(201).body("inatroVerified", equalTo(false));
        createVehicle(newCustomer(), vehicle("ZZZ-123-MC", "ANYCHASSIS12345"))
                .then().statusCode(201).body("inatroVerified", equalTo(false));
    }

    @Test
    void plateAndChassisAreNormalised() {
        Map<String, Object> body = vehicle("aaa 100 mc", "jtdbr32e-7200 12345");
        createVehicle(newCustomer(), body).then().statusCode(201)
                .body("plate", equalTo("AAA-100-MC"))
                .body("chassisNumber", equalTo(MOCK_CHASSIS))
                .body("inatroVerified", equalTo(true));
    }

    @Test
    void validationErrorsComeTogether() {
        Map<String, Object> body = vehicle("XYZ", "AB");
        body.put("category", "TRAILER");
        body.put("seats", 0);
        body.remove("make");
        createVehicle(newCustomer(), body).then().statusCode(400)
                .body("code", equalTo("VALIDATION_ERROR"))
                .body("errors.field", contains("plate", "chassisNumber", "make", "engineCc", "grossWeightKg"))
                .body("errors.code", contains("INVALID_PLATE", "INVALID_CHASSIS", "REQUIRED", "NOT_ALLOWED",
                        "REQUIRED"));

        Map<String, Object> usd = vehicle("AAA-100-MC", MOCK_CHASSIS);
        usd.put("commercialValue", Map.of("amount", 100, "currency", "USD"));
        createVehicle(newCustomer(), usd).then().statusCode(400)
                .body("code", equalTo("VALIDATION_ERROR"))
                .body("errors[0].field", equalTo("commercialValue"))
                .body("errors[0].code", equalTo("INVALID_CURRENCY"));
    }

    @Test
    void samePlateTwiceIs409AndAllowedAgainAfterDelete() {
        String token = newCustomer();
        UUID first = newVehicle(token, "BBB-100-MC");
        createVehicle(token, vehicle("bbb-100-mc", "OTHERCHASSIS1")).then().statusCode(409)
                .body("code", equalTo("VEHICLE_ALREADY_REGISTERED"));

        given().auth().oauth2(token).delete("/api/v1/me/vehicles/{id}", first).then().statusCode(204);
        UUID second = newVehicle(token, "BBB-100-MC");
        assertThat(second).isNotEqualTo(first);

        // Outro cliente pode ter a mesma matrícula.
        newVehicle(newCustomer(), "BBB-100-MC");

        // No PUT, mudar para a matrícula de outro veículo activo também é 409.
        UUID third = newVehicle(token, "BBB-200-MC");
        Map<String, Object> change = vehicle("BBB-100-MC", "CHASSIS3");
        change.put("version", 0);
        given().auth().oauth2(token).contentType(ContentType.JSON).body(change)
                .put("/api/v1/me/vehicles/{id}", third)
                .then().statusCode(409).body("code", equalTo("VEHICLE_ALREADY_REGISTERED"));
    }

    @Test
    void the21stActiveVehicleIs409() {
        String token = newCustomer();
        for (int i = 0; i < 20; i++) {
            newVehicle(token, String.format("CCA-%03d-MC", i));
        }
        createVehicle(token, vehicle("CCA-020-MC", "CHASSIS21")).then().statusCode(409)
                .body("code", equalTo("VEHICLE_LIMIT_REACHED"));
        given().auth().oauth2(token).queryParam("size", 100).get("/api/v1/me/vehicles")
                .then().statusCode(200).body("totalElements", equalTo(20));
    }

    @Test
    void deleteIsLogicalAndAGuardCanBlockIt() {
        String token = newCustomer();
        UUID free = newVehicle(token, "DDD-100-MC");
        given().auth().oauth2(token).delete("/api/v1/me/vehicles/{id}", free).then().statusCode(204);
        Map<String, Object> row = owner.queryForMap(
                "select status, removed_at from vehicle.vehicle where id = ?", free);
        assertThat(row.get("status")).isEqualTo("REMOVED");
        assertThat(row.get("removed_at")).isNotNull();
        given().auth().oauth2(token).get("/api/v1/me/vehicles/{id}", free).then().statusCode(404)
                .body("code", equalTo("VEHICLE_NOT_FOUND"));
        given().auth().oauth2(token).delete("/api/v1/me/vehicles/{id}", free).then().statusCode(404);
        assertThat(owner.queryForObject("select count(*) from audit.audit_event where action = 'VEHICLE.DELETE' "
                + "and entity_id = ?", Integer.class, free.toString())).isEqualTo(1);

        UUID blocked = newVehicle(token, "DDD-200-MC");
        guard.block(blocked);
        try {
            given().auth().oauth2(token).delete("/api/v1/me/vehicles/{id}", blocked).then().statusCode(409)
                    .body("code", equalTo("VEHICLE_REMOVAL_BLOCKED"));
        } finally {
            guard.unblock(blocked);
        }
        assertThat(owner.queryForObject("select status from vehicle.vehicle where id = ?", String.class, blocked))
                .isEqualTo("ACTIVE");
        given().auth().oauth2(token).get("/api/v1/me/vehicles/{id}", blocked).then().statusCode(200);
    }

    @Test
    void updateChecksTheVersionAndTheRegistryAgain() {
        String token = newCustomer();
        UUID id = UUID.fromString(createVehicle(token, vehicle("AAA-200-MC", "WRONGCHASSIS1"))
                .then().statusCode(201).body("inatroVerified", equalTo(false)).extract().path("id"));

        Map<String, Object> body = vehicle("AAA-200-MC", "AHTFR22G106012345");
        body.put("model", "Hilux");
        body.put("hasTracker", true);
        body.put("version", 0);
        given().auth().oauth2(token).contentType(ContentType.JSON).body(body).put("/api/v1/me/vehicles/{id}", id)
                .then().statusCode(200)
                .body("model", equalTo("Hilux"))
                .body("hasTracker", equalTo(true))
                .body("inatroVerified", equalTo(true))
                .body("version", equalTo(1));

        body.put("model", "Hilux 2");
        given().auth().oauth2(token).contentType(ContentType.JSON).body(body).put("/api/v1/me/vehicles/{id}", id)
                .then().statusCode(409).body("code", equalTo("CONCURRENT_MODIFICATION"));

        body.remove("version");
        given().auth().oauth2(token).contentType(ContentType.JSON).body(body).put("/api/v1/me/vehicles/{id}", id)
                .then().statusCode(400).body("errors[0].field", equalTo("version"));

        body.put("version", 1);
        body.remove("commercialValue");
        given().auth().oauth2(token).contentType(ContentType.JSON).body(body).put("/api/v1/me/vehicles/{id}", id)
                .then().statusCode(200)
                .body("model", equalTo("Hilux 2"))
                .body("inatroVerified", equalTo(true))
                .body("$", not(hasKey("commercialValue")));
        assertThat(owner.queryForObject("select count(*) from audit.audit_event where action = 'VEHICLE.UPDATE' "
                + "and entity_id = ?", Integer.class, id.toString())).isEqualTo(2);
    }

    @Test
    void listIsPagedAndSortable() {
        String token = newCustomer();
        newVehicle(token, "EEB-100-MC");
        newVehicle(token, "EEA-100-MC");
        given().auth().oauth2(token).get("/api/v1/me/vehicles")
                .then().statusCode(200)
                .body("content.plate", contains("EEA-100-MC", "EEB-100-MC"))
                .body("page", equalTo(0))
                .body("size", equalTo(20))
                .body("totalElements", equalTo(2));
        given().auth().oauth2(token).queryParam("sort", "plate,asc").get("/api/v1/me/vehicles")
                .then().statusCode(200).body("content.plate", contains("EEA-100-MC", "EEB-100-MC"));
        given().auth().oauth2(token).queryParam("sort", "plate,desc").get("/api/v1/me/vehicles")
                .then().statusCode(200).body("content.plate", contains("EEB-100-MC", "EEA-100-MC"));
        given().auth().oauth2(token).queryParam("sort", "make").get("/api/v1/me/vehicles")
                .then().statusCode(400).body("errors[0].code", equalTo("INVALID_SORT"));
    }

    @Test
    void anotherCustomerCannotSeeOrChangeTheVehicle() throws Exception {
        String tokenA = newCustomer();
        String tokenB = newCustomer();
        UUID id = newVehicle(tokenA, "FFF-100-MC");
        newVehicle(tokenB, "FFF-200-MC");

        given().auth().oauth2(tokenB).get("/api/v1/me/vehicles/{id}", id)
                .then().statusCode(404).body("code", equalTo("VEHICLE_NOT_FOUND"));
        Map<String, Object> body = vehicle("FFF-100-MC", "CHASSISFFF100MC");
        body.put("version", 0);
        given().auth().oauth2(tokenB).contentType(ContentType.JSON).body(body).put("/api/v1/me/vehicles/{id}", id)
                .then().statusCode(404).body("code", equalTo("VEHICLE_NOT_FOUND"));
        given().auth().oauth2(tokenB).delete("/api/v1/me/vehicles/{id}", id)
                .then().statusCode(404).body("code", equalTo("VEHICLE_NOT_FOUND"));
        given().auth().oauth2(tokenB).multiPart("file", "livrete.jpg", TestFiles.bytes("sample.jpg"), "image/jpeg")
                .post("/api/v1/me/vehicles/{id}/registration-doc", id)
                .then().statusCode(404).body("code", equalTo("VEHICLE_NOT_FOUND"));
        given().auth().oauth2(tokenB).contentType(ContentType.JSON).body(Map.of("drivers", List.of(), "version", 0))
                .put("/api/v1/me/vehicles/{id}/drivers", id)
                .then().statusCode(404).body("code", equalTo("VEHICLE_NOT_FOUND"));
        given().auth().oauth2(tokenB).get("/api/v1/me/vehicles")
                .then().statusCode(200).body("content.plate", contains("FFF-200-MC"));

        assertThat(owner.queryForObject("select status from vehicle.vehicle where id = ?", String.class, id))
                .isEqualTo("ACTIVE");
        assertThat(owner.queryForObject("select count(*) from document.file where owner_id = ?", Integer.class, id))
                .isZero();
        given().auth().oauth2(tokenA).get("/api/v1/me/vehicles/{id}", id).then().statusCode(200)
                .body("$", not(hasKey("registrationDoc")))
                .body("registrationDoc", nullValue());
    }

    @Test
    void staffIs403AndWithoutTokenIs401() {
        Organization insurer = fixtures.organization(OrganizationType.INSURER, "Seguros Veículo");
        String staff = token(fixtures.staff("Gestor Seguros", insurer.getId(), "INSURER_ADMIN"));

        given().auth().oauth2(staff).get("/api/v1/me/vehicles").then().statusCode(403)
                .body("code", equalTo("FORBIDDEN"));
        createVehicle(staff, vehicle("GGG-100-MC", "CHASSISGGG100")).then().statusCode(403)
                .body("code", equalTo("FORBIDDEN"));
        given().auth().oauth2(staff).get("/api/v1/me/vehicles/{id}", UUID.randomUUID()).then().statusCode(403);
        given().auth().oauth2(staff).delete("/api/v1/me/vehicles/{id}", UUID.randomUUID()).then().statusCode(403);
        given().auth().oauth2(staff).queryParam("plate", "AAA-100-MC").get("/api/v1/me/vehicles/registry-lookup")
                .then().statusCode(403);

        given().get("/api/v1/me/vehicles").then().statusCode(401).body("code", equalTo("UNAUTHORIZED"));
        given().contentType(ContentType.JSON).body(vehicle("GGG-100-MC", "CHASSISGGG100")).post("/api/v1/me/vehicles")
                .then().statusCode(401);
    }
}
