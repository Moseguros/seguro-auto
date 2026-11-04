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
 * Resposta a uma declaração de risco ({@code proposal.declaration}, S4-02 §4 e §7). Uma linha por
 * pergunta e proposta; uma nova gravação actualiza as linhas (o conjunto de perguntas é fixo, nunca
 * há linhas a apagar). O {@code insurer_id} é sempre o da proposta.
 */
@Entity
@Table(schema = "proposal", name = "declaration")
public class Declaration extends BaseEntity {

    /** Campo de seguimento: número de sinistros (1–20). */
    public static final String CLAIMS_COUNT = "claimsCount";
    /** Campo de seguimento: descrição (10–500 caracteres). */
    public static final String DETAILS = "details";
    public static final int MIN_CLAIMS = 1;
    public static final int MAX_CLAIMS = 20;
    public static final int DETAILS_MIN = 10;
    public static final int DETAILS_MAX = 500;

    @Column(name = "insurer_id", nullable = false, updatable = false)
    private UUID insurerId;

    @Column(name = "proposal_id", nullable = false, updatable = false)
    private UUID proposalId;

    @Enumerated(EnumType.STRING)
    @Column(name = "question_code", nullable = false, updatable = false, length = 30)
    private DeclarationQuestion questionCode;

    @Column(name = "answer", nullable = false)
    private boolean answer;

    @Column(name = "claims_count")
    private Integer claimsCount;

    @Column(name = "details", length = 500)
    private String details;

    protected Declaration() {
    }

    /** Primeira resposta à pergunta, com o {@code insurer_id} da proposta. */
    public static Declaration of(Proposal proposal, DeclarationQuestion question, boolean answer,
                                 Integer claimsCount, String details) {
        Declaration d = new Declaration();
        d.insurerId = proposal.getInsurerId();
        d.proposalId = proposal.getId();
        d.questionCode = Objects.requireNonNull(question, "question");
        d.replace(answer, claimsCount, details);
        return d;
    }

    /** Substitui a resposta (já validada). */
    public void replace(boolean answer, Integer claimsCount, String details) {
        this.answer = answer;
        this.claimsCount = claimsCount;
        this.details = details;
    }

    public UUID getInsurerId() {
        return insurerId;
    }

    public UUID getProposalId() {
        return proposalId;
    }

    public DeclarationQuestion getQuestionCode() {
        return questionCode;
    }

    public boolean getAnswer() {
        return answer;
    }

    public Integer getClaimsCount() {
        return claimsCount;
    }

    public String getDetails() {
        return details;
    }
}
