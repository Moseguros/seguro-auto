package mz.co.moseguros.proposal.domain;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

/** Declarações de risco visíveis no âmbito da transacção (as da proposta visível, RLS). */
public interface DeclarationRepository extends JpaRepository<Declaration, UUID> {

    List<Declaration> findByProposalId(UUID proposalId);
}
