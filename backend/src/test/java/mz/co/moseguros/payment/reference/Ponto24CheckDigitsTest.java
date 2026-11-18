package mz.co.moseguros.payment.reference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Stream;

import mz.co.moseguros.payment.application.reference.Ponto24CheckDigits;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Dígitos de controlo Ponto24 (S5-07 §2 e §6): os casos de {@code ponto24/reference-cases.csv}, as 25
 * referências dos ficheiros de exemplo de {@code docs/specs/interbancos/} (lidas pelas posições do README)
 * e um teste de propriedade com 10 000 entradas aleatórias.
 */
class Ponto24CheckDigitsTest {

    private static final Path SPECS = Path.of("..", "docs", "specs", "interbancos");

    record Case(String entity, String invoice, String month, long amount, String reference, boolean valid) {
        @Override
        public String toString() {
            return entity + "/" + reference + "/" + amount + (valid ? "" : " (inválida)");
        }
    }

    static List<Case> cases() throws IOException {
        List<Case> cases = new ArrayList<>();
        try (InputStream in = Ponto24CheckDigitsTest.class.getResourceAsStream("/ponto24/reference-cases.csv")) {
            assertThat(in).as("reference-cases.csv").isNotNull();
            String[] lines = new String(in.readAllBytes(), StandardCharsets.UTF_8).split("\r?\n");
            boolean header = true;
            for (String line : lines) {
                if (line.isBlank() || line.startsWith("#")) {
                    continue;
                }
                if (header) {
                    assertThat(line).isEqualTo("entity,invoiceNumber,month,amountCentavos,reference,valid");
                    header = false;
                    continue;
                }
                String[] f = line.split(",", -1);
                assertThat(f).hasSize(6);
                cases.add(new Case(f[0], f[1], f[2], Long.parseLong(f[3]), f[4], Boolean.parseBoolean(f[5])));
            }
        }
        return cases;
    }

    static Stream<Case> validCases() throws IOException {
        return cases().stream().filter(Case::valid);
    }

    static Stream<Case> invalidCases() throws IOException {
        return cases().stream().filter(c -> !c.valid());
    }

    @ParameterizedTest
    @MethodSource("validCases")
    void validCasesGenerateTheExpectedReference(Case c) {
        int invoice = Integer.parseInt(c.invoice());
        int month = Integer.parseInt(c.month());
        assertThat(Ponto24CheckDigits.reference(c.entity(), invoice, month, c.amount())).isEqualTo(c.reference());
        assertThat(Ponto24CheckDigits.compute(c.entity(), invoice, month, c.amount()))
                .isEqualTo(c.reference().substring(9));
        assertThat(Ponto24CheckDigits.isValid(c.entity(), c.reference(), c.amount())).isTrue();
    }

    @ParameterizedTest
    @MethodSource("invalidCases")
    void invalidCasesFail(Case c) {
        assertThat(Ponto24CheckDigits.isValid(c.entity(), c.reference(), c.amount())).isFalse();
    }

    @Test
    void theCsvHasTheCasesOfTheTask() throws IOException {
        List<Case> all = cases();
        assertThat(all.stream().filter(Case::valid).filter(c -> c.entity().equals("90002"))).hasSizeGreaterThan(10);
        assertThat(all.stream().filter(Case::valid).filter(c -> c.entity().equals("90090"))).hasSize(14);
        assertThat(all).anySatisfy(c -> assertThat(c.reference()).isEqualTo("12122450702"));
        assertThat(all.stream().filter(c -> !c.valid())).hasSizeGreaterThanOrEqualTo(9);
    }

    @Test
    void officialExample() {
        assertThat(Ponto24CheckDigits.reference("54321", 1_212_245, 7, 200_000)).isEqualTo("12122450702");
        assertThat(Ponto24CheckDigits.compute("54321", 1_212_245, 7, 200_000)).isEqualTo("02");
        // o mesmo cálculo com o montante "0200000" (zero à esquerda) daria 21: o código nunca o usa
        assertThat(Ponto24CheckDigits.checkDigitsOf("54321" + "1212245" + "07" + "0200000")).isEqualTo("21");
        assertThat(Ponto24CheckDigits.checkDigitsOf("54321" + "1212245" + "07" + "200000")).isEqualTo("02");
        assertThat(Ponto24CheckDigits.isValid("54321", "12122450721", 200_000)).isFalse();
    }

