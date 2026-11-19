package mz.co.moseguros.paymentworker.parser;

import static mz.co.moseguros.paymentworker.parser.InterbancosTestFiles.actual;
import static mz.co.moseguros.paymentworker.parser.InterbancosTestFiles.expected;
import static mz.co.moseguros.paymentworker.parser.InterbancosTestFiles.mepsContext;
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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

/** Leitor MEPS (S5-08 §3, §5 e §7). */
class MepsFileParserTest {

    final MepsFileParser parser = new MepsFileParser();

    static Stream<String> files() {
        return Stream.of("exemplo-ficheiro-meps.txt", "meps-ok-lf.txt", "meps-ok-trimmed.txt", "meps-ok-a035-10.txt",
                "meps-ok-currency-508.txt", "meps-ok-empty.txt", "meps-err-trailer-count.txt",
                "meps-err-trailer-total.txt", "meps-err-entity.txt", "meps-err-processing-code.txt",
                "meps-err-currency.txt", "meps-err-line-too-long.txt", "meps-err-no-trailer.txt");
    }

    @ParameterizedTest
    @MethodSource("files")
    void eachFileGivesItsExpectedResult(String name) {
        assertThat(actual(parser, resource(name), mepsContext(name))).containsExactlyElementsOf(expected(name));
    }

    @Test
    void officialExampleHeaderAndTrailer() throws Exception {
        ParsedFile file = parse(resource("exemplo-ficheiro-meps.txt"));

        FileHeader h = file.header();
        assertThat(h.entity()).isEqualTo("90002");
        assertThat(h.formatFileId()).isEqualTo("201211230");
        assertThat(h.previousFormatFileId()).isEqualTo("201211220");
        assertThat(h.fileDate()).isEqualTo(LocalDate.of(2012, 11, 23));
        assertThat(h.bmepsCompanyId()).isNull();
        assertThat(h.bmepsProfileId()).isNull();
        assertThat(h.bmepsFileSequence()).isNull();
        assertThat(h.rawHeader()).hasSize(107).startsWith("0MEPS");
        assertThat(file.records()).hasSize(11);
        assertThat(file.records().stream().mapToLong(ParsedRecord::amountCentavos).sum()).isEqualTo(5_132_000);
        assertThat(file.trailer()).isEqualTo(new FileTrailer(11, 5_132_000, 11_000L));
        List<ParsedRecord> twice = file.records().stream().filter(r -> r.reference().equals("00620400869")).toList();
        assertThat(twice).extracting(ParsedRecord::paidAt).containsExactly(
                Instant.parse("2012-11-22T07:26:00Z"), Instant.parse("2012-11-22T07:29:00Z")); // 09:26 e 09:29 em Maputo
        assertThat(file.records().get(0).rawLine()).hasSize(107).doesNotContain("\r");
        assertThat(file.records().get(0).bankTransactionId()).isNull();
        assertThat(file.records().get(0).channel()).isNull();
    }

    @Test
    void firstFileHasNoPrevious() throws Exception {
        String content = new String(resource("exemplo-ficheiro-meps.txt"), StandardCharsets.ISO_8859_1);
        ParsedFile file = parse((content.substring(0, 30) + "000000000" + content.substring(39))
                .getBytes(StandardCharsets.ISO_8859_1));
        assertThat(file.header().previousFormatFileId()).isNull();
    }

    @Test
    void bankLogTerminalTypeAndSpecHeaderAreRead() throws Exception {
        String header = pad("0MEPS5000000000000100202611191000000000900025080000");
        String detail = pad("204" + "0001" + "00000042" + "2026111910  " + "0000000299900" + "000001000" + "AT"
                + "0".repeat(15) + " ".repeat(15) + "00000011205" + "00");
        String trailer = pad("9" + "00000001" + "00000000000299900" + "000000001000" + "000000000000");
        ParsedFile file = parse(join(header, detail, trailer));

        ParsedRecord r = file.records().get(0);
        assertThat(r.bankTransactionId()).isEqualTo("0001-00000042");
        assertThat(r.channel()).isEqualTo("AT");
        assertThat(r.paidAt()).isEqualTo(Instant.parse("2026-11-19T08:00:00Z"));
        assertThat(r.feeCentavos()).isEqualTo(1000);
        assertThat(file.header().formatFileId()).isEqualTo("202611191");
        assertThat(file.header().previousFormatFileId()).isNull();
    }

