package mz.co.moseguros.ponto24mock;

import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import mz.co.moseguros.ponto24mock.MockProperties.FileStyle;

/**
 * Escreve ficheiros MEPS com as posições de {@code docs/specs/interbancos/README.md} §2: todas as linhas com
 * 107 caracteres, terminadas em {@code \r\n}.
 *
 * <ul>
 *   <li>{@code EXAMPLE} reproduz o exemplo oficial: A003/A004 {@code 00000008}/{@code 000<entidade>}, A006
 *       {@code 943}, A031 {@code XX}, A035 com 12 dígitos, A008/A014 a zeros e A023 desalinhado
 *       ({@code 00000000000 });</li>
 *   <li>{@code SPEC} segue a letra da especificação: A003/A004 {@code 50000000}/{@code 00000100}, A006
 *       {@code 508}, A031 {@code 00}, A035 com 10 dígitos e 2 espaços, A008 {@code 0001} e A014 com o contador
 *       do banco, A023 com 12 zeros.</li>
 * </ul>
 */
public final class MepsFileWriter {

    public static final int LINE_LENGTH = 107;

    private static final DateTimeFormatter A035_EXAMPLE = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
    private static final DateTimeFormatter A035_SPEC = DateTimeFormatter.ofPattern("yyyyMMddHH");

    private MepsFileWriter() {
    }

    /**
     * @param entity   A033 (5 dígitos)
     * @param a005     identificador deste ficheiro ({@code AAAAMMDDS})
     * @param a007     identificador do ficheiro anterior, ou {@code null} no primeiro (zeros)
     * @param payments detalhes, pela ordem em que ficam no ficheiro
     */
    public static List<String> lines(String entity, String a005, String a007, List<FilePayment> payments,
                                     FileStyle style, ZoneId zone) {
        boolean spec = style == FileStyle.SPEC;
        List<String> lines = new ArrayList<>();
        lines.add(pad("0" + "MEPS"
                + (spec ? "50000000" : "00000008")
                + (spec ? "00000100" : "000" + entity)
                + a005
                + (a007 == null ? "000000000" : a007)
                + entity
                + (spec ? "508" : "943")
                + (spec ? "00" : "XX")
                + "0000"));
        long total = 0;
        long fees = 0;
        for (FilePayment p : payments) {
            String a035 = spec
                    ? A035_SPEC.format(p.paidAt().atZone(zone)) + "  "
                    : A035_EXAMPLE.format(p.paidAt().atZone(zone));
            lines.add(pad("2" + "04"
                    + (spec ? "0001" : "0000")
                    + (spec ? num(p.bankLog(), 8) : "00000000")
                    + a035
                    + num(p.amountCentavos(), 13)
                    + num(p.feeCentavos(), 9)
                    + (p.channel() == null ? "  " : p.channel())
                    + "0000000000"        // A016
                    + "00000"             // A017
                    + " ".repeat(15)      // A030
                    + p.reference()       // A034
                    + "0"                 // A010
                    + "0"                 // A012
                    + " ".repeat(12)));   // A013 (+ filler)
            total += p.amountCentavos();
            fees += p.feeCentavos();
        }
        lines.add(pad("9" + num(payments.size(), 8) + num(total, 17) + num(fees, 12)
                + (spec ? "000000000000" : "00000000000 ")));
        return lines;
    }

    public static byte[] write(String entity, String a005, String a007, List<FilePayment> payments, FileStyle style,
                               ZoneId zone) {
        return toBytes(lines(entity, a005, a007, payments, style, zone));
    }

    static byte[] toBytes(List<String> lines) {
        StringBuilder sb = new StringBuilder();
        for (String line : lines) {
            sb.append(line).append("\r\n");
        }
        return sb.toString().getBytes(StandardCharsets.ISO_8859_1);
    }

    static String num(long value, int width) {
        String s = Long.toString(value);
        if (value < 0 || s.length() > width) {
            throw new IllegalArgumentException("Value does not fit in " + width + " digits");
        }
        return "0".repeat(width - s.length()) + s;
    }

    private static String pad(String s) {
        if (s.length() > LINE_LENGTH) {
            throw new IllegalStateException("MEPS line longer than " + LINE_LENGTH);
        }
        return s + " ".repeat(LINE_LENGTH - s.length());
    }
}
