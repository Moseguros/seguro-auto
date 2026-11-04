package mz.co.moseguros.rating;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Resultado da tarifação de uma versão de produto por uma seguradora (S3-02 §4): com preço
 * ({@code OFFERED}) ou sem preço e com o motivo ({@code NOT_AVAILABLE}). Público: o S7 reutiliza-o
 * em {@code rateRenewal}.
 *
 * @param status                   só {@code OFFERED} ou {@code NOT_AVAILABLE}
 * @param unavailableReason        só em {@code NOT_AVAILABLE}
 * @param unavailableDetail        pormenores do motivo (ex.: {@code {"coverageCode": "MTPL", "table": "BASE"}})
 * @param coverages                só em {@code OFFERED}
 * @param commercialPremiumAmount  prémio comercial em centavos (só em {@code OFFERED})
 * @param charges                  encargos calculados (só em {@code OFFERED})
 * @param roundingAdjustmentAmount ajuste do arredondamento ao metical (0 a 99; {@code 0} na tarifação externa)
 * @param totalAmount              total a pagar em centavos (só em {@code OFFERED})
 * @param provider                 {@code INTERNAL} ou {@code EXTERNAL}
 * @param externalReference        referência devolvida pela seguradora externa ({@code null} na interna)
 */
public record QuoteResult(QuoteStatus status,
                          UnavailableReason unavailableReason, Map<String, Object> unavailableDetail,
                          List<QuotedCoverage> coverages, Long commercialPremiumAmount, List<QuotedCharge> charges,
                          Long roundingAdjustmentAmount, Long totalAmount,
                          String provider, String externalReference) {

    public QuoteResult {
        if (status != QuoteStatus.OFFERED && status != QuoteStatus.NOT_AVAILABLE) {
            throw new IllegalArgumentException("A rating result is OFFERED or NOT_AVAILABLE: " + status);
        }
        if (status == QuoteStatus.NOT_AVAILABLE && unavailableReason == null) {
            throw new IllegalArgumentException("NOT_AVAILABLE needs a reason");
        }
        if (status == QuoteStatus.OFFERED && totalAmount == null) {
            throw new IllegalArgumentException("OFFERED needs a total");
        }
        coverages = coverages == null ? List.of() : List.copyOf(coverages);
        charges = charges == null ? List.of() : List.copyOf(charges);
        unavailableDetail = unavailableDetail == null ? null
                : Collections.unmodifiableMap(new LinkedHashMap<>(unavailableDetail));
    }

    /** Sem preço, com o motivo. */
    public static QuoteResult notAvailable(UnavailableReason reason, Map<String, Object> detail, String provider) {
        return new QuoteResult(QuoteStatus.NOT_AVAILABLE, reason, detail == null ? Map.of() : detail, List.of(), null,
                List.of(), null, null, provider, null);
    }

    public boolean isOffered() {
        return status == QuoteStatus.OFFERED;
    }
}
