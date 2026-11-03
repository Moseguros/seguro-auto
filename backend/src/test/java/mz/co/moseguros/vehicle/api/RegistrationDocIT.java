package mz.co.moseguros.vehicle.api;

import static io.restassured.RestAssured.given;
import static mz.co.moseguros.vehicle.VehicleTestSupport.newVehicle;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import mz.co.moseguros.SecurityTestConfiguration;
import mz.co.moseguros.SecurityTestConfiguration.IdentityFixtures;
import mz.co.moseguros.TestcontainersConfiguration;
import mz.co.moseguros.document.TestFiles;
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
 * {@code POST /me/vehicles/{id}/registration-doc} (S2-03 §4): ficheiro guardado e ligado, download
 * só pelo dono ({@code VehicleFileAccessPolicy}), tipos e tamanho, e histórico.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestcontainersConfiguration.class, SecurityTestConfiguration.class})
@ActiveProfiles("test")
class RegistrationDocIT {

    @LocalServerPort
    int port;

    @Autowired
    IdentityFixtures fixtures;

    @Autowired
    IdentityApi identityApi;

    JdbcTemplate owner;
    byte[] jpeg;
    byte[] pdf;

    @BeforeEach
    void setUp() throws IOException {
        RestAssured.port = port;
        owner = fixtures.owner();
        jpeg = TestFiles.bytes("sample.jpg");
        pdf = TestFiles.bytes("sample.pdf");
    }

    private String token(UserAccount account) {
        return identityApi.issueTokens(account, null).accessToken();
    }

    private static Response upload(String token, UUID vehicleId, String filename, byte[] content, String type) {
        return given().auth().oauth2(token).multiPart("file", filename, content, type)
                .post("/api/v1/me/vehicles/{id}/registration-doc", vehicleId);
    }

    @Test
    void jpegIsStoredLinkedAndOnlyTheOwnerDownloadsIt() {
        UserAccount customer = fixtures.customer("Ana Cliente");
        String token = token(customer);
        UUID vehicle = newVehicle(token, "KKK-100-MC");

        Response response = upload(token, vehicle, "livrete.jpg", jpeg, "image/jpeg");
        response.then().statusCode(200)
                .body("id", equalTo(vehicle.toString()))
                .body("registrationDoc.contentType", equalTo("image/jpeg"))
                .body("registrationDoc.sizeBytes", equalTo(jpeg.length))
                .body("version", equalTo(1));
        UUID fileId = UUID.fromString(response.path("registrationDoc.fileId"));
        assertThat((String) response.path("registrationDoc.uploadedAt")).isNotBlank();

        Map<String, Object> file = owner.queryForMap("select owner_type, owner_id, purpose, classification, linked_at, "
                + "insurer_id from document.file where id = ?", fileId);
        assertThat(file.get("owner_type")).isEqualTo("VEHICLE");
        assertThat(file.get("owner_id")).isEqualTo(vehicle);
        assertThat(file.get("purpose")).isEqualTo("REGISTRATION_DOC");
        assertThat(file.get("classification")).isEqualTo("PERSONAL");
        assertThat(file.get("linked_at")).isNotNull();
        assertThat(file.get("insurer_id")).isNull();
        assertThat(owner.queryForObject("select registration_doc_file_id from vehicle.vehicle where id = ?",
                UUID.class, vehicle)).isEqualTo(fileId);
        assertThat(owner.queryForObject("""
                select after_state->>'fileId' from audit.audit_event
                 where action = 'VEHICLE.REGISTRATION_DOC_UPLOAD' and entity_id = ?""", String.class,
                vehicle.toString())).isEqualTo(fileId.toString());

        Response download = given().auth().oauth2(token).get("/api/v1/files/{id}", fileId);
        download.then().statusCode(200).header("Content-Type", "image/jpeg");
        assertThat(download.asByteArray()).isEqualTo(jpeg);

        given().auth().oauth2(token).get("/api/v1/me/vehicles/{id}", vehicle).then().statusCode(200)
                .body("registrationDoc.fileId", equalTo(fileId.toString()));

        String other = token(fixtures.customer("Bruno Cliente"));
        given().auth().oauth2(other).get("/api/v1/files/{id}", fileId).then().statusCode(404)
                .body("code", equalTo("FILE_NOT_FOUND"));
        given().get("/api/v1/files/{id}", fileId).then().statusCode(404).body("code", equalTo("FILE_NOT_FOUND"));

        Organization insurer = fixtures.organization(OrganizationType.INSURER, "Seguros Livrete");
        String staff = token(fixtures.staff("Gestor Livrete", insurer.getId(), "INSURER_ADMIN"));
        given().auth().oauth2(staff).get("/api/v1/files/{id}", fileId).then().statusCode(404);

        // O dono continua a ler o livrete de um veículo retirado.
        given().auth().oauth2(token).delete("/api/v1/me/vehicles/{id}", vehicle).then().statusCode(204);
        given().auth().oauth2(token).get("/api/v1/files/{id}", fileId).then().statusCode(200);
        upload(token, vehicle, "livrete.jpg", jpeg, "image/jpeg").then().statusCode(404)
                .body("code", equalTo("VEHICLE_NOT_FOUND"));
    }

