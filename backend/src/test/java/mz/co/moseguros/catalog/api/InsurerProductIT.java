package mz.co.moseguros.catalog.api;

import static io.restassured.RestAssured.given;
import static mz.co.moseguros.catalog.CatalogTestSupport.PRODUCTS;
import static mz.co.moseguros.catalog.CatalogTestSupport.VERSIONS;
import static mz.co.moseguros.catalog.CatalogTestSupport.code;
import static mz.co.moseguros.catalog.CatalogTestSupport.mtpl;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import mz.co.moseguros.SecurityTestConfiguration;
import mz.co.moseguros.SecurityTestConfiguration.IdentityFixtures;
import mz.co.moseguros.TestcontainersConfiguration;
import mz.co.moseguros.catalog.CatalogTestSupport;
import mz.co.moseguros.catalog.CatalogTestSupport.Created;
import mz.co.moseguros.catalog.CatalogTestSupport.Insurer;
import mz.co.moseguros.identity.IdentityApi;
import mz.co.moseguros.shared.tenant.TenantContext;
import mz.co.moseguros.shared.tenant.TenantSnapshot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Produtos da seguradora (S2-04 §2 e §3): criação, código único por seguradora, tipo bloqueado
 * depois de publicado, retirada, isolamento entre seguradoras (API e RLS) e acesso negado.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestcontainersConfiguration.class, SecurityTestConfiguration.class})
@ActiveProfiles("test")
class InsurerProductIT {

    @LocalServerPort
    int port;

    @Autowired
    IdentityFixtures fixtures;

    @Autowired
    IdentityApi identityApi;

    @Autowired
    JdbcTemplate jdbc;

    @Autowired
    TransactionTemplate tx;

