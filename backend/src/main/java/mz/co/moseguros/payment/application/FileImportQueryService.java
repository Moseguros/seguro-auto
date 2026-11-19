package mz.co.moseguros.payment.application;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import jakarta.persistence.criteria.Predicate;
import mz.co.moseguros.payment.PaymentMethod;
import mz.co.moseguros.payment.domain.FileImport;
import mz.co.moseguros.payment.domain.FileImportRepository;
import mz.co.moseguros.payment.domain.FileOutcomeCount;
import mz.co.moseguros.payment.domain.InboundMessage.Outcome;
import mz.co.moseguros.payment.domain.InboundMessageRepository;
import mz.co.moseguros.payment.domain.InsurerPaymentMethod;
import mz.co.moseguros.payment.domain.InsurerPaymentMethodRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Ficheiros de movimentos e estado da fonte da seguradora (S5-10 §7), no seu âmbito {@code ORG} (RLS) e
 * filtrados também pela seguradora no código.
 */
@Service
public class FileImportQueryService {

    private final FileImportRepository files;
    private final InboundMessageRepository inbound;
    private final InsurerPaymentMethodRepository methods;
    private final PaymentProperties properties;

    public FileImportQueryService(FileImportRepository files, InboundMessageRepository inbound,
                                  InsurerPaymentMethodRepository methods, PaymentProperties properties) {
        this.files = files;
        this.inbound = inbound;
        this.methods = methods;
        this.properties = properties;
    }

    /**
     * Filtros da lista.
     *
     * @param from primeiro dia (Maputo) de {@code occurred_at}, inclusivo
     * @param to   último dia (Maputo) de {@code occurred_at}, inclusivo
     */
    public record Filter(FileImport.Status status, LocalDate from, LocalDate to) {
    }

    /** Um ficheiro com os pagamentos confirmados e as excepções que gerou. */
    public record FileView(FileImport file, long confirmedCount, long exceptionCount) {
    }

    /**
     * Estado da fonte de ficheiros da seguradora; {@code configured = false} sem linha {@code BANK_REFERENCE}
     * (os outros campos ficam {@code null}).
     */
    public record SourceStatus(boolean configured, Boolean enabled, String entity, String fileFormat,
                               Long staleAfterMinutes, Instant lastPollAt, String lastPollOutcome,
                               String lastPollError, Instant lastSuccessAt, Instant staleSince) {

        static SourceStatus notConfigured() {
            return new SourceStatus(false, null, null, null, null, null, null, null, null, null);
        }
    }

    @PreAuthorize("hasAuthority('payment:read')")
    @Transactional(readOnly = true)
    public Page<FileView> list(Filter filter, Pageable pageable) {
        UUID insurerId = PaymentExceptionService.requireInsurer();
        Page<FileImport> page = files.findAll(specification(insurerId, filter), pageable);
        Set<String> shas = page.getContent().stream().map(FileImport::getSha256).collect(Collectors.toSet());
        Map<String, Map<Outcome, Long>> counts = new HashMap<>();
        if (!shas.isEmpty()) {
            for (FileOutcomeCount c : inbound.countByFile(shas)) {
                counts.computeIfAbsent(c.sourceId() + "|" + c.fileSha256(), k -> new HashMap<>())
                        .merge(c.outcome(), c.count(), Long::sum);
            }
        }
        List<FileView> views = page.getContent().stream().map(f -> {
            Map<Outcome, Long> byOutcome = counts.getOrDefault(f.getSourceId() + "|" + f.getSha256(), Map.of());
            return new FileView(f, byOutcome.getOrDefault(Outcome.CONFIRMED, 0L),
                    byOutcome.getOrDefault(Outcome.EXCEPTION, 0L));
        }).toList();
        return new PageImpl<>(views, page.getPageable(), page.getTotalElements());
    }

    @PreAuthorize("hasAuthority('payment:read')")
    @Transactional(readOnly = true)
    public SourceStatus sourceStatus() {
        UUID insurerId = PaymentExceptionService.requireInsurer();
        return methods.findByInsurerIdAndMethod(insurerId, PaymentMethod.BANK_REFERENCE)
                .map(m -> status(m, properties.getFileSource().getStaleAfter().toMinutes()))
                .orElseGet(SourceStatus::notConfigured);
    }

    static SourceStatus status(InsurerPaymentMethod m, long staleAfterMinutes) {
        var settings = m.getSettings();
        String entity = settings != null && settings.hasNonNull("entity") ? settings.get("entity").asText() : null;
        String format = settings != null && settings.hasNonNull("fileFormat") ? settings.get("fileFormat").asText()
                : null;
        return new SourceStatus(true, m.isEnabled(), entity, format, staleAfterMinutes, m.getLastPollAt(),
                m.getLastPollOutcome(), m.getLastPollError(), m.getLastPollSuccessAt(), m.getStaleSince());
    }

    private static Specification<FileImport> specification(UUID insurerId, Filter f) {
        return (root, query, cb) -> {
            List<Predicate> p = new ArrayList<>();
            p.add(cb.equal(root.get("insurerId"), insurerId));
            if (f.status() != null) {
                p.add(cb.equal(root.get("status"), f.status()));
            }
            if (f.from() != null) {
                p.add(cb.greaterThanOrEqualTo(root.get("occurredAt"),
                        f.from().atStartOfDay(PaymentQueryService.ZONE).toInstant()));
            }
            if (f.to() != null) {
                p.add(cb.lessThan(root.get("occurredAt"),
                        f.to().plusDays(1).atStartOfDay(PaymentQueryService.ZONE).toInstant()));
            }
            return cb.and(p.toArray(Predicate[]::new));
        };
    }
}
