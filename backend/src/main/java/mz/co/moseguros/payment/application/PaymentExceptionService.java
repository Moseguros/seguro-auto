package mz.co.moseguros.payment.application;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.persistence.criteria.Predicate;
import mz.co.moseguros.payment.FailureCode;
import mz.co.moseguros.payment.PaymentMethod;
import mz.co.moseguros.payment.PaymentStatus;
import mz.co.moseguros.payment.domain.ConfirmedBy;
import mz.co.moseguros.payment.domain.PaymentEvent;
import mz.co.moseguros.payment.domain.PaymentEventRepository;
import mz.co.moseguros.payment.domain.PaymentEventType;
import mz.co.moseguros.payment.domain.PaymentExceptionCase;
import mz.co.moseguros.payment.domain.PaymentExceptionRepository;
import mz.co.moseguros.payment.domain.PaymentExceptionType;
import mz.co.moseguros.payment.domain.PaymentIntent;
import mz.co.moseguros.payment.domain.PaymentIntentRepository;
import mz.co.moseguros.payment.domain.PaymentResolution;
import mz.co.moseguros.payment.domain.ReferencePayment;
import mz.co.moseguros.payment.domain.ReferencePaymentRepository;
import mz.co.moseguros.payment.domain.ReferenceStatus;
import mz.co.moseguros.shared.audit.ActorType;
import mz.co.moseguros.shared.audit.AuditRecord;
import mz.co.moseguros.shared.audit.AuditService;
import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.error.ErrorCodes;
import mz.co.moseguros.shared.error.FieldErrorCodes;
import mz.co.moseguros.shared.error.Problems.FieldError;
import mz.co.moseguros.shared.security.CurrentUser;
import mz.co.moseguros.shared.tenant.Scope;
import mz.co.moseguros.shared.tenant.TenantContext;
import mz.co.moseguros.shared.tenant.TenantSnapshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Casos para o financeiro da seguradora (S5-02 §9, S5-10 §6): {@code TARGET_NOT_PAYABLE} criado pelo núcleo;
 * a fila, o detalhe, as intenções candidatas e a resolução. Nunca movimenta dinheiro: uma devolução
 * ({@code REFUND}) faz-se fora da plataforma, pela seguradora; a plataforma só regista a decisão.
 *
 * <p>Os pedidos da seguradora correm no seu âmbito {@code ORG} (RLS) e filtram também pela seguradora no código.
 */
@Service
public class PaymentExceptionService {

    /** Intenções candidatas a {@code LINKED}: criadas nos últimos 60 dias, até 20. */
    static final Duration CANDIDATE_WINDOW = Duration.ofDays(60);
    static final int MAX_CANDIDATES = 20;
    static final int MAX_PROVIDER_TX_ID = 100;
    private static final Set<PaymentStatus> LINKABLE = EnumSet.of(PaymentStatus.PENDING, PaymentStatus.EXPIRED,
            PaymentStatus.CANCELLED);
    private static final Logger log = LoggerFactory.getLogger(PaymentExceptionService.class);

    private final PaymentIntentRepository intents;
    private final ReferencePaymentRepository references;
    private final PaymentExceptionRepository exceptions;
    private final PaymentEventRepository paymentEvents;
    private final IntentStateService states;
    private final AuditService audit;
    private final CurrentUser currentUser;
    private final NamedParameterJdbcTemplate jdbc;
    private final Clock clock;

    public PaymentExceptionService(PaymentIntentRepository intents, ReferencePaymentRepository references,
                                   PaymentExceptionRepository exceptions, PaymentEventRepository paymentEvents,
                                   IntentStateService states, AuditService audit, CurrentUser currentUser,
                                   NamedParameterJdbcTemplate jdbc, Clock clock) {
        this.intents = intents;
        this.references = references;
        this.exceptions = exceptions;
        this.paymentEvents = paymentEvents;
        this.states = states;
        this.audit = audit;
        this.currentUser = currentUser;
        this.jdbc = jdbc;
        this.clock = clock;
    }

    // --- Núcleo (S5-02) ---

