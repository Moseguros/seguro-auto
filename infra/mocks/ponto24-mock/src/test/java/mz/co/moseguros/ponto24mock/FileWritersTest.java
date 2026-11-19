package mz.co.moseguros.ponto24mock;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import mz.co.moseguros.ponto24mock.MockProperties.FileStyle;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

/**
 * Os <i>writers</i> com entradas fixas produzem exactamente os ficheiros {@code golden/} (S5-08 §7), que os
 * testes do worker também lêem. Para os gerar de novo depois de uma mudança intencional:
 * {@code ./mvnw -B test -Dtest=FileWritersTest -Dgolden.update=true}.
 */
class FileWritersTest {

    static final ZoneId MAPUTO = ZoneId.of("Africa/Maputo");
    static final Path GOLDEN = Path.of("src", "test", "resources", "golden");
    static final Path EXAMPLES = Path.of("src", "main", "resources", "examples");

    /** Três pagamentos fixos da entidade MEPS 90002 (o segundo com tipo de terminal). */
    static List<FilePayment> mepsPayments() {
        return List.of(
                new FilePayment(Ponto24CheckDigits.reference("90002", 1, 11, 299_900), 299_900, 1_000,
                        Instant.parse("2026-11-19T07:15:30Z"), null, 1),
                new FilePayment(Ponto24CheckDigits.reference("90002", 2, 11, 1_250_050), 1_250_050, 1_000,
                        Instant.parse("2026-11-19T08:42:05Z"), "AT", 2),
                new FilePayment(Ponto24CheckDigits.reference("90002", 3, 11, 61_000), 61_000, 1_000,
                        Instant.parse("2026-11-19T21:59:59Z"), null, 3));
    }

    /** Dois pagamentos fixos da entidade BMEPS 90090 (empresa 800, perfil 02). */
    static List<FilePayment> bmepsPayments() {
        return List.of(
                new FilePayment(Ponto24CheckDigits.reference("90090", 1, 11, 572_000), 572_000, 500,
                        Instant.parse("2026-11-19T12:30:44Z"), null, 4),
                new FilePayment(Ponto24CheckDigits.reference("90090", 10, 11, 110_000), 110_000, 500,
                        Instant.parse("2026-11-19T21:00:00Z"), null, 5));
    }

    static byte[] meps(FileStyle style) {
        return MepsFileWriter.write("90002", "202611192", "202611191", mepsPayments(), style, MAPUTO);
    }

    static byte[] bmeps(FileStyle style) {
        return BmepsFileWriter.write("800", "02", LocalDate.of(2026, 11, 19), 3, bmepsPayments(), style, MAPUTO);
    }

    @ParameterizedTest
    @EnumSource(FileStyle.class)
    void mepsWriterProducesTheGoldenFile(FileStyle style) throws IOException {
        assertGolden("meps-" + style.name().toLowerCase() + ".txt", meps(style));
    }

    @ParameterizedTest
    @EnumSource(FileStyle.class)
    void bmepsWriterProducesTheGoldenFile(FileStyle style) throws IOException {
        assertGolden("bmeps-" + style.name().toLowerCase() + ".txt", bmeps(style));
    }

    @Test
    void goldenManifestMatchesTheFiles() throws IOException {
        String manifest = """
                file,format,entity,companyId,profileId,recordCount,totalAmountCentavos
                meps-example.txt,MEPS,90002,,,3,1610950
                meps-spec.txt,MEPS,90002,,,3,1610950
                bmeps-example.txt,BMEPS,90090,800,02,2,682000
                bmeps-spec.txt,BMEPS,90090,800,02,2,682000
                """;
        assertThat(mepsPayments().stream().mapToLong(FilePayment::amountCentavos).sum()).isEqualTo(1_610_950);
        assertThat(bmepsPayments().stream().mapToLong(FilePayment::amountCentavos).sum()).isEqualTo(682_000);
        assertGolden("golden-files.csv", manifest.getBytes(StandardCharsets.UTF_8));
    }

