package mz.co.moseguros.catalog.application;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import mz.co.moseguros.catalog.domain.ReferenceDomain;
import mz.co.moseguros.catalog.domain.ReferenceValue;
import mz.co.moseguros.catalog.domain.ReferenceValueRepository;
import mz.co.moseguros.shared.audit.ActorType;
import mz.co.moseguros.shared.audit.AuditRecord;
import mz.co.moseguros.shared.audit.AuditService;
import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.error.ErrorCodes;
import mz.co.moseguros.shared.error.FieldErrorCodes;
import mz.co.moseguros.shared.error.FieldValidationException;
import mz.co.moseguros.shared.reference.ReferenceValueLookup;
import mz.co.moseguros.shared.security.CurrentUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Tabelas de referência (S2-02 §5 e §6): marcas, modelos e categorias de carta. A lista pública só
 * tem valores activos (e, nos modelos, só os de marcas activas); a gestão exige
 * {@code reference:manage}. Nenhum valor se apaga: desactivar não altera os registos que já o usam.
 *
 * <p>Implementa {@link ReferenceValueLookup} para outros módulos validarem códigos sem dependerem
 * do {@code catalog}.
 */
@Service
public class ReferenceValueService implements ReferenceValueLookup {

    static final Sort PUBLIC_ORDER = Sort.by("sortOrder", "labelPt", "code");

    private final ReferenceValueRepository values;
    private final AuditService audit;
    private final CurrentUser currentUser;

    public ReferenceValueService(ReferenceValueRepository values, AuditService audit, CurrentUser currentUser) {
        this.values = values;
        this.audit = audit;
        this.currentUser = currentUser;
    }

    /**
     * Valores activos do domínio, por {@code sortOrder} e {@code labelPt} (público). Em
     * {@code VEHICLE_MODEL} exclui os modelos de marcas inactivas.
     *
     * @param parentCode opcional: só os valores com este pai
     * @throws DomainException {@code REFERENCE_DOMAIN_UNKNOWN} (404)
     */
    @Transactional(readOnly = true)
    public List<ReferenceValue> listPublic(String domainName, String parentCode) {
        ReferenceDomain domain = ReferenceDomain.parse(domainName);
        return values.findAll(publicFilter(domain, blankToNull(parentCode)), PUBLIC_ORDER);
    }

    /**
     * Página de valores do domínio para o administrador.
     *
     * @param q procura no código ou nos rótulos, sem diferenciar maiúsculas
     */
    @PreAuthorize("hasAuthority('reference:manage')")
    @Transactional(readOnly = true)
    public Page<ReferenceValue> list(String domainName, Boolean active, String parentCode, String q,
                                     Pageable pageable) {
        ReferenceDomain domain = ReferenceDomain.parse(domainName);
        return values.findAll(adminFilter(domain, active, blankToNull(parentCode), blankToNull(q)), pageable);
    }

    /**
     * Cria um valor activo.
     *
     * @throws DomainException {@code VALIDATION_ERROR} ({@code INVALID_CODE}, {@code REQUIRED},
     *                         {@code NOT_ALLOWED}, {@code PARENT_NOT_FOUND}, {@code OUT_OF_RANGE}) ou
     *                         {@code REFERENCE_CODE_ALREADY_EXISTS} (409)
     */
    @PreAuthorize("hasAuthority('reference:manage')")
    @Transactional
    public ReferenceValue create(String domainName, Data data) {
        ReferenceDomain domain = ReferenceDomain.parse(domainName);
        ReferenceValue value = ReferenceValue.create(domain, data.code(), data.labelPt(), data.labelEn(),
                data.parentCode(), data.sortOrder());
        if (domain.hasParent() && !values.existsByDomainAndCode(ReferenceDomain.VEHICLE_MAKE, value.getParentCode())) {
            throw new FieldValidationException("parentCode", FieldErrorCodes.PARENT_NOT_FOUND,
                    "parentCode is not an existing make");
        }
        if (values.existsByDomainAndCode(domain, value.getCode())) {
            throw DomainException.of(ErrorCodes.REFERENCE_CODE_ALREADY_EXISTS,
                    "Code already exists in the reference domain");
        }
        values.save(value);
        values.flush();
        record("REFERENCE_VALUE.CREATE", value, null);
        return value;
    }

