package mz.co.moseguros.proposal.application;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.micrometer.core.instrument.MeterRegistry;
import mz.co.moseguros.identity.CustomerView;
import mz.co.moseguros.identity.IdentityApi;
import mz.co.moseguros.identity.StaffContact;
import mz.co.moseguros.notification.DeliveryResult;
import mz.co.moseguros.notification.NotificationApi;
import mz.co.moseguros.payment.PaymentSucceeded;
import mz.co.moseguros.proposal.domain.Proposal;
import mz.co.moseguros.shared.format.MoneyFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Aviso de pagamento recebido a todos os actores da aquisição (S5-02 §9.1; decisão do proprietário do
 * produto de 18/11/2026): o cliente (e-mail verificado; sem ele, SMS), os utilizadores activos do
 * mediador com {@code proposal:create-for-client} (se a proposta tiver mediador) e os utilizadores
 * activos da seguradora com {@code payment:read}. Todos com {@code purpose = PAYMENT} e
 * {@code correlationId} = intenção, na língua de cada um.
 *
 * <p>Os dados são lidos já, na transacção do {@code ProposalPaymentListener}; os envios saem depois do
 * commit (uma falha fica em {@code outbound_message} e nunca desfaz o {@code PAID}; um rollback não envia
 * nada). O método aparece pelo nome; nunca o telemóvel completo nem dados do documento.
 */
@Component
public class ProposalPaymentNotifier {

    /** {@code outbound_message.purpose}. */
    static final String PURPOSE = "PAYMENT";
    static final String CUSTOMER_EMAIL = "proposal.paid.email";
    static final String CUSTOMER_SMS = "proposal.paid.sms";
    static final String BROKER_EMAIL = "proposal.paid.broker.email";
    static final String INSURER_EMAIL = "proposal.paid.insurer.email";
    static final String BROKER_PERMISSION = "proposal:create-for-client";
    static final String INSURER_PERMISSION = "payment:read";
    static final String NO_RECIPIENT_METRIC = "moseguros.payment.notifications.no_recipient";

    private static final Logger log = LoggerFactory.getLogger(ProposalPaymentNotifier.class);

    private final NotificationApi notifications;
    private final IdentityApi identity;
    private final ObjectProvider<MeterRegistry> meters;
    private final TransactionTemplate newTransaction;
    private final String webBaseUrl;

    public ProposalPaymentNotifier(NotificationApi notifications, IdentityApi identity,
                                   ObjectProvider<MeterRegistry> meters, PlatformTransactionManager transactionManager,
                                   @Value("${moseguros.web.base-url:http://localhost:5173}") String webBaseUrl) {
        this.notifications = notifications;
        this.identity = identity;
        this.meters = meters;
        this.newTransaction = new TransactionTemplate(transactionManager);
        this.newTransaction.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        this.webBaseUrl = webBaseUrl.endsWith("/") ? webBaseUrl.substring(0, webBaseUrl.length() - 1) : webBaseUrl;
    }

    /** Mensagem a enviar depois do commit. */
    private record Message(boolean sms, String to, String template, Object[] args, String language) {
    }

