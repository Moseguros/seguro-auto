package mz.co.moseguros.notification.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Mensagens enviadas (só âmbito SYSTEM).
 */
public interface OutboundMessageRepository extends JpaRepository<OutboundMessage, UUID> {

    Optional<OutboundMessage> findFirstByProviderAndProviderMessageId(String provider, String providerMessageId);

    List<OutboundMessage> findByCorrelationIdOrderByCreatedAtAsc(UUID correlationId);
}
