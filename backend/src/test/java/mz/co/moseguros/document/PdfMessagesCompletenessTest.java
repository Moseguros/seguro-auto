package mz.co.moseguros.document;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import mz.co.moseguros.catalog.CoverageCode;
import org.junit.jupiter.api.Test;

/**
 * Os textos dos PDF (S4-04 §3) têm todas as chaves das perguntas, respostas e avisos de
 * {@code api/proposal-questions.json} e dos 12 códigos de cobertura, em {@code pdf_pt.properties} e
 * {@code pdf_en.properties}; os dois ficheiros têm as mesmas chaves.
 */
class PdfMessagesCompletenessTest {

    static final Path QUESTIONS = Path.of("../api/proposal-questions.json").toAbsolutePath().normalize();

    @Test
    void portugueseHasEveryRequiredKey() throws IOException {
        assertThat(missing(load("pt"), requiredKeys())).isEmpty();
    }

    @Test
    void englishHasEveryRequiredKey() throws IOException {
        assertThat(missing(load("en"), requiredKeys())).isEmpty();
    }

    @Test
    void bothLanguagesHaveTheSameKeys() throws IOException {
        assertThat(new TreeSet<>(load("en").stringPropertyNames()))
                .isEqualTo(new TreeSet<>(load("pt").stringPropertyNames()));
    }

    @Test
    void removingAKeyFromEnglishIsDetected() throws IOException {
        Properties en = load("en");
        en.remove("pdf.suitability.reason.NEED_UNSURE");
        en.remove("pdf.coverage.OD_GLASS");

        assertThat(missing(en, requiredKeys()))
                .containsExactly("pdf.coverage.OD_GLASS", "pdf.suitability.reason.NEED_UNSURE");
    }

    @Test
    void theTwelveCoverageCodesAreRequired() throws IOException {
        assertThat(requiredKeys().stream().filter(k -> k.startsWith("pdf.coverage."))).hasSize(12);
    }

    /** Chaves obrigatórias, por ordem alfabética. */
    static Set<String> requiredKeys() throws IOException {
        JsonNode questions = new ObjectMapper().readTree(Files.readString(QUESTIONS));
        Set<String> keys = new TreeSet<>();
        questions.path("declarations").forEach(q -> keys.add("pdf.declaration." + q.path("code").asText()));
        questions.path("suitability").forEach(q -> {
            keys.add("pdf.suitability." + q.path("code").asText());
            q.path("options").forEach(o -> keys.add("pdf.suitability.answer." + o.asText()));
        });
        questions.path("suitabilityReasons").forEach(r -> keys.add("pdf.suitability.reason." + r.asText()));
        Arrays.stream(CoverageCode.values()).forEach(c -> keys.add("pdf.coverage." + c.name()));
        assertThat(keys).as("api/proposal-questions.json must not be empty").hasSizeGreaterThan(12);
        return keys;
    }

    static List<String> missing(Properties texts, Set<String> required) {
        List<String> missing = new ArrayList<>();
        for (String key : required) {
            String value = texts.getProperty(key);
            if (value == null || value.isBlank()) {
                missing.add(key);
            }
        }
        return missing;
    }

    static Properties load(String language) throws IOException {
        Properties properties = new Properties();
        try (InputStream in = PdfMessagesCompletenessTest.class.getClassLoader()
                .getResourceAsStream("messages/pdf_" + language + ".properties")) {
            assertThat(in).as("messages/pdf_" + language + ".properties").isNotNull();
            properties.load(new InputStreamReader(in, StandardCharsets.UTF_8));
        }
        return properties;
    }
}
