package mz.co.moseguros.shared.audit;

import java.time.Clock;
import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import mz.co.moseguros.shared.domain.Ids;
import mz.co.moseguros.shared.web.ClientIpResolver;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Grava eventos em {@code audit.audit_event}.
 *
 * <p>Corre na transacção de quem chama ({@link Propagation#MANDATORY}): se a operação
 * falhar, o evento também não fica; sem transacção activa lança excepção.
 */
@Service
public class AuditService {

    static final String TRACE_ID_MDC_KEY = "traceId";
    private static final int MAX_IP = 45;
    private static final int MAX_TRACE_ID = 64;

    private final AuditEventRepository repository;
    private final ObjectMapper objectMapper;
    private final Clock clock;
    private final ClientIpResolver clientIp;

    public AuditService(AuditEventRepository repository, ObjectMapper objectMapper, Clock clock,
                        ClientIpResolver clientIp) {
        this.repository = repository;
        this.objectMapper = objectMapper;
        this.clock = clock;
        this.clientIp = clientIp;
    }

    /**
     * Grava o evento, preenchendo {@code id} (UUID v7), {@code occurred_at} (UTC),
     * {@code ip} (do pedido HTTP actual, pelo {@link ClientIpResolver}) e {@code trace_id} (do MDC).
     *
     * @return o id do evento gravado
     */
    @Transactional(propagation = Propagation.MANDATORY)
    public UUID record(AuditRecord r) {
        AuditEvent event = new AuditEvent(
                Ids.newId(),
                clock.instant(),
                r.actorType(),
                r.actorId(),
                r.insurerId(),
                r.action(),
                r.entityType(),
                r.entityId(),
                toJson(r.beforeState()),
                toJson(r.afterState()),
                truncate(currentIp(), MAX_IP),
                truncate(MDC.get(TRACE_ID_MDC_KEY), MAX_TRACE_ID));
        repository.save(event);
        return event.getId();
    }

    private JsonNode toJson(Object state) {
        return state == null ? null : objectMapper.valueToTree(state);
    }

    /** IP do pedido HTTP actual ({@link ClientIpResolver}); {@code null} fora de um pedido HTTP. */
    String currentIp() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (!(attributes instanceof ServletRequestAttributes servlet)) {
            return null;
        }
        return clientIp.resolve(servlet.getRequest());
    }

    private static String truncate(String value, int max) {
        return value == null || value.length() <= max ? value : value.substring(0, max);
    }
}
