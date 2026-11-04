package mz.co.moseguros.proposal.domain;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Estados da vistoria (S4-03 §4). {@link #PENDING} e {@link #REJECTED} são editáveis pelo cliente e
 * pelo mediador (com a proposta editável); {@link #SUBMITTED} espera pelo subscritor.
 */
@Schema(enumAsRef = true)
public enum InspectionStatus {
    PENDING,
    SUBMITTED,
    APPROVED,
    REJECTED;

    private static final Map<InspectionStatus, Set<InspectionStatus>> TRANSITIONS = Map.of(
            PENDING, EnumSet.of(SUBMITTED),
            REJECTED, EnumSet.of(PENDING, SUBMITTED),
            SUBMITTED, EnumSet.of(APPROVED, REJECTED, PENDING),
            APPROVED, EnumSet.of(PENDING));

    /** {@code PENDING} ou {@code REJECTED}: o cliente e o mediador podem alterar a vistoria. */
    public boolean isEditable() {
        return this == PENDING || this == REJECTED;
    }

    /** A transição está na tabela do S4-03 §4 (sem olhar para a acção que a provoca). */
    public boolean canTransitionTo(InspectionStatus target) {
        return TRANSITIONS.get(this).contains(target);
    }
}
