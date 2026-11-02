package mz.co.moseguros.identity.domain;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Pedidos de recuperação de palavra-passe (só âmbito SYSTEM).
 */
public interface PasswordResetRepository extends JpaRepository<PasswordReset, UUID> {

    Optional<PasswordReset> findByTokenHash(String tokenHash);

    /** Lê o pedido com {@code select ... for update} (o mesmo token não serve duas vezes). */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select r from PasswordReset r where r.tokenHash = :tokenHash")
    Optional<PasswordReset> findByTokenHashForUpdate(@Param("tokenHash") String tokenHash);

    List<PasswordReset> findByUserIdAndUsedAtIsNullAndInvalidatedAtIsNull(UUID userId);

    /** Pedidos da conta criados depois de {@code since} (limite de e-mails por hora). */
    long countByUserIdAndCreatedAtAfter(UUID userId, Instant since);
}
