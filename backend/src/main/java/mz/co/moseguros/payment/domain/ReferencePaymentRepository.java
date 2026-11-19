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

    /**
     * A referência da seguradora com esta entidade e referência, bloqueada até ao fim da transacção (S5-10
     * §3): a activa primeiro, depois a mais recente. Filtra sempre pela seguradora (corre em SYSTEM).
     */
    @Query(value = """
            select * from payment.reference_payment
             where insurer_id = :insurerId and entity = :entity and reference = :reference
             order by (status = 'ACTIVE') desc, created_at desc
             limit 1
             for update""", nativeQuery = true)
    Optional<ReferencePayment> lockForReconciliation(@Param("insurerId") UUID insurerId,
                                                     @Param("entity") String entity,
                                                     @Param("reference") String reference);

    boolean existsByEntityAndReference(String entity, String reference);

    boolean existsByEntityAndInvoiceNumber(String entity, int invoiceNumber);
}
