package mz.co.moseguros.rating.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import mz.co.moseguros.catalog.ProductType;
import mz.co.moseguros.rating.QuoteStatus;
import mz.co.moseguros.rating.QuotedCoverage;
import mz.co.moseguros.rating.UnavailableReason;
import mz.co.moseguros.shared.domain.Ids;
import mz.co.moseguros.shared.domain.Msisdn;
import mz.co.moseguros.shared.error.DomainException;
import org.junit.jupiter.api.Test;

/** Estado efectivo e uso de uma cotação (S3-02 §10) e a decomposição gravada. */
class QuoteTest {

    static final Instant CREATED = Instant.parse("2027-01-10T08:15:00Z");
    static final LocalDate START = LocalDate.of(2027, 1, 30);
    static final JsonNodeFactory F = JsonNodeFactory.instance;

    static QuoteRequest request() {
        return QuoteRequest.byCustomer(Ids.newId(), ProductType.COMPREHENSIVE, F.objectNode(), F.objectNode(),
                F.arrayNode(), START, 365, START.plusYears(1).minusDays(1), 12);
    }

    static Quote offered() {
        return Quote.offered(Ids.newId(), request(), Ids.newId(), Ids.newId(), Ids.newId(), RatingProviderKind.INTERNAL,
                F.objectNode().put("totalAmount", 100), 90, 10, 100, null, CREATED, Duration.ofDays(15), 3);
    }

    static Clock at(Instant instant) {
        return Clock.fixed(instant, ZoneOffset.UTC);
    }

    @Test
    void offeredUntilTheValidityEnds() {
        Quote q = offered();
        assertThat(q.getValidUntil()).isEqualTo(Instant.parse("2027-01-25T08:15:00Z"));
        assertThat(q.effectiveStatus(at(CREATED), START)).isEqualTo(QuoteStatus.OFFERED);
        assertThat(q.effectiveStatus(at(q.getValidUntil().minusMillis(1)), START)).isEqualTo(QuoteStatus.OFFERED);
        assertThat(q.effectiveStatus(at(q.getValidUntil()), START)).isEqualTo(QuoteStatus.EXPIRED);
        assertThat(q.getStatus()).isEqualTo(QuoteStatus.OFFERED);
    }

    @Test
    void expiresWhenTheStartDatePassesInMaputo() {
        Quote q = offered();
        LocalDate start = LocalDate.of(2027, 1, 11);
        // 2027-01-11T21:59Z ainda é dia 11 em Maputo (UTC+2); às 22:00Z já é dia 12
        assertThat(q.effectiveStatus(at(Instant.parse("2027-01-11T21:59:00Z")), start)).isEqualTo(QuoteStatus.OFFERED);
        assertThat(q.effectiveStatus(at(Instant.parse("2027-01-11T22:00:00Z")), start)).isEqualTo(QuoteStatus.EXPIRED);
    }

    @Test
    void usedOnlyOnce() {
        Quote q = offered();
        q.markUsed(at(CREATED.plusSeconds(60)), START);
        assertThat(q.getStatus()).isEqualTo(QuoteStatus.USED);
        assertThat(q.getUsedAt()).isEqualTo(CREATED.plusSeconds(60));
        assertThat(q.effectiveStatus(at(CREATED.plus(Duration.ofDays(30))), START)).isEqualTo(QuoteStatus.USED);
        assertThatThrownBy(() -> q.markUsed(at(CREATED), START)).isInstanceOf(DomainException.class)
                .extracting(e -> ((DomainException) e).getCode()).isEqualTo("QUOTE_ALREADY_USED");
    }

    @Test
    void expiredAndNotAvailableCannotBeUsed() {
        Quote expired = offered();
        assertThatThrownBy(() -> expired.markUsed(at(CREATED.plus(Duration.ofDays(15))), START))
                .isInstanceOf(DomainException.class)
                .extracting(e -> ((DomainException) e).getCode()).isEqualTo("QUOTE_EXPIRED");
        Quote none = Quote.notAvailable(Ids.newId(), request(), Ids.newId(), Ids.newId(), Ids.newId(),
                RatingProviderKind.EXTERNAL, UnavailableReason.INSURER_TIMEOUT, null, 3000);
        assertThat(none.effectiveStatus(at(CREATED), START)).isEqualTo(QuoteStatus.NOT_AVAILABLE);
        assertThat(none.getUnavailableDetail()).isNull();
        assertThat(none.getPremiumBreakdown()).isNull();
        assertThatThrownBy(() -> none.markUsed(at(CREATED), START)).isInstanceOf(DomainException.class)
                .extracting(e -> ((DomainException) e).getCode()).isEqualTo("QUOTE_NOT_AVAILABLE");
    }

