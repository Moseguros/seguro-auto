package mz.co.moseguros.rating.api;

import static mz.co.moseguros.rating.QuoteTestSupport.FULL_OD;
import static mz.co.moseguros.rating.QuoteTestSupport.START;
import static mz.co.moseguros.rating.QuoteTestSupport.c05Driver;
import static mz.co.moseguros.rating.QuoteTestSupport.c05Vehicle;
import static mz.co.moseguros.rating.QuoteTestSupport.getQuote;
import static mz.co.moseguros.rating.QuoteTestSupport.postQuote;
import static mz.co.moseguros.rating.QuoteTestSupport.quoteBody;
import static mz.co.moseguros.rating.QuoteTestSupport.vehicle;
import static mz.co.moseguros.rating.QuoteTestSupport.withCommercialFactor;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

import io.restassured.RestAssured;
import mz.co.moseguros.SecurityTestConfiguration;
import mz.co.moseguros.SecurityTestConfiguration.IdentityFixtures;
import mz.co.moseguros.SecurityTestConfiguration.MutableClock;
import mz.co.moseguros.TestcontainersConfiguration;
import mz.co.moseguros.catalog.CatalogTestSupport.Insurer;
import mz.co.moseguros.identity.IdentityApi;
import mz.co.moseguros.rating.QuoteTestSupport;
import mz.co.moseguros.rating.QuoteTestSupport.Customer;
import mz.co.moseguros.rating.domain.tariff.ReferenceData;
import mz.co.moseguros.shared.tenant.TenantContext;
import mz.co.moseguros.shared.tenant.TenantSnapshot;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Isolamento das cotações (S3-02 §1, RLS; ADR-002): um cliente não vê o pedido de outro; a
 * seguradora vê só as suas linhas de {@code rating.quote} e nenhum {@code quote_request}.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestcontainersConfiguration.class, SecurityTestConfiguration.class})
@ActiveProfiles("test")
class QuoteIsolationIT {

    @LocalServerPort
    int port;

    @Autowired
    IdentityFixtures fixtures;

    @Autowired
    IdentityApi identityApi;

    @Autowired
    MutableClock clock;

    @Autowired
    JdbcTemplate app;

    @Autowired
    TransactionTemplate tx;

    QuoteTestSupport support;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        support = new QuoteTestSupport(fixtures, identityApi, clock);
        support.atNow();
    }

    @AfterEach
    void tearDown() {
        clock.reset();
        TenantContext.clear();
    }

    <T> T as(TenantSnapshot tenant, Supplier<T> work) {
        TenantSnapshot previous = TenantContext.current();
        TenantContext.set(tenant);
        try {
            return tx.execute(status -> work.get());
        } finally {
            TenantContext.set(previous);
        }
    }

    @Test
    void customersAndInsurersOnlySeeTheirOwnRows() {
        Insurer demo = support.insurerWithCharges("Seguradora Isolamento A");
        Insurer demoB = support.insurerWithCharges("Seguradora Isolamento B");
        support.publishedDemoAuto(demo.manager(), ReferenceData.tariff("DEMO-AUTO"));
        support.publishedDemoAuto(demoB.manager(), withCommercialFactor(ReferenceData.tariff("DEMO-AUTO")));

        Customer a = support.customer();
        Customer b = support.customer();
        String requestId = postQuote(a.token(), quoteBody(vehicle(a.token(), c05Vehicle()), c05Driver(a.token()),
                "COMPREHENSIVE", START, 365, FULL_OD)).then().statusCode(201).extract().path("id");
        UUID id = UUID.fromString(requestId);

        getQuote(b.token(), id).then().statusCode(404).body("code", equalTo("QUOTE_REQUEST_NOT_FOUND"));
        getQuote(a.token(), id).then().statusCode(200);

        // directamente na BD, como a aplicação (sem ser dona das tabelas)
        TenantSnapshot insurerB = TenantSnapshot.org(UUID.randomUUID(), demoB.id(), demoB.id());
        List<UUID> visibleInsurers = as(insurerB, () -> app.queryForList(
                "select insurer_id from rating.quote where quote_request_id = ?", UUID.class, id));
        assertThat(visibleInsurers).containsExactly(demoB.id());
        assertThat(as(insurerB, () -> app.queryForList("select distinct insurer_id from rating.quote", UUID.class)))
                .containsOnly(demoB.id());
        assertThat(as(insurerB, () -> app.queryForObject("select count(*) from rating.quote_request", Integer.class)))
                .isZero();

        TenantSnapshot customerB = TenantSnapshot.customer(b.id());
        assertThat(as(customerB, () -> app.queryForObject(
                "select count(*) from rating.quote where quote_request_id = ?", Integer.class, id))).isZero();
        assertThat(as(customerB, () -> app.queryForObject(
                "select count(*) from rating.quote_request where id = ?", Integer.class, id))).isZero();
        TenantSnapshot customerA = TenantSnapshot.customer(a.id());
        assertThat(as(customerA, () -> app.queryForObject(
                "select count(*) from rating.quote_request where id = ?", Integer.class, id))).isEqualTo(1);
        assertThat(as(TenantSnapshot.none(), () -> app.queryForObject(
                "select count(*) from rating.quote where quote_request_id = ?", Integer.class, id))).isZero();
    }
}