    @Test
    void pdfIsAcceptedAndANewUploadReplacesTheCurrentOne() {
        String token = token(fixtures.customer("Carla Cliente"));
        UUID vehicle = newVehicle(token, "KKK-200-MC");

        UUID first = UUID.fromString(upload(token, vehicle, "livrete.jpg", jpeg, "image/jpeg")
                .then().statusCode(200).extract().path("registrationDoc.fileId"));
        UUID second = UUID.fromString(upload(token, vehicle, "livrete.pdf", pdf, "application/pdf")
                .then().statusCode(200)
                .body("registrationDoc.contentType", equalTo("application/pdf"))
                .extract().path("registrationDoc.fileId"));

        assertThat(second).isNotEqualTo(first);
        assertThat(owner.queryForObject("select registration_doc_file_id from vehicle.vehicle where id = ?",
                UUID.class, vehicle)).isEqualTo(second);
        assertThat(owner.queryForObject("select linked_at is not null from document.file where id = ?",
                Boolean.class, first)).isTrue();
        assertThat(owner.queryForObject("select linked_at is not null from document.file where id = ?",
                Boolean.class, second)).isTrue();
        given().auth().oauth2(token).get("/api/v1/files/{id}", first).then().statusCode(200);
    }

    @Test
    void wrongTypeTooLargeEmptyOrMissingPartAreRejected() throws IOException {
        String token = token(fixtures.customer("Dora Cliente"));
        UUID vehicle = newVehicle(token, "KKK-300-MC");

        upload(token, vehicle, "livrete.gif", TestFiles.bytes("sample.gif"), "image/gif").then().statusCode(415)
                .body("code", equalTo("FILE_TYPE_NOT_ALLOWED"));

        byte[] eleven = new byte[11 * 1024 * 1024];
        System.arraycopy(pdf, 0, eleven, 0, pdf.length);
        upload(token, vehicle, "grande.pdf", eleven, "application/pdf").then().statusCode(413)
                .body("code", equalTo("FILE_TOO_LARGE"));

        upload(token, vehicle, "vazio.pdf", new byte[0], "application/pdf").then().statusCode(400)
                .body("code", equalTo("FILE_EMPTY"));

        given().auth().oauth2(token).multiPart("other", "x")
                .post("/api/v1/me/vehicles/{id}/registration-doc", vehicle)
                .then().statusCode(400)
                .body("code", equalTo("VALIDATION_ERROR"))
                .body("errors[0].field", equalTo("file"))
                .body("errors[0].code", equalTo("REQUIRED"));

        assertThat(owner.queryForObject("select registration_doc_file_id from vehicle.vehicle where id = ?",
                UUID.class, vehicle)).isNull();
    }
}
