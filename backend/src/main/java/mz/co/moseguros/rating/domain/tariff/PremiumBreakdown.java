package mz.co.moseguros.rating.domain.tariff;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import mz.co.moseguros.catalog.DeductibleType;
import mz.co.moseguros.organization.ChargeType;

/**
 * Decomposição de um prémio (S3-01 §10): o que a S3-02 grava em
 * {@code rating.quote.premium_breakdown} e devolve no simulador. {@link #toJson()} serializa os
 * decimais em texto ({@code toPlainString()}) e os centavos em inteiro.
 *
 * @param provider       {@code INTERNAL} (tarifa interna) ou o fornecedor externo (S3-02)
 * @param variables      só as variáveis presentes ({@link RatingVariables#toMap()})
 * @param durationFactor {@code fd}
 */
public record PremiumBreakdown(String provider, Map<String, Object> variables, BigDecimal durationFactor,
                               List<CoverageLine> coverages, long commercialPremiumAmount, List<ChargeLine> charges,
                               long subtotalAmount, long roundingAdjustmentAmount, long totalAmount) {

    public static final String INTERNAL = "INTERNAL";

    public PremiumBreakdown {
        Objects.requireNonNull(provider, "provider");
        variables = variables == null ? Map.of() : Collections.unmodifiableMap(new LinkedHashMap<>(variables));
        coverages = List.copyOf(coverages);
        charges = List.copyOf(charges);
    }

    /**
     * Prémio de uma cobertura.
     *
     * @param capitalAmount  capital resolvido (centavos); {@code null} se não tiver
     * @param exactPremium   {@code Pₑ = B × Π fᵢ × fbm × fd}, sem arredondamentos
     * @param premiumAmount  {@code Pₑ} arredondado ao centavo ({@code HALF_UP})
     */
    public record CoverageLine(String code, Long capitalAmount, Deductible deductible, BaseLine base,
                               List<FactorLine> factors, BigDecimal bonusMalusFactor, BigDecimal exactPremium,
                               long premiumAmount) {

        public CoverageLine {
            factors = List.copyOf(factors);
        }
    }

    /**
     * Franquia copiada do catálogo (não entra no cálculo).
     *
     * @param amount    só {@code FIXED}
     * @param percent   só {@code PERCENT}
     * @param minAmount só {@code PERCENT}; pode ser {@code null}
     */
    public record Deductible(DeductibleType type, Long amount, BigDecimal percent, Long minAmount) {

        public static Deductible of(CoverageDefinition c) {
            return switch (c.deductibleType()) {
                case NONE -> new Deductible(DeductibleType.NONE, null, null, null);
                case FIXED -> new Deductible(DeductibleType.FIXED, c.deductibleAmount(), null, null);
                case PERCENT -> new Deductible(DeductibleType.PERCENT, null, c.deductiblePercent(),
                        c.deductibleMinAmount());
            };
        }
    }

    /**
     * Linha da base usada.
     *
     * @param value  {@code AMOUNT}: centavos; {@code RATE_OF_*}: taxa
     * @param amount {@code B} exacto, em centavos
     */
    public record BaseLine(BaseType type, int rowIndex, String rowLabel, BigDecimal value, BigDecimal amount) {
    }

    /** Linha de um factor usada. */
    public record FactorLine(String code, String label, int rowIndex, String rowLabel, BigDecimal value) {
    }

    /**
     * Encargo calculado sobre o prémio comercial.
     *
     * @param percent     só {@code PERCENT}
     * @param fixedAmount só {@code FIXED}
     */
    public record ChargeLine(String code, String labelPt, String labelEn, ChargeType type, BigDecimal percent,
                             Long fixedAmount, long amount) {
    }

    /** JSON da decomposição (S3-01 §10). */
    public ObjectNode toJson() {
        JsonNodeFactory f = JsonNodeFactory.instance;
        ObjectNode root = f.objectNode();
        root.put("provider", provider);
        ObjectNode vars = root.putObject("variables");
        variables.forEach((k, v) -> {
            if (v instanceof Boolean b) {
                vars.put(k, b);
            } else if (v instanceof Integer i) {
                vars.put(k, i);
            } else if (v instanceof Long l) {
                vars.put(k, l);
            } else if (v != null) {
                vars.put(k, v.toString());
            }
        });
        root.put("durationFactor", plain(durationFactor));
        ArrayNode covs = root.putArray("coverages");
        for (CoverageLine c : coverages) {
            ObjectNode n = covs.addObject();
            n.put("code", c.code());
            putLong(n, "capitalAmount", c.capitalAmount());
            ObjectNode d = n.putObject("deductible");
            d.put("type", c.deductible().type().name());
            if (c.deductible().type() == DeductibleType.FIXED) {
                putLong(d, "amount", c.deductible().amount());
            } else if (c.deductible().type() == DeductibleType.PERCENT) {
                d.put("percent", plain(c.deductible().percent()));
                putLong(d, "minAmount", c.deductible().minAmount());
            }
            ObjectNode b = n.putObject("base");
            b.put("type", c.base().type().name());
            b.put("rowIndex", c.base().rowIndex());
            b.put("rowLabel", c.base().rowLabel());
            if (c.base().type() == BaseType.AMOUNT) {
                b.put("value", c.base().value().longValueExact());
            } else {
                b.put("value", plain(c.base().value()));
            }
            b.put("amount", plain(c.base().amount()));
            ArrayNode factors = n.putArray("factors");
            for (FactorLine fl : c.factors()) {
                ObjectNode fn = factors.addObject();
                fn.put("code", fl.code());
                fn.put("label", fl.label());
                fn.put("rowIndex", fl.rowIndex());
                fn.put("rowLabel", fl.rowLabel());
                fn.put("value", plain(fl.value()));
            }
            n.put("bonusMalusFactor", plain(c.bonusMalusFactor()));
            n.put("exactPremium", plain(c.exactPremium()));
            n.put("premiumAmount", c.premiumAmount());
        }
        root.put("commercialPremiumAmount", commercialPremiumAmount);
        ArrayNode chs = root.putArray("charges");
        for (ChargeLine c : charges) {
            ObjectNode n = chs.addObject();
            n.put("code", c.code());
            n.put("labelPt", c.labelPt());
            n.put("labelEn", c.labelEn());
            n.put("type", c.type().name());
            if (c.type() == ChargeType.PERCENT) {
                n.put("value", plain(c.percent()));
            } else {
                putLong(n, "value", c.fixedAmount());
            }
            n.put("amount", c.amount());
        }
        root.put("subtotalAmount", subtotalAmount);
        root.put("roundingAdjustmentAmount", roundingAdjustmentAmount);
        root.put("totalAmount", totalAmount);
        return root;
    }

    private static String plain(BigDecimal value) {
        return value == null ? null : value.toPlainString();
    }

    private static void putLong(ObjectNode node, String field, Long value) {
        if (value == null) {
            node.putNull(field);
        } else {
            node.put(field, value.longValue());
        }
    }
}
