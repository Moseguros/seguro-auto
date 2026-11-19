package mz.co.moseguros.paymentworker.parser;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * As cópias dos exemplos oficiais (testes do worker e simulador) são iguais byte a byte às de
 * {@code docs/specs/interbancos/} (S5-08 §5).
 */
class OfficialExamplesAreCopiesTest {

    static final Path SPECS = Path.of("..", "docs", "specs", "interbancos");
    static final Path MOCK_EXAMPLES = Path.of("..", "infra", "mocks", "ponto24-mock", "src", "main", "resources",
            "examples");

    @ParameterizedTest
    @ValueSource(strings = {"exemplo-ficheiro-meps.txt", "exemplo-ficheiro-bmeps.txt"})
    void copiesAreIdenticalToTheOfficialExamples(String name) throws Exception {
        byte[] official = Files.readAllBytes(SPECS.resolve(name));
        assertThat(official).as("official example has CRLF").contains((byte) '\r', (byte) '\n');
        assertThat(InterbancosTestFiles.resource(name)).as("worker copy of " + name).isEqualTo(official);
        assertThat(Files.readAllBytes(MOCK_EXAMPLES.resolve(name))).as("ponto24-mock copy of " + name)
                .isEqualTo(official);
    }
}
