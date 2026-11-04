package mz.co.moseguros.proposal.domain;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

/** Assinaturas das propostas visíveis no âmbito da transacção (Row Level Security). */
public interface SignatureRepository extends JpaRepository<Signature, UUID> {

    /** Todas as assinaturas da proposta (válidas e invalidadas), as mais recentes primeiro. */
    List<Signature> findByProposalIdOrderBySignedAtDesc(UUID proposalId);
}
