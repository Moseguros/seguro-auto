package mz.co.moseguros.rating.api;

import static io.restassured.RestAssured.given;
import static mz.co.moseguros.rating.QuoteTestSupport.BROKER_QUOTES;
import static mz.co.moseguros.rating.QuoteTestSupport.MAPPER;
import static mz.co.moseguros.rating.QuoteTestSupport.MY_QUOTES;
import static mz.co.moseguros.rating.QuoteTestSupport.OD;
import static mz.co.moseguros.rating.QuoteTestSupport.START;
import static mz.co.moseguros.rating.QuoteTestSupport.coverages;
import static mz.co.moseguros.rating.QuoteTestSupport.getQuote;
import static mz.co.moseguros.rating.QuoteTestSupport.quotesOf;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;

import com.fasterxml.jackson.databind.JsonNode;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import mz.co.moseguros.SecurityTestConfiguration;
import mz.co.moseguros.SecurityTestConfiguration.IdentityFixtures;
import mz.co.moseguros.SecurityTestConfiguration.MutableClock;
import mz.co.moseguros.TestcontainersConfiguration;
import mz.co.moseguros.catalog.CatalogTestSupport.Insurer;
import mz.co.moseguros.identity.IdentityApi;
import mz.co.moseguros.identity.domain.UserAccount;
import mz.co.moseguros.notification.FakeOtpDeliveryChannels;
import mz.co.moseguros.organization.OrganizationType;
import mz.co.moseguros.rating.QuoteTestSupport;
import mz.co.moseguros.rating.domain.tariff.ReferenceData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

