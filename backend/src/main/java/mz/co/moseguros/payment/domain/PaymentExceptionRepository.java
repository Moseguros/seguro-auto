package mz.co.moseguros.payment.domain;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

/** Casos para o financeiro (RLS: só a seguradora, ou todos em SYSTEM). */
public interface PaymentExceptionRepository extends JpaRepository<PaymentExceptionCase, UUID> {

    boolean existsByIntentIdAndType(UUID intentId, PaymentExceptionType type);

    List<PaymentExceptionCase> findByIntentId(UUID intentId);
}
