package mz.co.moseguros.catalog.api;

import static io.restassured.RestAssured.given;
import static mz.co.moseguros.catalog.CatalogTestSupport.MIN_CAPITAL;
import static mz.co.moseguros.catalog.CatalogTestSupport.PRODUCTS;
import static mz.co.moseguros.catalog.CatalogTestSupport.mtpl;
import static mz.co.moseguros.catalog.CatalogTestSupport.ownDamage;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.not;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import mz.co.moseguros.SecurityTestConfiguration;
import mz.co.moseguros.SecurityTestConfiguration.IdentityFixtures;
import mz.co.moseguros.TestcontainersConfiguration;
import mz.co.moseguros.catalog.CatalogTestSupport;
import mz.co.moseguros.catalog.CatalogTestSupport.Created;
import mz.co.moseguros.catalog.CatalogTestSupport.Insurer;
import mz.co.moseguros.identity.IdentityApi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

/**
 * Catálogo público (S2-04 §6): sem token, só versões em vigor de produtos não retirados de
 * seguradoras activas, filtros, e consulta de versões publicadas (também substituídas). Os testes
 * partilham a BD: as verificações olham só para as seguradoras criadas aqui.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestcontainersConfiguration.class, SecurityTestConfiguration.class})
@ActiveProfiles("test")
class PublicCatalogIT {

    static final String CATALOG = "/api/v1/catalog/products";

    @LocalServerPort
    int port;

    @Autowired
    IdentityFixtures fixtures;

    @Autowired
    IdentityApi identityApi;

    CatalogTestSupport support;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        support = new CatalogTestSupport(fixtures, identityApi);
    }

    private static String suffix() {
        return UUID.randomUUID().toString().substring(0, 6);
    }

    /** Ids das versões da lista que são das seguradoras indicadas. */
    private List<String> listed(Map<String, ?> params, UUID... insurers) {
        List<String> ids = List.of(insurers).stream().map(UUID::toString).toList();
        return all(params).stream()
                .filter(c -> ids.contains(((Map<?, ?>) c.get("insurer")).get("id")))
                .map(c -> (String) c.get("productVersionId"))
                .toList();
    }

    /** Todas as páginas da lista. */
    private List<Map<String, Object>> all(Map<String, ?> params) {
        List<Map<String, Object>> content = new ArrayList<>();
        for (int page = 0; ; page++) {
            JsonPath body = given().queryParams(params).queryParam("size", 100).queryParam("page", page).get(CATALOG)
                    .then().statusCode(200).extract().jsonPath();
            content.addAll(body.getList("content"));
            if ((page + 1) * 100L >= body.getLong("totalElements")) {
                return content;
            }
        }
    }

    @Test
    void listsOnlyPublishedVersionsWithFilters() {
        Insurer a = support.insurer("Alfa Seguros " + suffix());
        Insurer b = support.insurer("Beta Seguros " + suffix());
        Created aMtpl = support.published(a.manager(), "MTPL_ONLY", List.of("LIGHT_PASSENGER", "MOTORCYCLE"),
                List.of("PRIVATE"), List.of(mtpl(MIN_CAPITAL)));
        Created aComp = support.published(a.manager(), "COMPREHENSIVE", List.of("LIGHT_PASSENGER"),
                List.of("PRIVATE", "TAXI"), List.of(mtpl(MIN_CAPITAL), ownDamage("OD_COLLISION")));
        Created bHeavy = support.published(b.manager(), "FLEET", List.of("HEAVY"), List.of("GOODS"),
                List.of(mtpl(MIN_CAPITAL)));
        Created aDraft = support.product(a.manager(), "MTPL_ONLY");
        // nova versão em preparação de um publicado: a lista mostra a publicada
        given().auth().oauth2(a.manager()).post(PRODUCTS + "/{id}/versions", aMtpl.productId()).then().statusCode(201);

        assertThat(listed(Map.of(), a.id(), b.id())).containsExactlyInAnyOrder(aMtpl.versionId().toString(),
                aComp.versionId().toString(), bHeavy.versionId().toString());
        assertThat(listed(Map.of(), a.id())).doesNotContain(aDraft.versionId().toString());
        assertThat(listed(Map.of("vehicleCategory", "MOTORCYCLE"), a.id(), b.id()))
                .containsExactly(aMtpl.versionId().toString());
        assertThat(listed(Map.of("usage", "TAXI"), a.id(), b.id())).containsExactly(aComp.versionId().toString());
        assertThat(listed(Map.of("type", "FLEET"), a.id(), b.id())).containsExactly(bHeavy.versionId().toString());
        assertThat(listed(Map.of("vehicleCategory", "LIGHT_PASSENGER", "usage", "PRIVATE"), a.id(), b.id()))
                .containsExactlyInAnyOrder(aMtpl.versionId().toString(), aComp.versionId().toString());
        assertThat(listed(Map.of("vehicleCategory", "BUS"), a.id(), b.id())).isEmpty();

        // conteúdo de uma linha; um token inválido é ignorado no caminho público
        given().auth().oauth2("invalid").queryParam("type", "COMPREHENSIVE").queryParam("size", 100).get(CATALOG)
                .then().statusCode(200)
                .rootPath("content.find { it.productVersionId == '" + aComp.versionId() + "' }")
                .body("productId", equalTo(aComp.productId().toString()))
                .body("status", equalTo("PUBLISHED"))
                .body("versionNo", equalTo(1))
                .body("insurer.displayName", equalTo(a.name()))
                .body("type", equalTo("COMPREHENSIVE"))
                .body("usages", contains("PRIVATE", "TAXI"))
                .body("coverages.code", contains("MTPL", "OD_COLLISION"))
                .body("coverages[0].capital.amount", equalTo((int) MIN_CAPITAL))
                .body("coverages[1].deductiblePercent", equalTo(10.0f))
                .body("coverages[1]", not(hasKey("territorialScope")))
                .body("coverages[1]", not(hasKey("requiresCodes")))
                .body("documents.size()", equalTo(2));

        given().queryParam("vehicleCategory", "NOT_A_CATEGORY").get(CATALOG).then().statusCode(400)
                .body("errors[0].code", equalTo("TYPE_MISMATCH"));
    }

    @Test
    void orderIsByInsurerNameThenProductName() {
        String s = suffix();
        Insurer z = support.insurer("Zulu Ordem " + s);
        Insurer m = support.insurer("Mike Ordem " + s);
        support.published(z.manager(), "MTPL_ONLY", List.of("TRAILER"), List.of("PRIVATE"), List.of(mtpl(MIN_CAPITAL)));
        Created m1 = support.product(m.manager(), "MTPL_ONLY");
        given().auth().oauth2(m.manager()).contentType(ContentType.JSON)
                .body(Map.of("name", "Zeta", "type", "MTPL_ONLY", "version",
                        given().auth().oauth2(m.manager()).get(PRODUCTS + "/{id}", m1.productId()).then().extract()
                                .jsonPath().getLong("version")))
                .put(PRODUCTS + "/{id}", m1.productId()).then().statusCode(200);
        support.complete(m.manager(), m1.versionId(), List.of("TRAILER"), List.of("PRIVATE"), List.of(mtpl(MIN_CAPITAL)));
        support.publish(m.manager(), m1.versionId()).then().statusCode(200);
        Created m2 = support.published(m.manager(), "MTPL_ONLY", List.of("TRAILER"), List.of("PRIVATE"),
                List.of(mtpl(MIN_CAPITAL)));

        List<Map<String, Object>> rows = all(Map.of("vehicleCategory", "TRAILER")).stream()
                .filter(c -> ((String) ((Map<?, ?>) c.get("insurer")).get("displayName")).endsWith(s))
                .toList();
        String m2Name = given().auth().oauth2(m.manager()).get(PRODUCTS + "/{id}", m2.productId()).then().extract()
                .path("name");
        assertThat(rows).extracting(c -> (Object) ((Map<?, ?>) c.get("insurer")).get("displayName"))
                .containsExactly("Mike Ordem " + s, "Mike Ordem " + s, "Zulu Ordem " + s);
        assertThat(rows.subList(0, 2)).extracting(c -> c.get("name")).containsExactly(m2Name, "Zeta");

        // paginação
        given().queryParam("vehicleCategory", "TRAILER").queryParam("size", 1).queryParam("page", 1).get(CATALOG)
                .then().statusCode(200).body("size", equalTo(1)).body("page", equalTo(1))
                .body("content.size()", equalTo(1));
    }

    @Test
    void retiredProductsAndSuspendedInsurersDoNotAppear() {
        Insurer a = support.insurer("Gama Seguros " + suffix());
        Created retired = support.publishedMtplOnly(a.manager());
        Created kept = support.publishedMtplOnly(a.manager());
        given().auth().oauth2(a.manager()).post(PRODUCTS + "/{id}/retire", retired.productId()).then().statusCode(200);
        assertThat(listed(Map.of(), a.id())).containsExactly(kept.versionId().toString());
        // a versão retirada continua consultável por id (apólices antigas)
        given().get("/api/v1/catalog/product-versions/{id}", retired.versionId()).then().statusCode(200)
                .body("status", equalTo("RETIRED"));

        String admin = support.staffToken(null, "PLATFORM_ADMIN");
        given().auth().oauth2(admin).contentType(ContentType.JSON).body(Map.of("reason", "Teste do catálogo"))
                .post("/api/v1/organizations/{id}/suspend", a.id()).then().statusCode(200);
        assertThat(listed(Map.of(), a.id())).isEmpty();
        given().get("/api/v1/catalog/product-versions/{id}", kept.versionId()).then().statusCode(404)
                .body("code", equalTo("PRODUCT_VERSION_NOT_FOUND"));

        given().auth().oauth2(admin).post("/api/v1/organizations/{id}/reactivate", a.id()).then().statusCode(200);
        assertThat(listed(Map.of(), a.id())).containsExactly(kept.versionId().toString());
    }

    @Test
    void getVersionAcceptsAnyPublishedVersionButNotDrafts() {
        Insurer a = support.insurer("Delta Seguros " + suffix());
        Map<String, Object> theft = ownDamage("OD_THEFT");
        theft.put("requiresCodes", List.of("OD_COLLISION", "OD_FIRE", "OD_NATURE"));
        theft.put("exclusions", "Chaves no veículo");
        Created p = support.published(a.manager(), "COMPREHENSIVE", List.of("LIGHT_MIXED"), List.of("PRIVATE"),
                List.of(mtpl(MIN_CAPITAL), ownDamage("OD_COLLISION"), ownDamage("OD_FIRE"), ownDamage("OD_NATURE"),
                        theft));
        String v2 = given().auth().oauth2(a.manager()).post(PRODUCTS + "/{id}/versions", p.productId()).then()
                .statusCode(201).extract().path("id");

        given().get("/api/v1/catalog/product-versions/{id}", v2).then().statusCode(404)
                .body("code", equalTo("PRODUCT_VERSION_NOT_FOUND"));
        given().get("/api/v1/catalog/product-versions/{id}", UUID.randomUUID()).then().statusCode(404);

        support.publish(a.manager(), UUID.fromString(v2)).then().statusCode(200);
        given().get("/api/v1/catalog/product-versions/{id}", p.versionId()).then().statusCode(200)
                .body("status", equalTo("SUPERSEDED"))
                .body("validTo", equalTo(PublicationIT.today()))
                .body("insurer.id", equalTo(a.id().toString()))
                .body("coverages[4].code", equalTo("OD_THEFT"))
                .body("coverages[4].requiresCodes", contains("OD_COLLISION", "OD_FIRE", "OD_NATURE"))
                .body("coverages[4].territorialScope", contains("MZ"))
                .body("coverages[4].exclusions", equalTo("Chaves no veículo"));
        given().get("/api/v1/catalog/product-versions/{id}", v2).then().statusCode(200)
                .body("status", equalTo("PUBLISHED")).body("versionNo", equalTo(2));
        assertThat(listed(Map.of(), a.id())).containsExactly(v2);
    }
}
