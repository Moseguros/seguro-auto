package mz.co.moseguros.identity.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Pedidos de recuperação de palavra-passe (só âmbito SYSTEM).
 */
public interface PasswordResetRepository extends JpaRepository<PasswordReset, UUID> {

    Optional<PasswordReset> findByTokenHash(String tokenHash);

    List<PasswordReset> findByUserIdAndUsedAtIsNullAndInvalidatedAtIsNull(UUID userId);
}
