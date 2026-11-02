package mz.co.moseguros.shared.tenant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import mz.co.moseguros.shared.error.DomainException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class TenantContextTest {

    @AfterEach
    void clear() {
        TenantContext.clear();
    }

    @Test
    void defaultsToNone() {
        assertThat(TenantContext.current()).isEqualTo(TenantSnapshot.none());
        assertThat(TenantContext.current().scope()).isEqualTo(Scope.NONE);
    }

    @Test
    void setAndClear() {
        UUID user = UUID.randomUUID();
        UUID org = UUID.randomUUID();
        TenantContext.set(TenantSnapshot.org(user, org, org));
        assertThat(TenantContext.current().scope()).isEqualTo(Scope.ORG);
        assertThat(TenantContext.insurerId()).isEqualTo(org);

        TenantContext.set(null);
        assertThat(TenantContext.current().scope()).isEqualTo(Scope.NONE);
    }

    @Test
    void insurerIdIsRequired() {
        TenantContext.set(TenantSnapshot.org(UUID.randomUUID(), UUID.randomUUID(), null));
        assertThatThrownBy(TenantContext::insurerId)
                .isInstanceOfSatisfying(DomainException.class, e -> {
                    assertThat(e.getCode()).isEqualTo("TENANT_REQUIRED");
                    assertThat(e.getHttpStatus()).isEqualTo(500);
                });
    }

    @Test
    void isPerThread() throws Exception {
        TenantContext.set(TenantSnapshot.platform(UUID.randomUUID()));
        AtomicReference<Scope> seen = new AtomicReference<>();
        Thread thread = new Thread(() -> seen.set(TenantContext.current().scope()));
        thread.start();
        thread.join();
        assertThat(seen.get()).isEqualTo(Scope.NONE);
    }

    @Test
    void factoriesRequireTheirIds() {
        assertThatThrownBy(() -> TenantSnapshot.org(null, null, null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> TenantSnapshot.customer(null)).isInstanceOf(NullPointerException.class);
        assertThat(TenantSnapshot.system(null).scope()).isEqualTo(Scope.SYSTEM);
    }
}
