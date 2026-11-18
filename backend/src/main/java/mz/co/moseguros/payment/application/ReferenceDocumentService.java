package mz.co.moseguros.payment.application;

import java.time.Clock;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import mz.co.moseguros.document.DocumentApi;
import mz.co.moseguros.document.FileClassification;
import mz.co.moseguros.document.FileRef;
import mz.co.moseguros.document.PdfTarget;
import mz.co.moseguros.notification.DeliveryResult;
import mz.co.moseguros.notification.NotificationApi;
import mz.co.moseguros.payment.PaymentMethod;
import mz.co.moseguros.payment.domain.PaymentIntent;
import mz.co.moseguros.payment.domain.PaymentIntentRepository;
import mz.co.moseguros.payment.domain.ReferencePayment;
import mz.co.moseguros.payment.domain.ReferencePaymentRepository;
import mz.co.moseguros.shared.audit.ActorType;
import mz.co.moseguros.shared.audit.AuditRecord;
import mz.co.moseguros.shared.audit.AuditService;
import mz.co.moseguros.shared.domain.Msisdn;
import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.error.ErrorCodes;
import mz.co.moseguros.shared.format.MoneyFormat;
import mz.co.moseguros.shared.security.CurrentUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Entidade e referência por SMS e em PDF, a pedido do pagador (S5-02 §7). A referência leva sempre o
 * montante exacto (a rede recusa outro) e a frase de que o pagamento é feito directamente à seguradora.
 */
@Service
public class ReferenceDocumentService {

    /** {@code outbound_message.purpose}. */
    static final String PURPOSE = "PAYMENT";
    static final String SMS_TEMPLATE = "payment.reference.sms";
    /** Modelo de PDF ({@code PdfTemplates.PAYMENT_REFERENCE}). */
    static final String PDF_TEMPLATE = "payment-reference";

    private static final Logger log = LoggerFactory.getLogger(ReferenceDocumentService.class);
    private static final DateTimeFormatter SMS_DATE =
            DateTimeFormatter.ofPattern("dd/MM HH:mm").withZone(PaymentQueryService.ZONE);

    private final PaymentIntentRepository intents;
    private final ReferencePaymentRepository references;
    private final PaymentSystemLookups lookups;
    private final NotificationApi notifications;
    private final DocumentApi documents;
    private final AuditService audit;
    private final CurrentUser currentUser;
    private final Clock clock;

    public ReferenceDocumentService(PaymentIntentRepository intents, ReferencePaymentRepository references,
                                    PaymentSystemLookups lookups, NotificationApi notifications,
                                    DocumentApi documents, AuditService audit, CurrentUser currentUser, Clock clock) {
        this.intents = intents;
        this.references = references;
        this.lookups = lookups;
        this.notifications = notifications;
        this.documents = documents;
        this.audit = audit;
        this.currentUser = currentUser;
        this.clock = clock;
    }

    /** Resultado de {@code sendReferenceSms}. */
    public record SmsSent(String sentTo) {
    }

    /**
     * {@code sendReferenceSms}: {@code payment.reference.sms} para o telemóvel indicado (qualquer
     * operadora), no máximo {@value ReferencePayment#MAX_SMS} vezes por referência.
     *
     * @param language {@code en} ou outro ({@code pt})
     */
    @PreAuthorize("hasAuthority('self:payments')")
    @Transactional
    public SmsSent sendSms(UUID intentId, Msisdn msisdn, String language) {
        PaymentIntent intent = loadOwn(intentId);
        ReferencePayment ref = requireActive(intent);
        if (ref.getSmsSentCount() >= ReferencePayment.MAX_SMS) {
            throw DomainException.of(ErrorCodes.REFERENCE_SMS_RATE_LIMITED,
                    "The reference was already sent " + ReferencePayment.MAX_SMS + " times");
        }
        String lang = "en".equals(language) ? "en" : "pt";
        Object[] args = {MoneyFormat.format(ref.amount(), lang), ref.getEntity(), grouped(ref.getReference()),
                SMS_DATE.format(ref.getValidUntil()), insurerName(intent)};
        DeliveryResult result = notifications.sendSms(msisdn.e164(), SMS_TEMPLATE, args, lang, PURPOSE, intentId);
        ref.smsSent();
        references.save(ref);
        Map<String, Object> after = new LinkedHashMap<>();
        after.put("msisdn", msisdn.masked());
        after.put("status", result.status().name());
        after.put("smsSentCount", ref.getSmsSentCount());
        audit.record(new AuditRecord(ActorType.CUSTOMER, currentUser.id().toString(), intent.getInsurerId(),
                "PAYMENT_INTENT.REFERENCE_SMS", "PaymentIntent", intentId.toString(), null, after));
        log.info("Payment reference SMS intentId={} reference={} to={} status={}", intentId, intent.getReference(),
                msisdn.masked(), result.status());
        return new SmsSent(msisdn.masked());
    }

