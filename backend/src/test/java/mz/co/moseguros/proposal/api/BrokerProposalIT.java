package mz.co.moseguros.proposal.api;

import static io.restassured.RestAssured.given;
import static mz.co.moseguros.proposal.BrokerProposalTestSupport.CHASSIS;
import static mz.co.moseguros.proposal.BrokerProposalTestSupport.ID_DOC;
import static mz.co.moseguros.proposal.BrokerProposalTestSupport.NUIT;
import static mz.co.moseguros.proposal.BrokerProposalTestSupport.body;
import static mz.co.moseguros.proposal.BrokerProposalTestSupport.create;
import static mz.co.moseguros.proposal.BrokerProposalTestSupport.driver;
import static mz.co.moseguros.proposal.BrokerProposalTestSupport.policyholder;
import static mz.co.moseguros.proposal.BrokerProposalTestSupport.vehicle;
import static mz.co.moseguros.proposal.ProposalTestSupport.MY_PROPOSALS;
import static mz.co.moseguros.proposal.ProposalTestSupport.get;
import static mz.co.moseguros.proposal.ProposalTestSupport.version;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.in;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import mz.co.moseguros.SecurityTestConfiguration;
import mz.co.moseguros.SecurityTestConfiguration.IdentityFixtures;
import mz.co.moseguros.SecurityTestConfiguration.MutableClock;
import mz.co.moseguros.TestcontainersConfiguration;
import mz.co.moseguros.catalog.CatalogTestSupport.Insurer;
import mz.co.moseguros.identity.IdentityApi;
import mz.co.moseguros.notification.FakeOtpDeliveryChannels;
import mz.co.moseguros.organization.OrganizationType;
import mz.co.moseguros.proposal.BrokerProposalTestSupport;
import mz.co.moseguros.proposal.BrokerProposalTestSupport.BrokerQuote;
import mz.co.moseguros.proposal.InspectionTestSupport;
import mz.co.moseguros.proposal.ProposalTestSupport;
import mz.co.moseguros.proposal.SubmissionTestSupport;
import mz.co.moseguros.rating.QuoteTestSupport;
import mz.co.moseguros.rating.QuoteTestSupport.Customer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

