package mz.co.moseguros.identity.api;

import static io.restassured.RestAssured.given;
import static mz.co.moseguros.identity.KycTestSupport.DOC;
import static mz.co.moseguros.identity.KycTestSupport.completeProfile;
import static mz.co.moseguros.identity.KycTestSupport.submit;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;

import java.io.IOException;
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
import mz.co.moseguros.identity.KycTestSupport.Part;
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
 * Isolamento do KYC (S4-01 §2, §7 e §8): entre seguradoras (RLS e {@code KycFileAccessPolicy}), entre
 * clientes, e permissões ({@code kyc:review}, {@code self:profile}).
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestcontainersConfiguration.class, SecurityTestConfiguration.class})
@ActiveProfiles("test")
class KycIsolationIT {

    @LocalServerPort
    int port;

    @Autowired
    IdentityFixtures fixtures;

    @Autowired
    IdentityApi identityApi;

    JdbcTemplate owner;
    Part jpeg;
    Part png;

    @BeforeEach
    void setUp() throws IOException {
        RestAssured.port = port;
        owner = fixtures.owner();
        jpeg = new Part("frente.jpg", TestFiles.bytes("sample.jpg"), "image/jpeg");
        png = new Part("foto.png", TestFiles.bytes("sample.png"), "image/png");
    }

    private String token(UserAccount account) {
        return identityApi.issueTokens(account, null).accessToken();
    }

    private static Response post(String token, String path, Map<String, Object> body) {
        return given().auth().oauth2(token).contentType(ContentType.JSON).body(body).post(path);
    }

    @Test
    void otherInsurersAndOtherCustomersSeeNothing() {
        Organization insurerA = fixtures.organization(OrganizationType.INSURER, "Seguros KYC A");
        Organization insurerB = fixtures.organization(OrganizationType.INSURER, "Seguros KYC B");
        String underwriterA = token(fixtures.staff("Subscritor A", insurerA.getId(), "UNDERWRITER"));
        String adminA = token(fixtures.staff("Administrador A", insurerA.getId(), "INSURER_ADMIN"));
        String underwriterB = token(fixtures.staff("Subscritor B", insurerB.getId(), "UNDERWRITER"));

        UserAccount customerC = fixtures.customer("Carla Cliente");
        String tokenC = token(customerC);
        completeProfile(tokenC, "BI", DOC, 0);
        UUID check = UUID.fromString(submit(tokenC, insurerA.getId(), jpeg, png, png).then().statusCode(201)
                .extract().path("id"));
        Response opened = given().auth().oauth2(underwriterA).get("/api/v1/insurer/kyc/{id}", check);
        opened.then().statusCode(200);
        List<String> files = List.of(opened.path("files.docFrontFileId"), opened.path("files.docBackFileId"),
                opened.path("files.selfieFileId"));

        // Os ficheiros ficam ligados e pertencem ao KYC.
        for (String file : files) {
            Map<String, Object> row = owner.queryForMap("select owner_type, owner_id, classification, insurer_id, "
                    + "linked_at from document.file where id = ?::uuid", file);
            assertThat(row.get("owner_type")).isEqualTo("KYC");
            assertThat(row.get("owner_id")).isEqualTo(check);
            assertThat(row.get("classification")).isEqualTo("SENSITIVE");
            assertThat(row.get("insurer_id")).isEqualTo(insurerA.getId());
            assertThat(row.get("linked_at")).isNotNull();
        }
        assertThat(owner.queryForList("select purpose from document.file where owner_id = ? order by purpose",
                String.class, check)).containsExactly("KYC_DOC_BACK", "KYC_DOC_FRONT", "KYC_SELFIE");

        // Seguradora B: não lista, não abre, não decide e não descarrega.
        given().auth().oauth2(underwriterB).get("/api/v1/insurer/kyc").then().statusCode(200)
                .body("content.id", not(hasItem(check.toString())));
        given().auth().oauth2(underwriterB).get("/api/v1/insurer/kyc/{id}", check).then().statusCode(404)
                .body("code", equalTo("KYC_NOT_FOUND"));
        post(underwriterB, "/api/v1/insurer/kyc/" + check + "/verify", Map.of("version", 0)).then().statusCode(404)
                .body("code", equalTo("KYC_NOT_FOUND"));
        for (String file : files) {
            given().auth().oauth2(underwriterB).get("/api/v1/files/{id}", file).then().statusCode(404)
                    .body("code", equalTo("FILE_NOT_FOUND"));
        }

        // Seguradora A (UNDERWRITER e INSURER_ADMIN) e o próprio cliente descarregam.
        for (String file : files) {
            given().auth().oauth2(underwriterA).get("/api/v1/files/{id}", file).then().statusCode(200);
            given().auth().oauth2(adminA).get("/api/v1/files/{id}", file).then().statusCode(200);
            given().auth().oauth2(tokenC).get("/api/v1/files/{id}", file).then().statusCode(200);
        }

        // Cliente D: não vê o KYC de C nem descarrega os ficheiros.
        String tokenD = token(fixtures.customer("Duarte Cliente"));
        given().auth().oauth2(tokenD).get("/api/v1/me/kyc").then().statusCode(200).body("state", equalTo("NONE"));
        for (String file : files) {
            given().auth().oauth2(tokenD).get("/api/v1/files/{id}", file).then().statusCode(404)
                    .body("code", equalTo("FILE_NOT_FOUND"));
        }
        given().get("/api/v1/files/{id}", files.get(0)).then().statusCode(404);

        // Staff da seguradora sem kyc:review (FINANCE) não descarrega.
        String financeA = token(fixtures.staff("Financeiro A", insurerA.getId(), "FINANCE"));
        given().auth().oauth2(financeA).get("/api/v1/files/{id}", files.get(2)).then().statusCode(404);
    }

    @Test
    void permissionsAreEnforced() {
        Organization insurer = fixtures.organization(OrganizationType.INSURER, "Seguros KYC Permissoes");
        String finance = token(fixtures.staff("Financeiro", insurer.getId(), "FINANCE"));
        UserAccount customer = fixtures.customer("Eva Cliente");
        String customerToken = token(customer);
        UUID id = UUID.randomUUID();

        for (String token : List.of(finance, customerToken)) {
            given().auth().oauth2(token).get("/api/v1/insurer/kyc").then().statusCode(403);
            given().auth().oauth2(token).get("/api/v1/insurer/kyc/{id}", id).then().statusCode(403);
            post(token, "/api/v1/insurer/kyc/" + id + "/verify", Map.of("version", 0)).then().statusCode(403);
            post(token, "/api/v1/insurer/kyc/" + id + "/reject", Map.of("reasonCode", "DATA_MISMATCH", "version", 0))
                    .then().statusCode(403);
        }
        given().get("/api/v1/insurer/kyc").then().statusCode(401);
        given().get("/api/v1/insurer/kyc/{id}", id).then().statusCode(401);
        given().contentType(ContentType.JSON).body(Map.of("version", 0))
                .post("/api/v1/insurer/kyc/{id}/verify", id).then().statusCode(401);

        // Staff não usa os endpoints do cliente; sem token → 401.
        given().auth().oauth2(finance).get("/api/v1/me/kyc").then().statusCode(403);
        submit(finance, insurer.getId(), jpeg, png, png).then().statusCode(403);
        given().get("/api/v1/me/kyc").then().statusCode(401);
    }
}
