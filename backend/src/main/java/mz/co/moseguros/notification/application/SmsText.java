package mz.co.moseguros.notification.application;

import java.text.Normalizer;
import java.util.regex.Pattern;

/**
 * Normalização e tamanho dos SMS das notificações (S6-04 §5):
 * <ol>
 *   <li>caracteres tipográficos → ASCII ({@code “ ” „} → {@code "}, {@code ‘ ’} → {@code '}, {@code – —} →
 *       {@code -}, {@code …} → {@code ...}, espaço não separável → espaço, {@code º} → {@code o}, {@code ª} →
 *       {@code a});</li>
 *   <li>com {@code asciiFold}, decomposição NFD e remoção das marcas ({@code Apólice} → {@code Apolice});</li>
 *   <li>limite 160 se todos os caracteres forem GSM-7 (os de extensão {@code ^ { } \ [ ~ ] | €} contam 2),
 *       senão 70.</li>
 * </ol>
 */
public final class SmsText {

    public static final int GSM_LIMIT = 160;
    public static final int UNICODE_LIMIT = 70;
    static final String ELLIPSIS = "...";

    /** Alfabeto básico GSM 03.38. */
    private static final String GSM_BASIC = "@£$¥èéùìòÇ\nØø\rÅåΔ_ΦΓΛΩΠΨΣΘΞÆæßÉ !\"#¤%&'()*+,-./0123456789:;<=>?"
            + "¡ABCDEFGHIJKLMNOPQRSTUVWXYZÄÖÑÜ§¿abcdefghijklmnopqrstuvwxyzäöñüà";
    /** Extensão GSM 03.38 (cada um conta 2). */
    private static final String GSM_EXTENSION = "^{}\\[~]|€\f";
    private static final Pattern MARKS = Pattern.compile("\\p{M}+");

    private SmsText() {
    }

    /** Passos 1 e 2. */
    public static String normalize(String text, boolean asciiFold) {
        StringBuilder sb = new StringBuilder(text.length());
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            switch (c) {
                case '“', '”', '„' -> sb.append('"');
                case '‘', '’' -> sb.append('\'');
                case '–', '—' -> sb.append('-');
                case '…' -> sb.append(ELLIPSIS);
                case ' ', ' ' -> sb.append(' ');
                case 'º' -> sb.append('o');
                case 'ª' -> sb.append('a');
                default -> sb.append(c);
            }
        }
        String out = sb.toString();
        if (asciiFold) {
            out = MARKS.matcher(Normalizer.normalize(out, Normalizer.Form.NFD)).replaceAll("");
        }
        return out;
    }

    /** {@code true} se todos os caracteres forem do alfabeto GSM-7 (básico ou extensão). */
    public static boolean isGsm7(String text) {
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (GSM_BASIC.indexOf(c) < 0 && GSM_EXTENSION.indexOf(c) < 0) {
                return false;
            }
        }
        return true;
    }

    /** Tamanho contado como na rede: em GSM-7 os caracteres de extensão contam 2; senão, um por carácter. */
    public static int length(String text) {
        if (!isGsm7(text)) {
            return text.length();
        }
        int length = 0;
        for (int i = 0; i < text.length(); i++) {
            length += GSM_EXTENSION.indexOf(text.charAt(i)) >= 0 ? 2 : 1;
        }
        return length;
    }

    public static int limit(String text) {
        return isGsm7(text) ? GSM_LIMIT : UNICODE_LIMIT;
    }

    /** Normaliza e mede; {@code fits = length <= limit}. */
    public static Measured measure(String text, boolean asciiFold) {
        String normalized = normalize(text, asciiFold);
        int limit = limit(normalized);
        int length = length(normalized);
        return new Measured(normalized, length, limit, length <= limit);
    }

    /**
     * Normaliza e, se passar do limite, corta para {@code limite − 3} + {@code ...} (passo 4).
     */
    public static Measured fit(String text, boolean asciiFold) {
        Measured measured = measure(text, asciiFold);
        if (measured.fits()) {
            return measured;
        }
        String normalized = measured.text();
        int max = measured.limit() - ELLIPSIS.length();
        StringBuilder sb = new StringBuilder();
        int used = 0;
        boolean gsm = isGsm7(normalized);
        for (int i = 0; i < normalized.length(); i++) {
            char c = normalized.charAt(i);
            int cost = gsm && GSM_EXTENSION.indexOf(c) >= 0 ? 2 : 1;
            if (used + cost > max) {
                break;
            }
            sb.append(c);
            used += cost;
        }
        String cut = sb + ELLIPSIS;
        return new Measured(cut, length(cut), measured.limit(), false);
    }

    /**
     * Texto medido.
     *
     * @param fits {@code false} se passava do limite (no {@link #fit} o texto já vem cortado)
     */
    public record Measured(String text, int length, int limit, boolean fits) {
    }
}
