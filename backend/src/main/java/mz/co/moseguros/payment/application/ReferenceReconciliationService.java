package mz.co.moseguros.payment.application;

import java.time.Clock;
import java.time.Instant;
import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.micrometer.core.instrument.MeterRegistry;
import mz.co.moseguros.payment.PaymentStatus;
import mz.co.moseguros.payment.application.reference.ReferenceGenerator;
import mz.co.moseguros.payment.domain.ConfirmedBy;
import mz.co.moseguros.payment.domain.InboundMessage;
import mz.co.moseguros.payment.domain.InboundMessage.Outcome;
import mz.co.moseguros.payment.domain.InboundMessageRepository;
import mz.co.moseguros.payment.domain.PaymentEvent;
import mz.co.moseguros.payment.domain.PaymentEventRepository;
import mz.co.moseguros.payment.domain.PaymentEventType;
import mz.co.moseguros.payment.domain.PaymentExceptionCase;
import mz.co.moseguros.payment.domain.PaymentExceptionRepository;
import mz.co.moseguros.payment.domain.PaymentExceptionType;
import mz.co.moseguros.payment.domain.PaymentIntent;
import mz.co.moseguros.payment.domain.PaymentIntentRepository;
import mz.co.moseguros.payment.domain.ReferencePayment;
import mz.co.moseguros.payment.domain.ReferencePaymentRepository;
import mz.co.moseguros.paymentcontracts.ReferencePaymentReceived;
import mz.co.moseguros.shared.domain.Ids;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Reconciliação de um pagamento lido num ficheiro de movimentos ({@link ReferencePaymentReceived}, S5-10 §3).
 * Só confirma sozinha uma referência {@code ACTIVE}, dentro da validade e com o montante certo; em todos os
 * outros casos cria uma excepção para o financeiro da seguradora e nunca confirma. Uma devolução faz-se sempre
 * fora da plataforma.
 *
 * <p>Corre em {@code SYSTEM} (quem chama, o {@code ReferencePaymentListener}) e filtra sempre pela seguradora
 * da mensagem. Os resultados de negócio nunca lançam: a mensagem é confirmada. Só os erros técnicos (BD,
 * <i>deadlock</i>) chegam ao consumidor, que repete.
 */
@Service
public class ReferenceReconciliationService {

    static final String ACTOR = "ReferencePaymentListener";
    /** Detalhes de {@code UNKNOWN_REFERENCE}. */
    static final String INVALID_CHECK_DIGITS = "INVALID_CHECK_DIGITS";
    static final String NOT_ISSUED = "NOT_ISSUED";

    private static final Logger log = LoggerFactory.getLogger(ReferenceReconciliationService.class);
    /** Estados da intenção a partir dos quais o ficheiro do banco a confirma. */
    private static final Set<PaymentStatus> CONFIRMABLE = EnumSet.of(PaymentStatus.PENDING, PaymentStatus.UNKNOWN,
            PaymentStatus.EXPIRED, PaymentStatus.CANCELLED);

    private final InboundMessageRepository inbound;
    private final ReferencePaymentRepository references;
    private final PaymentIntentRepository intents;
    private final PaymentEventRepository paymentEvents;
    private final PaymentExceptionRepository exceptions;
    private final IntentStateService states;
    private final ReferenceGenerator referenceGenerator;
    private final ObjectProvider<MeterRegistry> meters;
    private final Clock clock;

    public ReferenceReconciliationService(InboundMessageRepository inbound, ReferencePaymentRepository references,
                                          PaymentIntentRepository intents, PaymentEventRepository paymentEvents,
                                          PaymentExceptionRepository exceptions, IntentStateService states,
                                          ReferenceGenerator referenceGenerator, ObjectProvider<MeterRegistry> meters,
                                          Clock clock) {
        this.inbound = inbound;
        this.references = references;
        this.intents = intents;
        this.paymentEvents = paymentEvents;
        this.exceptions = exceptions;
        this.states = states;
        this.referenceGenerator = referenceGenerator;
        this.meters = meters;
        this.clock = clock;
    }

    /** Resultado do tratamento de uma mensagem. */
    public record Result(Outcome outcome, PaymentExceptionType exceptionType, UUID intentId, UUID exceptionId,
                         boolean duplicate) {

        static Result duplicateMessage() {
            return new Result(null, null, null, null, true);
        }
    }

