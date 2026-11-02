package mz.co.moseguros.identity.domain;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Perfis e permissões (só leitura).
 */
public interface RoleRepository extends JpaRepository<Role, UUID> {

    Optional<Role> findByCode(String code);

    List<Role> findByCodeIn(Collection<String> codes);

    /** Permissões dos perfis indicados ({@code identity.role_permission}). */
    @Query("select distinct rp.permission from RolePermission rp where rp.roleCode in :roleCodes")
    Set<String> findPermissionsByRoleCodes(@Param("roleCodes") Collection<String> roleCodes);
}
