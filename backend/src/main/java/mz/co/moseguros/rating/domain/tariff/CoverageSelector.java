package mz.co.moseguros.rating.domain.tariff;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import mz.co.moseguros.catalog.CoverageCode;
import mz.co.moseguros.rating.UnavailableReason;
import mz.co.moseguros.rating.domain.tariff.EvaluationResult.Unavailable;
import mz.co.moseguros.rating.domain.tariff.RatingInput.RequestedCoverage;

/**
 * Escolhe as coberturas a tarifar e resolve o capital de cada uma (S3-01 §9, "Selecção"). Separado
 * do {@link TariffEvaluator} porque a tarifação externa (S3-02) também o usa. Pára no primeiro
 * problema:
 * <ol>
 *   <li>{@code S} = obrigatórias da versão ∪ pedidas;</li>
 *   <li>pedida que a versão não tem → {@code COVERAGE_NOT_OFFERED} ({@code codes});</li>
 *   <li>{@code requiresCodes} fora de {@code S} → {@code COVERAGE_REQUIREMENTS_NOT_MET}
 *       ({@code coverageCode}, {@code missing});</li>
 *   <li>capital: {@code FIXED}/{@code PER_PERSON} → o do catálogo; {@code VEHICLE_VALUE} → valor do
 *       veículo ({@code VEHICLE_VALUE_REQUIRED}); {@code OPTIONS} → a menor opção ≥ pedido (sem
 *       pedido, a menor), senão {@code CAPITAL_NOT_OFFERED} ({@code coverageCode},
 *       {@code requested}, {@code maxOffered});</li>
 *   <li>ordem: a das coberturas da versão.</li>
 * </ol>
 */
public final class CoverageSelector {

    private CoverageSelector() {
    }

    /**
     * Cobertura escolhida.
     *
     * @param capital capital resolvido em centavos
     */
    public record SelectedCoverage(CoverageDefinition coverage, Long capital) {
    }

    /** Coberturas escolhidas (pela ordem da versão) ou o motivo de não haver cotação. */
    public record Selection(List<SelectedCoverage> coverages, Unavailable unavailable) {

        public Selection {
            coverages = coverages == null ? List.of() : List.copyOf(coverages);
        }

        static Selection of(List<SelectedCoverage> coverages) {
            return new Selection(coverages, null);
        }

        static Selection rejected(UnavailableReason reason, Map<String, Object> detail) {
            return new Selection(List.of(), new Unavailable(reason, detail));
        }

        public boolean isAvailable() {
            return unavailable == null;
        }
    }

    public static Selection select(RatingInput input, ProductDefinition product) {
        Objects.requireNonNull(input, "input");
        Objects.requireNonNull(product, "product");

        Map<CoverageCode, Long> requested = new EnumMap<>(CoverageCode.class);
        Set<CoverageCode> notOffered = new LinkedHashSet<>();
        for (RequestedCoverage r : input.requestedCoverages()) {
            if (product.coverage(r.code()).isEmpty()) {
                notOffered.add(r.code());
            } else if (!requested.containsKey(r.code())) {
                requested.put(r.code(), r.capital());
            }
        }
        if (!notOffered.isEmpty()) {
            return Selection.rejected(UnavailableReason.COVERAGE_NOT_OFFERED,
                    Map.of("codes", notOffered.stream().map(Enum::name).toList()));
        }

        Set<CoverageCode> chosen = EnumSet.noneOf(CoverageCode.class);
        List<CoverageDefinition> ordered = new ArrayList<>();
        for (CoverageDefinition c : product.coverages()) {
            if (c.mandatory() || requested.containsKey(c.code())) {
                chosen.add(c.code());
                ordered.add(c);
            }
        }

        for (CoverageDefinition c : ordered) {
            List<String> missing = c.requiresCodes().stream().filter(code -> !chosen.contains(code))
                    .map(Enum::name).toList();
            if (!missing.isEmpty()) {
                Map<String, Object> detail = new LinkedHashMap<>();
                detail.put("coverageCode", c.code().name());
                detail.put("missing", missing);
                return Selection.rejected(UnavailableReason.COVERAGE_REQUIREMENTS_NOT_MET, detail);
            }
        }

        List<SelectedCoverage> selected = new ArrayList<>();
        for (CoverageDefinition c : ordered) {
            Long requestedCapital = requested.get(c.code());
            switch (c.capitalType()) {
                case FIXED, PER_PERSON -> selected.add(new SelectedCoverage(c, c.capital()));
                case VEHICLE_VALUE -> {
                    Long value = input.vehicle().commercialValue();
                    if (value == null) {
                        return Selection.rejected(UnavailableReason.VEHICLE_VALUE_REQUIRED,
                                Map.of("coverageCode", c.code().name()));
                    }
                    selected.add(new SelectedCoverage(c, value));
                }
                case OPTIONS -> {
                    List<Long> options = c.capitalOptions() == null ? List.of()
                            : c.capitalOptions().stream().sorted().toList();
                    Long capital = options.stream()
                            .filter(o -> requestedCapital == null || o >= requestedCapital)
                            .findFirst().orElse(null);
                    if (capital == null) {
                        Map<String, Object> detail = new LinkedHashMap<>();
                        detail.put("coverageCode", c.code().name());
                        detail.put("requested", requestedCapital);
                        detail.put("maxOffered", options.isEmpty() ? null : options.get(options.size() - 1));
                        return Selection.rejected(UnavailableReason.CAPITAL_NOT_OFFERED, detail);
                    }
                    selected.add(new SelectedCoverage(c, capital));
                }
                default -> throw new IllegalStateException("Unknown capital type: " + c.capitalType());
            }
        }
        return Selection.of(selected);
    }
}
