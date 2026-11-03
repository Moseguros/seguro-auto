package mz.co.moseguros.document.api;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import mz.co.moseguros.SecurityTestConfiguration;
import mz.co.moseguros.SecurityTestConfiguration.IdentityFixtures;
import mz.co.moseguros.TestcontainersConfiguration;
import mz.co.moseguros.document.DocumentApi;
import mz.co.moseguros.document.FileClassification;
import mz.co.moseguros.document.FileRef;
import mz.co.moseguros.document.StoreFileCommand;
import mz.co.moseguros.document.application.TestFileAccessPolicy;
import mz.co.moseguros.document.TestFiles;
import mz.co.moseguros.identity.IdentityApi;
import mz.co.moseguros.identity.domain.UserAccount;
import mz.co.moseguros.shared.tenant.TenantContext;
import mz.co.moseguros.shared.tenant.TenantSnapshot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.test.context.ActiveProfiles;

/**
 * {@code GET /api/v1/files/{id}} (S2-01 §5): autorização pela {@code FileAccessPolicy}, 404 sem
 * revelar se o ficheiro existe, cabeçalhos da resposta, pedidos anónimos e limite de envio.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestcontainersConfiguration.class, SecurityTestConfiguration.class})
@ActiveProfiles("test")
class FileControllerIT {

    @LocalServerPort
    int port;

    @Autowired
    DocumentApi documents;

    @Autowired
    TestFileAccessPolicy policy;

    @Autowired
    IdentityFixtures fixtures;

    @Autowired
    IdentityApi identityApi;

    @Autowired
    JwtEncoder encoder;

    JdbcTemplate owner;
    byte[] pdf;
    byte[] jpeg;

    @BeforeEach
    void setUp() throws IOException {
        RestAssured.port = port;
        owner = fixtures.owner();
        pdf = TestFiles.bytes("sample.pdf");
        jpeg = TestFiles.bytes("sample.jpg");
    }

    @Test
    void authorisedReadReturnsTheContentWithTheHeaders() {
        UserAccount customer = fixtures.customer("Ana Cliente");
        FileRef ref = store("Condições Gerais é.pdf", pdf);
        policy.allowUser(ref.ownerId(), customer.getId());

        Response response = given().auth().oauth2(token(customer)).get("/api/v1/files/{id}", ref.id());
        response.then().statusCode(200)
                .header("Content-Type", "application/pdf")
                .header("Content-Length", String.valueOf(pdf.length))
                .header("Content-Disposition",
                        "inline; filename=\"Condi__es Gerais _.pdf\"; filename*=UTF-8''Condi%C3%A7%C3%B5es%20Gerais%20%C3%A9.pdf")
                .header("Cache-Control", "private, no-store")
                .header("X-Content-Type-Options", "nosniff");
        assertThat(response.asByteArray()).isEqualTo(pdf);
        assertThat(response.headers().getValues("Cache-Control")).hasSize(1);
        assertThat(response.headers().getValues("X-Content-Type-Options")).hasSize(1);

        given().auth().oauth2(token(customer)).queryParam("download", true).get("/api/v1/files/{id}", ref.id())
                .then().statusCode(200)
                .header("Content-Disposition",
                        "attachment; filename=\"Condi__es Gerais _.pdf\"; filename*=UTF-8''Condi%C3%A7%C3%B5es%20Gerais%20%C3%A9.pdf");
    }

    @Test
    void anonymousReadOfAFileThePolicyAllowsToAnyone() {
        FileRef ref = store("logo.jpg", jpeg);
        policy.allowAnyone(ref.ownerId());

        Response response = given().get("/api/v1/files/{id}", ref.id());
        response.then().statusCode(200).header("Content-Type", "image/jpeg");
        assertThat(response.asByteArray()).isEqualTo(jpeg);
    }

    @Test
    void refusedNonexistentWithoutPolicyAndPurgedAllGiveTheSame404() {
        UserAccount customer = fixtures.customer("Bruno Cliente");
        String token = token(customer);

        FileRef refused = store("recusado.pdf", pdf);
        policy.allowUser(refused.ownerId(), UUID.randomUUID());

        UUID withoutPolicy = UUID.randomUUID();
        owner.update("""
                insert into document.file (id, created_at, created_by, updated_at, updated_by, owner_type, owner_id,
                  purpose, original_filename, content_type, size_bytes, sha256, storage_key, classification)
                values (?, now(), 'test', now(), 'test', 'NO_POLICY_TYPE', ?, 'ANY', 'x.pdf', 'application/pdf', 1,
                  repeat('a', 64), ?, 'PUBLIC')""", withoutPolicy, UUID.randomUUID(), "2026/12/01/" + withoutPolicy + ".pdf");

        FileRef purged = store("limpo.pdf", pdf);
        policy.allowAnyone(purged.ownerId());
        owner.update("update document.file set purged_at = now() where id = ?", purged.id());

        List<Map<String, Object>> bodies = List.of(
                notFound(token, refused.id()),
                notFound(token, UUID.randomUUID()),
                notFound(token, withoutPolicy),
                notFound(token, purged.id()));
        assertThat(bodies).allSatisfy(body -> assertThat(body).isEqualTo(bodies.get(0)));
        assertThat(bodies.get(0)).containsEntry("code", "FILE_NOT_FOUND").containsEntry("status", 404);
    }

    @Test
    void expiredTokenIs401() {
        FileRef ref = store("publico.pdf", pdf);
        policy.allowAnyone(ref.ownerId());
        Instant past = Instant.now().minus(Duration.ofHours(1));
        String expired = encoder.encode(JwtEncoderParameters.from(
                JwsHeader.with(SignatureAlgorithm.RS256).keyId("dev").build(),
                JwtClaimsSet.builder().issuer("https://api.moseguros.co.mz").audience(List.of("moseguros-api"))
                        .subject(UUID.randomUUID().toString()).issuedAt(past).expiresAt(past.plusSeconds(900))
                        .claim("type", "CUSTOMER").build())).getTokenValue();

        given().auth().oauth2(expired).get("/api/v1/files/{id}", ref.id())
                .then().statusCode(401).body("code", equalTo("UNAUTHORIZED"));
        given().auth().oauth2("not-a-jwt").get("/api/v1/files/{id}", ref.id())
                .then().statusCode(401).body("code", equalTo("UNAUTHORIZED"));
    }

    @Test
    void withoutTokenAFileThatRequiresAUserIs404() {
        UserAccount customer = fixtures.customer("Carla Cliente");
        FileRef ref = store("privado.pdf", pdf);
        policy.allowUser(ref.ownerId(), customer.getId());

        given().get("/api/v1/files/{id}", ref.id())
                .then().statusCode(404).body("code", equalTo("FILE_NOT_FOUND"));
        given().auth().oauth2(token(customer)).get("/api/v1/files/{id}", ref.id()).then().statusCode(200);
    }

    @Test
    void missingContentIs500FileStorageError() {
        FileRef ref = store("perdido.pdf", pdf);
        policy.allowAnyone(ref.ownerId());
        owner.update("update document.file set storage_key = ? where id = ?",
                "2026/12/01/" + UUID.randomUUID() + ".pdf", ref.id());

        given().get("/api/v1/files/{id}", ref.id())
                .then().statusCode(500).body("code", equalTo("FILE_STORAGE_ERROR"));
    }

    @Test
    void invalidIdIsAValidationError() {
        given().get("/api/v1/files/not-a-uuid").then().statusCode(400).body("code", equalTo("VALIDATION_ERROR"));
    }

    @Test
    void multipartUploadAbove10MbIs413() {
        String token = token(fixtures.customer("Dora Cliente"));
        byte[] eleven = new byte[11_000_000];
        System.arraycopy(pdf, 0, eleven, 0, pdf.length);

        given().auth().oauth2(token)
                .multiPart("file", "grande.pdf", eleven, "application/pdf")
                .post("/api/v1/test-probes/files")
                .then().statusCode(413).body("code", equalTo("FILE_TOO_LARGE"));
    }

    @Test
    void multipartUploadWithoutTheFilePartIsAFieldError() {
        String token = token(fixtures.customer("Eva Cliente"));
        given().auth().oauth2(token)
                .multiPart("other", "x")
                .post("/api/v1/test-probes/files")
                .then().statusCode(400)
                .body("code", equalTo("VALIDATION_ERROR"))
                .body("errors[0].field", equalTo("file"))
                .body("errors[0].code", equalTo("REQUIRED"));
    }

    @Test
    void multipartUploadOfAnAllowedFileIsStored() {
        String token = token(fixtures.customer("Filipa Cliente"));
        given().auth().oauth2(token)
                .multiPart("file", "foto.png", jpeg, "application/octet-stream")
                .post("/api/v1/test-probes/files")
                .then().statusCode(200)
                .body("contentType", equalTo("image/jpeg"))
                .body("originalFilename", equalTo("foto.jpg"));
    }

    private Map<String, Object> notFound(String token, UUID id) {
        Map<String, Object> body = new LinkedHashMap<>(given().auth().oauth2(token).get("/api/v1/files/{id}", id)
                .then().statusCode(404).extract().jsonPath().getMap("$"));
        body.remove("instance"); // caminho do pedido (tem o id)
        return body;
    }

    private FileRef store(String filename, byte[] content) {
        return system(() -> documents.store(new StoreFileCommand(TestFileAccessPolicy.OWNER_TYPE, UUID.randomUUID(),
                "TEST_FILE", FileClassification.PERSONAL, null, filename, null, null, content)));
    }

    private String token(UserAccount account) {
        return identityApi.issueTokens(account, null).accessToken();
    }

    private static <T> T system(Supplier<T> action) {
        TenantSnapshot previous = TenantContext.current();
        TenantContext.set(TenantSnapshot.system(null));
        try {
            return action.get();
        } finally {
            TenantContext.set(previous);
        }
    }
}
