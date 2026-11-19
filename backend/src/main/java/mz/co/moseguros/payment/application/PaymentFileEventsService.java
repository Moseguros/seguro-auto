package mz.co.moseguros.payment.application;

import java.time.Clock;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import io.micrometer.core.instrument.MeterRegistry;
import mz.co.moseguros.payment.domain.FileImport;
import mz.co.moseguros.payment.domain.FileImportRepository;
import mz.co.moseguros.payment.domain.InboundMessage;
import mz.co.moseguros.payment.domain.InboundMessage.Outcome;
import mz.co.moseguros.payment.domain.InboundMessageRepository;
import mz.co.moseguros.payment.domain.InsurerPaymentMethodRepository;
import mz.co.moseguros.paymentcontracts.PaymentFileProcessed;
import mz.co.moseguros.paymentcontracts.PaymentFileRejected;
import mz.co.moseguros.paymentcontracts.PaymentSourcePolled;
import mz.co.moseguros.shared.domain.Ids;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Registo dos ficheiros de movimentos e das leituras das fontes (S5-10 §4), com a idempotência por
 * {@code inbound_message}. Corre em {@code SYSTEM} (quem chama, o {@link PaymentFileEventsListener}) e filtra
 * sempre pela seguradora da mensagem.
 */
@Service
public class PaymentFileEventsService {

    static final String ACTOR = "PaymentFileEventsListener";
    private static final Logger log = LoggerFactory.getLogger(PaymentFileEventsService.class);

    private final InboundMessageRepository inbound;
    private final FileImportRepository files;
    private final InsurerPaymentMethodRepository methods;
    private final PaymentAlerts alerts;
    private final ObjectProvider<MeterRegistry> meters;
    private final Clock clock;

    public PaymentFileEventsService(InboundMessageRepository inbound, FileImportRepository files,
                                    InsurerPaymentMethodRepository methods, PaymentAlerts alerts,
                                    ObjectProvider<MeterRegistry> meters, Clock clock) {
        this.inbound = inbound;
        this.files = files;
        this.methods = methods;
        this.alerts = alerts;
        this.meters = meters;
        this.clock = clock;
    }

    /** {@code PaymentFileProcessed} → {@code file_import} {@code PROCESSED}. @return {@code false} se repetida */
    @Transactional
    public boolean processed(PaymentFileProcessed m) {
        InboundMessage message = register(m.messageId(), InboundMessage.PAYMENT_FILE_PROCESSED, m.insurerId(),
                m.sourceId());
        if (message == null) {
            return false;
        }
        files.save(FileImport.processed(m));
        message.complete(Outcome.RECORDED, null, null);
        inbound.save(message);
        log.info("Payment file processed insurerId={} sourceId={} format={} formatFileId={} records={}",
                m.insurerId(), m.sourceId(), m.fileFormat(), m.formatFileId(), m.recordCount());
        return true;
    }

    /** {@code PaymentFileRejected} → {@code file_import} {@code REJECTED} e alerta. @return {@code false} se repetida */
    @Transactional
    public boolean rejected(PaymentFileRejected m) {
        InboundMessage message = register(m.messageId(), InboundMessage.PAYMENT_FILE_REJECTED, m.insurerId(),
                m.sourceId());
        if (message == null) {
            return false;
        }
        files.save(FileImport.rejected(m));
        message.complete(Outcome.RECORDED, null, null);
        inbound.save(message);
        MeterRegistry registry = meters.getIfAvailable();
        if (registry != null) {
            registry.counter("moseguros.payment.files.rejected").increment();
        }
        Map<String, String> tags = new LinkedHashMap<>();
        tags.put("sourceId", m.sourceId().toString());
        tags.put("format", m.fileFormat());
        tags.put("formatFileId", String.valueOf(m.formatFileId()));
        tags.put("reason", m.reason().name());
        tags.put("lineNo", String.valueOf(m.lineNo()));
        alerts.raise("PAYMENT_FILE_REJECTED", m.insurerId(), tags);
        return true;
    }

    /**
     * {@code PaymentSourcePolled} → colunas {@code last_poll_*} da fonte, só se a fonte for desta seguradora e a
     * leitura for mais recente do que a gravada.
     *
     * @return o resultado gravado em {@code inbound_message}, ou {@code null} se a mensagem for repetida
     */
    @Transactional
    public Outcome polled(PaymentSourcePolled m) {
        InboundMessage message = register(m.messageId(), InboundMessage.PAYMENT_SOURCE_POLLED, m.insurerId(),
                m.sourceId());
        if (message == null) {
            return null;
        }
        Outcome outcome;
        if (!methods.isFileSourceOf(m.sourceId(), m.insurerId())) {
            log.error("Poll result for a source that is not of the insurer ignored insurerId={} sourceId={} "
                    + "messageId={}", m.insurerId(), m.sourceId(), m.messageId());
            outcome = Outcome.IGNORED;
        } else if (methods.recordPoll(m.sourceId(), m.insurerId(), m.startedAt(), m.finishedAt(), m.outcome(),
                m.errorCode() == null ? null : m.errorCode().name()) == 1) {
            outcome = Outcome.RECORDED;
            if (PaymentSourcePolled.OUTCOME_ERROR.equals(m.outcome())) {
                log.warn("Payment file source poll failed insurerId={} sourceId={} errorCode={}", m.insurerId(),
                        m.sourceId(), m.errorCode());
            }
        } else {
            log.info("Out-of-order poll result ignored insurerId={} sourceId={} startedAt={}", m.insurerId(),
                    m.sourceId(), m.startedAt());
            outcome = Outcome.IGNORED;
        }
        // O update nativo limpou o contexto de persistência: grava a linha de novo pelo id.
        InboundMessage fresh = inbound.findById(message.getId()).orElseThrow();
        fresh.complete(outcome, null, null);
        inbound.save(fresh);
        return outcome;
    }

    /** Insere a mensagem em {@code inbound_message}; {@code null} se já existia (repetição). */
    private InboundMessage register(String messageId, String type, UUID insurerId, UUID sourceId) {
        Instant now = clock.instant();
        if (inbound.insertIfAbsent(Ids.newId(), messageId, type, insurerId, sourceId, null, Outcome.IGNORED.name(),
                now, ACTOR) == 0) {
            MeterRegistry registry = meters.getIfAvailable();
            if (registry != null) {
                registry.counter("moseguros.payment.inbound.duplicates").increment();
            }
            log.info("Duplicate payment file event ignored messageId={} type={}", messageId, type);
            return null;
        }
        return inbound.findByMessageId(messageId).orElseThrow();
    }
}