    /**
     * Altera rótulos e ordem (o código e a marca não mudam).
     *
     * @param version versão lida; diferente → {@code CONCURRENT_MODIFICATION} (409)
     */
    @PreAuthorize("hasAuthority('reference:manage')")
    @Transactional
    public ReferenceValue update(String domainName, UUID id, Data data, long version) {
        ReferenceValue value = find(domainName, id);
        if (value.getVersion() != version) {
            throw DomainException.of(ErrorCodes.CONCURRENT_MODIFICATION,
                    "The reference value was changed by another request");
        }
        Snapshot before = Snapshot.of(value);
        value.update(data.labelPt(), data.labelEn(), data.sortOrder());
        values.flush();
        record("REFERENCE_VALUE.UPDATE", value, before);
        return value;
    }

    /** @throws DomainException {@code INVALID_STATE_TRANSITION} (409) se já estiver activo */
    @PreAuthorize("hasAuthority('reference:manage')")
    @Transactional
    public ReferenceValue activate(String domainName, UUID id) {
        ReferenceValue value = find(domainName, id);
        Snapshot before = Snapshot.of(value);
        value.activate();
        values.flush();
        record("REFERENCE_VALUE.ACTIVATE", value, before);
        return value;
    }

    /** @throws DomainException {@code INVALID_STATE_TRANSITION} (409) se já estiver inactivo */
    @PreAuthorize("hasAuthority('reference:manage')")
    @Transactional
    public ReferenceValue deactivate(String domainName, UUID id) {
        ReferenceValue value = find(domainName, id);
        Snapshot before = Snapshot.of(value);
        value.deactivate();
        values.flush();
        record("REFERENCE_VALUE.DEACTIVATE", value, before);
        return value;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isActive(String domain, String code) {
        return code != null && ReferenceDomain.find(domain)
                .map(d -> values.existsByDomainAndCodeAndActiveTrue(d, code))
                .orElse(false);
    }

    private ReferenceValue find(String domainName, UUID id) {
        ReferenceDomain domain = ReferenceDomain.parse(domainName);
        return values.findByIdAndDomain(id, domain).orElseThrow(() -> DomainException.of(
                ErrorCodes.REFERENCE_VALUE_NOT_FOUND, "Reference value not found"));
    }

    private void record(String action, ReferenceValue value, Snapshot before) {
        audit.record(new AuditRecord(ActorType.STAFF, currentUser.id().toString(), null, action, "ReferenceValue",
                value.getId().toString(), before, Snapshot.of(value)));
    }

    static Specification<ReferenceValue> publicFilter(ReferenceDomain domain, String parentCode) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("domain"), domain));
            predicates.add(cb.isTrue(root.get("active")));
            if (parentCode != null) {
                predicates.add(cb.equal(root.get("parentCode"), parentCode));
            }
            if (domain.hasParent()) {
                Subquery<Integer> activeParent = query.subquery(Integer.class);
                Root<ReferenceValue> parent = activeParent.from(ReferenceValue.class);
                activeParent.select(cb.literal(1)).where(
                        cb.equal(parent.get("domain"), ReferenceDomain.VEHICLE_MAKE),
                        cb.equal(parent.get("code"), root.get("parentCode")),
                        cb.isTrue(parent.get("active")));
                predicates.add(cb.exists(activeParent));
            }
            return cb.and(predicates.toArray(Predicate[]::new));
        };
    }

    static Specification<ReferenceValue> adminFilter(ReferenceDomain domain, Boolean active, String parentCode,
                                                     String q) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("domain"), domain));
            if (active != null) {
                predicates.add(cb.equal(root.get("active"), active));
            }
            if (parentCode != null) {
                predicates.add(cb.equal(root.get("parentCode"), parentCode));
            }
            if (q != null) {
                String pattern = "%" + q.trim().toLowerCase(Locale.ROOT)
                        .replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_") + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("code")), pattern, '\\'),
                        cb.like(cb.lower(root.get("labelPt")), pattern, '\\'),
                        cb.like(cb.lower(root.get("labelEn")), pattern, '\\')));
            }
            return cb.and(predicates.toArray(Predicate[]::new));
        };
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    /** Campos de criação e alteração ({@code code} e {@code parentCode} só na criação). */
    public record Data(String code, String labelPt, String labelEn, String parentCode, Integer sortOrder) {
    }

    /** Estado auditado (antes e depois). */
    public record Snapshot(String domain, String code, String labelPt, String labelEn, String parentCode,
                           int sortOrder, boolean active, long version, Instant updatedAt) {

        static Snapshot of(ReferenceValue v) {
            return new Snapshot(v.getDomain().name(), v.getCode(), v.getLabelPt(), v.getLabelEn(), v.getParentCode(),
                    v.getSortOrder(), v.isActive(), v.getVersion(), v.getUpdatedAt());
        }
    }
}
