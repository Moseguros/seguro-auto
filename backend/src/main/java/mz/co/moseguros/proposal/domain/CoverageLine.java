package mz.co.moseguros.proposal.domain;

import java.math.BigDecimal;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import mz.co.moseguros.shared.domain.Money;

/**
 * Cobertura da proposta tal como foi cotada ({@code proposal.coverages}, S4-02 §5), no formato do
 * {@code QuoteResponse} do S3-02. Os nomes vêm do i18n ({@code coverage.<CODE>}), não se guardam.
 *
 * @param capital {@code null} se a cobertura não tiver capital
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record CoverageLine(String code, boolean mandatory, Money capital, Deductible deductible, Money premium) {

    public CoverageLine {
        Objects.requireNonNull(code, "code");
        Objects.requireNonNull(deductible, "deductible");
        Objects.requireNonNull(premium, "premium");
    }

    /** Cobertura de danos próprios ({@code OD_*}). */
    @JsonIgnore
    public boolean isOwnDamage() {
        return code.startsWith("OD_");
    }

    /**
     * Franquia: {@code { "type": "NONE" }}, {@code { "type": "FIXED", "amount" }} ou
     * {@code { "type": "PERCENT", "percent": "10", "minAmount" }} ({@code minAmount} só se o produto
     * o tiver).
     *
     * @param percent percentagem em texto ({@code "10"} = 10 %)
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record Deductible(String type, Money amount, String percent, Money minAmount) {

        public static final String NONE = "NONE";
        public static final String FIXED = "FIXED";
        public static final String PERCENT = "PERCENT";

        public Deductible {
            Objects.requireNonNull(type, "type");
        }

        public static Deductible none() {
            return new Deductible(NONE, null, null, null);
        }

        @JsonIgnore
        public boolean isNone() {
            return NONE.equals(type);
        }

        /** {@code PERCENT}: a percentagem; senão {@code null}. */
        @JsonIgnore
        public BigDecimal percentValue() {
            return percent == null ? null : new BigDecimal(percent);
        }
    }
}
