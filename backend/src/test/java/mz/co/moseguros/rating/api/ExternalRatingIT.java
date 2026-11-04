package mz.co.moseguros.rating.api;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static io.restassured.RestAssured.given;
import static mz.co.moseguros.rating.QuoteTestSupport.FULL_OD;
import static mz.co.moseguros.rating.QuoteTestSupport.MAPPER;
import static mz.co.moseguros.rating.QuoteTestSupport.OD;
import static mz.co.moseguros.rating.QuoteTestSupport.START;
import static mz.co.moseguros.rating.QuoteTestSupport.c05Driver;
import static mz.co.moseguros.rating.QuoteTestSupport.c05Vehicle;
import static mz.co.moseguros.rating.QuoteTestSupport.postQuote;
import static mz.co.moseguros.rating.QuoteTestSupport.quoteBody;
import static mz.co.moseguros.rating.QuoteTestSupport.quotesOf;
import static mz.co.moseguros.rating.QuoteTestSupport.vehicle;
import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import mz.co.moseguros.SecurityTestConfiguration;
import mz.co.moseguros.SecurityTestConfiguration.IdentityFixtures;
import mz.co.moseguros.SecurityTestConfiguration.MutableClock;
import mz.co.moseguros.TestcontainersConfiguration;
import mz.co.moseguros.catalog.CatalogTestSupport.Insurer;
import mz.co.moseguros.identity.IdentityApi;
import mz.co.moseguros.rating.QuoteTestSupport;
import mz.co.moseguros.rating.QuoteTestSupport.Customer;
import mz.co.moseguros.rating.application.RatingProperties;
import mz.co.moseguros.rating.domain.tariff.ReferenceData;
import mz.co.moseguros.rating.infrastructure.ExternalRatingClient;
import mz.co.moseguros.rating.infrastructure.RequestSigner;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

