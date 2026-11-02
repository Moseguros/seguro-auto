package mz.co.moseguros.shared.tenant;

import java.util.UUID;

import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.error.ErrorCodes;

/**
 * Âmbito do pedido actual, guardado por thread. Por defeito {@link Scope#NONE} (não vê nada).
 *
 * <p>O {@link TenantJpaTransactionManager} copia-o para a ligação ao começar cada
 * transacção ({@code set_config('app.scope', ..., true)} e afins). O preenchimento a
 * partir do token é feito no S1-02.
 */
public final class TenantContext {

    private static final ThreadLocal<TenantSnapshot> CURRENT =
            ThreadLocal.withInitial(TenantSnapshot::none);

    private TenantContext() {
    }

    public static TenantSnapshot current() {
        return CURRENT.get();
    }

    public static void set(TenantSnapshot snapshot) {
        if (snapshot == null) {
            clear();
        } else {
            CURRENT.set(snapshot);
        }
    }

    public static void clear() {
        CURRENT.remove();
    }

    /** Seguradora do âmbito actual; {@code TENANT_REQUIRED} (500) se não houver. */
    public static UUID insurerId() {
        UUID insurerId = current().insurerId();
        if (insurerId == null) {
            throw new DomainException(ErrorCodes.TENANT_REQUIRED, 500, "No insurer in the current tenant context");
        }
        return insurerId;
    }
}
