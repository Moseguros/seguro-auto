package mz.co.moseguros.rating.domain.tariff;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.fasterxml.jackson.databind.JsonNode;
import mz.co.moseguros.rating.domain.tariff.EvaluationResult.Offered;
import mz.co.moseguros.rating.domain.tariff.EvaluationResult.Unavailable;
import mz.co.moseguros.rating.domain.tariff.PremiumBreakdown.ChargeLine;
import mz.co.moseguros.rating.domain.tariff.PremiumBreakdown.CoverageLine;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Os 10 casos de {@code docs/tasks/S3-rating-cases.md} (S3-01, critério de aceitação): estado,
 * motivo, prémio de cada cobertura, encargos, prémio comercial, ajuste e total exactos.
 */
class ReferenceCasesTest {

    static Stream<Arguments> cases() {
        return StreamSupport.stream(ReferenceData.json("cases.json").spliterator(), false)
                .map(c -> Arguments.of(c.get("id").asText(), c));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("cases")
    void evaluatesExactlyAsTheReferenceCase(String id, JsonNode testCase) {
        String product = testCase.get("product").asText();
        EvaluationResult result = TariffEvaluator.evaluate(ReferenceData.input(testCase), ReferenceData.product(product),
                ReferenceData.tariff(product), ReferenceData.charges());
        JsonNode expected = testCase.get("expected");

        if ("NOT_AVAILABLE".equals(expected.get("status").asText())) {
            assertThat(result).isInstanceOf(Unavailable.class);
            Unavailable unavailable = (Unavailable) result;
            assertThat(unavailable.reason().name()).isEqualTo(expected.get("reason").asText());
            Map<String, Object> detail = new LinkedHashMap<>();
            expected.get("detail").fields().forEachRemaining(e -> detail.put(e.getKey(), e.getValue().asText()));
            assertThat(unavailable.detail()).isEqualTo(detail);
            return;
        }

        assertThat(expected.get("status").asText()).isEqualTo("OFFERED");
        assertThat(result).isInstanceOf(Offered.class);
        PremiumBreakdown b = ((Offered) result).breakdown();
        List<CoverageLine> coverages = b.coverages();
        JsonNode expectedCoverages = expected.get("coverages");
        assertThat(coverages).hasSize(expectedCoverages.size());
        for (int i = 0; i < coverages.size(); i++) {
            JsonNode e = expectedCoverages.get(i);
            CoverageLine line = coverages.get(i);
            assertThat(line.code()).as(id + " coverage " + i).isEqualTo(e.get("code").asText());
            assertThat(line.exactPremium()).as(id + " " + line.code() + " exactPremium")
                    .isEqualByComparingTo(new BigDecimal(e.get("exactPremium").asText()));
            assertThat(line.premiumAmount()).as(id + " " + line.code() + " premiumAmount")
                    .isEqualTo(e.get("premiumAmount").asLong());
        }
        assertThat(b.commercialPremiumAmount()).isEqualTo(expected.get("commercialPremiumAmount").asLong());
        List<String> charges = new ArrayList<>();
        for (ChargeLine c : b.charges()) {
            charges.add(c.code() + "=" + c.amount());
        }
        List<String> expectedCharges = new ArrayList<>();
        expected.get("charges").forEach(c -> expectedCharges.add(c.get("code").asText() + "=" + c.get("amount").asLong()));
        assertThat(charges).isEqualTo(expectedCharges);
        assertThat(b.subtotalAmount()).isEqualTo(expected.get("subtotalAmount").asLong());
        assertThat(b.roundingAdjustmentAmount()).isEqualTo(expected.get("roundingAdjustmentAmount").asLong());
        assertThat(b.totalAmount()).isEqualTo(expected.get("totalAmount").asLong());
        assertThat(b.totalAmount() % 100).isZero();
    }

    /** O mesmo pedido avaliado duas vezes dá o mesmo resultado (nada depende da hora actual). */
    @ParameterizedTest(name = "{0}")
    @MethodSource("cases")
    void isDeterministic(String id, JsonNode testCase) {
        String product = testCase.get("product").asText();
        EvaluationResult first = TariffEvaluator.evaluate(ReferenceData.input(testCase), ReferenceData.product(product),
                ReferenceData.tariff(product), ReferenceData.charges());
        EvaluationResult second = TariffEvaluator.evaluate(ReferenceData.input(testCase),
                ReferenceData.product(product), ReferenceData.tariff(product), ReferenceData.charges());
        assertThat(second).isEqualTo(first);
    }

    /** Os ficheiros de teste são cópias exactas dos blocos JSON do documento dos casos. */
    @Test
    void referenceFilesAreCopiesOfTheMarkdown() throws Exception {
        Path md = Path.of("..", "docs", "tasks", "S3-rating-cases.md");
        String text = Files.readString(md, StandardCharsets.UTF_8);
        Matcher m = Pattern.compile("```json\\R(.*?)```", Pattern.DOTALL).matcher(text);
        List<JsonNode> blocks = new ArrayList<>();
        while (m.find()) {
            String block = m.group(1);
            blocks.add(block.contains("<entrada") ? null : ReferenceData.MAPPER.readTree(block));
        }
        assertThat(ReferenceData.json("tariff-dp.json")).isEqualTo(blocks.get(0));
        assertThat(ReferenceData.json("tariff-temp.json")).isEqualTo(blocks.get(2));
        assertThat(ReferenceData.json("charges.json")).isEqualTo(blocks.get(3));
        assertThat(ReferenceData.json("products.json")).isEqualTo(blocks.get(4));
        assertThat(ReferenceData.json("cases.json")).isEqualTo(blocks.get(5));
        JsonNode rc = ReferenceData.json("tariff-rc.json");
        assertThat(rc.get("schemaVersion")).isEqualTo(blocks.get(0).get("schemaVersion"));
        assertThat(rc.has("durationFactors")).isFalse();
        assertThat(rc.get("coverages")).containsExactly(blocks.get(0).get("coverages").get(0),
                blocks.get(0).get("coverages").get(1));
    }
}
