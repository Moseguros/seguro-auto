package mz.co.moseguros.proposal.application;

import java.util.Set;
import java.util.UUID;

import mz.co.moseguros.document.FileAccessPolicy;
import mz.co.moseguros.proposal.domain.InspectionRepository;
import mz.co.moseguros.proposal.domain.Proposal;
import mz.co.moseguros.proposal.domain.ProposalRepository;
import mz.co.moseguros.shared.security.CurrentUser;
import mz.co.moseguros.shared.tenant.TenantContext;
import mz.co.moseguros.shared.tenant.TenantSnapshot;
import org.springframework.stereotype.Component;

/**
 * Quem lê as fotografias da vistoria (dono {@value InspectionService#OWNER_TYPE}; S4-03 §8): a vistoria
 * tem de ser visível no âmbito de quem pede (RLS) e o pedido vem do cliente dono da proposta (âmbito
 * {@code CUSTOMER}), do mediador da proposta (âmbito {@code ORG} com a organização da proposta) ou da
 * seguradora, só depois da submissão da proposta e com {@code inspection:review} ou
 * {@code proposal:read}. Usa só os repositórios do próprio módulo.
 */
@Component
public class InspectionFileAccessPolicy implements FileAccessPolicy {

    public static final String REVIEW_PERMISSION = "inspection:review";
    public static final String READ_PERMISSION = "proposal:read";

    private final InspectionRepository inspections;
    private final ProposalRepository proposals;

    public InspectionFileAccessPolicy(InspectionRepository inspections, ProposalRepository proposals) {
        this.inspections = inspections;
        this.proposals = proposals;
    }

    @Override
    public Set<String> ownerTypes() {
        return Set.of(InspectionService.OWNER_TYPE);
    }

    @Override
    public boolean canRead(String ownerType, UUID ownerId, CurrentUser user) {
        if (!user.isAuthenticated() || ownerId == null) {
            return false;
        }
        TenantSnapshot tenant = TenantContext.current();
        return inspections.findById(ownerId)
                .flatMap(i -> proposals.findById(i.getProposalId()))
                .filter(p -> allowed(p, tenant, user))
                .isPresent();
    }

    private static boolean allowed(Proposal p, TenantSnapshot tenant, CurrentUser user) {
        return switch (tenant.scope()) {
            case CUSTOMER -> p.getCustomerUserId().equals(tenant.userId());
            case ORG -> (tenant.orgId() != null && tenant.orgId().equals(p.getBrokerOrgId()))
                    || (tenant.insurerId() != null && tenant.insurerId().equals(p.getInsurerId())
                        && p.getSubmittedAt() != null
                        && (user.hasPermission(REVIEW_PERMISSION) || user.hasPermission(READ_PERMISSION)));
            default -> false;
        };
    }
}
