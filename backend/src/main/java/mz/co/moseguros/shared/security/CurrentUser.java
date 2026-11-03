package mz.co.moseguros.shared.security;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.error.ErrorCodes;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

/**
 * Utilizador autenticado do pedido actual, lido do token de acesso ({@link Jwt}) no
 * {@code SecurityContext}. Sem token, os métodos lançam {@code UNAUTHORIZED} (401).
 */
@Component
public class CurrentUser {

    /** {@code sub}: id da conta. */
    public UUID id() {
        return UUID.fromString(jwt().getSubject());
    }

    /** {@code CUSTOMER} ou {@code STAFF}. */
    public String type() {
        return jwt().getClaimAsString(JwtClaims.TYPE);
    }

    public boolean isCustomer() {
        return JwtClaims.TYPE_CUSTOMER.equals(type());
    }

    /** Organização do staff; {@code null} para clientes e staff da plataforma. */
    public UUID orgId() {
        String orgId = jwt().getClaimAsString(JwtClaims.ORG_ID);
        return orgId == null ? null : UUID.fromString(orgId);
    }

    /** Tipo da organização ({@code INSURER}…); {@code null} sem organização. */
    public String orgType() {
        return jwt().getClaimAsString(JwtClaims.ORG_TYPE);
    }

    public List<String> roles() {
        return JwtClaimsConverter.list(jwt(), JwtClaims.ROLES);
    }

    public List<String> permissions() {
        return JwtClaimsConverter.list(jwt(), JwtClaims.PERMISSIONS);
    }

    public boolean hasPermission(String permission) {
        return permissions().contains(permission);
    }

    /**
     * {@code false} para pedidos anónimos (ex.: download de um ficheiro público). Quem pode receber
     * pedidos anónimos chama-o antes de {@link #id()} ou {@link #permissions()}.
     */
    public boolean isAuthenticated() {
        return token().isPresent();
    }

    /** Token do pedido, se houver. */
    public Optional<Jwt> token() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.getPrincipal() instanceof Jwt jwt
                ? Optional.of(jwt) : Optional.empty();
    }

    private Jwt jwt() {
        return token().orElseThrow(() -> new DomainException(ErrorCodes.UNAUTHORIZED, 401, "Authentication required"));
    }
}
