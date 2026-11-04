package mz.co.moseguros.proposal.api;

import static mz.co.moseguros.proposal.BrokerProposalTestSupport.confirmSignature;
import static mz.co.moseguros.proposal.BrokerProposalTestSupport.requestSignature;
import static mz.co.moseguros.proposal.ProposalTestSupport.get;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.time.Duration;
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
import mz.co.moseguros.proposal.BrokerProposalTestSupport.Staff;
import mz.co.moseguros.proposal.ProposalTestSupport;
import mz.co.moseguros.rating.QuoteTestSupport;
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
 * Assinatura assistida pelo mediador (S4-06 §6): o código vai para o telemóvel da conta do cliente com o
 * texto {@code otp.sign-assisted.sms}; confirmada pelo mediador fica assistida; outro mediador não a
 * confirma; o cliente pode confirmar o mesmo pedido na sua sessão (fica não assistida).
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestcontainersConfiguration.class, SecurityTestConfiguration.class})
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AssistedSignatureIT {

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
    Staff broker;
    String outsider;

    @BeforeAll
    void createInsurerAndBrokers() {
        RestAssured.port = port;
        support = new ProposalTestSupport(fixtures, identityApi, clock);
        brokers = new BrokerProposalTestSupport(fixtures, support, channels, clock);
        support.atNow();
        insurer = support.insurerWithProducts("Seguradora Assistida");
        brokerOrg = brokers.brokerOrg(OrganizationType.AGENT, "Agente Assistente");
        broker = brokers.staff(brokerOrg, "AGENT");
        outsider = brokers.broker(brokers.brokerOrg(OrganizationType.BROKER, "Corretora Alheia"), "BROKER");
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

    /** Proposta MTPL do mediador com as declarações e a adequação feitas, pronta a assinar. */
    record Ready(BrokerQuote quote, UUID id) {
    }

    Ready ready() throws Exception {
        String plate = QuoteTestSupport.randomPlate();
        BrokerQuote quote = brokers.quote(broker.token(), BrokerProposalTestSupport.newNational(), insurer,
                "MTPL_ONLY", plate);
        UUID id = BrokerProposalTestSupport.created(broker.token(), quote, plate);
        BrokerProposalTestSupport.answer(broker.token(), id, false);
        clock.advance(Duration.ofSeconds(61));
        return new Ready(quote, id);
    }

    @Test
    void theCodeGoesToTheCustomerWithTheAssistedTextAndTheBrokerConfirms() throws Exception {
        Ready r = ready();
        String number = get(broker.token(), r.id()).path("number");
        Response request = requestSignature(broker.token(), r.id());
        request.then().statusCode(202);
        FakeOtpDeliveryChannels.Sent sms = channels.lastTo(r.quote().e164());
        assertThat(sms.text()).contains(number).contains("preparada por Agente Assistente")
                .contains(sms.code()).contains("Seguradora Assistida");
        assertThat(channels.sentTo(r.quote().e164())).hasSize(1);
        assertThat(owner().queryForObject("select sign_request_assisted_by_user_id from proposal.proposal where id = ?",
                UUID.class, r.id())).isEqualTo(broker.id());
        assertThat(owner().queryForObject("select msisdn_verified from identity.user_account where id = ?",
                Boolean.class, r.quote().customerId())).isFalse();

        // outro mediador não confirma
        confirmSignature(outsider, r.id(), request.path("challengeId"), sms.code()).then().statusCode(404)
                .body("code", equalTo("PROPOSAL_NOT_FOUND"));

        confirmSignature(broker.token(), r.id(), request.path("challengeId"), sms.code()).then().statusCode(200)
                .body("signature.assisted", equalTo(true))
                .body("checklist.signature", equalTo("DONE"));
        Map<String, Object> signature = owner().queryForMap("""
                select signer_user_id, assisted_by_user_id, assisted_by_org_id from proposal.signature
                 where proposal_id = ?""", r.id());
        assertThat(signature.get("signer_user_id")).isEqualTo(r.quote().customerId());
        assertThat(signature.get("assisted_by_user_id")).isEqualTo(broker.id());
        assertThat(signature.get("assisted_by_org_id")).isEqualTo(brokerOrg);
        // a verificação do código marca o telemóvel da conta como verificado
        assertThat(owner().queryForObject("select msisdn_verified from identity.user_account where id = ?",
                Boolean.class, r.quote().customerId())).isTrue();
    }

    @Test
    void theCustomerCanConfirmTheBrokersRequestInTheirOwnSession() throws Exception {
        Ready r = ready();
        String customer = brokers.customerLogin(r.quote().national());
        clock.advance(Duration.ofSeconds(61));
        Response request = requestSignature(broker.token(), r.id());
        request.then().statusCode(202);
        String code = channels.lastTo(r.quote().e164()).code();

        confirmSignature(customer, r.id(), request.path("challengeId"), code).then().statusCode(200)
                .body("signature.assisted", equalTo(false));
        Map<String, Object> signature = owner().queryForMap("""
                select signer_user_id, assisted_by_user_id, assisted_by_org_id from proposal.signature
                 where proposal_id = ?""", r.id());
        assertThat(signature.get("signer_user_id")).isEqualTo(r.quote().customerId());
        assertThat(signature.get("assisted_by_user_id")).isNull();
        assertThat(signature.get("assisted_by_org_id")).isNull();
        get(broker.token(), r.id()).then().statusCode(200).body("signature.assisted", equalTo(false));
    }
}
