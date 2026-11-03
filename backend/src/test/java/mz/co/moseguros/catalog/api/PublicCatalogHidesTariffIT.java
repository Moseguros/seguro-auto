package mz.co.moseguros.catalog.api;

import static io.restassured.RestAssured.given;
import static mz.co.moseguros.catalog.CatalogTestSupport.MIN_CAPITAL;
import static mz.co.moseguros.catalog.CatalogTestSupport.mtpl;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
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
 * A tarifa é informação comercial da seguradora (S3-01 §13): depois de publicar uma versão com
 * tarifa, {@code listCatalogProducts} e {@code getCatalogProductVersion} continuam sem
 * {@code rules} nem {@code rating}.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestcontainersConfiguration.class, SecurityTestConfiguration.class})
@ActiveProfiles("test")
class PublicCatalogHidesTariffIT {

    static final ObjectMapper MAPPER = new ObjectMapper();
    static final String TARIFF = """
            {"schemaVersion":"rating-v1","coverages":[{"coverageCode":"MTPL","base":{"type":"AMOUNT",
             "rows":[{"label":"Tarifa secreta","when":{},"value":450000}]}}]}""";

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

    @Test
    void thePublicCatalogNeverReturnsTheTariff() throws Exception {
        Insurer insurer = support.insurer("Seg Secreta " + UUID.randomUUID().toString().substring(0, 4));
        String t = insurer.manager();
        Created p = support.product(t, "MTPL_ONLY");
        support.complete(t, p.versionId(), List.of("BUS"), List.of("PASSENGER_TRANSPORT"), List.of(mtpl(MIN_CAPITAL)));
        given().auth().oauth2(t).contentType(ContentType.JSON)
                .body(Map.of("rating", MAPPER.readTree(TARIFF), "version", support.version(t, p.versionId())))
                .put(CatalogTestSupport.VERSIONS + "/{id}/rating", p.versionId()).then().statusCode(200);
        support.publish(t, p.versionId()).then().statusCode(200);
        assertThat(support.owner().queryForObject(
                "select rules -> 'rating' is not null from catalog.product_version where id = ?", Boolean.class, p.versionId()))
                .isTrue();

        List<JsonNode> pages = new ArrayList<>();
        boolean found = false;
        for (int page = 0; ; page++) {
            String body = given().queryParam("vehicleCategory", "BUS").queryParam("usage", "PASSENGER_TRANSPORT")
                    .queryParam("type", "MTPL_ONLY").queryParam("size", 100).queryParam("page", page)
                    .get("/api/v1/catalog/products").then().statusCode(200).extract().asString();
            JsonNode json = MAPPER.readTree(body);
            pages.add(json);
            for (JsonNode item : json.get("content")) {
                found |= item.get("productVersionId").asText().equals(p.versionId().toString());
            }
            if ((page + 1) * 100L >= json.get("totalElements").asLong()) {
                break;
            }
        }
        assertThat(found).isTrue();
        String one = given().get("/api/v1/catalog/product-versions/{id}", p.versionId()).then().statusCode(200)
                .extract().asString();
        pages.add(MAPPER.readTree(one));

        for (JsonNode json : pages) {
            assertThat(fieldNames(json)).doesNotContain("rating", "rules", "schemaVersion");
            assertThat(json.toString()).doesNotContain("Tarifa secreta");
        }
    }

    private static List<String> fieldNames(JsonNode node) {
        List<String> names = new ArrayList<>();
        collect(node, names);
        return names;
    }

    private static void collect(JsonNode node, List<String> names) {
        if (node.isObject()) {
            node.fields().forEachRemaining(e -> {
                names.add(e.getKey());
                collect(e.getValue(), names);
            });
        } else if (node.isArray()) {
            node.forEach(n -> collect(n, names));
        }
    }
}
