package mz.co.moseguros.rating;

import static io.restassured.RestAssured.given;
import static mz.co.moseguros.catalog.CatalogTestSupport.MIN_CAPITAL;
import static mz.co.moseguros.catalog.CatalogTestSupport.money;
import static mz.co.moseguros.catalog.CatalogTestSupport.mtpl;
import static mz.co.moseguros.catalog.CatalogTestSupport.ownDamage;
import static mz.co.moseguros.catalog.CatalogTestSupport.vtplOptions;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import mz.co.moseguros.SecurityTestConfiguration.IdentityFixtures;
import mz.co.moseguros.SecurityTestConfiguration.MutableClock;
import mz.co.moseguros.catalog.CatalogTestSupport;
import mz.co.moseguros.catalog.CatalogTestSupport.Created;
import mz.co.moseguros.catalog.CatalogTestSupport.Insurer;
import mz.co.moseguros.identity.IdentityApi;
import mz.co.moseguros.identity.domain.UserAccount;
import mz.co.moseguros.rating.domain.tariff.ReferenceData;

/**
 * Apoio aos testes de integração das cotações (S3-02): relógio no dia dos casos de referência,
 * seguradoras com encargos e produtos publicados com tarifa, clientes com veículo e condutor, e os
 * pedidos REST.
 */
public final class QuoteTestSupport {

    public static final String QUOTES = "/api/v1/quotes";
    public static final String MY_QUOTES = "/api/v1/me/quotes";
    public static final String BROKER_QUOTES = "/api/v1/broker/quotes";
    public static final ObjectMapper MAPPER = new ObjectMapper();
    /** "Agora" nos testes: cinco dias antes da data de início dos casos de referência. */
    public static final Instant NOW = Instant.parse("2027-01-10T08:15:00Z");
    public static final LocalDate START = LocalDate.of(2027, 1, 15);
    /** Total do caso C05 (S3-rating-cases.md). */
    public static final long C05_TOTAL = 5_226_200L;

    private final IdentityFixtures fixtures;
    private final IdentityApi identityApi;
    private final MutableClock clock;
    private final CatalogTestSupport catalog;

    public QuoteTestSupport(IdentityFixtures fixtures, IdentityApi identityApi, MutableClock clock) {
        this.fixtures = fixtures;
        this.identityApi = identityApi;
        this.clock = clock;
        this.catalog = new CatalogTestSupport(fixtures, identityApi);
    }

    public CatalogTestSupport catalog() {
        return catalog;
    }

    /** O relógio da aplicação passa a marcar {@link #NOW} (os tokens têm de ser emitidos depois). */
    public void atNow() {
        clock.reset();
        clock.advance(Duration.between(Instant.now(), NOW));
    }

    public void advance(Duration duration) {
        clock.advance(duration);
    }

    // --- Seguradoras e produtos ---

    /** Seguradora activa com os encargos de {@code charges.json} (selo 2,5 % e taxa de 50 MT). */
    public Insurer insurerWithCharges(String name) {
        Insurer insurer = catalog.insurer(name + " " + digits(4));
        putCharges(insurer.manager(), ReferenceData.json("charges.json"));
        return insurer;
    }

    public void putCharges(String token, JsonNode charges) {
        long version = given().auth().oauth2(token).get("/api/v1/insurer/rating-settings").then().statusCode(200)
                .extract().jsonPath().getLong("version");
        given().auth().oauth2(token).contentType(ContentType.JSON)
                .body(Map.of("charges", charges, "version", version))
                .put("/api/v1/insurer/rating-settings").then().statusCode(200);
    }

    /** Grava a tarifa de uma versão em preparação. */
    public void putTariff(String token, UUID versionId, JsonNode tariff) {
        given().auth().oauth2(token).contentType(ContentType.JSON)
                .body(Map.of("rating", tariff, "version", catalog.version(token, versionId)))
                .put(CatalogTestSupport.VERSIONS + "/{id}/rating", versionId).then().statusCode(200);
    }

