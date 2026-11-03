package mz.co.moseguros.rating.domain;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/** Cotações visíveis no âmbito da transacção (Row Level Security). */
public interface QuoteRepository extends JpaRepository<Quote, UUID> {

    List<Quote> findByQuoteRequestId(UUID quoteRequestId);

    List<Quote> findByQuoteRequestIdIn(Collection<UUID> quoteRequestIds);

    /** Lê a cotação com {@code select ... for update} ({@code RatingApi.markUsed}). */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select q from Quote q where q.id = :id")
    Optional<Quote> findForUpdate(@Param("id") UUID id);
}
