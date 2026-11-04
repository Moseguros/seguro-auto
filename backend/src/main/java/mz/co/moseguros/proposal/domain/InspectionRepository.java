package mz.co.moseguros.proposal.domain;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/** Vistorias visíveis no âmbito da transacção (as das propostas visíveis, RLS). */
public interface InspectionRepository extends JpaRepository<Inspection, UUID> {

    Optional<Inspection> findByProposalId(UUID proposalId);

    /**
     * Vistorias da seguradora de propostas já submetidas ({@code listInsurerInspections}).
     *
     * @param mode {@code null} = todos os modos
     */
    @Query(value = """
            select i from Inspection i
             where i.insurerId = :insurerId and i.status = :status
               and (:mode is null or i.mode = :mode)
               and exists (select 1 from Proposal p where p.id = i.proposalId and p.submittedAt is not null)""",
            countQuery = """
            select count(i) from Inspection i
             where i.insurerId = :insurerId and i.status = :status
               and (:mode is null or i.mode = :mode)
               and exists (select 1 from Proposal p where p.id = i.proposalId and p.submittedAt is not null)""")
    Page<Inspection> findForInsurer(@Param("insurerId") UUID insurerId, @Param("status") InspectionStatus status,
                                    @Param("mode") InspectionMode mode, Pageable pageable);
}
