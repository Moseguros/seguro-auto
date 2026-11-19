package mz.co.moseguros.ponto24mock;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * A cópia de {@code Ponto24CheckDigits} (S5-08 §7): os casos do S5-07 dão os mesmos resultados e o código-fonte é
 * igual ao do backend, excepto o {@code package}.
 */
class Ponto24CheckDigitsTest {

    static final Path BACKEND = Path.of("..", "..", "..", "backend");
    static final Path CASES = BACKEND.resolve(Path.of("src", "test", "resources", "ponto24", "reference-cases.csv"));
    static final Path ORIGINAL = BACKEND.resolve(Path.of("src", "main", "java", "mz", "co", "moseguros", "payment",
            "application", "reference", "Ponto24CheckDigits.java"));
    static final Path COPY = Path.of("src", "main", "java", "mz", "co", "moseguros", "ponto24mock",
            "Ponto24CheckDigits.java");

    record Case(String entity, String invoice, String month, long amount, String reference, boolean valid) {
        @Override
        public String toString() {
            return entity + "/" + reference + "/" + amount + (valid ? "" : " (inválida)");
        }
    }

    static List<Case> cases() throws IOException {
        List<Case> cases = new ArrayList<>();
        boolean header = true;
        for (String line : Files.readAllLines(CASES, StandardCharsets.UTF_8)) {
            if (line.isBlank() || line.startsWith("#")) {
                continue;
            }
            if (header) {
                assertThat(line).isEqualTo("entity,invoiceNumber,month,amountCentavos,reference,valid");
                header = false;
                continue;
            }
            String[] f = line.split(",", -1);
            cases.add(new Case(f[0], f[1], f[2], Long.parseLong(f[3]), f[4], Boolean.parseBoolean(f[5])));
        }
        assertThat(cases).hasSizeGreaterThan(20);
        return cases;
    }

    @ParameterizedTest
    @MethodSource("cases")
    void casesOfTheBackendGiveTheSameResults(Case c) {
        assertThat(Ponto24CheckDigits.isValid(c.entity(), c.reference(), c.amount())).isEqualTo(c.valid());
        if (c.valid()) {
            assertThat(Ponto24CheckDigits.reference(c.entity(), Integer.parseInt(c.invoice()),
                    Integer.parseInt(c.month()), c.amount())).isEqualTo(c.reference());
        }
    }

    @Test
    void sourceIsAnExactCopyOfTheBackendClass() throws IOException {
        assertThat(withoutPackage(COPY)).isEqualTo(withoutPackage(ORIGINAL));
    }

    static String withoutPackage(Path file) throws IOException {
        String source = Files.readString(file, StandardCharsets.UTF_8).replace("\r\n", "\n");
        assertThat(source).startsWith("package ");
        return source.substring(source.indexOf('\n') + 1);
    }
}
