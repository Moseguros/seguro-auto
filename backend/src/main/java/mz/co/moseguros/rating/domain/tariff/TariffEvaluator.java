package mz.co.moseguros.rating.domain.tariff;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;

import com.fasterxml.jackson.databind.JsonNode;
import mz.co.moseguros.organization.ChargeRule;
import mz.co.moseguros.rating.UnavailableReason;
import mz.co.moseguros.rating.domain.tariff.CoverageSelector.SelectedCoverage;
import mz.co.moseguros.rating.domain.tariff.CoverageSelector.Selection;
import mz.co.moseguros.rating.domain.tariff.EvaluationResult.Offered;
import mz.co.moseguros.rating.domain.tariff.EvaluationResult.Unavailable;
import mz.co.moseguros.rating.domain.tariff.PremiumBreakdown.BaseLine;
import mz.co.moseguros.rating.domain.tariff.PremiumBreakdown.ChargeLine;
import mz.co.moseguros.rating.domain.tariff.PremiumBreakdown.CoverageLine;
import mz.co.moseguros.rating.domain.tariff.PremiumBreakdown.Deductible;
import mz.co.moseguros.rating.domain.tariff.PremiumBreakdown.FactorLine;

/**
 * Calcula o prémio com a tarifa interna (S3-01 §9):
 * <ol>
 *   <li>coberturas e capitais pelo {@link CoverageSelector};</li>
 *   <li>sem tarifa → {@code TARIFF_MISSING}; tarifa ilegível → {@code TARIFF_INVALID};</li>
 *   <li>factor de duração {@code fd} ({@code 365} sem entrada → 1; outra duração sem entrada →
 *       {@code DURATION_NOT_RATED});</li>
 *   <li>por cobertura: a primeira linha da base e de cada factor que se cumpre (nenhuma →
 *       {@code NO_MATCHING_ROW}); {@code Pₑ = B × Π fᵢ × fbm × fd} exacto e {@code P = Pₑ}
 *       arredondado ao centavo ({@code HALF_UP});</li>
 *   <li>{@code PC = Σ P}; encargos ({@link ChargeCalculator}); {@code ST = PC + Σ encargos};
 *       total arredondado para cima ao metical e ajuste {@code A = T − ST}.</li>
 * </ol>
 * A primeira cobertura que falhe torna toda a cotação {@code Unavailable} (não há preços parciais).
 * Nada depende da hora actual: o mesmo pedido dá sempre o mesmo resultado.
 */
public final class TariffEvaluator {

    /** Factor bónus/malus: sempre 1 até ao S7. */
    public static final BigDecimal BONUS_MALUS_FACTOR = BigDecimal.ONE;
    static final String BASE_TABLE = "BASE";
    private static final int FULL_YEAR = 365;

    private TariffEvaluator() {
    }

    /**
     * Avalia com a tarifa em JSON ({@code rules.rating}): {@code null}, ausente ou JSON nulo →
     * {@code TARIFF_MISSING}; não se consegue ler → {@code TARIFF_INVALID}.
     */
    public static EvaluationResult evaluate(RatingInput input, ProductDefinition product, JsonNode rating,
                                            List<ChargeRule> charges) {
        Selection selection = CoverageSelector.select(input, product);
        if (!selection.isAvailable()) {
            return selection.unavailable();
        }
        if (rating == null || rating.isNull() || rating.isMissingNode()) {
            return Unavailable.of(UnavailableReason.TARIFF_MISSING);
        }
        RatingRules rules;
        try {
            rules = RatingRulesParser.parse(rating);
        } catch (RatingRulesParser.InvalidTariffException e) {
            return Unavailable.of(UnavailableReason.TARIFF_INVALID);
        }
        return price(input, selection, rules, charges);
    }

    /** Avalia com a tarifa já lida; {@code rules} {@code null} → {@code TARIFF_MISSING}. */
    public static EvaluationResult evaluate(RatingInput input, ProductDefinition product, RatingRules rules,
                                            List<ChargeRule> charges) {
        Selection selection = CoverageSelector.select(input, product);
        if (!selection.isAvailable()) {
            return selection.unavailable();
        }
        if (rules == null) {
            return Unavailable.of(UnavailableReason.TARIFF_MISSING);
        }
        return price(input, selection, rules, charges);
    }

