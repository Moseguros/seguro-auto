package mz.co.moseguros.identity.application;

import java.util.List;
import java.util.UUID;

import mz.co.moseguros.identity.domain.RefreshToken.RevokedReason;
import mz.co.moseguros.identity.domain.UserAccountRepository;
import mz.co.moseguros.organization.OrganizationSuspended;
import mz.co.moseguros.shared.tenant.RunAsSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

/**
 * Eventos do módulo {@code organization} (S1-04): quando uma organização é suspensa, revoga
 * todas as sessões dos seus utilizadores ({@code ORG_SUSPENDED}). A dependência continua
 * {@code identity → organization}.
 */
@Component
public class OrganizationEventsListener {

    private static final Logger log = LoggerFactory.getLogger(OrganizationEventsListener.class);

    private final UserAccountRepository accounts;
    private final SessionService sessions;

    public OrganizationEventsListener(UserAccountRepository accounts, SessionService sessions) {
        this.accounts = accounts;
        this.sessions = sessions;
    }

    @ApplicationModuleListener
    @RunAsSystem
    public void on(OrganizationSuspended event) {
        List<UUID> users = accounts.findIdsByOrganizationId(event.organizationId());
        int revoked = users.stream()
                .mapToInt(userId -> sessions.revokeAllForUser(userId, RevokedReason.ORG_SUSPENDED))
                .sum();
        log.info("Organization {} suspended: revoked {} refresh tokens of {} users", event.organizationId(), revoked,
                users.size());
    }
}
