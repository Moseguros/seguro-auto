package mz.co.moseguros.catalog.domain;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

/** Coberturas visíveis no âmbito da transacção (Row Level Security por {@code insurer_id}). */
public interface CoverageRepository extends JpaRepository<Coverage, UUID> {

    List<Coverage> findByProductVersionIdOrderBySortOrderAsc(UUID productVersionId);

    List<Coverage> findByProductVersionIdInOrderBySortOrderAsc(Collection<UUID> productVersionIds);
}