    /** Ver {@code PaymentApi.raiseTargetNotPayable}; repetido para a mesma intenção não cria outro caso. */
    @Transactional
    public void raiseTargetNotPayable(UUID intentId, String detail) {
        PaymentIntent intent = intents.lock(intentId).orElseThrow(() -> DomainException.of(
                ErrorCodes.PAYMENT_INTENT_NOT_FOUND, "Payment intent not found"));
        if (exceptions.existsByIntentIdAndType(intentId, PaymentExceptionType.TARGET_NOT_PAYABLE)) {
            return;
        }
        exceptions.save(PaymentExceptionCase.targetNotPayable(intent,
                references.findByIntentId(intentId).orElse(null), detail));
        log.warn("Payment received for a target that is no longer payable intentId={} reference={} detail={}",
                intentId, intent.getReference(), detail);
    }

    // --- Seguradora (S5-10 §6) ---

    /**
     * Filtros da fila.
     *
     * @param status {@code null} = todos
     * @param types  vazio = todos
     * @param from   primeiro dia (Maputo) de {@code created_at}, inclusivo
     * @param to     último dia (Maputo) de {@code created_at}, inclusivo
     * @param q      igual à referência ou ao nome do ficheiro
     */
    public record Filter(PaymentExceptionCase.Status status, Set<PaymentExceptionType> types, LocalDate from,
                         LocalDate to, String q) {
    }

    /** Resumo de uma intenção. */
    public record IntentSummary(UUID id, String reference, long amount, PaymentStatus status, String targetLabel) {

        static IntentSummary of(PaymentIntent i) {
            return i == null ? null : new IntentSummary(i.getId(), i.getReference(), i.getAmount(), i.getStatus(),
                    i.getTargetLabel());
        }
    }

    /** Uma excepção com os resumos da intenção da referência e da associada. */
    public record ExceptionView(PaymentExceptionCase exception, IntentSummary intent, IntentSummary linkedIntent) {
    }

    /** Intenção candidata a {@code LINKED}. */
    public record Candidate(UUID id, String reference, long amount, PaymentStatus status, String targetLabel,
                            Instant createdAt, String bankReference, boolean matchesReference,
                            boolean matchesAmount) {
    }

    /** Pedido de {@code resolvePaymentException}. */
    public record ResolveCommand(String resolution, UUID intentId, String providerTxId, String note, Long version) {
    }

    @PreAuthorize("hasAuthority('payment:read')")
    @Transactional(readOnly = true)
    public Page<ExceptionView> list(Filter filter, Pageable pageable) {
        UUID insurerId = requireInsurer();
        Page<PaymentExceptionCase> page = exceptions.findAll(specification(insurerId, filter), pageable);
        Map<UUID, PaymentIntent> byId = intentsOf(page.getContent());
        List<ExceptionView> views = page.getContent().stream().map(c -> view(c, byId)).toList();
        return new PageImpl<>(views, page.getPageable(), page.getTotalElements());
    }

    @PreAuthorize("hasAuthority('payment:read')")
    @Transactional(readOnly = true)
    public ExceptionView detail(UUID id) {
        PaymentExceptionCase c = load(id, requireInsurer());
        return view(c, intentsOf(List.of(c)));
    }

    @PreAuthorize("hasAuthority('payment:resolve-exception')")
    @Transactional(readOnly = true)
    public List<Candidate> candidates(UUID id) {
        UUID insurerId = requireInsurer();
        PaymentExceptionCase c = load(id, insurerId);
        if (c.getStatus() == PaymentExceptionCase.Status.RESOLVED
                || !PaymentExceptionCase.allowedFor(c.getType()).contains(PaymentResolution.LINKED)) {
            return List.of();
        }
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("insurerId", insurerId)
                .addValue("since", java.sql.Timestamp.from(clock.instant().minus(CANDIDATE_WINDOW)))
                .addValue("entity", c.getEntity() == null ? "" : c.getEntity())
                .addValue("reference", c.getReference() == null ? "" : c.getReference())
                .addValue("amount", c.getAmount() == null ? -1L : c.getAmount())
                .addValue("limit", MAX_CANDIDATES);
        return jdbc.query("""
                select p.id, p.reference, p.amount, p.status, p.target_label, p.created_at,
                       r.reference as bank_reference,
                       coalesce(r.entity = :entity and r.reference = :reference, false) as matches_reference,
                       (p.amount = :amount) as matches_amount
                  from payment.payment_intent p
                  left join payment.reference_payment r on r.intent_id = p.id
                 where p.insurer_id = :insurerId and p.method = 'BANK_REFERENCE'
                   and p.status in ('PENDING','EXPIRED','CANCELLED') and p.created_at >= :since
                 order by matches_reference desc, matches_amount desc, p.created_at desc, p.id
                 limit :limit""", params, (rs, n) -> new Candidate(rs.getObject("id", UUID.class),
                rs.getString("reference"), rs.getLong("amount"), PaymentStatus.valueOf(rs.getString("status")),
                rs.getString("target_label"), rs.getTimestamp("created_at").toInstant(),
                rs.getString("bank_reference"), rs.getBoolean("matches_reference"), rs.getBoolean("matches_amount")));
    }

