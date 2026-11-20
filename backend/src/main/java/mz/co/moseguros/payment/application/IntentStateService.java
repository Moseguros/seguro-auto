package mz.co.moseguros.payment.application;

import java.time.Clock;
import java.time.Instant;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;

import mz.co.moseguros.payment.FailureCode;
import mz.co.moseguros.payment.PaymentFailed;
import mz.co.moseguros.payment.PaymentMethod;
import mz.co.moseguros.payment.PaymentStatus;
import mz.co.moseguros.payment.PaymentSucceeded;
import mz.co.moseguros.payment.ReferenceGenerated;
import mz.co.moseguros.payment.application.gateway.InitiationResult;
import mz.co.moseguros.payment.application.gateway.ReferenceData;
import mz.co.moseguros.payment.application.gateway.StatusResult;
import mz.co.moseguros.payment.domain.PaymentExceptionCase;
import mz.co.moseguros.payment.domain.PaymentExceptionRepository;
import mz.co.moseguros.payment.domain.PaymentExceptionType;
import mz.co.moseguros.payment.domain.ConfirmedBy;
import mz.co.moseguros.payment.domain.PaymentEvent;
import mz.co.moseguros.payment.domain.PaymentEventRepository;
import mz.co.moseguros.payment.domain.PaymentEventType;
import mz.co.moseguros.payment.domain.PaymentIntent;
import mz.co.moseguros.payment.domain.PaymentIntentRepository;
import mz.co.moseguros.payment.domain.ReferencePayment;
import mz.co.moseguros.payment.domain.ReferencePaymentRepository;
import mz.co.moseguros.shared.audit.ActorType;
import mz.co.moseguros.shared.audit.AuditRecord;
import mz.co.moseguros.shared.audit.AuditService;
import mz.co.moseguros.shared.domain.Ids;
import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.error.ErrorCodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Todas as mudanças de estado das intenções (S5-02 §3): bloqueia a linha ({@code PESSIMISTIC_WRITE}),
 * aplica a transição do domínio, grava {@code payment_event} ({@code STATUS_CHANGED}), a auditoria
 * {@code PAYMENT_INTENT.STATUS_CHANGE} (actor {@code SYSTEM}, {@code actorId} = quem pediu a transição)
 * e publica o evento ({@link PaymentSucceeded}, {@link PaymentFailed}). As transições para um estado final
 * ou {@code UNKNOWN} contam em {@link PaymentMetrics}.
 *
 * <p>Corre na transacção de quem chama (ou numa nova); quem chama garante o âmbito: em {@code SYSTEM}
 * (jobs, listeners) ou no do pagador (criação da intenção).
 */
@Service
public class IntentStateService {

    private static final Logger log = LoggerFactory.getLogger(IntentStateService.class);

    private final PaymentIntentRepository intents;
    private final PaymentEventRepository paymentEvents;
    private final ReferencePaymentRepository references;
    private final AuditService audit;
    private final ApplicationEventPublisher events;
    private final Clock clock;
    private final PaymentExceptionRepository exceptions;
    private final PaymentProperties.StatusQuery schedule;
    private final PaymentMetrics metrics;

    public IntentStateService(PaymentIntentRepository intents, PaymentEventRepository paymentEvents,
                              ReferencePaymentRepository references, AuditService audit,
                              ApplicationEventPublisher events, Clock clock, PaymentExceptionRepository exceptions,
                              PaymentProperties properties, PaymentMetrics metrics) {
        this.intents = intents;
        this.paymentEvents = paymentEvents;
        this.references = references;
        this.audit = audit;
        this.events = events;
        this.clock = clock;
        this.exceptions = exceptions;
        this.schedule = properties.getStatusQuery();
        this.metrics = metrics;
    }

