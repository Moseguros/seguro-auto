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
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/** Propostas visíveis no âmbito da transacção (Row Level Security). */
public interface ProposalRepository extends JpaRepository<Proposal, UUID> {

    Optional<Proposal> findByQuoteId(UUID quoteId);

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
}
