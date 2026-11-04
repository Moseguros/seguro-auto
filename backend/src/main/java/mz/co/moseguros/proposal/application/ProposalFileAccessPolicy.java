package mz.co.moseguros.proposal.application;

import java.util.Set;
import java.util.UUID;

import mz.co.moseguros.document.FileAccessPolicy;
import mz.co.moseguros.proposal.domain.ProposalRepository;
import mz.co.moseguros.shared.security.CurrentUser;
import mz.co.moseguros.shared.tenant.TenantContext;
import mz.co.moseguros.shared.tenant.TenantSnapshot;
import org.springframework.stereotype.Component;

/**
 * Quem lê os ficheiros das propostas (dono {@value #OWNER_TYPE}; S4-04 §9), como o PDF assinado: a
 * proposta tem de ser visível no âmbito de quem pede (RLS) e, além disso, o pedido vem do cliente dono
 * (âmbito {@code CUSTOMER}), do mediador da proposta (âmbito {@code ORG} com {@code broker_org_id} igual
 * à organização) ou da seguradora depois da submissão, com {@code proposal:read}. Usa só o repositório do
 * próprio módulo.
 */
@Component
public class ProposalFileAccessPolicy implements FileAccessPolicy {

    /** Tipo de dono dos ficheiros das propostas. */
    public static final String OWNER_TYPE = "PROPOSAL";
    /** Permissão da seguradora para ler propostas submetidas (S4-05). */
    public static final String READ_PERMISSION = "proposal:read";

    private final ProposalRepository proposals;

    public ProposalFileAccessPolicy(ProposalRepository proposals) {
        this.proposals = proposals;
    }

    @Override
    public Set<String> ownerTypes() {
        return Set.of(OWNER_TYPE);
    }

    @Override
    public boolean canRead(String ownerType, UUID ownerId, CurrentUser user) {
        if (!user.isAuthenticated() || ownerId == null) {
            return false;
        }
        TenantSnapshot tenant = TenantContext.current();
        return proposals.findById(ownerId)
                .filter(p -> switch (tenant.scope()) {
                    case CUSTOMER -> p.getCustomerUserId().equals(user.id());
                    case ORG -> (tenant.orgId() != null && tenant.orgId().equals(p.getBrokerOrgId()))
                            || (tenant.insurerId() != null && tenant.insurerId().equals(p.getInsurerId())
                                && p.getSubmittedAt() != null && user.hasPermission(READ_PERMISSION));
                    default -> false;
                })
                .isPresent();
    }
}
