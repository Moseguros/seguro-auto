package mz.co.moseguros.shared.idempotency;

import java.time.Instant;
import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * Registo de um pedido com {@code Idempotency-Key} ({@code shared.idempotency_record}, S5-02 §2 e §5).
 * Tabela de infra-estrutura: sem as colunas obrigatórias (só {@code id} e {@code created_at}). A linha
 * é inserida pelo {@link IdempotencyService} com {@code insert … on conflict do nothing}; esta entidade
 * só serve para a ler e gravar a resposta.
 */
@Entity
@Table(schema = "shared", name = "idempotency_record")
public class IdempotencyRecord {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "user_id", nullable = false, updatable = false)
    private UUID userId;

    @Column(name = "idempotency_key", nullable = false, updatable = false)
    private UUID idempotencyKey;

    @Column(name = "operation", nullable = false, length = 40)
    private String operation;

    @Column(name = "request_hash", nullable = false, length = 64)
    private String requestHash;

    @Column(name = "response_status")
    private Integer responseStatus;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "response_body", columnDefinition = "jsonb")
    private JsonNode responseBody;

    @Column(name = "resource_id")
    private UUID resourceId;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    protected IdempotencyRecord() {
    }

    /** Grava a resposta do pedido (passo 2 do §5). */
    void complete(IdempotentResponse response) {
        this.responseStatus = response.status();
        this.responseBody = response.body() == null ? null : response.body().deepCopy();
        this.resourceId = response.resourceId();
    }

    /** Substitui um registo expirado por um pedido novo com a mesma chave (passo 3 do §5). */
    void restart(String operation, String requestHash, Instant now, Instant expiresAt) {
        this.operation = operation;
        this.requestHash = requestHash;
        this.createdAt = now;
        this.expiresAt = expiresAt;
        this.responseStatus = null;
        this.responseBody = null;
        this.resourceId = null;
    }

    boolean matches(String operation, String requestHash) {
        return this.operation.equals(operation) && this.requestHash.equals(requestHash);
    }

    IdempotentResponse response() {
        return new IdempotentResponse(responseStatus, responseBody == null ? null : responseBody.deepCopy(),
                resourceId);
    }

    public UUID getId() {
        return id;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public UUID getUserId() {
        return userId;
    }

    public UUID getIdempotencyKey() {
        return idempotencyKey;
    }

    public String getOperation() {
        return operation;
    }

    public String getRequestHash() {
        return requestHash;
    }

    public Integer getResponseStatus() {
        return responseStatus;
    }

    public UUID getResourceId() {
        return resourceId;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }
}
