package mz.co.moseguros.payment.application;

import java.util.Set;
import java.util.UUID;

import mz.co.moseguros.document.FileAccessPolicy;
import mz.co.moseguros.payment.domain.PaymentIntentRepository;
import mz.co.moseguros.shared.security.CurrentUser;
import mz.co.moseguros.shared.tenant.TenantContext;
import mz.co.moseguros.shared.tenant.TenantSnapshot;
import org.springframework.stereotype.Component;

/**
 * Quem lê os PDF das referências ({@code PAYMENT_REFERENCE}, dono = a intenção; S5-02 §7; modelo
 * transversal §1.1): o pagador (âmbito {@code CUSTOMER}) ou o staff da seguradora com
 * {@code payment:read}. Outro → 404 no download.
 */
@Component
public class PaymentFileAccessPolicy implements FileAccessPolicy {

    /** Tipo de dono dos PDF das referências. */
    public static final String OWNER_TYPE = "PAYMENT_REFERENCE";
    static final String READ_PERMISSION = "payment:read";

    private final PaymentIntentRepository intents;

    public PaymentFileAccessPolicy(PaymentIntentRepository intents) {
        this.intents = intents;
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
        return intents.findById(ownerId)
                .filter(i -> switch (tenant.scope()) {
                    case CUSTOMER -> i.getPayerUserId().equals(user.id());
                    case ORG -> tenant.insurerId() != null && tenant.insurerId().equals(i.getInsurerId())
                            && user.hasPermission(READ_PERMISSION);
                    default -> false;
                })
                .isPresent();
    }
}
