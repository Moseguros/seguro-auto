package mz.co.moseguros.shared.tenant;

import java.util.Objects;
import java.util.UUID;

/**
 * Âmbito, utilizador, organização e seguradora da transacção actual.
 *
 * @param insurerId igual a {@code orgId} quando a organização é uma seguradora
 */
public record TenantSnapshot(Scope scope, UUID userId, UUID orgId, UUID insurerId) {

    private static final TenantSnapshot NONE = new TenantSnapshot(Scope.NONE, null, null, null);

    public TenantSnapshot {
        Objects.requireNonNull(scope, "scope");
    }

    public static TenantSnapshot none() {
        return NONE;
    }

    public static TenantSnapshot system(UUID userId) {
        return new TenantSnapshot(Scope.SYSTEM, userId, null, null);
    }

    public static TenantSnapshot platform(UUID userId) {
        return new TenantSnapshot(Scope.PLATFORM, userId, null, null);
    }

    public static TenantSnapshot org(UUID userId, UUID orgId, UUID insurerId) {
        return new TenantSnapshot(Scope.ORG, userId, Objects.requireNonNull(orgId, "orgId"), insurerId);
    }

    public static TenantSnapshot customer(UUID userId) {
        return new TenantSnapshot(Scope.CUSTOMER, Objects.requireNonNull(userId, "userId"), null, null);
    }
}