    /** Versão em preparação com as coberturas e a elegibilidade indicadas (e os documentos). */
    public Created draft(String token, String type, List<String> categories, List<String> usages,
                         List<Map<String, Object>> coverages) {
        Created created = catalog.product(token, type);
        catalog.complete(token, created.versionId(), categories, usages, coverages);
        return created;
    }

    /** Produto publicado com a tarifa indicada ({@code null} = sem tarifa). */
    public Created published(String token, String type, List<String> categories, List<String> usages,
                             List<Map<String, Object>> coverages, JsonNode tariff) {
        Created created = draft(token, type, categories, usages, coverages);
        if (tariff != null) {
            putTariff(token, created.versionId(), tariff);
        }
        catalog.publish(token, created.versionId()).then().statusCode(200);
        return created;
    }

    /** {@code DEMO-AUTO} publicado ({@code LIGHT_PASSENGER}/{@code PRIVATE}) com a tarifa indicada. */
    public Created publishedDemoAuto(String token, JsonNode tariff) {
        return published(token, "COMPREHENSIVE", List.of("LIGHT_PASSENGER"), List.of("PRIVATE"),
                demoAutoCoverages(), tariff);
    }

    /** As coberturas do produto {@code DEMO-AUTO} de {@code products.json}. */
    public static List<Map<String, Object>> demoAutoCoverages() {
        Map<String, Object> theft = ownDamage("OD_THEFT");
        theft.put("requiresCodes", List.of("OD_COLLISION", "OD_FIRE", "OD_NATURE"));
        theft.put("deductiblePercent", 20);
        theft.put("deductibleMinAmount", money(5_000_000));
        return List.of(mtpl(MIN_CAPITAL), vtplOptions(500_000_000, 1_000_000_000), ownDamage("OD_COLLISION"),
                ownDamage("OD_FIRE"), ownDamage("OD_NATURE"), theft);
    }

    /** As coberturas do produto {@code DEMO-RC}. */
    public static List<Map<String, Object>> demoRcCoverages() {
        return List.of(mtpl(MIN_CAPITAL), vtplOptions(500_000_000, 1_000_000_000));
    }

    /** As coberturas do produto {@code DEMO-TEMP}. */
    public static List<Map<String, Object>> demoTempCoverages() {
        return List.of(mtpl(MIN_CAPITAL));
    }

    /** A tarifa com mais um factor {@code COMMERCIAL} de 1,08 em cada cobertura (a "Seguradora Demo B"). */
    public static JsonNode withCommercialFactor(JsonNode tariff) {
        ObjectNode copy = tariff.deepCopy();
        for (JsonNode coverage : copy.get("coverages")) {
            ObjectNode c = (ObjectNode) coverage;
            ArrayNode factors = c.has("factors") ? (ArrayNode) c.get("factors") : c.putArray("factors");
            ObjectNode factor = factors.addObject();
            factor.put("code", "COMMERCIAL");
            factor.put("label", "Ajuste comercial");
            ObjectNode row = factor.putArray("rows").addObject();
            row.putObject("when");
            row.put("value", "1.08");
        }
        return copy;
    }

    // --- Clientes, veículos e condutores ---

    public record Customer(UserAccount account, String token) {

        public UUID id() {
            return account.getId();
        }
    }

    public Customer customer() {
        UserAccount account = fixtures.customer("Cliente Cotação");
        return new Customer(account, token(account));
    }

    public String token(UserAccount account) {
        return identityApi.issueTokens(account, null).accessToken();
    }

    /** Veículo do caso C05: ligeiro particular de 2024, 1 234 567,89 MT, Maputo Cidade, sem localizador. */
    public static Map<String, Object> c05Vehicle() {
        return vehicleBody("LIGHT_PASSENGER", "PRIVATE", "MAPUTO_CITY", 2024, 123_456_789L, false);
    }

    /** Veículo do caso C07: ligeiro particular de 2014 (13 anos), 600 000 MT. */
    public static Map<String, Object> c07Vehicle() {
        return vehicleBody("LIGHT_PASSENGER", "PRIVATE", "GAZA", 2014, 60_000_000L, false);
    }

