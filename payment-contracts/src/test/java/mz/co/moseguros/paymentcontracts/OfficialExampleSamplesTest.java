package mz.co.moseguros.paymentcontracts;

import static mz.co.moseguros.paymentcontracts.ContractSupport.MAPPER;
import static mz.co.moseguros.paymentcontracts.ContractSupport.sample;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * As amostras de {@link ReferencePaymentReceived} têm os dados da linha 2 dos exemplos oficiais
 * da InterBancos ({@code docs/specs/interbancos/}, posições do README), com as horas de Maputo em
 * UTC.
 */
class OfficialExampleSamplesTest {

    private static final Path SPECS = Path.of("..", "docs", "specs", "interbancos");
    private static final ZoneId MAPUTO = ZoneId.of("Africa/Maputo");

    @Test
    void mepsSampleMatchesLine2OfTheOfficialExample() throws Exception {
        byte[] bytes = Files.readAllBytes(SPECS.resolve("exemplo-ficheiro-meps.txt"));
        List<String> lines = lines(bytes);
        String header = lines.get(0);
        String line2 = lines.get(1);

        ReferencePaymentReceived sample =
                MAPPER.treeToValue(sample("reference-payment-received-meps.json"), ReferencePaymentReceived.class);

        assertEquals(MessageIds.sha256Hex(bytes), sample.fileSha256());
        assertEquals("MEPS", sample.fileFormat());
        assertEquals(field(header, 22, 30), sample.formatFileId());          // A005
        assertEquals(field(header, 40, 44), sample.entity());                 // A033
        assertEquals(field(line2, 82, 92), sample.reference());               // A034
        assertEquals(MoneyAmount.mzn(Long.parseLong(field(line2, 28, 40))), sample.amount()); // A011
        assertEquals(MoneyAmount.mzn(Long.parseLong(field(line2, 41, 49))), sample.fee());    // A032
        assertEquals(MoneyAmount.mzn(1000), sample.fee());                    // 10,00
        LocalDateTime local = LocalDateTime.parse(field(line2, 16, 27), DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
        assertEquals(local.atZone(MAPUTO).toInstant(), sample.paidAt());      // A035
        assertEquals("  ", field(line2, 50, 51));                             // A015 vazio
        assertNull(sample.channel());
        assertNull(sample.bankTransactionId());                               // identificadores a zeros
        assertEquals(2, sample.lineNo());
    }

    @Test
    void bmepsSampleMatchesLine2OfTheOfficialExample() throws Exception {
        byte[] bytes = Files.readAllBytes(SPECS.resolve("exemplo-ficheiro-bmeps.txt"));
        List<String> lines = lines(bytes);
        String header = lines.get(0);
        String line2 = lines.get(1);

        ReferencePaymentReceived sample =
                MAPPER.treeToValue(sample("reference-payment-received-bmeps.json"), ReferencePaymentReceived.class);

        assertEquals(MessageIds.sha256Hex(bytes), sample.fileSha256());
        assertEquals("BMEPS", sample.fileFormat());
        assertEquals(field(header, 2, 4) + "-" + field(header, 5, 6) + "-" + field(header, 15, 19),
                sample.formatFileId());
        assertEquals("90090", sample.entity());                               // da configuração (S5-01)
        assertEquals(field(line2, 2, 12), sample.reference());
        assertEquals(MoneyAmount.mzn(Long.parseLong(field(line2, 13, 28))), sample.amount());
        assertEquals(MoneyAmount.mzn(Long.parseLong(field(line2, 29, 44))), sample.fee());
        assertEquals(MoneyAmount.mzn(500), sample.fee());                     // 5,00
        LocalDateTime local = LocalDateTime.parse(field(line2, 45, 58), DateTimeFormatter.ofPattern("ddMMyyyyHHmmss"));
        assertEquals(local.atZone(MAPUTO).toInstant(), sample.paidAt());
        assertNull(sample.channel());
        assertNull(sample.bankTransactionId());
        assertEquals(2, sample.lineNo());
    }

    private static List<String> lines(byte[] bytes) {
        return new String(bytes, StandardCharsets.ISO_8859_1).lines().toList();
    }

    /** Posições do README (1-based, inclusivas). */
    private static String field(String line, int from, int to) {
        return line.substring(from - 1, to);
    }
}
