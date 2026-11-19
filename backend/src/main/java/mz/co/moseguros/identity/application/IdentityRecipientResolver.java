package mz.co.moseguros.identity.application;

import java.util.Optional;
import java.util.UUID;

import mz.co.moseguros.identity.domain.AccountStatus;
import mz.co.moseguros.identity.domain.UserAccountRepository;
import mz.co.moseguros.notification.NotificationRecipient;
import mz.co.moseguros.notification.NotificationRecipientResolver;
import mz.co.moseguros.shared.tenant.RunAsSystem;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Contactos de uma conta para as notificações (S6-04 §1; modelo transversal §1.1, decisão D6): lê
 * {@code identity.user_account} pelo id, em {@code SYSTEM}. {@code active} = estado {@code ACTIVE}; a língua
 * {@code en} ou, outra, {@code pt}.
 */
@Component
public class IdentityRecipientResolver implements NotificationRecipientResolver {

    private final UserAccountRepository accounts;

    public IdentityRecipientResolver(UserAccountRepository accounts) {
        this.accounts = accounts;
    }

    @Override
    @RunAsSystem
    @Transactional(readOnly = true)
    public Optional<NotificationRecipient> resolve(UUID userId) {
        return accounts.findById(userId).map(a -> new NotificationRecipient(a.getId(), a.getMsisdn(),
                a.isMsisdnVerified(), a.getEmail(), a.isEmailVerified(),
                "en".equals(a.getPreferredLanguage()) ? "en" : "pt", a.getStatus() == AccountStatus.ACTIVE));
    }
}
