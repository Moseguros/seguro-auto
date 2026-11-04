package mz.co.moseguros.proposal;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import mz.co.moseguros.proposal.domain.DeclarationQuestion;
import mz.co.moseguros.proposal.domain.SuitabilityQuestion;
import mz.co.moseguros.proposal.domain.SuitabilityReason;
import org.junit.jupiter.api.Test;

/**
 * {@code api/proposal-questions.json} (S4-02 §11) é a lista oficial dos códigos das declarações de
 * risco, do questionário de adequação e dos motivos de aviso, para a web (S4-08), a app (S4-09) e o
 * PDF (S4-04) terem um texto para cada código. Só códigos, sem textos.
 *
 * <p>Com {@code -Dproposal-questions.export=true} escreve o ficheiro a partir dos enums; sem a flag
 * falha se o ficheiro não lhes corresponder.
 */
class ProposalQuestionsExportTest {

    static final Path FILE = Path.of(System.getProperty("proposal-questions.output",
            "../api/proposal-questions.json")).toAbsolutePath().normalize();

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void proposalQuestionsFileMatchesTheEnums() throws IOException {
        JsonNode expected = expected();
        if (Boolean.getBoolean("proposal-questions.export")) {
            Files.createDirectories(FILE.getParent());
            Files.writeString(FILE, render(expected), StandardCharsets.UTF_8);
        }
        String hint = "corre ./mvnw test -Dtest=ProposalQuestionsExportTest -Dproposal-questions.export=true";
        assertThat(Files.exists(FILE)).as("%s não existe: %s", FILE, hint).isTrue();
        JsonNode actual = mapper.readTree(Files.readString(FILE, StandardCharsets.UTF_8));
        assertThat(actual).as("%s está desactualizado: %s", FILE, hint).isEqualTo(expected);
    }

    @Test
    void thereAreFiveDeclarationsFiveSuitabilityQuestionsAndEightReasons() {
        assertThat(DeclarationQuestion.values()).hasSize(5);
        assertThat(SuitabilityQuestion.values()).hasSize(5);
        assertThat(SuitabilityReason.values()).hasSize(8);
        assertThat(DeclarationQuestion.CLAIMS_LAST_3_YEARS.followUp()).containsExactly("claimsCount", "details");
        assertThat(SuitabilityQuestion.SUIT_NEED.options())
                .containsExactly("MANDATORY_ONLY", "OWN_DAMAGE", "FULL", "UNSURE");
    }

    private JsonNode expected() {
        ObjectNode root = mapper.createObjectNode();
        ArrayNode declarations = root.putArray("declarations");
        for (DeclarationQuestion q : DeclarationQuestion.values()) {
            ObjectNode node = declarations.addObject();
            node.put("code", q.name());
            ArrayNode followUp = node.putArray("followUp");
            q.followUp().forEach(followUp::add);
        }
        ArrayNode suitability = root.putArray("suitability");
        for (SuitabilityQuestion q : SuitabilityQuestion.values()) {
            ObjectNode node = suitability.addObject();
            node.put("code", q.name());
            node.put("type", q.type().name());
            ArrayNode options = node.putArray("options");
            q.options().forEach(options::add);
        }
        ArrayNode reasons = root.putArray("suitabilityReasons");
        for (SuitabilityReason r : SuitabilityReason.values()) {
            reasons.add(r.name());
        }
        return root;
    }

    private String render(JsonNode node) throws IOException {
        DefaultPrettyPrinter printer = new DefaultPrettyPrinter()
                .withObjectIndenter(new DefaultIndenter("  ", "\n"))
                .withArrayIndenter(new DefaultIndenter("  ", "\n"));
        return mapper.writer(printer).writeValueAsString(node) + "\n";
    }
}
