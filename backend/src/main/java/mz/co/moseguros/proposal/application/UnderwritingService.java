package mz.co.moseguros.proposal.application;

import java.time.Instant;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import mz.co.moseguros.identity.IdentityApi;
import mz.co.moseguros.identity.KycState;
import mz.co.moseguros.proposal.AcceptanceMode;
import mz.co.moseguros.proposal.ProposalAccepted;
import mz.co.moseguros.proposal.domain.Declaration;
import mz.co.moseguros.proposal.domain.DeclarationQuestion;
import mz.co.moseguros.proposal.domain.DeclarationRepository;
import mz.co.moseguros.proposal.domain.Inspection;
import mz.co.moseguros.proposal.domain.InspectionRepository;
import mz.co.moseguros.proposal.domain.InspectionStatus;
import mz.co.moseguros.proposal.domain.Proposal;
import mz.co.moseguros.proposal.domain.UnderwritingEvaluator;
import mz.co.moseguros.proposal.domain.UnderwritingEvaluator.Evaluation;
import mz.co.moseguros.proposal.domain.UnderwritingRules;
import mz.co.moseguros.shared.audit.ActorType;
import mz.co.moseguros.shared.audit.AuditRecord;
import mz.co.moseguros.shared.audit.AuditService;
import mz.co.moseguros.shared.domain.Ids;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * Avaliação das regras de aceitação de uma proposta e aceitação automática (S4-05 §3, §6 passo 5 e
 * §8). Reúne os factos (KYC do cliente, declarações "sim", vistoria, valor comercial do veículo) e
 * chama o {@link UnderwritingEvaluator}. Corre na transacção de quem chama (submissão ou aprovação da
 * vistoria).
 */
@Component
public class UnderwritingService {

    /** {@code actorId} da auditoria das decisões automáticas. */
    static final String SYSTEM_ACTOR = "UnderwritingRules";

    private final ProposalSystemLookups lookups;
    private final DeclarationRepository declarations;
    private final InspectionRepository inspections;
    private final IdentityApi identity;
    private final ApplicationEventPublisher events;
    private final AuditService audit;
    private final ProposalProperties properties;

    public UnderwritingService(ProposalSystemLookups lookups, DeclarationRepository declarations,
                               InspectionRepository inspections, IdentityApi identity, ApplicationEventPublisher events,
                               AuditService audit, ProposalProperties properties) {
        this.lookups = lookups;
        this.declarations = declarations;
        this.inspections = inspections;
        this.identity = identity;
        this.events = events;
        this.audit = audit;
        this.properties = properties;
    }

    /** Avalia as regras da versão do produto da proposta com os factos actuais. */
    public Evaluation evaluate(Proposal proposal, Instant now) {
        UnderwritingRules rules = lookups.underwritingRules(proposal.getProductVersionId());
        KycState kyc = identity.kycStatus(proposal.getCustomerUserId()).state();
        List<DeclarationQuestion> positive = declarations.findByProposalId(proposal.getId()).stream()
                .filter(Declaration::getAnswer)
                .map(Declaration::getQuestionCode)
                .sorted(Comparator.naturalOrder())
                .toList();
        InspectionStatus inspection = proposal.isInspectionRequired()
                ? inspections.findByProposalId(proposal.getId()).map(Inspection::getStatus).orElse(null)
                : null;
        return UnderwritingEvaluator.evaluate(rules, new UnderwritingEvaluator.Input(proposal.isInspectionRequired(),
                proposal.getTotalAmount(), kyc, positive, inspection, vehicleValue(proposal)), now);
    }

    /**
     * Grava o resultado e, se for {@code AUTOMATIC}, aceita a proposta: {@code ACCEPTED} com
     * {@code acceptance_mode = AUTOMATIC}, {@code ProposalAccepted}, auditoria {@code PROPOSAL.ACCEPT}
     * ({@code actorType = SYSTEM}) e o e-mail {@code proposal.accepted} depois do commit.
     *
     * @return {@code true} se aceitou
     */
    public boolean apply(Proposal proposal, Evaluation evaluation, Instant now) {
        proposal.recordUnderwriting(evaluation.toJson());
        if (!evaluation.automatic()) {
            return false;
        }
        String before = proposal.getStatus().name();
        proposal.acceptAutomatically(now, properties.expiry());
        events.publishEvent(new ProposalAccepted(Ids.newId(), now, proposal.getId(), proposal.getInsurerId(),
                proposal.getCustomerUserId(), proposal.getBrokerOrgId(), AcceptanceMode.AUTOMATIC));
        Map<String, Object> after = new LinkedHashMap<>();
        after.put("status", proposal.getStatus().name());
        after.put("acceptanceMode", AcceptanceMode.AUTOMATIC.name());
        after.put("expiresAt", proposal.getExpiresAt().toString());
        audit.record(new AuditRecord(ActorType.SYSTEM, SYSTEM_ACTOR, proposal.getInsurerId(), "PROPOSAL.ACCEPT",
                "Proposal", proposal.getId().toString(), Map.of("status", before), after));
        return true;
    }

    /** {@code kyc.requiredForAutomaticAcceptance} (S4-05 §12). */
    public static boolean kycRequired(UnderwritingRules rules, KycState kyc, long totalAmount) {
        return rules.kycRequired(kyc == KycState.VERIFIED, totalAmount);
    }

    private static Long vehicleValue(Proposal proposal) {
        JsonNode value = proposal.getVehicleSnapshot().get("commercialValue");
        if (value == null || value.isNull() || !value.hasNonNull("amount")) {
            return null;
        }
        return value.get("amount").asLong();
    }
}
