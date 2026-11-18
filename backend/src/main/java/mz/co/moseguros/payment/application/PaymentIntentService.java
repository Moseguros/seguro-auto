package mz.co.moseguros.payment.application;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import mz.co.moseguros.payment.CreateIntentCommand;
import mz.co.moseguros.payment.FailureCode;
import mz.co.moseguros.payment.IntentView;
import mz.co.moseguros.payment.PayableTarget;
import mz.co.moseguros.payment.PaymentMethod;
import mz.co.moseguros.payment.PaymentStatus;
import mz.co.moseguros.payment.PaymentTargetResolver;
import mz.co.moseguros.payment.application.PaymentSystemLookups.MethodConfig;
import mz.co.moseguros.payment.application.gateway.AsyncDispatcher;
import mz.co.moseguros.payment.application.gateway.DispatchMode;
import mz.co.moseguros.payment.application.gateway.GatewayRegistry;
import mz.co.moseguros.payment.application.gateway.InitiationResult;
import mz.co.moseguros.payment.application.gateway.PaymentGateway;
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
import mz.co.moseguros.shared.domain.Money;
import mz.co.moseguros.shared.domain.Msisdn;
import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.error.ErrorCodes;
import mz.co.moseguros.shared.error.FieldErrorCodes;
import mz.co.moseguros.shared.error.FieldValidationException;
import mz.co.moseguros.shared.idempotency.CanonicalJson;
import mz.co.moseguros.shared.idempotency.IdempotencyService;
import mz.co.moseguros.shared.idempotency.IdempotentResponse;
import mz.co.moseguros.shared.numbering.BusinessNumberGenerator;
import mz.co.moseguros.shared.numbering.BusinessPrefix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Criação de uma intenção de pagamento (S5-02 §4), numa só transacção e no âmbito de quem pede:
 * idempotência, alvo ({@link PaymentTargetResolver}), configuração da seguradora, telemóvel, intenções
 * activas do alvo, prazo, gravação e envio ao fornecedor. O dinheiro vai directamente para a conta da
 * seguradora.
 */
@Service
public class PaymentIntentService {

    /** {@code shared.idempotency_record.operation}. */
    public static final String OPERATION = "CREATE_PAYMENT_INTENT";
    static final String ACTOR = "PaymentIntentService";
    /** Prazo mínimo do alvo para aceitar um pagamento novo. */
    static final Duration MIN_TARGET_TIME = Duration.ofMinutes(5);

    private static final Logger log = LoggerFactory.getLogger(PaymentIntentService.class);

    private final List<PaymentTargetResolver> resolvers;
    private final PaymentIntentRepository intents;
    private final PaymentEventRepository paymentEvents;
    private final ReferencePaymentRepository references;
    private final PaymentSystemLookups lookups;
    private final GatewayRegistry gateways;
    private final AsyncDispatcher dispatcher;
    private final IntentStateService states;
    private final IntentViews views;
    private final IdempotencyService idempotency;
    private final BusinessNumberGenerator numbers;
    private final AuditService audit;
    private final ObjectMapper mapper;
    private final Clock clock;

    public PaymentIntentService(List<PaymentTargetResolver> resolvers, PaymentIntentRepository intents,
                                PaymentEventRepository paymentEvents, ReferencePaymentRepository references,
                                PaymentSystemLookups lookups, GatewayRegistry gateways, AsyncDispatcher dispatcher,
                                IntentStateService states, IntentViews views, IdempotencyService idempotency,
                                BusinessNumberGenerator numbers, AuditService audit, ObjectMapper mapper,
                                Clock clock) {
        this.resolvers = resolvers;
        this.intents = intents;
        this.paymentEvents = paymentEvents;
        this.references = references;
        this.lookups = lookups;
        this.gateways = gateways;
        this.dispatcher = dispatcher;
        this.states = states;
        this.views = views;
        this.idempotency = idempotency;
        this.numbers = numbers;
        this.audit = audit;
        this.mapper = mapper;
        this.clock = clock;
    }