    /**
     * Aplica o resultado do envio ({@code PaymentGateway.initiate}, S5-02 §6): grava sempre
     * {@code PROVIDER_RESPONSE} com a resposta mascarada; {@code PENDING} → {@code PENDING} (com os ids do
     * fornecedor; para a referência cria {@code reference_payment} e publica {@link ReferenceGenerated});
     * {@code SUCCEEDED}, {@code FAILED} e {@code UNKNOWN} como na tabela do §3 (passando por
     * {@code PENDING} se a intenção ainda estiver em {@code CREATED}).
     *
     * @throws DomainException 503 {@code REFERENCE_GENERATION_FAILED} se a referência já existir na entidade
     */
    @Transactional
    public PaymentIntent applyInitiation(UUID intentId, InitiationResult result, String actor) {
        PaymentIntent intent = locked(intentId);
        Instant now = clock.instant();
        paymentEvents.save(PaymentEvent.of(intent, PaymentEventType.PROVIDER_RESPONSE, null, null,
                result.maskedRawResponse(), now));
        if (result.reference() != null) {
            createReference(intent, result.reference(), now);
        }
        if (intent.getStatus() != PaymentStatus.CREATED && intent.getStatus() != PaymentStatus.PENDING) {
            return lateInitiation(intent, result, now, actor);
        }
        switch (result.outcome()) {
            case PENDING -> {
                if (intent.getStatus() == PaymentStatus.CREATED) {
                    transition(intent, actor, i -> i.markPending(now, result.providerTxId(),
                            result.providerConversationId(), result.thirdPartyReference()));
                } else {
                    intent.recordProvider(result.providerTxId(), result.providerConversationId(),
                            result.thirdPartyReference());
                }
            }
            case SUCCEEDED -> {
                toPending(intent, actor, result, now);
                succeed(intent, now, ConfirmedBy.PROVIDER, result.providerTxId(), actor);
            }
            case FAILED -> {
                toPending(intent, actor, result, now);
                transition(intent, actor, i -> i.markFailed(result.failureCode(), now));
            }
            case UNKNOWN -> {
                toPending(intent, actor, result, now);
                transition(intent, actor, i -> i.markUnknown(now, schedule.firstQueryAt(now)));
            }
        }
        if (result.reference() != null) {
            ReferenceData r = result.reference();
            events.publishEvent(new ReferenceGenerated(Ids.newId(), now, intent.getId(), intent.getInsurerId(),
                    intent.getPayerUserId(), r.entity(), r.reference(), intent.amount(), intent.getExpiresAt()));
        }
        return intents.saveAndFlush(intent);
    }

    /**
     * Resposta do envio que chega depois de a intenção ter saído de {@code CREATED}/{@code PENDING}: a
     * recuperação já a pôs em {@code UNKNOWN}, ou uma consulta ou o financeiro já a resolveram. Um
     * resultado final aplica-se a uma {@code UNKNOWN}; o resto fica só no histórico.
     */
    private PaymentIntent lateInitiation(PaymentIntent intent, InitiationResult result, Instant now, String actor) {
        if (intent.getStatus() == PaymentStatus.UNKNOWN) {
            switch (result.outcome()) {
                case SUCCEEDED -> succeed(intent, now, ConfirmedBy.PROVIDER, result.providerTxId(), actor);
                case FAILED -> transition(intent, actor, i -> i.markFailed(result.failureCode(), now));
                default -> intent.recordProvider(result.providerTxId(), result.providerConversationId(),
                        result.thirdPartyReference());
            }
        } else if (result.outcome() == InitiationResult.Outcome.SUCCEEDED
                && intent.getStatus() != PaymentStatus.SUCCEEDED) {
            log.error("Provider confirmed a payment intent that is already final intentId={} reference={} status={}",
                    intent.getId(), intent.getReference(), intent.getStatus());
        } else {
            log.info("Late provider response ignored intentId={} reference={} status={} outcome={}", intent.getId(),
                    intent.getReference(), intent.getStatus(), result.outcome());
        }
        return intents.saveAndFlush(intent);
    }

    /**
     * Início do envio assíncrono (M-Pesa, S5-04 §2, transacção 1): compare-and-set
     * {@code CREATED → PENDING} com {@code dispatched_at}, a {@code third_party_reference} gerada antes
     * do envio e o evento {@code DISPATCHED}. A linha fica bloqueada até ao commit: duas chamadas em
     * paralelo para a mesma intenção dão um só envio.
     *
     * @return {@code false} se a intenção já não estava em {@code CREATED} ou já passou o prazo (nada muda)
     */
    @Transactional
    public boolean startDispatch(UUID intentId, String thirdPartyReference, String actor) {
        PaymentIntent intent = locked(intentId);
        Instant now = clock.instant();
        if (intent.getStatus() != PaymentStatus.CREATED || !intent.getExpiresAt().isAfter(now)) {
            return false;
        }
        paymentEvents.save(PaymentEvent.of(intent, PaymentEventType.DISPATCHED, null, null, null, now));
        transition(intent, actor, i -> i.markPending(now, null, null, thirdPartyReference));
        intents.saveAndFlush(intent);
        return true;
    }

