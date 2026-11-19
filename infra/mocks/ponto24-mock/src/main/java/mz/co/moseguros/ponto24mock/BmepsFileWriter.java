package mz.co.moseguros.ponto24mock;

import static mz.co.moseguros.ponto24mock.MepsFileWriter.num;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import mz.co.moseguros.ponto24mock.MockProperties.FileStyle;

/**
 * Escreve ficheiros BMEPS com as posições de {@code docs/specs/interbancos/README.md} §3: cabeçalho com 19
 * caracteres, detalhes com 91 (com os espaços finais), rodapé com 40, terminados em {@code \r\n}. O estilo
 * {@code SPEC} só difere do {@code EXAMPLE} no "ID da transacção" (contador do banco em vez de zeros).
 */
public final class BmepsFileWriter {

    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("ddMMyyyy");
    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HHmmss");

    private BmepsFileWriter() {
    }

    /**
     * @param fileDate data de criação do ficheiro (cabeçalho)
     * @param sequence ID do ficheiro (1, 2, …)
     */
    public static List<String> lines(String companyId, String profileId, LocalDate fileDate, int sequence,
                                     List<FilePayment> payments, FileStyle style, ZoneId zone) {
        List<String> lines = new ArrayList<>();
        lines.add("0" + companyId + profileId + DATE.format(fileDate) + num(sequence, 5));
        long total = 0;
        long fees = 0;
        for (FilePayment p : payments) {
            var local = p.paidAt().atZone(zone);
            lines.add("1" + p.reference()
                    + num(p.amountCentavos(), 16)
                    + num(p.feeCentavos(), 16)
                    + DATE.format(local)
                    + TIME.format(local)
                    + (style == FileStyle.SPEC ? num(p.bankLog(), 7) : "0000000")
                    + " ".repeat(10)    // ID do distribuidor/terminal
                    + " ".repeat(16));  // localização
            total += p.amountCentavos();
            fees += p.feeCentavos();
        }
        lines.add("9" + num(payments.size(), 7) + num(total, 16) + num(fees, 16));
        return lines;
    }

    public static byte[] write(String companyId, String profileId, LocalDate fileDate, int sequence,
                               List<FilePayment> payments, FileStyle style, ZoneId zone) {
        return MepsFileWriter.toBytes(lines(companyId, profileId, fileDate, sequence, payments, style, zone));
    }
}
