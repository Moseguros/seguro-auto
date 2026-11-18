package mz.co.moseguros.payment.domain;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import jakarta.persistence.LockModeType;
import mz.co.moseguros.payment.PaymentMethod;
import mz.co.moseguros.payment.PaymentStatus;
import mz.co.moseguros.payment.PaymentTargetType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/** Intenções visíveis no âmbito da transacção (RLS: seguradora, pagador, ou todas em SYSTEM). */
public interface PaymentIntentRepository extends JpaRepository<PaymentIntent, UUID>,
        JpaSpecificationExecutor<PaymentIntent> {

    /** A intenção bloqueada até ao fim da transacção ({@code select … for update}). */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from PaymentIntent p where p.id = :id")
    Optional<PaymentIntent> lock(@Param("id") UUID id);

    /** Intenções do alvo nos estados indicados, as mais recentes primeiro. */
    @Query("""
            select p from PaymentIntent p
             where p.targetType = :type and p.targetId = :targetId and p.status in :statuses
             order by p.createdAt desc, p.id desc""")
    List<PaymentIntent> findByTarget(@Param("type") PaymentTargetType type, @Param("targetId") UUID targetId,
                                     @Param("statuses") Collection<PaymentStatus> statuses);

    @Query("""
            select count(p) > 0 from PaymentIntent p
             where p.targetType = :type and p.targetId = :targetId and p.method = :method
               and p.status in :statuses""")
    boolean existsByTarget(@Param("type") PaymentTargetType type, @Param("targetId") UUID targetId,
                           @Param("method") PaymentMethod method,
                           @Param("statuses") Collection<PaymentStatus> statuses);

    @Query("""
            select count(p) > 0 from PaymentIntent p
             where p.targetType = :type and p.targetId = :targetId and p.status = :status""")
    boolean existsByTargetAndStatus(@Param("type") PaymentTargetType type, @Param("targetId") UUID targetId,
                                    @Param("status") PaymentStatus status);

    Page<PaymentIntent> findByPayerUserId(UUID payerUserId, Pageable pageable);

    Page<PaymentIntent> findByPayerUserIdAndStatusIn(UUID payerUserId, Collection<PaymentStatus> statuses,
                                                     Pageable pageable);

    /**
     * Ids das intenções vencidas que o {@code PaymentExpiryJob} expira (S5-02 §8), bloqueadas com
     * {@code for update skip locked} para duas instâncias não tratarem as mesmas.
     */
    @Query(value = """
            select id from payment.payment_intent
             where expires_at < :now
               and ((method = 'BANK_REFERENCE' and status in ('CREATED','PENDING'))
                 or (method = 'MPESA' and status = 'CREATED'))
             order by expires_at, id
             limit :limit
             for update skip locked""", nativeQuery = true)
    List<UUID> lockExpired(@Param("now") Instant now, @Param("limit") int limit);
}