    /**
     * Resolve a excepção (S5-10 §6).
     *
     * @throws DomainException 400 {@code VALIDATION_ERROR}, 404 {@code PAYMENT_EXCEPTION_NOT_FOUND}, 409
     *                         {@code INVALID_STATE_TRANSITION} / {@code CONCURRENT_MODIFICATION}, 400
     *                         {@code RESOLUTION_NOT_ALLOWED} / {@code LINK_INTENT_INVALID} /
     *                         {@code LINK_AMOUNT_INSUFFICIENT}, 409 {@code PAYMENT_TARGET_ALREADY_PAID}
     */
    @PreAuthorize("hasAuthority('payment:resolve-exception')")
    @Transactional
    public ExceptionView resolve(UUID id, ResolveCommand command) {
        UUID insurerId = requireInsurer();
        PaymentResolution resolution = validate(command);
        PaymentExceptionCase c = exceptions.lock(id).filter(e -> e.getInsurerId().equals(insurerId))
                .orElseThrow(PaymentExceptionService::notFound);
        c.requireResolvable(resolution, command.version());
        Map<String, Object> before = auditState(c);
        Instant now = clock.instant();
        UUID userId = currentUser.id();
        String actor = userId.toString();
        UUID affectedIntent = null;
        switch (resolution) {
            case LINKED -> affectedIntent = link(c, command.intentId(), insurerId, actor, now);
            case MARK_SUCCEEDED -> {
                PaymentIntent intent = unresolvedIntent(c);
                affectedIntent = intent.getId();
                manualEvent(intent, resolution, c, now);
                states.markSucceeded(intent.getId(), now, ConfirmedBy.MANUAL, blankToNull(command.providerTxId()),
                        actor);
            }
            case MARK_FAILED -> {
                PaymentIntent intent = unresolvedIntent(c);
                affectedIntent = intent.getId();
                manualEvent(intent, resolution, c, now);
                states.markFailed(intent.getId(), FailureCode.MANUAL, actor);
            }
            case REFUND, IGNORED -> {
                if (c.getIntentId() != null) {
                    intents.findById(c.getIntentId()).ifPresent(i -> manualEvent(i, resolution, c, now));
                }
            }
        }
        c.resolve(resolution, resolution == PaymentResolution.LINKED ? command.intentId() : null, userId, now,
                command.note(), command.version());
        PaymentExceptionCase saved = exceptions.saveAndFlush(c);
        audit.record(new AuditRecord(ActorType.STAFF, actor, insurerId, "PAYMENT_EXCEPTION.RESOLVE",
                "PaymentException", saved.getId().toString(), before, auditState(saved)));
        log.info("Payment exception resolved exceptionId={} type={} resolution={} intentId={} by={}", saved.getId(),
                saved.getType(), resolution, affectedIntent, userId);
        return view(saved, intentsOf(List.of(saved)));
    }

    // --- Resoluções ---

