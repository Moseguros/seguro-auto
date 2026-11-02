package mz.co.moseguros.shared.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.error.ErrorCodes;

/**
 * Valor monetário em centavos. A moeda é sempre {@code MZN}.
 *
 * <p>JSON: {@code {"amount": 299900, "currency": "MZN"}}.
 */
public record Money(long amount, String currency) implements Comparable<Money> {

    public static final String MZN = "MZN";

    public Money {
        if (!MZN.equals(currency)) {
            throw DomainException.badRequest(ErrorCodes.INVALID_CURRENCY, "Unsupported currency: " + currency);
        }
    }

    public static Money ofCentavos(long centavos) {
        return new Money(centavos, MZN);
    }

    /** Converte meticais em centavos, arredondando HALF_UP a 2 casas decimais. */
    public static Money ofMeticais(BigDecimal meticais) {
        Objects.requireNonNull(meticais, "meticais");
        return ofCentavos(meticais.setScale(2, RoundingMode.HALF_UP).movePointRight(2).longValueExact());
    }

    public static Money zero() {
        return ofCentavos(0);
    }

    public Money plus(Money other) {
        return ofCentavos(Math.addExact(amount, other.amount));
    }

    public Money minus(Money other) {
        return ofCentavos(Math.subtractExact(amount, other.amount));
    }

    /** Multiplica por um factor, arredondando HALF_UP ao centavo. */
    public Money times(BigDecimal factor) {
        Objects.requireNonNull(factor, "factor");
        return ofCentavos(BigDecimal.valueOf(amount).multiply(factor)
                .setScale(0, RoundingMode.HALF_UP).longValueExact());
    }

    public boolean isNegative() {
        return amount < 0;
    }

    @Override
    public int compareTo(Money other) {
        return Long.compare(amount, other.amount);
    }
}
