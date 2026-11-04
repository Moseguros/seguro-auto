package mz.co.moseguros.proposal.application;

import java.util.Optional;
import java.util.UUID;

import mz.co.moseguros.organization.OrganizationApi;
import mz.co.moseguros.organization.OrganizationSummary;
import mz.co.moseguros.organization.OrganizationType;
import mz.co.moseguros.shared.tenant.RunAsSystem;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Leituras entre âmbitos das propostas, em {@code SYSTEM} (S4-02 §9): o cliente não vê as
 * organizações pelo RLS. Lê só a organização pedida e só se for uma seguradora.
 */
@Component
public class ProposalSystemLookups {

    private final OrganizationApi organizations;

    public ProposalSystemLookups(OrganizationApi organizations) {
        this.organizations = organizations;
    }

    /** Seguradora: nome comercial congelado na proposta e se está activa. */
    public record InsurerRef(UUID id, String displayName, boolean active) {
    }

    /** Seguradora da cotação; vazio se não existir ou não for uma seguradora. */
    @RunAsSystem
    @Transactional(readOnly = true)
    public Optional<InsurerRef> insurer(UUID insurerId) {
        return organizations.findSummary(insurerId)
                .filter(o -> o.type() == OrganizationType.INSURER)
                .map(ProposalSystemLookups::ref);
    }

    private static InsurerRef ref(OrganizationSummary o) {
        return new InsurerRef(o.id(), o.displayName(), o.isActive());
    }
}
