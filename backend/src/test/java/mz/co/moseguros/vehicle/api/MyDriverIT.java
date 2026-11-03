package mz.co.moseguros.vehicle.api;

import static io.restassured.RestAssured.given;
import static mz.co.moseguros.vehicle.VehicleTestSupport.createDriver;
import static mz.co.moseguros.vehicle.VehicleTestSupport.driver;
import static mz.co.moseguros.vehicle.VehicleTestSupport.newDriver;
import static mz.co.moseguros.vehicle.VehicleTestSupport.newVehicle;
import static mz.co.moseguros.vehicle.VehicleTestSupport.putDrivers;
import static mz.co.moseguros.vehicle.VehicleTestSupport.vehicleVersion;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

/**
 * {@code /me/drivers} e {@code PUT /me/vehicles/{id}/drivers} (S2-03 §2–3): máscara e cifra do
 * número da carta, repetidos, categorias, habitual, associação a veículos e isolamento.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestcontainersConfiguration.class, SecurityTestConfiguration.class})
@ActiveProfiles("test")
class MyDriverIT {

    private static final String LICENCE = "MAP-1234567";

    @LocalServerPort
    int port;

    @Autowired
    IdentityFixtures fixtures;

    @Autowired
    IdentityApi identityApi;

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

    private static Response put(String token, UUID id, Map<String, Object> body) {
        return given().auth().oauth2(token).contentType(ContentType.JSON).body(body).put("/api/v1/me/drivers/{id}", id);
    }

    @Test
    void createMasksAndEncryptsTheLicenceNumber() {
        UserAccount customer = fixtures.customer("Ana Cliente");
        Map<String, Object> body = driver("Ana Maria Mussa", "map 1234567");
        body.put("licenceCategories", List.of("B", "a1"));
        body.put("isOwner", true);
        Response response = createDriver(token(customer), body);
        String responseText = response.then().statusCode(201)
                .body("fullName", equalTo("Ana Maria Mussa"))
                .body("birthDate", equalTo("1990-05-14"))
                .body("licenceNumberMasked", equalTo("****4567"))
                .body("licenceCategories", contains("B", "A1"))
                .body("licenceIssueDate", equalTo("2010-03-01"))
                .body("isOwner", equalTo(true))
                .body("vehicleIds", hasSize(0))
                .body("version", equalTo(0))
                .body("$", not(hasKey("licenceNumber")))
                .extract().asString();
        assertThat(responseText).doesNotContain("MAP1234567");
        String id = response.path("id");
        assertThat(response.header("Location")).endsWith("/api/v1/me/drivers/" + id);

        Map<String, Object> row = owner.queryForMap("select licence_number_enc, licence_number_hash, "
                + "licence_number_last4 from vehicle.driver where id = ?::uuid", id);
        String encrypted = new String((byte[]) row.get("licence_number_enc"), StandardCharsets.ISO_8859_1);
        assertThat(encrypted).doesNotContain("MAP1234567").doesNotContain("1234567");
        assertThat((String) row.get("licence_number_hash")).hasSize(64).doesNotContain("MAP1234567");
        assertThat(row.get("licence_number_last4")).isEqualTo("4567");

        Map<String, Object> audit = owner.queryForMap("""
                select actor_type, after_state::text as after from audit.audit_event
                 where action = 'DRIVER.CREATE' and entity_id = ?""", id);
        assertThat(audit.get("actor_type")).isEqualTo("CUSTOMER");
        assertThat((String) audit.get("after")).contains("****4567").doesNotContain("MAP1234567");
    }

    @Test
    void updateWithoutLicenceNumberKeepsIt() {
        String token = newCustomer();
        UUID id = newDriver(token, "Ana Maria Mussa", LICENCE);
        String hashBefore = owner.queryForObject("select licence_number_hash from vehicle.driver where id = ?",
                String.class, id);

        Map<String, Object> body = driver("Ana Maria Mussa Langa", null);
        body.remove("licenceNumber");
        body.put("licenceCategories", List.of("B", "C"));
        body.put("version", 0);
        put(token, id, body).then().statusCode(200)
                .body("fullName", equalTo("Ana Maria Mussa Langa"))
                .body("licenceNumberMasked", equalTo("****4567"))
                .body("licenceCategories", contains("B", "C"))
                .body("version", equalTo(1));
        assertThat(owner.queryForObject("select licence_number_hash from vehicle.driver where id = ?", String.class, id))
                .isEqualTo(hashBefore);

        body.put("licenceNumber", "NEW-99887766");
        body.put("version", 1);
        put(token, id, body).then().statusCode(200).body("licenceNumberMasked", equalTo("****7766"));
        assertThat(owner.queryForObject("select licence_number_hash from vehicle.driver where id = ?", String.class, id))
                .isNotEqualTo(hashBefore);

        put(token, id, body).then().statusCode(409).body("code", equalTo("CONCURRENT_MODIFICATION"));

        String audit = owner.queryForObject("""
                select before_state::text || after_state::text from audit.audit_event
                 where action = 'DRIVER.UPDATE' and entity_id = ? order by occurred_at desc limit 1""",
                String.class, id.toString());
        assertThat(audit).contains("****4567").contains("****7766").doesNotContain("NEW99887766")
                .doesNotContain("MAP1234567");
    }

    @Test
    void sameLicenceOrSecondOwnerIs409() {
        String token = newCustomer();
        Map<String, Object> self = driver("Ana Maria Mussa", LICENCE);
        self.put("isOwner", true);
        createDriver(token, self).then().statusCode(201);

        createDriver(token, driver("Bruno Mussa", " map-123 4567")).then().statusCode(409)
                .body("code", equalTo("DRIVER_ALREADY_REGISTERED"));
        Map<String, Object> second = driver("Bruno Mussa", "BRU-7654321");
        second.put("isOwner", true);
        createDriver(token, second).then().statusCode(409).body("code", equalTo("OWNER_DRIVER_ALREADY_EXISTS"));

        // Outro cliente pode registar a mesma carta.
        createDriver(newCustomer(), driver("Ana Maria Mussa", LICENCE)).then().statusCode(201);

        // No PUT também.
        UUID other = newDriver(token, "Carlos Mussa", "CAR-1111111");
        Map<String, Object> change = driver("Carlos Mussa", LICENCE);
        change.put("version", 0);
        put(token, other, change).then().statusCode(409).body("code", equalTo("DRIVER_ALREADY_REGISTERED"));
        change.put("licenceNumber", null);
        change.put("isOwner", true);
        put(token, other, change).then().statusCode(409).body("code", equalTo("OWNER_DRIVER_ALREADY_EXISTS"));
    }

    @Test
    void unknownOrInactiveCategoryIsRejected() {
        String token = newCustomer();
        Map<String, Object> body = driver("Ana Maria Mussa", LICENCE);
        body.put("licenceCategories", List.of("B", "XYZ"));
        createDriver(token, body).then().statusCode(400)
                .body("code", equalTo("VALIDATION_ERROR"))
                .body("errors[0].field", equalTo("licenceCategories[1]"))
                .body("errors[0].code", equalTo("LICENCE_CATEGORY_UNKNOWN"));

        owner.update("update catalog.reference_value set active = false where domain = 'LICENCE_CATEGORY' "
                + "and code = 'DE'");
        try {
            body.put("licenceCategories", List.of("DE"));
            createDriver(token, body).then().statusCode(400)
                    .body("errors[0].field", equalTo("licenceCategories[0]"))
                    .body("errors[0].code", equalTo("LICENCE_CATEGORY_UNKNOWN"));
        } finally {
            owner.update("update catalog.reference_value set active = true where domain = 'LICENCE_CATEGORY' "
                    + "and code = 'DE'");
        }

        body.put("licenceCategories", List.of("B", "B"));
        body.put("licenceIssueDate", "1980-01-01");
        createDriver(token, body).then().statusCode(400)
                .body("errors.field", contains("licenceCategories[1]", "licenceIssueDate"))
                .body("errors.code", contains("DUPLICATED", "LICENCE_ISSUE_DATE_INVALID"));
    }

    @Test
    void driversOfAVehicleNeedOneHabitualAndTheHabitualCannotBeDeleted() {
        String token = newCustomer();
        UUID vehicle = newVehicle(token, "HHH-100-MC");
        UUID ana = newDriver(token, "Ana Maria Mussa", LICENCE);
        UUID bruno = newDriver(token, "Bruno Mussa", "BRU-7654321");

        putDrivers(token, vehicle, 0, ana, true, bruno, true).then().statusCode(400)
                .body("errors[0].field", equalTo("drivers"))
                .body("errors[0].code", equalTo("MULTIPLE_HABITUAL_DRIVERS"));
        putDrivers(token, vehicle, 0, ana, false, bruno, false).then().statusCode(400)
                .body("errors[0].code", equalTo("HABITUAL_DRIVER_REQUIRED"));
        putDrivers(token, vehicle, 0, ana, true, ana, false).then().statusCode(400)
                .body("errors[0].code", equalTo("DUPLICATED"));

        putDrivers(token, vehicle, 0, bruno, false, ana, true).then().statusCode(200)
                .body("drivers.driverId", contains(ana.toString(), bruno.toString()))
                .body("drivers.habitual", contains(true, false))
                .body("drivers[0].fullName", equalTo("Ana Maria Mussa"))
                .body("version", equalTo(1));
        putDrivers(token, vehicle, 0, ana, true).then().statusCode(409)
                .body("code", equalTo("CONCURRENT_MODIFICATION"));

        given().auth().oauth2(token).get("/api/v1/me/drivers/{id}", ana).then().statusCode(200)
                .body("vehicleIds", contains(vehicle.toString()));
        given().auth().oauth2(token).delete("/api/v1/me/drivers/{id}", ana).then().statusCode(409)
                .body("code", equalTo("DRIVER_IS_HABITUAL"));

        // Bruno (não habitual) sai do veículo ao ser retirado.
        given().auth().oauth2(token).delete("/api/v1/me/drivers/{id}", bruno).then().statusCode(204);
        assertThat(owner.queryForObject("select status from vehicle.driver where id = ?", String.class, bruno))
                .isEqualTo("REMOVED");
        given().auth().oauth2(token).get("/api/v1/me/vehicles/{id}", vehicle).then().statusCode(200)
                .body("drivers.driverId", contains(ana.toString()));
        given().auth().oauth2(token).get("/api/v1/me/drivers/{id}", bruno).then().statusCode(404)
                .body("code", equalTo("DRIVER_NOT_FOUND"));

        // Lista vazia é permitida; depois o antigo habitual pode ser retirado.
        putDrivers(token, vehicle, vehicleVersion(token, vehicle)).then().statusCode(200).body("drivers", hasSize(0));
        given().auth().oauth2(token).delete("/api/v1/me/drivers/{id}", ana).then().statusCode(204);
        assertThat(owner.queryForObject("select count(*) from audit.audit_event where action = 'VEHICLE.DRIVERS_UPDATE' "
                + "and entity_id = ?", Integer.class, vehicle.toString())).isEqualTo(2);
    }

    @Test
    void habitualOfARemovedVehicleCanBeDeleted() {
        String token = newCustomer();
        UUID vehicle = newVehicle(token, "HHH-200-MC");
        UUID ana = newDriver(token, "Ana Maria Mussa", LICENCE);
        putDrivers(token, vehicle, 0, ana, true).then().statusCode(200);
        given().auth().oauth2(token).delete("/api/v1/me/vehicles/{id}", vehicle).then().statusCode(204);
        // As ligações do veículo retirado ficam.
        assertThat(owner.queryForObject("select count(*) from vehicle.vehicle_driver where vehicle_id = ?",
                Integer.class, vehicle)).isEqualTo(1);
        given().auth().oauth2(token).get("/api/v1/me/drivers/{id}", ana).then().statusCode(200)
                .body("vehicleIds", hasSize(0));
        given().auth().oauth2(token).delete("/api/v1/me/drivers/{id}", ana).then().statusCode(204);
    }

    @Test
    void driversOfAnotherCustomerAreInvisible() {
        String tokenA = newCustomer();
        String tokenB = newCustomer();
        UUID driverA = newDriver(tokenA, "Ana Maria Mussa", LICENCE);
        UUID driverB = newDriver(tokenB, "Bruno Mussa", "BRU-7654321");
        UUID vehicleB = newVehicle(tokenB, "JJJ-100-MC");

        putDrivers(tokenB, vehicleB, 0, driverA, true).then().statusCode(404)
                .body("code", equalTo("DRIVER_NOT_FOUND"));
        putDrivers(tokenB, vehicleB, 0, driverB, true, driverA, false).then().statusCode(404);
        putDrivers(tokenB, vehicleB, 0, UUID.randomUUID(), true).then().statusCode(404);

        given().auth().oauth2(tokenB).get("/api/v1/me/drivers/{id}", driverA).then().statusCode(404)
                .body("code", equalTo("DRIVER_NOT_FOUND"));
        Map<String, Object> body = driver("Bruno Mussa", null);
        body.put("version", 0);
        put(tokenB, driverA, body).then().statusCode(404).body("code", equalTo("DRIVER_NOT_FOUND"));
        given().auth().oauth2(tokenB).delete("/api/v1/me/drivers/{id}", driverA).then().statusCode(404);
        given().auth().oauth2(tokenB).get("/api/v1/me/drivers").then().statusCode(200)
                .body("content.id", containsInAnyOrder(driverB.toString()))
                .body("totalElements", equalTo(1));
        assertThat(owner.queryForObject("select status from vehicle.driver where id = ?", String.class, driverA))
                .isEqualTo("ACTIVE");
    }

    @Test
    void theEleventhDriverIs409() {
        String token = newCustomer();
        for (int i = 0; i < 10; i++) {
            newDriver(token, "Condutor Teste", String.format("LIC-%07d", i));
        }
        createDriver(token, driver("Condutor Teste", "LIC-0000010")).then().statusCode(409)
                .body("code", equalTo("DRIVER_LIMIT_REACHED"));
    }

    @Test
    void staffIs403AndWithoutTokenIs401() {
        Organization insurer = fixtures.organization(OrganizationType.INSURER, "Seguros Condutor");
        String staff = token(fixtures.staff("Gestor Seguros", insurer.getId(), "INSURER_ADMIN"));
        given().auth().oauth2(staff).get("/api/v1/me/drivers").then().statusCode(403)
                .body("code", equalTo("FORBIDDEN"));
        createDriver(staff, driver("Ana Maria Mussa", LICENCE)).then().statusCode(403);
        given().get("/api/v1/me/drivers").then().statusCode(401);
    }
}
