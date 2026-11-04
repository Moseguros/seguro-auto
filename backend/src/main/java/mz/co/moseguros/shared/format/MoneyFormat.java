package mz.co.moseguros.shared.format;

import java.util.Objects;

import mz.co.moseguros.shared.domain.Money;

/**
 * Valores em meticais para textos enviados ou impressos pelo backend (PDF, SMS de assinatura), iguais
 * ao {@code formatMoney} da web (S3-03 §9):
 * <ul>
 *   <li>{@code pt}: {@code 12 345,67 MT}, com U+00A0 nos milhares, vírgula decimal e 2 casas;</li>
 *   <li>{@code en}: {@code MZN 12,345.67}.</li>
 * </ul>
 * Agrupa sempre os milhares (também com 4 algarismos: {@code 4 663,00 MT}). Não depende dos dados de
 * localização do JDK, para o texto ser igual em qualquer máquina.
 */
public final class MoneyFormat {

    /** Espaço não separável (U+00A0), separador de milhares em pt. */
    public static final char NBSP = ' ';

    private MoneyFormat() {
    }

    /**
     * @param language {@code en} → formato inglês; outro valor (incluindo {@code null}) → pt
     */
    public static String format(Money money, String language) {
        Objects.requireNonNull(money, "money");
        boolean english = "en".equalsIgnoreCase(language);
        String number = number(money.amount(), english ? ',' : NBSP, english ? '.' : ',');
        return english ? "MZN " + number : number + " MT";
    }

    /** Só o número ({@code 12 345,67} ou {@code 12,345.67}), com sinal se negativo. */
    static String number(long centavos, char groupSeparator, char decimalSeparator) {
        boolean negative = centavos < 0;
        String digits = Long.toString(Math.abs(centavos));
        if (digits.length() < 3) {
            digits = "0".repeat(3 - digits.length()) + digits;
        }
        String units = digits.substring(0, digits.length() - 2);
        String cents = digits.substring(digits.length() - 2);
        StringBuilder grouped = new StringBuilder();
        for (int i = 0; i < units.length(); i++) {
            if (i > 0 && (units.length() - i) % 3 == 0) {
                grouped.append(groupSeparator);
            }
            grouped.append(units.charAt(i));
        }
        return (negative ? "-" : "") + grouped + decimalSeparator + cents;
    }
}
