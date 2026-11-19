package mz.co.moseguros.payment.api;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import mz.co.moseguros.SecurityTestConfiguration;
import mz.co.moseguros.SecurityTestConfiguration.IdentityFixtures;
import mz.co.moseguros.TestcontainersConfiguration;
import mz.co.moseguros.identity.domain.UserAccount;
import mz.co.moseguros.organization.OrganizationType;
import mz.co.moseguros.shared.json.JsonSchemas;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * {@code GET /internal/v1/payment-file-sources} (S5-09 §1): as fontes {@code BANK_REFERENCE} activas de
 * todas as seguradoras e as inactivas que ainda têm referências {@code ACTIVE}, só com os caminhos dos
 * segredos, e fora do contrato público.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestcontainersConfiguration.class, SecurityTestConfiguration.class})
@ActiveProfiles("test")
class InternalFileSourceIT {

    static final String SOURCES = "/internal/v1/payment-file-sources";
    static final String SECRET_VALUE = "valor-secreto-que-nunca-sai-" + UUID.randomUUID();

    @LocalServerPort
    int port;

    @Autowired
    IdentityFixtures fixtures;

    @Autowired
    ObjectMapper objectMapper;

    JdbcTemplate owner;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        owner = fixtures.owner();
    }

    static String serviceToken() {
        return given().auth().preemptive().basic("payment-file-worker", "test-worker-secret-only-for-tests-0001")
                .contentType("application/x-www-form-urlencoded")
                .formParam("grant_type", "client_credentials")
                .post("/internal/v1/auth/token")
                .then().statusCode(200).extract().path("access_token");
    }

    static String entity() {
        return String.valueOf(ThreadLocalRandom.current().nextInt(10000, 89999));
    }

    UUID insurer(String name) {
        return fixtures.organization(OrganizationType.INSURER, name + " " + UUID.randomUUID().toString()
                .substring(0, 4)).getId();
    }

    /** Linha {@code BANK_REFERENCE} gravada como dono, com o segredo da palavra-passe no cofre (só o caminho). */
    UUID bankReference(UUID insurerId, boolean enabled, String format, boolean move) {
        UUID id = UUID.randomUUID();
        String bmeps = "BMEPS".equals(format) ? "\"bmepsCompanyId\": \"800\", \"bmepsProfileId\": \"02\"," : "";
        String dirs = move ? "\"processedDir\": \"processed\", \"errorDir\": \"error\"," : "";
        String settings = """
                {"entity": "%s", "fileFormat": "%s", %s
                 "sftp": {"host": "sftp.seguradora.co.mz", "port": 22, "username": "banco", "authType": "PASSWORD",
                          "inboundDir": "inbound", "moveAfterProcessing": %s, %s "filePattern": "*.txt",
                          "hostKeyFingerprint": "SHA256:47DEQpj8HBSa+/TImW+5JCeuQeRkm5NMpJWZG3hSuFU"}}"""
                .formatted(entity(), format, bmeps, move, dirs);
        String path = "secret/insurers/" + insurerId + "/payments/BANK_REFERENCE/sftp.password";
        owner.update("""
                insert into payment.insurer_payment_method
                  (id, created_at, created_by, updated_at, updated_by, version, insurer_id, method, enabled, enabled_at,
                   environment, display_name, settings, secret_refs, intent_validity_minutes, sort_order)
                values (?, now(), 'test', now(), 'test', 3, ?, 'BANK_REFERENCE', ?, now(), 'SANDBOX', null, ?::jsonb,
                        jsonb_build_object('sftp.password', jsonb_build_object('path', ?::text, 'updatedAt',
                                           '2026-11-18T10:00:00Z')), 30, 1)""",
                id, insurerId, enabled, settings, path);
        return id;
    }

    /** Referência no estado indicado (intenção e referência gravadas como dono). */
    void reference(UUID insurerId, UUID methodConfigId, String status) {
        UserAccount payer = fixtures.customer("Pagador Interno");
        UUID intentId = UUID.randomUUID();
        int invoice = ThreadLocalRandom.current().nextInt(1, 9_999_999);
        owner.update("""
                insert into payment.payment_intent
                  (id, created_at, created_by, updated_at, updated_by, reference, insurer_id, payer_user_id, purpose,
                   target_type, target_id, target_label, amount, method, method_config_id, status, idempotency_key,
                   expires_at, initiated_by_user_id, initiated_via)
                values (?, now(), 'test', now(), 'test', ?, ?, ?, 'NEW_POLICY', 'PROPOSAL', ?, 'PR-2026-000001', 299900,
                        'BANK_REFERENCE', ?, 'PENDING', ?, now() + interval '15 days', ?, 'WEB')""",
                intentId, "PG-2026-" + String.format("%07d", invoice), insurerId, payer.getId(), UUID.randomUUID(),
                methodConfigId, UUID.randomUUID(), payer.getId());
        String entity = owner.queryForObject(
                "select settings ->> 'entity' from payment.insurer_payment_method where id = ?", String.class,
                methodConfigId);
        owner.update("""
                insert into payment.reference_payment
                  (id, created_at, created_by, updated_at, updated_by, intent_id, insurer_id, payer_user_id, entity,
                   reference, invoice_number, reference_month, amount, valid_until, status)
                values (?, now(), 'test', now(), 'test', ?, ?, ?, ?, ?, ?, 11, 299900, now() + interval '15 days', ?)""",
                UUID.randomUUID(), intentId, insurerId, payer.getId(), entity,
                String.format("%07d", invoice) + "1100", invoice, status);
    }

    static Map<String, Object> source(List<Map<String, Object>> sources, UUID id) {
        return sources.stream().filter(s -> id.toString().equals(s.get("sourceId"))).findFirst().orElse(null);
    }

    @Test
    void listsEnabledSourcesOfAllInsurersAndDisabledOnesWithActiveReferences() throws Exception {
        UUID a = insurer("Seg Fontes A");
        UUID b = insurer("Seg Fontes B");
        UUID c = insurer("Seg Fontes C");
        UUID d = insurer("Seg Fontes D");
        UUID sourceA = bankReference(a, true, "MEPS", true);
        UUID sourceB = bankReference(b, true, "BMEPS", false);
        UUID sourceC = bankReference(c, false, "MEPS", true);
        reference(c, sourceC, "ACTIVE");
        UUID sourceD = bankReference(d, false, "MEPS", true);
        reference(d, sourceD, "PAID");
        reference(d, sourceD, "EXPIRED");

        Response r = given().auth().oauth2(serviceToken()).get(SOURCES);
        r.then().statusCode(200).contentType("application/json");
        List<Map<String, Object>> sources = r.jsonPath().getList("sources");

        Map<String, Object> descA = source(sources, sourceA);
        assertThat(descA).isNotNull();
        assertThat(descA).containsEntry("insurerId", a.toString()).containsEntry("enabled", true)
                .containsEntry("fileFormat", "MEPS").containsEntry("version", 3)
                .doesNotContainKeys("bmepsCompanyId", "bmepsProfileId", "pollCron", "timezone");
        assertThat(descA.get("secretRefs")).isEqualTo(Map.of("sftp.password",
                "secret/insurers/" + a + "/payments/BANK_REFERENCE/sftp.password"));
        @SuppressWarnings("unchecked")
        Map<String, Object> sftpA = (Map<String, Object>) descA.get("sftp");
        assertThat(sftpA).containsEntry("moveAfterProcessing", true).containsEntry("processedDir", "processed")
                .containsEntry("errorDir", "error").containsEntry("filePattern", "*.txt")
                .doesNotContainKeys("password", "privateKey", "passphrase");

        Map<String, Object> descB = source(sources, sourceB);
        assertThat(descB).isNotNull();
        assertThat(descB).containsEntry("insurerId", b.toString()).containsEntry("enabled", true)
                .containsEntry("bmepsCompanyId", "800").containsEntry("bmepsProfileId", "02");
        @SuppressWarnings("unchecked")
        Map<String, Object> sftpB = (Map<String, Object>) descB.get("sftp");
        assertThat(sftpB).containsEntry("moveAfterProcessing", false).doesNotContainKeys("processedDir", "errorDir");

        Map<String, Object> descC = source(sources, sourceC);
        assertThat(descC).isNotNull();
        assertThat(descC).containsEntry("enabled", false);

        assertThat(source(sources, sourceD)).isNull();

        String body = r.asString();
        assertThat(body).doesNotContain(SECRET_VALUE).doesNotContain("pollCron").doesNotContain("timezone");

        // a resposta cumpre o contrato do payment-contracts
        JsonNode json = objectMapper.readTree(body);
        assertThat(JsonSchemas.validate("file-source-list.v1.json", json)).isEmpty();
    }

    @Test
    void omitsRowsWhoseSettingsDoNotMatchTheFullSchema() {
        UUID e = insurer("Seg Fontes E");
        UUID id = UUID.randomUUID();
        owner.update("""
                insert into payment.insurer_payment_method
                  (id, created_at, created_by, updated_at, updated_by, version, insurer_id, method, enabled, enabled_at,
                   environment, settings, secret_refs, intent_validity_minutes, sort_order)
                values (?, now(), 'test', now(), 'test', 0, ?, 'BANK_REFERENCE', true, now(), 'SANDBOX',
                        '{"entity": "12345", "fileFormat": "MEPS"}'::jsonb, '{}'::jsonb, 30, 1)""", id, e);

        List<Map<String, Object>> sources = given().auth().oauth2(serviceToken()).get(SOURCES)
                .then().statusCode(200).extract().jsonPath().getList("sources");
        assertThat(source(sources, id)).isNull();
    }

    @Test
    void internalPathsAreNotInThePublicContract() throws Exception {
        String docs = given().get("/v3/api-docs").then().statusCode(200).extract().asString();
        assertThat(docs).doesNotContain("/internal/");
        Path contract = Path.of("..", "api", "openapi.yaml");
        if (Files.exists(contract)) {
            assertThat(Files.readString(contract)).doesNotContain("/internal/");
        }
    }
}
