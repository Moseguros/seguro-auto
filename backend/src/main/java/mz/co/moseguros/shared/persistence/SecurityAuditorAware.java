package mz.co.moseguros.shared.persistence;

import java.util.Optional;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

/**
 * Autor das alterações ({@code created_by}/{@code updated_by}): o {@code sub} do
 * utilizador autenticado ou {@value #SYSTEM} quando não há utilizador (jobs, arranque).
 */
public class SecurityAuditorAware implements AuditorAware<String> {

    public static final String SYSTEM = "system";

    @Override
    public Optional<String> getCurrentAuditor() {
        return Optional.of(currentActor());
    }

    static String currentActor() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            return SYSTEM;
        }
        if (auth.getPrincipal() instanceof Jwt jwt && jwt.getSubject() != null) {
            return jwt.getSubject();
        }
        String name = auth.getName();
        return name == null || name.isBlank() ? SYSTEM : name;
    }
}
