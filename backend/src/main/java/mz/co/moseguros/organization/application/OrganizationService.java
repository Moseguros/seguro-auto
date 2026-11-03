package mz.co.moseguros.organization.application;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

import jakarta.persistence.criteria.Predicate;
import mz.co.moseguros.organization.OrganizationReactivated;
import mz.co.moseguros.organization.OrganizationStatus;
import mz.co.moseguros.organization.OrganizationSuspended;
import mz.co.moseguros.organization.OrganizationType;
import mz.co.moseguros.organization.domain.InsurerProfile;
import mz.co.moseguros.organization.domain.InsurerProfileRepository;
import mz.co.moseguros.organization.domain.Organization;
import mz.co.moseguros.organization.domain.OrganizationRepository;
import mz.co.moseguros.shared.audit.ActorType;
import mz.co.moseguros.shared.audit.AuditRecord;
import mz.co.moseguros.shared.audit.AuditService;
import mz.co.moseguros.shared.domain.Emails;
import mz.co.moseguros.shared.domain.Nuit;
import mz.co.moseguros.shared.domain.Province;
import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.error.ErrorCodes;
import mz.co.moseguros.shared.error.FieldErrorCodes;
import mz.co.moseguros.shared.error.FieldValidationException;
import mz.co.moseguros.shared.security.CurrentUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Gestão das organizações pela plataforma (S1-04): criar, listar, consultar, alterar, suspender
 * e reactivar. Corre no âmbito de quem pede (só a plataforma tem as permissões
 * {@code organization:*}; o RLS deixa-a ver todas as organizações).
 *
 * <p>Não cria utilizadores: o primeiro gestor é convidado a seguir pelo módulo {@code identity}
 * ({@code POST /organizations/{id}/users}).
 */
@Service
public class OrganizationService {

    private static final Logger log = LoggerFactory.getLogger(OrganizationService.class);

    private final OrganizationRepository organizations;
    private final InsurerProfileRepository insurerProfiles;
    private final AuditService audit;
    private final CurrentUser currentUser;
    private final ApplicationEventPublisher events;

    public OrganizationService(OrganizationRepository organizations, InsurerProfileRepository insurerProfiles,
                               AuditService audit, CurrentUser currentUser, ApplicationEventPublisher events) {
        this.organizations = organizations;
        this.insurerProfiles = insurerProfiles;
        this.audit = audit;
        this.currentUser = currentUser;
        this.events = events;
    }

    /**
     * Cria uma organização {@code ACTIVE} e, se for uma seguradora, o seu perfil.
     *
     * @throws DomainException {@code NUIT_ALREADY_IN_USE} e {@code SHORT_CODE_ALREADY_IN_USE} (409),
     *                         {@code LICENSE_NUMBER_REQUIRED} e {@code OMBUDSMAN_INVALID} (400) ou
     *                         {@code VALIDATION_ERROR} com o campo
     */
    @PreAuthorize("hasAuthority('organization:manage')")
    @Transactional
    public Details create(OrganizationType type, Data data) {
        checkInsurerProfilePresence(type, data.insurerProfile());
        String contactEmail = email("contactEmail", data.contactEmail());
        if (organizations.existsByNuit(data.nuit())) {
            throw DomainException.of(ErrorCodes.NUIT_ALREADY_IN_USE, "NUIT already used by another organization");
        }
        Organization organization = organizations.save(Organization.create(type, data.legalName(), data.tradeName(),
                data.nuit(), data.licenseNumber(), contactEmail, data.contactPhone(), data.address(),
                data.province()));
        InsurerProfile profile = null;
        if (type == OrganizationType.INSURER) {
            InsurerData insurer = data.insurerProfile();
            if (insurerProfiles.existsByShortCode(insurer.shortCode())) {
                throw shortCodeInUse();
            }
            profile = insurerProfiles.save(InsurerProfile.create(organization, insurer.shortCode(),
                    email("insurerProfile.complaintsEmail", insurer.complaintsEmail()),
                    ombudsman(insurer.ombudsmanOrganizationId())));
        }
        organizations.flush();
        Details created = new Details(organization, profile);
        record("ORGANIZATION.CREATE", organization, null, Snapshot.of(created));
        log.info("Organization {} ({}) created", organization.getId(), type);
        return created;
    }

    /** Página de organizações; {@code q} procura no nome (sem diferenciar maiúsculas) ou no NUIT exacto. */
    @PreAuthorize("hasAuthority('organization:read')")
    @Transactional(readOnly = true)
    public Page<Organization> list(OrganizationType type, OrganizationStatus status, String q, Pageable pageable) {
        return organizations.findAll(filter(type, status, q), pageable);
    }

