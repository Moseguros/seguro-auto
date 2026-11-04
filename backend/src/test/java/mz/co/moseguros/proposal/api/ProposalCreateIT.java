package mz.co.moseguros.proposal.api;

import static io.restassured.RestAssured.given;
import static mz.co.moseguros.proposal.ProposalTestSupport.MY_PROPOSALS;
import static mz.co.moseguros.proposal.ProposalTestSupport.NUIT;
import static mz.co.moseguros.proposal.ProposalTestSupport.create;
import static mz.co.moseguros.proposal.ProposalTestSupport.get;
import static mz.co.moseguros.proposal.ProposalTestSupport.update;
import static mz.co.moseguros.proposal.ProposalTestSupport.version;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.LinkedHashMap;
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
import mz.co.moseguros.catalog.CatalogTestSupport.Insurer;
import mz.co.moseguros.identity.IdentityApi;
import mz.co.moseguros.identity.KycTestSupport;
import mz.co.moseguros.organization.OrganizationType;
import mz.co.moseguros.proposal.ProposalTestSupport;
import mz.co.moseguros.proposal.ProposalTestSupport.Quoted;
import mz.co.moseguros.rating.QuoteTestSupport;
import mz.co.moseguros.rating.QuoteTestSupport.Customer;
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
 * Criação de propostas a partir de cotações (S4-02 §5 e §9) e alteração da data de início, com
 * produtos e cotações criados pelos serviços do S2/S3 e a tarifa de demonstração.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestcontainersConfiguration.class, SecurityTestConfiguration.class})
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ProposalCreateIT {

    @LocalServerPort
    int port;

    @Autowired
    IdentityFixtures fixtures;

    @Autowired
    IdentityApi identityApi;

    @Autowired
    MutableClock clock;

    ProposalTestSupport support;
    Insurer insurer;

    @BeforeAll
    void createInsurer() {
        RestAssured.port = port;
        support = new ProposalTestSupport(fixtures, identityApi, clock);
        support.atNow();
        insurer = support.insurerWithProducts("Seguradora Propostas");
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
    }

    JdbcTemplate owner() {
        return support.owner();
    }

    @Test
    void validQuoteGivesANumberedDraftWithTheSnapshotsAndTheQuoteBecomesUsed() throws Exception {
        Customer c = support.customerWithProfile();
        Quoted q = support.comprehensive(c, insurer);

        Response r = create(c.token(), q.quoteId());
        r.then().statusCode(201)
                .body("status", equalTo("DRAFT"))
                .body("channel", equalTo("APP"))
                .body("quoteId", equalTo(q.quoteId().toString()))
                .body("insurer.id", equalTo(insurer.id().toString()))
                .body("insurer.displayName", equalTo(insurer.name()))
                .body("broker", nullValue())
                .body("product.type", equalTo("COMPREHENSIVE"))
                .body("product.name", notNullValue())
                .body("policyholder.fullName", equalTo("Ana Maria Mussa"))
                .body("policyholder.idDocType", equalTo("BI"))
                .body("policyholder.idDocNumberMasked", equalTo("*********456A"))
                .body("policyholder.nuitMasked", equalTo("*****6789"))
                .body("policyholder.msisdnMasked", equalTo(c.account().getMsisdn().masked()))
                .body("vehicle.id", equalTo(q.vehicleId().toString()))
                .body("vehicle.year", equalTo(2024))
                .body("vehicle.commercialValue.amount", equalTo(123_456_789))
                .body("driver.id", equalTo(q.driverId().toString()))
                .body("driver.licenceNumberMasked", notNullValue())
                .body("premium.total.amount", equalTo(q.quote().get("totalAmount").get("amount").asInt()))
                .body("startAt", equalTo("2027-01-14T22:00:00Z"))
                .body("endAt", equalTo("2028-01-14T22:00:00Z"))
                .body("endDate", equalTo("2028-01-14"))
                .body("durationDays", equalTo(365))
                .body("declarations", nullValue())
                .body("suitability", nullValue())
                .body("inspection.required", equalTo(true))
                .body("kyc.state", equalTo("NONE"))
                .body("kyc.requiredForAutomaticAcceptance", nullValue())
                .body("signature", nullValue())
                .body("decision", nullValue())
                .body("checklist.declarations", equalTo("MISSING"))
                .body("checklist.suitability", equalTo("MISSING"))
                .body("checklist.inspection", equalTo("MISSING"))
                .body("checklist.signature", equalTo("MISSING"))
                .body("submittedAt", nullValue())
                .body("version", equalTo(0));
        String id = r.path("id");
        assertThat(r.getHeader("Location")).isEqualTo("/api/v1/proposals/" + id);
        JsonNode body = QuoteTestSupport.MAPPER.readTree(r.asString());
        assertThat(body.get("vehicle").has("chassisNumber")).isFalse();
        assertThat(body.get("coverages")).hasSize(5);
        JsonNode collision = body.get("coverages").get(1);
        assertThat(collision.get("code").asText()).isEqualTo("OD_COLLISION");
        assertThat(collision.get("deductible").get("type").asText()).isEqualTo("PERCENT");
        assertThat(collision.get("deductible").get("percent").asText()).isEqualTo("10");
        assertThat(collision.get("deductible").get("minAmount").get("amount").asLong()).isEqualTo(2_500_000L);
        assertThat(body.get("premium").get("charges").get(0).get("code").asText()).isEqualTo("DEMO_STAMP");
        assertThat(body.get("premium").get("commercial").get("amount").asLong())
                .isEqualTo(q.quote().get("commercialPremium").get("amount").asLong());
        assertThat(Instant.parse(body.get("expiresAt").asText()))
                .isBetween(ProposalTestSupport.NOW.plus(Duration.ofDays(15)).minusSeconds(60),
                        ProposalTestSupport.NOW.plus(Duration.ofDays(15)).plusSeconds(60));

        // número PR-2027-NNNNNN tirado de shared.business_sequence
        String number = r.path("number");
        long last = owner().queryForObject(
                "select last_value from shared.business_sequence where prefix = 'PR' and year = 2027", Long.class);
        assertThat(number).matches("^PR-2027-\\d{6}$");
        assertThat(Long.parseLong(number.substring(8))).isBetween(1L, last);

        // a cotação ficou USED
        assertThat(owner().queryForObject("select status from rating.quote where id = ?", String.class, q.quoteId()))
                .isEqualTo("USED");

        // linha gravada: snapshots copiados, documento e NUIT só cifrados
        Map<String, Object> row = owner().queryForMap("""
                select customer_user_id, created_by_user_id, channel, broker_org_id, vehicle_id, driver_id,
                       policyholder::text as holder, insured::text as insured, vehicle_snapshot::text as vehicle,
                       driver_snapshot::text as driver, total_amount, content_revision, inspection_required,
                       position(convert_to(?, 'UTF8') in policyholder_nuit_enc) as nuit_pos,
                       position(convert_to(?, 'UTF8') in policyholder_id_doc_number_enc) as doc_pos,
                       length(policyholder_nuit_enc) as nuit_len
                  from proposal.proposal where id = ?::uuid""", NUIT, KycTestSupport.DOC, id);
        assertThat(row.get("customer_user_id")).isEqualTo(c.id());
        assertThat(row.get("created_by_user_id")).isEqualTo(c.id());
        assertThat(row.get("channel")).isEqualTo("APP");
        assertThat(row.get("broker_org_id")).isNull();
        assertThat(row.get("vehicle_id")).isEqualTo(q.vehicleId());
        assertThat(row.get("driver_id")).isEqualTo(q.driverId());
        assertThat(row.get("content_revision")).isEqualTo(1);
        assertThat(row.get("inspection_required")).isEqualTo(true);
        assertThat(((Number) row.get("nuit_pos")).intValue()).isZero();
        assertThat(((Number) row.get("doc_pos")).intValue()).isZero();
        assertThat(((Number) row.get("nuit_len")).intValue()).isGreaterThan(NUIT.length());
        String holder = (String) row.get("holder");
        assertThat(holder).doesNotContain(NUIT).doesNotContain(KycTestSupport.DOC)
                .doesNotContain("\"idDocNumber\"").doesNotContain("\"nuit\"").contains("*****6789");
        assertThat((String) row.get("insured")).contains("\"sameAsPolicyholder\": true");
        assertThat((String) row.get("vehicle")).contains("\"commercialValue\": {\"amount\": 123456789");
        assertThat((String) row.get("driver")).doesNotContain("licenceNumberHash").contains("licenceNumberMasked");
        assertThat(row.get("total_amount")).isEqualTo(q.quote().get("totalAmount").get("amount").asLong());

        // auditoria PROPOSAL.CREATE
        Map<String, Object> audit = owner().queryForMap("""
                select actor_type, insurer_id, after_state::text as after from audit.audit_event
                 where action = 'PROPOSAL.CREATE' and entity_id = ?""", id);
        assertThat(audit.get("actor_type")).isEqualTo("CUSTOMER");
        assertThat(audit.get("insurer_id")).isEqualTo(insurer.id());
        assertThat((String) audit.get("after")).contains(number).contains("\"channel\": \"APP\"")
                .doesNotContain(NUIT);

        // a lista do cliente mostra-a
        given().auth().oauth2(c.token()).get(MY_PROPOSALS).then().statusCode(200)
                .body("totalElements", equalTo(1))
                .body("content[0].id", equalTo(id))
                .body("content[0].number", equalTo(number))
                .body("content[0].status", equalTo("DRAFT"))
                .body("content[0].insurer.displayName", equalTo(insurer.name()))
                .body("content[0].productType", equalTo("COMPREHENSIVE"))
                .body("content[0].vehiclePlate", notNullValue())
                .body("content[0].total.amount", equalTo(q.quote().get("totalAmount").get("amount").asInt()));
        given().auth().oauth2(c.token()).queryParam("status", "UNDER_REVIEW").queryParam("status", "ACCEPTED")
                .get(MY_PROPOSALS).then().statusCode(200).body("totalElements", equalTo(0));
        given().auth().oauth2(c.token()).queryParam("status", "DRAFT").get(MY_PROPOSALS).then().statusCode(200)
                .body("totalElements", equalTo(1));
        get(c.token(), UUID.fromString(id)).then().statusCode(200).body("number", equalTo(number));
    }

    @Test
    void theSameQuoteTwiceIsProposalAlreadyExists() throws Exception {
        Customer c = support.customerWithProfile();
        Quoted q = support.mtplOnly(c, insurer);
        String id = create(c.token(), q.quoteId()).then().statusCode(201).extract().path("id");
        create(c.token(), q.quoteId()).then().statusCode(409)
                .body("code", equalTo("PROPOSAL_ALREADY_EXISTS"))
                .body("proposalId", equalTo(id));
    }

    @Test
    void mtplOnlyDoesNotRequireInspectionAndWebChannelIsKept() throws Exception {
        Customer c = support.customerWithProfile();
        Quoted q = support.mtplOnly(c, insurer);
        create(c.token(), q.quoteId(), "WEB").then().statusCode(201)
                .body("channel", equalTo("WEB"))
                .body("product.type", equalTo("MTPL_ONLY"))
                .body("inspection.required", equalTo(false))
                .body("checklist.inspection", equalTo("NOT_REQUIRED"));
    }

    @Test
    void channelIsRequiredAndBrokerIsNotAllowed() throws Exception {
        Customer c = support.customerWithProfile();
        Quoted q = support.mtplOnly(c, insurer);
        create(c.token(), q.quoteId(), null).then().statusCode(400)
                .body("code", equalTo("VALIDATION_ERROR"))
                .body("errors.field", hasItem("channel"))
                .body("errors.code", hasItem("REQUIRED"));
        create(c.token(), q.quoteId(), "BROKER").then().statusCode(400)
                .body("errors.code", hasItem("NOT_ALLOWED"));
        create(c.token(), null, "APP").then().statusCode(400).body("errors.field", hasItem("quoteId"));
    }

    @Test
    void expiredQuoteIsQuoteExpired() throws Exception {
        Customer c = support.customerWithProfile();
        Quoted q = support.mtplOnly(c, insurer);
        support.advance(Duration.ofDays(15));
        String fresh = support.token(c.account());
        create(fresh, q.quoteId()).then().statusCode(409).body("code", equalTo("QUOTE_EXPIRED"));
        assertThat(owner().queryForObject("select count(*) from proposal.proposal where quote_id = ?", Integer.class,
                q.quoteId())).isZero();
    }

    @Test
    void anotherCustomersQuoteAndABrokerQuoteAreNotFound() throws Exception {
        Customer a = support.customerWithProfile();
        Customer b = support.customerWithProfile();
        Quoted ofA = support.mtplOnly(a, insurer);
        create(b.token(), ofA.quoteId()).then().statusCode(404).body("code", equalTo("QUOTE_NOT_FOUND"));
        create(b.token(), UUID.randomUUID()).then().statusCode(404).body("code", equalTo("QUOTE_NOT_FOUND"));

        // cotação de um mediador em nome de b: b vê-a, mas não a usa aqui (S4-06)
        UUID brokerOrg = fixtures.organization(OrganizationType.BROKER, "Corretora Propostas").getId();
        String broker = support.token(fixtures.staff("Mediador Propostas", brokerOrg, "BROKER"));
        Response r = given().auth().oauth2(broker).contentType(ContentType.JSON)
                .body(brokerBody(b.account().getMsisdn().national()))
                .post(QuoteTestSupport.BROKER_QUOTES);
        r.then().statusCode(201);
        UUID brokerQuote = UUID.fromString(QuoteTestSupport.quotesOf(r, insurer.id()).stream()
                .filter(x -> x.get("status").asText().equals("OFFERED")).findFirst().orElseThrow()
                .get("id").asText());
        create(b.token(), brokerQuote).then().statusCode(404).body("code", equalTo("QUOTE_NOT_FOUND"));
    }

    /** Pedido de cotação de um mediador (o do {@code BrokerQuoteIT}) para o telemóvel indicado. */
    static Map<String, Object> brokerBody(String clientMsisdn) {
        Map<String, Object> vehicle = new LinkedHashMap<>();
        vehicle.put("plate", QuoteTestSupport.randomPlate());
        vehicle.put("make", "Toyota");
        vehicle.put("model", "Corolla");
        vehicle.put("year", 2024);
        vehicle.put("engineCc", 1600);
        vehicle.put("category", "LIGHT_PASSENGER");
        vehicle.put("usage", "PRIVATE");
        vehicle.put("seats", 5);
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
        body.put("productType", "MTPL_ONLY");
        body.put("coverages", List.of());
        body.put("startDate", ProposalTestSupport.START.toString());
        body.put("durationDays", 365);
        return body;
    }

    @Test
    void incompleteProfileIsProfileIncomplete() throws Exception {
        Customer c = support.quotes().customer();
        Quoted q = support.mtplOnly(c, insurer);
        create(c.token(), q.quoteId()).then().statusCode(409).body("code", equalTo("PROFILE_INCOMPLETE"));
    }

    @Test
    void suspendedInsurerIsInsurerNotActive() throws Exception {
        Insurer suspended = support.insurerWithProducts("Seguradora Suspensa Propostas");
        Customer c = support.customerWithProfile();
        Quoted q = support.mtplOnly(c, suspended);
        fixtures.suspendOrganization(suspended.id());
        create(c.token(), q.quoteId()).then().statusCode(409).body("code", equalTo("INSURER_NOT_ACTIVE"));
        assertThat(owner().queryForObject("select status from rating.quote where id = ?", String.class, q.quoteId()))
                .isEqualTo("OFFERED");
    }

    @Test
    void changedCommercialValueIsQuoteDataChangedAndTheQuoteStaysOffered() throws Exception {
        Customer c = support.customerWithProfile();
        Quoted q = support.comprehensive(c, insurer);
        Response vehicle = given().auth().oauth2(c.token()).get("/api/v1/me/vehicles/{id}", q.vehicleId());
        Map<String, Object> body = new LinkedHashMap<>(QuoteTestSupport.c05Vehicle());
        body.put("plate", vehicle.path("plate"));
        body.put("chassisNumber", vehicle.path("chassisNumber"));
        body.put("commercialValue", Map.of("amount", 100_000_000, "currency", "MZN"));
        body.put("version", vehicle.path("version"));
        given().auth().oauth2(c.token()).contentType(ContentType.JSON).body(body)
                .put("/api/v1/me/vehicles/{id}", q.vehicleId()).then().statusCode(200);

        create(c.token(), q.quoteId()).then().statusCode(409)
                .body("code", equalTo("QUOTE_DATA_CHANGED"))
                .body("fields", equalTo(List.of("vehicle.commercialValue")));
        assertThat(owner().queryForObject("select status from rating.quote where id = ?", String.class, q.quoteId()))
                .isEqualTo("OFFERED");
    }

    @Test
    void removedVehicleIsVehicleNotFound() throws Exception {
        Customer c = support.customerWithProfile();
        Quoted q = support.mtplOnly(c, insurer);
        owner().update("update vehicle.vehicle set status = 'REMOVED', removed_at = now() where id = ?", q.vehicleId());
        create(c.token(), q.quoteId()).then().statusCode(404).body("code", equalTo("VEHICLE_NOT_FOUND"));
    }

    // --- updateProposal ---

    @Test
    void startDateReferenceCasesFor365Days() throws Exception {
        Customer c = support.customerWithProfile();
        UUID id = ProposalTestSupport.created(c.token(), support.comprehensive(c, insurer).quoteId());
        long v = version(c.token(), id);
        update(c.token(), id, "2027-01-10", v).then().statusCode(200)
                .body("endDate", equalTo("2028-01-09"))
                .body("startAt", equalTo("2027-01-09T22:00:00Z"))
                .body("endAt", equalTo("2028-01-09T22:00:00Z"))
                .body("durationDays", equalTo(365));
        update(c.token(), id, "2027-03-01", version(c.token(), id)).then().statusCode(200)
                .body("endDate", equalTo("2028-02-29"))
                .body("endAt", equalTo("2028-02-29T22:00:00Z"));
        assertThat(owner().queryForObject("select content_revision from proposal.proposal where id = ?",
                Integer.class, id)).isEqualTo(3);
        assertThat(owner().queryForObject("select count(*) from audit.audit_event where action = 'PROPOSAL.UPDATE' "
                + "and entity_id = ?", Integer.class, id.toString())).isEqualTo(2);
    }

    @Test
    void startDateReferenceCaseFor30Days() throws Exception {
        Customer c = support.customerWithProfile();
        Quoted q = support.quoted(c, insurer, "TEMPORARY", 30, List.of());
        UUID id = ProposalTestSupport.created(c.token(), q.quoteId());
        update(c.token(), id, "2027-02-01", version(c.token(), id)).then().statusCode(200)
                .body("endDate", equalTo("2027-03-02"))
                .body("endAt", equalTo("2027-03-02T22:00:00Z"))
                .body("durationDays", equalTo(30));
    }

    @Test
    void yesterdayIsOutOfRangeAndAnOldVersionIsAConflict() throws Exception {
        Customer c = support.customerWithProfile();
        UUID id = ProposalTestSupport.created(c.token(), support.mtplOnly(c, insurer).quoteId());
        long v = version(c.token(), id);
        String today = LocalDate.ofInstant(ProposalTestSupport.NOW, java.time.ZoneId.of("Africa/Maputo")).toString();
        update(c.token(), id, LocalDate.parse(today).minusDays(1).toString(), v).then().statusCode(400)
                .body("code", equalTo("VALIDATION_ERROR"))
                .body("errors[0].field", equalTo("startDate"))
                .body("errors[0].code", equalTo("START_DATE_OUT_OF_RANGE"));
        update(c.token(), id, LocalDate.parse(today).plusDays(61).toString(), v).then().statusCode(400)
                .body("errors[0].code", equalTo("START_DATE_OUT_OF_RANGE"));
        update(c.token(), id, today, v).then().statusCode(200);
        update(c.token(), id, today, v).then().statusCode(409).body("code", equalTo("CONCURRENT_MODIFICATION"));
    }

    @Test
    void updateCopiesThePolicyholderAgainAndAnIncompleteProfileIsRefused() throws Exception {
        Customer c = support.customerWithProfile();
        UUID id = ProposalTestSupport.created(c.token(), support.mtplOnly(c, insurer).quoteId());
        long profileVersion = ((Number) given().auth().oauth2(c.token()).get("/api/v1/me/profile").path("version"))
                .longValue();
        Map<String, Object> profile = new LinkedHashMap<>();
        profile.put("fullName", "Ana Maria Mussa Novo");
        profile.put("birthDate", "1990-05-14");
        profile.put("idDocType", "BI");
        profile.put("idDocNumber", KycTestSupport.DOC);
        profile.put("nuit", NUIT);
        profile.put("address", "Av. 24 de Julho, 10");
        profile.put("province", "MAPUTO_CITY");
        profile.put("version", profileVersion);
        given().auth().oauth2(c.token()).contentType(ContentType.JSON).body(profile).put("/api/v1/me/profile")
                .then().statusCode(200);
        update(c.token(), id, ProposalTestSupport.START.toString(), version(c.token(), id)).then().statusCode(200)
                .body("policyholder.fullName", equalTo("Ana Maria Mussa Novo"))
                .body("policyholder.address", equalTo("Av. 24 de Julho, 10"));
    }
}
