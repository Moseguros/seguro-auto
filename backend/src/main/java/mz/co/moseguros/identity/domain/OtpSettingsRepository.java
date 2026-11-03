package mz.co.moseguros.identity.domain;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Definições de OTP (âmbitos SYSTEM e PLATFORM).
 */
public interface OtpSettingsRepository extends JpaRepository<OtpSettings, UUID> {

    /** A única linha de definições. */
    Optional<OtpSettings> findFirstBy();
}
