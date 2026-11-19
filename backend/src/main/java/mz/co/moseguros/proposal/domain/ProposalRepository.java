package mz.co.moseguros.proposal.domain;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import mz.co.moseguros.proposal.ProposalStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/** Propostas visíveis no âmbito da transacção (Row Level Security). */
public interface ProposalRepository extends JpaRepository<Proposal, UUID>, JpaSpecificationExecutor<Proposal> {

    Optional<Proposal> findByQuoteId(UUID quoteId);

    /** A proposta bloqueada até ao fim da transacção ({@code select … for update}). */
    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Proposal p where p.id = :id")
    Optional<Proposal> lock(@Param("id") UUID id);

    Page<Proposal> findByCustomerUserId(UUID customerUserId, Pageable pageable);

    Page<Proposal> findByCustomerUserIdAndStatusIn(UUID customerUserId, Collection<ProposalStatus> statuses,
                                                   Pageable pageable);

    /** Ids das propostas com prazo vencido nos estados indicados, as mais antigas primeiro. */
    @Query("""
            select p.id from Proposal p
             where p.status in :statuses and p.expiresAt is not null and p.expiresAt <= :now
             order by p.expiresAt, p.id""")
    List<UUID> findExpiredIds(@Param("statuses") Collection<ProposalStatus> statuses, @Param("now") Instant now,
                              Pageable pageable);

    /**
     * Ids das propostas em análise cujo alerta de SLA já devia ter sido enviado e não foi
     * ({@code ProposalSlaJob}, S4-05 §10), as mais antigas primeiro.
     */
    @Query("""
            select p.id from Proposal p
             where p.status = mz.co.moseguros.proposal.ProposalStatus.UNDER_REVIEW
               and p.slaAlertAt is not null and p.slaAlertAt <= :now and p.slaAlertedAt is null
             order by p.slaAlertAt, p.id""")
    List<UUID> findSlaAlertDueIds(@Param("now") Instant now, Pageable pageable);
}
