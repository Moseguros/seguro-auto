package mz.co.moseguros.payment.application;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import jakarta.persistence.criteria.Predicate;
import mz.co.moseguros.payment.IntentView;
import mz.co.moseguros.payment.PaymentMethod;
import mz.co.moseguros.payment.PaymentStatus;
import mz.co.moseguros.payment.PaymentTargetType;
import mz.co.moseguros.payment.domain.PaymentEvent;
import mz.co.moseguros.payment.domain.PaymentEventRepository;
import mz.co.moseguros.payment.domain.PaymentExceptionCase;
import mz.co.moseguros.payment.domain.PaymentExceptionRepository;
import mz.co.moseguros.payment.domain.PaymentIntent;
import mz.co.moseguros.payment.domain.PaymentIntentRepository;
import mz.co.moseguros.payment.domain.ReferencePayment;
import mz.co.moseguros.payment.domain.ReferencePaymentRepository;
import mz.co.moseguros.shared.audit.ActorType;
import mz.co.moseguros.shared.audit.AuditRecord;
import mz.co.moseguros.shared.audit.AuditService;
import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.error.ErrorCodes;
import mz.co.moseguros.shared.security.CurrentUser;
import mz.co.moseguros.shared.tenant.TenantContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Leituras das intenções (S5-02 §7): do cliente (as suas), da seguradora (as suas, com filtros e
 * exportação CSV) e as consultas do {@code PaymentApi}. Tudo no âmbito de quem chama (RLS).
 */
@Service
public class PaymentQueryService {

