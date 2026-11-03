package mz.co.moseguros.identity.domain;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import org.hibernate.annotations.Immutable;

/**
 * Permissão de um perfil ({@code identity.role_permission}). Tabela de associação de
 * referência: chave composta, só {@code created_at}/{@code created_by}, só leitura.
 */
@Entity
@Immutable
@IdClass(RolePermission.Key.class)
@Table(schema = "identity", name = "role_permission")
public class RolePermission {

    @Id
    @Column(name = "role_code", nullable = false, length = 30)
    private String roleCode;

    @Id
    @Column(name = "permission", nullable = false, length = 60)
    private String permission;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "created_by", nullable = false, length = 64)
    private String createdBy;

    protected RolePermission() {
    }

    public String getRoleCode() {
        return roleCode;
    }

    public String getPermission() {
        return permission;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    /** Chave composta. */
    public static class Key implements Serializable {

        private String roleCode;
        private String permission;

        protected Key() {
        }

        public Key(String roleCode, String permission) {
            this.roleCode = roleCode;
            this.permission = permission;
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof Key k && Objects.equals(roleCode, k.roleCode) && Objects.equals(permission, k.permission);
        }

        @Override
        public int hashCode() {
            return Objects.hash(roleCode, permission);
        }
    }
}
