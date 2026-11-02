package mz.co.moseguros.identity.application;

import java.security.SecureRandom;
import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import jakarta.persistence.criteria.Predicate;
import mz.co.moseguros.identity.domain.Invitation;
import mz.co.moseguros.identity.domain.InvitationRepository;
import mz.co.moseguros.identity.domain.InvitationStatus;
import mz.co.moseguros.identity.domain.ManagerRoles;
import mz.co.moseguros.identity.domain.Role;
import mz.co.moseguros.identity.domain.RoleRepository;
import mz.co.moseguros.notification.DeliveryResult;
import mz.co.moseguros.notification.NotificationApi;
import mz.co.moseguros.organization.OrganizationApi;
import mz.co.moseguros.organization.OrganizationSummary;
import mz.co.moseguros.organization.OrganizationType;
import mz.co.moseguros.shared.audit.ActorType;
import mz.co.moseguros.shared.audit.AuditRecord;
import mz.co.moseguros.shared.audit.AuditService;
import mz.co.moseguros.shared.domain.Emails;
import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.error.ErrorCodes;
import mz.co.moseguros.shared.error.FieldValidationException;
import mz.co.moseguros.shared.security.CurrentUser;
import mz.co.moseguros.shared.tenant.Scope;
import mz.co.moseguros.shared.tenant.TenantContext;
import mz.co.moseguros.shared.tenant.TenantSnapshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Convites de staff (S1-04 §2): convidar para a plataforma ou para uma organização, listar,
 * reenviar e revogar, e os perfis que se podem atribuir.
 *
 * <p>Corre no âmbito de quem convida. Âmbito {@code PLATFORM}: qualquer organização e a
 * plataforma. Âmbito {@code ORG}: só a sua organização (outra → 404 {@code ORGANIZATION_NOT_FOUND}).
 * As verificações do e-mail que têm de ver todas as organizações estão no {@link SystemLookups}.
 *
 * <p>O e-mail é enviado depois de gravar o convite, fora da transacção. O token só existe em
 * claro no link do e-mail: nunca é gravado (só o SHA-256) nem registado.
 */
@Service
public class InvitationService {

    private static final Logger log = LoggerFactory.getLogger(InvitationService.class);

    static final int TOKEN_BYTES = 32;
    static final String EMAIL_TEMPLATE = "invitation.email";
    static final String EMAIL_ORGANIZATION = "invitation.email.organization";
    static final String PURPOSE = "INVITATION";
    static final String SYSTEM_ACTOR = "platform-admin-bootstrap";
    /** O convidado ainda não tem conta (nem língua preferida): o e-mail vai em português. */
    static final String EMAIL_LANGUAGE = "pt";

    private final InvitationRepository invitations;
    private final RoleRepository roles;
    private final SystemLookups lookups;
    private final OrganizationApi organizations;
    private final NotificationApi notifications;
    private final AuditService audit;
    private final CurrentUser currentUser;
    private final InvitationProperties properties;
    private final TransactionTemplate tx;
    private final Clock clock;
    private final SecureRandom random = new SecureRandom();

    public InvitationService(InvitationRepository invitations, RoleRepository roles, SystemLookups lookups,
                             OrganizationApi organizations, NotificationApi notifications, AuditService audit,
                             CurrentUser currentUser, InvitationProperties properties, TransactionTemplate tx,
                             Clock clock) {
        this.invitations = invitations;
        this.roles = roles;
        this.lookups = lookups;
        this.organizations = organizations;
        this.notifications = notifications;
        this.audit = audit;
        this.currentUser = currentUser;
        this.properties = properties;
        this.tx = tx;
        this.clock = clock;
    }

    /**
     * Convida staff da plataforma. Exige o âmbito {@code PLATFORM} (senão 403 {@code FORBIDDEN}).
     *
     * @throws DomainException {@code ROLE_NOT_ALLOWED_FOR_ORGANIZATION} (400), {@code EMAIL_ALREADY_IN_USE} (409)
     */
    @PreAuthorize("hasAuthority('org-user:manage')")
    public InvitationView invitePlatformUser(String fullName, String email, List<String> roleCodes) {
        requirePlatformScope();
        return invite(null, fullName, email, roleCodes, Actor.staff(currentUser.id()));
    }

    /**
     * Convida staff de uma organização.
     *
     * @throws DomainException {@code ORGANIZATION_NOT_FOUND} (404), {@code ORGANIZATION_NOT_ACTIVE} (409),
     *                         {@code ROLE_NOT_ALLOWED_FOR_ORGANIZATION} (400), {@code EMAIL_ALREADY_IN_USE} (409)
     */
    @PreAuthorize("hasAuthority('org-user:manage')")
    public InvitationView inviteOrganizationUser(UUID organizationId, String fullName, String email,
                                                 List<String> roleCodes) {
        OrganizationSummary organization = tx.execute(status -> organizationInScope(organizationId));
        if (!organization.isActive()) {
            throw DomainException.of(ErrorCodes.ORGANIZATION_NOT_ACTIVE, "The organization is suspended");
        }
        return invite(organization, fullName, email, roleCodes, Actor.staff(currentUser.id()));
    }

