package mz.co.moseguros.identity.application;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.criteria.Predicate;
import mz.co.moseguros.identity.application.InvitationService.InvitationView;
import mz.co.moseguros.identity.domain.AccountStatus;
import mz.co.moseguros.identity.domain.AccountType;
import mz.co.moseguros.identity.domain.ManagerRoles;
import mz.co.moseguros.identity.domain.RefreshToken.RevokedReason;
import mz.co.moseguros.identity.domain.Role;
import mz.co.moseguros.identity.domain.RoleRepository;
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
import mz.co.moseguros.shared.security.CurrentUser;
import mz.co.moseguros.shared.tenant.Scope;
import mz.co.moseguros.shared.tenant.TenantContext;
import mz.co.moseguros.shared.tenant.TenantSnapshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Gestão dos utilizadores de staff (S1-05 §3): listar, consultar, alterar perfis, desactivar,
 * reactivar, desbloquear e repor o acesso.
 *
 * <p>Corre no âmbito de quem gere, com as regras dos convites do S1-04: {@code PLATFORM} gere
 * qualquer organização e o staff da plataforma; {@code ORG} só a sua organização (um utilizador
 * de outra não é visível pelo RLS → 404 {@code USER_NOT_FOUND}). Ninguém altera a própria conta
 * ({@code CANNOT_MODIFY_SELF}) e a organização (ou a plataforma) nunca fica sem um gestor activo
 * ({@code LAST_MANAGER}).
 */
@Service
public class StaffUserService {

    private static final Logger log = LoggerFactory.getLogger(StaffUserService.class);

    private final UserAccountRepository accounts;
    private final UserRoleRepository userRoles;
    private final RoleRepository roles;
    private final OrganizationApi organizations;
    private final SessionService sessions;
    private final InvitationService invitations;
    private final AuditService audit;
    private final CurrentUser currentUser;
    private final TransactionTemplate tx;
    private final Clock clock;
    private final EntityManager entityManager;

    public StaffUserService(UserAccountRepository accounts, UserRoleRepository userRoles, RoleRepository roles,
                            OrganizationApi organizations, SessionService sessions, InvitationService invitations,
                            AuditService audit, CurrentUser currentUser, TransactionTemplate tx, Clock clock,
                            EntityManager entityManager) {
        this.accounts = accounts;
        this.userRoles = userRoles;
        this.roles = roles;
        this.organizations = organizations;
        this.sessions = sessions;
        this.invitations = invitations;
        this.audit = audit;
        this.currentUser = currentUser;
        this.tx = tx;
        this.clock = clock;
        this.entityManager = entityManager;
    }

    // --- consulta ---

    /**
     * Utilizadores de staff de uma organização.
     *
     * @param status estado mostrado ({@code LOCKED} só enquanto o bloqueio não passou)
     * @param q      parte do nome ou do e-mail, sem diferenciar maiúsculas
     * @throws DomainException {@code ORGANIZATION_NOT_FOUND} (404) fora do âmbito
     */
    @PreAuthorize("hasAuthority('org-user:read')")
    @Transactional(readOnly = true)
    public Page<StaffUserView> listForOrganization(UUID organizationId, AccountStatus status, String q,
                                                   Pageable pageable) {
        organizationInScope(organizationId);
        return list(organizationId, status, q, pageable);
    }

    /** Utilizadores de staff da plataforma. Exige o âmbito {@code PLATFORM} (senão 403). */
    @PreAuthorize("hasAuthority('org-user:read')")
    @Transactional(readOnly = true)
    public Page<StaffUserView> listForPlatform(AccountStatus status, String q, Pageable pageable) {
        requirePlatformScope();
        return list(null, status, q, pageable);
    }

    /** @throws DomainException {@code USER_NOT_FOUND} (404) */
    @PreAuthorize("hasAuthority('org-user:read')")
    @Transactional(readOnly = true)
    public StaffUserView get(UUID userId) {
        UserAccount account = findInScope(userId);
        return StaffUserView.of(account, roleCodes(userId), clock.instant());
    }

    // --- acções ---

