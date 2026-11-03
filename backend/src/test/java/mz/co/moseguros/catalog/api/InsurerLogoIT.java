package mz.co.moseguros.catalog.api;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.nullValue;

import java.io.IOException;
import java.util.UUID;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import mz.co.moseguros.SecurityTestConfiguration;
import mz.co.moseguros.SecurityTestConfiguration.IdentityFixtures;
import mz.co.moseguros.TestcontainersConfiguration;
import mz.co.moseguros.catalog.CatalogTestSupport;
import mz.co.moseguros.catalog.CatalogTestSupport.Insurer;
import mz.co.moseguros.document.TestFiles;
import mz.co.moseguros.identity.IdentityApi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

/** Logótipo da seguradora (S2-04 §7.1): envio, leitura pública, limites, remoção e permissões. */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestcontainersConfiguration.class, SecurityTestConfiguration.class})
@ActiveProfiles("test")
class InsurerLogoIT {

    static final String BRANDING = "/api/v1/insurer/branding";

    @LocalServerPort
    int port;

    @Autowired
    IdentityFixtures fixtures;

    @Autowired
    IdentityApi identityApi;

    CatalogTestSupport support;
    byte[] png;

    @BeforeEach
    void setUp() throws IOException {
        RestAssured.port = port;
        support = new CatalogTestSupport(fixtures, identityApi);
        png = TestFiles.bytes("sample.png");
    }

    private Response upload(String token, byte[] content, String filename, String contentType) {
        return given().auth().oauth2(token).multiPart("file", filename, content, contentType).post(BRANDING + "/logo");
    }

    @Test
    void pngIsAcceptedAndPublicAndCanBeRemoved() {
        Insurer a = support.insurer("Seg Logo " + UUID.randomUUID().toString().substring(0, 4));
        given().auth().oauth2(a.manager()).get(BRANDING).then().statusCode(200)
                .body("$", hasKey("logoFileId")).body("logoFileId", nullValue());

        String fileId = upload(a.manager(), png, "logo.png", "image/png").then().statusCode(200)
                .extract().path("logoFileId");
        given().auth().oauth2(a.manager()).get(BRANDING).then().statusCode(200).body("logoFileId", equalTo(fileId));
        Response anonymous = given().get("/api/v1/files/{id}", fileId);
        anonymous.then().statusCode(200).header("Content-Type", "image/png");
        assertThat(anonymous.asByteArray()).isEqualTo(png);
        assertThat(fixtures.owner().queryForMap("""
                select owner_type, owner_id, purpose, classification, linked_at is not null as linked
                  from document.file where id = ?::uuid""", fileId))
                .containsEntry("owner_type", "INSURER_LOGO").containsEntry("owner_id", a.id())
                .containsEntry("purpose", "LOGO").containsEntry("classification", "PUBLIC")
                .containsEntry("linked", true);
        assertThat(fixtures.owner().queryForObject(
                "select logo_file_id::text from organization.insurer_profile where organization_id = ?", String.class,
                a.id())).isEqualTo(fileId);

        // um novo envio substitui; o anterior fica guardado
        String second = upload(a.manager(), png, "logo2.png", "image/png").then().statusCode(200)
                .extract().path("logoFileId");
        assertThat(second).isNotEqualTo(fileId);
        given().get("/api/v1/files/{id}", fileId).then().statusCode(200);

        given().auth().oauth2(a.manager()).delete(BRANDING + "/logo").then().statusCode(204);
        given().auth().oauth2(a.manager()).get(BRANDING).then().statusCode(200).body("logoFileId", nullValue());
        given().auth().oauth2(a.manager()).delete(BRANDING + "/logo").then().statusCode(204);

        assertThat(fixtures.owner().queryForObject("""
                select count(*) from audit.audit_event where action = 'INSURER_PROFILE.LOGO_UPDATE' and insurer_id = ?""",
                Integer.class, a.id())).isEqualTo(3);
        assertThat(fixtures.owner().queryForObject("""
                select count(*) from audit.audit_event where action = 'INSURER_PROFILE.LOGO_UPDATE' and insurer_id = ?
                   and before_state ->> 'logoFileId' = ? and after_state -> 'logoFileId' = 'null'::jsonb""",
                Integer.class, a.id(), second)).isEqualTo(1);
    }

    @Test
    void wrongTypesAndLargeFilesAreRejected() throws IOException {
        Insurer a = support.insurer("Seg Logo Erros " + UUID.randomUUID().toString().substring(0, 4));
        upload(a.manager(), support.pdf(), "logo.pdf", "application/pdf").then().statusCode(415)
                .body("code", equalTo("FILE_TYPE_NOT_ALLOWED"));
        upload(a.manager(), TestFiles.bytes("sample.jpg"), "logo.jpg", "image/jpeg").then().statusCode(200);

        byte[] large = new byte[1_048_577];
        System.arraycopy(png, 0, large, 0, png.length);
        upload(a.manager(), large, "grande.png", "image/png").then().statusCode(400)
                .body("code", equalTo("VALIDATION_ERROR"))
                .body("errors[0].field", equalTo("file"))
                .body("errors[0].code", equalTo("OUT_OF_RANGE"));
        byte[] exact = new byte[1_048_576];
        System.arraycopy(png, 0, exact, 0, png.length);
        upload(a.manager(), exact, "limite.png", "image/png").then().statusCode(200);
    }

    @Test
    void underwriterReadsButCannotUploadAndEachInsurerOnlyWritesItsOwn() {
        Insurer a = support.insurer("Seg Logo A " + UUID.randomUUID().toString().substring(0, 4));
        Insurer b = support.insurer("Seg Logo B " + UUID.randomUUID().toString().substring(0, 4));
        String aLogo = upload(a.manager(), png, "a.png", "image/png").then().statusCode(200).extract()
                .path("logoFileId");

        given().auth().oauth2(a.underwriter()).get(BRANDING).then().statusCode(200).body("logoFileId", equalTo(aLogo));
        upload(a.underwriter(), png, "u.png", "image/png").then().statusCode(403);
        given().auth().oauth2(a.underwriter()).delete(BRANDING + "/logo").then().statusCode(403);

        // a B só altera o seu
        String bLogo = upload(b.manager(), png, "b.png", "image/png").then().statusCode(200).extract()
                .path("logoFileId");
        given().auth().oauth2(b.manager()).delete(BRANDING + "/logo").then().statusCode(204);
        given().auth().oauth2(a.manager()).get(BRANDING).then().body("logoFileId", equalTo(aLogo));
        assertThat(bLogo).isNotEqualTo(aLogo);

        given().auth().oauth2(support.customerToken()).get(BRANDING).then().statusCode(403);
        given().auth().oauth2(support.staffToken(null, "PLATFORM_ADMIN")).get(BRANDING).then().statusCode(403);
        given().get(BRANDING).then().statusCode(401);
    }
}
