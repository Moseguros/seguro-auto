package mz.co.moseguros.payment.application;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import mz.co.moseguros.notification.NotificationApi;
import mz.co.moseguros.notification.NotificationCategory;
import mz.co.moseguros.payment.PaymentFailed;
import mz.co.moseguros.payment.PaymentSucceeded;
import mz.co.moseguros.payment.ReferenceGenerated;
import mz.co.moseguros.payment.domain.PaymentIntent;
import mz.co.moseguros.payment.domain.PaymentIntentRepository;
import mz.co.moseguros.shared.domain.Money;
import mz.co.moseguros.shared.tenant.RunAsSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

/**
 * Avisos de pagamento ao cliente (S6-04 §9; modelo transversal §1.1): {@code PaymentSucceeded} →
 * {@code PAYMENT_SUCCEEDED}, {@code PaymentFailed} ({@code FAILED} ou {@code EXPIRED}) → {@code PAYMENT_FAILED},
 * {@code ReferenceGenerated} → {@code PAYMENT_REFERENCE_GENERATED}, todos na categoria obrigatória
 * {@code PAYMENT}, para o {@code payerUserId} do evento. Depois do commit, numa transacção nova, em
 * {@code SYSTEM}; a intenção lê-se pelo id do evento. O aviso {@code proposal.paid.*} do S5-02 (decisão D36)
 * continua à parte (Questões do S6-04).
 */
@Component
public class PaymentNotificationsListener {

    static final String SUCCEEDED = "PAYMENT_SUCCEEDED";
    static final String FAILED = "PAYMENT_FAILED";
    static final String REFERENCE = "PAYMENT_REFERENCE_GENERATED";

    private static final Logger log = LoggerFactory.getLogger(PaymentNotificationsListener.class);

    private final PaymentIntentRepository intents;
    private final PaymentSystemLookups lookups;
    private final NotificationApi notifications;

    public PaymentNotificationsListener(PaymentIntentRepository intents, PaymentSystemLookups lookups,
                                        NotificationApi notifications) {
        this.intents = intents;
        this.lookups = lookups;
        this.notifications = notifications;
    }

    @ApplicationModuleListener
    @RunAsSystem
    public void on(PaymentSucceeded event) {
        Map<String, Object> params = links(event.intentId());
        params.put("paymentReference", event.reference());
        params.put("amount", event.amount());
        params.put("insurerName", insurerName(event.insurerId()));
        notifications.notify(event.payerUserId(), SUCCEEDED, params, NotificationCategory.PAYMENT);
    }

    @ApplicationModuleListener
    @RunAsSystem
    public void on(PaymentFailed event) {
        PaymentIntent intent = intents.findById(event.intentId()).orElse(null);
        if (intent == null || !intent.getPayerUserId().equals(event.payerUserId())) {
            log.warn("Payment intent {} not found for the {} notification", event.intentId(), FAILED);
            return;
        }
        Map<String, Object> params = links(event.intentId());
        params.put("paymentReference", intent.getReference());
        params.put("amount", Money.ofCentavos(intent.getAmount()));
        notifications.notify(event.payerUserId(), FAILED, params, NotificationCategory.PAYMENT);
    }

    @ApplicationModuleListener
    @RunAsSystem
    public void on(ReferenceGenerated event) {
        Map<String, Object> params = links(event.intentId());
        params.put("amount", event.amount());
        params.put("insurerName", insurerName(event.insurerId()));
        params.put("entity", event.entity());
        params.put("reference", event.reference());
        params.put("validUntil", event.validUntil());
        notifications.notify(event.payerUserId(), REFERENCE, params, NotificationCategory.PAYMENT);
    }

    private String insurerName(UUID insurerId) {
        return lookups.insurerNames(Set.of(insurerId)).getOrDefault(insurerId, "");
    }

    private static Map<String, Object> links(UUID intentId) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("webPath", "/cliente/pagamentos/" + intentId);
        params.put("deepLink", "moseguros://payment/" + intentId);
        return params;
    }
}
