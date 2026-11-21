package mz.co.moseguros.proposal.application;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import mz.co.moseguros.identity.CustomerView;
import mz.co.moseguros.identity.IdentityApi;
import mz.co.moseguros.identity.StaffContact;
import mz.co.moseguros.notification.DeliveryResult;
import mz.co.moseguros.notification.NotificationApi;
import mz.co.moseguros.proposal.domain.Proposal;
import mz.co.moseguros.shared.domain.Money;
import mz.co.moseguros.shared.format.MoneyFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * E-mails da proposta (S4-05 §9): ao cliente na submissão e aos subscritores no alerta de SLA; ao cliente
 * quando um mediador lhe prepara uma proposta (S4-06 §8) ({@code NotificationApi.sendEmail},
 * {@code purpose = PROPOSAL}, {@code correlationId} = id da proposta). Desde o S6-04 a aceitação, a recusa,
 * o pedido de informação e a expiração chegam ao cliente pelo {@link ProposalNotificationsListener}
 * ({@code NotificationApi.notify}: centro de notificações, push, WhatsApp, SMS ou e-mail).
 *
 * <p>O cliente só recebe se tiver e-mail verificado, na sua língua; sem ele vê o estado na app e na
 * web. Os dados da mensagem são lidos já, na transacção de quem chama; o envio fica para depois do
 * commit (uma falha do envio fica em {@code outbound_message} e não desfaz a decisão; um rollback não
 * envia nada). Os logs têm o id da proposta, nunca o e-mail.
 */
@Component
public class ProposalNotifier {

    /** {@code outbound_message.purpose}. */
    static final String PURPOSE = "PROPOSAL";
    static final String SUBMITTED = "proposal.submitted.email";
    static final String SLA_ALERT = "proposal.sla-alert.email";
    static final String CREATED_BY_BROKER = "proposal.created-by-broker.email";

    private static final Logger log = LoggerFactory.getLogger(ProposalNotifier.class);
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy").withZone(Proposal.ZONE);
    private static final DateTimeFormatter DATE_TIME =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm").withZone(Proposal.ZONE);

    private final NotificationApi notifications;
    private final IdentityApi identity;
    private final ProposalProperties properties;
    private final TransactionTemplate newTransaction;
    private final String webBaseUrl;

    public ProposalNotifier(NotificationApi notifications, IdentityApi identity, ProposalProperties properties,
                            PlatformTransactionManager transactionManager,
                            @Value("${moseguros.web.base-url:http://localhost:5173}") String webBaseUrl) {
        this.notifications = notifications;
        this.identity = identity;
        this.properties = properties;
        this.newTransaction = new TransactionTemplate(transactionManager);
        this.newTransaction.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        this.webBaseUrl = webBaseUrl.endsWith("/") ? webBaseUrl.substring(0, webBaseUrl.length() - 1) : webBaseUrl;
    }

    /**
     * Proposta preparada por um mediador em nome do cliente ({@code proposal.created-by-broker}, S4-06 §8):
     * só com e-mail verificado (uma conta criada pelo mediador não tem e-mail); depois do commit.
     */
    public void createdByBroker(Proposal p) {
        toCustomer(p, CREATED_BY_BROKER, (language, firstName) -> new Object[] {p.getNumber(), p.getBrokerName(),
                firstName.isEmpty() ? ("en".equals(language) ? "customer" : "cliente") : firstName,
                p.getInsurerName(), MoneyFormat.format(Money.ofCentavos(p.getTotalAmount()), language),
                customerLink(p)});
    }

    /** Proposta enviada à seguradora, em análise ({@code proposal.submitted}). */
    public void submitted(Proposal p) {
        toCustomer(p, SUBMITTED, (language, firstName) -> new Object[] {p.getNumber(), p.getInsurerName(), firstName,
                date(p.getSlaDueAt()), customerLink(p), date(p.getExpiresAt())});
    }

    /**
     * Alerta de SLA a um subscritor ({@code proposal.sla-alert}). Envia já: o {@code ProposalSlaJob}
     * chama-o depois de gravar {@code sla_alerted_at}, fora da transacção.
     *
     * @param reviewStartedAt e {@code dueAt} lidos antes de a proposta sair da transacção
     */
    public void slaAlert(Proposal p, StaffContact staff, Instant reviewStartedAt, Instant dueAt) {
        if (staff.email() == null) {
            return;
        }
        Object[] args = {p.getNumber(), Integer.toString(properties.decisionAlertDays()),
                staff.displayName() == null ? "" : staff.displayName(), dateTime(reviewStartedAt), dateTime(dueAt),
                webBaseUrl + "/seguradora/propostas/" + p.getId()};
        send(p.getId(), staff.email(), SLA_ALERT, args, staff.preferredLanguage());
    }

    /** Argumentos de uma mensagem ao cliente, na sua língua. */
    @FunctionalInterface
    private interface Args {
        Object[] of(String language, String firstName);
    }

    private void toCustomer(Proposal p, String template, Args args) {
        CustomerView customer = identity.customer(p.getCustomerUserId()).orElse(null);
        if (customer == null || customer.email() == null || !customer.emailVerified()) {
            return; // sem e-mail verificado: o cliente vê o estado na app e na web (SMS e push no S6-04)
        }
        String language = "en".equals(customer.preferredLanguage()) ? "en" : "pt";
        Object[] values = args.of(language, firstName(customer.fullName()));
        UUID proposalId = p.getId();
        String to = customer.email();
        afterCommit(() -> send(proposalId, to, template, values, language));
    }

    private void afterCommit(Runnable action) {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            action.run();
            return;
        }
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                // Os recursos da transacção terminada ainda estão ligados: o registo da mensagem corre numa nova.
                newTransaction.executeWithoutResult(status -> action.run());
            }
        });
    }

    private void send(UUID proposalId, String to, String template, Object[] args, String language) {
        try {
            DeliveryResult result = notifications.sendEmail(to, template, args, language, PURPOSE, proposalId);
            if (result.failed()) {
                log.warn("Proposal {}: e-mail {} not delivered ({})", proposalId, template, result.failureReason());
            }
        } catch (RuntimeException e) {
            log.warn("Proposal {}: e-mail {} failed ({})", proposalId, template, e.getClass().getSimpleName());
        }
    }

    private String customerLink(Proposal p) {
        return webBaseUrl + "/cliente/propostas/" + p.getId();
    }

    static String firstName(String fullName) {
        if (fullName == null || fullName.isBlank()) {
            return "";
        }
        return fullName.strip().split("\\s+")[0];
    }

    private static String date(Instant instant) {
        return instant == null ? "" : DATE.format(instant);
    }

    private static String dateTime(Instant instant) {
        return instant == null ? "" : DATE_TIME.format(instant);
    }
}
