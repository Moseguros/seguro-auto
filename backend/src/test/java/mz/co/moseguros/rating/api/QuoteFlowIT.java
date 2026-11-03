package mz.co.moseguros.rating.api;

import static io.restassured.RestAssured.given;
import static mz.co.moseguros.rating.QuoteTestSupport.C05_TOTAL;
import static mz.co.moseguros.rating.QuoteTestSupport.FULL_OD;
import static mz.co.moseguros.rating.QuoteTestSupport.MAPPER;
import static mz.co.moseguros.rating.QuoteTestSupport.MY_QUOTES;
import static mz.co.moseguros.rating.QuoteTestSupport.QUOTES;
import static mz.co.moseguros.rating.QuoteTestSupport.START;
import static mz.co.moseguros.rating.QuoteTestSupport.c05Driver;
import static mz.co.moseguros.rating.QuoteTestSupport.c05Vehicle;
import static mz.co.moseguros.rating.QuoteTestSupport.c07Vehicle;
import static mz.co.moseguros.rating.QuoteTestSupport.postQuote;
import static mz.co.moseguros.rating.QuoteTestSupport.quoteBody;
import static mz.co.moseguros.rating.QuoteTestSupport.quotesOf;
import static mz.co.moseguros.rating.QuoteTestSupport.vehicle;
import static mz.co.moseguros.rating.QuoteTestSupport.vehicleBody;
import static mz.co.moseguros.rating.QuoteTestSupport.withCommercialFactor;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import mz.co.moseguros.SecurityTestConfiguration;
import mz.co.moseguros.SecurityTestConfiguration.IdentityFixtures;
import mz.co.moseguros.SecurityTestConfiguration.MutableClock;
import mz.co.moseguros.TestcontainersConfiguration;
import mz.co.moseguros.catalog.CatalogTestSupport.Created;
import mz.co.moseguros.catalog.CatalogTestSupport.Insurer;
import mz.co.moseguros.identity.IdentityApi;
import mz.co.moseguros.rating.QuoteCreated;
import mz.co.moseguros.rating.QuoteTestSupport;
import mz.co.moseguros.rating.QuoteTestSupport.Customer;
import mz.co.moseguros.rating.application.QuoteDetails.QuoteRequestDetails;
import mz.co.moseguros.rating.application.QuoteService;
import mz.co.moseguros.rating.application.QuoteService.CustomerQuoteCommand;
import mz.co.moseguros.rating.domain.QuoteRequestValidator.CoverageInput;
import mz.co.moseguros.rating.domain.QuoteRequestValidator.TermsInput;
import mz.co.moseguros.rating.domain.tariff.ReferenceData;
import mz.co.moseguros.catalog.CoverageCode;
import mz.co.moseguros.catalog.ProductType;
import mz.co.moseguros.shared.security.JwtClaimsConverter;
import mz.co.moseguros.shared.tenant.TenantContext;
import mz.co.moseguros.shared.tenant.TenantSnapshot;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.modulith.test.PublishedEvents;
import org.springframework.modulith.test.PublishedEventsExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;

