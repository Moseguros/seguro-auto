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
 * Leitor do ficheiro de movimentos <b>BMEPS</b> da InterBancos (S5-08 §4; posições em
 * {@code docs/specs/interbancos/README.md} §3): cabeçalho {@code 0} com 19 caracteres, detalhes {@code 1}
 * com 91, rodapé {@code 9} com 40. Um ficheiro por perfil da empresa; a entidade não vem no ficheiro e é a
 * da fonte ({@code ctx.sourceEntity}). O nome do ficheiro não é verificado (ADR-007). Leitor real, em todos
 * os ambientes.
 */
@Component
public class BmepsFileParser implements PaymentFileParser {

    public static final String FORMAT = FileSourceDescriptor.FORMAT_BMEPS;

    static final int HEADER_LENGTH = 19;
    static final int DETAIL_LENGTH = 91;
    static final int TRAILER_LENGTH = 40;

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
            FileHeader header = header(first.fixed(HEADER_LENGTH), ctx);
            List<ParsedRecord> records = new ArrayList<>();
            FileTrailer trailer = null;
            Line line;
            while ((line = lines.next()) != null) {
                if (trailer != null) {
                    throw new PaymentFileParseException(FileRejectionReason.PARSE_ERROR, line.lineNo(),
                            "Record after the trailer");
                }
                switch (line.type()) {
                    case '1' -> records.add(detail(line.fixed(DETAIL_LENGTH), ctx));
                    case '9' -> trailer = trailer(line.fixed(TRAILER_LENGTH));
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

    private static FileHeader header(Line l, ParseContext ctx) throws PaymentFileParseException {
        String company = l.digits(2, 4, "Company id");
        String profile = l.digits(5, 6, "Profile id");
        LocalDate fileDate = l.dateDmy(7, "File creation date");
        String fileId = l.digits(15, 19, "File id");
        return new FileHeader(ctx.sourceEntity(), company + "-" + profile + "-" + fileId, null, company, profile,
                Integer.valueOf(fileId), fileDate, l.raw());
    }

    private static ParsedRecord detail(Line l, ParseContext ctx) throws PaymentFileParseException {
        String reference = l.digits(2, 12, "Reference");
        long amount = l.number(13, 28, "Amount");
        long fee = l.number(29, 44, "Commission");
        LocalDate date = l.dateDmy(45, "Date");
        String time = l.digits(53, 58, "Time");
        LocalDateTime paidAt = l.dateTime(date, Integer.parseInt(time.substring(0, 2)),
                Integer.parseInt(time.substring(2, 4)), Integer.parseInt(time.substring(4, 6)), "Time");
        String transactionId = l.digits(59, 65, "Transaction id");
        return new ParsedRecord(l.lineNo(), ctx.sourceEntity(), reference, amount, fee,
                paidAt.atZone(ctx.zone()).toInstant(), null,
                FixedWidthLines.isZeros(transactionId) ? null : transactionId, l.raw());
    }

    private static FileTrailer trailer(Line l) throws PaymentFileParseException {
        int count = Integer.parseInt(l.digits(2, 8, "Record count"));
        long total = l.number(9, 24, "Total amount");
        long fees = l.number(25, 40, "Total commissions");
        return new FileTrailer(count, total, fees);
    }
}