    private static EvaluationResult price(RatingInput input, Selection selection, RatingRules rules,
                                          List<ChargeRule> charges) {
        Optional<BigDecimal> configuredDuration = rules.durationFactor(input.durationDays());
        BigDecimal fd;
        if (configuredDuration.isPresent()) {
            fd = configuredDuration.get();
        } else if (input.durationDays() == FULL_YEAR) {
            fd = BigDecimal.ONE;
        } else {
            return new Unavailable(UnavailableReason.DURATION_NOT_RATED, Map.of("durationDays", input.durationDays()));
        }

        RatingVariables variables = RatingVariables.of(input.vehicle(), input.driver(), input.startDate(),
                input.durationDays());
        List<CoverageLine> lines = new ArrayList<>();
        long commercialPremium = 0;
        for (SelectedCoverage selected : selection.coverages()) {
            CoverageDefinition coverage = selected.coverage();
            Optional<CoverageTariff> tariff = rules.coverage(coverage.code());
            if (tariff.isEmpty()) {
                return new Unavailable(UnavailableReason.TARIFF_INVALID, Map.of("coverageCode", coverage.code().name()));
            }
            Object line = coverageLine(tariff.get(), selected, variables, fd);
            if (line instanceof Unavailable unavailable) {
                return unavailable;
            }
            CoverageLine coverageLine = (CoverageLine) line;
            lines.add(coverageLine);
            commercialPremium = Math.addExact(commercialPremium, coverageLine.premiumAmount());
        }

        List<ChargeLine> chargeLines = ChargeCalculator.calculate(commercialPremium, charges);
        long subtotal = commercialPremium;
        for (ChargeLine c : chargeLines) {
            subtotal = Math.addExact(subtotal, c.amount());
        }
        long total = Math.floorDiv(Math.addExact(subtotal, 99), 100) * 100;
        return new Offered(new PremiumBreakdown(PremiumBreakdown.INTERNAL, variables.toMap(), fd, lines,
                commercialPremium, chargeLines, subtotal, total - subtotal, total));
    }

    /** {@link CoverageLine} ou {@link Unavailable}. */
    private static Object coverageLine(CoverageTariff tariff, SelectedCoverage selected, RatingVariables variables,
                                       BigDecimal fd) {
        String code = selected.coverage().code().name();
        Long capital = selected.capital();
        BaseTable base = tariff.base();
        OptionalInt baseIndex = base.firstMatch(variables, capital);
        if (baseIndex.isEmpty()) {
            return noMatchingRow(code, BASE_TABLE);
        }
        TariffRow baseRow = base.rows().get(baseIndex.getAsInt());
        BigDecimal b;
        switch (base.type()) {
            case AMOUNT -> b = baseRow.value();
            case RATE_OF_VEHICLE_VALUE -> {
                if (variables.vehicleValue() == null) {
                    return new Unavailable(UnavailableReason.VEHICLE_VALUE_REQUIRED, Map.of("coverageCode", code));
                }
                b = BigDecimal.valueOf(variables.vehicleValue()).multiply(baseRow.value());
            }
            case RATE_OF_CAPITAL -> {
                if (capital == null) {
                    return new Unavailable(UnavailableReason.TARIFF_INVALID, Map.of("coverageCode", code));
                }
                b = BigDecimal.valueOf(capital).multiply(baseRow.value());
            }
            default -> throw new IllegalStateException("Unknown base type: " + base.type());
        }

        BigDecimal exact = b;
        List<FactorLine> factorLines = new ArrayList<>();
        for (FactorTable factor : tariff.factors()) {
            OptionalInt index = factor.firstMatch(variables, capital);
            if (index.isEmpty()) {
                return noMatchingRow(code, factor.code());
            }
            TariffRow row = factor.rows().get(index.getAsInt());
            exact = exact.multiply(row.value());
            factorLines.add(new FactorLine(factor.code(), factor.label(), index.getAsInt(), row.label(), row.value()));
        }
        exact = exact.multiply(BONUS_MALUS_FACTOR).multiply(fd);
        long premium = exact.setScale(0, RoundingMode.HALF_UP).longValueExact();
        return new CoverageLine(code, capital, Deductible.of(selected.coverage()),
                new BaseLine(base.type(), baseIndex.getAsInt(), baseRow.label(), baseRow.value(), b),
                factorLines, BONUS_MALUS_FACTOR, exact, premium);
    }

    private static Unavailable noMatchingRow(String coverageCode, String table) {
        Map<String, Object> detail = new LinkedHashMap<>();
        detail.put("coverageCode", coverageCode);
        detail.put("table", table);
        return new Unavailable(UnavailableReason.NO_MATCHING_ROW, detail);
    }
}
