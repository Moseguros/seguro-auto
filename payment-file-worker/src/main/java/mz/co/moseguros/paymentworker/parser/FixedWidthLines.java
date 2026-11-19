package mz.co.moseguros.paymentworker.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import mz.co.moseguros.paymentcontracts.FileRejectionReason;

/**
 * Leitura linha a linha e campos por posição, comum aos leitores MEPS e BMEPS (S5-08 §2).
 *
 * <ul>
 *   <li>ISO-8859-1 (cada byte é um carácter; nunca falha a descodificar). Bytes de controlo
 *       ({@code 0x00}–{@code 0x1F} excepto o terminador, {@code 0x7F}–{@code 0x9F}) → {@code ENCODING_ERROR};</li>
 *   <li>linhas separadas por {@code \n}, com um {@code \r} final opcional; linhas vazias no fim são ignoradas e
 *       uma linha vazia no meio → {@code PARSE_ERROR};</li>
 *   <li>em <i>streaming</i>: só se guarda a linha actual (e as vazias à espera de saber se são as últimas).</li>
 * </ul>
 *
 * <p>Os comprimentos, os tipos de registo e a ordem são do leitor de cada formato. As mensagens das
 * excepções nunca levam o conteúdo das linhas.
 */
final class FixedWidthLines implements AutoCloseable {

    private final BufferedReader reader;
    private int physicalLineNo;
    private int pendingEmptyLines;
    private int firstPendingEmpty;
    private boolean eof;

    FixedWidthLines(InputStream in) {
        this.reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.ISO_8859_1));
    }

    /** A linha seguinte com conteúdo, ou {@code null} no fim do ficheiro (as linhas vazias finais ignoram-se). */
    Line next() throws PaymentFileParseException {
        while (true) {
            String text = readPhysicalLine();
            if (text == null) {
                return null;
            }
            if (text.isEmpty()) {
                if (pendingEmptyLines++ == 0) {
                    firstPendingEmpty = physicalLineNo;
                }
                continue;
            }
            if (pendingEmptyLines > 0) {
                throw new PaymentFileParseException(FileRejectionReason.PARSE_ERROR, firstPendingEmpty,
                        "Empty line in the middle of the file");
            }
            return new Line(physicalLineNo, text);
        }
    }

    /** Número da última linha com conteúdo lida (0 antes da primeira). */
    int lastLineNo() {
        return physicalLineNo - pendingEmptyLines;
    }

    private String readPhysicalLine() throws PaymentFileParseException {
        if (eof) {
            return null;
        }
        StringBuilder sb = new StringBuilder(128);
        int lineNo = physicalLineNo + 1;
        try {
            int c;
            while ((c = reader.read()) != -1) {
                if (c == '\n') {
                    physicalLineNo = lineNo;
                    return stripCr(sb, lineNo);
                }
                sb.append((char) c);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        eof = true;
        if (sb.length() == 0) {
            return null;
        }
        physicalLineNo = lineNo;
        return stripCr(sb, lineNo);
    }

    private static String stripCr(StringBuilder sb, int lineNo) throws PaymentFileParseException {
        int len = sb.length();
        if (len > 0 && sb.charAt(len - 1) == '\r') {
            sb.setLength(len - 1);
        }
        for (int i = 0; i < sb.length(); i++) {
            char ch = sb.charAt(i);
            if (ch < 0x20 || (ch >= 0x7F && ch <= 0x9F)) {
                throw new PaymentFileParseException(FileRejectionReason.ENCODING_ERROR, lineNo,
                        "Control character at position " + (i + 1));
            }
        }
        return sb.toString();
    }

    @Override
    public void close() {
        try {
            reader.close();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Uma linha sem o terminador. As posições dos campos contam-se de 1, como na especificação.
     *
     * @param lineNo número da linha no ficheiro (1 = primeira)
     * @param raw    a linha como veio (sem o terminador)
     */
    record Line(int lineNo, String raw) {

        /** Tipo de registo: o 1.º carácter. */
        char type() {
            return raw.charAt(0);
        }

        /**
         * A linha com o comprimento do registo: mais curta → completada com espaços à direita; mais comprida →
         * {@code PARSE_ERROR}.
         */
        Line fixed(int length) throws PaymentFileParseException {
            if (raw.length() > length) {
                throw new PaymentFileParseException(FileRejectionReason.PARSE_ERROR, lineNo,
                        "Line longer than " + length + " characters (" + raw.length() + ")");
            }
            if (raw.length() == length) {
                return this;
            }
            return new Line(lineNo, raw + " ".repeat(length - raw.length()));
        }

        /** Texto das posições {@code from}–{@code to} (inclusive, de 1). */
        String text(int from, int to) {
            return raw.substring(from - 1, to);
        }

        /** Só dígitos {@code 0}–{@code 9}, senão {@code PARSE_ERROR}. */
        String digits(int from, int to, String field) throws PaymentFileParseException {
            String s = text(from, to);
            if (!isDigits(s)) {
                throw new PaymentFileParseException(FileRejectionReason.PARSE_ERROR, lineNo,
                        field + " (positions " + from + "-" + to + ") is not numeric");
            }
            return s;
        }

        /** Valor de um campo numérico (montantes em centavos; nunca {@code double}). */
        long number(int from, int to, String field) throws PaymentFileParseException {
            return Long.parseLong(digits(from, to, field));
        }

        /** Data {@code AAAAMMDD}; data impossível → {@code PARSE_ERROR}. */
        LocalDate dateYmd(int from, String field) throws PaymentFileParseException {
            String s = digits(from, from + 7, field);
            return date(Integer.parseInt(s.substring(0, 4)), Integer.parseInt(s.substring(4, 6)),
                    Integer.parseInt(s.substring(6, 8)), field);
        }

        /** Data {@code DDMMAAAA}; data impossível → {@code PARSE_ERROR}. */
        LocalDate dateDmy(int from, String field) throws PaymentFileParseException {
            String s = digits(from, from + 7, field);
            return date(Integer.parseInt(s.substring(4, 8)), Integer.parseInt(s.substring(2, 4)),
                    Integer.parseInt(s.substring(0, 2)), field);
        }

        /** Data e hora; valores impossíveis → {@code PARSE_ERROR}. */
        LocalDateTime dateTime(LocalDate date, int hour, int minute, int second, String field)
                throws PaymentFileParseException {
            try {
                return LocalDateTime.of(date, LocalTime.of(hour, minute, second));
            } catch (DateTimeException e) {
                throw new PaymentFileParseException(FileRejectionReason.PARSE_ERROR, lineNo, field + " is not a valid time");
            }
        }

        private LocalDate date(int year, int month, int day, String field) throws PaymentFileParseException {
            try {
                return LocalDate.of(year, month, day);
            } catch (DateTimeException e) {
                throw new PaymentFileParseException(FileRejectionReason.PARSE_ERROR, lineNo, field + " is not a valid date");
            }
        }

        /** Não escreve o conteúdo (logs). */
        @Override
        public String toString() {
            return "Line[" + lineNo + "]";
        }
    }

    static boolean isDigits(String s) {
        if (s.isEmpty()) {
            return false;
        }
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            if (ch < '0' || ch > '9') {
                return false;
            }
        }
        return true;
    }

    static boolean isZeros(String s) {
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) != '0') {
                return false;
            }
        }
        return true;
    }
}