    /**
     * Substitui os perfis. Vale no login seguinte ou na renovação seguinte do token.
     *
     * @param version versão lida antes da alteração; diferente → {@code CONCURRENT_MODIFICATION} (409)
     * @throws DomainException {@code USER_NOT_FOUND} (404), {@code CANNOT_MODIFY_SELF} (400),
     *                         {@code INVALID_STATE_TRANSITION} (409) numa conta por activar,
     *                         {@code ROLE_NOT_ALLOWED_FOR_ORGANIZATION} (400), {@code LAST_MANAGER} (400)
     */
    @PreAuthorize("hasAuthority('org-user:manage')")
    @Transactional
    public StaffUserView updateRoles(UUID userId, List<String> roleCodes, long version) {
        Instant now = clock.instant();
        UserAccount account = findInScope(userId);
        requireNotSelf(account);
        if (account.getVersion() != version) {
            throw DomainException.of(ErrorCodes.CONCURRENT_MODIFICATION,
                    "The user was changed by another request (version " + account.getVersion() + ")");
        }
        if (account.getStatus() == AccountStatus.PENDING_ACTIVATION) {
            // Os perfis de uma conta por activar vêm do convite e seriam substituídos na activação.
            throw DomainException.of(ErrorCodes.INVALID_STATE_TRANSITION,
                    "The roles of an account pending activation come from its invitation");
        }
        OrganizationSummary organization = organizationOf(account);
        String orgType = organization == null ? null : organization.type().name();
        List<String> codes = roleCodes == null ? List.of() : List.copyOf(new LinkedHashSet<>(roleCodes));
        checkRoles(codes, orgType);
        List<String> before = roleCodes(userId);
        List<String> managerRoles = ManagerRoles.managerRoles(orgType);
        boolean remainsManager = codes.stream().anyMatch(managerRoles::contains);
        requireAnotherManager(account, before, managerRoles, remainsManager, now);

        // A linha da conta não muda, mas quem a leu antes tem de ver uma versão nova.
        entityManager.lock(account, LockModeType.PESSIMISTIC_FORCE_INCREMENT);
        replaceRoles(userId, codes);
        List<String> after = codes.stream().sorted().toList();
        record("USER.ROLES_UPDATE", account, organization, Map.of("roleCodes", before), Map.of("roleCodes", after));
        log.info("Staff account {}: roles changed to {}", userId, after);
        accounts.flush();
        return StaffUserView.of(account, after, now);
    }

    /**
     * {@code ACTIVE}/{@code LOCKED} → {@code DISABLED}; revoga as sessões ({@code DISABLED}).
     *
     * @throws DomainException {@code USER_NOT_FOUND} (404), {@code CANNOT_MODIFY_SELF} (400),
     *                         {@code INVALID_STATE_TRANSITION} (409), {@code LAST_MANAGER} (400)
     */
    @PreAuthorize("hasAuthority('org-user:manage')")
    @Transactional
    public StaffUserView disable(UUID userId) {
        Instant now = clock.instant();
        UserAccount account = findInScope(userId);
        requireNotSelf(account);
        OrganizationSummary organization = organizationOf(account);
        List<String> current = roleCodes(userId);
        requireAnotherManager(account, current,
                ManagerRoles.managerRoles(organization == null ? null : organization.type().name()), false, now);
        AccountStatus before = account.getStatus();
        account.disable();
        accounts.saveAndFlush(account);
        int revoked = sessions.revokeAllForUser(userId, RevokedReason.DISABLED);
        record("USER.DISABLE", account, organization, Map.of("status", before.name()),
                Map.of("status", account.getStatus().name(), "revokedSessions", revoked));
        log.info("Staff account {} disabled ({} sessions revoked)", userId, revoked);
        return StaffUserView.of(account, current, now);
    }

    /** {@code DISABLED} → {@code ACTIVE}, com as falhas a zero. */
    @PreAuthorize("hasAuthority('org-user:manage')")
    @Transactional
    public StaffUserView enable(UUID userId) {
        return changeStatus(userId, "USER.ENABLE", UserAccount::enable);
    }

    /** {@code LOCKED} → {@code ACTIVE}, com as falhas a zero. */
    @PreAuthorize("hasAuthority('org-user:manage')")
    @Transactional
    public StaffUserView unlock(UUID userId) {
        return changeStatus(userId, "USER.UNLOCK", UserAccount::unlock);
    }

    /**
     * Reposição de acesso (telemóvel do TOTP perdido, por exemplo): a conta fica
     * {@code PENDING_ACTIVATION} sem palavra-passe nem TOTP, as sessões são revogadas
     * ({@code ACCESS_RESET}) e é enviado um convite novo, ligado à conta, com os perfis actuais.
     *
     * @throws DomainException {@code USER_NOT_FOUND} (404), {@code CANNOT_MODIFY_SELF} (400),
     *                         {@code INVALID_STATE_TRANSITION} (409), {@code ORGANIZATION_NOT_ACTIVE} (409)
     */
    @PreAuthorize("hasAuthority('org-user:manage')")
    public InvitationView resetAccess(UUID userId) {
        return tx.execute(status -> {
            UserAccount account = findInScope(userId);
            requireNotSelf(account);
            OrganizationSummary organization = organizationOf(account);
            if (organization != null && !organization.isActive()) {
                throw DomainException.of(ErrorCodes.ORGANIZATION_NOT_ACTIVE, "The organization is suspended");
            }
            List<String> current = roleCodes(userId);
            AccountStatus before = account.getStatus();
            account.resetAccess();
            accounts.saveAndFlush(account);
            int revoked = sessions.revokeAllForUser(userId, RevokedReason.ACCESS_RESET);
            record("USER.ACCESS_RESET", account, organization, Map.of("status", before.name()),
                    Map.of("status", account.getStatus().name(), "revokedSessions", revoked));
            log.info("Staff account {}: access reset ({} sessions revoked)", userId, revoked);
            String fullName = account.getDisplayName() != null ? account.getDisplayName()
                    : account.getEmail().substring(0, account.getEmail().indexOf('@'));
            return invitations.inviteForAccessReset(organization, fullName, account.getEmail(), current);
        });
    }

