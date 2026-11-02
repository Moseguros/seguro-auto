package mz.co.moseguros.identity.domain;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PostPersist;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * Perfil atribuído a uma conta ({@code identity.user_role}). Tabela de associação: chave
 * composta e só {@code created_at}/{@code created_by} (AGENTS.md §5). Única tabela em que a
 * aplicação apaga linhas.
 */
@Entity
@IdClass(UserRole.Key.class)
@EntityListeners(AuditingEntityListener.class)
@Table(schema = "identity", name = "user_role")
public class UserRole implements Persistable<UserRole.Key> {

    @Id
    @Column(name = "user_id", nullable = false, updatable = false)
    private UUID userId;

    @Id
    @Column(name = "role_code", nullable = false, updatable = false, length = 30)
    private String roleCode;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @CreatedBy
    @Column(name = "created_by", nullable = false, updatable = false, length = 64)
    private String createdBy;

    @Transient
    private boolean isNew = true;

    protected UserRole() {
    }

    public UserRole(UUID userId, String roleCode) {
        this.userId = Objects.requireNonNull(userId, "userId");
        this.roleCode = Objects.requireNonNull(roleCode, "roleCode");
    }

    @PostLoad
    @PostPersist
    void markNotNew() {
        this.isNew = false;
    }

    @Override
    public Key getId() {
        return new Key(userId, roleCode);
    }

    @Override
    public boolean isNew() {
        return isNew;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getRoleCode() {
        return roleCode;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    /** Chave composta. */
    public static class Key implements Serializable {

        private UUID userId;
        private String roleCode;

        protected Key() {
        }

        public Key(UUID userId, String roleCode) {
            this.userId = userId;
            this.roleCode = roleCode;
        }

        public UUID getUserId() {
            return userId;
        }

        public String getRoleCode() {
            return roleCode;
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof Key k && Objects.equals(userId, k.userId) && Objects.equals(roleCode, k.roleCode);
        }

        @Override
        public int hashCode() {
            return Objects.hash(userId, roleCode);
        }
    }
}
