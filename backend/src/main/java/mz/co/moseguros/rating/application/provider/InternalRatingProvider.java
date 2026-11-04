package mz.co.moseguros.rating.application.provider;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.JsonNode;
import mz.co.moseguros.catalog.DeductibleType;
import mz.co.moseguros.organization.ChargeRule;
import mz.co.moseguros.organization.RatingMode;
import mz.co.moseguros.rating.QuoteResult;
import mz.co.moseguros.rating.QuoteStatus;
import mz.co.moseguros.rating.QuotedCharge;
import mz.co.moseguros.rating.QuotedCoverage;
import mz.co.moseguros.rating.application.RatingRulesService;
import mz.co.moseguros.rating.domain.RatingProviderKind;
import mz.co.moseguros.rating.domain.StoredBreakdown;
import mz.co.moseguros.rating.domain.tariff.CoverageDefinition;
import mz.co.moseguros.rating.domain.tariff.EvaluationResult;
import mz.co.moseguros.rating.domain.tariff.EvaluationResult.Offered;
import mz.co.moseguros.rating.domain.tariff.EvaluationResult.Unavailable;
import mz.co.moseguros.rating.domain.tariff.PremiumBreakdown;
import mz.co.moseguros.rating.domain.tariff.PremiumBreakdown.ChargeLine;
import mz.co.moseguros.rating.domain.tariff.PremiumBreakdown.CoverageLine;
import mz.co.moseguros.rating.domain.tariff.ProductDefinition;
import mz.co.moseguros.rating.domain.tariff.TariffEvaluator;
import org.springframework.stereotype.Component;

/**
 * Tarifa interna (S3-02 §4): {@code CoverageSelector} + {@code TariffEvaluator} +
 * {@code ChargeCalculator} (S3-01) com a tarifa da versão ({@code rules.rating}) e os encargos da
 * seguradora. Também é o que o simulador usa (com a tarifa e os encargos do ecrã, se vierem).
 */
@Component
public class InternalRatingProvider implements RatingProvider {

    @Override
    public boolean supports(RatingMode mode) {
        return mode == RatingMode.INTERNAL;
    }

    @Override
    public ProviderResult rate(RatingRequest request) {
        long start = System.nanoTime();
        EvaluationResult result = evaluate(request, RatingRulesService.ratingOf(request.version()),
                request.insurer().charges());
        return toProviderResult(result, request.product(), elapsedMs(start));
    }

    /**
     * Avalia com uma tarifa e uns encargos dados (o simulador passa os do ecrã).
     *
     * @param rating {@code null} = sem tarifa ({@code TARIFF_MISSING})
     */
    public EvaluationResult evaluate(RatingRequest request, JsonNode rating, List<ChargeRule> charges) {
        return TariffEvaluator.evaluate(request.input(), request.product(), rating, charges);
    }

    static ProviderResult toProviderResult(EvaluationResult result, ProductDefinition product, int ratingMs) {
        String provider = RatingProviderKind.INTERNAL.name();
        if (result instanceof Unavailable u) {
            return new ProviderResult(QuoteResult.notAvailable(u.reason(), u.detail(), provider), null, ratingMs);
        }
        PremiumBreakdown b = ((Offered) result).breakdown();
        Set<String> mandatory = product.coverages().stream().filter(CoverageDefinition::mandatory)
                .map(c -> c.code().name()).collect(Collectors.toSet());
        List<QuotedCoverage> coverages = b.coverages().stream().map(c -> coverage(c, mandatory)).toList();
        List<QuotedCharge> charges = b.charges().stream().map(InternalRatingProvider::charge).toList();
        QuoteResult quote = new QuoteResult(QuoteStatus.OFFERED, null, null, coverages, b.commercialPremiumAmount(),
                charges, b.roundingAdjustmentAmount(), b.totalAmount(), provider, null);
        return new ProviderResult(quote, b.toJson(), ratingMs);
    }

    private static QuotedCoverage coverage(CoverageLine c, Set<String> mandatory) {
        PremiumBreakdown.Deductible d = c.deductible();
        String value = d.type() == DeductibleType.FIXED ? (d.amount() == null ? null : String.valueOf(d.amount()))
                : d.type() == DeductibleType.PERCENT && d.percent() != null ? StoredBreakdown.plain(d.percent())
                : null;
        return new QuotedCoverage(c.code(), mandatory.contains(c.code()),
                c.capitalAmount() == null ? 0 : c.capitalAmount(), d.type().name(), value, c.premiumAmount());
    }

    private static QuotedCharge charge(ChargeLine c) {
        return new QuotedCharge(c.code(), c.labelPt(), c.labelEn(), c.amount());
    }

    static int elapsedMs(long startNanos) {
        return (int) Math.min(Integer.MAX_VALUE, (System.nanoTime() - startNanos) / 1_000_000);
    }
}