    /**
     * Resultado de uma consulta de estado (S5-04 §4): conta a tentativa, grava {@code STATUS_QUERY} com o
     * pedido e a resposta mascarados e aplica a tabela da secção 3 ({@code SUCCEEDED} com
     * {@code confirmed_by = STATUS_QUERY}, {@code FAILED}, ou a próxima consulta pelo calendário). Passado
     * o prazo das consultas deixa de consultar e abre {@code UNRESOLVED_STATUS}. Uma intenção que já não
     * está {@code UNKNOWN} (resolvida entretanto) só fica com o histórico.
     */
    @Transactional
    public PaymentIntent applyStatusQuery(UUID intentId, StatusResult result, String actor) {
        PaymentIntent intent = locked(intentId);
        Instant now = clock.instant();
        paymentEvents.save(PaymentEvent.of(intent, PaymentEventType.STATUS_QUERY, null, null,
                result.maskedRawResponse(), now));
        if (intent.getStatus() != PaymentStatus.UNKNOWN) {
            log.info("Status query result for an intent that is no longer UNKNOWN intentId={} status={}",
                    intent.getId(), intent.getStatus());
            return intents.saveAndFlush(intent);
        }
        switch (result.outcome()) {
            case SUCCEEDED -> {
                intent.countStatusQuery();
                succeed(intent, now, ConfirmedBy.STATUS_QUERY, result.providerTxId(), actor);
            }
            case FAILED -> {
                intent.countStatusQuery();
                transition(intent, actor, i -> i.markFailed(result.failureCode(), now));
            }
            case STILL_UNKNOWN -> {
                Instant next = schedule.nextQueryAt(intent.getUnknownSince(), intent.getQueryAttempts() + 1, now);
                intent.recordStatusQuery(next);
                if (next == null) {
                    giveUp(intent);
                }
            }
        }
        return intents.saveAndFlush(intent);
    }

    private void giveUp(PaymentIntent intent) {
        if (!exceptions.existsByIntentIdAndType(intent.getId(), PaymentExceptionType.UNRESOLVED_STATUS)) {
            exceptions.save(PaymentExceptionCase.unresolvedStatus(intent));
        }
        log.warn("Payment status still unknown, queries stopped intentId={} reference={} attempts={}",
                intent.getId(), intent.getReference(), intent.getQueryAttempts());
    }

    /**
     * Confirmação do pagamento (fornecedor, consulta, ficheiro do banco ou resolução manual):
     * {@code SUCCEEDED}, {@link PaymentSucceeded} e o cancelamento das outras referências do mesmo alvo
     * ({@code SUPERSEDED}; as de M-Pesa não se cancelam).
     *
     * @param paidAt momento da confirmação ({@code null} = agora)
     */
    @Transactional
    public PaymentIntent markSucceeded(UUID intentId, Instant paidAt, ConfirmedBy by, String providerTxId,
                                       String actor) {
        PaymentIntent intent = locked(intentId);
        succeed(intent, paidAt == null ? clock.instant() : paidAt, by, providerTxId, actor);
        return intents.saveAndFlush(intent);
    }

    /** Recusa do fornecedor ou consulta com resultado final: {@code FAILED} e {@link PaymentFailed}. */
    @Transactional
    public PaymentIntent markFailed(UUID intentId, FailureCode code, String actor) {
        PaymentIntent intent = locked(intentId);
        Instant now = clock.instant();
        transition(intent, actor, i -> i.markFailed(code, now));
        return intents.saveAndFlush(intent);
    }

    /** {@code PENDING → UNKNOWN} com a primeira consulta pelo calendário (S5-04 §4). */
    @Transactional
    public PaymentIntent markUnknown(UUID intentId, String actor) {
        PaymentIntent intent = locked(intentId);
        Instant now = clock.instant();
        transition(intent, actor, i -> i.markUnknown(now, schedule.firstQueryAt(now)));
        return intents.saveAndFlush(intent);
    }

    /** Prazo passado ({@code PaymentExpiryJob}): {@code EXPIRED}, a referência {@code EXPIRED} e {@link PaymentFailed}. */
    @Transactional
    public PaymentIntent expire(UUID intentId, String actor) {
        PaymentIntent intent = locked(intentId);
        Instant now = clock.instant();
        transition(intent, actor, i -> i.expire(now));
        references.findByIntentId(intent.getId()).ifPresent(ReferencePayment::expire);
        return intents.saveAndFlush(intent);
    }

    /** Referência substituída: {@code CANCELLED} ({@code SUPERSEDED}) e a referência {@code CANCELLED}. */
    @Transactional
    public PaymentIntent cancel(UUID intentId, FailureCode code, String actor) {
        PaymentIntent intent = locked(intentId);
        cancel(intent, code, actor, clock.instant());
        return intents.saveAndFlush(intent);
    }

