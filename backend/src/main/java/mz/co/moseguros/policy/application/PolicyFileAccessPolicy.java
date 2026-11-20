package mz.co.moseguros.policy.application;

import java.util.Set;
import java.util.UUID;

import mz.co.moseguros.document.FileAccessPolicy;
import mz.co.moseguros.policy.domain.PolicyRepository;
import mz.co.moseguros.shared.security.CurrentUser;
import mz.co.moseguros.shared.tenant.TenantContext;
import mz.co.moseguros.shared.tenant.TenantSnapshot;
import org.springframework.stereotype.Component;

/**
 * Quem lê os ficheiros das apólices (dono {@value #OWNER_TYPE}; S6-02 §8, modelo transversal §1.1): o
 * certificado e as condições particulares. A apólice tem de ser visível no âmbito de quem pede (RLS) e,
 * além disso, o pedido vem do cliente tomador (âmbito {@code CUSTOMER}, com {@value #CUSTOMER_PERMISSION})
 * ou da própria seguradora (âmbito {@code ORG}, com {@value #INSURER_PERMISSION}). Todos os outros, anónimos
 * incluídos, não lêem (o {@code document} responde 404 {@code FILE_NOT_FOUND}). Usa só o repositório do
 * próprio módulo.
 */
@Component
public class PolicyFileAccessPolicy implements FileAccessPolicy {

    /** Tipo de dono dos ficheiros das apólices. */
    public static final String OWNER_TYPE = "POLICY";
    static final String CUSTOMER_PERMISSION = "self:policies";
    static final String INSURER_PERMISSION = "policy:read";

    private final PolicyRepository policies;

    public PolicyFileAccessPolicy(PolicyRepository policies) {
        this.policies = policies;
    }

    @Override
    public Set<String> ownerTypes() {
        return Set.of(OWNER_TYPE);
    }

    @Override
    public boolean canRead(String ownerType, UUID ownerId, CurrentUser user) {
        if (!user.isAuthenticated() || ownerId == null || !OWNER_TYPE.equals(ownerType)) {
            return false;
        }
        TenantSnapshot tenant = TenantContext.current();
        return switch (tenant.scope()) {
            case CUSTOMER -> user.hasPermission(CUSTOMER_PERMISSION) && policies.findById(ownerId)
                    .filter(p -> p.getPolicyholderUserId().equals(user.id()))
                    .isPresent();
            case ORG -> user.hasPermission(INSURER_PERMISSION) && tenant.insurerId() != null
                    && policies.findById(ownerId)
                    .filter(p -> tenant.insurerId().equals(p.getInsurerId()))
                    .isPresent();
            default -> false;
        };
    }
}
