package mz.co.moseguros.catalog.api;

import static io.restassured.RestAssured.given;
import static mz.co.moseguros.catalog.CatalogTestSupport.MIN_CAPITAL;
import static mz.co.moseguros.catalog.CatalogTestSupport.VERSIONS;
import static mz.co.moseguros.catalog.CatalogTestSupport.mtpl;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import io.restassured.RestAssured;
import io.restassured.builder.MultiPartSpecBuilder;
import io.restassured.response.Response;
import mz.co.moseguros.SecurityTestConfiguration;
import mz.co.moseguros.SecurityTestConfiguration.IdentityFixtures;
import mz.co.moseguros.TestcontainersConfiguration;
import mz.co.moseguros.catalog.CatalogTestSupport;
import mz.co.moseguros.catalog.CatalogTestSupport.Created;
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

/**
 * Documentos das versões (S2-04 §3 e §7): só PDF, ligação ao ficheiro, substituição dos documentos
 * principais, limite de 10, remoção e quem os descarrega ({@code ProductVersionFileAccessPolicy}).
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestcontainersConfiguration.class, SecurityTestConfiguration.class})
@ActiveProfiles("test")
class ProductDocumentIT {

    @LocalServerPort
    int port;

    @Autowired
    IdentityFixtures fixtures;

    @Autowired
    IdentityApi identityApi;

    CatalogTestSupport support;
    Insurer insurer;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        support = new CatalogTestSupport(fixtures, identityApi);
        insurer = support.insurer("Seg Documentos " + UUID.randomUUID().toString().substring(0, 4));
    }

    @Test
    void pdfIsAcceptedAndLinkedAndJpegIsRejected() throws IOException {
        String t = insurer.manager();
        Created p = support.product(t, "MTPL_ONLY");
        long before = support.version(t, p.versionId());

        Response uploaded = given().auth().oauth2(t)
                .multiPart("file", "Condições Gerais.pdf", support.pdf(), "application/pdf")
                .multiPart("docType", "GENERAL_CONDITIONS")
                .multiPart(new MultiPartSpecBuilder("Condições Gerais 2026").controlName("title")
                        .mimeType("text/plain").charset(StandardCharsets.UTF_8).build())
                .post(VERSIONS + "/{id}/documents", p.versionId());
        uploaded.then().statusCode(201)
                .body("docType", equalTo("GENERAL_CONDITIONS"))
                .body("language", equalTo("pt"))
                .body("title", equalTo("Condições Gerais 2026"));
        UUID fileId = UUID.fromString(uploaded.path("fileId"));
        assertThat(fixtures.owner().queryForMap("""
                select owner_type, owner_id, purpose, classification, insurer_id, linked_at is not null as linked,
                       content_type from document.file where id = ?""", fileId))
                .containsEntry("owner_type", "PRODUCT_VERSION")
                .containsEntry("owner_id", p.versionId())
                .containsEntry("purpose", "GENERAL_CONDITIONS")
                .containsEntry("classification", "PUBLIC")
                .containsEntry("insurer_id", insurer.id())
                .containsEntry("linked", true)
                .containsEntry("content_type", "application/pdf");
        assertThat(support.version(t, p.versionId())).isEqualTo(before + 1);

        support.upload(t, p.versionId(), "OTHER", TestFiles.bytes("sample.jpg"), "foto.jpg", "image/jpeg")
                .then().statusCode(415).body("code", equalTo("FILE_TYPE_NOT_ALLOWED"));
        given().auth().oauth2(t).multiPart("file", "x.pdf", support.pdf(), "application/pdf")
                .multiPart("docType", "OTHER").multiPart("language", "fr")
                .post(VERSIONS + "/{id}/documents", p.versionId()).then().statusCode(400)
                .body("errors[0].field", equalTo("language"));
        given().auth().oauth2(t).multiPart("file", "x.pdf", support.pdf(), "application/pdf")
                .multiPart("docType", "CONTRACT")
                .post(VERSIONS + "/{id}/documents", p.versionId()).then().statusCode(400)
                .body("errors[0].field", equalTo("docType")).body("errors[0].code", equalTo("INVALID_VALUE"));
        given().auth().oauth2(t).multiPart("file", "x.pdf", support.pdf(), "application/pdf")
                .post(VERSIONS + "/{id}/documents", p.versionId()).then().statusCode(400)
                .body("errors[0].field", equalTo("docType")).body("errors[0].code", equalTo("REQUIRED"));
        assertThat(support.version(t, p.versionId())).isEqualTo(before + 1);
        assertThat(fixtures.owner().queryForObject("""
                select count(*) from audit.audit_event where action = 'PRODUCT_VERSION.DOCUMENT_ADD' and entity_id = ?""",
                Integer.class, p.versionId().toString())).isEqualTo(1);
    }

    @Test
    void aSecondMainDocumentReplacesTheFileAndTheLimitIsTen() {
        String t = insurer.manager();
        Created p = support.product(t, "MTPL_ONLY");
        String firstFile = support.uploadPdf(t, p.versionId(), "GENERAL_CONDITIONS").then().statusCode(201)
                .extract().path("fileId");
        Response second = support.uploadPdf(t, p.versionId(), "GENERAL_CONDITIONS");
        second.then().statusCode(201).body("fileId", org.hamcrest.Matchers.not(equalTo(firstFile)))
                .body("title", nullValue());
        // em inglês é outro documento
        given().auth().oauth2(t).multiPart("file", "en.pdf", support.pdf(), "application/pdf")
                .multiPart("docType", "GENERAL_CONDITIONS").multiPart("language", "en")
                .post(VERSIONS + "/{id}/documents", p.versionId()).then().statusCode(201);
        given().auth().oauth2(t).get(VERSIONS + "/{id}", p.versionId()).then()
                .body("documents.findAll { it.docType == 'GENERAL_CONDITIONS' && it.language == 'pt' }.size()", equalTo(1))
                .body("documents.find { it.docType == 'GENERAL_CONDITIONS' && it.language == 'pt' }.fileId",
                        equalTo(second.path("fileId")))
                .body("documents.size()", equalTo(2));

        for (int i = 0; i < 8; i++) {
            support.uploadPdf(t, p.versionId(), "OTHER").then().statusCode(201);
        }
        support.uploadPdf(t, p.versionId(), "SPECIAL_CONDITIONS").then().statusCode(409)
                .body("code", equalTo("PRODUCT_DOCUMENT_LIMIT_REACHED"));
        // substituir um principal continua possível com 10
        support.uploadPdf(t, p.versionId(), "GENERAL_CONDITIONS").then().statusCode(201);
        assertThat(fixtures.owner().queryForObject("select count(*) from catalog.product_document where product_version_id = ?",
                Integer.class, p.versionId())).isEqualTo(10);
    }

    @Test
    void deleteRemovesTheRowAndUnknownDocumentIs404() {
        String t = insurer.manager();
        Created p = support.product(t, "MTPL_ONLY");
        Response uploaded = support.uploadPdf(t, p.versionId(), "OTHER");
        String documentId = uploaded.path("id");
        long before = support.version(t, p.versionId());

        given().auth().oauth2(t).delete(VERSIONS + "/{id}/documents/{doc}", p.versionId(), documentId)
                .then().statusCode(204);
        assertThat(support.version(t, p.versionId())).isEqualTo(before + 1);
        given().auth().oauth2(t).get(VERSIONS + "/{id}", p.versionId()).then().body("documents.size()", equalTo(0));
        // o ficheiro fica
        assertThat(fixtures.owner().queryForObject("select count(*) from document.file where id = ?::uuid",
                Integer.class, (String) uploaded.path("fileId"))).isEqualTo(1);

        given().auth().oauth2(t).delete(VERSIONS + "/{id}/documents/{doc}", p.versionId(), documentId)
                .then().statusCode(404).body("code", equalTo("PRODUCT_DOCUMENT_NOT_FOUND"));
        given().auth().oauth2(insurer.underwriter())
                .delete(VERSIONS + "/{id}/documents/{doc}", p.versionId(), UUID.randomUUID()).then().statusCode(403);
        assertThat(fixtures.owner().queryForObject("""
                select count(*) from audit.audit_event where action = 'PRODUCT_VERSION.DOCUMENT_REMOVE' and entity_id = ?""",
                Integer.class, p.versionId().toString())).isEqualTo(1);
    }

    @Test
    void draftDocumentsAreOnlyForTheInsurerAndPublishedOnesArePublic() {
        String t = insurer.manager();
        Insurer other = support.insurer("Seg Outra Docs " + UUID.randomUUID().toString().substring(0, 4));
        Created p = support.product(t, "MTPL_ONLY");
        support.complete(t, p.versionId(), List.of("LIGHT_PASSENGER"), List.of("PRIVATE"), List.of(mtpl(MIN_CAPITAL)));
        String fileId = given().auth().oauth2(t).get(VERSIONS + "/{id}", p.versionId()).then()
                .extract().path("documents[0].fileId");

        given().auth().oauth2(t).get("/api/v1/files/{id}", fileId).then().statusCode(200)
                .header("Content-Type", "application/pdf");
        given().auth().oauth2(insurer.underwriter()).get("/api/v1/files/{id}", fileId).then().statusCode(200);
        given().get("/api/v1/files/{id}", fileId).then().statusCode(404).body("code", equalTo("FILE_NOT_FOUND"));
        given().auth().oauth2(other.manager()).get("/api/v1/files/{id}", fileId).then().statusCode(404);
        given().auth().oauth2(support.customerToken()).get("/api/v1/files/{id}", fileId).then().statusCode(404);

        given().auth().oauth2(t).contentType(io.restassured.http.ContentType.JSON)
                .body(Map.of("version", support.version(t, p.versionId())))
                .post(VERSIONS + "/{id}/publish", p.versionId()).then().statusCode(200);

        Response anonymous = given().get("/api/v1/files/{id}", fileId);
        anonymous.then().statusCode(200).header("Content-Type", "application/pdf");
        assertThat(anonymous.asByteArray()).isEqualTo(support.pdf());
        given().auth().oauth2(other.manager()).get("/api/v1/files/{id}", fileId).then().statusCode(200);
    }
}
