package mz.co.moseguros.proposal;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Estados da proposta (S4-02 §3; decisão D4: não há {@code SUBMITTED}). Todos existem desde o S4-02;
 * as transições de cada tarefa estão em {@link #canTransitionTo}.
 *
 * <ul>
 *   <li>{@link #DRAFT} e {@link #INFO_REQUESTED}: editáveis pelo cliente (e pelo mediador, S4-06);</li>
 *   <li>{@link #UNDER_REVIEW}: à espera da seguradora (não expira);</li>
 *   <li>{@link #ACCEPTED}: pagável (S5);</li>
 *   <li>{@link #REJECTED}, {@link #EXPIRED} e {@link #CONVERTED}: finais.</li>
 * </ul>
 */
@Schema(enumAsRef = true) // um só tipo nos clientes gerados
public enum ProposalStatus {
    DRAFT,
    UNDER_REVIEW,
    INFO_REQUESTED,
    ACCEPTED,
    REJECTED,
    EXPIRED,
    PAID,
    CONVERTED;

    private static final Map<ProposalStatus, Set<ProposalStatus>> TRANSITIONS = Map.of(
            DRAFT, EnumSet.of(ACCEPTED, UNDER_REVIEW, EXPIRED),
            INFO_REQUESTED, EnumSet.of(ACCEPTED, UNDER_REVIEW, EXPIRED),
            UNDER_REVIEW, EnumSet.of(ACCEPTED, REJECTED, INFO_REQUESTED),
            ACCEPTED, EnumSet.of(EXPIRED, PAID),
            PAID, EnumSet.of(CONVERTED),
            REJECTED, EnumSet.noneOf(ProposalStatus.class),
            EXPIRED, EnumSet.noneOf(ProposalStatus.class),
            CONVERTED, EnumSet.noneOf(ProposalStatus.class));

    /** {@code DRAFT} ou {@code INFO_REQUESTED}: o cliente e o mediador podem alterar o conteúdo. */
    public boolean isEditable() {
        return this == DRAFT || this == INFO_REQUESTED;
    }

    /** {@code REJECTED}, {@code EXPIRED} ou {@code CONVERTED}. */
    public boolean isFinal() {
        return this == REJECTED || this == EXPIRED || this == CONVERTED;
    }

    /** {@code DRAFT}, {@code INFO_REQUESTED} ou {@code ACCEPTED}: estados com prazo de expiração. */
    public boolean expires() {
        return this == DRAFT || this == INFO_REQUESTED || this == ACCEPTED;
    }

    /** A transição está na tabela do S4-02 §3. */
    public boolean canTransitionTo(ProposalStatus target) {
        return TRANSITIONS.get(this).contains(target);
    }
}
