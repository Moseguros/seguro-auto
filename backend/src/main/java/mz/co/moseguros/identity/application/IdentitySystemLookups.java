package mz.co.moseguros.identity.application;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import mz.co.moseguros.identity.StaffContact;
import mz.co.moseguros.identity.domain.AccountStatus;
import mz.co.moseguros.identity.domain.AccountType;
import mz.co.moseguros.identity.domain.Invitation;
import mz.co.moseguros.identity.domain.InvitationRepository;
import mz.co.moseguros.identity.domain.OtpChallenge.IdentifierType;
import mz.co.moseguros.identity.domain.UserAccount;
import mz.co.moseguros.identity.domain.UserAccountRepository;
import mz.co.moseguros.organization.OrganizationApi;
import mz.co.moseguros.organization.OrganizationType;
import mz.co.moseguros.shared.domain.Msisdn;
import mz.co.moseguros.shared.tenant.RunAsSystem;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Verificações do convite que têm de ver todas as organizações (S1-04 §2), quando quem convida
 * só vê a sua (âmbito {@code ORG}), e dos contactos do cliente, que só vê a sua conta (S1-06).
 * Cada método corre em {@code SYSTEM} ({@code @RunAsSystem}) e filtra pelo e-mail ou contacto
 * indicado; o resto do pedido corre no âmbito de quem o faz.
 */
@Component
public class IdentitySystemLookups {

    private final UserAccountRepository accounts;
    private final InvitationRepository invitations;
    private final OrganizationApi organizations;

    public IdentitySystemLookups(UserAccountRepository accounts, InvitationRepository invitations,
                                 OrganizationApi organizations) {
        this.accounts = accounts;
        this.invitations = invitations;
        this.organizations = organizations;
    }

    /**
     * A organização existe, é uma seguradora ({@code INSURER}) e está {@code ACTIVE} (S4-01 §3: o
     * cliente, em âmbito {@code CUSTOMER}, não vê as organizações).
     */
    @RunAsSystem
    @Transactional(readOnly = true)
    public boolean insurerActive(UUID insurerId) {
        return organizations.findSummary(insurerId)
                .filter(o -> o.type() == OrganizationType.INSURER && o.isActive())
                .isPresent();
    }

    /**
     * Contactos de uma conta {@code CUSTOMER} para a revisão do KYC (S4-01 §7 e §9): a seguradora, em
     * âmbito {@code ORG}, não vê as contas dos clientes. Outra conta ou inexistente → vazio.
     */
    @RunAsSystem
    @Transactional(readOnly = true)
    public Optional<CustomerContact> customerContact(UUID userId) {
        return accounts.findById(userId)
                .filter(UserAccount::isCustomer)
                .map(a -> new CustomerContact(a.getMsisdn(),
                        a.getEmail() != null && a.isEmailVerified() ? a.getEmail() : null,
                        a.getPreferredLanguage()));
    }

    /**
     * Contas de staff {@code ACTIVE} da organização com algum perfil que tenha a permissão, ordenadas
     * por {@code displayName} ({@code IdentityApi.staffWithPermission}, S4-05 §10; decisão D10).
     */
    @RunAsSystem
    @Transactional(readOnly = true)
    public List<StaffContact> staffWithPermission(UUID organizationId, String permission) {
        if (organizationId == null || permission == null || permission.isBlank()) {
            return List.of();
        }
        return accounts.findActiveStaffWithPermission(organizationId, permission).stream()
                .map(a -> new StaffContact(a.getId(), a.getDisplayName(), a.getEmail(), a.getPreferredLanguage()))
                .toList();
    }

    /** Conta de staff com o e-mail (normalizado), em qualquer organização ou na plataforma. */
    @RunAsSystem
    @Transactional(readOnly = true)
    public Optional<StaffAccount> findStaffByEmail(String email) {
        return accounts.findByTypeAndEmail(AccountType.STAFF, email)
                .map(a -> new StaffAccount(a.getId(), a.getOrganizationId(), a.getStatus()));
    }

    /**
     * O contacto (normalizado) já pertence a uma conta de cliente diferente de
     * {@code exceptUserId} (S1-06). O âmbito {@code CUSTOMER} só vê a própria conta.
     *
     * @param value telemóvel {@code 258XXXXXXXXX} ou e-mail normalizado
     */
    @RunAsSystem
    @Transactional(readOnly = true)
    public boolean customerContactTaken(IdentifierType type, String value, UUID exceptUserId) {
        Optional<UserAccount> owner = type == IdentifierType.MSISDN
                ? accounts.findByTypeAndMsisdn(AccountType.CUSTOMER, Msisdn.of(value))
                : accounts.findByTypeAndEmail(AccountType.CUSTOMER, value);
        return owner.filter(account -> !account.getId().equals(exceptUserId)).isPresent();
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
     * Contactos de um cliente.
     *
     * @param msisdn        {@code null} se a conta só tem e-mail
     * @param verifiedEmail {@code null} sem e-mail verificado
     * @param language      língua das mensagens ({@code pt} ou {@code en})
     */
    public record CustomerContact(Msisdn msisdn, String verifiedEmail, String language) {

        @Override
        public String toString() {
            return "CustomerContact[msisdn=" + msisdn + ", language=" + language + "]";
        }
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
