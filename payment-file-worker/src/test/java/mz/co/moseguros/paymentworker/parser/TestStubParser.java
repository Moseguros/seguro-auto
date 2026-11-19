package mz.co.moseguros.paymentworker.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import mz.co.moseguros.paymentcontracts.FileRejectionReason;

/**
 * Leitor só para os testes do S5-09, até aos leitores reais MEPS e BMEPS (S5-08, que o apaga). Formato
 * {@value #FORMAT}, separado por {@code ;}:
 *
 * <pre>
 * H;&lt;entidade&gt;;&lt;id do ficheiro&gt;;&lt;id do anterior ou vazio&gt;
 * D;&lt;referência&gt;;&lt;centavos&gt;;&lt;yyyy-MM-dd'T'HH:mm:ss&gt;
 * T;&lt;n&gt;;&lt;total&gt;
 * </pre>
 */
public class TestStubParser implements PaymentFileParser {

    public static final String FORMAT = "TEST_STUB";

    @Override
    public String format() {
        return FORMAT;
    }

    @Override
    public ParsedFile parse(InputStream in, ParseContext ctx) throws PaymentFileParseException {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.ISO_8859_1))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        while (!lines.isEmpty() && lines.get(lines.size() - 1).isBlank()) {
            lines.remove(lines.size() - 1);
        }
        if (lines.isEmpty() || !lines.get(0).startsWith("H;")) {
            throw new PaymentFileParseException(FileRejectionReason.MISSING_HEADER, 1, "Missing header");
        }
        if (!lines.get(lines.size() - 1).startsWith("T;")) {
            throw new PaymentFileParseException(FileRejectionReason.MISSING_TRAILER, lines.size(), "Missing trailer");
        }
        String[] h = lines.get(0).split(";", -1);
        if (h.length != 4) {
            throw new PaymentFileParseException(FileRejectionReason.PARSE_ERROR, 1, "Invalid header");
        }
        // id numérico → também sequência (como o BMEPS), para os testes da sequência ("3;1" depois de "1")
        Integer sequence = h[2].matches("[0-9]{1,9}") ? Integer.valueOf(h[2]) : null;
        FileHeader header = new FileHeader(h[1], h[2], h[3].isEmpty() ? null : h[3], null, null, sequence, null,
                lines.get(0));
        List<ParsedRecord> records = new ArrayList<>();
        for (int i = 1; i < lines.size() - 1; i++) {
            int lineNo = i + 1;
            String[] d = lines.get(i).split(";", -1);
            if (d.length != 4 || !"D".equals(d[0])) {
                throw new PaymentFileParseException(FileRejectionReason.UNSUPPORTED_RECORD, lineNo,
                        "Unsupported record");
            }
            try {
                records.add(new ParsedRecord(lineNo, h[1], d[1], Long.parseLong(d[2]), null,
                        LocalDateTime.parse(d[3]).atZone(ctx.zone()).toInstant(), null, null, lines.get(i)));
            } catch (NumberFormatException | DateTimeParseException e) {
                throw new PaymentFileParseException(FileRejectionReason.PARSE_ERROR, lineNo, "Invalid detail");
            }
        }
        String[] t = lines.get(lines.size() - 1).split(";", -1);
        try {
            return new ParsedFile(header, records, new FileTrailer(Integer.parseInt(t[1]), Long.parseLong(t[2]), null));
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            throw new PaymentFileParseException(FileRejectionReason.PARSE_ERROR, lines.size(), "Invalid trailer");
        }
    }
}