    /** Ver {@code PaymentApi.createIntent}. */
    @Transactional
    public IntentView create(CreateIntentCommand cmd) {
        IdempotentResponse response = idempotency.execute(cmd.initiatedByUserId(), cmd.idempotencyKey(), OPERATION,
                requestHash(cmd), () -> {
                    IntentView view = doCreate(cmd);
                    return new IdempotentResponse(202, mapper.valueToTree(view), view.id());
                });
        try {
            return mapper.treeToValue(response.body(), IntentView.class);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Stored idempotent response is not an IntentView", e);
        }
    }

    /** {@code operation + "\n" + JSON canónico de { targetType, targetId, method, payerMsisdn, initiatedVia }}. */
    static String requestHash(CreateIntentCommand cmd) {
        Map<String, Object> request = new LinkedHashMap<>();
        request.put("targetType", cmd.targetType().name());
        request.put("targetId", cmd.targetId().toString());
        request.put("method", cmd.method().name());
        request.put("payerMsisdn", cmd.payerMsisdn() == null ? null : cmd.payerMsisdn().value());
        request.put("initiatedVia", cmd.initiatedVia().name());
        return CanonicalJson.requestHash(OPERATION, request);
    }

    private IntentView doCreate(CreateIntentCommand cmd) {
        PaymentTargetResolver resolver = resolvers.stream().filter(r -> r.supports(cmd.targetType())).findFirst()
                .orElseThrow(() -> new IllegalStateException("No PaymentTargetResolver for " + cmd.targetType()));
        // 5. Já pago. Vem antes do resolver: um alvo pago já não é pagável e a resposta tem de ser
        // ALREADY_PAID (o RLS só mostra as intenções do próprio pagador).
        if (intents.existsByTargetAndStatus(cmd.targetType(), cmd.targetId(), PaymentStatus.SUCCEEDED)) {
            throw DomainException.of(ErrorCodes.PAYMENT_TARGET_ALREADY_PAID, "The payment target is already paid");
        }
        // 2–4. Alvo, no âmbito de quem chama; só o próprio pagador paga (S5).
        PayableTarget target = resolver.resolve(cmd.targetType(), cmd.targetId());
        if (!target.payerUserId().equals(cmd.initiatedByUserId())) {
            throw DomainException.of(resolver.notFoundCode(), "Payment target not found");
        }
        // 6. Configuração e adaptador.
        MethodConfig config = lookups.enabledConfig(target.insurerId(), cmd.method())
                .filter(c -> withinLimits(c, target.amount()))
                .orElseThrow(PaymentIntentService::notAvailable);
        PaymentGateway gateway = gateways.find(cmd.method()).orElseThrow(PaymentIntentService::notAvailable);
        // 7. Telemóvel.
        Msisdn msisdn = null;
        if (cmd.method() == PaymentMethod.MPESA) {
            msisdn = requireMpesaMsisdn(cmd.payerMsisdn());
        }
        // 8. Intenções activas do alvo.
        List<PaymentIntent> active = intents.findByTarget(cmd.targetType(), cmd.targetId(),
                EnumSet.of(PaymentStatus.CREATED, PaymentStatus.PENDING, PaymentStatus.UNKNOWN));
        Optional<PaymentIntent> mpesa = active.stream().filter(PaymentIntent::isMpesa).findFirst();
        if (mpesa.isPresent()) {
            throw DomainException.of(ErrorCodes.PAYMENT_IN_PROGRESS, "A mobile payment is already in progress")
                    .withProperty("intentId", mpesa.get().getId().toString());
        }
        if (cmd.method() == PaymentMethod.BANK_REFERENCE) {
            for (PaymentIntent existing : active) {
                if (existing.getMethod() != PaymentMethod.BANK_REFERENCE
                        || existing.getStatus() != PaymentStatus.PENDING) {
                    continue;
                }
                Optional<ReferencePayment> ref = references.findByIntentId(existing.getId());
                if (ref.isEmpty() || !ref.get().isActive()) {
                    continue;
                }
                if (existing.getAmount() == target.amount().amount()) {
                    log.info("Reusing active payment reference intentId={} reference={}", existing.getId(),
                            existing.getReference());
                    return views.of(existing);
                }
                states.cancel(existing.getId(), FailureCode.SUPERSEDED, ACTOR);
            }
        }
        // 9. Prazo.
        Instant now = clock.instant();
        if (Duration.between(now, target.expiresAt()).compareTo(MIN_TARGET_TIME) < 0) {
            throw DomainException.of(ErrorCodes.PAYMENT_TARGET_NOT_PAYABLE, "The payment deadline is too close");
        }
        Instant expiresAt = cmd.method() == PaymentMethod.MPESA
                ? min(now.plus(Duration.ofMinutes(config.intentValidityMinutes())), target.expiresAt())
                : target.expiresAt();
        // 10. Gravar.
        PaymentIntent intent = intents.saveAndFlush(PaymentIntent.create(new PaymentIntent.New(
                numbers.next(BusinessPrefix.PAYMENT), target.insurerId(), target.payerUserId(), target.purpose(),
                cmd.targetType(), cmd.targetId(), target.description(), target.amount(), cmd.method(), config.id(),
                msisdn, cmd.idempotencyKey(), expiresAt, cmd.initiatedByUserId(), cmd.initiatedVia())));
        paymentEvents.save(PaymentEvent.of(intent, PaymentEventType.CREATED, null, PaymentStatus.CREATED, null, now));
        Map<String, Object> after = new LinkedHashMap<>();
        after.put("reference", intent.getReference());
        after.put("method", intent.getMethod().name());
        after.put("amount", intent.getAmount());
        after.put("target", intent.getTargetType().name() + ":" + intent.getTargetLabel());
        after.put("payerMsisdn", msisdn == null ? null : msisdn.masked());
        audit.record(new AuditRecord(ActorType.CUSTOMER, cmd.initiatedByUserId().toString(), intent.getInsurerId(),
                "PAYMENT_INTENT.CREATE", "PaymentIntent", intent.getId().toString(), null, after));
        log.info("Payment intent created intentId={} reference={} method={} status={}", intent.getId(),
                intent.getReference(), intent.getMethod(), intent.getStatus());
        // 11. Envio.
        if (gateway.dispatchMode() == DispatchMode.SYNC) {
            InitiationResult result = gateway.initiate(intent, config);
            intent = states.applyInitiation(intent.getId(), result, ACTOR);
        } else {
            dispatcher.dispatchAfterCommit(intent.getId());
        }
        return views.of(intent);
    }

    /** Número nacional a começar por 84 ou 85 (M-Pesa). */
    static Msisdn requireMpesaMsisdn(Msisdn msisdn) {
        if (msisdn == null) {
            throw new FieldValidationException("payerMsisdn", FieldErrorCodes.REQUIRED,
                    "payerMsisdn is required for M-Pesa");
        }
        String national = msisdn.national();
        if (!national.startsWith("84") && !national.startsWith("85")) {
            throw new FieldValidationException("payerMsisdn", FieldErrorCodes.MSISDN_NOT_MPESA,
                    "The number is not an M-Pesa number");
        }
        return msisdn;
    }

    static boolean withinLimits(MethodConfig config, Money amount) {
        return (config.minAmount() == null || amount.compareTo(config.minAmount()) >= 0)
                && (config.maxAmount() == null || amount.compareTo(config.maxAmount()) <= 0);
    }

    private static DomainException notAvailable() {
        return DomainException.of(ErrorCodes.PAYMENT_METHOD_NOT_AVAILABLE,
                "The payment method is not available for this insurer");
    }

    private static Instant min(Instant a, Instant b) {
        return a.isBefore(b) ? a : b;
    }
}