/**
 * Fluxo do cliente (S3-02 §4, §5 e §11): duas seguradoras ({@code DEMO} e {@code DEMOB}, criadas
 * aqui com as tarifas de {@code S3-rating-cases.md}), cotações ordenadas por preço, gravação e
 * eventos, motivos {@code NO_MATCHING_ROW} e {@code TARIFF_MISSING}, seguradora suspensa, sem
 * produtos compatíveis, veículo de outro cliente, limite por hora, a lista do cliente e as
 * permissões. Outras seguradoras de outros testes podem aparecer na resposta: as verificações
 * filtram pelas seguradoras deste teste.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestcontainersConfiguration.class, SecurityTestConfiguration.class})
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(PublishedEventsExtension.class)
class QuoteFlowIT {

    @LocalServerPort
    int port;

    @Autowired
    IdentityFixtures fixtures;

    @Autowired
    IdentityApi identityApi;

    @Autowired
    MutableClock clock;

    @Autowired
    QuoteService quoteService;

    @Autowired
    JwtDecoder jwtDecoder;

    QuoteTestSupport support;
    Insurer demo;
    Insurer demoB;
    Created demoAuto;
    Created demoBAuto;

    @BeforeAll
    void createInsurers() {
        RestAssured.port = port;
        support = new QuoteTestSupport(fixtures, identityApi, clock);
        support.atNow();
        demo = support.insurerWithCharges("Seguradora Demo");
        demoB = support.insurerWithCharges("Seguradora Demo B");
        JsonNode tariff = ReferenceData.tariff("DEMO-AUTO");
        demoAuto = support.publishedDemoAuto(demo.manager(), tariff);
        demoBAuto = support.publishedDemoAuto(demoB.manager(), withCommercialFactor(tariff));
        clock.reset();
    }

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        support.atNow();
    }

    @AfterEach
    void tearDown() {
        clock.reset();
        TenantContext.clear();
        SecurityContextHolder.clearContext();
    }

    JdbcTemplate owner() {
        return fixtures.owner();
    }

    @Test
    void c05GivesTwoOfferedQuotesCheapestFirst() throws Exception {
        Customer c = support.customer();
        UUID vehicleId = vehicle(c.token(), c05Vehicle());
        UUID driverId = c05Driver(c.token());

        long started = System.nanoTime();
        Response r = postQuote(c.token(), quoteBody(vehicleId, driverId, "COMPREHENSIVE", START, 365, FULL_OD));
        long elapsedMs = (System.nanoTime() - started) / 1_000_000;
        r.then().statusCode(201)
                .body("origin", equalTo("CUSTOMER"))
                .body("clientMsisdn", nullValue())
                .body("productType", equalTo("COMPREHENSIVE"))
                .body("startDate", equalTo("2027-01-15"))
                .body("endDate", equalTo("2028-01-14"))
                .body("durationDays", equalTo(365))
                .body("vehicle.vehicleId", equalTo(vehicleId.toString()))
                .body("vehicle.year", equalTo(2024))
                .body("vehicle.commercialValue.amount", equalTo(123_456_789))
                .body("driver.driverId", equalTo(driverId.toString()))
                .body("driver.birthDate", equalTo("1991-09-09"))
                .body("driver.licenceCategories", equalTo(List.of("B")))
                .body("requestedCoverages", hasSize(4));
        assertThat(elapsedMs).isLessThan(3_000);
        String requestId = r.path("id");
        assertThat(r.getHeader("Location")).isEqualTo("/api/v1/quotes/" + requestId);
        assertThat(MAPPER.readTree(r.asString()).get("driver").has("fullName")).isFalse();

        List<JsonNode> mine = quotesOf(r, demo.id(), demoB.id());
        assertThat(mine).hasSize(2);
        JsonNode first = mine.get(0);
        JsonNode second = mine.get(1);
        assertThat(first.get("insurer").get("id").asText()).isEqualTo(demo.id().toString());
        assertThat(first.get("status").asText()).isEqualTo("OFFERED");
        assertThat(first.get("totalAmount").get("amount").asLong()).isEqualTo(C05_TOTAL);
        assertThat(first.get("commercialPremium").get("amount").asLong()).isEqualTo(5_093_766L);
        assertThat(first.get("roundingAdjustment").get("amount").asLong()).isEqualTo(90L);
        assertThat(first.get("charges")).hasSize(2);
        assertThat(first.get("charges").get(0).get("code").asText()).isEqualTo("DEMO_STAMP");
        assertThat(first.get("charges").get(0).get("amount").get("amount").asLong()).isEqualTo(127_344L);
        assertThat(first.get("coverages")).hasSize(5);
        JsonNode mtpl = first.get("coverages").get(0);
        assertThat(mtpl.get("code").asText()).isEqualTo("MTPL");
        assertThat(mtpl.get("mandatory").asBoolean()).isTrue();
        assertThat(mtpl.get("capital").get("amount").asLong()).isEqualTo(300_000_000L);
        assertThat(mtpl.get("deductible").get("type").asText()).isEqualTo("NONE");
        assertThat(mtpl.get("premium").get("amount").asLong()).isEqualTo(495_000L);
        JsonNode collision = first.get("coverages").get(1);
        assertThat(collision.get("code").asText()).isEqualTo("OD_COLLISION");
        assertThat(collision.get("deductible").get("type").asText()).isEqualTo("PERCENT");
        assertThat(collision.get("deductible").get("percent").asText()).isEqualTo("10");
        assertThat(collision.get("deductible").get("minAmount").get("amount").asLong()).isEqualTo(2_500_000L);
        assertThat(collision.get("premium").get("amount").asLong()).isEqualTo(3_549_383L);
        assertThat(first.get("product").get("versionId").asText()).isEqualTo(demoAuto.versionId().toString());
        assertThat(first.get("product").get("type").asText()).isEqualTo("COMPREHENSIVE");
        assertThat(first.get("product").get("generalConditionsFileId").isNull()).isFalse();
        assertThat(first.get("validUntil").asText()).startsWith("2027-01-25T08:");
        assertThat(first.get("unavailableReason").isNull()).isTrue();
        assertThat(first.has("breakdown")).isFalse();
        assertThat(second.get("insurer").get("id").asText()).isEqualTo(demoB.id().toString());
        assertThat(second.get("status").asText()).isEqualTo("OFFERED");
        assertThat(second.get("totalAmount").get("amount").asLong()).isGreaterThan(C05_TOTAL);
        assertThat(second.get("product").get("versionId").asText()).isEqualTo(demoBAuto.versionId().toString());

        // ordem global: as OFFERED por total crescente, depois as NOT_AVAILABLE
        List<JsonNode> all = quotesOf(r, allInsurers(r));
        boolean seenUnavailable = false;
        long lastTotal = -1;
        for (JsonNode q : all) {
            if (q.get("status").asText().equals("OFFERED")) {
                assertThat(seenUnavailable).isFalse();
                assertThat(q.get("totalAmount").get("amount").asLong()).isGreaterThanOrEqualTo(lastTotal);
                lastTotal = q.get("totalAmount").get("amount").asLong();
            } else {
                seenUnavailable = true;
                assertThat(q.has("totalAmount")).isFalse();
                assertThat(q.has("coverages")).isFalse();
                assertThat(q.get("unavailableReason").isNull()).isFalse();
            }
        }

        // linhas gravadas
        Map<String, Object> row = owner().queryForMap("""
                select origin, customer_user_id, requested_by_user_id, requested_by_org_id, client_msisdn,
                       product_type, start_date::text as start_date, end_date::text as end_date, duration_days,
                       evaluation_ms, vehicle_snapshot::text as vehicle, driver_snapshot::text as driver
                  from rating.quote_request where id = ?::uuid""", requestId);
        assertThat(row.get("origin")).isEqualTo("CUSTOMER");
        assertThat(row.get("customer_user_id")).isEqualTo(c.id());
        assertThat(row.get("requested_by_user_id")).isEqualTo(c.id());
        assertThat(row.get("requested_by_org_id")).isNull();
        assertThat(row.get("client_msisdn")).isNull();
        assertThat(row.get("end_date")).isEqualTo("2028-01-14");
        assertThat((String) row.get("driver")).doesNotContain("Ana Maria").doesNotContain("licenceNumber");
        Map<String, Object> demoRow = owner().queryForMap("""
                select status, provider, total_amount, commercial_premium_amount, charges_amount, valid_until,
                       customer_user_id, premium_breakdown ->> 'totalAmount' as breakdown_total, rating_ms
                  from rating.quote where quote_request_id = ?::uuid and insurer_id = ?""", requestId, demo.id());
        assertThat(demoRow.get("status")).isEqualTo("OFFERED");
        assertThat(demoRow.get("provider")).isEqualTo("INTERNAL");
        assertThat(demoRow.get("total_amount")).isEqualTo(C05_TOTAL);
        assertThat(demoRow.get("commercial_premium_amount")).isEqualTo(5_093_766L);
        assertThat(demoRow.get("charges_amount")).isEqualTo(132_344L);
        assertThat(demoRow.get("customer_user_id")).isEqualTo(c.id());
        assertThat(demoRow.get("breakdown_total")).isEqualTo(String.valueOf(C05_TOTAL));
        assertThat(owner().queryForObject("select count(*) from rating.quote where quote_request_id = ?::uuid",
                Integer.class, requestId)).isEqualTo(MAPPER.readTree(r.asString()).get("quotes").size());
        // as cotações não se auditam
        assertThat(owner().queryForObject("select count(*) from audit.audit_event where entity_id = ?",
                Integer.class, requestId)).isZero();

        // a leitura dá o mesmo
        given().auth().oauth2(c.token()).get(QUOTES + "/{id}", requestId).then().statusCode(200)
                .body("id", equalTo(requestId))
                .body("quotes", hasSize(MAPPER.readTree(r.asString()).get("quotes").size()));
        assertThat(quotesOf(given().auth().oauth2(c.token()).get(QUOTES + "/{id}", requestId), demo.id())
                .get(0).get("totalAmount").get("amount").asLong()).isEqualTo(C05_TOTAL);
    }

    /** Um {@code QuoteCreated} por linha gravada (chamada directa ao serviço: o PublishedEvents é da thread). */
    @Test
    void publishesOneQuoteCreatedPerRow(PublishedEvents events) {
        Customer c = support.customer();
        UUID vehicleId = vehicle(c.token(), c05Vehicle());
        UUID driverId = c05Driver(c.token());
        // como o TenantContextFilter e o conversor de autoridades da aplicação fariam no pedido HTTP
        TenantContext.set(TenantSnapshot.customer(c.id()));
        SecurityContextHolder.getContext().setAuthentication(
                new JwtClaimsConverter().convert(jwtDecoder.decode(c.token())));

        QuoteRequestDetails details = quoteService.createForCustomer(new CustomerQuoteCommand(vehicleId, driverId,
                new TermsInput(ProductType.COMPREHENSIVE, 365, START, List.of(new CoverageInput(CoverageCode.OD_COLLISION,
                        null), new CoverageInput(CoverageCode.OD_FIRE, null),
                        new CoverageInput(CoverageCode.OD_NATURE, null)))));

        UUID requestId = details.request().getId();
        int rows = owner().queryForObject("select count(*) from rating.quote where quote_request_id = ?",
                Integer.class, requestId);
        assertThat(rows).isEqualTo(details.quotes().size()).isGreaterThanOrEqualTo(2);
        List<QuoteCreated> created = new java.util.ArrayList<>();
        events.ofType(QuoteCreated.class).matching(e -> e.quoteRequestId().equals(requestId)).forEach(created::add);
        assertThat(created).hasSize(rows);
        assertThat(created.stream().map(QuoteCreated::quoteId).distinct()).hasSize(rows);
        QuoteCreated demoEvent = created.stream().filter(e -> e.insurerId().equals(demo.id())).findFirst().orElseThrow();
        assertThat(demoEvent.origin()).isEqualTo("CUSTOMER");
        assertThat(demoEvent.status()).isEqualTo("OFFERED");
        assertThat(demoEvent.productVersionId()).isEqualTo(demoAuto.versionId());
        assertThat(demoEvent.eventId()).isNotNull();
        assertThat(demoEvent.occurredAt()).isNotNull();
        assertThat(demoEvent.totalAmount()).isPositive();
    }

    @Test
    void c07VehicleHasNoMatchingRowInBothInsurers() throws Exception {
        Customer c = support.customer();
        UUID vehicleId = vehicle(c.token(), c07Vehicle());
        UUID driverId = QuoteTestSupport.driver(c.token(), "1986-06-01", "2016-03-01");
        Response r = postQuote(c.token(), quoteBody(vehicleId, driverId, "COMPREHENSIVE", START, 365, FULL_OD));
        r.then().statusCode(201);
        List<JsonNode> mine = quotesOf(r, demo.id(), demoB.id());
        assertThat(mine).hasSize(2).allSatisfy(q -> {
            assertThat(q.get("status").asText()).isEqualTo("NOT_AVAILABLE");
            assertThat(q.get("unavailableReason").asText()).isEqualTo("NO_MATCHING_ROW");
            assertThat(q.get("unavailableDetail").get("coverageCode").asText()).isEqualTo("OD_COLLISION");
            assertThat(q.get("unavailableDetail").get("table").asText()).isEqualTo("BASE");
            assertThat(q.has("validUntil")).isFalse();
        });
        assertThat(owner().queryForObject("""
                select count(*) from rating.quote where quote_request_id = ?::uuid and insurer_id in (?, ?)
                   and status = 'NOT_AVAILABLE' and unavailable_reason = 'NO_MATCHING_ROW' and total_amount is null""",
                Integer.class, (String) r.path("id"), demo.id(), demoB.id())).isEqualTo(2);
    }

    @Test
    void suspendedInsurerDoesNotAppearAndAVersionWithoutTariffIsTariffMissing() throws Exception {
        Insurer suspended = support.insurerWithCharges("Seguradora Suspensa");
        support.publishedDemoAuto(suspended.manager(), ReferenceData.tariff("DEMO-AUTO"));
        fixtures.suspendOrganization(suspended.id());
        Insurer noTariff = support.insurerWithCharges("Seguradora Sem Tarifa");
        Created withoutTariff = support.publishedDemoAuto(noTariff.manager(), null);

        Customer c = support.customer();
        Response r = postQuote(c.token(), quoteBody(vehicle(c.token(), c05Vehicle()), c05Driver(c.token()),
                "COMPREHENSIVE", START, 365, FULL_OD));
        r.then().statusCode(201);
        assertThat(quotesOf(r, suspended.id())).isEmpty();
        List<JsonNode> missing = quotesOf(r, noTariff.id());
        assertThat(missing).hasSize(1);
        assertThat(missing.get(0).get("status").asText()).isEqualTo("NOT_AVAILABLE");
        assertThat(missing.get(0).get("unavailableReason").asText()).isEqualTo("TARIFF_MISSING");
        assertThat(missing.get(0).get("product").get("versionId").asText())
                .isEqualTo(withoutTariff.versionId().toString());
        assertThat(quotesOf(r, demo.id())).hasSize(1);
    }

    @Test
    void noCompatibleProductsGivesAnEmptyList() {
        Customer c = support.customer();
        Map<String, Object> body = vehicleBody("MOTORCYCLE", "DRIVING_SCHOOL", "NIASSA", 2022, null, false);
        UUID vehicleId = vehicle(c.token(), body);
        Response r = postQuote(c.token(), quoteBody(vehicleId, c05Driver(c.token()), "TEMPORARY", START, 30,
                List.of()));
        r.then().statusCode(201)
                .body("quotes", hasSize(0))
                .body("endDate", equalTo("2027-02-13"));
        assertThat(owner().queryForObject("select count(*) from rating.quote_request where id = ?::uuid",
                Integer.class, (String) r.path("id"))).isEqualTo(1);
    }

    @Test
    void anotherCustomersVehicleOrDriverIsNotFound() {
        Customer a = support.customer();
        Customer b = support.customer();
        UUID vehicleOfA = vehicle(a.token(), c05Vehicle());
        UUID driverOfA = c05Driver(a.token());
        UUID driverOfB = c05Driver(b.token());
        UUID vehicleOfB = vehicle(b.token(), c05Vehicle());
        postQuote(b.token(), quoteBody(vehicleOfA, driverOfB, "COMPREHENSIVE", START, 365, FULL_OD))
                .then().statusCode(404).body("code", equalTo("VEHICLE_NOT_FOUND"));
        postQuote(b.token(), quoteBody(vehicleOfB, driverOfA, "COMPREHENSIVE", START, 365, FULL_OD))
                .then().statusCode(404).body("code", equalTo("DRIVER_NOT_FOUND"));
    }

    @Test
    void validationErrorsAndVehicleValueRequired() {
        Customer c = support.customer();
        UUID driverId = c05Driver(c.token());
        Map<String, Object> noValue = vehicleBody("LIGHT_PASSENGER", "PRIVATE", "MAPUTO_CITY", 2024, null, false);
        UUID vehicleWithoutValue = vehicle(c.token(), noValue);
        postQuote(c.token(), quoteBody(vehicleWithoutValue, driverId, "COMPREHENSIVE", START, 365, FULL_OD))
                .then().statusCode(400).body("code", equalTo("VEHICLE_VALUE_REQUIRED"));

        postQuote(c.token(), quoteBody(null, null, "BORDER", START.minusDays(10), 30, List.of("OD_THEFT")))
                .then().statusCode(400)
                .body("code", equalTo("VALIDATION_ERROR"))
                .body("errors.field", org.hamcrest.Matchers.hasItems("vehicleId", "driverId", "productType",
                        "startDate"));
        postQuote(c.token(), quoteBody(vehicleWithoutValue, driverId, "MTPL_ONLY", START, 30, List.of("OD_FIRE")))
                .then().statusCode(400)
                .body("errors.code", org.hamcrest.Matchers.hasItems("DURATION_NOT_ALLOWED",
                        "COVERAGE_NOT_ALLOWED_FOR_TYPE"));
    }

    @Test
    void the31stRequestInTheSameHourIsRateLimited() {
        Customer c = support.customer();
        UUID vehicleId = vehicle(c.token(), c05Vehicle());
        UUID driverId = c05Driver(c.token());
        Map<String, Object> body = quoteBody(vehicleId, driverId, "MTPL_ONLY", START, 365, List.of());
        for (int i = 0; i < 30; i++) {
            postQuote(c.token(), body).then().statusCode(201);
            support.advance(Duration.ofSeconds(1));
        }
        Response limited = postQuote(c.token(), body);
        limited.then().statusCode(429)
                .body("code", equalTo("QUOTE_RATE_LIMITED"))
                .body("retryAfterSeconds", notNullValue());
        long retryAfter = Long.parseLong(limited.getHeader("Retry-After"));
        // o mais antigo foi feito há ~30 s: sai da janela daqui a ~3570 s
        assertThat(retryAfter).isBetween(3_500L, 3_600L);
        assertThat(((Number) limited.path("retryAfterSeconds")).longValue()).isEqualTo(retryAfter);

        // uma hora depois do primeiro volta a poder
        support.advance(Duration.ofSeconds(retryAfter + 1));
        String fresh = support.token(c.account());
        postQuote(fresh, body).then().statusCode(201);
    }

    @Test
    void myQuotesListsTheRequestWithOfferedCountAndBestTotal() throws Exception {
        Customer c = support.customer();
        Response r = postQuote(c.token(), quoteBody(vehicle(c.token(), c05Vehicle()), c05Driver(c.token()),
                "COMPREHENSIVE", START, 365, FULL_OD));
        r.then().statusCode(201);
        JsonNode created = MAPPER.readTree(r.asString());
        long offered = 0;
        long best = Long.MAX_VALUE;
        for (JsonNode q : created.get("quotes")) {
            if (q.get("status").asText().equals("OFFERED")) {
                offered++;
                best = Math.min(best, q.get("totalAmount").get("amount").asLong());
            }
        }
        assertThat(offered).isGreaterThanOrEqualTo(2);

        given().auth().oauth2(c.token()).get(MY_QUOTES).then().statusCode(200)
                .body("totalElements", equalTo(1))
                .body("page", equalTo(0))
                .body("size", equalTo(20))
                .body("content[0].id", equalTo(r.path("id")))
                .body("content[0].origin", equalTo("CUSTOMER"))
                .body("content[0].productType", equalTo("COMPREHENSIVE"))
                .body("content[0].vehicle.make", equalTo("Toyota"))
                .body("content[0].vehicle.plate", notNullValue())
                .body("content[0].clientMsisdn", nullValue())
                .body("content[0].startDate", equalTo("2027-01-15"))
                .body("content[0].durationDays", equalTo(365))
                .body("content[0].offeredCount", equalTo((int) offered))
                .body("content[0].bestTotal.amount", equalTo((int) best))
                .body("content[0].validUntil", notNullValue())
                .body("content[0].expired", equalTo(false));

        // passada a validade, conta zero e fica expirado
        support.advance(Duration.ofDays(15));
        given().auth().oauth2(support.token(c.account())).get(MY_QUOTES).then().statusCode(200)
                .body("content[0].offeredCount", equalTo(0))
                .body("content[0].bestTotal", nullValue())
                .body("content[0].expired", equalTo(true));
        given().auth().oauth2(support.token(c.account())).queryParam("sort", "plate").get(MY_QUOTES)
                .then().statusCode(400).body("errors[0].code", equalTo("INVALID_SORT"));
    }

    @Test
    void authenticationAndPermissions() {
        Map<String, Object> body = quoteBody(UUID.randomUUID(), UUID.randomUUID(), "MTPL_ONLY", START, 365, List.of());
        given().contentType(ContentType.JSON).body(body).post(QUOTES).then().statusCode(401);
        given().get(MY_QUOTES).then().statusCode(401);
        postQuote(demo.manager(), body).then().statusCode(403).body("code", equalTo("FORBIDDEN"));
        given().auth().oauth2(demo.underwriter()).get(MY_QUOTES).then().statusCode(403);
        given().auth().oauth2(demo.manager()).get(QUOTES + "/{id}", UUID.randomUUID()).then().statusCode(403);
        given().auth().oauth2(support.customer().token()).get(QUOTES + "/{id}", UUID.randomUUID())
                .then().statusCode(404).body("code", equalTo("QUOTE_REQUEST_NOT_FOUND"));
    }

    private static UUID[] allInsurers(Response r) throws Exception {
        List<UUID> ids = new java.util.ArrayList<>();
        for (JsonNode q : MAPPER.readTree(r.asString()).get("quotes")) {
            ids.add(UUID.fromString(q.get("insurer").get("id").asText()));
        }
        return ids.toArray(UUID[]::new);
    }
}
