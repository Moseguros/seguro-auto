package mz.co.moseguros.organization.domain;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Perfis de seguradora visíveis no âmbito da transacção (Row Level Security).
 */
public interface InsurerProfileRepository extends JpaRepository<InsurerProfile, UUID> {

    Optional<InsurerProfile> findByOrganizationId(UUID organizationId);

    List<InsurerProfile> findByOrganizationIdIn(Collection<UUID> organizationIds);

    boolean existsByShortCode(String shortCode);

    boolean existsByShortCodeAndOrganizationIdNot(String shortCode, UUID organizationId);
}
