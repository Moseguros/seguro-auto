package mz.co.moseguros.vehicle;

import static io.restassured.RestAssured.given;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import io.restassured.http.ContentType;
import io.restassured.response.Response;

/** Pedidos de exemplo e chamadas REST comuns aos testes de integração do módulo Veículos. */
public final class VehicleTestSupport {

    /** Chassis de {@code AAA-100-MC} no registo simulado. */
    public static final String MOCK_CHASSIS = "JTDBR32E720012345";

    private VehicleTestSupport() {
    }

    /** Pedido válido de um Toyota Corolla com a matrícula e o chassis indicados. */
    public static Map<String, Object> vehicle(String plate, String chassis) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("plate", plate);
        body.put("chassisNumber", chassis);
        body.put("make", "Toyota");
        body.put("model", "Corolla");
        body.put("year", 2018);
        body.put("engineCc", 1598);
        body.put("category", "LIGHT_PASSENGER");
        body.put("seats", 5);
        body.put("usage", "PRIVATE");
        body.put("provinceOfCirculation", "MAPUTO_CITY");
        body.put("commercialValue", Map.of("amount", 85_000_000, "currency", "MZN"));
        return body;
    }

    /** Pedido válido de condutor com o número da carta indicado. */
    public static Map<String, Object> driver(String fullName, String licenceNumber) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("fullName", fullName);
        body.put("birthDate", "1990-05-14");
        body.put("licenceNumber", licenceNumber);
        body.put("licenceCategories", List.of("B"));
        body.put("licenceIssueDate", "2010-03-01");
        return body;
    }

    public static Response createVehicle(String token, Map<String, Object> body) {
        return given().auth().oauth2(token).contentType(ContentType.JSON).body(body).post("/api/v1/me/vehicles");
    }

    /** Cria o veículo (201) e devolve o id. */
    public static UUID newVehicle(String token, String plate) {
        return UUID.fromString(createVehicle(token, vehicle(plate, "CHASSIS" + plate.replace("-", "")))
                .then().statusCode(201).extract().path("id"));
    }

    public static Response createDriver(String token, Map<String, Object> body) {
        return given().auth().oauth2(token).contentType(ContentType.JSON).body(body).post("/api/v1/me/drivers");
    }

    /** Cria o condutor (201) e devolve o id. */
    public static UUID newDriver(String token, String fullName, String licenceNumber) {
        return UUID.fromString(createDriver(token, driver(fullName, licenceNumber))
                .then().statusCode(201).extract().path("id"));
    }

    /** {@code PUT /me/vehicles/{id}/drivers} com {@code [driverId, habitual]} alternados. */
    public static Response putDrivers(String token, UUID vehicleId, long version, Object... pairs) {
        List<Map<String, Object>> drivers = new java.util.ArrayList<>();
        for (int i = 0; i < pairs.length; i += 2) {
            drivers.add(Map.of("driverId", pairs[i].toString(), "habitual", pairs[i + 1]));
        }
        return given().auth().oauth2(token).contentType(ContentType.JSON)
                .body(Map.of("drivers", drivers, "version", version))
                .put("/api/v1/me/vehicles/{id}/drivers", vehicleId);
    }

    /** Versão actual do veículo. */
    public static long vehicleVersion(String token, UUID vehicleId) {
        return ((Number) given().auth().oauth2(token).get("/api/v1/me/vehicles/{id}", vehicleId)
                .then().statusCode(200).extract().path("version")).longValue();
    }
}
