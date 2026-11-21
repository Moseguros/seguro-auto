package mz.co.moseguros.policy.application;

import java.time.Clock;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import mz.co.moseguros.catalog.CatalogApi;
import mz.co.moseguros.payment.IntentView;
import mz.co.moseguros.payment.PaymentApi;
import mz.co.moseguros.payment.PaymentStatus;
import mz.co.moseguros.payment.PaymentTargetType;
import mz.co.moseguros.policy.PaymentPlan;
import mz.co.moseguros.policy.PolicyCoverageView;
import mz.co.moseguros.policy.PolicyIssued;
import mz.co.moseguros.policy.application.PolicyMetrics.FailureReason;
import mz.co.moseguros.policy.domain.Certificate;
import mz.co.moseguros.policy.domain.CertificateRepository;
import mz.co.moseguros.policy.domain.Instalment;
import mz.co.moseguros.policy.domain.InstalmentRepository;
import mz.co.moseguros.policy.domain.InstalmentSchedule;
import mz.co.moseguros.policy.domain.Policy;
import mz.co.moseguros.policy.domain.PolicyEvent;
import mz.co.moseguros.policy.domain.PolicyEventRepository;
import mz.co.moseguros.policy.domain.PolicyEventType;
import mz.co.moseguros.policy.domain.PolicyRepository;
import mz.co.moseguros.proposal.ProposalApi;
import mz.co.moseguros.proposal.ProposalStatus;
import mz.co.moseguros.proposal.ProposalView;
import mz.co.moseguros.shared.audit.ActorType;
import mz.co.moseguros.shared.audit.AuditRecord;
import mz.co.moseguros.shared.audit.AuditService;
import mz.co.moseguros.shared.domain.Ids;
import mz.co.moseguros.shared.domain.PlateParser;
import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.error.ErrorCodes;
import mz.co.moseguros.shared.numbering.BusinessNumberGenerator;
import mz.co.moseguros.shared.numbering.BusinessPrefix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

/**
 * Emissão automática da apólice (S6-01 §5), chamada pelo {@link ProposalPaidListener} em {@code SYSTEM}:
 * apólice {@code ACTIVE}, prestações, certificado com código de verificação, linha temporal, auditoria,
 * {@code ProposalApi.markConverted} e {@link PolicyIssued}, tudo numa transacção nova. Uma apólice nunca
 * fica {@code ACTIVE} sem um pagamento {@code SUCCEEDED} da própria proposta.
 *
 * <p>As falhas de negócio (proposta não paga, pagamento inválido, montante diferente) terminam sem erro,
 * com log e métrica: um reenvio não as resolve. A proposta fica {@code PAID} e o dinheiro está na conta
 * da seguradora; uma devolução faz-se sempre fora da plataforma (ADR-007).
 */
@Service
public class PolicyIssuanceService {

    /** {@code actorId} da auditoria e da linha temporal. */
    public static final String ACTOR = "policy-issuance";
    static final int TOKEN_ATTEMPTS = 3;
    private static final Logger log = LoggerFactory.getLogger(PolicyIssuanceService.class);

    private final PolicyRepository policies;
    private final InstalmentRepository instalments;
    private final CertificateRepository certificates;
    private final PolicyEventRepository policyEvents;
    private final ProposalApi proposals;
    private final PaymentApi payments;
    private final CatalogApi catalog;
    private final BusinessNumberGenerator numbers;
    private final VerificationTokens tokens;
    private final PlateParser plates;
    private final AuditService audit;
    private final ApplicationEventPublisher events;
    private final PolicyMetrics metrics;
    private final ObjectMapper objectMapper;
    private final Clock clock;

    public PolicyIssuanceService(PolicyRepository policies, InstalmentRepository instalments,
                                 CertificateRepository certificates, PolicyEventRepository policyEvents,
                                 ProposalApi proposals, PaymentApi payments, CatalogApi catalog,
                                 BusinessNumberGenerator numbers, VerificationTokens tokens, PlateParser plates,
                                 AuditService audit, ApplicationEventPublisher events, PolicyMetrics metrics,
                                 ObjectMapper objectMapper, Clock clock) {
        this.policies = policies;
        this.instalments = instalments;
        this.certificates = certificates;
        this.policyEvents = policyEvents;
        this.proposals = proposals;
        this.payments = payments;
        this.catalog = catalog;
        this.numbers = numbers;
        this.tokens = tokens;
        this.plates = plates;
        this.audit = audit;
        this.events = events;
        this.metrics = metrics;
        this.objectMapper = objectMapper;
        this.clock = clock;
    }

