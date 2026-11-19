package mz.co.moseguros.paymentworker.parser;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import mz.co.moseguros.paymentcontracts.FileSourceDescriptor;

/**
 * Ficheiros de {@code src/test/resources/interbancos/} (S5-08 §5) e dos {@code golden/} do simulador: leitura,
 * validação comum e o resultado no formato dos {@code expected-*.csv}.
 */
final class InterbancosTestFiles {

    static final String MEPS_ENTITY = "90002";
    static final String BMEPS_ENTITY = "90090";
    static final String BMEPS_COMPANY = "800";
    static final String BMEPS_PROFILE = "02";

    /** Ficheiros {@code golden/} do simulador (S5-08 §7), pelo caminho relativo ao módulo. */
    static final Path GOLDEN = Path.of("..", "infra", "mocks", "ponto24-mock", "src", "test", "resources", "golden");

    private InterbancosTestFiles() {
    }

    static ParseContext mepsContext(String fileName) {
        return new ParseContext(MEPS_ENTITY, null, null, ParseContext.MAPUTO, fileName);
    }

    static ParseContext bmepsContext(String fileName) {
        return new ParseContext(BMEPS_ENTITY, BMEPS_COMPANY, BMEPS_PROFILE, ParseContext.MAPUTO, fileName);
    }

    static byte[] resource(String name) {
        try (InputStream in = InterbancosTestFiles.class.getResourceAsStream("/interbancos/" + name)) {
            assertThat(in).as(name).isNotNull();
            return in.readAllBytes();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /** Linhas do {@code expected-<ficheiro>.csv}. */
    static List<String> expected(String fileName) {
        String csv = new String(resource("expected-" + fileName.replace(".txt", ".csv")), StandardCharsets.UTF_8);
        List<String> rows = new ArrayList<>();
        for (String line : csv.split("\r?\n")) {
            if (!line.isEmpty()) {
                rows.add(line);
            }
        }
        return rows;
    }

    /** Lê e valida como o worker (parse + {@link FileValidator}); devolve o resultado no formato do CSV. */
    static List<String> actual(PaymentFileParser parser, byte[] content, ParseContext ctx) {
        ParsedFile file;
        try {
            file = parser.parse(new ByteArrayInputStream(content), ctx);
        } catch (PaymentFileParseException e) {
            return List.of(error(e.code().name(), e.lineNo()));
        }
        Optional<FileValidator.Failure> failure = new FileValidator().validate(file, ctx, parser.format());
        if (failure.isPresent()) {
            return List.of(error(failure.get().reason().name(), failure.get().lineNo()));
        }
        return file.records().stream().map(InterbancosTestFiles::row).toList();
    }

    static String row(ParsedRecord r) {
        return r.lineNo() + "," + r.entity() + "," + r.reference() + "," + r.amountCentavos() + ","
                + nullToEmpty(r.feeCentavos()) + "," + r.paidAt() + "," + nullToEmpty(r.channel()) + ","
                + nullToEmpty(r.bankTransactionId());
    }

    static String error(String code, Integer lineNo) {
        return "error," + code + "," + nullToEmpty(lineNo);
    }

    private static String nullToEmpty(Object o) {
        return o == null ? "" : o.toString();
    }

    /**
     * Uma linha do {@code golden/golden-files.csv} do simulador:
     * {@code file,format,entity,companyId,profileId,recordCount,totalAmountCentavos}.
     */
    record Golden(String file, String format, String entity, String companyId, String profileId, int recordCount,
                  long totalAmountCentavos) {

        ParseContext context() {
            return FileSourceDescriptor.FORMAT_BMEPS.equals(format)
                    ? new ParseContext(entity, companyId, profileId, ParseContext.MAPUTO, file)
                    : new ParseContext(entity, null, null, ParseContext.MAPUTO, file);
        }

        byte[] content() {
            try {
                return Files.readAllBytes(GOLDEN.resolve(file));
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }

        @Override
        public String toString() {
            return file;
        }
    }

    static List<Golden> golden(String format) {
        try {
            List<Golden> out = new ArrayList<>();
            List<String> lines = Files.readAllLines(GOLDEN.resolve("golden-files.csv"), StandardCharsets.UTF_8);
            assertThat(lines.get(0)).isEqualTo("file,format,entity,companyId,profileId,recordCount,totalAmountCentavos");
            for (String line : lines.subList(1, lines.size())) {
                if (line.isBlank()) {
                    continue;
                }
                String[] f = line.split(",", -1);
                Golden g = new Golden(f[0], f[1], f[2], f[3].isEmpty() ? null : f[3], f[4].isEmpty() ? null : f[4],
                        Integer.parseInt(f[5]), Long.parseLong(f[6]));
                if (g.format().equals(format)) {
                    out.add(g);
                }
            }
            assertThat(out).as("golden files of " + format).isNotEmpty();
            return out;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
