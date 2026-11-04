package mz.co.moseguros.proposal.domain;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Declarações de risco (S4-02 §7): perguntas fixas, todas obrigatórias, de resposta sim/não. Com
 * "sim", os campos de seguimento ({@link #followUp}) são obrigatórios. Os textos (provisórios, a
 * validar juridicamente) estão nos frontends e no PDF, não aqui: mudar um texto não muda o código.
 * Exportadas para {@code api/proposal-questions.json}.
 */
@Schema(enumAsRef = true)
public enum DeclarationQuestion {
    CLAIMS_LAST_3_YEARS(List.of(Declaration.CLAIMS_COUNT, Declaration.DETAILS)),
    PREVIOUS_REFUSAL(List.of(Declaration.DETAILS)),
    USAGE_DIFFERENT(List.of(Declaration.DETAILS)),
    VEHICLE_MODIFIED(List.of(Declaration.DETAILS)),
    NOVICE_DRIVERS(List.of(Declaration.DETAILS));

    private final List<String> followUp;

    DeclarationQuestion(List<String> followUp) {
        this.followUp = followUp;
    }

    /** Campos obrigatórios com resposta "sim" ({@code claimsCount}, {@code details}). */
    public List<String> followUp() {
        return followUp;
    }

    public boolean requiresClaimsCount() {
        return followUp.contains(Declaration.CLAIMS_COUNT);
    }
}
