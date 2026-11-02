package mz.co.moseguros.identity.application;

import java.time.Clock;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import mz.co.moseguros.identity.TokenPair;
import mz.co.moseguros.identity.domain.AccountStatus;
import mz.co.moseguros.identity.domain.AccountType;
import mz.co.moseguros.identity.domain.Invitation;
import mz.co.moseguros.identity.domain.InvitationRepository;
import mz.co.moseguros.identity.domain.UserAccount;
import mz.co.moseguros.identity.domain.UserAccountRepository;
import mz.co.moseguros.identity.domain.UserRole;
import mz.co.moseguros.identity.domain.UserRoleRepository;
import mz.co.moseguros.organization.OrganizationApi;
import mz.co.moseguros.organization.OrganizationSummary;
import mz.co.moseguros.organization.OrganizationType;
import mz.co.moseguros.shared.audit.ActorType;
import mz.co.moseguros.shared.audit.AuditRecord;
import mz.co.moseguros.shared.audit.AuditService;
import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.error.ErrorCodes;
import mz.co.moseguros.shared.tenant.RunAsSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Activação de staff pelo convidado (S1-04 §3, ADR-003): consultar o convite, definir a
 * palavra-passe e o TOTP ({@code accept}) e confirmar com o primeiro código ({@code activate}).
 *
 * <p>Endpoints públicos: tudo corre em {@code SYSTEM} e filtra pelo hash do token. Nunca regista
 * o token, a palavra-passe nem o segredo TOTP.
 */
@Service
public class ActivationService {

    private static final Logger log = LoggerFactory.getLogger(ActivationService.class);

    private final InvitationRepository invitations;
    private final UserAccountRepository accounts;
    private final UserRoleRepository userRoles;
    private final OrganizationApi organizations;
    private final PasswordPolicy passwordPolicy;
    private final PasswordEncoder passwordEncoder;
    private final TotpService totp;
    private final TokenService tokenService;
    private final AuditService audit;
    private final InvitationProperties properties;
    private final Clock clock;

    public ActivationService(InvitationRepository invitations, UserAccountRepository accounts,
                             UserRoleRepository userRoles, OrganizationApi organizations,
                             PasswordPolicy passwordPolicy, PasswordEncoder passwordEncoder, TotpService totp,
                             TokenService tokenService, AuditService audit, InvitationProperties properties,
                             Clock clock) {
        this.invitations = invitations;
        this.accounts = accounts;
        this.userRoles = userRoles;
        this.organizations = organizations;
        this.passwordPolicy = passwordPolicy;
        this.passwordEncoder = passwordEncoder;
        this.totp = totp;
        this.tokenService = tokenService;
        this.audit = audit;
        this.properties = properties;
        this.clock = clock;
    }

    /**
     * Dados do convite para o ecrã de activação.
     *
     * @throws DomainException {@code INVITATION_INVALID} (404) se o token for desconhecido, ou o
     *                         convite estiver expirado, revogado ou já aceite
     */
    @RunAsSystem
    @Transactional(readOnly = true)
    public InvitationDetails view(String token) {
        Invitation invitation = usable(invitations.findByTokenHash(hash(token)));
        OrganizationSummary organization = invitation.getOrganizationId() == null ? null
                : organizations.findSummary(invitation.getOrganizationId()).orElse(null);
        return new InvitationDetails(invitation.getEmail(), invitation.getFullName(),
                organization == null ? null : organization.displayName(),
                organization == null ? null : organization.type(), invitation.getExpiresAt());
    }

    /**
     * Define a palavra-passe e um segredo TOTP novo; cria a conta {@code PENDING_ACTIVATION} se
     * ainda não existir. Pode repetir-se até à activação (substitui a palavra-passe e o segredo).
     *
     * @throws DomainException {@code INVITATION_INVALID} (404), {@code PASSWORD_TOO_WEAK} (400) ou
     *                         {@code EMAIL_ALREADY_IN_USE} (409)
     */
    @RunAsSystem
    @Transactional
    public TotpSetup accept(String token, String password) {
        Invitation invitation = usable(invitations.findByTokenHashForUpdate(hash(token)));
        passwordPolicy.check(password, invitation.getEmail(), "password");
        UserAccount account = account(invitation);
        String secret = totp.newSecret();
        account.setCredentials(invitation.getFullName(), passwordEncoder.encode(password), secret);
        accounts.save(account);
        log.info("Invitation {}: password and TOTP set for account {}", invitation.getId(), account.getId());
        return new TotpSetup(secret, totp.otpauthUri(invitation.getEmail(), secret));
    }