    /**
     * Convite criado pelo sistema (primeiro administrador da plataforma, {@link PlatformAdminBootstrap}).
     * Sem verificação de permissões: só para quem já corre em {@code SYSTEM}.
     */
    InvitationView inviteBySystem(String fullName, String email, List<String> roleCodes) {
        return invite(null, fullName, email, roleCodes, Actor.system());
    }

    /**
     * Convite da reposição de acesso (S1-05 §3, {@link StaffUserService#resetAccess}): a conta já
     * está {@code PENDING_ACTIVATION} na mesma organização, por isso o convite fica ligado a ela.
     * Sem verificação de permissões: quem chama já as verificou e corre na sua transacção.
     */
    InvitationView inviteForAccessReset(OrganizationSummary organization, String fullName, String email,
                                        List<String> roleCodes) {
        return invite(organization, fullName, email, roleCodes, Actor.staff(currentUser.id()));
    }

    /** Convites de uma organização, do mais recente para o mais antigo por defeito. */
    @PreAuthorize("hasAuthority('org-user:read')")
    @Transactional(readOnly = true)
    public Page<InvitationView> listForOrganization(UUID organizationId, InvitationStatus status, Pageable pageable) {
        organizationInScope(organizationId);
        Instant now = clock.instant();
        return invitations.findAll(filter(organizationId, status, now), pageable).map(i -> InvitationView.of(i, now));
    }

    /** Convites da plataforma ({@code organization_id} vazio). Exige o âmbito {@code PLATFORM}. */
    @PreAuthorize("hasAuthority('org-user:read')")
    @Transactional(readOnly = true)
    public Page<InvitationView> listForPlatform(InvitationStatus status, Pageable pageable) {
        requirePlatformScope();
        Instant now = clock.instant();
        return invitations.findAll(filter(null, status, now), pageable).map(i -> InvitationView.of(i, now));
    }

    /**
     * Gera um token novo (o anterior deixa de funcionar), dá mais 7 dias e envia de novo.
     *
     * @throws DomainException {@code NOT_FOUND} (404) fora do âmbito; {@code INVALID_STATE_TRANSITION}
     *                         (409) se não estiver {@code PENDING} nem {@code EXPIRED}
     */
    @PreAuthorize("hasAuthority('org-user:manage')")
    public InvitationView resend(UUID invitationId) {
        Prepared prepared = tx.execute(status -> {
            Instant now = clock.instant();
            Invitation invitation = findInScope(invitationId);
            requireOpen(invitation, now);
            String token = newToken();
            invitation.renew(TokenService.hash(token), now.plus(properties.validity()));
            invitations.saveAndFlush(invitation);
            OrganizationSummary organization = invitation.getOrganizationId() == null ? null
                    : organizations.findSummary(invitation.getOrganizationId()).orElse(null);
            record(Actor.staff(currentUser.id()), "INVITATION.RESEND", invitation, organization, null,
                    Map.of("expiresAt", invitation.getExpiresAt()));
            return new Prepared(invitation, token, organization, now);
        });
        send(prepared);
        log.info("Invitation {} resent", prepared.invitation().getId());
        return InvitationView.of(prepared.invitation(), prepared.now());
    }

    /**
     * Revoga o convite.
     *
     * @throws DomainException {@code NOT_FOUND} (404) fora do âmbito; {@code INVALID_STATE_TRANSITION}
     *                         (409) se não estiver {@code PENDING} nem {@code EXPIRED}
     */
    @PreAuthorize("hasAuthority('org-user:manage')")
    @Transactional
    public void revoke(UUID invitationId) {
        Instant now = clock.instant();
        Invitation invitation = findInScope(invitationId);
        requireOpen(invitation, now);
        invitation.revoke(now);
        invitations.saveAndFlush(invitation);
        OrganizationSummary organization = invitation.getOrganizationId() == null ? null
                : organizations.findSummary(invitation.getOrganizationId()).orElse(null);
        record(Actor.staff(currentUser.id()), "INVITATION.REVOKE", invitation, organization, null,
                Map.of("revokedAt", now));
        log.info("Invitation {} revoked", invitation.getId());
    }

