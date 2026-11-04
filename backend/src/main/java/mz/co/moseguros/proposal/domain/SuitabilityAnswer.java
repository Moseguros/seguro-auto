package mz.co.moseguros.proposal.domain;

import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import mz.co.moseguros.shared.persistence.BaseEntity;

/**
 * Resposta a uma pergunta do questionário de adequação ({@code proposal.suitability_answer},
 * S4-02 §4 e §8). Uma linha por pergunta e proposta, actualizada em cada gravação. O
 * {@code insurer_id} é sempre o da proposta.
 */
@Entity
@Table(schema = "proposal", name = "suitability_answer")
public class SuitabilityAnswer extends BaseEntity {

    @Column(name = "insurer_id", nullable = false, updatable = false)
    private UUID insurerId;

    @Column(name = "proposal_id", nullable = false, updatable = false)
    private UUID proposalId;

    @Enumerated(EnumType.STRING)
    @Column(name = "question_code", nullable = false, updatable = false, length = 30)
    private SuitabilityQuestion questionCode;

    @Column(name = "answer", nullable = false, length = 20)
    private String answer;

    protected SuitabilityAnswer() {
    }

    /** Primeira resposta à pergunta, com o {@code insurer_id} da proposta. */
    public static SuitabilityAnswer of(Proposal proposal, SuitabilityQuestion question, String answer) {
        SuitabilityAnswer a = new SuitabilityAnswer();
        a.insurerId = proposal.getInsurerId();
        a.proposalId = proposal.getId();
        a.questionCode = Objects.requireNonNull(question, "question");
        a.replace(answer);
        return a;
    }

    /** Substitui a resposta (já validada com {@link SuitabilityQuestion#accepts}). */
    public void replace(String answer) {
        if (!questionCode.accepts(answer)) {
            throw new IllegalArgumentException("Invalid answer for " + questionCode);
        }
        this.answer = answer;
    }

    public UUID getInsurerId() {
        return insurerId;
    }

    public UUID getProposalId() {
        return proposalId;
    }

    public SuitabilityQuestion getQuestionCode() {
        return questionCode;
    }

    public String getAnswer() {
        return answer;
    }
}
