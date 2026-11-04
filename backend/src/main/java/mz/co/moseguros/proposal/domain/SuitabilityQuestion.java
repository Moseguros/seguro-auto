package mz.co.moseguros.proposal.domain;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Questionário de adequação (S4-02 §8; dever de adequação do DL 1/2026): perguntas fixas, todas
 * obrigatórias. Os textos (provisórios, a validar juridicamente) estão nos frontends e no PDF.
 * Exportado para {@code api/proposal-questions.json}.
 */
@Schema(enumAsRef = true)
public enum SuitabilityQuestion {
    SUIT_NEED(Type.SINGLE_CHOICE, List.of("MANDATORY_ONLY", "OWN_DAMAGE", "FULL", "UNSURE")),
    SUIT_FINANCED(Type.YES_NO, Type.YES_NO.options),
    SUIT_CAN_AFFORD_REPAIR(Type.YES_NO_UNSURE, Type.YES_NO_UNSURE.options),
    SUIT_CROSS_BORDER(Type.YES_NO, Type.YES_NO.options),
    SUIT_DEDUCTIBLE(Type.YES_NO_UNSURE, Type.YES_NO_UNSURE.options);

    /** Tipo de resposta. */
    public enum Type {
        SINGLE_CHOICE(List.of()),
        YES_NO(List.of("YES", "NO")),
        YES_NO_UNSURE(List.of("YES", "NO", "UNSURE"));

        private final List<String> options;

        Type(List<String> options) {
            this.options = options;
        }
    }

    private final Type type;
    private final List<String> options;

    SuitabilityQuestion(Type type, List<String> options) {
        this.type = type;
        this.options = List.copyOf(options);
    }

    public Type type() {
        return type;
    }

    /** Respostas permitidas, pela ordem em que se mostram. */
    public List<String> options() {
        return options;
    }

    public boolean accepts(String answer) {
        return answer != null && options.contains(answer);
    }
}