    @Test
    void invalidInputsAreProgrammingErrors() {
        assertThatThrownBy(() -> Ponto24CheckDigits.compute("5432", 1, 1, 1)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> Ponto24CheckDigits.compute("5432a", 1, 1, 1)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> Ponto24CheckDigits.compute(null, 1, 1, 1)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> Ponto24CheckDigits.compute("54321", 0, 1, 1)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> Ponto24CheckDigits.compute("54321", 10_000_000, 1, 1))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> Ponto24CheckDigits.compute("54321", 1, 0, 1)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> Ponto24CheckDigits.compute("54321", 1, 13, 1)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> Ponto24CheckDigits.compute("54321", 1, 1, 0)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> Ponto24CheckDigits.reference("54321", -1, 1, 1))
                .isInstanceOf(IllegalArgumentException.class);
        // a verificação nunca lança: só diz que não
        assertThat(Ponto24CheckDigits.isValid(null, "12122450702", 200_000)).isFalse();
        assertThat(Ponto24CheckDigits.isValid("54321", null, 200_000)).isFalse();
        assertThat(Ponto24CheckDigits.isValid("54321", "12122450702", 0)).isFalse();
        assertThat(Ponto24CheckDigits.isValid("54321", "121224507020", 200_000)).isFalse();
    }

    // --- Ficheiros de exemplo (docs/specs/interbancos/) ---

    record Paid(String reference, long amount) {
    }

    static List<String> lines(String file) throws IOException {
        String content = Files.readString(SPECS.resolve(file), StandardCharsets.US_ASCII);
        List<String> lines = new ArrayList<>();
        for (String line : content.split("\r\n")) {
            if (!line.isEmpty()) {
                lines.add(line);
            }
        }
        return lines;
    }

    /** Posições do README §2: entidade A033 (40–44) no cabeçalho; A011 (28–40) e A034 (82–92) no detalhe. */
    @Test
    void theElevenMepsReferencesValidateWithTheEntityOfTheHeader() throws IOException {
        List<String> lines = lines("exemplo-ficheiro-meps.txt");
        assertThat(lines.get(0)).startsWith("0MEPS");
        String entity = lines.get(0).substring(39, 44);
        assertThat(entity).isEqualTo("90002");
        List<Paid> paid = new ArrayList<>();
        for (String line : lines) {
            if (line.charAt(0) == '2') {
                paid.add(new Paid(line.substring(81, 92), Long.parseLong(line.substring(27, 40))));
            }
        }
        assertThat(paid).hasSize(11);
        assertThat(paid).allSatisfy(p -> assertThat(Ponto24CheckDigits.isValid(entity, p.reference(), p.amount()))
                .as(p.toString()).isTrue());
        assertAllInCsv(entity, paid);
    }

    /** Posições do README §3: referência (2–12) e valor (13–28) no detalhe; a entidade vem da configuração. */
    @Test
    void theFourteenBmepsReferencesValidateWithEntity90090() throws IOException {
        String entity = "90090";
        assertThat(Integer.parseInt(entity) % 97).isEqualTo(74);
        List<Paid> paid = new ArrayList<>();
        for (String line : lines("exemplo-ficheiro-bmeps.txt")) {
            if (line.charAt(0) == '1') {
                paid.add(new Paid(line.substring(1, 12), Long.parseLong(line.substring(12, 28))));
            }
        }
        assertThat(paid).hasSize(14);
        assertThat(paid).allSatisfy(p -> assertThat(Ponto24CheckDigits.isValid(entity, p.reference(), p.amount()))
                .as(p.toString()).isTrue());
        assertAllInCsv(entity, paid);
    }

    /** O CSV nunca diverge dos ficheiros: cada referência paga está lá, com o mesmo montante. */
    private static void assertAllInCsv(String entity, List<Paid> paid) throws IOException {
        Set<String> inCsv = new LinkedHashSet<>();
        for (Case c : cases()) {
            if (c.valid() && c.entity().equals(entity)) {
                inCsv.add(c.reference() + "/" + c.amount());
            }
        }
        for (Paid p : paid) {
            assertThat(inCsv).contains(p.reference() + "/" + p.amount());
        }
    }

    // --- Propriedades ---

    @Test
    void propertiesOverTenThousandRandomInputs() {
        Random random = new Random(20261118L);
        int transpositions = 0;
        int transpositionsDetected = 0;
        for (int n = 0; n < 10_000; n++) {
            String entity = String.format("%05d", random.nextInt(100_000));
            int invoice = 1 + random.nextInt(Ponto24CheckDigits.MAX_INVOICE);
            int month = 1 + random.nextInt(12);
            long amount = 1 + (long) (random.nextDouble() * 999_999_999_999L);
            String reference = Ponto24CheckDigits.reference(entity, invoice, month, amount);

            assertThat(reference).hasSize(11).containsOnlyDigits();
            assertThat(Ponto24CheckDigits.isValid(entity, reference, amount)).isTrue();

            // ISO 7064 MOD 97-10: CD = 98 − ((S × 100) mod 97)
            BigInteger s = new BigInteger(entity + reference.substring(0, 9) + amount);
            int expected = 98 - s.multiply(BigInteger.valueOf(100)).mod(BigInteger.valueOf(97)).intValue();
            assertThat(Integer.parseInt(reference.substring(9))).isEqualTo(expected);
            assertThat(expected).isBetween(1, 98);

            // um dígito trocado (referência, entidade ou montante): falha sempre
            assertThat(Ponto24CheckDigits.isValid(entity, changeOneDigit(reference, random, false), amount)).isFalse();
            assertThat(Ponto24CheckDigits.isValid(changeOneDigit(entity, random, false), reference, amount)).isFalse();
            long otherAmount = Long.parseLong(changeOneDigit(Long.toString(amount), random, true));
            assertThat(Ponto24CheckDigits.isValid(entity, reference, otherAmount)).isFalse();

            // dois dígitos adjacentes diferentes trocados: falha em pelo menos 99%
            int i = random.nextInt(10);
            if (reference.charAt(i) != reference.charAt(i + 1)) {
                transpositions++;
                char[] swapped = reference.toCharArray();
                swapped[i] = reference.charAt(i + 1);
                swapped[i + 1] = reference.charAt(i);
                if (!Ponto24CheckDigits.isValid(entity, new String(swapped), amount)) {
                    transpositionsDetected++;
                }
            }
        }
        assertThat(transpositions).isGreaterThan(8_000);
        assertThat((double) transpositionsDetected / transpositions).isGreaterThanOrEqualTo(0.99);
    }

    /** Troca um dígito por outro diferente (sem pôr um zero à esquerda num montante). */
    private static String changeOneDigit(String digits, Random random, boolean noLeadingZero) {
        char[] chars = digits.toCharArray();
        int i = random.nextInt(chars.length);
        char replacement;
        do {
            replacement = (char) ('0' + random.nextInt(10));
        } while (replacement == chars[i] || (noLeadingZero && i == 0 && replacement == '0'));
        chars[i] = replacement;
        return new String(chars);
    }
}
