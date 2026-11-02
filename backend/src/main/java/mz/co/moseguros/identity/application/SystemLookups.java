package mz.co.moseguros.identity.application;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import mz.co.moseguros.identity.domain.AccountStatus;
import mz.co.moseguros.identity.domain.AccountType;
import mz.co.moseguros.identity.domain.Invitation;
import mz.co.moseguros.identity.domain.InvitationRepository;
import mz.co.moseguros.identity.domain.UserAccountRepository;
import mz.co.moseguros.shared.tenant.RunAsSystem;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Verificações do convite que têm de ver todas as organizações (S1-04 §2), quando quem convida
 * só vê a sua (âmbito {@code ORG}). Cada método corre em {@code SYSTEM} ({@code @RunAsSystem})
 * e filtra pelo e-mail indicado; o resto do convite corre no âmbito de quem convida.
 */
@Component
public class SystemLookups {

    private final UserAccountRepository accounts;
    private final InvitationRepository invitations;

    public SystemLookups(UserAccountRepository accounts, InvitationRepository invitations) {
        this.accounts = accounts;
        this.invitations = invitations;
    }

    /** Conta de staff com o e-mail (normalizado), em qualquer organização ou na plataforma. */
    @RunAsSystem
    @Transactional(readOnly = true)
    public Optional<StaffAccount> findStaffByEmail(String email) {
        return accounts.findByTypeAndEmail(AccountType.STAFF, email)
                .map(a -> new StaffAccount(a.getId(), a.getOrganizationId(), a.getStatus()));
    }

    /**
     * Revoga os convites por aceitar para o e-mail, em qualquer organização (são substituídos pelo
     * convite novo). Devolve quantos foram revogados.
     */
    @RunAsSystem
    @Transactional
    public int revokePendingInvitations(String email, Instant now) {
        List<Invitation> pending = invitations.findByEmailAndRevokedAtIsNullAndAcceptedAtIsNull(email);
        pending.forEach(invitation -> invitation.revoke(now));
        return pending.size();
    }

    /**
     * Conta de staff, sem dados pessoais.
     *
     * @param organizationId {@code null} = plataforma
     */
    public record StaffAccount(UUID id, UUID organizationId, AccountStatus status) {

        public boolean isPendingActivation() {
            return status == AccountStatus.PENDING_ACTIVATION;
        }
    }
}
