package mz.co.moseguros.proposal.application;

import java.time.Clock;
import java.time.Instant;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import mz.co.moseguros.proposal.domain.Proposal;
import mz.co.moseguros.proposal.domain.ProposalRepository;
import mz.co.moseguros.proposal.domain.SuitabilityAnswer;
import mz.co.moseguros.proposal.domain.SuitabilityAnswerRepository;
import mz.co.moseguros.proposal.domain.SuitabilityEvaluator;
import mz.co.moseguros.proposal.domain.SuitabilityQuestion;
import mz.co.moseguros.shared.audit.AuditRecord;
import mz.co.moseguros.shared.audit.AuditService;
import mz.co.moseguros.shared.error.FieldErrorCodes;
import mz.co.moseguros.shared.security.CurrentUser;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Questionário de adequação da proposta (S4-02 §8 e §9): grava as respostas, calcula
 * {@code SUITABLE | WARNED} com o {@link SuitabilityEvaluator} e regista a confirmação consciente
 * dos avisos.
 */
@Service
public class SuitabilityService {

    private final ProposalQueryService queries;
    private final ProposalRepository proposals;
    private final SuitabilityAnswerRepository answers;
    private final ProposalService proposalService;
    private final ProposalJson json;
    private final AuditService audit;
    private final CurrentUser currentUser;
    private final ProposalProperties properties;
    private final Clock clock;

    public SuitabilityService(ProposalQueryService queries, ProposalRepository proposals,
                              SuitabilityAnswerRepository answers, ProposalService proposalService, ProposalJson json,
                              AuditService audit, CurrentUser currentUser, ProposalProperties properties, Clock clock) {
        this.queries = queries;
        this.proposals = proposals;
        this.answers = answers;
        this.proposalService = proposalService;
        this.json = json;
        this.audit = audit;
        this.currentUser = currentUser;
        this.properties = properties;
        this.clock = clock;
    }

    /** Uma resposta do pedido (códigos em texto: desconhecidos dão {@code UNKNOWN_QUESTION}). */
    public record AnswerInput(String questionCode, String answer) {
    }

    /**
     * Grava as respostas e o resultado; limpa uma confirmação anterior dos avisos. Proposta editável
     * (409 {@code PROPOSAL_NOT_EDITABLE}), versão igual (409 {@code CONCURRENT_MODIFICATION}), respostas
     * completas e válidas (400 {@code VALIDATION_ERROR}).
     */
    @PreAuthorize("hasAnyAuthority('self:proposals','proposal:create-for-client')")
    @Transactional
    public ProposalDetails save(UUID proposalId, List<AnswerInput> input, Long version) {
        Proposal proposal = queries.accessible(proposalId);
        proposal.requireEditable();
        DeclarationService.requireVersion(proposal, version);
        Map<SuitabilityQuestion, String> valid = validate(input);

        Map<SuitabilityQuestion, SuitabilityAnswer> existing = new EnumMap<>(SuitabilityQuestion.class);
        for (SuitabilityAnswer a : answers.findByProposalId(proposal.getId())) {
            existing.put(a.getQuestionCode(), a);
        }
        valid.forEach((question, answer) -> {
            SuitabilityAnswer current = existing.get(question);
            if (current == null) {
                answers.save(SuitabilityAnswer.of(proposal, question, answer));
            } else {
                current.replace(answer);
            }
        });
        SuitabilityEvaluator.Evaluation evaluation = SuitabilityEvaluator.evaluate(
                json.coverages(proposal.getCoverages()), proposal.getProductType().name(), valid);
        Instant now = clock.instant();
        proposal.recordSuitability(evaluation, now, properties.expiry());
        proposals.saveAndFlush(proposal);
        answers.flush();

        Map<String, Object> state = new LinkedHashMap<>();
        Map<String, String> answered = new LinkedHashMap<>();
        valid.forEach((q, a) -> answered.put(q.name(), a));
        state.put("answers", answered);
        state.put("result", evaluation.result().name());
        state.put("reasons", evaluation.reasons().stream().map(Enum::name).toList());
        state.put("contentRevision", proposal.getContentRevision());
        audit.record(new AuditRecord(proposalService.actorType(), currentUser.id().toString(), proposal.getInsurerId(),
                "PROPOSAL.SUITABILITY_SAVE", "Proposal", proposal.getId().toString(), null, state));
        return queries.details(proposal);
    }

    /**
     * O cliente leu os avisos e quer continuar: {@code suitability_acknowledged_at = agora}. Sem
     * {@code WARNED} → 409 {@code SUITABILITY_NOT_WARNED}.
     */
    @PreAuthorize("hasAnyAuthority('self:proposals','proposal:create-for-client')")
    @Transactional
    public ProposalDetails acknowledge(UUID proposalId, Long version) {
        Proposal proposal = queries.accessible(proposalId);
        proposal.requireEditable();
        DeclarationService.requireVersion(proposal, version);
        proposal.acknowledgeSuitability(clock.instant(), properties.expiry());
        proposals.saveAndFlush(proposal);
        audit.record(new AuditRecord(proposalService.actorType(), currentUser.id().toString(), proposal.getInsurerId(),
                "PROPOSAL.SUITABILITY_ACKNOWLEDGE", "Proposal", proposal.getId().toString(), null,
                Map.of("acknowledgedAt", proposal.getSuitabilityAcknowledgedAt().toString(),
                        "contentRevision", proposal.getContentRevision())));
        return queries.details(proposal);
    }

    /** Uma resposta permitida por pergunta; senão todos os erros juntos. */
    static Map<SuitabilityQuestion, String> validate(List<AnswerInput> input) {
        FieldErrors errors = new FieldErrors();
        Map<SuitabilityQuestion, String> byQuestion = new EnumMap<>(SuitabilityQuestion.class);
        List<AnswerInput> list = input == null ? List.of() : input;
        for (int i = 0; i < list.size(); i++) {
            AnswerInput a = list.get(i);
            String prefix = "answers[" + i + "]";
            if (a == null) {
                errors.add(prefix, FieldErrorCodes.REQUIRED, "The answer is required");
                continue;
            }
            SuitabilityQuestion question = question(a.questionCode());
            if (question == null) {
                errors.add(prefix + ".questionCode", FieldErrorCodes.UNKNOWN_QUESTION, "Unknown question");
                continue;
            }
            if (byQuestion.containsKey(question)) {
                errors.add(prefix + ".questionCode", FieldErrorCodes.ANSWER_DUPLICATED, "Duplicated question");
                continue;
            }
            byQuestion.put(question, a.answer());
            if (!question.accepts(a.answer())) {
                errors.add(prefix + ".answer", FieldErrorCodes.INVALID_ANSWER,
                        "Allowed answers: " + question.options());
            }
        }
        if (byQuestion.size() < SuitabilityQuestion.values().length) {
            errors.add("answers", FieldErrorCodes.ANSWERS_INCOMPLETE, "Answer all the questions");
        }
        errors.throwIfAny();
        return byQuestion;
    }

    private static SuitabilityQuestion question(String code) {
        if (code == null) {
            return null;
        }
        try {
            return SuitabilityQuestion.valueOf(code);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
