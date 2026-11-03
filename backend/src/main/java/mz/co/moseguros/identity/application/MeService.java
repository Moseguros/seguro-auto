package mz.co.moseguros.identity.application;

import java.util.List;
import java.util.UUID;

import mz.co.moseguros.identity.domain.AccountType;
import mz.co.moseguros.identity.domain.CustomerProfile;
import mz.co.moseguros.identity.domain.CustomerProfileRepository;
import mz.co.moseguros.identity.domain.UserAccount;
import mz.co.moseguros.identity.domain.UserAccountRepository;
import mz.co.moseguros.organization.OrganizationApi;
import mz.co.moseguros.organization.OrganizationSummary;
import mz.co.moseguros.shared.domain.Msisdn;
import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.error.ErrorCodes;
import mz.co.moseguros.shared.security.CurrentUser;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Dados do utilizador autenticado ({@code GET /me}). Lê a conta no âmbito do próprio pedido
 * (RLS); os perfis e as permissões são os do token, que são os que a API aplica até à
 * renovação seguinte.
 */
@Service
public class MeService {

    private final CurrentUser currentUser;
    private final UserAccountRepository accounts;
    private final CustomerProfileRepository profiles;
    private final OrganizationApi organizations;

    public MeService(CurrentUser currentUser, UserAccountRepository accounts, CustomerProfileRepository profiles,
                     OrganizationApi organizations) {
        this.currentUser = currentUser;
        this.accounts = accounts;
        this.profiles = profiles;
        this.organizations = organizations;
    }

    @PreAuthorize("isAuthenticated()")
    @Transactional(readOnly = true)
    public Me getMe() {
        UUID userId = currentUser.id();
        UserAccount account = accounts.findById(userId)
                .orElseThrow(() -> new DomainException(ErrorCodes.UNAUTHORIZED, 401, "Account not found"));
        String displayName = account.getDisplayName();
        Boolean profileComplete = null;
        if (account.isCustomer()) {
            CustomerProfile profile = profiles.findByUserId(userId).orElse(null);
            displayName = profile == null ? null : profile.getFullName();
            profileComplete = profile != null && profile.isComplete();
        }
        OrganizationSummary organization = account.getOrganizationId() == null ? null
                : organizations.findSummary(account.getOrganizationId()).orElse(null);
        return new Me(account.getId(), account.getType(), displayName, account.getMsisdn(),
                account.isMsisdnVerified(), account.getEmail(), account.isEmailVerified(),
                account.getPreferredLanguage(), organization, currentUser.roles(), currentUser.permissions(),
                profileComplete);
    }

    /**
     * Utilizador autenticado.
     *
     * @param profileComplete só para clientes; {@code null} para staff
     */
    public record Me(UUID id, AccountType type, String displayName, Msisdn msisdn, boolean msisdnVerified,
                     String email, boolean emailVerified, String preferredLanguage,
                     OrganizationSummary organization, List<String> roles, List<String> permissions,
                     Boolean profileComplete) {
    }
}
