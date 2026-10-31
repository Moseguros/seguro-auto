package mz.co.moseguros.shared.logging;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Mascara dados pessoais e credenciais em texto de log.
 *
 * <ul>
 *   <li>Telemóveis moçambicanos {@code 258XXXXXXXXX} → {@code 258*****1234} (últimos 4 visíveis).</li>
 *   <li>NUIT a seguir à palavra {@code nuit} → {@code *****6789}.</li>
 *   <li>Tokens {@code Bearer ...} → {@code Bearer ***}.</li>
 * </ul>
 */
public final class SensitiveDataMasker {

    private static final Pattern MSISDN = Pattern.compile("(?<!\\d)(\\+?258)(\\d{5})(\\d{4})(?!\\d)");
    private static final Pattern NUIT = Pattern.compile("(?i)(nuit\\W{0,3})(\\d{5})(\\d{4})(?!\\d)");
    private static final Pattern BEARER = Pattern.compile("(?i)(bearer\\s+)[A-Za-z0-9\\-._~+/]+=*");

    private SensitiveDataMasker() {
    }

    public static String mask(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        String out = input;
        out = replace(MSISDN, out, m -> m.group(1) + "*****" + m.group(3));
        out = replace(NUIT, out, m -> m.group(1) + "*****" + m.group(3));
        out = replace(BEARER, out, m -> m.group(1) + "***");
        return out;
    }

    private static String replace(Pattern pattern, String input, java.util.function.Function<Matcher, String> fn) {
        Matcher m = pattern.matcher(input);
        if (!m.find()) {
            return input;
        }
        StringBuilder sb = new StringBuilder();
        do {
            m.appendReplacement(sb, Matcher.quoteReplacement(fn.apply(m)));
        } while (m.find());
        m.appendTail(sb);
        return sb.toString();
    }
}
