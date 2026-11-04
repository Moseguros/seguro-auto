package mz.co.moseguros.identity.application;

import java.util.Set;
import java.util.UUID;

import mz.co.moseguros.document.FileAccessPolicy;
import mz.co.moseguros.identity.domain.KycCheckRepository;
import mz.co.moseguros.shared.security.CurrentUser;
import mz.co.moseguros.shared.tenant.Scope;
import mz.co.moseguros.shared.tenant.TenantContext;
import org.springframework.stereotype.Component;

/**
 * Quem lê as fotografias do KYC (dono {@value KycService#OWNER_TYPE}; S4-01 §8): a verificação tem
 * de ser visível no âmbito de quem pede (o RLS filtra: o cliente vê as suas, a seguradora as que lhe
 * foram enviadas) e, além disso, o pedido vem do próprio cliente (âmbito {@code CUSTOMER}) ou de staff
 * da seguradora com {@code kyc:review} (âmbito {@code ORG}). Usa só o repositório do próprio módulo.
 */
@Component
public class KycFileAccessPolicy implements FileAccessPolicy {

    public static final String REVIEW_PERMISSION = "kyc:review";

    private final KycCheckRepository checks;

    public KycFileAccessPolicy(KycCheckRepository checks) {
        this.checks = checks;
    }

    @Override
    public Set<String> ownerTypes() {
        return Set.of(KycService.OWNER_TYPE);
    }

    @Override
    public boolean canRead(String ownerType, UUID ownerId, CurrentUser user) {
        if (!user.isAuthenticated()) {
            return false;
        }
        Scope scope = TenantContext.current().scope();
        return checks.findById(ownerId)
                .filter(check -> switch (scope) {
                    case CUSTOMER -> check.getUserId().equals(user.id());
                    case ORG -> user.hasPermission(REVIEW_PERMISSION);
                    default -> false;
                })
                .isPresent();
    }
}
