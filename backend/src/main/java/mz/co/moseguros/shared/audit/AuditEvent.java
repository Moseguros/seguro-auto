package mz.co.moseguros.shared.audit;

import java.time.Instant;
import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PostLoad;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.domain.Persistable;

/**
 * Linha de {@code audit.audit_event}. Só recebe inserções: não estende {@code BaseEntity}
 * e não tem {@code created_by}, {@code updated_*} nem {@code version} (excepção do
 * AGENTS.md §5); {@code occurredAt} e {@code actorId} fazem esse papel.
 */
@Entity
@Immutable
@Table(schema = "audit", name = "audit_event")
public class AuditEvent implements Persistable<UUID> {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "occurred_at", nullable = false, updatable = false)
    private Instant occurredAt;

    @Column(name = "actor_id", nullable = false, updatable = false, length = 64)
    private String actorId;

    @Enumerated(EnumType.STRING)
    @Column(name = "actor_type", nullable = false, updatable = false, length = 20)
    private ActorType actorType;

    @Column(name = "insurer_id", updatable = false)
    private UUID insurerId;

    @Column(name = "action", nullable = false, updatable = false, length = 80)
    private String action;

    @Column(name = "entity_type", nullable = false, updatable = false, length = 60)
    private String entityType;

    @Column(name = "entity_id", nullable = false, updatable = false, length = 64)
    private String entityId;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "before_state", updatable = false, columnDefinition = "jsonb")
    private JsonNode beforeState;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "after_state", updatable = false, columnDefinition = "jsonb")
    private JsonNode afterState;

    @Column(name = "ip", updatable = false, length = 45)
    private String ip;

    @Column(name = "trace_id", updatable = false, length = 64)
    private String traceId;

    @Transient
    private boolean isNew = true;

    protected AuditEvent() {
    }

    AuditEvent(UUID id, Instant occurredAt, ActorType actorType, String actorId, UUID insurerId,
               String action, String entityType, String entityId, JsonNode beforeState,
               JsonNode afterState, String ip, String traceId) {
        this.id = id;
        this.occurredAt = occurredAt;
        this.actorType = actorType;
        this.actorId = actorId;
        this.insurerId = insurerId;
        this.action = action;
        this.entityType = entityType;
        this.entityId = entityId;
        this.beforeState = beforeState;
        this.afterState = afterState;
        this.ip = ip;
        this.traceId = traceId;
    }

    @PostLoad
    void markNotNew() {
        this.isNew = false;
    }

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public boolean isNew() {
        return isNew;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }

    public ActorType getActorType() {
        return actorType;
    }

    public String getActorId() {
        return actorId;
    }

    public UUID getInsurerId() {
        return insurerId;
    }

    public String getAction() {
        return action;
    }

    public String getEntityType() {
        return entityType;
    }

    public String getEntityId() {
        return entityId;
    }

    public JsonNode getBeforeState() {
        return beforeState;
    }

    public JsonNode getAfterState() {
        return afterState;
    }

    public String getIp() {
        return ip;
    }

    public String getTraceId() {
        return traceId;
    }
}
