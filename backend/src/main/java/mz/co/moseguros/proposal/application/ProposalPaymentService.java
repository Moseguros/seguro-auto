package mz.co.moseguros.proposal.application;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import mz.co.moseguros.identity.CustomerView;
import mz.co.moseguros.identity.IdentityApi;
import mz.co.moseguros.notification.DeliveryResult;
import mz.co.moseguros.notification.NotificationApi;
import mz.co.moseguros.payment.CreateIntentCommand;
import mz.co.moseguros.payment.InitiatedVia;
import mz.co.moseguros.payment.IntentView;
import mz.co.moseguros.payment.PaymentApi;
import mz.co.moseguros.payment.PaymentMethod;
import mz.co.moseguros.payment.PaymentMethodOption;
import mz.co.moseguros.payment.PaymentTargetType;
import mz.co.moseguros.proposal.ProposalStatus;
import mz.co.moseguros.proposal.domain.Proposal;
import mz.co.moseguros.proposal.domain.ProposalRepository;
import mz.co.moseguros.shared.audit.ActorType;
import mz.co.moseguros.shared.audit.AuditEventRepository;
import mz.co.moseguros.shared.audit.AuditRecord;
import mz.co.moseguros.shared.audit.AuditService;
import mz.co.moseguros.shared.domain.Money;
import mz.co.moseguros.shared.domain.Msisdn;
import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.error.ErrorCodes;
import mz.co.moseguros.shared.format.MoneyFormat;
import mz.co.moseguros.shared.idempotency.IdempotencyKeys;
import mz.co.moseguros.shared.security.CurrentUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Pagamento das propostas (S5-02 §7, módulo {@code proposal}): formas de pagamento e criação da
 * intenção pelo cliente ({@code PaymentApi}), e o link de pagamento que o mediador envia ao cliente por
 * SMS. Quem paga é sempre o cliente, directamente à seguradora; o mediador nunca recebe dinheiro.
 */
@Service
public class ProposalPaymentService {

    static final String LINK_SENT = "PROPOSAL.PAYMENT_LINK_SENT";
    static final String LINK_SMS = "proposal.payment-link.sms";
    static final int MAX_LINKS_PER_DAY = 3;
    static final Duration LINK_WINDOW = Duration.ofHours(24);

    private static final Logger log = LoggerFactory.getLogger(ProposalPaymentService.class);

    private final ProposalRepository proposals;
    private final ProposalQueryService queries;
    private final PaymentApi payments;
    private final IdentityApi identity;
    private final NotificationApi notifications;
    private final AuditService audit;
    private final AuditEventRepository auditEvents;
    private final CurrentUser currentUser;
    private final Clock clock;
    private final String webBaseUrl;

    public ProposalPaymentService(ProposalRepository proposals, ProposalQueryService queries, PaymentApi payments,
                                  IdentityApi identity, NotificationApi notifications, AuditService audit,
                                  AuditEventRepository auditEvents, CurrentUser currentUser, Clock clock,
                                  @Value("${moseguros.web.base-url:http://localhost:5173}") String webBaseUrl) {
        this.proposals = proposals;
        this.queries = queries;
        this.payments = payments;
        this.identity = identity;
        this.notifications = notifications;
        this.audit = audit;
        this.auditEvents = auditEvents;
        this.currentUser = currentUser;
        this.clock = clock;
        this.webBaseUrl = webBaseUrl.endsWith("/") ? webBaseUrl.substring(0, webBaseUrl.length() - 1) : webBaseUrl;
    }

    /** Formas de pagamento de uma proposta, para o ecrã de pagamento. */
    public record PaymentMethods(Proposal proposal, boolean paid, List<PaymentMethodOption> methods,
                                 Optional<IntentView> activeIntent) {
    }

    /**
     * {@code listProposalPaymentMethods}: só a proposta do próprio cliente (senão 404); {@code PAID} ou
     * {@code CONVERTED} → {@code paid = true} sem métodos; fora de {@code ACCEPTED} → 409
     * {@code PAYMENT_TARGET_NOT_PAYABLE}.
     */
    @PreAuthorize("hasAuthority('self:payments')")
    @Transactional(readOnly = true)
    public PaymentMethods methods(UUID proposalId) {
        Proposal p = own(proposalId);
        if (p.getStatus() == ProposalStatus.PAID || p.getStatus() == ProposalStatus.CONVERTED) {
            return new PaymentMethods(p, true, List.of(), Optional.empty());
        }
        if (p.getStatus() != ProposalStatus.ACCEPTED) {
            throw notPayable(p);
        }
        return new PaymentMethods(p, false,
                payments.availableMethods(p.getInsurerId(), Money.ofCentavos(p.getTotalAmount())),
                payments.activeIntent(PaymentTargetType.PROPOSAL, p.getId()));
    }

