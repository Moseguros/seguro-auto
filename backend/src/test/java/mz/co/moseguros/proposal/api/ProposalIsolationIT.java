package mz.co.moseguros.proposal.api;

import static io.restassured.RestAssured.given;
import static mz.co.moseguros.proposal.ProposalTestSupport.MY_PROPOSALS;
import static mz.co.moseguros.proposal.ProposalTestSupport.PROPOSALS;
import static mz.co.moseguros.proposal.ProposalTestSupport.declarations;
import static mz.co.moseguros.proposal.ProposalTestSupport.validDeclarations;
import static mz.co.moseguros.proposal.ProposalTestSupport.version;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import io.restassured.RestAssured;
import mz.co.moseguros.SecurityTestConfiguration;
import mz.co.moseguros.SecurityTestConfiguration.IdentityFixtures;
import mz.co.moseguros.SecurityTestConfiguration.MutableClock;
import mz.co.moseguros.TestcontainersConfiguration;
import mz.co.moseguros.catalog.CatalogTestSupport.Insurer;
import mz.co.moseguros.identity.IdentityApi;
import mz.co.moseguros.proposal.ProposalApi;
import mz.co.moseguros.proposal.ProposalStatus;
import mz.co.moseguros.proposal.ProposalTestSupport;
import mz.co.moseguros.proposal.ProposalView;
import mz.co.moseguros.proposal.domain.Declaration;
import mz.co.moseguros.proposal.domain.DeclarationRepository;
import mz.co.moseguros.proposal.domain.Proposal;
import mz.co.moseguros.proposal.domain.ProposalRepository;
import mz.co.moseguros.rating.QuoteTestSupport.Customer;
import mz.co.moseguros.shared.tenant.TenantContext;
import mz.co.moseguros.shared.tenant.TenantSnapshot;
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
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Isolamento das propostas (S4-02 §4 e §9): entre clientes (404), staff de seguradora sem as
 * permissões (403), sem token (401), RLS das declarações noutro âmbito e o {@code ProposalApi.view}
 * no âmbito de quem chama.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestcontainersConfiguration.class, SecurityTestConfiguration.class})
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ProposalIsolationIT {

    @LocalServerPort
    int port;

    @Autowired
    IdentityFixtures fixtures;

    @Autowired
    IdentityApi identityApi;

    @Autowired
    MutableClock clock;

    @Autowired
    DeclarationRepository declarationRepository;

    @Autowired
    ProposalRepository proposalRepository;

    @Autowired
    ProposalApi proposalApi;

    @Autowired
    TransactionTemplate tx;

    ProposalTestSupport support;
    Insurer insurer;
    Customer a;
    Customer b;
    UUID proposalOfA;

    @BeforeAll
    void createProposal() throws Exception {
        RestAssured.port = port;
        support = new ProposalTestSupport(fixtures, identityApi, clock);
        support.atNow();
        insurer = support.insurerWithProducts("Seguradora Isolamento");
        a = support.customerWithProfile();
        b = support.customerWithProfile();
        proposalOfA = ProposalTestSupport.created(a.token(), support.mtplOnly(a, insurer).quoteId());
        declarations(a.token(), proposalOfA, validDeclarations(), true, version(a.token(), proposalOfA))
                .then().statusCode(200);
        clock.reset();
    }

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        support.atNow();
        a = new Customer(a.account(), support.token(a.account()));
        b = new Customer(b.account(), support.token(b.account()));
    }

    @AfterEach
    void tearDown() {
        clock.reset();
        TenantContext.clear();
    }

    @Test
    void anotherCustomerNeitherReadsNorChangesTheProposal() {
        long v = version(a.token(), proposalOfA);
        ProposalTestSupport.get(b.token(), proposalOfA).then().statusCode(404)
                .body("code", equalTo("PROPOSAL_NOT_FOUND"));
        ProposalTestSupport.update(b.token(), proposalOfA, ProposalTestSupport.START.toString(), v)
                .then().statusCode(404).body("code", equalTo("PROPOSAL_NOT_FOUND"));
        declarations(b.token(), proposalOfA, validDeclarations(), true, v).then().statusCode(404)
                .body("code", equalTo("PROPOSAL_NOT_FOUND"));
        ProposalTestSupport.suitability(b.token(), proposalOfA, ProposalTestSupport.suitabilityAnswers(
                "FULL", "NO", "YES", "NO", "NO"), v).then().statusCode(404);
        ProposalTestSupport.acknowledge(b.token(), proposalOfA, v).then().statusCode(404);
        given().auth().oauth2(b.token()).get(MY_PROPOSALS).then().statusCode(200)
                .body("totalElements", equalTo(0));
        // nada mudou
        assertThat(version(a.token(), proposalOfA)).isEqualTo(v);
    }

    @Test
    void insurerStaffCannotUseTheCustomerEndpoints() {
        ProposalTestSupport.get(insurer.underwriter(), proposalOfA).then().statusCode(403)
                .body("code", equalTo("FORBIDDEN"));
        given().auth().oauth2(insurer.manager()).get(MY_PROPOSALS).then().statusCode(403);
        ProposalTestSupport.create(insurer.underwriter(), UUID.randomUUID()).then().statusCode(403);
        declarations(insurer.underwriter(), proposalOfA, validDeclarations(), true, 0).then().statusCode(403);
    }

    @Test
    void withoutTokenIs401() {
        given().get(PROPOSALS + "/{id}", proposalOfA).then().statusCode(401);
        given().get(MY_PROPOSALS).then().statusCode(401);
        given().contentType("application/json").body("{}").post(PROPOSALS).then().statusCode(401);
    }

    @Test
    void declarationRowsAreOnlyVisibleWhereTheProposalIs() {
        TenantContext.set(TenantSnapshot.customer(b.id()));
        List<Declaration> asB = tx.execute(s -> declarationRepository.findByProposalId(proposalOfA));
        assertThat(asB).isEmpty();
        Optional<Proposal> proposalAsB = tx.execute(s -> proposalRepository.findById(proposalOfA));
        assertThat(proposalAsB).isEmpty();

        TenantContext.set(TenantSnapshot.customer(a.id()));
        List<Declaration> asA = tx.execute(s -> declarationRepository.findByProposalId(proposalOfA));
        assertThat(asA).hasSize(5).allSatisfy(d -> assertThat(d.getInsurerId()).isEqualTo(insurer.id()));

        // a seguradora vê a linha pelo RLS (o serviço só lhe mostra propostas submetidas, S4-05)
        TenantContext.set(TenantSnapshot.org(null, insurer.id(), insurer.id()));
        List<Declaration> asInsurer = tx.execute(s -> declarationRepository.findByProposalId(proposalOfA));
        assertThat(asInsurer).hasSize(5);
        // outra organização não vê nada
        TenantContext.set(TenantSnapshot.org(null, UUID.randomUUID(), UUID.randomUUID()));
        List<Declaration> asOther = tx.execute(s -> declarationRepository.findByProposalId(proposalOfA));
        assertThat(asOther).isEmpty();
    }

    @Test
    void proposalApiViewRunsInTheCallersScope() {
        TenantContext.set(TenantSnapshot.customer(b.id()));
        assertThat(proposalApi.view(proposalOfA)).isEmpty();
        TenantContext.set(TenantSnapshot.system(null));
        ProposalView view = proposalApi.view(proposalOfA).orElseThrow();
        assertThat(view.status()).isEqualTo(ProposalStatus.DRAFT);
        assertThat(view.customerUserId()).isEqualTo(a.id());
        assertThat(view.insurerId()).isEqualTo(insurer.id());
        assertThat(view.channel()).isEqualTo("APP");
        assertThat(view.productType()).isEqualTo("MTPL_ONLY");
        assertThat(view.policyholderName()).isEqualTo("Ana Maria Mussa");
        assertThat(view.coverages()).isNotEmpty().allSatisfy(c -> assertThat(c.premium()).isNotNull());
        assertThat(view.totalAmount().amount()).isPositive();
        assertThat(view.commercialPremiumAmount().amount()).isPositive().isLessThan(view.totalAmount().amount());
        assertThat(view.signedDocumentFileId()).isNull();
        assertThat(view.driverSnapshot().has("licenceNumberHash")).isFalse();
    }
}
