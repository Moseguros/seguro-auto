package mz.co.moseguros.shared.audit;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Acesso a {@code audit.audit_event}. Os módulos gravam através do {@link AuditService};
 * as leituras servem os testes e, no Sprint 11, o ecrã de auditoria.
 */
public interface AuditEventRepository extends JpaRepository<AuditEvent, UUID> {

    List<AuditEvent> findByEntityTypeAndEntityIdOrderByOccurredAtDesc(String entityType, String entityId);

    /**
     * Momentos das acções indicadas sobre a entidade depois de {@code since}, os mais antigos primeiro
     * (limites de envio, ex.: {@code PROPOSAL.PAYMENT_LINK_SENT} no S5-02).
     */
    @Query("""
            select e.occurredAt from AuditEvent e
             where e.action = :action and e.entityType = :entityType and e.entityId = :entityId
               and e.occurredAt > :since
             order by e.occurredAt""")
    List<Instant> occurredAtSince(@Param("action") String action, @Param("entityType") String entityType,
                                  @Param("entityId") String entityId, @Param("since") Instant since);
}
