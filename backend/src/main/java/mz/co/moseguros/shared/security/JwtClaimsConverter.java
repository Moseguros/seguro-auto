package mz.co.moseguros.shared.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

/**
 * Converte um token de acesso válido em autenticação: autoridades = cada permissão tal e qual
 * ({@code organization:manage}) + cada perfil com o prefixo {@code ROLE_}
 * ({@code ROLE_PLATFORM_ADMIN}). A autorização faz-se sempre por permissão
 * ({@code @PreAuthorize("hasAuthority('organization:manage')")}).
 */
public class JwtClaimsConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        return new JwtAuthenticationToken(jwt, authorities(jwt), jwt.getSubject());
    }

    static Collection<GrantedAuthority> authorities(Jwt jwt) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (String permission : list(jwt, JwtClaims.PERMISSIONS)) {
            authorities.add(new SimpleGrantedAuthority(permission));
        }
        for (String role : list(jwt, JwtClaims.ROLES)) {
            authorities.add(new SimpleGrantedAuthority(JwtClaims.ROLE_PREFIX + role));
        }
        return authorities;
    }

    static List<String> list(Jwt jwt, String claim) {
        List<String> values = jwt.hasClaim(claim) ? jwt.getClaimAsStringList(claim) : null;
        return values == null ? List.of() : values;
    }
}
