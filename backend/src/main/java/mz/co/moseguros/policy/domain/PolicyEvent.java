package mz.co.moseguros.policy.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import mz.co.moseguros.shared.audit.ActorType;
import mz.co.moseguros.shared.persistence.BaseEntity;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/** Entrada da linha temporal da apólice ({@code policy.policy_event}, S6-01 §3). Sem dados pessoais. */
@Entity
@Table(schema = "policy", name = "policy_event")
public class PolicyEvent extends BaseEntity {

    @Column(name = "policy_id", nullable = false, updatable = false)
    private UUID policyId;

    @Column(name = "insurer_id", nullable = false, updatable = false)
    private UUID insurerId;

    @Column(name = "policyholder_user_id", nullable = false, updatable = false)
    private UUID policyholderUserId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, updatable = false, length = 30)
    private PolicyEventType type;

    @Column(name = "occurred_at", nullable = false, updatable = false)
    private Instant occurredAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "actor_type", nullable = false, updatable = false, length = 10)
    private ActorType actorType;

    @Column(name = "actor_id", nullable = false, updatable = false, length = 64)
    private String actorId;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "details", updatable = false, columnDefinition = "jsonb")
    private JsonNode details;

    protected PolicyEvent() {
    }

    /**
     * @param actorType {@code CUSTOMER}, {@code STAFF} ou {@code SYSTEM}
     * @param details   sem dados pessoais; pode ser {@code null}
     */
    public static PolicyEvent of(Policy policy, PolicyEventType type, Instant occurredAt, ActorType actorType,
                                 String actorId, JsonNode details) {
        Objects.requireNonNull(policy, "policy");
        if (actorType == ActorType.SERVICE) {
            throw new IllegalArgumentException("A policy event actor is a customer, a staff member or the system");
        }
        PolicyEvent e = new PolicyEvent();
        e.policyId = policy.getId();
        e.insurerId = policy.getInsurerId();
        e.policyholderUserId = policy.getPolicyholderUserId();
        e.type = Objects.requireNonNull(type, "type");
        e.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
        e.actorType = Objects.requireNonNull(actorType, "actorType");
        e.actorId = Objects.requireNonNull(actorId, "actorId");
        e.details = details;
        return e;
    }

    public UUID getPolicyId() {
        return policyId;
    }

    public UUID getInsurerId() {
        return insurerId;
    }

    public UUID getPolicyholderUserId() {
        return policyholderUserId;
    }

    public PolicyEventType getType() {
        return type;
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

    public JsonNode getDetails() {
        return details;
    }
}