    /** @throws DomainException {@code ORGANIZATION_NOT_FOUND} (404) */
    @PreAuthorize("hasAuthority('organization:read')")
    @Transactional(readOnly = true)
    public Details get(UUID id) {
        return details(find(id));
    }

    /**
     * Altera os dados (o tipo não muda).
     *
     * @param version a versão lida antes da alteração; diferente → {@code CONCURRENT_MODIFICATION} (409)
     */
    @PreAuthorize("hasAuthority('organization:manage')")
    @Transactional
    public Details update(UUID id, Data data, long version) {
        Organization organization = find(id);
        if (organization.getVersion() != version) {
            throw DomainException.of(ErrorCodes.CONCURRENT_MODIFICATION,
                    "The organization was changed by another request");
        }
        checkInsurerProfilePresence(organization.getType(), data.insurerProfile());
        Details current = details(organization);
        Snapshot before = Snapshot.of(current);
        String contactEmail = email("contactEmail", data.contactEmail());
        if (organizations.existsByNuitAndIdNot(data.nuit(), id)) {
            throw DomainException.of(ErrorCodes.NUIT_ALREADY_IN_USE, "NUIT already used by another organization");
        }
        organization.update(data.legalName(), data.tradeName(), data.nuit(), data.licenseNumber(), contactEmail,
                data.contactPhone(), data.address(), data.province());
        InsurerProfile profile = current.insurerProfile();
        if (organization.getType() == OrganizationType.INSURER) {
            InsurerData insurer = data.insurerProfile();
            if (insurerProfiles.existsByShortCodeAndOrganizationIdNot(insurer.shortCode(), id)) {
                throw shortCodeInUse();
            }
            String complaintsEmail = email("insurerProfile.complaintsEmail", insurer.complaintsEmail());
            Organization ombudsman = ombudsman(insurer.ombudsmanOrganizationId());
            if (profile == null) {
                profile = insurerProfiles.save(InsurerProfile.create(organization, insurer.shortCode(),
                        complaintsEmail, ombudsman));
            } else {
                profile.update(insurer.shortCode(), complaintsEmail, ombudsman);
            }
        }
        organizations.flush();
        Details updated = new Details(organization, profile);
        record("ORGANIZATION.UPDATE", organization, before, Snapshot.of(updated));
        return updated;
    }

    /**
     * {@code ACTIVE → SUSPENDED} e publica {@link OrganizationSuspended} (o módulo {@code identity}
     * revoga as sessões dos utilizadores da organização).
     *
     * @throws DomainException {@code INVALID_STATE_TRANSITION} (409) se já estiver suspensa
     */
    @PreAuthorize("hasAuthority('organization:manage')")
    @Transactional
    public Details suspend(UUID id, String reason) {
        Organization organization = find(id);
        Snapshot before = Snapshot.of(details(organization));
        organization.suspend();
        organizations.flush();
        Details after = details(organization);
        record("ORGANIZATION.SUSPEND", organization, before, new SuspendSnapshot(Snapshot.of(after), reason));
        events.publishEvent(new OrganizationSuspended(organization.getId(), reason));
        log.info("Organization {} suspended", organization.getId());
        return after;
    }

    /**
     * {@code SUSPENDED → ACTIVE} e publica {@link OrganizationReactivated}.
     *
     * @throws DomainException {@code INVALID_STATE_TRANSITION} (409) se já estiver activa
     */
    @PreAuthorize("hasAuthority('organization:manage')")
    @Transactional
    public Details reactivate(UUID id) {
        Organization organization = find(id);
        Snapshot before = Snapshot.of(details(organization));
        organization.reactivate();
        organizations.flush();
        Details after = details(organization);
        record("ORGANIZATION.REACTIVATE", organization, before, Snapshot.of(after));
        events.publishEvent(new OrganizationReactivated(organization.getId()));
        log.info("Organization {} reactivated", organization.getId());
        return after;
    }

    // --- regras ---

    /** {@code insurerProfile} é obrigatório nas seguradoras e proibido nos outros tipos. */
    private static void checkInsurerProfilePresence(OrganizationType type, InsurerData insurer) {
        if (type == OrganizationType.INSURER && insurer == null) {
            throw new FieldValidationException("insurerProfile", FieldErrorCodes.REQUIRED,
                    "insurerProfile is required for insurers");
        }
        if (type != OrganizationType.INSURER && insurer != null) {
            throw new FieldValidationException("insurerProfile", FieldErrorCodes.NOT_ALLOWED,
                    "insurerProfile is only allowed for insurers");
        }
    }

