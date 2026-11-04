package mz.co.moseguros.proposal.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Regra {@code SUITABLE | WARNED} (S4-02 §8), função pura: compara as respostas do questionário com
 * as coberturas e o tipo de produto da proposta. Os motivos avaliam-se pela ordem de
 * {@link SuitabilityReason}; cada um entra na lista se a sua condição for verdadeira.
 */
public final class SuitabilityEvaluator {

    private SuitabilityEvaluator() {
    }

    /** Resultado e motivos (vazio em {@code SUITABLE}). */
    public record Evaluation(SuitabilityResult result, List<SuitabilityReason> reasons) {

        public Evaluation {
            reasons = List.copyOf(reasons);
        }
    }

    /**
     * @param coverages   coberturas da proposta
     * @param productType tipo do produto ({@code BORDER} cobre fora do país)
     * @param answers     uma resposta válida por pergunta ({@link SuitabilityQuestion#accepts})
     */
    public static Evaluation evaluate(List<CoverageLine> coverages, String productType,
                                      Map<SuitabilityQuestion, String> answers) {
        Objects.requireNonNull(coverages, "coverages");
        Objects.requireNonNull(answers, "answers");
        Set<String> codes = coverages.stream().map(CoverageLine::code).collect(Collectors.toSet());
        boolean hasOd = coverages.stream().anyMatch(CoverageLine::isOwnDamage);
        boolean collision = codes.contains("OD_COLLISION");
        String need = answers.get(SuitabilityQuestion.SUIT_NEED);

        List<SuitabilityReason> reasons = new ArrayList<>();
        if (("OWN_DAMAGE".equals(need) || "FULL".equals(need)) && !collision) {
            reasons.add(SuitabilityReason.NEED_NOT_COVERED_OWN_DAMAGE);
        }
        if ("FULL".equals(need) && !codes.containsAll(List.of("OD_THEFT", "OD_FIRE", "OD_NATURE"))) {
            reasons.add(SuitabilityReason.NEED_NOT_COVERED_THEFT_FIRE);
        }
        if ("MANDATORY_ONLY".equals(need) && hasOd) {
            reasons.add(SuitabilityReason.COVER_ABOVE_NEED);
        }
        if ("UNSURE".equals(need)) {
            reasons.add(SuitabilityReason.NEED_UNSURE);
        }
        if ("YES".equals(answers.get(SuitabilityQuestion.SUIT_FINANCED)) && !collision) {
            reasons.add(SuitabilityReason.FINANCED_WITHOUT_OWN_DAMAGE);
        }
        if ("NO".equals(answers.get(SuitabilityQuestion.SUIT_CAN_AFFORD_REPAIR)) && !collision) {
            reasons.add(SuitabilityReason.CANNOT_AFFORD_REPAIR);
        }
        if ("YES".equals(answers.get(SuitabilityQuestion.SUIT_CROSS_BORDER)) && !codes.contains("CROSS_BORDER")
                && !"BORDER".equals(productType)) {
            reasons.add(SuitabilityReason.CROSS_BORDER_NOT_COVERED);
        }
        if ("NO".equals(answers.get(SuitabilityQuestion.SUIT_DEDUCTIBLE))
                && coverages.stream().anyMatch(c -> c.isOwnDamage() && !c.deductible().isNone())) {
            reasons.add(SuitabilityReason.DEDUCTIBLE_NOT_WANTED);
        }
        return new Evaluation(reasons.isEmpty() ? SuitabilityResult.SUITABLE : SuitabilityResult.WARNED, reasons);
    }
}
