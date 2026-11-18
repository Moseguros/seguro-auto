package mz.co.moseguros.payment.domain;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/** Referências Ponto24 (RLS: seguradora, pagador, ou todas em SYSTEM). */
public interface ReferencePaymentRepository extends JpaRepository<ReferencePayment, UUID> {

    Optional<ReferencePayment> findByIntentId(UUID intentId);

    /** A referência da intenção bloqueada até ao fim da transacção. */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select r from ReferencePayment r where r.intentId = :intentId")
    Optional<ReferencePayment> lockByIntentId(@Param("intentId") UUID intentId);

    List<ReferencePayment> findByIntentIdIn(Collection<UUID> intentIds);

    boolean existsByEntityAndReference(String entity, String reference);

    boolean existsByEntityAndInvoiceNumber(String entity, int invoiceNumber);
}
