package mz.co.moseguros.policy.domain;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

/** Prestações visíveis no âmbito da transacção (Row Level Security). */
public interface InstalmentRepository extends JpaRepository<Instalment, UUID> {

    List<Instalment> findByPolicyIdOrderBySeqAsc(UUID policyId);
}
