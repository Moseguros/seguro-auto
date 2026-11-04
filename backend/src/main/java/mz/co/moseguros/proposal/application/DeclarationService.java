package mz.co.moseguros.proposal.application;

import java.time.Clock;
import java.time.Instant;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import mz.co.moseguros.proposal.domain.Declaration;
import mz.co.moseguros.proposal.domain.DeclarationQuestion;
import mz.co.moseguros.proposal.domain.DeclarationRepository;
import mz.co.moseguros.proposal.domain.Proposal;
import mz.co.moseguros.proposal.domain.ProposalRepository;
import mz.co.moseguros.shared.audit.AuditRecord;
import mz.co.moseguros.shared.audit.AuditService;
import mz.co.moseguros.shared.error.FieldErrorCodes;
import mz.co.moseguros.shared.security.CurrentUser;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Declarações de risco da proposta (S4-02 §7 e §9, {@code saveProposalDeclarations}): as cinco
 * respostas, os campos de seguimento e a confirmação de veracidade. Cada gravação substitui as
 * respostas por inteiro.
 */
@Service
public class DeclarationService {

    private final ProposalQueryService queries;
    private final ProposalRepository proposals;
    private final DeclarationRepository declarations;
    private final ProposalService proposalService;
    private final AuditService audit;
    private final CurrentUser currentUser;
    private final ProposalProperties properties;
    private final Clock clock;

    public DeclarationService(ProposalQueryService queries, ProposalRepository proposals,
                              DeclarationRepository declarations, ProposalService proposalService, AuditService audit,
                              CurrentUser currentUser, ProposalProperties properties, Clock clock) {
        this.queries = queries;
        this.proposals = proposals;
        this.declarations = declarations;
        this.proposalService = proposalService;
        this.audit = audit;
        this.currentUser = currentUser;
        this.properties = properties;
        this.clock = clock;
    }

    /**
     * Uma resposta do pedido.
     *
     * @param questionCode texto: um código desconhecido dá {@code UNKNOWN_QUESTION}
     */
    public record AnswerInput(String questionCode, Boolean answer, Integer claimsCount, String details) {
    }

    /**
     * Grava as declarações: proposta editável (409 {@code PROPOSAL_NOT_EDITABLE}), versão igual (409
     * {@code CONCURRENT_MODIFICATION}) e as regras da tabela do S4-02 §9 (400 {@code VALIDATION_ERROR}).
     */
    @PreAuthorize("hasAnyAuthority('self:proposals','proposal:create-for-client')")
    @Transactional
    public ProposalDetails save(UUID proposalId, List<AnswerInput> answers, Boolean truthfulnessConfirmed,
                                Long version) {
        Proposal proposal = queries.accessible(proposalId);
        proposal.requireEditable();
        requireVersion(proposal, version);
        Map<DeclarationQuestion, AnswerInput> valid = validate(answers, truthfulnessConfirmed);

        Map<DeclarationQuestion, Declaration> existing = new EnumMap<>(DeclarationQuestion.class);
        for (Declaration d : declarations.findByProposalId(proposal.getId())) {
            existing.put(d.getQuestionCode(), d);
        }
        valid.forEach((question, a) -> {
            Declaration current = existing.get(question);
            if (current == null) {
                declarations.save(Declaration.of(proposal, question, a.answer(), a.claimsCount(), a.details()));
            } else {
                current.replace(a.answer(), a.claimsCount(), a.details());
            }
        });
        Instant now = clock.instant();
        proposal.confirmDeclarations(now, properties.expiry());
        proposals.saveAndFlush(proposal);
        declarations.flush();

        Map<String, Boolean> state = new LinkedHashMap<>();
        valid.forEach((q, a) -> state.put(q.name(), a.answer()));
        audit.record(new AuditRecord(proposalService.actorType(), currentUser.id().toString(), proposal.getInsurerId(),
                "PROPOSAL.DECLARATIONS_SAVE", "Proposal", proposal.getId().toString(), null,
                Map.of("answers", state, "contentRevision", proposal.getContentRevision())));
        return queries.details(proposal);
    }

