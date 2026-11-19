package mz.co.moseguros.payment.application;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import mz.co.moseguros.organization.OrganizationApi;
import mz.co.moseguros.payment.PaymentMethod;
import mz.co.moseguros.payment.domain.FileImport;
import mz.co.moseguros.payment.domain.FileImportRepository;
import mz.co.moseguros.payment.domain.InboundMessageRepository;
import mz.co.moseguros.payment.domain.InsurerPaymentMethod;
import mz.co.moseguros.payment.domain.InsurerPaymentMethodRepository;
import mz.co.moseguros.payment.domain.SourceFileCount;
import mz.co.moseguros.payment.infrastructure.amqp.PaymentQueueInspector;
import mz.co.moseguros.payment.infrastructure.amqp.PaymentQueueInspector.QueueStatus;
import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.error.ErrorCodes;
import mz.co.moseguros.shared.tenant.Scope;
import mz.co.moseguros.shared.tenant.TenantContext;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Operações dos pagamentos para o administrador da plataforma (S5-10 §8; {@code platform:operations}, âmbito
 * {@code PLATFORM}): as fontes de ficheiros de todas as seguradoras e o estado das filas e do worker. Sem
 * montantes nem dados de clientes.
 */
@Service
public class PaymentOperationsService {

    private final InsurerPaymentMethodRepository methods;
    private final FileImportRepository files;
    private final InboundMessageRepository inbound;
    private final OrganizationApi organizations;
    private final PaymentQueueInspector queues;
    private final Clock clock;

    public PaymentOperationsService(InsurerPaymentMethodRepository methods, FileImportRepository files,
                                    InboundMessageRepository inbound, OrganizationApi organizations,
                                    PaymentQueueInspector queues, Clock clock) {
        this.methods = methods;
        this.files = files;
        this.inbound = inbound;
        this.organizations = organizations;
        this.queues = queues;
        this.clock = clock;
    }

    /** Uma fonte de ficheiros. */
    public record SourceRow(UUID sourceId, UUID insurerId, String insurerName, String entity, String fileFormat,
                            boolean enabled, Instant lastPollAt, String lastPollOutcome, String lastPollError,
                            Instant lastSuccessAt, Instant staleSince, long filesLast24h, long rejectedLast24h) {
    }

    /** Estado das filas e do worker; {@code queues} vazio se o broker não estiver acessível. */
    public record WorkerStatus(boolean brokerReachable, List<QueueStatus> queues, Instant lastMessageAt) {
    }

    @PreAuthorize("hasAuthority('platform:operations')")
    @Transactional(readOnly = true)
    public List<SourceRow> sources() {
        requirePlatform();
        List<InsurerPaymentMethod> rows = methods.findByMethod(PaymentMethod.BANK_REFERENCE);
        Set<UUID> insurerIds = rows.stream().map(InsurerPaymentMethod::getInsurerId).collect(Collectors.toSet());
        Map<UUID, String> names = new LinkedHashMap<>();
        organizations.findSummaries(insurerIds).forEach((id, s) -> names.put(id, s.displayName()));
        Map<UUID, SourceFileCount> counts = new LinkedHashMap<>();
        files.countBySourceSince(clock.instant().minus(Duration.ofHours(24)), FileImport.Status.REJECTED)
                .forEach(c -> counts.put(c.sourceId(), c));
        return rows.stream().map(m -> {
            var status = FileImportQueryService.status(m, 0);
            SourceFileCount c = counts.get(m.getId());
            return new SourceRow(m.getId(), m.getInsurerId(), names.getOrDefault(m.getInsurerId(), ""),
                    status.entity(), status.fileFormat(), m.isEnabled(), m.getLastPollAt(), m.getLastPollOutcome(),
                    m.getLastPollError(), m.getLastPollSuccessAt(), m.getStaleSince(), c == null ? 0 : c.files(),
                    c == null ? 0 : c.rejected());
        }).sorted(Comparator.comparing(SourceRow::insurerName, String.CASE_INSENSITIVE_ORDER)
                .thenComparing(r -> r.sourceId().toString())).toList();
    }

    @PreAuthorize("hasAuthority('platform:operations')")
    @Transactional(readOnly = true)
    public WorkerStatus workerStatus() {
        requirePlatform();
        Instant last = inbound.lastReceivedAt();
        return queues.inspect()
                .map(list -> new WorkerStatus(true, list, last))
                .orElseGet(() -> new WorkerStatus(false, List.of(), last));
    }

    private static void requirePlatform() {
        if (TenantContext.current().scope() != Scope.PLATFORM) {
            throw DomainException.of(ErrorCodes.FORBIDDEN, "Only platform administrators can see payment operations");
        }
    }
}