    private UUID link(PaymentExceptionCase c, UUID intentId, UUID insurerId, String actor, Instant now) {
        PaymentIntent intent = intents.lock(intentId).orElseThrow(PaymentExceptionService::linkInvalid);
        if (!intent.getInsurerId().equals(insurerId) || intent.getMethod() != PaymentMethod.BANK_REFERENCE
                || !LINKABLE.contains(intent.getStatus())) {
            throw linkInvalid();
        }
        if (c.getAmount() == null || c.getAmount() < intent.getAmount()) {
            throw DomainException.of(ErrorCodes.LINK_AMOUNT_INSUFFICIENT,
                    "The amount received is lower than the amount of the payment intent");
        }
        if (intents.existsByTargetAndStatus(intent.getTargetType(), intent.getTargetId(), PaymentStatus.SUCCEEDED)) {
            throw DomainException.of(ErrorCodes.PAYMENT_TARGET_ALREADY_PAID, "The target of the payment intent is "
                    + "already paid");
        }
        Instant paidAt = c.getPaidAt() == null ? now : c.getPaidAt();
        ReferencePayment ref = references.lockByIntentId(intent.getId()).orElse(null);
        if (ref != null && ref.getStatus() != ReferenceStatus.PAID) {
            ref.recordBankPayment(paidAt, c.getBankTransactionId(), c.getMessageId());
            references.save(ref);
        }
        manualEvent(intent, PaymentResolution.LINKED, c, now);
        states.markSucceeded(intent.getId(), paidAt, ConfirmedBy.MANUAL, null, actor);
        return intent.getId();
    }

    /** A intenção M-Pesa {@code UNKNOWN} de um {@code UNRESOLVED_STATUS}. */
    private PaymentIntent unresolvedIntent(PaymentExceptionCase c) {
        PaymentIntent intent = c.getIntentId() == null ? null : intents.lock(c.getIntentId()).orElse(null);
        if (intent == null || intent.getStatus() != PaymentStatus.UNKNOWN) {
            throw DomainException.of(ErrorCodes.INVALID_STATE_TRANSITION, "The payment intent is no longer UNKNOWN");
        }
        return intent;
    }

    private void manualEvent(PaymentIntent intent, PaymentResolution resolution, PaymentExceptionCase c,
                             Instant now) {
        ObjectNode payload = JsonNodeFactory.instance.objectNode();
        payload.put("exceptionId", c.getId().toString());
        payload.put("exceptionType", c.getType().name());
        payload.put("resolution", resolution.name());
        paymentEvents.save(PaymentEvent.of(intent, PaymentEventType.MANUAL_RESOLUTION, null, null, payload, now));
    }

