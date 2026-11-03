package mz.co.moseguros.catalog.domain;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/** Valores de referência (sem RLS: dados da plataforma). */
public interface ReferenceValueRepository extends JpaRepository<ReferenceValue, UUID>,
        JpaSpecificationExecutor<ReferenceValue> {

    Optional<ReferenceValue> findByIdAndDomain(UUID id, ReferenceDomain domain);

    boolean existsByDomainAndCode(ReferenceDomain domain, String code);

    boolean existsByDomainAndCodeAndActiveTrue(ReferenceDomain domain, String code);
}