    /**
     * {@code createProposalPaymentIntent}: cria (ou devolve, pela chave) a intenção de pagamento.
     *
     * @param idempotencyKey valor do cabeçalho {@code Idempotency-Key}
     */
    @PreAuthorize("hasAuthority('self:payments')")
    @Transactional
    public IntentView createIntent(UUID proposalId, PaymentMethod method, Msisdn payerMsisdn, InitiatedVia channel,
                                   String idempotencyKey) {
        UUID key = IdempotencyKeys.require(idempotencyKey);
        return payments.createIntent(new CreateIntentCommand(PaymentTargetType.PROPOSAL, proposalId, method,
                payerMsisdn, key, channel, currentUser.id()));
    }

    /** Resultado de {@code sendProposalPaymentLink}. */
    public record LinkSent(String sentTo, Instant sentAt) {
    }

    /**
     * {@code sendProposalPaymentLink}: SMS ao cliente com o link de pagamento, só para propostas
     * {@code ACCEPTED} deste mediador, no máximo {@value #MAX_LINKS_PER_DAY} por proposta em 24 h.
     */
    @PreAuthorize("hasAuthority('payment:link-for-client')")
    @Transactional
    public LinkSent sendPaymentLink(UUID proposalId) {
        UUID orgId = currentUser.orgId();
        Proposal p = proposals.findById(proposalId)
                .filter(x -> orgId != null && orgId.equals(x.getBrokerOrgId()))
                .orElseThrow(ProposalQueryService::notFound);
        if (p.getStatus() != ProposalStatus.ACCEPTED) {
            throw notPayable(p);
        }
        CustomerView customer = identity.customer(p.getCustomerUserId()).orElse(null);
        if (customer == null || customer.msisdn() == null) {
            throw DomainException.of(ErrorCodes.CUSTOMER_HAS_NO_MSISDN, "The customer has no mobile number");
        }
        Instant now = clock.instant();
        List<Instant> sent = auditEvents.occurredAtSince(LINK_SENT, "Proposal", p.getId().toString(),
                now.minus(LINK_WINDOW));
        if (sent.size() >= MAX_LINKS_PER_DAY) {
            long retryAfter = Math.max(1, Duration.between(now, sent.get(0).plus(LINK_WINDOW)).toSeconds());
            throw DomainException.of(ErrorCodes.PAYMENT_LINK_RATE_LIMITED,
                    "The payment link was already sent " + MAX_LINKS_PER_DAY + " times in 24 hours")
                    .withProperty(DomainException.RETRY_AFTER_SECONDS, retryAfter);
        }
        Msisdn msisdn = customer.msisdn();
        String language = "en".equals(customer.preferredLanguage()) ? "en" : "pt";
        String link = webBaseUrl + "/cliente/propostas/" + p.getId() + "/pagar?origem=mediador";
        Object[] args = {p.getNumber(), p.getInsurerName(),
                MoneyFormat.format(Money.ofCentavos(p.getTotalAmount()), language), link};
        DeliveryResult result = notifications.sendSms(msisdn.e164(), LINK_SMS, args, language, "PAYMENT", p.getId());
        audit.record(new AuditRecord(ActorType.STAFF, currentUser.id().toString(), p.getInsurerId(), LINK_SENT,
                "Proposal", p.getId().toString(), null, Map.of("msisdn", msisdn.masked(),
                "status", result.status().name())));
        log.info("Payment link sent proposalId={} to={} status={}", p.getId(), msisdn.masked(), result.status());
        return new LinkSent(msisdn.masked(), now);
    }

    private Proposal own(UUID proposalId) {
        Proposal p = queries.accessible(proposalId);
        if (!p.getCustomerUserId().equals(currentUser.id())) {
            throw ProposalQueryService.notFound();
        }
        return p;
    }

    private static DomainException notPayable(Proposal p) {
        return DomainException.of(ErrorCodes.PAYMENT_TARGET_NOT_PAYABLE,
                "The proposal cannot be paid (status " + p.getStatus() + ")");
    }
}
