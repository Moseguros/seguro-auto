package mz.co.moseguros.identity.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Verificações de identidade visíveis no âmbito da transacção (Row Level Security: o cliente vê as
 * suas, a seguradora as que lhe foram enviadas).
 */
public interface KycCheckRepository extends JpaRepository<KycCheck, UUID>, JpaSpecificationExecutor<KycCheck> {

    /** Verificação mais recente do cliente ({@code submitted_at desc}). */
    Optional<KycCheck> findFirstByUserIdOrderBySubmittedAtDesc(UUID userId);

    boolean existsByUserIdAndStatus(UUID userId, KycCheckStatus status);

    /** Verificações do cliente, da mais recente para a mais antiga. */
    List<KycCheck> findByUserIdOrderBySubmittedAtDesc(UUID userId);
}
