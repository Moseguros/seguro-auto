package mz.co.moseguros.payment.application.reference;

import java.util.regex.Pattern;

/**
 * Dígitos de controlo das referências Ponto24, <b>exactamente</b> como em
 * {@code docs/specs/interbancos/README.md} §1.1 (S5-07 §2). Sem estado.
 *
 * <ol>
 *   <li>{@code S = entidade(5) + factura(7) + mês(2) + montante} — o montante em centavos <b>sem zeros à
 *       esquerda</b> ({@link Long#toString(long)}), nunca de um campo de largura fixa: com {@code "0200000"} o
 *       exemplo oficial daria {@code 21} em vez de {@code 02};</li>
 *   <li>{@code P = 0}; para cada dígito {@code d}: {@code P = ((d + P) × 10) mod 97};</li>
 *   <li>{@code CD = 98 − ((P × 10) mod 97)}, com 2 dígitos ({@code 01}–{@code 98}).</li>
 * </ol>
 *
 * <p>Referência (11 dígitos) = factura(7) + mês(2) + CD(2). A entidade e o montante não aparecem na
 * referência mas entram no {@code CD}.
 */
public final class Ponto24CheckDigits {

    /** Maior número de factura (7 dígitos). */
    public static final int MAX_INVOICE = 9_999_999;

    private static final Pattern ENTITY = Pattern.compile("^[0-9]{5}$");
    private static final Pattern REFERENCE = Pattern.compile("^[0-9]{11}$");

    private Ponto24CheckDigits() {
    }

    /**
     * {@code CD} (2 dígitos) da entidade, factura, mês e montante.
     *
     * @throws IllegalArgumentException entidade fora de {@code ^[0-9]{5}$}, factura fora de 1–9 999 999, mês fora
     *                                  de 1–12 ou montante ≤ 0 (erro de programação)
     */
    public static String compute(String entity, int invoiceNumber, int month, long amountCentavos) {
        requireValid(entity, invoiceNumber, month, amountCentavos);
        return checkDigitsOf(entity + invoice7(invoiceNumber) + month2(month) + Long.toString(amountCentavos));
    }

    /** Referência de 11 dígitos: {@code factura(7) + mês(2) + CD(2)}. Mesmas validações de {@link #compute}. */
    public static String reference(String entity, int invoiceNumber, int month, long amountCentavos) {
        return invoice7(invoiceNumber) + month2(month) + compute(entity, invoiceNumber, month, amountCentavos);
    }

    /**
     * {@code true} se a referência tem 11 dígitos, a factura (posições 1–7) está entre 1 e 9 999 999, o mês
     * (8–9) entre {@code 01} e {@code 12} e o {@code CD} (10–11) é o da entidade e do montante. Entradas
     * inválidas (entidade sem 5 dígitos, montante ≤ 0, {@code null}) dão {@code false}, nunca uma excepção.
     */
    public static boolean isValid(String entity, String reference, long amountCentavos) {
        if (entity == null || !ENTITY.matcher(entity).matches() || reference == null
                || !REFERENCE.matcher(reference).matches() || amountCentavos <= 0) {
            return false;
        }
        int invoice = Integer.parseInt(reference.substring(0, 7));
        int month = Integer.parseInt(reference.substring(7, 9));
        if (invoice < 1 || month < 1 || month > 12) {
            return false;
        }
        return reference.substring(9).equals(compute(entity, invoice, month, amountCentavos));
    }

    /**
     * Passos 2 a 4 do algoritmo sobre a cadeia {@code S} já montada (só dígitos). Público para os testes
     * mostrarem o efeito de um montante com zeros à esquerda; o código de produção usa {@link #compute}.
     */
    public static String checkDigitsOf(String digits) {
        if (digits == null || digits.isEmpty()) {
            throw new IllegalArgumentException("No digits");
        }
        int p = 0;
        for (int i = 0; i < digits.length(); i++) {
            char ch = digits.charAt(i);
            if (ch < '0' || ch > '9') {
                throw new IllegalArgumentException("Not a digit at position " + i);
            }
            p = ((ch - '0' + p) * 10) % 97;
        }
        int cd = 98 - (p * 10) % 97;
        return cd < 10 ? "0" + cd : Integer.toString(cd);
    }

    private static void requireValid(String entity, int invoiceNumber, int month, long amountCentavos) {
        if (entity == null || !ENTITY.matcher(entity).matches()) {
            throw new IllegalArgumentException("The entity must have 5 digits");
        }
        if (invoiceNumber < 1 || invoiceNumber > MAX_INVOICE) {
            throw new IllegalArgumentException("Invoice number out of range: " + invoiceNumber);
        }
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("Month out of range: " + month);
        }
        if (amountCentavos <= 0) {
            throw new IllegalArgumentException("The amount must be positive");
        }
    }

    private static String invoice7(int invoiceNumber) {
        String s = Integer.toString(invoiceNumber);
        return "0".repeat(Math.max(0, 7 - s.length())) + s;
    }

    private static String month2(int month) {
        return month < 10 ? "0" + month : Integer.toString(month);
    }
}
