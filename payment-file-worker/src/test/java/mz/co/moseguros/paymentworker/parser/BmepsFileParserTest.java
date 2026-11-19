package mz.co.moseguros.paymentworker.parser;

import static mz.co.moseguros.paymentworker.parser.InterbancosTestFiles.actual;
import static mz.co.moseguros.paymentworker.parser.InterbancosTestFiles.bmepsContext;
import static mz.co.moseguros.paymentworker.parser.InterbancosTestFiles.expected;
import static mz.co.moseguros.paymentworker.parser.InterbancosTestFiles.resource;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import mz.co.moseguros.paymentcontracts.FileRejectionReason;
import mz.co.moseguros.paymentworker.parser.InterbancosTestFiles.Golden;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

/** Leitor BMEPS (S5-08 §4, §5 e §7). */
@ExtendWith(OutputCaptureExtension.class)
class BmepsFileParserTest {

    final BmepsFileParser parser = new BmepsFileParser();

    static Stream<String> files() {
        return Stream.of("exemplo-ficheiro-bmeps.txt", "bmeps-ok-empty.txt", "bmeps-err-company.txt",
                "bmeps-err-trailer-total.txt", "bmeps-err-date.txt", "bmeps-err-control-char.txt",
                "bmeps-warn-fees.txt");
    }

    @ParameterizedTest
    @MethodSource("files")
    void eachFileGivesItsExpectedResult(String name) {
        assertThat(actual(parser, resource(name), bmepsContext(name))).containsExactlyElementsOf(expected(name));
    }

    @Test
    void officialExampleHeaderAndTrailer() throws Exception {
        ParsedFile file = parse(resource("exemplo-ficheiro-bmeps.txt"));

        FileHeader h = file.header();
        assertThat(h.entity()).isEqualTo("90090");
        assertThat(h.formatFileId()).isEqualTo("800-02-00001");
        assertThat(h.previousFormatFileId()).isNull();
        assertThat(h.bmepsCompanyId()).isEqualTo("800");
        assertThat(h.bmepsProfileId()).isEqualTo("02");
        assertThat(h.bmepsFileSequence()).isEqualTo(1);
        assertThat(h.fileDate()).isEqualTo(LocalDate.of(2012, 11, 22));
        assertThat(h.rawHeader()).isEqualTo("0800022211201200001");
        assertThat(file.records()).hasSize(14).allMatch(r -> r.entity().equals("90090") && r.channel() == null
                && r.bankTransactionId() == null);
        assertThat(file.records().stream().mapToLong(ParsedRecord::amountCentavos).sum()).isEqualTo(8_074_000);
        assertThat(file.trailer()).isEqualTo(new FileTrailer(14, 8_074_000, 7_500L));
        ParsedRecord tenMeticais = file.records().stream().filter(r -> r.reference().equals("02295721110"))
                .findFirst().orElseThrow();
        assertThat(tenMeticais.paidAt()).isEqualTo(Instant.parse("2012-11-22T12:30:44Z")); // 14:30:44 em Maputo
        assertThat(tenMeticais.feeCentavos()).isEqualTo(1000);
        assertThat(tenMeticais.rawLine()).hasSize(91);
    }

    @Test
    void feesMismatchOnlyWarns(CapturedOutput output) {
        List<String> rows = actual(parser, resource("bmeps-warn-fees.txt"), bmepsContext("bmeps-warn-fees.txt"));
        assertThat(rows).hasSize(14);
        assertThat(output).contains("alert=PAYMENT_FILE_FEES_MISMATCH");
    }

    @Test
    void transactionIdIsReadAndLinesCutAtTheEndArePadded() throws Exception {
        String header = "0800022211202600002";
        String detail = "1" + "02295721110" + "0000000000660000" + "0000000000000500" + "19112026" + "143044"
                + "0012345"; // sem os 26 espaços finais
        String trailer = "9" + "0000001" + "0000000000660000" + "0000000000000500";
        ParsedFile file = parse((header + "\n" + detail + "\n" + trailer + "\n").getBytes(StandardCharsets.ISO_8859_1));

        assertThat(file.header().formatFileId()).isEqualTo("800-02-00002");
        assertThat(file.header().bmepsFileSequence()).isEqualTo(2);
        ParsedRecord r = file.records().get(0);
        assertThat(r.bankTransactionId()).isEqualTo("0012345");
        assertThat(r.paidAt()).isEqualTo(Instant.parse("2026-11-19T12:30:44Z"));
        assertThat(r.rawLine()).hasSize(91);
    }

    @Test
    void structureErrors() {
        String[] lines = new String(resource("exemplo-ficheiro-bmeps.txt"), StandardCharsets.ISO_8859_1).split("\r\n");
        assertError(join(lines[1], lines[15]), FileRejectionReason.MISSING_HEADER, 1);
        assertError(join(lines[0], lines[1]), FileRejectionReason.MISSING_TRAILER, 2);
        assertError(join(lines[0], "2" + lines[1].substring(1), lines[15]), FileRejectionReason.UNSUPPORTED_RECORD, 2);
        assertError(join(lines[0] + "0", lines[15]), FileRejectionReason.PARSE_ERROR, 1);
        assertError(join(lines[0], lines[1] + " ", lines[15]), FileRejectionReason.PARSE_ERROR, 2);
        assertError(join(lines[0], lines[1], lines[15] + "0"), FileRejectionReason.PARSE_ERROR, 3);
        assertError(join(lines[0].substring(0, 6) + "31022012" + lines[0].substring(14), lines[15]),
                FileRejectionReason.PARSE_ERROR, 1);
        assertError(join(lines[0], lines[1].substring(0, 52) + "246000" + lines[1].substring(58), lines[15]),
                FileRejectionReason.PARSE_ERROR, 2);
    }

    @ParameterizedTest
    @MethodSource("goldenFiles")
    void goldenFilesOfTheSimulatorAreRead(Golden golden) {
        List<String> rows = actual(parser, golden.content(), golden.context());
        assertThat(rows).noneMatch(r -> r.startsWith("error,"));
        assertThat(rows).hasSize(golden.recordCount());
        assertThat(rows.stream().mapToLong(r -> Long.parseLong(r.split(",")[3])).sum())
                .isEqualTo(golden.totalAmountCentavos());
    }

    static List<Golden> goldenFiles() {
        return InterbancosTestFiles.golden("BMEPS");
    }

    // --- auxiliares ---

    ParsedFile parse(byte[] content) throws PaymentFileParseException {
        return parser.parse(new ByteArrayInputStream(content), bmepsContext("test"));
    }

    void assertError(byte[] content, FileRejectionReason code, int lineNo) {
        assertThatThrownBy(() -> parse(content)).isInstanceOfSatisfying(PaymentFileParseException.class, e -> {
            assertThat(e.code()).isEqualTo(code);
            assertThat(e.lineNo()).isEqualTo(lineNo);
        });
    }

    static byte[] join(String... lines) {
        StringBuilder sb = new StringBuilder();
        for (String l : lines) {
            sb.append(l).append("\r\n");
        }
        return sb.toString().getBytes(StandardCharsets.ISO_8859_1);
    }
}