/**
 * Tarifação externa (S3-02 §8) contra um WireMock: resposta válida (cabeçalhos, assinatura, corpo
 * sem matrícula), somas erradas, {@code DECLINED}, 500, seguradora lenta (tempo de leitura e prazo
 * global) e o circuit breaker. Cada teste tem a sua seguradora externa (suspensa no fim, para não
 * aparecer nos outros testes) e uma seguradora interna em comum.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestcontainersConfiguration.class, SecurityTestConfiguration.class})
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ExternalRatingIT {

    static final String SECRET = "segredo-de-teste-s3-02";
    static final AtomicInteger PATHS = new AtomicInteger();

    @LocalServerPort
    int port;

    @Autowired
    IdentityFixtures fixtures;

    @Autowired
    IdentityApi identityApi;

    @Autowired
    MutableClock clock;

    @Autowired
    RatingProperties properties;

    @Autowired
    ExternalRatingClient client;

    WireMockServer wireMock;
    QuoteTestSupport support;
    Insurer internal;
    String admin;
    final List<UUID> externals = new ArrayList<>();
    Duration readTimeout;

    @BeforeAll
    void start() {
        wireMock = new WireMockServer(wireMockConfig().dynamicPort().templatingEnabled(true)
                .globalTemplating(false));
        wireMock.start();
        warmUp();
        RestAssured.port = port;
        support = new QuoteTestSupport(fixtures, identityApi, clock);
        support.atNow();
        internal = support.insurerWithCharges("Seguradora Interna");
        support.publishedDemoAuto(internal.manager(), ReferenceData.tariff("DEMO-AUTO"));
        admin = support.token(fixtures.staff("Admin Plataforma", null, "PLATFORM_ADMIN"));
        clock.reset();
        readTimeout = properties.getExternal().getReadTimeout();
    }

    /**
     * A primeira resposta com modelo do WireMock (Handlebars) demora mais de um segundo a preparar;
     * sem este aquecimento o primeiro teste podia passar o tempo de leitura de 2 s.
     */
    void warmUp() {
        stub("/warmup", offered(1, 1, 0, 1));
        // com o cliente da aplicação: aquece também o RestClient e o HttpClient do JDK
        assertThat(client.post(URI.create(wireMock.baseUrl() + "/warmup"), Map.of(),
                "{\"requestId\": \"x\"}".getBytes(StandardCharsets.UTF_8)).status()).isEqualTo(200);
    }

    @AfterAll
    void stop() {
        wireMock.stop();
        fixtures.suspendOrganization(internal.id());
    }

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        support.atNow();
        wireMock.resetAll();
    }

    @AfterEach
    void tearDown() {
        properties.getExternal().setReadTimeout(readTimeout);
        externals.forEach(fixtures::suspendOrganization);
        externals.clear();
        clock.reset();
    }

    /** Seguradora externa com o {@code DEMO-AUTO} publicado e a ligação ao WireMock em {@code path}. */
    Insurer external(String path) {
        Insurer insurer = support.insurerWithCharges("Seguradora Externa");
        support.publishedDemoAuto(insurer.manager(), null);
        long version = given().auth().oauth2(admin).get("/api/v1/organizations/{id}/rating-integration", insurer.id())
                .then().statusCode(200).extract().jsonPath().getLong("version");
        given().auth().oauth2(admin).contentType(ContentType.JSON)
                .body(Map.of("ratingMode", "EXTERNAL", "ratingEndpointUrl", wireMock.baseUrl() + path,
                        "ratingSecretRef", "config:wiremock-test", "version", version))
                .put("/api/v1/organizations/{id}/rating-integration", insurer.id()).then().statusCode(200);
        externals.add(insurer.id());
        return insurer;
    }

    static String path() {
        return "/rating/" + PATHS.incrementAndGet();
    }

    /** Resposta OFFERED para MTPL + OD_COLLISION + OD_FIRE + OD_NATURE, com o requestId do pedido. */
    static String offered(long mtpl, long commercial, long stamp, long total) {
        return """
                {
                  "contractVersion": "moseguros-rating-1",
                  "requestId": "{{jsonPath request.body '$.requestId'}}",
                  "status": "OFFERED",
                  "externalReference": "COT-998877",
                  "coverages": [
                    { "code": "MTPL", "premium": { "amount": %d, "currency": "MZN" } },
                    { "code": "OD_COLLISION", "premium": { "amount": 3000000, "currency": "MZN" } },
                    { "code": "OD_FIRE", "premium": { "amount": 100000, "currency": "MZN" } },
                    { "code": "OD_NATURE", "premium": { "amount": 200000, "currency": "MZN" } }
                  ],
                  "commercialPremium": { "amount": %d, "currency": "MZN" },
                  "charges": [ { "code": "STAMP", "labelPt": "Imposto de selo", "labelEn": "Stamp duty",
                                 "amount": { "amount": %d, "currency": "MZN" } } ],
                  "totalAmount": { "amount": %d, "currency": "MZN" }
                }""".formatted(mtpl, commercial, stamp, total);
    }

    void stub(String path, String body) {
        wireMock.stubFor(post(urlEqualTo(path)).willReturn(aResponse().withStatus(200)
                .withHeader("Content-Type", "application/json").withBody(body).withTransformers("response-template")));
    }

    Response quote(Customer c) {
        return postQuote(c.token(), quoteBody(vehicle(c.token(), c05Vehicle()), c05Driver(c.token()),
                "COMPREHENSIVE", START, 365, OD));
    }

    static JsonNode only(Response r, UUID insurerId) throws Exception {
        List<JsonNode> list = quotesOf(r, insurerId);
        assertThat(list).hasSize(1);
        return list.get(0);
    }

    @Test
    void aValidResponseIsOfferedAndTheRequestIsSigned() throws Exception {
        String path = path();
        Insurer insurer = external(path);
        stub(path, offered(495_000, 3_795_000, 94_875, 3_889_875));
        Customer c = support.customer();
        Map<String, Object> vehicleBody = c05Vehicle();
        String plate = (String) vehicleBody.get("plate");
        Response r = postQuote(c.token(), quoteBody(vehicle(c.token(), vehicleBody), c05Driver(c.token()),
                "COMPREHENSIVE", START, 365, OD));
        r.then().statusCode(201);

        JsonNode q = only(r, insurer.id());
        assertThat(q.get("status").asText()).isEqualTo("OFFERED");
        assertThat(q.get("totalAmount").get("amount").asLong()).isEqualTo(3_889_875L);
        assertThat(q.get("commercialPremium").get("amount").asLong()).isEqualTo(3_795_000L);
        assertThat(q.get("roundingAdjustment").get("amount").asLong()).isZero();
        assertThat(q.get("charges").get(0).get("code").asText()).isEqualTo("STAMP");
        assertThat(q.get("coverages")).hasSize(4);
        assertThat(q.get("coverages").get(1).get("premium").get("amount").asLong()).isEqualTo(3_000_000L);
        assertThat(q.get("coverages").get(1).get("deductible").get("type").asText()).isEqualTo("PERCENT");
        // as internas também vêm
        assertThat(only(r, internal.id()).get("status").asText()).isEqualTo("OFFERED");
        String quoteId = q.get("id").asText();
        Map<String, Object> row = fixtures.owner().queryForMap("""
                select provider, external_reference, premium_breakdown ->> 'provider' as breakdown_provider,
                       premium_breakdown -> 'variables' as variables, total_amount
                  from rating.quote where id = ?::uuid""", quoteId);
        assertThat(row.get("provider")).isEqualTo("EXTERNAL");
        assertThat(row.get("external_reference")).isEqualTo("COT-998877");
        assertThat(row.get("breakdown_provider")).isEqualTo("EXTERNAL");
        assertThat(row.get("variables")).isNull();
        assertThat(row.get("total_amount")).isEqualTo(3_889_875L);

        List<LoggedRequest> sent = wireMock.findAll(postRequestedFor(urlEqualTo(path)));
        assertThat(sent).hasSize(1);
        LoggedRequest request = sent.get(0);
        assertThat(request.getHeader("Content-Type")).startsWith("application/json");
        assertThat(request.getHeader("Accept")).contains("application/json");
        assertThat(request.getHeader("X-MoSeguros-Request-Id")).isEqualTo(quoteId);
        String timestamp = request.getHeader("X-MoSeguros-Timestamp");
        assertThat(Long.parseLong(timestamp)).isCloseTo(QuoteTestSupport.NOW.getEpochSecond(),
                org.assertj.core.data.Offset.offset(60L));
        assertThat(request.getHeader("X-MoSeguros-Signature"))
                .isEqualTo(RequestSigner.sign(SECRET, Long.parseLong(timestamp), request.getBody()));

        String body = new String(request.getBody(), StandardCharsets.UTF_8);
        assertThat(body).doesNotContain(plate).doesNotContain("Ana Maria").doesNotContain(c.id().toString())
                .doesNotContain("plate").doesNotContain("licenceNumber");
        JsonNode json = MAPPER.readTree(body);
        assertThat(json.get("contractVersion").asText()).isEqualTo("moseguros-rating-1");
        assertThat(json.get("requestId").asText()).isEqualTo(quoteId);
        assertThat(json.get("insurerShortCode").isTextual()).isTrue();
        assertThat(json.get("product").get("type").asText()).isEqualTo("COMPREHENSIVE");
        assertThat(json.get("product").get("versionNo").asInt()).isEqualTo(1);
        assertThat(json.get("startDate").asText()).isEqualTo("2027-01-15");
        assertThat(json.get("endDate").asText()).isEqualTo("2028-01-14");
        assertThat(json.get("durationDays").asInt()).isEqualTo(365);
        assertThat(json.get("vehicle").get("commercialValue").get("amount").asLong()).isEqualTo(123_456_789L);
        assertThat(json.get("vehicle").has("grossWeightKg")).isTrue();
        assertThat(json.get("vehicle").get("grossWeightKg").isNull()).isTrue();
        assertThat(json.get("driver").get("birthDate").asText()).isEqualTo("1991-09-09");
        assertThat(json.get("coverages")).hasSize(4);
        assertThat(json.get("coverages").get(0).get("code").asText()).isEqualTo("MTPL");
        assertThat(json.get("coverages").get(0).get("capital").get("amount").asLong()).isEqualTo(300_000_000L);
        assertThat(json.get("coverages").get(1).get("capital").get("amount").asLong()).isEqualTo(123_456_789L);
    }

    @Test
    void wrongSumsDeclinedAndServerErrors() throws Exception {
        Customer c = support.customer();

        String wrongPath = path();
        Insurer wrong = external(wrongPath);
        stub(wrongPath, offered(495_000, 3_795_001, 94_875, 3_889_876));
        String declinedPath = path();
        Insurer declined = external(declinedPath);
        stub(declinedPath, """
                {"contractVersion": "moseguros-rating-1", "requestId": "{{jsonPath request.body '$.requestId'}}",
                 "status": "DECLINED", "declineReason": "Risco fora da política"}""");
        String errorPath = path();
        Insurer failing = external(errorPath);
        wireMock.stubFor(post(urlEqualTo(errorPath)).willReturn(aResponse().withStatus(500)));
        String otherIdPath = path();
        Insurer otherId = external(otherIdPath);
        stub(otherIdPath, offered(495_000, 3_795_000, 94_875, 3_889_875)
                .replace("{{jsonPath request.body '$.requestId'}}", UUID.randomUUID().toString()));
        String notJsonPath = path();
        Insurer notJson = external(notJsonPath);
        wireMock.stubFor(post(urlEqualTo(notJsonPath)).willReturn(aResponse().withStatus(200).withBody("<html/>")));

        Response r = quote(c);
        r.then().statusCode(201);
        JsonNode w = only(r, wrong.id());
        assertThat(w.get("status").asText()).isEqualTo("NOT_AVAILABLE");
        assertThat(w.get("unavailableReason").asText()).isEqualTo("EXTERNAL_INVALID_RESPONSE");
        JsonNode d = only(r, declined.id());
        assertThat(d.get("unavailableReason").asText()).isEqualTo("INSURER_DECLINED");
        assertThat(d.get("unavailableDetail").get("declineReason").asText()).isEqualTo("Risco fora da política");
        assertThat(only(r, failing.id()).get("unavailableReason").asText()).isEqualTo("INSURER_UNAVAILABLE");
        assertThat(only(r, otherId.id()).get("unavailableReason").asText()).isEqualTo("EXTERNAL_INVALID_RESPONSE");
        assertThat(only(r, notJson.id()).get("unavailableReason").asText()).isEqualTo("INSURER_UNAVAILABLE");
        assertThat(only(r, internal.id()).get("totalAmount").get("amount").asLong()).isPositive();
    }

    @Test
    void aSlowInsurerTimesOutAndTheOthersAreReturned() throws Exception {
        String path = path();
        Insurer slow = external(path);
        wireMock.stubFor(post(urlEqualTo(path)).willReturn(aResponse().withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody(offered(495_000, 3_795_000, 94_875, 3_889_875)).withTransformers("response-template")
                .withFixedDelay(5_000)));
        Customer c = support.customer();
        UUID vehicleId = vehicle(c.token(), c05Vehicle());
        UUID driverId = c05Driver(c.token());

        long started = System.nanoTime();
        Response r = postQuote(c.token(), quoteBody(vehicleId, driverId, "COMPREHENSIVE", START, 365, OD));
        long elapsed = (System.nanoTime() - started) / 1_000_000;
        r.then().statusCode(201);
        assertThat(elapsed).isLessThan(3_500);
        assertThat(only(r, slow.id()).get("unavailableReason").asText()).isEqualTo("INSURER_TIMEOUT");
        assertThat(only(r, internal.id()).get("status").asText()).isEqualTo("OFFERED");
    }

    @Test
    void theGlobalDeadlineCutsALongReadTimeout() throws Exception {
        properties.getExternal().setReadTimeout(Duration.ofSeconds(10));
        String path = path();
        Insurer slow = external(path);
        wireMock.stubFor(post(urlEqualTo(path)).willReturn(aResponse().withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody(offered(495_000, 3_795_000, 94_875, 3_889_875)).withTransformers("response-template")
                .withFixedDelay(6_000)));
        Customer c = support.customer();
        UUID vehicleId = vehicle(c.token(), c05Vehicle());
        UUID driverId = c05Driver(c.token());

        long started = System.nanoTime();
        Response r = postQuote(c.token(), quoteBody(vehicleId, driverId, "COMPREHENSIVE", START, 365, OD));
        long elapsed = (System.nanoTime() - started) / 1_000_000;
        r.then().statusCode(201);
        assertThat(elapsed).isLessThan(3_500);
        assertThat(only(r, slow.id()).get("unavailableReason").asText()).isEqualTo("INSURER_TIMEOUT");
        assertThat(only(r, internal.id()).get("status").asText()).isEqualTo("OFFERED");
    }

    @Test
    void theCircuitOpensAfterTenFailures() throws Exception {
        String path = path();
        Insurer failing = external(path);
        wireMock.stubFor(post(urlEqualTo(path)).willReturn(aResponse().withStatus(503)));
        Customer c = support.customer();
        Map<String, Object> body = quoteBody(vehicle(c.token(), c05Vehicle()), c05Driver(c.token()), "COMPREHENSIVE",
                START, 365, OD);
        for (int i = 0; i < 10; i++) {
            Response r = postQuote(c.token(), body);
            r.then().statusCode(201);
            assertThat(only(r, failing.id()).get("unavailableReason").asText()).isEqualTo("INSURER_UNAVAILABLE");
        }
        assertThat(wireMock.findAll(postRequestedFor(urlEqualTo(path)))).hasSize(10);

        Response open = postQuote(c.token(), body);
        open.then().statusCode(201);
        assertThat(only(open, failing.id()).get("unavailableReason").asText()).isEqualTo("INSURER_UNAVAILABLE");
        assertThat(wireMock.findAll(postRequestedFor(urlEqualTo(path)))).hasSize(10);
        assertThat(only(open, internal.id()).get("totalAmount").get("amount").asLong()).isPositive();
    }

    @Test
    void anUnknownSecretReferenceMakesTheInsurerUnavailableWithoutCallingIt() throws Exception {
        String path = path();
        Insurer insurer = external(path);
        fixtures.owner().update("update organization.insurer_profile set rating_secret_ref = 'config:desconhecida' "
                + "where organization_id = ?", insurer.id());
        stub(path, offered(495_000, 3_795_000, 94_875, 3_889_875));
        Response r = quote(support.customer());
        r.then().statusCode(201);
        assertThat(only(r, insurer.id()).get("unavailableReason").asText()).isEqualTo("INSURER_UNAVAILABLE");
        assertThat(wireMock.findAll(postRequestedFor(urlEqualTo(path)))).isEmpty();
    }

    @Test
    void theSelectorRunsBeforeTheCall() throws Exception {
        String path = path();
        Insurer insurer = external(path);
        stub(path, offered(495_000, 3_795_000, 94_875, 3_889_875));
        Customer c = support.customer();
        // VTPL com 20 000 000 MT: o produto só oferece até 10 000 000 MT
        Map<String, Object> body = quoteBody(vehicle(c.token(), c05Vehicle()), c05Driver(c.token()), "COMPREHENSIVE",
                START, 365, FULL_OD);
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> coverages = new ArrayList<>((List<Map<String, Object>>) body.get("coverages"));
        coverages.add(Map.of("code", "VTPL", "capital", Map.of("amount", 2_000_000_000L, "currency", "MZN")));
        body.put("coverages", coverages);
        Response r = postQuote(c.token(), body);
        r.then().statusCode(201);
        assertThat(only(r, insurer.id()).get("unavailableReason").asText()).isEqualTo("CAPITAL_NOT_OFFERED");
        assertThat(wireMock.findAll(postRequestedFor(urlEqualTo(path)))).isEmpty();
        assertThat(only(r, internal.id()).get("unavailableReason").asText()).isEqualTo("CAPITAL_NOT_OFFERED");
    }
}
