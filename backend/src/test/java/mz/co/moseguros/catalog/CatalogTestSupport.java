package mz.co.moseguros.catalog;

import static io.restassured.RestAssured.given;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import mz.co.moseguros.SecurityTestConfiguration.IdentityFixtures;
import mz.co.moseguros.document.TestFiles;
import mz.co.moseguros.identity.IdentityApi;
import mz.co.moseguros.identity.domain.UserAccount;
import mz.co.moseguros.organization.OrganizationType;
import mz.co.moseguros.organization.domain.Organization;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Apoio aos testes de integração do catálogo de produtos (S2-04): seguradoras com perfil, tokens
 * de staff e passos da API (criar, preencher e publicar versões).
 */
public final class CatalogTestSupport {

    public static final String PRODUCTS = "/api/v1/insurer/products";
    public static final String VERSIONS = "/api/v1/insurer/product-versions";
    public static final long MIN_CAPITAL = 300_000_000L;

    private final IdentityFixtures fixtures;
    private final IdentityApi identityApi;
    private final byte[] pdf;

    public CatalogTestSupport(IdentityFixtures fixtures, IdentityApi identityApi) {
        this.fixtures = fixtures;
        this.identityApi = identityApi;
        try {
            this.pdf = TestFiles.bytes("sample.pdf");
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /** Seguradora activa (com {@code insurer_profile}), o seu gestor de produto e um subscritor. */
    public record Insurer(UUID id, String name, String manager, String underwriter) {
    }

    public Insurer insurer(String name) {
        Organization org = fixtures.organization(OrganizationType.INSURER, name);
        owner().update("""
                insert into organization.insurer_profile
                  (id, created_at, created_by, updated_at, updated_by, organization_id, short_code, complaints_email)
                values (?, now(), 'test', now(), 'test', ?, ?, ?)""",
                UUID.randomUUID(), org.getId(), "T" + digits(8), "reclamacoes" + digits(6) + "@teste.co.mz");
        return new Insurer(org.getId(), name, staffToken(org.getId(), "PRODUCT_MANAGER"),
                staffToken(org.getId(), "UNDERWRITER"));
    }

    public String staffToken(UUID orgId, String... roles) {
        return token(fixtures.staff("Staff " + digits(4), orgId, roles));
    }

    public String customerToken() {
        return token(fixtures.customer("Cliente Catálogo"));
    }

    public String token(UserAccount account) {
        return identityApi.issueTokens(account, null).accessToken();
    }

    public JdbcTemplate owner() {
        return fixtures.owner();
    }

    public byte[] pdf() {
        return pdf;
    }

    /** Código de produto único. */
    public static String code(String prefix) {
        return prefix + "-" + digits(6);
    }

    // Produtos e versões

    public Response createProduct(String token, String code, String name, String type) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("code", code);
        body.put("name", name);
        body.put("type", type);
        return given().auth().oauth2(token).contentType(ContentType.JSON).body(body).post(PRODUCTS);
    }

    /** Cria o produto e devolve {@code [productId, versionId da v1]}. */
    public Created product(String token, String type) {
        Response r = createProduct(token, code("P"), "Produto " + digits(4), type);
        r.then().statusCode(201);
        return new Created(UUID.fromString(r.path("id")), UUID.fromString(r.path("draftVersion.id")));
    }

    public record Created(UUID productId, UUID versionId) {
    }

    public long version(String token, UUID versionId) {
        return given().auth().oauth2(token).get(VERSIONS + "/{id}", versionId).then().statusCode(200)
                .extract().jsonPath().getLong("version");
    }

    public Response updateEligibility(String token, UUID versionId, List<String> categories, List<String> usages) {
        return given().auth().oauth2(token).contentType(ContentType.JSON)
                .body(Map.of("vehicleCategories", categories, "usages", usages, "version", version(token, versionId)))
                .put(VERSIONS + "/{id}", versionId);
    }

    public Response replaceCoverages(String token, UUID versionId, List<Map<String, Object>> coverages) {
        return replaceCoverages(token, versionId, coverages, version(token, versionId));
    }

    public Response replaceCoverages(String token, UUID versionId, List<Map<String, Object>> coverages, long version) {
        return given().auth().oauth2(token).contentType(ContentType.JSON)
                .body(Map.of("coverages", coverages, "version", version))
                .put(VERSIONS + "/{id}/coverages", versionId);
    }

    public Response upload(String token, UUID versionId, String docType, byte[] content, String filename,
                           String contentType) {
        return given().auth().oauth2(token)
                .multiPart("file", filename, content, contentType)
                .multiPart("docType", docType)
                .post(VERSIONS + "/{id}/documents", versionId);
    }

    public Response uploadPdf(String token, UUID versionId, String docType) {
        return upload(token, versionId, docType, pdf, docType.toLowerCase() + ".pdf", "application/pdf");
    }

    public Response publish(String token, UUID versionId) {
        return given().auth().oauth2(token).contentType(ContentType.JSON)
                .body(Map.of("version", version(token, versionId)))
                .post(VERSIONS + "/{id}/publish", versionId);
    }

    /** Elegibilidade, coberturas e os dois documentos em pt: a versão fica publicável. */
    public void complete(String token, UUID versionId, List<String> categories, List<String> usages,
                         List<Map<String, Object>> coverages) {
        updateEligibility(token, versionId, categories, usages).then().statusCode(200);
        replaceCoverages(token, versionId, coverages).then().statusCode(200);
        uploadPdf(token, versionId, "GENERAL_CONDITIONS").then().statusCode(201);
        uploadPdf(token, versionId, "PRE_CONTRACT_INFO").then().statusCode(201);
    }

    /** Produto {@code MTPL_ONLY} publicado (v1), para {@code LIGHT_PASSENGER}/{@code PRIVATE}. */
    public Created publishedMtplOnly(String token) {
        return published(token, "MTPL_ONLY", List.of("LIGHT_PASSENGER"), List.of("PRIVATE"), List.of(mtpl(MIN_CAPITAL)));
    }

    public Created published(String token, String type, List<String> categories, List<String> usages,
                             List<Map<String, Object>> coverages) {
        Created created = product(token, type);
        complete(token, created.versionId(), categories, usages, coverages);
        publish(token, created.versionId()).then().statusCode(200);
        return created;
    }

    // Coberturas (JSON)

    public static Map<String, Object> money(long amount) {
        return Map.of("amount", amount, "currency", "MZN");
    }

    public static Map<String, Object> mtpl(long capital) {
        Map<String, Object> c = base("MTPL", true, "FIXED");
        c.put("capital", money(capital));
        return c;
    }

    public static Map<String, Object> ownDamage(String code) {
        Map<String, Object> c = base(code, false, "VEHICLE_VALUE");
        c.put("deductibleType", "PERCENT");
        c.put("deductiblePercent", 10.00);
        c.put("deductibleMinAmount", money(2_500_000));
        return c;
    }

    public static Map<String, Object> vtplOptions(long... amounts) {
        Map<String, Object> c = base("VTPL", false, "OPTIONS");
        List<Map<String, Object>> options = new ArrayList<>();
        for (long a : amounts) {
            options.add(money(a));
        }
        c.put("capitalOptions", options);
        return c;
    }

    public static Map<String, Object> base(String code, boolean mandatory, String capitalType) {
        Map<String, Object> c = new LinkedHashMap<>();
        c.put("code", code);
        c.put("mandatory", mandatory);
        c.put("requiresCodes", List.of());
        c.put("capitalType", capitalType);
        c.put("deductibleType", "NONE");
        c.put("territorialScope", List.of("MZ"));
        return c;
    }

    static String digits(int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append(ThreadLocalRandom.current().nextInt(10));
        }
        return sb.toString();
    }
}