    /** Resultado da emissão. */
    public enum Outcome {
        /** Apólice emitida agora. */
        ISSUED,
        /** Já existia apólice para a proposta (evento repetido). */
        ALREADY_ISSUED,
        /** Falha de negócio (log e métrica); nada gravado. */
        REJECTED
    }

    /** @param productType só com {@link Outcome#ISSUED} */
    public record Result(Outcome outcome, UUID policyId, String productType) {
    }

    /**
     * Emite a apólice da proposta paga (S6-01 §5, passos 1 a 10). Corre no âmbito de quem chama
     * ({@code SYSTEM}, no listener). Uma segunda emissão em simultâneo falha no índice
     * {@code ux_policy_proposal} ({@code DataIntegrityViolationException}, tratada pelo listener).
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Result issue(UUID proposalId, UUID paymentIntentId) {
        // 1. Já emitida?
        Optional<Policy> existing = policies.findByProposalId(proposalId);
        if (existing.isPresent()) {
            UUID policyId = existing.get().getId();
            proposals.view(proposalId)
                    .filter(p -> p.status() == ProposalStatus.PAID)
                    .ifPresent(p -> proposals.markConverted(proposalId, policyId));
            log.info("Policy already issued proposalId={} policyId={}", proposalId, policyId);
            return new Result(Outcome.ALREADY_ISSUED, policyId, null);
        }

        // 2. Proposta paga
        ProposalView proposal = proposals.view(proposalId).orElse(null);
        if (proposal == null || proposal.status() != ProposalStatus.PAID) {
            log.warn("Policy not issued: proposal is not paid proposalId={} status={}", proposalId,
                    proposal == null ? null : proposal.status());
            return rejected(FailureReason.PROPOSAL_NOT_PAID);
        }

        // 3. Pagamento válido
        IntentView intent = intent(paymentIntentId);
        if (intent == null || !validPayment(intent, proposal)) {
            log.error("Policy not issued: invalid payment proposalId={} intentId={} status={}", proposalId,
                    paymentIntentId, intent == null ? null : intent.status());
            return rejected(FailureReason.PAYMENT_NOT_VALID);
        }
        Instant paidAt = intent.succeededAt();

        // 4. Plano e prestações (regra 2): neste sprint sempre FULL (Questões)
        PaymentPlan plan = Policy.paymentPlanFor(proposal.productType(), PaymentPlan.FULL);
        long total = proposal.totalAmount().amount();
        // 5. Datas (regra 3)
        Policy.CoverPeriod period = Policy.coverPeriod(proposal.startAt(), proposal.endAt(), paidAt);
        List<InstalmentSchedule.Line> schedule = InstalmentSchedule.build(total, plan, period.startDate());
        if (intent.amount().amount() != schedule.get(0).amount()) {
            log.error("Policy not issued: paid amount differs from the first instalment proposalId={} intentId={} "
                    + "paid={} expected={}", proposalId, paymentIntentId, intent.amount().amount(),
                    schedule.get(0).amount());
            return rejected(FailureReason.AMOUNT_MISMATCH);
        }

        // 6. Números
        Instant now = clock.instant();
        String policyNumber = numbers.next(BusinessPrefix.POLICY);
        String certificateNumber = numbers.next(BusinessPrefix.CERTIFICATE);

        // 7. Apólice, prestações, certificado e linha temporal
        List<PolicyCoverageView> coverages = proposal.coverages().stream()
                .map(c -> new PolicyCoverageView(c.code(), c.mandatory(), c.capital(), c.deductibleType(),
                        c.deductibleAmount(), c.deductiblePercent(), c.premium()))
                .toList();
        String productName = catalog.version(proposal.productVersionId()).productName();
        Policy policy = Policy.issue(new Policy.Issue(policyNumber, proposal.insurerId(), proposal.id(),
                intent.id(), proposal.productVersionId(), productName, proposal.productType(),
                coverages.stream().map(PolicyCoverageView::code).toList(), proposal.customerUserId(),
                proposal.policyholderName(), proposal.vehicleId(), plate(proposal.vehicleSnapshot()),
                proposal.vehicleSnapshot(), proposal.driverSnapshot(), objectMapper.valueToTree(coverages), period,
                plan, total, proposal.channel(), proposal.brokerOrgId(), now));
        policies.saveAndFlush(policy); // ux_policy_proposal: a segunda emissão em simultâneo falha aqui
        instalments.saveAll(schedule.stream().map(l -> Instalment.of(policy, l, intent.id(), paidAt)).toList());
        Certificate certificate = certificates.save(Certificate.issue(policy, certificateNumber, uniqueToken()));
        policyEvents.save(PolicyEvent.of(policy, PolicyEventType.ISSUED, now, ActorType.SYSTEM, ACTOR, null));
        policyEvents.save(PolicyEvent.of(policy, PolicyEventType.CERTIFICATE_ISSUED, now, ActorType.SYSTEM, ACTOR,
                objectMapper.createObjectNode().put("certificateNumber", certificateNumber)));
        instalments.flush();

        // 8. Auditoria
        Map<String, Object> policyAfter = new LinkedHashMap<>();
        policyAfter.put("number", policy.getNumber());
        policyAfter.put("insurerId", policy.getInsurerId().toString());
        policyAfter.put("proposalId", policy.getProposalId().toString());
        policyAfter.put("paymentIntentId", policy.getPaymentIntentId().toString());
        policyAfter.put("productType", policy.getProductType());
        policyAfter.put("plate", policy.getPlate());
        policyAfter.put("startAt", policy.getStartAt().toString());
        policyAfter.put("endAt", policy.getEndAt().toString());
        policyAfter.put("paymentPlan", policy.getPaymentPlan().name());
        policyAfter.put("totalAmount", policy.getTotalAmount());
        audit.record(new AuditRecord(ActorType.SYSTEM, ACTOR, policy.getInsurerId(), "POLICY.ISSUE", "Policy",
                policy.getId().toString(), null, policyAfter));
        Map<String, Object> certificateAfter = new LinkedHashMap<>();
        certificateAfter.put("number", certificate.getNumber());
        certificateAfter.put("policyId", policy.getId().toString());
        certificateAfter.put("validFrom", certificate.getValidFrom().toString());
        certificateAfter.put("validTo", certificate.getValidTo().toString());
        audit.record(new AuditRecord(ActorType.SYSTEM, ACTOR, policy.getInsurerId(), "CERTIFICATE.ISSUE",
                "Certificate", certificate.getId().toString(), null, certificateAfter));

        // 9. Proposta convertida (na mesma transacção: se falhar, nada fica gravado)
        proposals.markConverted(proposal.id(), policy.getId());

        // 10. Evento
        events.publishEvent(new PolicyIssued(Ids.newId(), now, policy.getId(), policy.getNumber(),
                policy.getInsurerId(), policy.getPolicyholderUserId(), policy.getProposalId(), certificate.getId(),
                policy.getPlate(), policy.getStartAt(), policy.getEndAt()));
        log.info("Policy issued policyId={} number={} proposalId={} certificate={} startAt={} endAt={}",
                policy.getId(), policy.getNumber(), proposalId, certificate.getNumber(), policy.getStartAt(),
                policy.getEndAt());
        return new Result(Outcome.ISSUED, policy.getId(), policy.getProductType());
    }

    private Result rejected(FailureReason reason) {
        // Nada a gravar. Uma leitura que falhou (ex.: PAYMENT_INTENT_NOT_FOUND) já marcou a transacção para
        // rollback: marcá-la também aqui faz um rollback silencioso, sem UnexpectedRollbackException.
        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        metrics.failure(reason);
        return new Result(Outcome.REJECTED, null, null);
    }

    /** A intenção (em {@code SYSTEM}); {@code null} se não existir. */
    private IntentView intent(UUID intentId) {
        if (intentId == null) {
            return null;
        }
        try {
            return payments.intent(intentId);
        } catch (DomainException e) {
            if (ErrorCodes.PAYMENT_INTENT_NOT_FOUND.equals(e.getCode())) {
                return null;
            }
            throw e;
        }
    }

