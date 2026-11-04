package mz.co.moseguros.identity;

import java.util.UUID;

/**
 * Contacto de um utilizador de staff para os outros módulos ({@link IdentityApi#staffWithPermission},
 * S4-05; decisão D10: o S8 e o S10 reutilizam-no). Não é um DTO de API; {@link #toString()} não
 * mostra o e-mail.
 *
 * @param displayName       nome apresentado; pode ser {@code null} numa conta ainda sem nome
 * @param email             e-mail de login (normalizado)
 * @param preferredLanguage {@code pt} ou {@code en}
 */
public record StaffContact(UUID userId, String displayName, String email, String preferredLanguage) {

    @Override
    public String toString() {
        return "StaffContact[userId=" + userId + ", preferredLanguage=" + preferredLanguage + "]";
    }
}