    @Test
    void emptyLinesAtTheEndAreIgnoredButNotInTheMiddle() throws Exception {
        String content = new String(resource("exemplo-ficheiro-meps.txt"), StandardCharsets.ISO_8859_1);
        assertThat(parse((content + "\r\n\r\n\n").getBytes(StandardCharsets.ISO_8859_1)).records()).hasSize(11);

        String middle = content.replaceFirst("\r\n", "\r\n\r\n");
        assertError(middle.getBytes(StandardCharsets.ISO_8859_1), FileRejectionReason.PARSE_ERROR, 2);
    }

    @Test
    void lastLineWithoutTerminatorIsRead() throws Exception {
        String content = new String(resource("exemplo-ficheiro-meps.txt"), StandardCharsets.ISO_8859_1);
        assertThat(parse(content.substring(0, content.length() - 2).getBytes(StandardCharsets.ISO_8859_1)).records())
                .hasSize(11);
    }

    @Test
    void structureErrors() {
        String content = new String(resource("exemplo-ficheiro-meps.txt"), StandardCharsets.ISO_8859_1);
        String[] lines = content.split("\r\n");
        assertError(new byte[0], FileRejectionReason.MISSING_HEADER, 1);
        assertError(join(lines[1], lines[12]), FileRejectionReason.MISSING_HEADER, 1);
        assertError(join(lines[0], lines[0], lines[12]), FileRejectionReason.PARSE_ERROR, 2);
        assertError(join(lines[0], lines[12], lines[1]), FileRejectionReason.PARSE_ERROR, 3);
        assertError(join(lines[0], lines[12], lines[12]), FileRejectionReason.PARSE_ERROR, 3);
        assertError(join(lines[0], "3" + lines[1].substring(1), lines[12]), FileRejectionReason.UNSUPPORTED_RECORD, 2);
        assertError(join("0XEPS" + lines[0].substring(5), lines[12]), FileRejectionReason.PARSE_ERROR, 1);
    }

    @ParameterizedTest
    @ValueSource(ints = {28, 41, 82, 6, 40})
    void nonDigitInANumericFieldIsAParseError(int position) {
        String content = new String(resource("exemplo-ficheiro-meps.txt"), StandardCharsets.ISO_8859_1);
        String[] lines = content.split("\r\n");
        int line = position == 6 || position == 40 ? 0 : 1;
        lines[line] = lines[line].substring(0, position - 1) + "A" + lines[line].substring(position);
        assertError(join(lines), FileRejectionReason.PARSE_ERROR, line + 1);
    }

    @ParameterizedTest
    @ValueSource(strings = {"201213221034", "201211321034", "201211222534", "201211221060", "2012112210 1"})
    void impossibleDateOrTimeIsAParseError(String a035) {
        String content = new String(resource("exemplo-ficheiro-meps.txt"), StandardCharsets.ISO_8859_1);
        String[] lines = content.split("\r\n");
        lines[1] = lines[1].substring(0, 15) + a035 + lines[1].substring(27);
        assertError(join(lines), FileRejectionReason.PARSE_ERROR, 2);
    }

    @Test
    void controlCharactersAreEncodingErrorsButLatin1LettersAreAccepted() throws Exception {
        String content = new String(resource("exemplo-ficheiro-meps.txt"), StandardCharsets.ISO_8859_1);
        String[] lines = content.split("\r\n");
        lines[2] = lines[2].substring(0, 66) + "MAPUTO-MATOLAÇã" + lines[2].substring(81); // A030
        assertThat(parse(join(lines)).records()).hasSize(11);

        lines[2] = lines[2].substring(0, 66) + "\u0085" + lines[2].substring(67);
        assertError(join(lines), FileRejectionReason.ENCODING_ERROR, 3);
        lines[2] = lines[2].substring(0, 66) + "\r" + lines[2].substring(67);
        assertError(join(lines), FileRejectionReason.ENCODING_ERROR, 3);
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
        return InterbancosTestFiles.golden("MEPS");
    }

    // --- auxiliares ---

    ParsedFile parse(byte[] content) throws PaymentFileParseException {
        return parser.parse(new ByteArrayInputStream(content), mepsContext("test.txt"));
    }

    void assertError(byte[] content, FileRejectionReason code, int lineNo) {
        assertThatThrownBy(() -> parse(content)).isInstanceOfSatisfying(PaymentFileParseException.class, e -> {
            assertThat(e.code()).isEqualTo(code);
            assertThat(e.lineNo()).isEqualTo(lineNo);
        });
    }

    static String pad(String s) {
        assertThat(s.length()).isLessThanOrEqualTo(107);
        return s + " ".repeat(107 - s.length());
    }

    static byte[] join(String... lines) {
        StringBuilder sb = new StringBuilder();
        for (String l : lines) {
            sb.append(l).append("\r\n");
        }
        return sb.toString().getBytes(StandardCharsets.ISO_8859_1);
    }
}