    /** {@code SUCCEEDED}, da mesma seguradora, com a proposta como alvo e com a data da confirmação. */
    static boolean validPayment(IntentView intent, ProposalView proposal) {
        return intent.status() == PaymentStatus.SUCCEEDED
                && proposal.insurerId().equals(intent.insurerId())
                && intent.targetType() == PaymentTargetType.PROPOSAL
                && proposal.id().equals(intent.targetId())
                && intent.succeededAt() != null
                && intent.amount() != null;
    }

    /** Matrícula formatada ({@code Matricula}) do snapshot; o valor gravado se os formatos mudaram. */
    private String plate(JsonNode vehicleSnapshot) {
        String raw = vehicleSnapshot == null ? null : vehicleSnapshot.path("plate").asText(null);
        if (raw == null || raw.isBlank()) {
            throw new IllegalStateException("The proposal vehicle snapshot has no plate");
        }
        try {
            return plates.parse(raw).value();
        } catch (DomainException e) {
            return raw;
        }
    }

    /** Código de verificação que ainda não existe (até {@value #TOKEN_ATTEMPTS} tentativas). */
    private String uniqueToken() {
        for (int attempt = 0; attempt < TOKEN_ATTEMPTS; attempt++) {
            String token = tokens.newToken();
            if (!certificates.existsByVerificationToken(token)) {
                return token;
            }
        }
        throw new IllegalStateException("Could not generate a unique certificate verification code");
    }
}
