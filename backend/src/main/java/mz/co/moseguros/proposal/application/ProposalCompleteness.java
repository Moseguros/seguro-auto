package mz.co.moseguros.proposal.application;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import mz.co.moseguros.proposal.domain.Proposal;
import mz.co.moseguros.proposal.domain.SuitabilityResult;

/**
 * O que falta para a proposta poder ser assinada ou submetida (S4-04 §6). Função pura, usada no pedido
 * de assinatura (sem o item {@link Item#SIGNATURE}) e na submissão (S4-05, com ele). A lista sai pela
 * ordem da tabela da ficha.
 */
public final class ProposalCompleteness {

    /** Itens que podem faltar ({@code missing} do erro {@code PROPOSAL_INCOMPLETE}). */
    public enum Item {
        /** {@code declarations_confirmed_at is null}. */
        DECLARATIONS,
        /** {@code suitability_result is null}. */
        SUITABILITY,
        /** {@code WARNED} sem {@code suitability_acknowledged_at}. */
        SUITABILITY_ACKNOWLEDGEMENT,
        /** Vistoria obrigatória e não {@code SUBMITTED} nem {@code APPROVED} (S4-03). */
        INSPECTION,
        /** {@code start_at} antes do início do dia de hoje em Maputo. */
        START_DATE,
        /** Sem assinatura válida (só na submissão, S4-05). */
        SIGNATURE
    }

    /** Estados da vistoria (S4-03) que contam como feita. */
    static final Set<String> INSPECTION_DONE = Set.of("SUBMITTED", "APPROVED");

    /**
     * Factos da proposta usados na verificação.
     *
     * @param inspectionStatus estado da vistoria do S4-03 ({@code PENDING}, {@code SUBMITTED},
     *                         {@code APPROVED}, {@code REJECTED}) ou {@code null} sem vistoria
     * @param signed           há uma assinatura válida ({@code current_signature_id})
     */
    public record Facts(Instant declarationsConfirmedAt, SuitabilityResult suitabilityResult,
                        Instant suitabilityAcknowledgedAt, boolean inspectionRequired, String inspectionStatus,
                        Instant startAt, boolean signed) {

        public Facts {
            Objects.requireNonNull(startAt, "startAt");
        }

        /** Factos da proposta, com o estado da vistoria lido do S4-03 ({@code null} se não houver). */
        public static Facts of(Proposal p, String inspectionStatus) {
            return new Facts(p.getDeclarationsConfirmedAt(), p.getSuitabilityResult(),
                    p.getSuitabilityAcknowledgedAt(), p.isInspectionRequired(), inspectionStatus, p.getStartAt(),
                    p.getCurrentSignature() != null);
        }
    }

    private ProposalCompleteness() {
    }

    /**
     * Itens em falta, pela ordem da tabela do S4-04 §6.
     *
     * @param includeSignature {@code false} no pedido de assinatura, {@code true} na submissão
     */
    public static List<Item> missing(Facts facts, Instant now, boolean includeSignature) {
        List<Item> missing = new ArrayList<>();
        if (facts.declarationsConfirmedAt() == null) {
            missing.add(Item.DECLARATIONS);
        }
        if (facts.suitabilityResult() == null) {
            missing.add(Item.SUITABILITY);
        }
        if (facts.suitabilityResult() == SuitabilityResult.WARNED && facts.suitabilityAcknowledgedAt() == null) {
            missing.add(Item.SUITABILITY_ACKNOWLEDGEMENT);
        }
        if (facts.inspectionRequired()
                && (facts.inspectionStatus() == null || !INSPECTION_DONE.contains(facts.inspectionStatus()))) {
            missing.add(Item.INSPECTION);
        }
        Instant startOfToday = LocalDate.ofInstant(now, Proposal.ZONE).atStartOfDay(Proposal.ZONE).toInstant();
        if (facts.startAt().isBefore(startOfToday)) {
            missing.add(Item.START_DATE);
        }
        if (includeSignature && !facts.signed()) {
            missing.add(Item.SIGNATURE);
        }
        return List.copyOf(missing);
    }
}