    /**
     * Perfis que se podem atribuir na organização ({@code organizationId}) ou na plataforma
     * ({@code organizationId} {@code null}), com as mesmas regras de âmbito dos convites.
     */
    @PreAuthorize("hasAuthority('org-user:read')")
    @Transactional(readOnly = true)
    public AssignableRoles assignableRoles(UUID organizationId) {
        String type;
        if (organizationId == null) {
            requirePlatformScope();
            type = null;
        } else {
            type = organizationInScope(organizationId).type().name();
        }
        List<String> codes = roles.findAll().stream()
                .filter(role -> ManagerRoles.assignable(role, type))
                .map(Role::getCode)
                .sorted()
                .toList();
        return new AssignableRoles(codes, ManagerRoles.managerRoles(type), ManagerRoles.managerInviteRoles(type));
    }

    // --- convite ---

    private InvitationView invite(OrganizationSummary organization, String fullName, String rawEmail,
                                  List<String> roleCodes, Actor actor) {
        String email = email(rawEmail);
        List<String> codes = List.copyOf(new LinkedHashSet<>(roleCodes));
        UUID organizationId = organization == null ? null : organization.id();
        Prepared prepared = tx.execute(status -> {
            Instant now = clock.instant();
            checkRoles(codes, organization == null ? null : organization.type());
            UUID existingUserId = null;
            Optional<SystemLookups.StaffAccount> existing = lookups.findStaffByEmail(email);
            if (existing.isPresent()) {
                SystemLookups.StaffAccount account = existing.get();
                if (!account.isPendingActivation() || !Objects.equals(account.organizationId(), organizationId)) {
                    throw DomainException.of(ErrorCodes.EMAIL_ALREADY_IN_USE,
                            "The e-mail address is already used by another staff account");
                }
                existingUserId = account.id();
            }
            int replaced = lookups.revokePendingInvitations(email, now);
            String token = newToken();
            Invitation invitation = Invitation.create(email, fullName, organizationId, codes, TokenService.hash(token),
                    now.plus(properties.validity()), actor.userId());
            invitation.linkUser(existingUserId);
            invitations.saveAndFlush(invitation);
            Map<String, Object> after = new LinkedHashMap<>();
            after.put("email", email);
            after.put("fullName", invitation.getFullName());
            after.put("organizationId", organizationId);
            after.put("roleCodes", codes);
            after.put("expiresAt", invitation.getExpiresAt());
            after.put("existingUserId", existingUserId);
            after.put("replacedInvitations", replaced);
            record(actor, "USER.INVITE", invitation, organization, null, after);
            return new Prepared(invitation, token, organization, now);
        });
        send(prepared);
        log.info("Invitation {} created for organization {} with roles {}", prepared.invitation().getId(),
                organizationId == null ? "PLATFORM" : organizationId, codes);
        return InvitationView.of(prepared.invitation(), prepared.now());
    }

    /** Cada perfil tem de existir e poder ser atribuído no tipo de organização (ou na plataforma). */
    private void checkRoles(List<String> codes, OrganizationType type) {
        String typeName = type == null ? null : type.name();
        List<Role> found = roles.findByCodeIn(codes);
        boolean allAllowed = found.size() == codes.size()
                && found.stream().allMatch(role -> ManagerRoles.assignable(role, typeName));
        if (codes.isEmpty() || !allAllowed) {
            throw DomainException.of(ErrorCodes.ROLE_NOT_ALLOWED_FOR_ORGANIZATION,
                    "A role is unknown or not allowed for " + (typeName == null ? "the platform" : typeName));
        }
    }

    /** Envia o e-mail do convite (fora da transacção); uma falha fica registada e pode reenviar-se. */
    private void send(Prepared prepared) {
        Invitation invitation = prepared.invitation();
        Object membership = prepared.organization() == null ? ""
                : new DefaultMessageSourceResolvable(new String[] {EMAIL_ORGANIZATION},
                        new Object[] {prepared.organization().displayName()});
        DeliveryResult result = notifications.sendEmail(invitation.getEmail(), EMAIL_TEMPLATE,
                new Object[] {invitation.getFullName(), membership, properties.link(prepared.token())},
                EMAIL_LANGUAGE, PURPOSE, invitation.getId());
        if (result.failed()) {
            log.warn("Invitation {}: e-mail not delivered ({})", invitation.getId(), result.failureReason());
        }
    }

    // --- âmbito ---

    /**
     * Organização visível para quem pede: {@code PLATFORM} vê todas; {@code ORG} só a sua (outra
     * → 404); outros âmbitos → 403.
     */
    private OrganizationSummary organizationInScope(UUID organizationId) {
        TenantSnapshot tenant = TenantContext.current();
        if (tenant.scope() == Scope.ORG) {
            if (!organizationId.equals(tenant.orgId())) {
                throw organizationNotFound();
            }
        } else if (tenant.scope() != Scope.PLATFORM && tenant.scope() != Scope.SYSTEM) {
            throw DomainException.of(ErrorCodes.FORBIDDEN, "Access denied");
        }
        return organizations.findSummary(organizationId).orElseThrow(InvitationService::organizationNotFound);
    }