    /** Proposta paga ({@code ACCEPTED → PAID}): lê os destinatários e agenda os envios para depois do commit. */
    public void paid(Proposal p, PaymentSucceeded event) {
        UUID intentId = event.intentId();
        String method = "payment.method." + event.method().name();
        List<Message> messages = new ArrayList<>();
        CustomerView customer = identity.customer(p.getCustomerUserId()).orElse(null);
        String customerName = customerName(customer, p);

        // Cliente: e-mail verificado; sem ele, SMS para o telemóvel.
        if (customer != null && customer.email() != null && customer.emailVerified()) {
            String lang = language(customer.preferredLanguage());
            messages.add(new Message(false, customer.email(), CUSTOMER_EMAIL, new Object[] {
                    firstName(customerName, lang), money(event, lang), p.getNumber(), p.getInsurerName(),
                    resolvable(method)}, lang));
        } else if (customer != null && customer.msisdn() != null) {
            String lang = language(customer.preferredLanguage());
            messages.add(new Message(true, customer.msisdn().e164(), CUSTOMER_SMS, new Object[] {
                    money(event, lang), p.getNumber(), p.getInsurerName()}, lang));
        } else {
            noRecipient("customer", p);
        }

        // Mediador (só nas propostas do mediador).
        if (p.getBrokerOrgId() != null) {
            List<StaffContact> brokers = withEmail(identity.staffWithPermission(p.getBrokerOrgId(),
                    BROKER_PERMISSION));
            if (brokers.isEmpty()) {
                noRecipient("broker", p);
            }
            for (StaffContact s : brokers) {
                String lang = language(s.preferredLanguage());
                messages.add(new Message(false, s.email(), BROKER_EMAIL, new Object[] {p.getNumber(), customerName,
                        money(event, lang), resolvable(method), p.getInsurerName()}, lang));
            }
        }

        // Seguradora: quem trata os pagamentos.
        List<StaffContact> insurer = withEmail(identity.staffWithPermission(p.getInsurerId(), INSURER_PERMISSION));
        if (insurer.isEmpty()) {
            noRecipient("insurer", p);
        }
        String link = webBaseUrl + "/seguradora/pagamentos/" + intentId;
        for (StaffContact s : insurer) {
            String lang = language(s.preferredLanguage());
            messages.add(new Message(false, s.email(), INSURER_EMAIL, new Object[] {p.getNumber(), event.reference(),
                    money(event, lang), customerName, resolvable(method), link}, lang));
        }

        UUID proposalId = p.getId();
        afterCommit(() -> messages.forEach(m -> send(proposalId, intentId, m)));
    }

    private void send(UUID proposalId, UUID intentId, Message m) {
        try {
            DeliveryResult result = m.sms()
                    ? notifications.sendSms(m.to(), m.template(), m.args(), m.language(), PURPOSE, intentId)
                    : notifications.sendEmail(m.to(), m.template(), m.args(), m.language(), PURPOSE, intentId);
            if (result.failed()) {
                log.warn("Proposal {}: payment notice {} not delivered ({})", proposalId, m.template(),
                        result.failureReason());
            }
        } catch (RuntimeException e) {
            log.warn("Proposal {}: payment notice {} failed ({})", proposalId, m.template(),
                    e.getClass().getSimpleName());
        }
    }

    private void afterCommit(Runnable action) {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            action.run();
            return;
        }
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                newTransaction.executeWithoutResult(status -> action.run());
            }
        });
    }

    private void noRecipient(String group, Proposal p) {
        log.warn("Proposal {}: no recipient for the payment notice group={}", p.getId(), group);
        MeterRegistry registry = meters.getIfAvailable();
        if (registry != null) {
            registry.counter(NO_RECIPIENT_METRIC, "group", group).increment();
        }
    }

    private static List<StaffContact> withEmail(List<StaffContact> staff) {
        return staff.stream().filter(s -> s.email() != null && !s.email().isBlank()).toList();
    }

    private static String customerName(CustomerView customer, Proposal p) {
        if (customer != null && customer.fullName() != null && !customer.fullName().isBlank()) {
            return customer.fullName().strip();
        }
        String name = p.getPolicyholder() == null ? null : p.getPolicyholder().path("fullName").asText(null);
        return name == null ? "" : name;
    }

    private static String firstName(String fullName, String language) {
        String first = ProposalNotifier.firstName(fullName);
        return first.isEmpty() ? ("en".equals(language) ? "customer" : "cliente") : first;
    }

    private static String money(PaymentSucceeded event, String language) {
        return MoneyFormat.format(event.amount(), language);
    }

    private static DefaultMessageSourceResolvable resolvable(String code) {
        return new DefaultMessageSourceResolvable(new String[] {code}, code);
    }

    private static String language(String preferred) {
        return "en".equals(preferred) ? "en" : "pt";
    }
}
