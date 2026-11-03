package mz.co.moseguros.rating.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.databind.JsonNode;
import mz.co.moseguros.rating.QuotedCharge;
import mz.co.moseguros.rating.QuotedCoverage;

/**
 * Leitura da decomposição gravada em {@code rating.quote.premium_breakdown}: a interna
 * ({@code PremiumBreakdown}, S3-01 §10) e a externa (S3-02 §8) têm em comum as coberturas
 * ({@code code}, {@code capitalAmount}, {@code deductible}, {@code premiumAmount}), o prémio
 * comercial, os encargos e o total; a externa não tem ajuste de arredondamento (0).
 */
public record StoredBreakdown(List<Line> coverages, long commercialPremiumAmount, List<QuotedCharge> charges,
                              long roundingAdjustmentAmount, long totalAmount) {

    /**
     * Cobertura com o prémio.
     *
     * @param deductibleAmount    só {@code FIXED}
     * @param deductiblePercent   só {@code PERCENT}
     * @param deductibleMinAmount só {@code PERCENT}; pode ser {@code null}
     */
    public record Line(String code, Long capitalAmount, String deductibleType, Long deductibleAmount,
                       BigDecimal deductiblePercent, Long deductibleMinAmount, long premiumAmount) {

        /** {@code FIXED} → centavos em texto; {@code PERCENT} → a percentagem; {@code NONE} → {@code null}. */
        public String deductibleValue() {
            return switch (deductibleType) {
                case "FIXED" -> deductibleAmount == null ? null : String.valueOf(deductibleAmount);
                case "PERCENT" -> deductiblePercent == null ? null : plain(deductiblePercent);
                default -> null;
            };
        }
    }

    public static StoredBreakdown parse(JsonNode breakdown) {
        List<Line> lines = new ArrayList<>();
        for (JsonNode c : breakdown.path("coverages")) {
            JsonNode d = c.path("deductible");
            String type = d.path("type").asText("NONE");
            lines.add(new Line(c.path("code").asText(), longOrNull(c.get("capitalAmount")), type,
                    longOrNull(d.get("amount")),
                    d.hasNonNull("percent") ? new BigDecimal(d.get("percent").asText()) : null,
                    longOrNull(d.get("minAmount")), c.path("premiumAmount").asLong()));
        }
        List<QuotedCharge> charges = new ArrayList<>();
        for (JsonNode c : breakdown.path("charges")) {
            charges.add(new QuotedCharge(c.path("code").asText(), c.path("labelPt").asText(null),
                    c.path("labelEn").asText(null), c.path("amount").asLong()));
        }
        return new StoredBreakdown(List.copyOf(lines), breakdown.path("commercialPremiumAmount").asLong(),
                List.copyOf(charges), breakdown.path("roundingAdjustmentAmount").asLong(0),
                breakdown.path("totalAmount").asLong());
    }

    /** Coberturas para o {@code QuoteView}; {@code mandatory} = códigos obrigatórios da versão. */
    public List<QuotedCoverage> quotedCoverages(Set<String> mandatory) {
        return coverages.stream()
                .map(l -> new QuotedCoverage(l.code(), mandatory.contains(l.code()),
                        l.capitalAmount() == null ? 0 : l.capitalAmount(), l.deductibleType(), l.deductibleValue(),
                        l.premiumAmount()))
                .toList();
    }

    /** Percentagem sem zeros à direita ({@code 10.00} → {@code "10"}). */
    public static String plain(BigDecimal value) {
        return value.stripTrailingZeros().toPlainString();
    }

    private static Long longOrNull(JsonNode node) {
        return node == null || node.isNull() || node.isMissingNode() ? null : node.asLong();
    }
}
