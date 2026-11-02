package mz.co.moseguros.identity.application;

import java.time.Clock;
import java.util.List;

import mz.co.moseguros.identity.domain.InvitationRepository;
import mz.co.moseguros.identity.domain.ManagerRoles;
import mz.co.moseguros.identity.domain.UserAccountRepository;
import mz.co.moseguros.shared.domain.Emails;
import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.tenant.RunAsSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Primeiro administrador da plataforma (S1-04 §5). No arranque, se
 * {@code moseguros.bootstrap.platform-admin-email} estiver definido, não houver nenhuma conta
 * {@code ACTIVE} com {@code PLATFORM_ADMIN} e não houver um convite da plataforma pendente para esse
 * e-mail, cria um convite com {@code PLATFORM_ADMIN} e envia o e-mail.
 *
 * <p>Regista o e-mail mascarado; nunca o link nem o token.
 */
@Component
public class PlatformAdminBootstrap implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(PlatformAdminBootstrap.class);

    private final InvitationService invitationService;
    private final UserAccountRepository accounts;
    private final InvitationRepository invitations;
    private final TransactionTemplate tx;
    private final Clock clock;
    private final String email;
    private final String name;

    public PlatformAdminBootstrap(InvitationService invitationService, UserAccountRepository accounts,
                                  InvitationRepository invitations, TransactionTemplate tx, Clock clock,
                                  @Value("${moseguros.bootstrap.platform-admin-email:}") String email,
                                  @Value("${moseguros.bootstrap.platform-admin-name:Administrador}") String name) {
        this.invitationService = invitationService;
        this.accounts = accounts;
        this.invitations = invitations;
        this.tx = tx;
        this.clock = clock;
        this.email = email;
        this.name = name == null || name.isBlank() ? "Administrador" : name;
    }

    @Override
    @RunAsSystem
    public void run(ApplicationArguments args) {
        bootstrap();
    }

    /** Cria o convite, se for preciso. Devolve {@code true} se criou. */
    boolean bootstrap() {
        if (email == null || email.isBlank()) {
            return false;
        }
        String normalized;
        try {
            normalized = Emails.normalize(email);
        } catch (DomainException e) {
            log.warn("moseguros.bootstrap.platform-admin-email is not a valid e-mail address; no invitation created");
            return false;
        }
        String masked = mask(normalized);
        Boolean needed = tx.execute(status -> !accounts.existsActiveWithRole(ManagerRoles.PLATFORM_ADMIN)
                && !invitations.existsPendingPlatformInvitation(normalized, clock.instant()));
        if (!Boolean.TRUE.equals(needed)) {
            log.info("Platform admin bootstrap: nothing to do (active platform admin or pending invitation for {})",
                    masked);
            return false;
        }
        try {
            InvitationService.InvitationView invitation = invitationService.inviteBySystem(name, normalized,
                    List.of(ManagerRoles.PLATFORM_ADMIN));
            log.info("Platform admin bootstrap: invitation {} sent to {}", invitation.id(), masked);
            return true;
        } catch (DomainException e) {
            log.warn("Platform admin bootstrap: no invitation for {} ({})", masked, e.getCode());
            return false;
        }
    }

    /** {@code a***@dominio}. */
    static String mask(String email) {
        int at = email.indexOf('@');
        return (at > 0 ? email.charAt(0) : "") + "***" + (at >= 0 ? email.substring(at) : "");
    }
}