/**
 * Cotação do mediador em nome do cliente (S3-02 §6): dados escritos à mão, conta do cliente
 * encontrada ou criada pelo telemóvel (uma só, mesmo com pedidos simultâneos), visibilidade por
 * organização, o cliente vê o pedido e o login por OTP verifica o telemóvel na mesma conta.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestcontainersConfiguration.class, SecurityTestConfiguration.class})
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BrokerQuoteIT {

    @LocalServerPort
    int port;

    @Autowired
    IdentityFixtures fixtures;

    @Autowired
    IdentityApi identityApi;

    @Autowired
    MutableClock clock;

    @Autowired
    FakeOtpDeliveryChannels channels;

    QuoteTestSupport support;
    Insurer insurer;
    UUID brokerOrg;
    UUID otherOrg;

    @BeforeAll
    void createInsurerAndBrokers() {
        RestAssured.port = port;
        support = new QuoteTestSupport(fixtures, identityApi, clock);
        support.atNow();
        insurer = support.insurerWithCharges("Seguradora Mediada");
        support.publishedDemoAuto(insurer.manager(), ReferenceData.tariff("DEMO-AUTO"));
        brokerOrg = fixtures.organization(OrganizationType.BROKER, "Corretora Cotações").getId();
        otherOrg = fixtures.organization(OrganizationType.AGENT, "Agente Outro").getId();
        clock.reset();
    }

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        support.atNow();
        channels.reset();
    }

    @AfterEach
    void tearDown() {
        clock.reset();
        channels.reset();
    }

    JdbcTemplate owner() {
        return fixtures.owner();
    }

    String broker(UUID orgId) {
        return support.token(fixtures.staff("Mediador " + QuoteTestSupport.digits(4), orgId,
                orgId.equals(brokerOrg) ? "BROKER" : "AGENT"));
    }

    static String newMsisdn() {
        StringBuilder sb = new StringBuilder("84");
        for (int i = 0; i < 7; i++) {
            sb.append(ThreadLocalRandom.current().nextInt(10));
        }
        return sb.toString();
    }

    static Map<String, Object> body(String clientMsisdn) {
        Map<String, Object> vehicle = new LinkedHashMap<>();
        vehicle.put("plate", "abc 123 mc");
        vehicle.put("make", "Toyota");
        vehicle.put("model", "Corolla");
        vehicle.put("year", 2024);
        vehicle.put("engineCc", 1600);
        vehicle.put("category", "LIGHT_PASSENGER");
        vehicle.put("usage", "PRIVATE");
        vehicle.put("seats", 5);
        vehicle.put("grossWeightKg", null);
        vehicle.put("provinceOfCirculation", "MAPUTO_CITY");
        vehicle.put("commercialValue", Map.of("amount", 123_456_789, "currency", "MZN"));
        vehicle.put("hasTracker", false);
        Map<String, Object> driver = new LinkedHashMap<>();
        driver.put("birthDate", "1991-09-09");
        driver.put("licenceIssueDate", "2011-04-04");
        driver.put("licenceCategories", List.of("B"));
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("clientMsisdn", clientMsisdn);
        body.put("vehicle", vehicle);
        body.put("driver", driver);
        body.put("productType", "COMPREHENSIVE");
        body.put("coverages", coverages(OD));
        body.put("startDate", START.toString());
        body.put("durationDays", 365);
        return body;
    }

    static Response post(String token, Map<String, Object> body) {
        return given().auth().oauth2(token).contentType(ContentType.JSON).body(body).post(BROKER_QUOTES);
    }

    int accountsWith(String national) {
        return owner().queryForObject("""
                select count(*) from identity.user_account where type = 'CUSTOMER' and msisdn = ?""",
                Integer.class, "258" + national);
    }

    @Test
    void brokerQuotesForANewNumberAndTheCustomerAccountIsCreated() throws Exception {
        String broker = broker(brokerOrg);
        String national = newMsisdn();
        Response r = post(broker, body("+258" + national));
        r.then().statusCode(201)
                .body("origin", equalTo("BROKER"))
                .body("clientMsisdn", equalTo("+258" + national))
                .body("vehicle.vehicleId", nullValue())
                .body("vehicle.plate", equalTo("ABC-123-MC"))
                .body("vehicle.grossWeightKg", nullValue())
                .body("driver.driverId", nullValue())
                .body("driver.licenceCategories", equalTo(List.of("B")))
                .body("endDate", equalTo("2028-01-14"));
        String requestId = r.path("id");
        assertThat(r.getHeader("Location")).isEqualTo("/api/v1/quotes/" + requestId);
        List<JsonNode> mine = quotesOf(r, insurer.id());
        assertThat(mine).hasSize(1);
        assertThat(mine.get(0).get("status").asText()).isEqualTo("OFFERED");

        Map<String, Object> account = owner().queryForMap("""
                select id, status, msisdn_verified, preferred_language from identity.user_account
                 where type = 'CUSTOMER' and msisdn = ?""", "258" + national);
        assertThat(account.get("status")).isEqualTo("ACTIVE");
        assertThat(account.get("msisdn_verified")).isEqualTo(false);
        assertThat(account.get("preferred_language")).isEqualTo("pt");
        UUID customerId = (UUID) account.get("id");
        assertThat(owner().queryForObject("select count(*) from identity.customer_profile where user_id = ?",
                Integer.class, customerId)).isEqualTo(1);
        assertThat(owner().queryForObject("""
                select count(*) from identity.user_role where user_id = ? and role_code = 'CUSTOMER'""",
                Integer.class, customerId)).isEqualTo(1);
        Map<String, Object> audit = owner().queryForMap("""
                select actor_type, after_state ->> 'source' as source, after_state::text as after
                  from audit.audit_event where action = 'CUSTOMER.REGISTER' and entity_id = ?""",
                customerId.toString());
        assertThat(audit.get("source")).isEqualTo("FIND_OR_CREATE");
        assertThat(audit.get("actor_type")).isEqualTo("STAFF");
        assertThat((String) audit.get("after")).doesNotContain(national);

        Map<String, Object> request = owner().queryForMap("""
                select origin, customer_user_id, client_msisdn, requested_by_org_id, vehicle_snapshot::text as vehicle
                  from rating.quote_request where id = ?::uuid""", requestId);
        assertThat(request.get("origin")).isEqualTo("BROKER");
        assertThat(request.get("customer_user_id")).isEqualTo(customerId);
        assertThat(request.get("client_msisdn")).isEqualTo("258" + national);
        assertThat(request.get("requested_by_org_id")).isEqualTo(brokerOrg);
        assertThat(owner().queryForObject("""
                select count(*) from rating.quote where quote_request_id = ?::uuid and requested_by_org_id = ?
                   and customer_user_id = ?""", Integer.class, requestId, brokerOrg, customerId))
                .isEqualTo(MAPPER.readTree(r.asString()).get("quotes").size());
    }

    @Test
    void aNumberWithAnAccountReusesIt() {
        UserAccount existing = fixtures.customer("Cliente Existente");
        String national = existing.getMsisdn().national();
        Response r = post(broker(brokerOrg), body(national));
        r.then().statusCode(201).body("clientMsisdn", equalTo("+258" + national));
        assertThat(accountsWith(national)).isEqualTo(1);
        assertThat(owner().queryForObject("select customer_user_id from rating.quote_request where id = ?::uuid",
                UUID.class, (String) r.path("id"))).isEqualTo(existing.getId());
    }

    @Test
    void twoSimultaneousQuotesForANewNumberCreateOneAccount() throws Exception {
        String broker = broker(brokerOrg);
        String national = newMsisdn();
        CountDownLatch start = new CountDownLatch(1);
        CompletableFuture<Response> a = CompletableFuture.supplyAsync(() -> {
            await(start);
            return post(broker, body(national));
        });
        CompletableFuture<Response> b = CompletableFuture.supplyAsync(() -> {
            await(start);
            return post(broker, body("+258" + national));
        });
        start.countDown();
        Response ra = a.get();
        Response rb = b.get();
        ra.then().statusCode(201);
        rb.then().statusCode(201);
        assertThat(accountsWith(national)).isEqualTo(1);
        UUID ca = owner().queryForObject("select customer_user_id from rating.quote_request where id = ?::uuid",
                UUID.class, (String) ra.path("id"));
        UUID cb = owner().queryForObject("select customer_user_id from rating.quote_request where id = ?::uuid",
                UUID.class, (String) rb.path("id"));
        assertThat(ca).isEqualTo(cb);
        assertThat(owner().queryForObject("""
                select count(*) from audit.audit_event where action = 'CUSTOMER.REGISTER' and entity_id = ?""",
                Integer.class, ca.toString())).isEqualTo(1);
    }

    private static void await(CountDownLatch latch) {
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Test
    void visibilityByOrganizationAndTheClientFilter() {
        String broker = broker(brokerOrg);
        String colleague = broker(brokerOrg);
        String outsider = broker(otherOrg);
        String x = newMsisdn();
        String y = newMsisdn();
        String forX = post(broker, body(x)).then().statusCode(201).extract().path("id");
        String forY = post(broker, body(y)).then().statusCode(201).extract().path("id");

        given().auth().oauth2(broker).queryParam("clientMsisdn", "+258 " + x).get(BROKER_QUOTES)
                .then().statusCode(200)
                .body("totalElements", equalTo(1))
                .body("content[0].id", equalTo(forX))
                .body("content[0].clientMsisdn", equalTo("+258" + x))
                .body("content[0].origin", equalTo("BROKER"));
        given().auth().oauth2(colleague).get(BROKER_QUOTES).then().statusCode(200)
                .body("content.id", hasItems(forX, forY));
        getQuote(colleague, UUID.fromString(forX)).then().statusCode(200).body("id", equalTo(forX));

        getQuote(outsider, UUID.fromString(forX)).then().statusCode(404).body("code", equalTo("QUOTE_REQUEST_NOT_FOUND"));
        given().auth().oauth2(outsider).get(BROKER_QUOTES).then().statusCode(200)
                .body("content.id", not(hasItems(forX)))
                .body("content.id", not(hasItems(forY)));
        given().auth().oauth2(broker).queryParam("clientMsisdn", "12345").get(BROKER_QUOTES)
                .then().statusCode(400).body("errors[0].field", equalTo("clientMsisdn"))
                .body("errors[0].code", equalTo("INVALID_MSISDN"));
    }

    @Test
    void theCustomerSeesTheRequestAndTheOtpLoginVerifiesThePhoneInTheSameAccount() {
        String national = newMsisdn();
        String requestId = post(broker(brokerOrg), body(national)).then().statusCode(201).extract().path("id");
        UUID customerId = owner().queryForObject("""
                select id from identity.user_account where type = 'CUSTOMER' and msisdn = ?""", UUID.class,
                "258" + national);

        String challengeId = given().contentType(ContentType.JSON)
                .body(Map.of("identifier", national, "identifierType", "MSISDN"))
                .post("/api/v1/auth/otp/request").then().statusCode(202).extract().path("challengeId");
        String code = channels.lastTo("+258" + national).code();
        Response login = given().contentType(ContentType.JSON).body(Map.of("challengeId", challengeId, "code", code))
                .post("/api/v1/auth/otp/verify");
        login.then().statusCode(200).body("isNewUser", equalTo(false));
        String token = login.path("accessToken");

        given().auth().oauth2(token).get("/api/v1/me").then().statusCode(200)
                .body("id", equalTo(customerId.toString()))
                .body("msisdnVerified", equalTo(true));
        assertThat(owner().queryForObject("select msisdn_verified from identity.user_account where id = ?",
                Boolean.class, customerId)).isTrue();
        assertThat(accountsWith(national)).isEqualTo(1);

        given().auth().oauth2(token).get(MY_QUOTES).then().statusCode(200)
                .body("totalElements", equalTo(1))
                .body("content[0].id", equalTo(requestId))
                .body("content[0].origin", equalTo("BROKER"))
                .body("content[0].clientMsisdn", equalTo("+258" + national));
        getQuote(token, UUID.fromString(requestId)).then().statusCode(200).body("origin", equalTo("BROKER"));

        // o cliente não usa o endpoint do mediador
        post(token, body(newMsisdn())).then().statusCode(403);
        given().auth().oauth2(token).get(BROKER_QUOTES).then().statusCode(403);
    }

    @Test
    void validationOfTheManualData() {
        String broker = broker(brokerOrg);
        Map<String, Object> bad = body("12345");
        @SuppressWarnings("unchecked")
        Map<String, Object> vehicle = (Map<String, Object>) bad.get("vehicle");
        vehicle.put("year", null);
        vehicle.put("plate", "XX");
        vehicle.put("category", "TRAILER");
        @SuppressWarnings("unchecked")
        Map<String, Object> driver = (Map<String, Object>) bad.get("driver");
        driver.put("licenceCategories", List.of("ZZ"));
        driver.put("birthDate", null);
        post(broker, bad).then().statusCode(400)
                .body("code", equalTo("VALIDATION_ERROR"))
                .body("errors.field", hasItems("clientMsisdn", "vehicle.year", "vehicle.plate", "vehicle.engineCc",
                        "vehicle.seats", "vehicle.grossWeightKg", "driver.birthDate", "driver.licenceCategories[0]"))
                .body("errors.code", hasItems("INVALID_MSISDN", "REQUIRED", "INVALID_PLATE", "NOT_ALLOWED",
                        "OUT_OF_RANGE", "LICENCE_CATEGORY_UNKNOWN"));

        Map<String, Object> noValue = body(newMsisdn());
        @SuppressWarnings("unchecked")
        Map<String, Object> v = (Map<String, Object>) noValue.get("vehicle");
        v.remove("commercialValue");
        post(broker, noValue).then().statusCode(400).body("code", equalTo("VEHICLE_VALUE_REQUIRED"));

        // sem matrícula, marca, modelo, cilindrada, data e categorias da carta
        Map<String, Object> minimal = body(newMsisdn());
        @SuppressWarnings("unchecked")
        Map<String, Object> mv = (Map<String, Object>) minimal.get("vehicle");
        mv.remove("plate");
        mv.remove("make");
        mv.remove("model");
        mv.remove("engineCc");
        minimal.put("driver", Map.of("birthDate", "1991-09-09"));
        post(broker, minimal).then().statusCode(201).body("vehicle.plate", nullValue());
    }
}