    // --- apoio ---

    private StaffUserView changeStatus(UUID userId, String action, Consumer<UserAccount> change) {
        Instant now = clock.instant();
        UserAccount account = findInScope(userId);
        requireNotSelf(account);
        AccountStatus before = account.getStatus();
        change.accept(account);
        accounts.saveAndFlush(account);
        record(action, account, organizationOf(account), Map.of("status", before.name()),
                Map.of("status", account.getStatus().name()));
        log.info("Staff account {}: {} ({} → {})", userId, action, before, account.getStatus());
        return StaffUserView.of(account, roleCodes(userId), now);
    }

    private Page<StaffUserView> list(UUID organizationId, AccountStatus status, String q, Pageable pageable) {
        Instant now = clock.instant();
        Page<UserAccount> page = accounts.findAll(filter(organizationId, status, q, now), pageable);
        List<UUID> ids = page.getContent().stream().map(UserAccount::getId).toList();
        Map<UUID, List<String>> rolesByUser = ids.isEmpty() ? Map.of()
                : userRoles.findByUserIdIn(ids).stream().collect(Collectors.groupingBy(UserRole::getUserId,
                        Collectors.mapping(UserRole::getRoleCode, Collectors.toList())));
        return page.map(account -> StaffUserView.of(account,
                rolesByUser.getOrDefault(account.getId(), List.of()).stream().sorted().toList(), now));
    }

    /**
     * Conta de staff visível no âmbito (RLS); no âmbito {@code ORG} tem de ser da organização de
     * quem pede.
     */
    private UserAccount findInScope(UUID userId) {
        TenantSnapshot tenant = TenantContext.current();
        if (tenant.scope() != Scope.PLATFORM && tenant.scope() != Scope.ORG && tenant.scope() != Scope.SYSTEM) {
            throw DomainException.of(ErrorCodes.FORBIDDEN, "Access denied");
        }
        return accounts.findById(userId)
                .filter(UserAccount::isStaff)
                .filter(a -> tenant.scope() != Scope.ORG || tenant.orgId().equals(a.getOrganizationId()))
                .orElseThrow(() -> DomainException.of(ErrorCodes.USER_NOT_FOUND, "User not found"));
    }

    private void requireNotSelf(UserAccount account) {
        if (account.getId().equals(currentUser.id())) {
            throw DomainException.of(ErrorCodes.CANNOT_MODIFY_SELF, "Managers cannot change their own account");
        }
    }

    /**
     * Se a conta é hoje um gestor activo e deixa de o ser, tem de haver outro gestor activo na
     * organização (ou na plataforma).
     */
    private void requireAnotherManager(UserAccount account, List<String> currentRoles, List<String> managerRoles,
                                       boolean remainsManager, Instant now) {
        boolean isManager = account.effectiveStatus(now) == AccountStatus.ACTIVE
                && currentRoles.stream().anyMatch(managerRoles::contains);
        if (!isManager || remainsManager) {
            return;
        }
        long others = account.getOrganizationId() == null
                ? accounts.countActiveInPlatformWithRoles(managerRoles, account.getId(), now)
                : accounts.countActiveInOrganizationWithRoles(account.getOrganizationId(), managerRoles,
                        account.getId(), now);
        if (others == 0) {
            throw DomainException.of(ErrorCodes.LAST_MANAGER,
                    "The organization would be left without an active manager");
        }
    }

    /** Cada perfil tem de existir e poder ser atribuído no tipo de organização (ou na plataforma). */
    private void checkRoles(List<String> codes, String orgType) {
        List<Role> found = codes.isEmpty() ? List.of() : roles.findByCodeIn(codes);
        boolean allAllowed = found.size() == codes.size()
                && found.stream().allMatch(role -> ManagerRoles.assignable(role, orgType));
        if (codes.isEmpty() || !allAllowed) {
            throw DomainException.of(ErrorCodes.ROLE_NOT_ALLOWED_FOR_ORGANIZATION,
                    "A role is unknown or not allowed for " + (orgType == null ? "the platform" : orgType));
        }
    }