    CatalogTestSupport support;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        support = new CatalogTestSupport(fixtures, identityApi);
    }

    @Test
    void createProductStartsWithAnEmptyDraftVersionOne() {
        Insurer a = support.insurer("Seg Produtos A");
        String code = code("AUTO");
        Response created = support.createProduct(a.manager(), code, "Auto Teste", "COMPREHENSIVE");
        created.then().statusCode(201)
                .header("Location", equalTo("/api/v1/insurer/products/" + created.path("id")))
                .body("code", equalTo(code))
                .body("status", equalTo("DRAFT"))
                .body("type", equalTo("COMPREHENSIVE"))
                .body("currentVersion", nullValue())
                .body("draftVersion.versionNo", equalTo(1))
                .body("draftVersion.status", equalTo("DRAFT"))
                .body("versions.size()", equalTo(1));
        String versionId = created.path("draftVersion.id");
        given().auth().oauth2(a.manager()).get(VERSIONS + "/{id}", versionId).then().statusCode(200)
                .body("versionNo", equalTo(1))
                .body("status", equalTo("DRAFT"))
                .body("productCode", equalTo(code))
                .body("vehicleCategories.size()", equalTo(0))
                .body("coverages.size()", equalTo(0))
                .body("documents.size()", equalTo(0))
                .body("editable", equalTo(true));

        // mesmo código na mesma seguradora → 409; noutra seguradora → 201
        support.createProduct(a.manager(), code, "Outro", "MTPL_ONLY").then().statusCode(409)
                .body("code", equalTo("PRODUCT_CODE_ALREADY_EXISTS"));
        Insurer b = support.insurer("Seg Produtos B");
        support.createProduct(b.manager(), code, "Auto da B", "MTPL_ONLY").then().statusCode(201);

        assertThat(fixtures.owner().queryForObject("""
                select count(*) from audit.audit_event where action = 'PRODUCT.CREATE' and insurer_id = ?
                   and actor_type = 'STAFF' and after_state ->> 'code' = ?""", Integer.class, a.id(), code))
                .isEqualTo(1);
    }

    @Test
    void invalidRequestsAreFieldErrors() {
        Insurer a = support.insurer("Seg Validação");
        support.createProduct(a.manager(), "minusculas", "Auto", "MTPL_ONLY").then().statusCode(400)
                .body("code", equalTo("VALIDATION_ERROR"))
                .body("errors[0].field", equalTo("code"));
        support.createProduct(a.manager(), code("X"), "A", "MTPL_ONLY").then().statusCode(400)
                .body("errors[0].field", equalTo("name"));
    }

    @Test
    void typeIsLockedAfterPublishingAndRetireIsIrreversible() {
        Insurer a = support.insurer("Seg Retirar");
        Created p = support.product(a.manager(), "MTPL_ONLY");

        // em DRAFT o tipo muda
        update(a.manager(), p.productId(), "Nome Novo", "FLEET").then().statusCode(200)
                .body("type", equalTo("FLEET")).body("name", equalTo("Nome Novo"));
        update(a.manager(), p.productId(), "Nome Novo", "MTPL_ONLY").then().statusCode(200);

        support.complete(a.manager(), p.versionId(), List.of("LIGHT_PASSENGER"), List.of("PRIVATE"),
                List.of(mtpl(CatalogTestSupport.MIN_CAPITAL)));
        support.publish(a.manager(), p.versionId()).then().statusCode(200);

        update(a.manager(), p.productId(), "Nome Novo", "COMPREHENSIVE").then().statusCode(409)
                .body("code", equalTo("PRODUCT_TYPE_LOCKED"));
        update(a.manager(), p.productId(), "Só o nome", "MTPL_ONLY").then().statusCode(200)
                .body("name", equalTo("Só o nome"));
        // version antiga
        given().auth().oauth2(a.manager()).contentType(ContentType.JSON)
                .body(Map.of("name", "X1", "type", "MTPL_ONLY", "version", 0))
                .put(PRODUCTS + "/{id}", p.productId()).then().statusCode(409)
                .body("code", equalTo("CONCURRENT_MODIFICATION"));

        given().auth().oauth2(a.manager()).post(PRODUCTS + "/{id}/retire", p.productId()).then().statusCode(200)
                .body("status", equalTo("RETIRED"))
                .body("currentVersion", nullValue())
                .body("versions[0].status", equalTo("RETIRED"))
                .body("versions[0].validTo", equalTo(today()));

        given().auth().oauth2(a.manager()).post(PRODUCTS + "/{id}/versions", p.productId()).then().statusCode(409)
                .body("code", equalTo("PRODUCT_RETIRED"));
        given().auth().oauth2(a.manager()).post(PRODUCTS + "/{id}/retire", p.productId()).then().statusCode(409)
                .body("code", equalTo("PRODUCT_RETIRED"));
        update(a.manager(), p.productId(), "Depois", "MTPL_ONLY").then().statusCode(409)
                .body("code", equalTo("PRODUCT_RETIRED"));

        assertThat(fixtures.owner().queryForObject("""
                select count(*) from audit.audit_event where action = 'PRODUCT.RETIRE' and entity_id = ?""",
                Integer.class, p.productId().toString())).isEqualTo(1);
    }

    @Test
    void anotherInsurerCannotSeeOrChangeTheProduct() {
        Insurer a = support.insurer("Seg Isolada A");
        Insurer b = support.insurer("Seg Isolada B");
        Created p = support.product(a.manager(), "MTPL_ONLY");
        support.updateEligibility(a.manager(), p.versionId(), List.of("HEAVY"), List.of("GOODS")).then().statusCode(200);
        support.replaceCoverages(a.manager(), p.versionId(), List.of(mtpl(CatalogTestSupport.MIN_CAPITAL)))
                .then().statusCode(200);
        support.uploadPdf(a.manager(), p.versionId(), "GENERAL_CONDITIONS").then().statusCode(201);
        long version = support.version(a.manager(), p.versionId());
        String bt = b.manager();

        given().auth().oauth2(bt).get(PRODUCTS + "/{id}", p.productId()).then().statusCode(404)
                .body("code", equalTo("PRODUCT_NOT_FOUND"));
        given().auth().oauth2(bt).contentType(ContentType.JSON)
                .body(Map.of("name", "Roubado", "type", "MTPL_ONLY", "version", 0))
                .put(PRODUCTS + "/{id}", p.productId()).then().statusCode(404);
        given().auth().oauth2(bt).post(PRODUCTS + "/{id}/retire", p.productId()).then().statusCode(404);
        given().auth().oauth2(bt).post(PRODUCTS + "/{id}/versions", p.productId()).then().statusCode(404);
        given().auth().oauth2(bt).get(VERSIONS + "/{id}", p.versionId()).then().statusCode(404)
                .body("code", equalTo("PRODUCT_VERSION_NOT_FOUND"));
        support.replaceCoverages(bt, p.versionId(), List.of(mtpl(CatalogTestSupport.MIN_CAPITAL)), version)
                .then().statusCode(404).body("code", equalTo("PRODUCT_VERSION_NOT_FOUND"));
        support.uploadPdf(bt, p.versionId(), "OTHER").then().statusCode(404)
                .body("code", equalTo("PRODUCT_VERSION_NOT_FOUND"));
        given().auth().oauth2(bt).contentType(ContentType.JSON).body(Map.of("version", version))
                .post(VERSIONS + "/{id}/publish", p.versionId()).then().statusCode(404)
                .body("code", equalTo("PRODUCT_VERSION_NOT_FOUND"));
        given().auth().oauth2(bt).get(PRODUCTS).then().statusCode(200)
                .body("content.id", not(hasItem(p.productId().toString())));
        given().auth().oauth2(a.manager()).get(PRODUCTS).then().statusCode(200)
                .body("content.id", hasItem(p.productId().toString()));

        // nada mudou na A
        assertThat(support.version(a.manager(), p.versionId())).isEqualTo(version);

        // consulta directa com o âmbito ORG da B: nenhuma linha da A nas quatro tabelas
        TenantSnapshot scopeB = TenantSnapshot.org(UUID.randomUUID(), b.id(), b.id());
        for (String table : List.of("catalog.product", "catalog.product_version", "catalog.coverage",
                "catalog.product_document")) {
            assertThat(inScope(scopeB, () -> jdbc.queryForObject(
                    "select count(*) from " + table + " where insurer_id = ?", Integer.class, a.id())))
                    .as(table).isZero();
            assertThat(inScope(TenantSnapshot.org(UUID.randomUUID(), a.id(), a.id()), () -> jdbc.queryForObject(
                    "select count(*) from " + table + " where insurer_id = ?", Integer.class, a.id())))
                    .as(table).isPositive();
        }
    }

    @Test
    void underwriterReadsOnlyAndOthersAreForbidden() {
        Insurer a = support.insurer("Seg Permissões");
        Created p = support.product(a.manager(), "MTPL_ONLY");

        given().auth().oauth2(a.underwriter()).get(PRODUCTS).then().statusCode(200);
        given().auth().oauth2(a.underwriter()).get(PRODUCTS + "/{id}", p.productId()).then().statusCode(200);
        given().auth().oauth2(a.underwriter()).get(VERSIONS + "/{id}", p.versionId()).then().statusCode(200);
        support.createProduct(a.underwriter(), code("U"), "Subscritor", "MTPL_ONLY").then().statusCode(403)
                .body("code", equalTo("FORBIDDEN"));
        update(a.underwriter(), p.productId(), "Novo nome", "MTPL_ONLY").then().statusCode(403);
        given().auth().oauth2(a.underwriter()).post(PRODUCTS + "/{id}/retire", p.productId()).then().statusCode(403);

        String customer = support.customerToken();
        String platformAdmin = support.staffToken(null, "PLATFORM_ADMIN");
        for (String token : List.of(customer, platformAdmin)) {
            given().auth().oauth2(token).get(PRODUCTS).then().statusCode(403);
            given().auth().oauth2(token).get(PRODUCTS + "/{id}", p.productId()).then().statusCode(403);
            support.createProduct(token, code("F"), "Proibido", "MTPL_ONLY").then().statusCode(403);
        }
        given().get(PRODUCTS).then().statusCode(401).body("code", equalTo("UNAUTHORIZED"));
        given().get(PRODUCTS + "/{id}", p.productId()).then().statusCode(401);
    }

    @Test
    void listFiltersAndSorts() {
        Insurer a = support.insurer("Seg Lista");
        support.createProduct(a.manager(), code("BB"), "Bravo Frota", "FLEET").then().statusCode(201);
        support.createProduct(a.manager(), code("AA"), "Alfa Fronteira", "BORDER").then().statusCode(201);
        Created published = support.publishedMtplOnly(a.manager());

        given().auth().oauth2(a.manager()).get(PRODUCTS).then().statusCode(200)
                .body("totalElements", equalTo(3))
                .body("content[0].name", equalTo("Alfa Fronteira"))
                .body("content[1].name", equalTo("Bravo Frota"));
        given().auth().oauth2(a.manager()).queryParam("type", "FLEET").get(PRODUCTS).then().statusCode(200)
                .body("totalElements", equalTo(1)).body("content[0].hasDraft", equalTo(true))
                .body("content[0].currentVersionNo", nullValue());
        given().auth().oauth2(a.manager()).queryParam("status", "PUBLISHED").get(PRODUCTS).then().statusCode(200)
                .body("totalElements", equalTo(1))
                .body("content[0].id", equalTo(published.productId().toString()))
                .body("content[0].currentVersionNo", equalTo(1))
                .body("content[0].hasDraft", equalTo(false));
        given().auth().oauth2(a.manager()).queryParam("q", "fronteira").get(PRODUCTS).then().statusCode(200)
                .body("totalElements", equalTo(1));
        given().auth().oauth2(a.manager()).queryParam("q", "bb-").get(PRODUCTS).then().statusCode(200)
                .body("totalElements", equalTo(1));
        given().auth().oauth2(a.manager()).queryParam("sort", "name,desc").get(PRODUCTS).then().statusCode(200)
                .body("content[2].name", equalTo("Alfa Fronteira"));
        given().auth().oauth2(a.manager()).queryParam("sort", "status").get(PRODUCTS).then().statusCode(400)
                .body("errors[0].code", equalTo("INVALID_SORT"));
    }

    private Response update(String token, UUID productId, String name, String type) {
        long version = given().auth().oauth2(token).get(PRODUCTS + "/{id}", productId).then().extract()
                .jsonPath().getLong("version");
        return given().auth().oauth2(token).contentType(ContentType.JSON)
                .body(Map.of("name", name, "type", type, "version", version))
                .put(PRODUCTS + "/{id}", productId);
    }

    static String today() {
        return java.time.LocalDate.now(java.time.ZoneId.of("Africa/Maputo")).toString();
    }

    private <T> T inScope(TenantSnapshot snapshot, Supplier<T> work) {
        TenantSnapshot previous = TenantContext.current();
        TenantContext.set(snapshot);
        try {
            return tx.execute(status -> work.get());
        } finally {
            TenantContext.set(previous);
        }
    }
}