    @Test
    void copiesTheRequestOwnersForRls() {
        QuoteRequest r = QuoteRequest.byBroker(Ids.newId(), Msisdn.of("841234567"), Ids.newId(), Ids.newId(),
                ProductType.MTPL_ONLY, F.objectNode(), F.objectNode(), F.arrayNode(), START, 365, START, -5);
        UUID id = Ids.newId();
        Quote q = Quote.notAvailable(id, r, Ids.newId(), Ids.newId(), Ids.newId(), RatingProviderKind.INTERNAL,
                UnavailableReason.TARIFF_MISSING, F.objectNode(), -1);
        assertThat(q.getId()).isEqualTo(id);
        assertThat(q.getQuoteRequestId()).isEqualTo(r.getId());
        assertThat(q.getCustomerUserId()).isEqualTo(r.getCustomerUserId());
        assertThat(q.getRequestedByOrgId()).isEqualTo(r.getRequestedByOrgId());
        assertThat(q.getRatingMs()).isZero();
        assertThat(r.getOrigin()).isEqualTo(QuoteOrigin.BROKER);
        assertThat(r.getEvaluationMs()).isZero();
        assertThat(r.getClientMsisdn().value()).isEqualTo("258841234567");
    }

    @Test
    void storedBreakdownReadsTheInternalAndTheExternalForms() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        StoredBreakdown internal = StoredBreakdown.parse(mapper.readTree("""
                {"provider":"INTERNAL","coverages":[
                  {"code":"MTPL","capitalAmount":300000000,"deductible":{"type":"NONE"},"premiumAmount":495000},
                  {"code":"OD_COLLISION","capitalAmount":123456789,
                   "deductible":{"type":"PERCENT","percent":"10.00","minAmount":2500000},"premiumAmount":3549383},
                  {"code":"OD_GLASS","capitalAmount":null,"deductible":{"type":"FIXED","amount":500000},"premiumAmount":1}],
                 "commercialPremiumAmount":4044384,
                 "charges":[{"code":"DEMO_STAMP","labelPt":"Selo","labelEn":"Stamp","type":"PERCENT","value":"2.5","amount":101110}],
                 "subtotalAmount":4145494,"roundingAdjustmentAmount":6,"totalAmount":4145500}"""));
        assertThat(internal.commercialPremiumAmount()).isEqualTo(4_044_384L);
        assertThat(internal.roundingAdjustmentAmount()).isEqualTo(6L);
        assertThat(internal.totalAmount()).isEqualTo(4_145_500L);
        assertThat(internal.charges()).hasSize(1);
        List<QuotedCoverage> coverages = internal.quotedCoverages(java.util.Set.of("MTPL"));
        assertThat(coverages.get(0).mandatory()).isTrue();
        assertThat(coverages.get(0).deductibleValue()).isNull();
        assertThat(coverages.get(1).deductibleValue()).isEqualTo("10");
        assertThat(coverages.get(1).mandatory()).isFalse();
        assertThat(coverages.get(2).deductibleValue()).isEqualTo("500000");
        assertThat(coverages.get(2).capitalAmount()).isZero();
        assertThat(internal.coverages().get(1).deductibleMinAmount()).isEqualTo(2_500_000L);

        StoredBreakdown external = StoredBreakdown.parse(mapper.readTree("""
                {"provider":"EXTERNAL","coverages":[{"code":"MTPL","capitalAmount":300000000,
                 "deductible":{"type":"NONE"},"premiumAmount":495000}],"commercialPremiumAmount":495000,
                 "charges":[],"totalAmount":495000}"""));
        assertThat(external.roundingAdjustmentAmount()).isZero();
        assertThat(external.totalAmount()).isEqualTo(495_000L);
    }
}