/**
 * Proposta criada pelo mediador em nome do cliente (S4-06 §5): conta do cliente da cotação, tomador
 * preenchido pelo mediador ou copiado do perfil, coerência com a cotação, passos até à submissão feitos
 * pelo mediador, o cliente vê a proposta, alteração pelo mediador e logs sem dados sensíveis.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestcontainersConfiguration.class, SecurityTestConfiguration.class})
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(OutputCaptureExtension.class)
class BrokerProposalIT {

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

    ProposalTestSupport support;
    BrokerProposalTestSupport brokers;
    Insurer insurer;
    UUID brokerOrg;
    String broker;

    @BeforeAll
    void createInsurerAndBroker() {
        RestAssured.port = port;
        support = new ProposalTestSupport(fixtures, identityApi, clock);
        brokers = new BrokerProposalTestSupport(fixtures, support, channels, clock);
        support.atNow();
        insurer = support.insurerWithProducts("Seguradora Mediada S4");
        brokerOrg = brokers.brokerOrg(OrganizationType.BROKER, "Corretora Propostas");
        broker = brokers.broker(brokerOrg, "BROKER");
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
        return support.owner();
    }

    int count(String sql, Object... args) {
        Integer n = owner().queryForObject(sql, Integer.class, args);
        return n == null ? 0 : n;
    }

    String quoteStatus(UUID quoteId) {
        return owner().queryForObject("select status from rating.quote where id = ?", String.class, quoteId);
    }

    @Test
    void newNumberNeedsThePolicyholderAndNothingIsSavedWithoutIt(CapturedOutput output) throws Exception {
        String national = BrokerProposalTestSupport.newNational();
        String plate = QuoteTestSupport.randomPlate();
        BrokerQuote quote = brokers.quote(broker, national, insurer, "MTPL_ONLY", plate);
        int accounts = count("select count(*) from identity.user_account where type = 'CUSTOMER'");
        int logStart = output.getAll().length();
        String licence = "MAP-" + QuoteTestSupport.digits(6);

        // sem tomador: 400 e nada gravado
        create(broker, body(quote.quoteId(), vehicle(plate, CHASSIS), driver(licence), null)).then().statusCode(400)
                .body("code", equalTo("VALIDATION_ERROR"))
                .body("errors[0].field", equalTo("policyholder"))
                .body("errors[0].code", equalTo("REQUIRED"));
        assertThat(count("select count(*) from vehicle.vehicle where owner_user_id = ?", quote.customerId())).isZero();
        assertThat(count("select count(*) from vehicle.driver where owner_user_id = ?", quote.customerId())).isZero();
        assertThat(count("select count(*) from proposal.proposal where quote_id = ?", quote.quoteId())).isZero();
        assertThat(quoteStatus(quote.quoteId())).isEqualTo("OFFERED");

        // com o tomador do mediador
        Response r = create(broker, body(quote.quoteId(), vehicle(plate, CHASSIS), driver(licence), policyholder()));
        r.then().statusCode(201)
                .body("status", equalTo("DRAFT"))
                .body("channel", equalTo("BROKER"))
                .body("policyholderSource", equalTo("BROKER"))
                .body("broker.id", equalTo(brokerOrg.toString()))
                .body("broker.displayName", equalTo("Corretora Propostas"))
                .body("policyholder.fullName", equalTo("João Carlos Sitoe"))
                .body("policyholder.idDocNumberMasked", equalTo("*********432B"))
                .body("policyholder.nuitMasked", equalTo("*****3987"))
                .body("policyholder.province", equalTo("MAPUTO"))
                .body("policyholder.msisdnMasked", notNullValue())
                .body("vehicle.plate", equalTo(plate))
                .body("driver.licenceNumberMasked", equalTo("****" + licence.substring(licence.length() - 4)));
        UUID id = UUID.fromString(r.path("id"));
        assertThat(r.getHeader("Location")).isEqualTo("/api/v1/proposals/" + id);
        Map<String, Object> row = owner().queryForMap("""
                select customer_user_id, channel, broker_org_id, broker_name, created_by_user_id, policyholder_source,
                       vehicle_id, driver_id from proposal.proposal where id = ?""", id);
        assertThat(row.get("customer_user_id")).isEqualTo(quote.customerId());
        assertThat(row.get("broker_org_id")).isEqualTo(brokerOrg);
        assertThat(row.get("policyholder_source")).isEqualTo("BROKER");
        assertThat(count("select count(*) from vehicle.vehicle where id = ? and owner_user_id = ? and status = 'ACTIVE'",
                row.get("vehicle_id"), quote.customerId())).isEqualTo(1);
        assertThat(count("""
                select count(*) from vehicle.vehicle_driver where vehicle_id = ? and driver_id = ? and is_habitual""",
                row.get("vehicle_id"), row.get("driver_id"))).isEqualTo(1);
        assertThat(quoteStatus(quote.quoteId())).isEqualTo("USED");
        // nenhuma conta nova nesta chamada; o perfil do cliente continua vazio
        assertThat(count("select count(*) from identity.user_account where type = 'CUSTOMER'")).isEqualTo(accounts);
        Map<String, Object> profile = owner().queryForMap(
                "select full_name, nuit_enc from identity.customer_profile where user_id = ?", quote.customerId());
        assertThat(profile.get("full_name")).isNull();
        assertThat(profile.get("nuit_enc")).isNull();
        Map<String, Object> audit = owner().queryForMap("""
                select actor_type, after_state::text as after from audit.audit_event
                 where action = 'PROPOSAL.CREATE' and entity_id = ?""", id.toString());
        assertThat(audit.get("actor_type")).isEqualTo("STAFF");
        assertThat((String) audit.get("after")).contains("*********432B").doesNotContain(ID_DOC).doesNotContain(NUIT);
        assertThat(count("""
                select count(*) from audit.audit_event where action in ('VEHICLE.CREATE', 'DRIVER.CREATE')
                   and actor_type = 'STAFF' and after_state::text not like ?""", "%" + licence + "%")).isGreaterThan(1);

        // a lista do mediador
        given().auth().oauth2(broker).queryParam("q", r.<String>path("number")).get(
                BrokerProposalTestSupport.BROKER_PROPOSALS).then().statusCode(200)
                .body("totalElements", equalTo(1))
                .body("content[0].id", equalTo(id.toString()))
                .body("content[0].customerName", equalTo("João Carlos Sitoe"))
                .body("content[0].customerMsisdnMasked", equalTo("+258*****" + national.substring(5)))
                .body("content[0].insurer.id", equalTo(insurer.id().toString()))
                .body("content[0].checklist.declarations", equalTo("MISSING"))
                .body("content[0].checklist.inspection", equalTo("NOT_REQUIRED"));
        given().auth().oauth2(broker).queryParam("q", "joão carlos").queryParam("status", "DRAFT")
                .get(BrokerProposalTestSupport.BROKER_PROPOSALS).then().statusCode(200)
                .body("content.id", hasItem(id.toString()));

        // logs sem o documento, o NUIT, a carta nem o telemóvel completo
        String logs = output.getAll().substring(logStart);
        assertThat(logs).contains(r.<String>path("number"))
                .doesNotContain(ID_DOC).doesNotContain(NUIT).doesNotContain(licence).doesNotContain(national)
                .doesNotContain(CHASSIS);
    }

    @Test
    void aDisabledCustomerAccountIsRefused() throws Exception {
        String plate = QuoteTestSupport.randomPlate();
        BrokerQuote quote = brokers.quote(broker, BrokerProposalTestSupport.newNational(), insurer, "MTPL_ONLY", plate);
        fixtures.setAccountStatus(quote.customerId(), "DISABLED");
        create(broker, body(quote.quoteId(), vehicle(plate, CHASSIS), driver("MAP-" + QuoteTestSupport.digits(6)),
                policyholder())).then().statusCode(409).body("code", equalTo("CLIENT_ACCOUNT_DISABLED"));
        assertThat(quoteStatus(quote.quoteId())).isEqualTo("OFFERED");
    }

    @Test
    void dataDifferentFromTheQuoteIsRefused() throws Exception {
        String plate = QuoteTestSupport.randomPlate();
        BrokerQuote quote = brokers.quote(broker, BrokerProposalTestSupport.newNational(), insurer, "MTPL_ONLY", plate);
        Map<String, Object> vehicle = vehicle(plate, CHASSIS);
        vehicle.put("year", 2023);
        create(broker, body(quote.quoteId(), vehicle, driver("MAP-" + QuoteTestSupport.digits(6)), policyholder()))
                .then().statusCode(409)
                .body("code", equalTo("QUOTE_DATA_CHANGED"))
                .body("fields", equalTo(List.of("vehicle.year")));

        Map<String, Object> driver = driver("MAP-" + QuoteTestSupport.digits(6));
        driver.put("licenceIssueDate", "2012-04-04");
        Map<String, Object> other = vehicle(QuoteTestSupport.randomPlate(), CHASSIS);
        other.remove("commercialValue");
        create(broker, body(quote.quoteId(), other, driver, policyholder())).then().statusCode(409)
                .body("fields", equalTo(List.of("vehicle.plate", "vehicle.commercialValue",
                        "driver.licenceIssueDate")));

        // vehicle e driver obrigatórios
        create(broker, body(quote.quoteId(), null, null, policyholder())).then().statusCode(400)
                .body("errors.field", equalTo(List.of("vehicle", "driver")));
        assertThat(quoteStatus(quote.quoteId())).isEqualTo("OFFERED");
    }

    @Test
    void aCustomerWithACompleteProfileKeepsItAsThePolicyholder() throws Exception {
        Customer c = support.customerWithProfile();
        String national = c.account().getMsisdn().national();
        String plate = QuoteTestSupport.randomPlate();
        BrokerQuote quote = brokers.quote(broker, national, insurer, "MTPL_ONLY", plate);
        assertThat(quote.customerId()).isEqualTo(c.id());
        Map<String, Object> before = owner().queryForMap(
                "select version, updated_at from identity.customer_profile where user_id = ?", c.id());

        create(broker, body(quote.quoteId(), vehicle(plate, CHASSIS), driver("MAP-" + QuoteTestSupport.digits(6)),
                policyholder())).then().statusCode(400)
                .body("errors[0].field", equalTo("policyholder"))
                .body("errors[0].code", equalTo("NOT_ALLOWED"));
        Response r = create(broker, body(quote.quoteId(), vehicle(plate, CHASSIS),
                driver("MAP-" + QuoteTestSupport.digits(6)), null));
        r.then().statusCode(201)
                .body("policyholderSource", equalTo("PROFILE"))
                .body("policyholder.fullName", equalTo("Ana Maria Mussa"))
                .body("policyholder.nuitMasked", equalTo("*****6789"));
        UUID id = UUID.fromString(r.path("id"));
        assertThat(owner().queryForMap("select version, updated_at from identity.customer_profile where user_id = ?",
                c.id())).isEqualTo(before);

        // o mediador não altera o tomador de uma proposta PROFILE
        BrokerProposalTestSupport.update(broker, id, null, policyholder(), version(broker, id)).then().statusCode(400)
                .body("errors[0].field", equalTo("policyholder"))
                .body("errors[0].code", equalTo("NOT_ALLOWED"));
        BrokerProposalTestSupport.update(broker, id, "2027-01-20", null, version(broker, id)).then().statusCode(200)
                .body("startAt", equalTo("2027-01-19T22:00:00Z"))
                .body("policyholderSource", equalTo("PROFILE"));
        // sem nada para alterar
        BrokerProposalTestSupport.update(broker, id, null, null, version(broker, id)).then().statusCode(400)
                .body("errors[0].field", equalTo("startDate"));
        // o cliente vê-a
        given().auth().oauth2(c.token()).get(MY_PROPOSALS).then().statusCode(200)
                .body("content.id", hasItem(id.toString()));
    }

    @Test
    void aQuoteOfTheCustomerIsNotFound() throws Exception {
        Customer c = support.customerWithProfile();
        ProposalTestSupport.Quoted own = support.mtplOnly(c, insurer);
        create(broker, body(own.quoteId(), vehicle(QuoteTestSupport.randomPlate(), CHASSIS),
                driver("MAP-" + QuoteTestSupport.digits(6)), null)).then().statusCode(404)
                .body("code", equalTo("QUOTE_NOT_FOUND"));
        create(broker, body(UUID.randomUUID(), vehicle(QuoteTestSupport.randomPlate(), CHASSIS),
                driver("MAP-" + QuoteTestSupport.digits(6)), null)).then().statusCode(404)
                .body("code", equalTo("QUOTE_NOT_FOUND"));
    }

    @Test
    void theBrokerCompletesSignsAndSubmitsAndTheCustomerSeesTheProposal() throws Exception {
        String national = BrokerProposalTestSupport.newNational();
        String plate = QuoteTestSupport.randomPlate();
        BrokerQuote quote = brokers.quote(broker, national, insurer, "COMPREHENSIVE", plate);
        UUID id = BrokerProposalTestSupport.created(broker, quote, plate);
        String colleague = brokers.broker(brokerOrg, "BROKER");

        BrokerProposalTestSupport.answer(broker, id, true);
        submitInspection(colleague, id);
        brokers.assistedSign(broker, id, quote.e164());
        get(broker, id).then().statusCode(200)
                .body("checklist.declarations", equalTo("DONE"))
                .body("checklist.suitability", equalTo("DONE"))
                .body("checklist.inspection", equalTo("DONE"))
                .body("checklist.signature", equalTo("DONE"))
                .body("signature.assisted", equalTo(true));
        SubmissionTestSupport.submit(colleague, id).then().statusCode(200)
                .body("status", in(List.of("ACCEPTED", "UNDER_REVIEW")))
                .body("submittedAt", notNullValue());

        // o cliente entra com o telemóvel da cotação e vê a proposta
        String customer = brokers.customerLogin(national);
        given().auth().oauth2(customer).get(MY_PROPOSALS).then().statusCode(200)
                .body("content.id", hasItem(id.toString()));
        get(customer, id).then().statusCode(200)
                .body("channel", equalTo("BROKER"))
                .body("broker.displayName", equalTo("Corretora Propostas"))
                .body("policyholderSource", equalTo("BROKER"));
    }

    @Test
    void theBrokerChangesThePolicyholderAndTheCustomerKeepsItWhenChangingTheDate() throws Exception {
        String national = BrokerProposalTestSupport.newNational();
        String plate = QuoteTestSupport.randomPlate();
        BrokerQuote quote = brokers.quote(broker, national, insurer, "MTPL_ONLY", plate);
        UUID id = BrokerProposalTestSupport.created(broker, quote, plate);

        Map<String, Object> holder = policyholder();
        holder.put("address", "Av. 24 de Julho, 100");
        holder.remove("idDocNumber");
        holder.remove("nuit");
        BrokerProposalTestSupport.update(broker, id, null, holder, version(broker, id)).then().statusCode(200)
                .body("policyholder.address", equalTo("Av. 24 de Julho, 100"))
                .body("policyholder.idDocNumberMasked", equalTo("*********432B"))
                .body("policyholder.nuitMasked", equalTo("*****3987"));
        Map<String, Object> invalid = policyholder();
        invalid.put("nuit", "12");
        invalid.put("idDocType", "PASSPORT");
        invalid.remove("idDocNumber");
        BrokerProposalTestSupport.update(broker, id, null, invalid, version(broker, id)).then().statusCode(400)
                .body("errors.field", equalTo(List.of("policyholder.idDocNumber", "policyholder.nuit")));
        BrokerProposalTestSupport.update(broker, id, "2026-12-31", null, version(broker, id)).then().statusCode(400)
                .body("errors[0].code", equalTo("START_DATE_OUT_OF_RANGE"));

        String customer = brokers.customerLogin(national);
        ProposalTestSupport.update(customer, id, "2027-01-20", version(customer, id)).then().statusCode(200)
                .body("policyholderSource", equalTo("BROKER"))
                .body("policyholder.fullName", equalTo("João Carlos Sitoe"))
                .body("policyholder.address", equalTo("Av. 24 de Julho, 100"));
        // o veículo e o condutor da proposta são os registados na conta do cliente
        given().auth().oauth2(customer).get("/api/v1/me/vehicles").then().statusCode(200)
                .body("content.plate", hasItem(plate));
        given().auth().oauth2(customer).get(BrokerProposalTestSupport.BROKER_PROPOSALS).then().statusCode(403);
        get(customer, id).then().statusCode(200).body("signature", nullValue());
    }

    private static void submitInspection(String token, UUID id) throws IOException {
        InspectionTestSupport.uploadRequired(token, id);
        InspectionTestSupport.submit(token, id, 84_500, version(token, id)).then().statusCode(200);
    }
}
