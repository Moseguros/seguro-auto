package mz.co.moseguros.identity.domain;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Perfis atribuídos às contas visíveis no âmbito da transacção (Row Level Security).
 */
public interface UserRoleRepository extends JpaRepository<UserRole, UserRole.Key> {

    List<UserRole> findByUserId(UUID userId);
}
