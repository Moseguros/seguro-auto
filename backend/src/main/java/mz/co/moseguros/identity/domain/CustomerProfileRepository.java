package mz.co.moseguros.identity.domain;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Perfis de cliente visíveis no âmbito da transacção (Row Level Security).
 */
public interface CustomerProfileRepository extends JpaRepository<CustomerProfile, UUID> {

    Optional<CustomerProfile> findByUserId(UUID userId);
}