    public static Map<String, Object> vehicleBody(String category, String usage, String province, int year,
                                                  Long commercialValue, boolean hasTracker) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("plate", randomPlate());
        body.put("chassisNumber", "CH" + digits(12));
        body.put("make", "Toyota");
        body.put("model", "Corolla");
        body.put("year", year);
        body.put("engineCc", 1600);
        body.put("category", category);
        body.put("seats", category.equals("MOTORCYCLE") ? 2 : 5);
        body.put("usage", usage);
        body.put("provinceOfCirculation", province);
        if (commercialValue != null) {
            body.put("commercialValue", money(commercialValue));
        }
        body.put("hasTracker", hasTracker);
        return body;
    }

    public static UUID vehicle(String token, Map<String, Object> body) {
        return UUID.fromString(given().auth().oauth2(token).contentType(ContentType.JSON).body(body)
                .post("/api/v1/me/vehicles").then().statusCode(201).extract().path("id"));
    }

    /** Condutor do caso C05: 35 anos, carta há 15. */
    public static UUID c05Driver(String token) {
        return driver(token, "1991-09-09", "2011-04-04");
    }

    public static UUID driver(String token, String birthDate, String licenceIssueDate) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("fullName", "Ana Maria Mussa");
        body.put("birthDate", birthDate);
        body.put("licenceNumber", "MAP" + digits(7));
        body.put("licenceCategories", List.of("B"));
        body.put("licenceIssueDate", licenceIssueDate);
        return UUID.fromString(given().auth().oauth2(token).contentType(ContentType.JSON).body(body)
                .post("/api/v1/me/drivers").then().statusCode(201).extract().path("id"));
    }

    // --- Pedidos ---

    /** As três coberturas de danos próprios sem furto. */
    public static final List<String> OD = List.of("OD_COLLISION", "OD_FIRE", "OD_NATURE");
    /** Danos próprios completos (o caso C05). */
    public static final List<String> FULL_OD = List.of("OD_COLLISION", "OD_FIRE", "OD_NATURE", "OD_THEFT");

    public static Map<String, Object> quoteBody(UUID vehicleId, UUID driverId, String productType, LocalDate start,
                                                int days, List<String> coverages) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("vehicleId", vehicleId == null ? null : vehicleId.toString());
        body.put("driverId", driverId == null ? null : driverId.toString());
        body.put("productType", productType);
        body.put("coverages", coverages(coverages));
        body.put("startDate", start.toString());
        body.put("durationDays", days);
        return body;
    }

    public static List<Map<String, Object>> coverages(List<String> codes) {
        List<Map<String, Object>> list = new ArrayList<>();
        for (String code : codes) {
            list.add(Map.of("code", code));
        }
        return list;
    }

    public static Response postQuote(String token, Map<String, Object> body) {
        return given().auth().oauth2(token).contentType(ContentType.JSON).body(body).post(QUOTES);
    }

    public static Response getQuote(String token, UUID requestId) {
        return given().auth().oauth2(token).get(QUOTES + "/{id}", requestId);
    }

    /** As cotações da resposta de uma das seguradoras indicadas (as outras vêm de outros testes). */
    public static List<JsonNode> quotesOf(Response response, UUID... insurerIds) throws Exception {
        List<String> ids = new ArrayList<>();
        for (UUID id : insurerIds) {
            ids.add(id.toString());
        }
        List<JsonNode> result = new ArrayList<>();
        for (JsonNode q : MAPPER.readTree(response.asString()).get("quotes")) {
            if (ids.contains(q.get("insurer").get("id").asText())) {
                result.add(q);
            }
        }
        return result;
    }

    public static String randomPlate() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            sb.append((char) ('A' + ThreadLocalRandom.current().nextInt(26)));
        }
        sb.append('-').append(ThreadLocalRandom.current().nextInt(100, 1000)).append('-');
        for (int i = 0; i < 2; i++) {
            sb.append((char) ('A' + ThreadLocalRandom.current().nextInt(26)));
        }
        return sb.toString();
    }

    public static String digits(int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append(ThreadLocalRandom.current().nextInt(10));
        }
        return sb.toString();
    }
}
