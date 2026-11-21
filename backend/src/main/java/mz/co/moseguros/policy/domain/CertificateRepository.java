package mz.co.moseguros.policy.domain;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import mz.co.moseguros.policy.CertificateStatus;
import org.springframework.data.jpa.repository.JpaRepository;

/** Certificados visíveis no âmbito da transacção (Row Level Security). */
public interface CertificateRepository extends JpaRepository<Certificate, UUID> {

    Optional<Certificate> findByPolicyIdAndStatus(UUID policyId, CertificateStatus status);

    List<Certificate> findByPolicyIdInAndStatus(Collection<UUID> policyIds, CertificateStatus status);

    Optional<Certificate> findByVerificationToken(String verificationToken);

    boolean existsByVerificationToken(String verificationToken);
}
