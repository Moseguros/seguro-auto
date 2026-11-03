package mz.co.moseguros.shared.security;

import java.io.IOException;
import java.util.UUID;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mz.co.moseguros.shared.tenant.TenantContext;
import mz.co.moseguros.shared.tenant.TenantSnapshot;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Preenche o {@link TenantContext} a partir do token de acesso (ADR-002), depois da
 * autenticação, e limpa-o no fim do pedido:
 *
 * <ul>
 *   <li>{@code type = CUSTOMER} → {@code CUSTOMER} com {@code userId = sub};</li>
 *   <li>{@code type = STAFF} sem {@code orgId} e com o perfil {@code PLATFORM_ADMIN} → {@code PLATFORM};</li>
 *   <li>{@code type = STAFF} com {@code orgId} → {@code ORG}, com {@code insurerId = orgId} se
 *       {@code orgType = INSURER};</li>
 *   <li>outro caso, ou sem token → {@code NONE}.</li>
 * </ul>
 *
 * <p>Não é um bean: o {@code SecurityConfig} acrescenta-o à cadeia de segurança (como bean
 * seria registado também como filtro do servlet).
 */
public class TenantContextFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        try {
            TenantContext.set(snapshotOf(SecurityContextHolder.getContext().getAuthentication()));
            chain.doFilter(request, response);
        } finally {
            TenantContext.clear();
        }
    }

    static TenantSnapshot snapshotOf(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt jwt)) {
            return TenantSnapshot.none();
        }
        return snapshotOf(jwt);
    }

    static TenantSnapshot snapshotOf(Jwt jwt) {
        UUID userId = uuid(jwt.getSubject());
        if (userId == null) {
            return TenantSnapshot.none();
        }
        String type = jwt.getClaimAsString(JwtClaims.TYPE);
        if (JwtClaims.TYPE_CUSTOMER.equals(type)) {
            return TenantSnapshot.customer(userId);
        }
        if (!JwtClaims.TYPE_STAFF.equals(type)) {
            return TenantSnapshot.none();
        }
        UUID orgId = uuid(jwt.getClaimAsString(JwtClaims.ORG_ID));
        if (orgId != null) {
            UUID insurerId = JwtClaims.ORG_TYPE_INSURER.equals(jwt.getClaimAsString(JwtClaims.ORG_TYPE)) ? orgId : null;
            return TenantSnapshot.org(userId, orgId, insurerId);
        }
        if (JwtClaimsConverter.list(jwt, JwtClaims.ROLES).contains(JwtClaims.ROLE_PLATFORM_ADMIN)) {
            return TenantSnapshot.platform(userId);
        }
        return TenantSnapshot.none();
    }

    private static UUID uuid(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