    /** As cinco respostas válidas, pela ordem das perguntas; senão todos os erros juntos. */
    static Map<DeclarationQuestion, AnswerInput> validate(List<AnswerInput> answers, Boolean truthfulnessConfirmed) {
        FieldErrors errors = new FieldErrors();
        Map<DeclarationQuestion, AnswerInput> byQuestion = new EnumMap<>(DeclarationQuestion.class);
        List<AnswerInput> list = answers == null ? List.of() : answers;
        for (int i = 0; i < list.size(); i++) {
            AnswerInput a = list.get(i);
            String prefix = "answers[" + i + "]";
            if (a == null) {
                errors.add(prefix, FieldErrorCodes.REQUIRED, "The answer is required");
                continue;
            }
            DeclarationQuestion question = question(a.questionCode());
            if (question == null) {
                errors.add(prefix + ".questionCode", FieldErrorCodes.UNKNOWN_QUESTION, "Unknown question");
                continue;
            }
            if (byQuestion.containsKey(question)) {
                errors.add(prefix + ".questionCode", FieldErrorCodes.ANSWER_DUPLICATED, "Duplicated question");
                continue;
            }
            byQuestion.put(question, new AnswerInput(a.questionCode(), a.answer(), a.claimsCount(),
                    a.details() == null ? null : a.details().strip()));
            validateAnswer(question, a, prefix, errors);
        }
        if (byQuestion.size() < DeclarationQuestion.values().length) {
            errors.add("answers", FieldErrorCodes.ANSWERS_INCOMPLETE, "Answer all the questions");
        }
        if (!Boolean.TRUE.equals(truthfulnessConfirmed)) {
            errors.add("truthfulnessConfirmed", FieldErrorCodes.MUST_BE_TRUE, "The declaration must be confirmed");
        }
        errors.throwIfAny();
        return byQuestion;
    }

    private static void validateAnswer(DeclarationQuestion question, AnswerInput a, String prefix,
                                       FieldErrors errors) {
        if (a.answer() == null) {
            errors.add(prefix + ".answer", FieldErrorCodes.REQUIRED, "The answer is required");
            return;
        }
        String detailsField = prefix + "." + Declaration.DETAILS;
        String countField = prefix + "." + Declaration.CLAIMS_COUNT;
        if (!a.answer()) {
            if (a.details() != null) {
                errors.add(detailsField, FieldErrorCodes.NOT_ALLOWED, "No details with a 'no' answer");
            }
            if (a.claimsCount() != null) {
                errors.add(countField, FieldErrorCodes.NOT_ALLOWED, "No claims count with a 'no' answer");
            }
            return;
        }
        if (a.details() == null || a.details().isBlank()) {
            errors.add(detailsField, FieldErrorCodes.REQUIRED, "The details are required");
        } else if (a.details().strip().length() < Declaration.DETAILS_MIN) {
            errors.add(detailsField, FieldErrorCodes.TOO_SHORT,
                    "At least " + Declaration.DETAILS_MIN + " characters");
        } else if (a.details().strip().length() > Declaration.DETAILS_MAX) {
            errors.add(detailsField, FieldErrorCodes.TOO_LONG, "At most " + Declaration.DETAILS_MAX + " characters");
        }
        if (question.requiresClaimsCount()) {
            if (a.claimsCount() == null) {
                errors.add(countField, FieldErrorCodes.REQUIRED, "The number of claims is required");
            } else if (a.claimsCount() < Declaration.MIN_CLAIMS || a.claimsCount() > Declaration.MAX_CLAIMS) {
                errors.add(countField, FieldErrorCodes.OUT_OF_RANGE,
                        "Between " + Declaration.MIN_CLAIMS + " and " + Declaration.MAX_CLAIMS);
            }
        } else if (a.claimsCount() != null) {
            errors.add(countField, FieldErrorCodes.NOT_ALLOWED, "Only for " + DeclarationQuestion.CLAIMS_LAST_3_YEARS);
        }
    }

    private static DeclarationQuestion question(String code) {
        if (code == null) {
            return null;
        }
        try {
            return DeclarationQuestion.valueOf(code);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    static void requireVersion(Proposal proposal, Long version) {
        if (version == null) {
            new FieldErrors().add("version", FieldErrorCodes.REQUIRED, "The version is required").throwIfAny();
        }
        proposal.requireVersion(version);
    }
}
