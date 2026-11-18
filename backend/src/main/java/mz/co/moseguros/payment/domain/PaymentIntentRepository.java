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
import org.springframework.data.jpa.repository.Modifying;
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

    /**
     * Intenções {@code UNKNOWN} com a consulta vencida ({@code PaymentStatusQueryJob}, S5-04 §4),
     * bloqueadas com {@code for update skip locked}.
     */
    @Query(value = """
            select id from payment.payment_intent
             where status = 'UNKNOWN' and next_query_at <= :now
             order by next_query_at, id
             limit :limit
             for update skip locked""", nativeQuery = true)
    List<UUID> lockQueryDue(@Param("now") Instant now, @Param("limit") int limit);

    /**
     * Reserva as intenções para a consulta em curso (outra instância não as apanha até {@code until});
     * não muda a {@code version}: é só a marcação técnica da próxima consulta.
     */
    @Modifying
    @Query(value = "update payment.payment_intent set next_query_at = :until where id in (:ids)", nativeQuery = true)
    int deferQuery(@Param("ids") Collection<UUID> ids, @Param("until") Instant until);

    /**
     * M-Pesa {@code CREATED} criadas antes de {@code createdBefore} e ainda válidas: o envio perdeu-se
     * (executor cheio ou reinício) e o {@code PaymentDispatchRecoveryJob} volta a submetê-las (S5-04 §4).
     */
    @Query(value = """
            select id from payment.payment_intent
             where method = 'MPESA' and status = 'CREATED' and created_at < :createdBefore and expires_at > :now
             order by created_at, id
             limit :limit
             for update skip locked""", nativeQuery = true)
    List<UUID> lockUndispatchedMpesa(@Param("createdBefore") Instant createdBefore, @Param("now") Instant now,
                                     @Param("limit") int limit);

    /**
     * M-Pesa {@code PENDING} enviadas antes de {@code dispatchedBefore} (candidatas a thread perdida;
     * o job confirma o prazo de cada uma com o {@code c2bTimeoutSeconds} da configuração).
     */
    @Query(value = """
            select id from payment.payment_intent
             where method = 'MPESA' and status = 'PENDING' and dispatched_at < :dispatchedBefore
             order by dispatched_at, id
             limit :limit
             for update skip locked""", nativeQuery = true)
    List<UUID> lockStalePendingMpesa(@Param("dispatchedBefore") Instant dispatchedBefore, @Param("limit") int limit);
}