    private void succeed(PaymentIntent intent, Instant paidAt, ConfirmedBy by, String providerTxId, String actor) {
        transition(intent, actor, i -> i.markSucceeded(paidAt, by, providerTxId));
        if (intent.getMethod() == PaymentMethod.BANK_REFERENCE) {
            references.findByIntentId(intent.getId()).ifPresent(r -> r.markPaid(paidAt));
        }
        Instant now = clock.instant();
        // As outras referências do alvo deixam de valer (um pagamento nelas vira excepção, S5-10).
        for (PaymentIntent other : intents.findByTarget(intent.getTargetType(), intent.getTargetId(),
                EnumSet.of(PaymentStatus.CREATED, PaymentStatus.PENDING))) {
            if (!other.getId().equals(intent.getId()) && other.getMethod() == PaymentMethod.BANK_REFERENCE) {
                PaymentIntent lockedOther = locked(other.getId());
                cancel(lockedOther, FailureCode.SUPERSEDED, actor, now);
                intents.save(lockedOther);
            }
        }
    }

    private void cancel(PaymentIntent intent, FailureCode code, String actor, Instant now) {
        transition(intent, actor, i -> i.cancel(code, now));
        references.findByIntentId(intent.getId()).ifPresent(ReferencePayment::cancel);
    }

    private void toPending(PaymentIntent intent, String actor, InitiationResult result, Instant now) {
        if (intent.getStatus() == PaymentStatus.CREATED) {
            transition(intent, actor, i -> i.markPending(now, result.providerTxId(), result.providerConversationId(),
                    result.thirdPartyReference()));
        } else {
            intent.recordProvider(result.providerTxId(), result.providerConversationId(),
                    result.thirdPartyReference());
        }
    }

    private void createReference(PaymentIntent intent, ReferenceData data, Instant now) {
        if (references.existsByEntityAndReference(data.entity(), data.reference())
                || references.existsByEntityAndInvoiceNumber(data.entity(), data.invoiceNumber())) {
            log.error("Reference already used in the entity intentId={} entity={} invoiceNumber={}", intent.getId(),
                    data.entity(), data.invoiceNumber());
            throw DomainException.of(ErrorCodes.REFERENCE_GENERATION_FAILED, "The payment reference could not be "
                    + "generated");
        }
        references.save(ReferencePayment.active(intent, data.entity(), data.reference(), data.invoiceNumber(),
                data.month()));
    }

    /** Transição com histórico, auditoria e evento. */
    private void transition(PaymentIntent intent, String actor, Consumer<PaymentIntent> change) {
        PaymentStatus from = intent.getStatus();
        Map<String, Object> before = state(intent);
        change.accept(intent);
        PaymentStatus to = intent.getStatus();
        Instant now = clock.instant();
        paymentEvents.save(PaymentEvent.of(intent, PaymentEventType.STATUS_CHANGED, from, to, null, now));
        audit.record(new AuditRecord(ActorType.SYSTEM, Objects.requireNonNull(actor, "actor"), intent.getInsurerId(),
                "PAYMENT_INTENT.STATUS_CHANGE", "PaymentIntent", intent.getId().toString(), before, state(intent)));
        log.info("Payment intent status changed intentId={} reference={} method={} from={} to={} actor={}",
                intent.getId(), intent.getReference(), intent.getMethod(), from, to, actor);
        // moseguros.payment.intents{method, status} (S6-08 §1), depois do commit
        metrics.transitioned(intent.getMethod(), to);
        switch (to) {
            case SUCCEEDED -> events.publishEvent(new PaymentSucceeded(Ids.newId(), now, intent.getId(),
                    intent.getReference(), intent.getInsurerId(), intent.getPayerUserId(), intent.getTargetType(),
                    intent.getTargetId(), intent.getPurpose(), intent.getMethod(), intent.amount(),
                    intent.getSucceededAt()));
            case FAILED, EXPIRED -> events.publishEvent(new PaymentFailed(Ids.newId(), now, intent.getId(),
                    intent.getInsurerId(), intent.getPayerUserId(), intent.getTargetType(), intent.getTargetId(),
                    intent.getMethod(), to, intent.getFailureCode() == null ? null : intent.getFailureCode().name()));
            default -> {
                // PENDING, UNKNOWN, CANCELLED, REFUNDED: sem evento público no S5.
            }
        }
    }

    private static Map<String, Object> state(PaymentIntent intent) {
        Map<String, Object> s = new LinkedHashMap<>();
        s.put("status", intent.getStatus().name());
        s.put("failureCode", intent.getFailureCode() == null ? null : intent.getFailureCode().name());
        s.put("confirmedBy", intent.getConfirmedBy() == null ? null : intent.getConfirmedBy().name());
        return s;
    }

    private PaymentIntent locked(UUID intentId) {
        return intents.lock(intentId).orElseThrow(() -> DomainException.of(ErrorCodes.PAYMENT_INTENT_NOT_FOUND,
                "Payment intent not found"));
    }
}
