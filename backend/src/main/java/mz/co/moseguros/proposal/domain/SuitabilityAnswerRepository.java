package mz.co.moseguros.proposal.domain;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

/** Respostas de adequação visíveis no âmbito da transacção (as da proposta visível, RLS). */
public interface SuitabilityAnswerRepository extends JpaRepository<SuitabilityAnswer, UUID> {

    List<SuitabilityAnswer> findByProposalId(UUID proposalId);
}