    /** Fuso das datas dos filtros e do CSV. */
    public static final ZoneId ZONE = ZoneId.of("Africa/Maputo");
    static final Set<PaymentStatus> ACTIVE = EnumSet.of(PaymentStatus.CREATED, PaymentStatus.PENDING,
            PaymentStatus.UNKNOWN);
    private static final DateTimeFormatter CSV_DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZONE);
    private static final String CSV_HEADER =
            "reference;createdAt;method;status;amount;targetLabel;providerTxId;succeededAt;failureCode";

    private final PaymentIntentRepository intents;
    private final ReferencePaymentRepository references;
    private final PaymentEventRepository paymentEvents;
    private final PaymentExceptionRepository exceptions;
    private final IntentViews views;
    private final AuditService audit;
    private final CurrentUser currentUser;
    private final PaymentProperties properties;

    public PaymentQueryService(PaymentIntentRepository intents, ReferencePaymentRepository references,
                               PaymentEventRepository paymentEvents, PaymentExceptionRepository exceptions,
                               IntentViews views, AuditService audit, CurrentUser currentUser,
                               PaymentProperties properties) {
        this.intents = intents;
        this.references = references;
        this.paymentEvents = paymentEvents;
        this.exceptions = exceptions;
        this.views = views;
        this.audit = audit;
        this.currentUser = currentUser;
        this.properties = properties;
    }

    // --- PaymentApi ---

    /** A intenção no âmbito de quem chama. @throws DomainException 404 {@code PAYMENT_INTENT_NOT_FOUND} */
    @Transactional(readOnly = true)
    public IntentView intent(UUID intentId) {
        return views.of(load(intentId));
    }

    @Transactional(readOnly = true)
    public Optional<IntentView> activeIntent(PaymentTargetType type, UUID targetId) {
        return intents.findByTarget(type, targetId, ACTIVE).stream().findFirst().map(views::of);
    }

    @Transactional(readOnly = true)
    public boolean hasBlockingIntent(PaymentTargetType type, UUID targetId) {
        return intents.existsByTarget(type, targetId, PaymentMethod.MPESA, ACTIVE);
    }

    @Transactional(readOnly = true)
    public boolean isPaid(PaymentTargetType type, UUID targetId) {
        return intents.existsByTargetAndStatus(type, targetId, PaymentStatus.SUCCEEDED);
    }

    // --- Cliente ---

    /** {@code getPaymentIntent}: só as do próprio cliente (outra → 404). */
    @PreAuthorize("hasAuthority('self:payments')")
    @Transactional(readOnly = true)
    public IntentView myIntent(UUID intentId) {
        PaymentIntent intent = load(intentId);
        if (!intent.getPayerUserId().equals(currentUser.id())) {
            throw notFound();
        }
        return views.of(intent);
    }

    /** {@code listMyPayments}: as intenções do próprio cliente; {@code statuses} vazio = todas. */
    @PreAuthorize("hasAuthority('self:payments')")
    @Transactional(readOnly = true)
    public Page<IntentView> mine(Set<PaymentStatus> statuses, Pageable pageable) {
        Page<PaymentIntent> page = statuses == null || statuses.isEmpty()
                ? intents.findByPayerUserId(currentUser.id(), pageable)
                : intents.findByPayerUserIdAndStatusIn(currentUser.id(), statuses, pageable);
        return new PageImpl<>(views.of(page.getContent()), page.getPageable(), page.getTotalElements());
    }

    // --- Seguradora ---

    /**
     * Filtros da lista e da exportação da seguradora.
     *
     * @param from primeiro dia (Maputo) de {@code created_at}, inclusivo
     * @param to   último dia (Maputo) de {@code created_at}, inclusivo
     * @param q    igual à referência {@code PG-…}, ao número do alvo ou ao id do fornecedor
     */
    public record Filter(Set<PaymentStatus> statuses, PaymentMethod method, LocalDate from, LocalDate to, String q) {
    }

    /** Detalhe de uma intenção para a seguradora. */
    public record InsurerDetail(PaymentIntent intent, ReferencePayment reference, List<PaymentEvent> events,
                                List<UUID> exceptionIds) {
    }

    @PreAuthorize("hasAuthority('payment:read')")
    @Transactional(readOnly = true)
    public Page<PaymentIntent> insurerList(Filter filter, Pageable pageable) {
        UUID insurerId = TenantContext.current().insurerId();
        if (insurerId == null) {
            return Page.empty(pageable);
        }
        return intents.findAll(specification(insurerId, filter), pageable);
    }

    @PreAuthorize("hasAuthority('payment:read')")
    @Transactional(readOnly = true)
    public InsurerDetail insurerDetail(UUID intentId) {
        UUID insurerId = TenantContext.current().insurerId();
        PaymentIntent intent = intents.findById(intentId).filter(i -> i.getInsurerId().equals(insurerId))
                .orElseThrow(PaymentQueryService::notFound);
        return new InsurerDetail(intent, references.findByIntentId(intentId).orElse(null),
                paymentEvents.findByIntentIdOrderByReceivedAtAscIdAsc(intentId),
                exceptions.findByIntentId(intentId).stream().map(PaymentExceptionCase::getId).toList());
    }

    /** CSV (UTF-8 com BOM, {@code ;}) dos pagamentos da seguradora; auditoria {@code PAYMENT.EXPORT}. */
    @PreAuthorize("hasAuthority('payment:read')")
    @Transactional // grava a auditoria
    public byte[] insurerExport(Filter filter) {
        UUID insurerId = TenantContext.current().insurerId();
        List<PaymentIntent> rows = List.of();
        if (insurerId != null) {
            Specification<PaymentIntent> spec = specification(insurerId, filter);
            long count = intents.count(spec);
            if (count > properties.getExportMaxRows()) {
                throw DomainException.of(ErrorCodes.EXPORT_TOO_LARGE, "The export has " + count
                        + " rows; the maximum is " + properties.getExportMaxRows())
                        .withProperty("maxRows", properties.getExportMaxRows());
            }
            rows = intents.findAll(spec, Sort.by(Sort.Direction.DESC, "createdAt").and(Sort.by("id")));
        }
        StringBuilder csv = new StringBuilder("﻿").append(CSV_HEADER).append("\r\n");
        for (PaymentIntent i : rows) {
            csv.append(String.join(";", i.getReference(), CSV_DATE.format(i.getCreatedAt()), i.getMethod().name(),
                    i.getStatus().name(), meticais(i.getAmount()), csvText(i.getTargetLabel()),
                    csvText(i.getProviderTxId()), i.getSucceededAt() == null ? "" : CSV_DATE.format(i.getSucceededAt()),
                    i.getFailureCode() == null ? "" : i.getFailureCode().name())).append("\r\n");
        }
        Map<String, Object> after = new LinkedHashMap<>();
        after.put("statuses", filter.statuses() == null ? List.of() : filter.statuses().stream().map(Enum::name)
                .sorted().toList());
        after.put("method", filter.method() == null ? null : filter.method().name());
        after.put("from", filter.from() == null ? null : filter.from().toString());
        after.put("to", filter.to() == null ? null : filter.to().toString());
        after.put("q", filter.q());
        after.put("rows", rows.size());
        audit.record(new AuditRecord(ActorType.STAFF, currentUser.id().toString(), insurerId, "PAYMENT.EXPORT",
                "Payment", insurerId == null ? "none" : insurerId.toString(), null, after));
        return csv.toString().getBytes(StandardCharsets.UTF_8);
    }

    /** Referências das intenções indicadas, por id da intenção. */
    @Transactional(readOnly = true)
    public Map<UUID, ReferencePayment> referencesOf(Collection<PaymentIntent> list) {
        List<UUID> ids = list.stream().filter(i -> i.getMethod() == PaymentMethod.BANK_REFERENCE)
                .map(PaymentIntent::getId).toList();
        Map<UUID, ReferencePayment> map = new LinkedHashMap<>();
        if (!ids.isEmpty()) {
            references.findByIntentIdIn(ids).forEach(r -> map.put(r.getIntentId(), r));
        }
        return map;
    }

    /** {@code 299900} → {@code 2999,00}. */
    static String meticais(long centavos) {
        String digits = Long.toString(Math.abs(centavos));
        if (digits.length() < 3) {
            digits = "0".repeat(3 - digits.length()) + digits;
        }
        return (centavos < 0 ? "-" : "") + digits.substring(0, digits.length() - 2) + ","
                + digits.substring(digits.length() - 2);
    }

    private static String csvText(String value) {
        if (value == null) {
            return "";
        }
        String clean = value.replace(';', ',').replace('\r', ' ').replace('\n', ' ');
        // Sem fórmulas em folhas de cálculo (CSV injection).
        return !clean.isEmpty() && "=+-@".indexOf(clean.charAt(0)) >= 0 ? "'" + clean : clean;
    }

    private static Specification<PaymentIntent> specification(UUID insurerId, Filter f) {
        return (root, query, cb) -> {
            List<Predicate> p = new ArrayList<>();
            p.add(cb.equal(root.get("insurerId"), insurerId));
            if (f.statuses() != null && !f.statuses().isEmpty()) {
                p.add(root.get("status").in(f.statuses()));
            }
            if (f.method() != null) {
                p.add(cb.equal(root.get("method"), f.method()));
            }
            if (f.from() != null) {
                Instant start = f.from().atStartOfDay(ZONE).toInstant();
                p.add(cb.greaterThanOrEqualTo(root.get("createdAt"), start));
            }
            if (f.to() != null) {
                Instant end = f.to().plusDays(1).atStartOfDay(ZONE).toInstant();
                p.add(cb.lessThan(root.get("createdAt"), end));
            }
            if (f.q() != null && !f.q().isBlank()) {
                String q = f.q().strip();
                p.add(cb.or(cb.equal(root.get("reference"), q), cb.equal(root.get("targetLabel"), q),
                        cb.equal(root.get("providerTxId"), q)));
            }
            return cb.and(p.toArray(Predicate[]::new));
        };
    }

    private PaymentIntent load(UUID intentId) {
        return intents.findById(intentId).orElseThrow(PaymentQueryService::notFound);
    }

    private static DomainException notFound() {
        return DomainException.of(ErrorCodes.PAYMENT_INTENT_NOT_FOUND, "Payment intent not found");
    }
}
