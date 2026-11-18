package mz.co.moseguros.payment.domain;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

/** Histórico das intenções (RLS como {@code payment_intent}). */
public interface PaymentEventRepository extends JpaRepository<PaymentEvent, UUID> {

    List<PaymentEvent> findByIntentIdOrderByReceivedAtAscIdAsc(UUID intentId);
}