    private static PaymentResolution validate(ResolveCommand command) {
        List<FieldError> errors = new ArrayList<>();
        PaymentResolution resolution = null;
        if (command.resolution() == null || command.resolution().isBlank()) {
            errors.add(new FieldError("resolution", FieldErrorCodes.REQUIRED, "The resolution is required"));
        } else {
            try {
                resolution = PaymentResolution.valueOf(command.resolution().strip().toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException e) {
                errors.add(new FieldError("resolution", FieldErrorCodes.INVALID_VALUE, "Unknown resolution"));
            }
        }
        if (resolution == PaymentResolution.LINKED && command.intentId() == null) {
            errors.add(new FieldError("intentId", FieldErrorCodes.REQUIRED, "The payment intent is required"));
        }
        if (resolution == PaymentResolution.MARK_SUCCEEDED && command.providerTxId() != null
                && command.providerTxId().strip().length() > MAX_PROVIDER_TX_ID) {
            errors.add(new FieldError("providerTxId", FieldErrorCodes.TOO_LONG,
                    "At most " + MAX_PROVIDER_TX_ID + " characters"));
        }
        String note = command.note() == null ? "" : command.note().strip();
        if (note.isEmpty()) {
            errors.add(new FieldError("note", FieldErrorCodes.REQUIRED, "The note is required"));
        } else if (note.length() < PaymentExceptionCase.NOTE_MIN) {
            errors.add(new FieldError("note", FieldErrorCodes.TOO_SHORT,
                    "At least " + PaymentExceptionCase.NOTE_MIN + " characters"));
        } else if (note.length() > PaymentExceptionCase.NOTE_MAX) {
            errors.add(new FieldError("note", FieldErrorCodes.TOO_LONG,
                    "At most " + PaymentExceptionCase.NOTE_MAX + " characters"));
        }
        if (command.version() == null) {
            errors.add(new FieldError("version", FieldErrorCodes.REQUIRED, "The version is required"));
        }
        if (!errors.isEmpty()) {
            throw DomainException.of(ErrorCodes.VALIDATION_ERROR, "Request validation failed")
                    .withProperty("errors", List.copyOf(errors));
        }
        return resolution;
    }

    // --- Apoio ---

    private ExceptionView view(PaymentExceptionCase c, Map<UUID, PaymentIntent> byId) {
        return new ExceptionView(c, IntentSummary.of(c.getIntentId() == null ? null : byId.get(c.getIntentId())),
                IntentSummary.of(c.getLinkedIntentId() == null ? null : byId.get(c.getLinkedIntentId())));
    }

    private Map<UUID, PaymentIntent> intentsOf(Collection<PaymentExceptionCase> cases) {
        Set<UUID> ids = new HashSet<>();
        for (PaymentExceptionCase c : cases) {
            if (c.getIntentId() != null) {
                ids.add(c.getIntentId());
            }
            if (c.getLinkedIntentId() != null) {
                ids.add(c.getLinkedIntentId());
            }
        }
        Map<UUID, PaymentIntent> map = new LinkedHashMap<>();
        if (!ids.isEmpty()) {
            intents.findAllById(ids).forEach(i -> map.put(i.getId(), i));
        }
        return map;
    }

    private PaymentExceptionCase load(UUID id, UUID insurerId) {
        return exceptions.findById(id).filter(e -> e.getInsurerId().equals(insurerId))
                .orElseThrow(PaymentExceptionService::notFound);
    }

    private static Map<String, Object> auditState(PaymentExceptionCase c) {
        Map<String, Object> s = new LinkedHashMap<>();
        s.put("status", c.getStatus().name());
        s.put("type", c.getType().name());
        s.put("resolution", c.getResolution() == null ? null : c.getResolution().name());
        s.put("intentId", c.getIntentId() == null ? null : c.getIntentId().toString());
        s.put("linkedIntentId", c.getLinkedIntentId() == null ? null : c.getLinkedIntentId().toString());
        s.put("note", c.getNote());
        return s;
    }

    /** Só staff de uma seguradora, no âmbito {@code ORG}. */
    static UUID requireInsurer() {
        TenantSnapshot tenant = TenantContext.current();
        if (tenant.scope() != Scope.ORG || tenant.insurerId() == null) {
            throw DomainException.of(ErrorCodes.FORBIDDEN, "Only insurer staff can see payment exceptions");
        }
        return tenant.insurerId();
    }

    private static Specification<PaymentExceptionCase> specification(UUID insurerId, Filter f) {
        return (root, query, cb) -> {
            List<Predicate> p = new ArrayList<>();
            p.add(cb.equal(root.get("insurerId"), insurerId));
            if (f.status() != null) {
                p.add(cb.equal(root.get("status"), f.status()));
            }
            if (f.types() != null && !f.types().isEmpty()) {
                p.add(root.get("type").in(f.types()));
            }
            if (f.from() != null) {
                p.add(cb.greaterThanOrEqualTo(root.get("createdAt"),
                        f.from().atStartOfDay(PaymentQueryService.ZONE).toInstant()));
            }
            if (f.to() != null) {
                p.add(cb.lessThan(root.get("createdAt"),
                        f.to().plusDays(1).atStartOfDay(PaymentQueryService.ZONE).toInstant()));
            }
            if (f.q() != null && !f.q().isBlank()) {
                String q = f.q().strip();
                p.add(cb.or(cb.equal(root.get("reference"), q), cb.equal(root.get("fileName"), q)));
            }
            return cb.and(p.toArray(Predicate[]::new));
        };
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.strip();
    }

    private static DomainException notFound() {
        return DomainException.of(ErrorCodes.PAYMENT_EXCEPTION_NOT_FOUND, "Payment exception not found");
    }

    private static DomainException linkInvalid() {
        return DomainException.of(ErrorCodes.LINK_INTENT_INVALID, "The payment intent cannot be linked");
    }
}
