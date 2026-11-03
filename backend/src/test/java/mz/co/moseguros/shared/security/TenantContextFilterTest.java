package mz.co.moseguros.shared.security;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import mz.co.moseguros.shared.tenant.Scope;
import mz.co.moseguros.shared.tenant.TenantContext;
import mz.co.moseguros.shared.tenant.TenantSnapshot;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

class TenantContextFilterTest {

    final UUID userId = UUID.randomUUID();
    final UUID orgId = UUID.randomUUID();

    @AfterEach
    void clear() {
        SecurityContextHolder.clearContext();
        TenantContext.clear();
    }

    @Test
    void withoutTokenTheScopeIsNone() {
        assertThat(TenantContextFilter.snapshotOf((org.springframework.security.core.Authentication) null))
                .isEqualTo(TenantSnapshot.none());
    }

    @Test
    void mapsTheTokenToTheScope() {
        assertThat(TenantContextFilter.snapshotOf(jwt("CUSTOMER", null, null, List.of("CUSTOMER"))))
                .isEqualTo(TenantSnapshot.customer(userId));
        assertThat(TenantContextFilter.snapshotOf(jwt("STAFF", null, null, List.of("PLATFORM_ADMIN"))))
                .isEqualTo(TenantSnapshot.platform(userId));
        assertThat(TenantContextFilter.snapshotOf(jwt("STAFF", orgId, "INSURER", List.of("INSURER_ADMIN"))))
                .isEqualTo(TenantSnapshot.org(userId, orgId, orgId));
        assertThat(TenantContextFilter.snapshotOf(jwt("STAFF", orgId, "BROKER", List.of("ORG_ADMIN"))))
                .isEqualTo(TenantSnapshot.org(userId, orgId, null));
        assertThat(TenantContextFilter.snapshotOf(jwt("STAFF", null, null, List.of("ORG_ADMIN"))))
                .isEqualTo(TenantSnapshot.none());
        assertThat(TenantContextFilter.snapshotOf(jwt("ROBOT", null, null, List.of())))
                .isEqualTo(TenantSnapshot.none());
    }

    @Test
    void setsTheContextDuringTheRequestAndClearsItAfterwards() throws Exception {
        Jwt jwt = jwt("CUSTOMER", null, null, List.of("CUSTOMER"));
        SecurityContextHolder.getContext().setAuthentication(new JwtClaimsConverter().convert(jwt));
        AtomicReference<TenantSnapshot> seen = new AtomicReference<>();

        new TenantContextFilter().doFilter(new MockHttpServletRequest(), new MockHttpServletResponse(),
                (req, res) -> seen.set(TenantContext.current()));

        assertThat(seen.get().scope()).isEqualTo(Scope.CUSTOMER);
        assertThat(TenantContext.current()).isEqualTo(TenantSnapshot.none());
    }

    @Test
    void authoritiesArePermissionsAndPrefixedRoles() {
        Jwt jwt = Jwt.withTokenValue("t").header("alg", "RS256").subject(userId.toString())
                .issuedAt(Instant.now()).expiresAt(Instant.now().plusSeconds(60))
                .claim("roles", List.of("PLATFORM_ADMIN"))
                .claim("permissions", List.of("otp-settings:manage", "organization:manage"))
                .build();

        assertThat(new JwtClaimsConverter().convert(jwt).getAuthorities())
                .extracting(GrantedAuthority::getAuthority)
                .containsExactly("otp-settings:manage", "organization:manage", "ROLE_PLATFORM_ADMIN");
    }

    private Jwt jwt(String type, UUID org, String orgType, List<String> roles) {
        Jwt.Builder builder = Jwt.withTokenValue("t").header("alg", "RS256").subject(userId.toString())
                .issuedAt(Instant.now()).expiresAt(Instant.now().plusSeconds(60))
                .claim("type", type).claim("roles", roles).claim("permissions", List.of());
        if (org != null) {
            builder.claim("orgId", org.toString()).claim("orgType", orgType);
        }
        return builder.build();
    }
}
