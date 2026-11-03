package mz.co.moseguros.rating.api;

import static mz.co.moseguros.rating.QuoteTestSupport.FULL_OD;
import static mz.co.moseguros.rating.QuoteTestSupport.START;
import static mz.co.moseguros.rating.QuoteTestSupport.c05Driver;
import static mz.co.moseguros.rating.QuoteTestSupport.c05Vehicle;
import static mz.co.moseguros.rating.QuoteTestSupport.c07Vehicle;
import static mz.co.moseguros.rating.QuoteTestSupport.getQuote;
import static mz.co.moseguros.rating.QuoteTestSupport.postQuote;
import static mz.co.moseguros.rating.QuoteTestSupport.quoteBody;
import static mz.co.moseguros.rating.QuoteTestSupport.quotesOf;
import static mz.co.moseguros.rating.QuoteTestSupport.vehicle;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Duration;
import java.time.LocalDate;
import java.util.UUID;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import mz.co.moseguros.SecurityTestConfiguration;
import mz.co.moseguros.SecurityTestConfiguration.IdentityFixtures;
import mz.co.moseguros.SecurityTestConfiguration.MutableClock;
import mz.co.moseguros.TestcontainersConfiguration;
import mz.co.moseguros.catalog.CatalogTestSupport.Insurer;
import mz.co.moseguros.identity.IdentityApi;
import mz.co.moseguros.rating.QuoteStatus;
import mz.co.moseguros.rating.QuoteTestSupport;
import mz.co.moseguros.rating.QuoteTestSupport.Customer;
import mz.co.moseguros.rating.QuoteView;
import mz.co.moseguros.rating.RatingApi;
import mz.co.moseguros.rating.domain.tariff.ReferenceData;
import mz.co.moseguros.shared.error.DomainException;
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
import org.springframework.transaction.IllegalTransactionStateException;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Validade e uso das cotações (S3-02 §10), com o relógio controlado: {@code RatingApi.markUsed}
 * uma só vez, estado efectivo {@code EXPIRED} passados 15 dias ou com a data de início no passado,
 * {@code NOT_AVAILABLE} não se usa, e {@code markUsed} exige uma transacção.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestcontainersConfiguration.class, SecurityTestConfiguration.class})
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class QuoteExpiryIT {

    @LocalServerPort
    int port;

    @Autowired
    IdentityFixtures fixtures;

    @Autowired
    IdentityApi identityApi;

    @Autowired
    MutableClock clock;

    @Autowired
    RatingApi ratingApi;

    @Autowired
    TransactionTemplate tx;

    QuoteTestSupport support;
    Insurer insurer;

    @BeforeAll
    void createInsurer() {
        RestAssured.port = port;
        support = new QuoteTestSupport(fixtures, identityApi, clock);
        support.atNow();
        insurer = support.insurerWithCharges("Seguradora Validade");
        support.publishedDemoAuto(insurer.manager(), ReferenceData.tariff("DEMO-AUTO"));
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
    }

    /** Pedido C05 (ou C07) com a data de início indicada; devolve [pedido, cotação da seguradora]. */
    UUID[] quote(Customer c, java.util.Map<String, Object> vehicleBody, LocalDate start) throws Exception {
        Response r = postQuote(c.token(), quoteBody(vehicle(c.token(), vehicleBody), c05Driver(c.token()),
                "COMPREHENSIVE", start, 365, FULL_OD));
        r.then().statusCode(201);
        return new UUID[] {UUID.fromString(r.path("id")),
                UUID.fromString(quotesOf(r, insurer.id()).get(0).get("id").asText())};
    }

    void markUsed(Customer c, UUID quoteId) {
        TenantSnapshot previous = TenantContext.current();
        TenantContext.set(TenantSnapshot.customer(c.id()));
        try {
            tx.executeWithoutResult(status -> ratingApi.markUsed(quoteId));
        } finally {
            TenantContext.set(previous);
        }
    }

    QuoteView view(Customer c, UUID quoteId) {
        TenantSnapshot previous = TenantContext.current();
        TenantContext.set(TenantSnapshot.customer(c.id()));
        try {
            return ratingApi.quote(quoteId);
        } finally {
            TenantContext.set(previous);
        }
    }

    static String codeOf(Throwable e) {
        return ((DomainException) e).getCode();
    }

    String statusInApi(Customer c, UUID requestId) throws Exception {
        Response r = getQuote(support.token(c.account()), requestId);
        r.then().statusCode(200);
        return quotesOf(r, insurer.id()).get(0).get("status").asText();
    }

    @Test
    void aValidQuoteIsUsedOnce() throws Exception {
        Customer c = support.customer();
        UUID[] ids = quote(c, c05Vehicle(), START);
        QuoteView before = view(c, ids[1]);
        assertThat(before.status()).isEqualTo(QuoteStatus.OFFERED);
        assertThat(before.quoteRequestId()).isEqualTo(ids[0]);
        assertThat(before.insurerId()).isEqualTo(insurer.id());
        assertThat(before.origin()).isEqualTo("CUSTOMER");
        assertThat(before.customerUserId()).isEqualTo(c.id());
        assertThat(before.totalAmount()).isEqualTo(QuoteTestSupport.C05_TOTAL);
        assertThat(before.roundingAdjustmentAmount()).isEqualTo(90L);
        assertThat(before.commercialPremiumAmount()).isEqualTo(5_093_766L);
        assertThat(before.coverages()).hasSize(5);
        assertThat(before.coverages().get(0).code()).isEqualTo("MTPL");
        assertThat(before.coverages().get(0).mandatory()).isTrue();
        assertThat(before.coverages().get(1).deductibleType()).isEqualTo("PERCENT");
        assertThat(before.coverages().get(1).deductibleValue()).isEqualTo("10");
        assertThat(before.charges()).hasSize(2);
        assertThat(before.vehicle().commercialValue()).isEqualTo(123_456_789L);
        assertThat(before.driver().birthDate()).isEqualTo(LocalDate.of(1991, 9, 9));
        assertThat(before.endDate()).isEqualTo(LocalDate.of(2028, 1, 14));
        assertThat(before.provider()).isEqualTo("INTERNAL");

        markUsed(c, ids[1]);
        assertThat(view(c, ids[1]).status()).isEqualTo(QuoteStatus.USED);
        assertThat(fixtures.owner().queryForObject("select used_at is not null from rating.quote where id = ?",
                Boolean.class, ids[1])).isTrue();
        assertThat(statusInApi(c, ids[0])).isEqualTo("USED");

        assertThatThrownBy(() -> markUsed(c, ids[1])).isInstanceOf(DomainException.class)
                .satisfies(e -> assertThat(codeOf(e)).isEqualTo("QUOTE_ALREADY_USED"));
    }

    @Test
    void after15DaysTheQuoteIsExpired() throws Exception {
        Customer c = support.customer();
        // início daqui a 20 dias: passados 15 só a validade conta
        UUID[] ids = quote(c, c05Vehicle(), LocalDate.of(2027, 1, 30));
        support.advance(Duration.ofDays(15));
        assertThat(view(c, ids[1]).status()).isEqualTo(QuoteStatus.EXPIRED);
        assertThat(statusInApi(c, ids[0])).isEqualTo("EXPIRED");
        assertThatThrownBy(() -> markUsed(c, ids[1])).isInstanceOf(DomainException.class)
                .satisfies(e -> {
                    assertThat(codeOf(e)).isEqualTo("QUOTE_EXPIRED");
                    assertThat(((DomainException) e).getHttpStatus()).isEqualTo(409);
                });
        // na BD continua OFFERED: o estado efectivo calcula-se na leitura
        assertThat(fixtures.owner().queryForObject("select status from rating.quote where id = ?", String.class,
                ids[1])).isEqualTo("OFFERED");
    }

    @Test
    void aStartDateInThePastExpiresTheQuote() throws Exception {
        Customer c = support.customer();
        UUID[] ids = quote(c, c05Vehicle(), LocalDate.of(2027, 1, 11));
        support.advance(Duration.ofDays(1));
        assertThat(view(c, ids[1]).status()).isEqualTo(QuoteStatus.OFFERED);
        support.advance(Duration.ofDays(1));
        assertThat(view(c, ids[1]).status()).isEqualTo(QuoteStatus.EXPIRED);
        assertThatThrownBy(() -> markUsed(c, ids[1])).isInstanceOf(DomainException.class)
                .satisfies(e -> assertThat(codeOf(e)).isEqualTo("QUOTE_EXPIRED"));
    }

    @Test
    void aQuoteWithoutPriceCannotBeUsed() throws Exception {
        Customer c = support.customer();
        UUID[] ids = quote(c, c07Vehicle(), START);
        assertThat(view(c, ids[1]).status()).isEqualTo(QuoteStatus.NOT_AVAILABLE);
        assertThat(view(c, ids[1]).coverages()).isEmpty();
        assertThatThrownBy(() -> markUsed(c, ids[1])).isInstanceOf(DomainException.class)
                .satisfies(e -> assertThat(codeOf(e)).isEqualTo("QUOTE_NOT_AVAILABLE"));
    }

    @Test
    void markUsedNeedsATransactionAndAVisibleQuote() throws Exception {
        Customer c = support.customer();
        UUID[] ids = quote(c, c05Vehicle(), START);
        TenantContext.set(TenantSnapshot.customer(c.id()));
        assertThatThrownBy(() -> ratingApi.markUsed(ids[1])).isInstanceOf(IllegalTransactionStateException.class);
        TenantContext.clear();

        Customer other = support.customer();
        assertThatThrownBy(() -> markUsed(other, ids[1])).isInstanceOf(DomainException.class)
                .satisfies(e -> assertThat(codeOf(e)).isEqualTo("QUOTE_NOT_FOUND"));
        assertThatThrownBy(() -> view(other, ids[1])).isInstanceOf(DomainException.class)
                .satisfies(e -> assertThat(codeOf(e)).isEqualTo("QUOTE_NOT_FOUND"));
        assertThat(view(c, ids[1]).status()).isEqualTo(QuoteStatus.OFFERED);
    }
}
