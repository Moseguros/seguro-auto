package mz.co.moseguros.proposal.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import mz.co.moseguros.proposal.domain.CoverageLine.Deductible;
import mz.co.moseguros.proposal.domain.SuitabilityEvaluator.Evaluation;
import mz.co.moseguros.shared.domain.Money;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/** Os 7 casos de referência da regra {@code SUITABLE | WARNED} (S4-02 §8). */
class SuitabilityEvaluatorTest {

    static final Deductible FIXED = new Deductible(Deductible.FIXED, Money.ofCentavos(500_000), null, null);

    static CoverageLine line(String code, Deductible deductible) {
        return new CoverageLine(code, code.equals("MTPL"), Money.ofCentavos(100_000_000), deductible,
                Money.ofCentavos(100_000));
    }

    static List<CoverageLine> lines(Deductible deductible, String... codes) {
        return Arrays.stream(codes).map(c -> line(c, c.startsWith("OD_") ? deductible : Deductible.none())).toList();
    }

    static Map<SuitabilityQuestion, String> answers(String need, String financed, String canAfford, String crossBorder,
                                                    String deductible) {
        Map<SuitabilityQuestion, String> answers = new EnumMap<>(SuitabilityQuestion.class);
        answers.put(SuitabilityQuestion.SUIT_NEED, need);
        answers.put(SuitabilityQuestion.SUIT_FINANCED, financed);
        answers.put(SuitabilityQuestion.SUIT_CAN_AFFORD_REPAIR, canAfford);
        answers.put(SuitabilityQuestion.SUIT_CROSS_BORDER, crossBorder);
        answers.put(SuitabilityQuestion.SUIT_DEDUCTIBLE, deductible);
        return answers;
    }

    static Stream<Arguments> referenceCases() {
        return Stream.of(
                Arguments.of("1", lines(Deductible.none(), "MTPL"), "MTPL_ONLY",
                        answers("MANDATORY_ONLY", "NO", "YES", "NO", "UNSURE"), SuitabilityResult.SUITABLE, List.of()),
                Arguments.of("2", lines(Deductible.none(), "MTPL"), "MTPL_ONLY",
                        answers("OWN_DAMAGE", "YES", "NO", "NO", "YES"), SuitabilityResult.WARNED,
                        List.of(SuitabilityReason.NEED_NOT_COVERED_OWN_DAMAGE,
                                SuitabilityReason.FINANCED_WITHOUT_OWN_DAMAGE, SuitabilityReason.CANNOT_AFFORD_REPAIR)),
                Arguments.of("3", lines(FIXED, "MTPL", "OD_COLLISION", "OD_GLASS"), "COMPREHENSIVE",
                        answers("FULL", "NO", "YES", "NO", "NO"), SuitabilityResult.WARNED,
                        List.of(SuitabilityReason.NEED_NOT_COVERED_THEFT_FIRE, SuitabilityReason.DEDUCTIBLE_NOT_WANTED)),
                Arguments.of("4", lines(Deductible.none(), "MTPL", "OD_COLLISION", "OD_FIRE", "OD_NATURE", "OD_THEFT"),
                        "COMPREHENSIVE", answers("FULL", "YES", "NO", "NO", "NO"), SuitabilityResult.SUITABLE,
                        List.of()),
                Arguments.of("5", lines(Deductible.none(), "MTPL", "OD_COLLISION"), "COMPREHENSIVE",
                        answers("MANDATORY_ONLY", "NO", "YES", "YES", "YES"), SuitabilityResult.WARNED,
                        List.of(SuitabilityReason.COVER_ABOVE_NEED, SuitabilityReason.CROSS_BORDER_NOT_COVERED)),
                Arguments.of("6", lines(Deductible.none(), "MTPL"), "BORDER",
                        answers("MANDATORY_ONLY", "NO", "YES", "YES", "YES"), SuitabilityResult.SUITABLE, List.of()),
                Arguments.of("7", lines(Deductible.none(), "MTPL"), "MTPL_ONLY",
                        answers("UNSURE", "NO", "UNSURE", "NO", "UNSURE"), SuitabilityResult.WARNED,
                        List.of(SuitabilityReason.NEED_UNSURE)));
    }

    @ParameterizedTest(name = "caso {0}")
    @MethodSource("referenceCases")
    void referenceCase(String name, List<CoverageLine> coverages, String productType,
                       Map<SuitabilityQuestion, String> answers, SuitabilityResult result,
                       List<SuitabilityReason> reasons) {
        Evaluation evaluation = SuitabilityEvaluator.evaluate(coverages, productType, answers);
        assertThat(evaluation.result()).isEqualTo(result);
        assertThat(evaluation.reasons()).containsExactlyElementsOf(reasons);
    }

    @ParameterizedTest(name = "cross border {0}")
    @MethodSource("crossBorderCases")
    void crossBorderCoverageAlsoCoversAbroad(String name, List<CoverageLine> coverages, boolean warned) {
        Evaluation e = SuitabilityEvaluator.evaluate(coverages, "COMPREHENSIVE",
                answers("MANDATORY_ONLY", "NO", "YES", "YES", "YES"));
        assertThat(e.reasons().contains(SuitabilityReason.CROSS_BORDER_NOT_COVERED)).isEqualTo(warned);
    }

    static Stream<Arguments> crossBorderCases() {
        return Stream.of(Arguments.of("com CROSS_BORDER", lines(Deductible.none(), "MTPL", "CROSS_BORDER"), false),
                Arguments.of("sem CROSS_BORDER", lines(Deductible.none(), "MTPL"), true));
    }
}
