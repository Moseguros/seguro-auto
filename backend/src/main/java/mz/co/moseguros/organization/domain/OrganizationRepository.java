package mz.co.moseguros.organization.domain;

import java.util.Optional;
import java.util.UUID;

import mz.co.moseguros.shared.domain.Nuit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Organizações visíveis no âmbito da transacção (Row Level Security).
 */
public interface OrganizationRepository extends JpaRepository<Organization, UUID>,
        JpaSpecificationExecutor<Organization> {

    Optional<Organization> findByNuit(Nuit nuit);

    boolean existsByNuit(Nuit nuit);

    boolean existsByNuitAndIdNot(Nuit nuit, UUID id);
}