    /** Os perfis passam a ser exactamente os indicados (sem apagar e voltar a inserir os mesmos). */
    private void replaceRoles(UUID userId, List<String> roleCodes) {
        Set<String> wanted = Set.copyOf(roleCodes);
        List<UserRole> current = userRoles.findByUserId(userId);
        userRoles.deleteAll(current.stream().filter(r -> !wanted.contains(r.getRoleCode())).toList());
        Set<String> kept = current.stream().map(UserRole::getRoleCode).collect(Collectors.toSet());
        roleCodes.stream().filter(code -> !kept.contains(code))
                .forEach(code -> userRoles.save(new UserRole(userId, code)));
        userRoles.flush();
    }

    private List<String> roleCodes(UUID userId) {
        return userRoles.findByUserId(userId).stream().map(UserRole::getRoleCode).sorted().toList();
    }

    private OrganizationSummary organizationOf(UserAccount account) {
        if (account.getOrganizationId() == null) {
            return null;
        }
        return organizations.findSummary(account.getOrganizationId())
                .orElseThrow(() -> new IllegalStateException("Staff account of a missing organization"));
    }

    /** Organização visível para quem pede (mesmas regras dos convites). */
    private OrganizationSummary organizationInScope(UUID organizationId) {
        TenantSnapshot tenant = TenantContext.current();
        if (tenant.scope() == Scope.ORG) {
            if (!organizationId.equals(tenant.orgId())) {
                throw organizationNotFound();
            }
        } else if (tenant.scope() != Scope.PLATFORM && tenant.scope() != Scope.SYSTEM) {
            throw DomainException.of(ErrorCodes.FORBIDDEN, "Access denied");
        }
        return organizations.findSummary(organizationId).orElseThrow(StaffUserService::organizationNotFound);
    }

    private static void requirePlatformScope() {
        Scope scope = TenantContext.current().scope();
        if (scope != Scope.PLATFORM && scope != Scope.SYSTEM) {
            throw DomainException.of(ErrorCodes.FORBIDDEN, "Only platform staff can do this");
        }
    }

    private static DomainException organizationNotFound() {
        return DomainException.of(ErrorCodes.ORGANIZATION_NOT_FOUND, "Organization not found");
    }

    private void record(String action, UserAccount account, OrganizationSummary organization, Object before,
                        Object after) {
        UUID insurerId = organization != null && organization.type() == OrganizationType.INSURER
                ? organization.id() : null;
        UUID actor = currentUser.id();
        audit.record(new AuditRecord(ActorType.STAFF, actor.toString(), insurerId, action, "UserAccount",
                account.getId().toString(), before, after));
    }

    static Specification<UserAccount> filter(UUID organizationId, AccountStatus status, String q, Instant now) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("type"), AccountType.STAFF));
            predicates.add(organizationId == null ? cb.isNull(root.get("organizationId"))
                    : cb.equal(root.get("organizationId"), organizationId));
            if (status == AccountStatus.LOCKED) {
                predicates.add(cb.equal(root.get("status"), AccountStatus.LOCKED));
                predicates.add(cb.greaterThan(root.get("lockedUntil"), now));
            } else if (status == AccountStatus.ACTIVE) {
                predicates.add(cb.or(cb.equal(root.get("status"), AccountStatus.ACTIVE),
                        cb.and(cb.equal(root.get("status"), AccountStatus.LOCKED),
                                cb.lessThanOrEqualTo(root.get("lockedUntil"), now))));
            } else if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            if (q != null && !q.isBlank()) {
                String pattern = "%" + escapeLike(q.trim().toLowerCase(Locale.ROOT)) + "%";
                predicates.add(cb.or(cb.like(cb.lower(root.get("displayName")), pattern, '\\'),
                        cb.like(root.get("email"), pattern, '\\')));
            }
            return cb.and(predicates.toArray(Predicate[]::new));
        };
    }

    static String escapeLike(String value) {
        return value.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
    }

    /** Utilizador de staff como é devolvido (sem credenciais). */
    public record StaffUserView(UUID id, String email, String displayName, AccountStatus status,
                                List<String> roles, Instant lastLoginAt, Instant createdAt, long version) {

        static StaffUserView of(UserAccount account, List<String> roles, Instant now) {
            return new StaffUserView(account.getId(), account.getEmail(), account.getDisplayName(),
                    account.effectiveStatus(now), List.copyOf(roles), account.getLastLoginAt(),
                    account.getCreatedAt(), account.getVersion());
        }
    }
}