    /** Provedor do cliente: tem de existir e ser do tipo {@code OMBUDSMAN} ({@code OMBUDSMAN_INVALID}). */
    private Organization ombudsman(UUID id) {
        if (id == null) {
            return null;
        }
        return organizations.findById(id)
                .filter(org -> org.getType() == OrganizationType.OMBUDSMAN)
                .orElseThrow(() -> DomainException.of(ErrorCodes.OMBUDSMAN_INVALID,
                        "The ombudsman must be an existing OMBUDSMAN organization"));
    }

    private Organization find(UUID id) {
        return organizations.findById(id).orElseThrow(() -> DomainException.of(ErrorCodes.ORGANIZATION_NOT_FOUND,
                "Organization not found"));
    }

    private Details details(Organization organization) {
        InsurerProfile profile = organization.getType() == OrganizationType.INSURER
                ? insurerProfiles.findByOrganizationId(organization.getId()).orElse(null)
                : null;
        return new Details(organization, profile);
    }

    private void record(String action, Organization organization, Object before, Object after) {
        UUID insurerId = organization.getType() == OrganizationType.INSURER ? organization.getId() : null;
        audit.record(new AuditRecord(ActorType.STAFF, currentUser.id().toString(), insurerId, action, "Organization",
                organization.getId().toString(), before, after));
    }

    private static String email(String field, String raw) {
        try {
            return Emails.normalize(raw);
        } catch (DomainException e) {
            throw FieldValidationException.of(field, e);
        }
    }

    private static DomainException shortCodeInUse() {
        return DomainException.of(ErrorCodes.SHORT_CODE_ALREADY_IN_USE, "Short code already used by another insurer");
    }

    static Specification<Organization> filter(OrganizationType type, OrganizationStatus status, String q) {
        String text = q == null || q.isBlank() ? null : q.trim();
        Optional<Nuit> nuit = Optional.empty();
        if (text != null) {
            try {
                nuit = Optional.of(Nuit.of(text));
            } catch (DomainException e) {
                nuit = Optional.empty();
            }
        }
        Optional<Nuit> exactNuit = nuit;
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (type != null) {
                predicates.add(cb.equal(root.get("type"), type));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            if (text != null) {
                String pattern = "%" + text.toLowerCase(Locale.ROOT)
                        .replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_") + "%";
                List<Predicate> any = new ArrayList<>();
                any.add(cb.like(cb.lower(root.get("legalName")), pattern, '\\'));
                any.add(cb.like(cb.lower(root.get("tradeName")), pattern, '\\'));
                exactNuit.ifPresent(n -> any.add(cb.equal(root.get("nuit"), n)));
                predicates.add(cb.or(any.toArray(Predicate[]::new)));
            }
            return cb.and(predicates.toArray(Predicate[]::new));
        };
    }

    // --- tipos ---

    /** Dados comuns ao pedido de criação e de alteração. */
    public record Data(String legalName, String tradeName, Nuit nuit, String licenseNumber, String contactEmail,
                       String contactPhone, String address, Province province, InsurerData insurerProfile) {
    }

    /** Dados do perfil de seguradora. */
    public record InsurerData(String shortCode, String complaintsEmail, UUID ombudsmanOrganizationId) {
    }

    /** Organização e, se for uma seguradora, o seu perfil. */
    public record Details(Organization organization, InsurerProfile insurerProfile) {
    }

    /** Estado auditado (antes e depois). */
    public record Snapshot(String type, String legalName, String tradeName, String nuit, String licenseNumber,
                    String status, String contactEmail, String contactPhone, String address, String province,
                    String shortCode, String complaintsEmail, UUID ombudsmanOrganizationId, long version,
                    Instant updatedAt) {

        static Snapshot of(Details d) {
            Organization o = d.organization();
            InsurerProfile p = d.insurerProfile();
            return new Snapshot(o.getType().name(), o.getLegalName(), o.getTradeName(), o.getNuit().value(),
                    o.getLicenseNumber(), o.getStatus().name(), o.getContactEmail(), o.getContactPhone(),
                    o.getAddress(), o.getProvince() == null ? null : o.getProvince().name(),
                    p == null ? null : p.getShortCode(), p == null ? null : p.getComplaintsEmail(),
                    p == null ? null : p.getOmbudsmanOrgId(), o.getVersion(), o.getUpdatedAt());
        }
    }

    /** Estado depois da suspensão, com o motivo. */
    public record SuspendSnapshot(Snapshot organization, String reason) {
    }
}
