package mz.co.moseguros.proposal.domain;

import java.util.List;
import java.util.Objects;

import mz.co.moseguros.shared.domain.Money;

/**
 * Decomposição do prémio copiada da cotação ({@code proposal.premium_breakdown}, S4-02 §5): prémio
 * comercial, encargos (com os nomes da seguradora em pt e en), ajuste do arredondamento e total.
 * {@code total.amount = proposal.total_amount}.
 */
public record PremiumBreakdown(Money commercial, List<Charge> charges, Money roundingAdjustment, Money total) {

    public PremiumBreakdown {
        Objects.requireNonNull(commercial, "commercial");
        charges = charges == null ? List.of() : List.copyOf(charges);
        Objects.requireNonNull(roundingAdjustment, "roundingAdjustment");
        Objects.requireNonNull(total, "total");
    }

    /** Encargo calculado (imposto ou taxa). */
    public record Charge(String code, String labelPt, String labelEn, Money amount) {
    }
}
