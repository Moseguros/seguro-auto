package mz.co.moseguros.proposal.api;

import static io.restassured.RestAssured.given;
import static mz.co.moseguros.proposal.BrokerProposalTestSupport.BROKER_PROPOSALS;
import static mz.co.moseguros.proposal.BrokerProposalTestSupport.CHASSIS;
import static mz.co.moseguros.proposal.BrokerProposalTestSupport.body;
import static mz.co.moseguros.proposal.BrokerProposalTestSupport.create;
import static mz.co.moseguros.proposal.BrokerProposalTestSupport.driver;
import static mz.co.moseguros.proposal.BrokerProposalTestSupport.policyholder;
import static mz.co.moseguros.proposal.BrokerProposalTestSupport.vehicle;
import static mz.co.moseguros.proposal.ProposalTestSupport.get;
import static mz.co.moseguros.proposal.ProposalTestSupport.version;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.util.Map;
import java.util.UUID;

import io.restassured.RestAssured;
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
import mz.co.moseguros.proposal.ProposalTestSupport;
import mz.co.moseguros.proposal.SubmissionTestSupport;
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
import org.springframework.test.context.ActiveProfiles;

/**
 * Isolamento das propostas do mediador (S4-06 §4; plano: "mediador não vê propostas de outros
 * mediadores"): outra corretora não lista, não abre, não altera, não assina nem submete (404) e não usa
 * as cotações da primeira (404); colegas da mesma corretora trabalham as mesmas propostas; seguradora,
 * cliente e oficina (mesmo com a permissão) → 403; sem token → 401.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestcontainersConfiguration.class, SecurityTestConfiguration.class})
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BrokerProposalIsolationIT {

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
    String brokerA;
    String colleagueA;
    String brokerB;

    @BeforeAll
    void createInsurerAndBrokers() {
        RestAssured.port = port;
        support = new ProposalTestSupport(fixtures, identityApi, clock);
        brokers = new BrokerProposalTestSupport(fixtures, support, channels, clock);
        support.atNow();
        insurer = support.insurerWithProducts("Seguradora Isolamento Mediador");
        UUID orgA = brokers.brokerOrg(OrganizationType.BROKER, "Corretora A");
        brokerA = brokers.broker(orgA, "BROKER");
        colleagueA = brokers.broker(orgA, "BROKER");
        brokerB = brokers.broker(brokers.brokerOrg(OrganizationType.BROKER, "Corretora B"), "BROKER");
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

    @Test
    void anotherBrokerNeitherSeesNorTouchesTheProposalsOrQuotesOfTheFirst() throws Exception {
        String plate = QuoteTestSupport.randomPlate();
        BrokerQuote quote = brokers.quote(brokerA, BrokerProposalTestSupport.newNational(), insurer, "MTPL_ONLY",
                plate);
        UUID id = BrokerProposalTestSupport.created(brokerA, quote, plate);
        BrokerProposalTestSupport.answer(brokerA, id, false);
        long v = version(brokerA, id);

        // a mesma corretora: outro utilizador vê e trabalha a proposta
        assertThat(BrokerProposalTestSupport.ids(BrokerProposalTestSupport.list(colleagueA, Map.of())))
                .contains(id.toString());
        get(colleagueA, id).then().statusCode(200);

        // outra corretora: não lista, não abre, não altera, não assina, não submete
        assertThat(BrokerProposalTestSupport.ids(BrokerProposalTestSupport.list(brokerB, Map.of())))
                .doesNotContain(id.toString());
        get(brokerB, id).then().statusCode(404).body("code", equalTo("PROPOSAL_NOT_FOUND"));
        BrokerProposalTestSupport.update(brokerB, id, "2027-01-20", null, v).then().statusCode(404)
                .body("code", equalTo("PROPOSAL_NOT_FOUND"));
        ProposalTestSupport.update(brokerB, id, "2027-01-20", v).then().statusCode(404)
                .body("code", equalTo("PROPOSAL_NOT_FOUND"));
        ProposalTestSupport.declarations(brokerB, id, SubmissionTestSupport.allNegativeDeclarations(), true, v)
                .then().statusCode(404).body("code", equalTo("PROPOSAL_NOT_FOUND"));
        BrokerProposalTestSupport.requestSignature(brokerA, id).then().statusCode(202);
        BrokerProposalTestSupport.confirmSignature(brokerB, id, UUID.randomUUID().toString(), "123456").then()
                .statusCode(404).body("code", equalTo("PROPOSAL_NOT_FOUND"));
        given().auth().oauth2(brokerB).contentType("application/json")
                .body(Map.of("documentsAcknowledged", true, "version", v))
                .post(ProposalTestSupport.PROPOSALS + "/{id}/sign/request", id).then().statusCode(404)
                .body("code", equalTo("PROPOSAL_NOT_FOUND"));
        SubmissionTestSupport.submit(brokerB, id, v).then().statusCode(404).body("code", equalTo("PROPOSAL_NOT_FOUND"));
        assertThat(support.owner().queryForObject("select status from proposal.proposal where id = ?", String.class,
                id)).isEqualTo("DRAFT");

        // nem usa as cotações da corretora A
        String otherPlate = QuoteTestSupport.randomPlate();
        BrokerQuote quoteA = brokers.quote(brokerA, BrokerProposalTestSupport.newNational(), insurer, "MTPL_ONLY",
                otherPlate);
        create(brokerB, body(quoteA.quoteId(), vehicle(otherPlate, CHASSIS),
                driver("MAP-" + QuoteTestSupport.digits(6)), policyholder())).then().statusCode(404)
                .body("code", equalTo("QUOTE_NOT_FOUND"));
    }

    @Test
    void onlyBrokerAndAgentStaffUseTheBrokerEndpoints() throws Exception {
        String plate = QuoteTestSupport.randomPlate();
        BrokerQuote quote = brokers.quote(brokerA, BrokerProposalTestSupport.newNational(), insurer, "MTPL_ONLY",
                plate);
        Map<String, Object> request = body(quote.quoteId(), vehicle(plate, CHASSIS),
                driver("MAP-" + QuoteTestSupport.digits(6)), policyholder());

        // seguradora
        create(insurer.manager(), request).then().statusCode(403);
        given().auth().oauth2(insurer.underwriter()).get(BROKER_PROPOSALS).then().statusCode(403);
        // cliente
        Customer c = support.quotes().customer();
        create(c.token(), request).then().statusCode(403);
        given().auth().oauth2(c.token()).get(BROKER_PROPOSALS).then().statusCode(403);
        // oficina com a permissão atribuída à força (perfil BROKER): tipo de organização errado
        String workshop = brokers.broker(brokers.brokerOrg(OrganizationType.WORKSHOP, "Oficina Intrusa"), "BROKER");
        create(workshop, request).then().statusCode(403).body("code", equalTo("FORBIDDEN"));
        given().auth().oauth2(workshop).get(BROKER_PROPOSALS).then().statusCode(403).body("code", equalTo("FORBIDDEN"));
        BrokerProposalTestSupport.update(workshop, UUID.randomUUID(), "2027-01-20", null, 0).then().statusCode(403);
        // sem token
        given().contentType("application/json").body(request).post(BROKER_PROPOSALS).then().statusCode(401);
        given().get(BROKER_PROPOSALS).then().statusCode(401);

        // nada foi gravado; o mediador certo ainda a usa
        BrokerProposalTestSupport.create(brokerA, request).then().statusCode(201);
    }
}
