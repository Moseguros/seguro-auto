package mz.co.moseguros.shared.idempotency;

import java.util.Optional;
import java.util.UUID;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/** Registos de idempotência visíveis no âmbito (RLS: os do próprio utilizador, ou todos em SYSTEM). */
public interface IdempotencyRecordRepository extends JpaRepository<IdempotencyRecord, UUID> {

    Optional<IdempotencyRecord> findByUserIdAndIdempotencyKey(UUID userId, UUID idempotencyKey);

    /** O registo bloqueado até ao fim da transacção ({@code select … for update}). */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select r from IdempotencyRecord r where r.userId = :userId and r.idempotencyKey = :key")
    Optional<IdempotencyRecord> lock(@Param("userId") UUID userId, @Param("key") UUID key);
}