    private static void requirePlatformScope() {
        Scope scope = TenantContext.current().scope();
        if (scope != Scope.PLATFORM && scope != Scope.SYSTEM) {
            throw DomainException.of(ErrorCodes.FORBIDDEN, "Only platform staff can do this");
        }
    }

    /** Convite visível no âmbito (RLS); a plataforma só gere os seus pelo âmbito {@code PLATFORM}. */
    private Invitation findInScope(UUID invitationId) {
        Invitation invitation = invitations.findById(invitationId)
                .orElseThrow(() -> DomainException.of(ErrorCodes.NOT_FOUND, "Invitation not found"));
        TenantSnapshot tenant = TenantContext.current();
        if (tenant.scope() == Scope.ORG && !tenant.orgId().equals(invitation.getOrganizationId())) {
            throw DomainException.of(ErrorCodes.NOT_FOUND, "Invitation not found");
        }
        return invitation;
    }

    private static void requireOpen(Invitation invitation, Instant now) {
        InvitationStatus status = invitation.status(now);
        if (status != InvitationStatus.PENDING && status != InvitationStatus.EXPIRED) {
            throw DomainException.of(ErrorCodes.INVALID_STATE_TRANSITION,
                    "Only pending or expired invitations can be changed (status " + status + ")");
        }
    }

    // --- utilitários ---

    private void record(Actor actor, String action, Invitation invitation, OrganizationSummary organization,
                        Object before, Object after) {
        UUID insurerId = organization != null && organization.type() == OrganizationType.INSURER
                ? organization.id() : null;
        audit.record(new AuditRecord(actor.type(), actor.id(), insurerId, action, "Invitation",
                invitation.getId().toString(), before, after));
    }

    private String newToken() {
        byte[] bytes = new byte[TOKEN_BYTES];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private static String email(String raw) {
        try {
            return Emails.normalize(raw);
        } catch (DomainException e) {
            throw FieldValidationException.of("email", e);
        }
    }

    private static DomainException organizationNotFound() {
        return DomainException.of(ErrorCodes.ORGANIZATION_NOT_FOUND, "Organization not found");
    }

    static Specification<Invitation> filter(UUID organizationId, InvitationStatus status, Instant now) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(organizationId == null ? cb.isNull(root.get("organizationId"))
                    : cb.equal(root.get("organizationId"), organizationId));
            if (status != null) {
                switch (status) {
                    case REVOKED -> predicates.add(cb.isNotNull(root.get("revokedAt")));
                    case ACCEPTED -> {
                        predicates.add(cb.isNull(root.get("revokedAt")));
                        predicates.add(cb.isNotNull(root.get("acceptedAt")));
                    }
                    case EXPIRED -> {
                        predicates.add(cb.isNull(root.get("revokedAt")));
                        predicates.add(cb.isNull(root.get("acceptedAt")));
                        predicates.add(cb.lessThanOrEqualTo(root.get("expiresAt"), now));
                    }
                    case PENDING -> {
                        predicates.add(cb.isNull(root.get("revokedAt")));
                        predicates.add(cb.isNull(root.get("acceptedAt")));
                        predicates.add(cb.greaterThan(root.get("expiresAt"), now));
                    }
                    default -> throw new IllegalArgumentException("Unknown status " + status);
                }
            }
            return cb.and(predicates.toArray(Predicate[]::new));
        };
    }

    // --- tipos ---

    /** Convite como é devolvido (sem o token nem o hash). */
    public record InvitationView(UUID id, String email, String fullName, UUID organizationId, List<String> roleCodes,
                                 InvitationStatus status, Instant expiresAt, Instant createdAt) {

        static InvitationView of(Invitation invitation, Instant now) {
            return new InvitationView(invitation.getId(), invitation.getEmail(), invitation.getFullName(),
                    invitation.getOrganizationId(), invitation.getRoleCodes(), invitation.status(now),
                    invitation.getExpiresAt(), invitation.getCreatedAt());
        }
    }

    /** Perfis atribuíveis, perfis de gestor e perfis do primeiro gestor. */
    public record AssignableRoles(List<String> roles, List<String> managerRoles, List<String> managerInviteRoles) {
    }

    /** Convite gravado e o token em claro (só para o link do e-mail). */
    private record Prepared(Invitation invitation, String token, OrganizationSummary organization, Instant now) {

        @Override
        public String toString() {
            return "Prepared[invitation=" + invitation.getId() + ", token=***]";
        }
    }

    /** Quem faz a acção. */
    private record Actor(ActorType type, String id, UUID userId) {

        static Actor staff(UUID userId) {
            return new Actor(ActorType.STAFF, userId.toString(), userId);
        }

        static Actor system() {
            return new Actor(ActorType.SYSTEM, SYSTEM_ACTOR, null);
        }
    }
}
