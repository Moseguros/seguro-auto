package mz.co.moseguros.policy.domain;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

/** Linha temporal das apólices visíveis no âmbito da transacção (Row Level Security). */
public interface PolicyEventRepository extends JpaRepository<PolicyEvent, UUID> {

    List<PolicyEvent> findByPolicyIdOrderByOccurredAtAscIdAsc(UUID policyId);

    boolean existsByPolicyIdAndType(UUID policyId, PolicyEventType type);
}