    /**
     * Confirma o primeiro código TOTP: activa a conta, atribui os perfis do convite e abre uma
     * sessão. Os códigos errados contam no convite mesmo com a resposta de erro; ao 5.º o convite
     * é revogado.
     *
     * @param deviceInfo o {@code User-Agent} do pedido
     * @throws DomainException {@code INVITATION_INVALID} (404), {@code INVITATION_NOT_ACCEPTED} (400),
     *                         {@code TOTP_INVALID} (400) ou {@code ORGANIZATION_NOT_ACTIVE} (409)
     */
    @RunAsSystem
    @Transactional(noRollbackFor = DomainException.class)
    public TokenPair activate(String token, String code, String deviceInfo) {
        Instant now = clock.instant();
        Invitation invitation = usable(invitations.findByTokenHashForUpdate(hash(token)));
        UserAccount account = Optional.ofNullable(invitation.getUserId())
                .flatMap(accounts::findById)
                .filter(UserAccount::hasCredentials)
                .orElseThrow(() -> DomainException.of(ErrorCodes.INVITATION_NOT_ACCEPTED,
                        "Set the password before activating"));
        OptionalLong step = totp.verify(account.getTotpSecret(), code, null);
        if (step.isEmpty()) {
            int attempts = invitation.registerFailedActivation();
            if (attempts >= properties.maxActivationAttempts()) {
                invitation.revoke(now);
                log.info("Invitation {}: revoked after {} wrong TOTP codes", invitation.getId(), attempts);
                throw invalid();
            }
            log.info("Invitation {}: wrong TOTP code ({} attempts)", invitation.getId(), attempts);
            throw DomainException.of(ErrorCodes.TOTP_INVALID, "Wrong TOTP code");
        }
        OrganizationSummary organization = null;
        if (invitation.getOrganizationId() != null) {
            organization = organizations.findSummary(invitation.getOrganizationId()).orElse(null);
            if (organization == null || !organization.isActive()) {
                throw DomainException.of(ErrorCodes.ORGANIZATION_NOT_ACTIVE, "The organization is suspended");
            }
        }

        account.activate(step.getAsLong());
        account.recordLogin(now);
        accounts.save(account);
        replaceRoles(account.getId(), invitation.getRoleCodes());
        invitation.accept(now);
        invitations.save(invitation);

        Map<String, Object> after = new LinkedHashMap<>();
        after.put("invitationId", invitation.getId());
        after.put("organizationId", invitation.getOrganizationId());
        after.put("roleCodes", invitation.getRoleCodes());
        after.put("status", account.getStatus().name());
        UUID insurerId = organization != null && organization.type() == OrganizationType.INSURER
                ? organization.id() : null;
        audit.record(new AuditRecord(ActorType.STAFF, account.getId().toString(), insurerId, "USER.ACTIVATE",
                "UserAccount", account.getId().toString(), null, after));
        log.info("Invitation {}: account {} activated", invitation.getId(), account.getId());
        return tokenService.issue(account, deviceInfo);
    }

    /** Conta ligada ao convite, ou uma conta de staff nova {@code PENDING_ACTIVATION}. */
    private UserAccount account(Invitation invitation) {
        if (invitation.getUserId() != null) {
            UserAccount account = accounts.findById(invitation.getUserId())
                    .orElseThrow(() -> new IllegalStateException("Invitation linked to a missing account"));
            if (!account.isStaff() || !Objects.equals(account.getOrganizationId(), invitation.getOrganizationId())) {
                throw new IllegalStateException("Invitation linked to an account of another organization");
            }
            return account;
        }
        Optional<UserAccount> existing = accounts.findByTypeAndEmail(AccountType.STAFF, invitation.getEmail());
        if (existing.isPresent()) {
            UserAccount account = existing.get();
            if (account.getStatus() != AccountStatus.PENDING_ACTIVATION
                    || !Objects.equals(account.getOrganizationId(), invitation.getOrganizationId())) {
                throw DomainException.of(ErrorCodes.EMAIL_ALREADY_IN_USE,
                        "The e-mail address is already used by another staff account");
            }
            invitation.linkUser(account.getId());
            return account;
        }
        UserAccount account = accounts.save(UserAccount.newStaff(invitation.getEmail(), invitation.getFullName(),
                invitation.getOrganizationId()));
        invitation.linkUser(account.getId());
        return account;
    }

    /** Os perfis passam a ser exactamente os do convite (sem apagar e voltar a inserir os mesmos). */
    private void replaceRoles(UUID userId, List<String> roleCodes) {
        Set<String> wanted = Set.copyOf(roleCodes);
        List<UserRole> current = userRoles.findByUserId(userId);
        List<UserRole> toRemove = current.stream().filter(r -> !wanted.contains(r.getRoleCode())).toList();
        userRoles.deleteAll(toRemove);
        Set<String> kept = current.stream().map(UserRole::getRoleCode).collect(Collectors.toSet());
        roleCodes.stream().filter(code -> !kept.contains(code))
                .forEach(code -> userRoles.save(new UserRole(userId, code)));
        userRoles.flush();
    }

    private Invitation usable(Optional<Invitation> invitation) {
        Instant now = clock.instant();
        return invitation.filter(i -> i.isUsable(now)).orElseThrow(ActivationService::invalid);
    }

    private static String hash(String token) {
        return TokenService.hash(token == null ? "" : token);
    }

    private static DomainException invalid() {
        return DomainException.of(ErrorCodes.INVITATION_INVALID, "The invitation is not valid");
    }

    // --- resultados ---

    /**
     * Convite visto pelo convidado.
     *
     * @param organizationName {@code null} na plataforma
     */
    public record InvitationDetails(String email, String fullName, String organizationName,
                                    OrganizationType organizationType, Instant expiresAt) {
    }

    /** Segredo TOTP novo e o URI para o QR code. Nunca registado. */
    public record TotpSetup(String totpSecret, String otpauthUri) {

        @Override
        public String toString() {
            return "TotpSetup[totpSecret=***, otpauthUri=***]";
        }
    }
}
