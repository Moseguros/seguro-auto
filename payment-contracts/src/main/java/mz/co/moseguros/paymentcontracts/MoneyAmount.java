package mz.co.moseguros.paymentcontracts;

import java.util.Objects;

/**
 * Montante em centavos ({@code { "amount": 300000, "currency": "MZN" }} = 3 000,00 MT;
 * {@code AGENTS.md} §4). Só existe o metical.
 *
 * @param amount centavos, nunca negativo
 * @param currency sempre {@value #MZN}
 */
public record MoneyAmount(long amount, String currency) {

    public static final String MZN = "MZN";

    public MoneyAmount {
        Objects.requireNonNull(currency, "currency");
        if (!MZN.equals(currency)) {
            throw new IllegalArgumentException("currency must be " + MZN + ": " + currency);
        }
        if (amount < 0) {
            throw new IllegalArgumentException("amount must not be negative: " + amount);
        }
    }

    /** Montante em meticais a partir de centavos. */
    public static MoneyAmount mzn(long centavos) {
        return new MoneyAmount(centavos, MZN);
    }
}
