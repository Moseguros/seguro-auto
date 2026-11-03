package mz.co.moseguros.identity.domain;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import mz.co.moseguros.shared.persistence.BaseEntity;

/**
 * Perfil ({@code identity.role}). Tabela de referência: a aplicação só lê; os perfis mudam
 * por migração.
 */
@Entity
@Immutable
@Table(schema = "identity", name = "role")
public class Role extends BaseEntity {

    @Column(name = "code", nullable = false, length = 30)
    private String code;

    /** Tipos de organização onde o perfil pode ser atribuído; {@code null} = só fora de organizações. */
    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "org_types", columnDefinition = "text[]")
    private List<String> orgTypes;

    protected Role() {
    }

    public String getCode() {
        return code;
    }

    public List<String> getOrgTypes() {
        return orgTypes == null ? null : List.copyOf(orgTypes);
    }

    /** {@code organizationType} {@code null} = fora de organizações (cliente ou plataforma). */
    public boolean isAssignableIn(String organizationType) {
        return organizationType == null ? orgTypes == null : orgTypes != null && orgTypes.contains(organizationType);
    }
}