    @Transactional
    public Result handle(ReferencePaymentReceived m) {
        Instant now = clock.instant();
        int inserted = inbound.insertIfAbsent(Ids.newId(), m.messageId(), InboundMessage.REFERENCE_PAYMENT_RECEIVED,
                m.insurerId(), m.sourceId(), m.fileSha256(), Outcome.IGNORED.name(), now, ACTOR);
        if (inserted == 0) {
            count("moseguros.payment.inbound.duplicates");
            log.info("Duplicate reference payment message ignored messageId={} insurerId={}", m.messageId(),
                    m.insurerId());
            return Result.duplicateMessage();
        }
        InboundMessage message = inbound.findByMessageId(m.messageId()).orElseThrow();

        Optional<ReferencePayment> found = references.lockForReconciliation(m.insurerId(), m.entity(), m.reference());
        long amount = m.amount().amount();
        Result result;
        if (found.isEmpty()) {
            String detail = referenceGenerator.isValid(m.entity(), m.reference(), amount) ? NOT_ISSUED
                    : INVALID_CHECK_DIGITS;
            result = exception(m, PaymentExceptionType.UNKNOWN_REFERENCE, null, detail);
        } else {
            result = decide(m, found.get(), now);
        }
        message.complete(result.outcome(), result.intentId(), result.exceptionId());
        inbound.save(message);
        return result;
    }

    private Result decide(ReferencePaymentReceived m, ReferencePayment ref, Instant now) {
        UUID intentId = ref.getIntentId();
        switch (ref.getStatus()) {
            case PAID:
                return exception(m, PaymentExceptionType.ALREADY_PAID, intentId, null);
            case EXPIRED:
                return exception(m, PaymentExceptionType.EXPIRED_REFERENCE, intentId, null);
            case CANCELLED:
                return exception(m, PaymentExceptionType.CANCELLED_REFERENCE, intentId, null);
            case ACTIVE:
            default:
                break;
        }
        if (m.paidAt().isAfter(ref.getValidUntil())) {
            return exception(m, PaymentExceptionType.EXPIRED_REFERENCE, intentId, "PAID_AFTER_VALID_UNTIL");
        }
        if (m.amount().amount() != ref.getAmount()) {
            return exception(m, PaymentExceptionType.AMOUNT_MISMATCH, intentId, null);
        }
        PaymentIntent intent = intents.lock(intentId).orElse(null);
        if (intent == null || !CONFIRMABLE.contains(intent.getStatus())) {
            // Referência activa com a intenção já paga ou num estado final: dados incoerentes, nunca confirma.
            String detail = intent == null ? "INTENT_NOT_FOUND" : "INTENT_" + intent.getStatus().name();
            log.error("Active reference with a non-confirmable intent intentId={} status={} messageId={}", intentId,
                    intent == null ? null : intent.getStatus(), m.messageId());
            return exception(m, PaymentExceptionType.ALREADY_PAID, intentId, detail);
        }
        ref.recordBankPayment(m.paidAt(), m.bankTransactionId(), m.messageId());
        references.save(ref);
        paymentEvents.save(PaymentEvent.of(intent, PaymentEventType.REFERENCE_PAID, null, null, payload(m), now));
        states.markSucceeded(intentId, m.paidAt(), ConfirmedBy.REFERENCE_FILE, null, ACTOR);
        count("moseguros.payment.reference.confirmed");
        log.info("Reference payment confirmed intentId={} reference={} entity={} messageId={}", intentId,
                intent.getReference(), m.entity(), m.messageId());
        return new Result(Outcome.CONFIRMED, null, intentId, null, false);
    }

    private Result exception(ReferencePaymentReceived m, PaymentExceptionType type, UUID intentId, String detail) {
        PaymentExceptionCase c = exceptions.save(PaymentExceptionCase.fromBankPayment(type, m, intentId, detail));
        MeterRegistry registry = meters.getIfAvailable();
        if (registry != null) {
            registry.counter("moseguros.payment.exceptions", "type", type.name()).increment();
        }
        log.warn("Reference payment exception type={} detail={} insurerId={} entity={} reference={} intentId={} "
                + "messageId={} lineNo={}", type, detail, m.insurerId(), m.entity(), m.reference(), intentId,
                m.messageId(), m.lineNo());
        return new Result(Outcome.EXCEPTION, type, intentId, c.getId(), false);
    }

    /** Dados do registo para o histórico da intenção (sem dados pessoais: o ficheiro não os tem). */
    private static ObjectNode payload(ReferencePaymentReceived m) {
        ObjectNode n = JsonNodeFactory.instance.objectNode();
        n.put("messageId", m.messageId());
        n.put("fileFormat", m.fileFormat());
        n.put("fileName", m.fileName());
        n.put("formatFileId", m.formatFileId());
        n.put("lineNo", m.lineNo());
        n.put("entity", m.entity());
        n.put("reference", m.reference());
        n.put("amount", m.amount().amount());
        if (m.fee() != null) {
            n.put("fee", m.fee().amount());
        }
        n.put("paidAt", m.paidAt().toString());
        if (m.channel() != null) {
            n.put("channel", m.channel());
        }
        if (m.bankTransactionId() != null) {
            n.put("bankTransactionId", m.bankTransactionId());
        }
        return n;
    }

    private void count(String name) {
        MeterRegistry registry = meters.getIfAvailable();
        if (registry != null) {
            registry.counter(name).increment();
        }
    }
}
