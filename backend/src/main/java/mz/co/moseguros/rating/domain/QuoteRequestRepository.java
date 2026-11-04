package mz.co.moseguros.rating.domain;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import mz.co.moseguros.shared.domain.Msisdn;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/** Pedidos de cotação visíveis no âmbito da transacção (Row Level Security). */
public interface QuoteRequestRepository extends JpaRepository<QuoteRequest, UUID> {

    /** Pedidos feitos pelo utilizador desde {@code since} (limite por hora, S3-02 §3.7). */
    long countByRequestedByUserIdAndCreatedAtAfter(UUID requestedByUserId, Instant since);

    /** O pedido mais antigo do utilizador desde {@code since}. */
    Optional<QuoteRequest> findFirstByRequestedByUserIdAndCreatedAtAfterOrderByCreatedAtAsc(UUID requestedByUserId,
                                                                                           Instant since);

    Page<QuoteRequest> findByCustomerUserId(UUID customerUserId, Pageable pageable);

    Page<QuoteRequest> findByRequestedByOrgId(UUID requestedByOrgId, Pageable pageable);

    Page<QuoteRequest> findByRequestedByOrgIdAndClientMsisdn(UUID requestedByOrgId, Msisdn clientMsisdn,
                                                            Pageable pageable);
}