    /**
     * {@code createReferenceDocument}: o PDF da referência ({@code PAYMENT_REFERENCE}), gerado uma vez e
     * guardado em {@code document_file_id}; descarrega-se em {@code GET /files/{fileId}}.
     */
    @PreAuthorize("hasAuthority('self:payments')")
    @Transactional
    public UUID document(UUID intentId, Locale locale) {
        PaymentIntent intent = loadOwn(intentId);
        if (intent.getMethod() != PaymentMethod.BANK_REFERENCE) {
            throw notReference();
        }
        ReferencePayment ref = references.lockByIntentId(intentId).orElseThrow(ReferenceDocumentService::notReference);
        if (ref.getDocumentFileId() != null) {
            return ref.getDocumentFileId();
        }
        Locale effective = locale != null && "en".equals(locale.getLanguage()) ? Locale.ENGLISH
                : Locale.forLanguageTag("pt");
        Map<String, Object> model = new HashMap<>();
        model.put("insurerName", insurerName(intent));
        model.put("entity", ref.getEntity());
        model.put("reference", grouped(ref.getReference()));
        model.put("amount", ref.amount());
        model.put("validUntil", ref.getValidUntil());
        model.put("targetLabel", intent.getTargetLabel());
        model.put("intentReference", intent.getReference());
        model.put("generatedAt", clock.instant());
        FileRef file = documents.renderPdf(PDF_TEMPLATE, model, effective, new PdfTarget(
                PaymentFileAccessPolicy.OWNER_TYPE, intentId, "PAYMENT_REFERENCE", intent.getInsurerId(),
                FileClassification.PERSONAL, "referencia-" + intent.getReference() + ".pdf"));
        ref.recordDocument(file.id());
        references.save(ref);
        return file.id();
    }

    /** {@code 00548781108} → {@code 005 487 811 08}. */
    static String grouped(String reference) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < reference.length(); i++) {
            if (i > 0 && i % 3 == 0) {
                sb.append(' ');
            }
            sb.append(reference.charAt(i));
        }
        return sb.toString();
    }

    private ReferencePayment requireActive(PaymentIntent intent) {
        if (intent.getMethod() != PaymentMethod.BANK_REFERENCE) {
            throw notReference();
        }
        ReferencePayment ref = references.lockByIntentId(intent.getId())
                .orElseThrow(ReferenceDocumentService::notReference);
        if (!ref.isActive()) {
            throw DomainException.of(ErrorCodes.REFERENCE_NOT_ACTIVE, "The payment reference is no longer active");
        }
        return ref;
    }

    private PaymentIntent loadOwn(UUID intentId) {
        return intents.findById(intentId).filter(i -> i.getPayerUserId().equals(currentUser.id()))
                .orElseThrow(() -> DomainException.of(ErrorCodes.PAYMENT_INTENT_NOT_FOUND,
                        "Payment intent not found"));
    }

    private String insurerName(PaymentIntent intent) {
        String name = lookups.insurerNames(Set.of(intent.getInsurerId())).get(intent.getInsurerId());
        return name == null ? "" : name;
    }

    private static DomainException notReference() {
        return DomainException.of(ErrorCodes.NOT_A_REFERENCE_PAYMENT, "The payment intent is not a bank reference");
    }
}
