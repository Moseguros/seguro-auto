package mz.co.moseguros.payment.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/** Casos para o financeiro (RLS: só a seguradora, ou todos em SYSTEM). */
public interface PaymentExceptionRepository extends JpaRepository<PaymentExceptionCase, UUID>,
        JpaSpecificationExecutor<PaymentExceptionCase> {

    boolean existsByIntentIdAndType(UUID intentId, PaymentExceptionType type);

    List<PaymentExceptionCase> findByIntentId(UUID intentId);

    /** O caso bloqueado até ao fim da transacção (resolução, S5-10 §6). */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select e from PaymentExceptionCase e where e.id = :id")
    Optional<PaymentExceptionCase> lock(@Param("id") UUID id);

    Optional<PaymentExceptionCase> findByMessageId(String messageId);
}
