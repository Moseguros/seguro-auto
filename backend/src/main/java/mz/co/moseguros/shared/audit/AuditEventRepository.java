package mz.co.moseguros.shared.audit;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Acesso a {@code audit.audit_event}. Os módulos gravam através do {@link AuditService};
 * as leituras servem os testes e, no Sprint 11, o ecrã de auditoria.
 */
public interface AuditEventRepository extends JpaRepository<AuditEvent, UUID> {

    List<AuditEvent> findByEntityTypeAndEntityIdOrderByOccurredAtDesc(String entityType, String entityId);
}
