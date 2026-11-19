package mz.co.moseguros.paymentworker.parser;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import mz.co.moseguros.paymentcontracts.FileRejectionReason;
import mz.co.moseguros.paymentcontracts.FileSourceDescriptor;
import mz.co.moseguros.paymentworker.parser.FixedWidthLines.Line;
import org.springframework.stereotype.Component;

/**
 * Leitor do ficheiro de movimentos <b>MEPS</b> da InterBancos (S5-08 §3; posições em
 * {@code docs/specs/interbancos/README.md} §2). Todas as linhas com 107 caracteres: cabeçalho {@code 0},
 * detalhes {@code 2}, rodapé {@code 9}. Com as tolerâncias do README §4 (A035 com 12 dígitos ou 10 e dois
 * espaços, A003/A004 quaisquer, moeda {@code 508} ou {@code 943}, A031/A025 e A023 não lidos). Leitor real,
 * em todos os ambientes.
 */
@Component
public class MepsFileParser implements PaymentFileParser {

    public static final String FORMAT = FileSourceDescriptor.FORMAT_MEPS;

    static final int LINE_LENGTH = 107;

    @Override
    public String format() {
        return FORMAT;
    }

    @Override
    public ParsedFile parse(InputStream in, ParseContext ctx) throws PaymentFileParseException {
        try (FixedWidthLines lines = new FixedWidthLines(in)) {
            Line first = lines.next();
            if (first == null || first.type() != '0') {
                throw new PaymentFileParseException(FileRejectionReason.MISSING_HEADER, 1, "The first line is not a header");
            }
            FileHeader header = header(first.fixed(LINE_LENGTH));
            List<ParsedRecord> records = new ArrayList<>();
            FileTrailer trailer = null;
            Line line;
            while ((line = lines.next()) != null) {
                if (trailer != null) {
                    throw new PaymentFileParseException(FileRejectionReason.PARSE_ERROR, line.lineNo(),
                            "Record after the trailer");
                }
                switch (line.type()) {
                    case '2' -> records.add(detail(line.fixed(LINE_LENGTH), header.entity(), ctx));
                    case '9' -> trailer = trailer(line.fixed(LINE_LENGTH));
                    case '0' -> throw new PaymentFileParseException(FileRejectionReason.PARSE_ERROR, line.lineNo(),
                            "Repeated header");
                    default -> throw new PaymentFileParseException(FileRejectionReason.UNSUPPORTED_RECORD,
                            line.lineNo(), "Unsupported record type");
                }
            }
            if (trailer == null) {
                throw new PaymentFileParseException(FileRejectionReason.MISSING_TRAILER, lines.lastLineNo(),
                        "The last line is not a trailer");
            }
            return new ParsedFile(header, records, trailer);
        }
    }

    private static FileHeader header(Line l) throws PaymentFileParseException {
        if (!"MEPS".equals(l.text(2, 5))) {
            throw new PaymentFileParseException(FileRejectionReason.PARSE_ERROR, l.lineNo(), "A002 is not MEPS");
        }
        l.digits(6, 13, "A003");
        l.digits(14, 21, "A004");
        String a005 = l.digits(22, 30, "A005");
        LocalDate fileDate = l.dateYmd(22, "A005");
        String a007 = l.digits(31, 39, "A007");
        String entity = l.digits(40, 44, "A033");
        String currency = l.text(45, 47);
        if (!"508".equals(currency) && !"943".equals(currency)) {
            throw new PaymentFileParseException(FileRejectionReason.PARSE_ERROR, l.lineNo(),
                    "A006 currency is neither 508 nor 943");
        }
        return new FileHeader(entity, a005, FixedWidthLines.isZeros(a007) ? null : a007, null, null, null, fileDate,
                l.raw());
    }

    private static ParsedRecord detail(Line l, String entity, ParseContext ctx) throws PaymentFileParseException {
        if (!"04".equals(l.text(2, 3))) {
            throw new PaymentFileParseException(FileRejectionReason.UNSUPPORTED_RECORD, l.lineNo(),
                    "A009 processing code is not 04");
        }
        String a008 = l.digits(4, 7, "A008");
        String a014 = l.digits(8, 15, "A014");
        String bankTransactionId = FixedWidthLines.isZeros(a008 + a014) ? null : a008 + "-" + a014;
        LocalDateTime paidAt = a035(l);
        long amount = l.number(28, 40, "A011");
        long fee = l.number(41, 49, "A032");
        String terminalType = l.text(50, 51).trim();
        String reference = l.digits(82, 92, "A034");
        return new ParsedRecord(l.lineNo(), entity, reference, amount, fee, paidAt.atZone(ctx.zone()).toInstant(),
                terminalType.isEmpty() ? null : terminalType, bankTransactionId, l.raw());
    }

    /** A035: {@code AAAAMMDDHHMM} (exemplo) ou {@code AAAAMMDDHH} e dois espaços (especificação, minutos 00). */
    private static LocalDateTime a035(Line l) throws PaymentFileParseException {
        String minutes = l.text(26, 27);
        String dateHour = l.digits(16, 25, "A035");
        int minute;
        if ("  ".equals(minutes)) {
            minute = 0;
        } else {
            minute = Integer.parseInt(l.digits(26, 27, "A035"));
        }
        LocalDate date = l.dateYmd(16, "A035");
        return l.dateTime(date, Integer.parseInt(dateHour.substring(8, 10)), minute, 0, "A035");
    }

    private static FileTrailer trailer(Line l) throws PaymentFileParseException {
        int count = Integer.parseInt(l.digits(2, 9, "A024"));
        long total = l.number(10, 26, "A026");
        long fees = l.number(27, 38, "A027");
        return new FileTrailer(count, total, fees);
    }
}
