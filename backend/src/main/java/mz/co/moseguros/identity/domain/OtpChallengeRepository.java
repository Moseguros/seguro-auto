package mz.co.moseguros.identity.domain;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Desafios OTP (só âmbito SYSTEM).
 */
public interface OtpChallengeRepository extends JpaRepository<OtpChallenge, UUID> {

    List<OtpChallenge> findByIdentifierOrderByCreatedAtDesc(String identifier);

    List<OtpChallenge> findByRootChallengeId(UUID rootChallengeId);
}