    @Test
    void specStyleDiffersOnlyWhereTheSpecificationSays() {
        String example = new String(meps(FileStyle.EXAMPLE), StandardCharsets.ISO_8859_1);
        String spec = new String(meps(FileStyle.SPEC), StandardCharsets.ISO_8859_1);
        String[] e = example.split("\r\n");
        String[] s = spec.split("\r\n");
        assertThat(e[0].substring(5, 21)).isEqualTo("0000000800090002");
        assertThat(s[0].substring(5, 21)).isEqualTo("5000000000000100");
        assertThat(e[0].substring(44, 49)).isEqualTo("943XX");
        assertThat(s[0].substring(44, 49)).isEqualTo("50800");
        assertThat(e[1].substring(3, 27)).isEqualTo("000000000000202611190915");
        assertThat(s[1].substring(3, 27)).isEqualTo("00010000000120261119" + "09  ");
        assertThat(e[3].substring(49, 51)).isEqualTo("  ");
        assertThat(e[2].substring(49, 51)).isEqualTo("AT");
        assertThat(e[4].substring(38, 50)).isEqualTo("00000000000 ");
        assertThat(s[4].substring(38, 50)).isEqualTo("000000000000");
        for (String line : e) {
            assertThat(line).hasSize(107);
        }
        for (String line : s) {
            assertThat(line).hasSize(107);
        }
        String[] b = new String(bmeps(FileStyle.SPEC), StandardCharsets.ISO_8859_1).split("\r\n");
        assertThat(b).extracting(String::length).containsExactly(19, 91, 91, 40);
        assertThat(b[1].substring(58, 65)).isEqualTo("0000004");
        assertThat(new String(bmeps(FileStyle.EXAMPLE), StandardCharsets.ISO_8859_1).split("\r\n")[1].substring(58, 65))
                .isEqualTo("0000000");
    }

    @Test
    void exampleStyleWithTheOfficialPaymentsIsTheOfficialMepsExample() throws IOException {
        byte[] official = Files.readAllBytes(EXAMPLES.resolve("exemplo-ficheiro-meps.txt"));
        String[] lines = new String(official, StandardCharsets.ISO_8859_1).split("\r\n");
        DateTimeFormatter a035 = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
        List<FilePayment> payments = new ArrayList<>();
        for (int i = 1; i < lines.length - 1; i++) {
            String l = lines[i];
            payments.add(new FilePayment(l.substring(81, 92), Long.parseLong(l.substring(27, 40)),
                    Long.parseLong(l.substring(40, 49)),
                    LocalDateTime.parse(l.substring(15, 27), a035).atZone(MAPUTO).toInstant(), null, i));
        }
        assertThat(payments).hasSize(11);

        // A003/A004 e A005/A007 vêm da entidade e da data; com os do exemplo, o ficheiro é igual byte a byte
        byte[] written = MepsFileWriter.write("90002", "201211230", "201211220", payments, FileStyle.EXAMPLE, MAPUTO);

        assertThat(written).isEqualTo(official);
    }

    @Test
    void exampleStyleBmepsHasTheLayoutOfTheOfficialExample() throws IOException {
        String official = Files.readString(EXAMPLES.resolve("exemplo-ficheiro-bmeps.txt"), StandardCharsets.ISO_8859_1);
        String[] lines = official.split("\r\n");
        DateTimeFormatter dt = DateTimeFormatter.ofPattern("ddMMyyyyHHmmss");
        List<FilePayment> payments = new ArrayList<>();
        for (int i = 1; i < lines.length - 1; i++) {
            String l = lines[i];
            payments.add(new FilePayment(l.substring(1, 12), Long.parseLong(l.substring(12, 28)),
                    Long.parseLong(l.substring(28, 44)),
                    LocalDateTime.parse(l.substring(44, 58), dt).atZone(MAPUTO).toInstant(), null, i));
        }
        byte[] written = BmepsFileWriter.write("800", "02", LocalDate.of(2012, 11, 22), 1, payments, FileStyle.EXAMPLE,
                MAPUTO);
        assertThat(new String(written, StandardCharsets.ISO_8859_1)).isEqualTo(official);
    }

    static void assertGolden(String name, byte[] content) throws IOException {
        Path file = GOLDEN.resolve(name);
        if (Boolean.getBoolean("golden.update")) {
            Files.createDirectories(GOLDEN);
            Files.write(file, content);
        }
        assertThat(Files.exists(file)).as(name + " (generate with -Dgolden.update=true)").isTrue();
        assertThat(Files.readAllBytes(file)).as(name).isEqualTo(content);
    }
}
