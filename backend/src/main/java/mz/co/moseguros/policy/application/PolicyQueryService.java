package mz.co.moseguros.policy.application;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.criteria.Predicate;
import mz.co.moseguros.organization.InsurerProfileView;
import mz.co.moseguros.organization.OrganizationApi;
import mz.co.moseguros.policy.CertificateStatus;
import mz.co.moseguros.policy.PolicyCoverageView;
import mz.co.moseguros.policy.PolicyStatus;
import mz.co.moseguros.policy.PolicySummaryView;
import mz.co.moseguros.policy.PolicyView;
import mz.co.moseguros.policy.domain.Certificate;
import mz.co.moseguros.policy.domain.CertificateRepository;
import mz.co.moseguros.policy.domain.Instalment;
import mz.co.moseguros.policy.domain.InstalmentRepository;
import mz.co.moseguros.policy.domain.Policy;
import mz.co.moseguros.policy.domain.PolicyEvent;
import mz.co.moseguros.policy.domain.PolicyEventRepository;
import mz.co.moseguros.policy.domain.PolicyRepository;
import mz.co.moseguros.proposal.ProposalApi;
import mz.co.moseguros.proposal.ProposalView;
import mz.co.moseguros.shared.domain.Money;
import mz.co.moseguros.shared.domain.PlateParser;
import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.error.ErrorCodes;
import mz.co.moseguros.shared.security.CurrentUser;
import mz.co.moseguros.shared.tenant.TenantContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Leituras das apólices (S6-01 §9 e §10), no âmbito de quem chama (RLS): do cliente (as suas), da
 * seguradora (as suas; o serviço filtra também pelo {@code TenantContext.insurerId()}, ADR-002) e as do
 * {@code PolicyApi} ({@code view}, {@code customerPolicies}).
 */
@Service
public class PolicyQueryService {

    private static final Pattern POLICY_NUMBER = Pattern.compile("^AP-\\d{4}-\\d{6,}$");

    private final PolicyRepository policies;
    private final InstalmentRepository instalments;
    private final CertificateRepository certificates;
    private final PolicyEventRepository policyEvents;
    private final OrganizationApi organizations;
    private final ProposalApi proposals;
    private final VerificationTokens tokens;
    private final PlateParser plates;
    private final CurrentUser currentUser;
    private final ObjectMapper objectMapper;
    private final Clock clock;

    public PolicyQueryService(PolicyRepository policies, InstalmentRepository instalments,
                              CertificateRepository certificates, PolicyEventRepository policyEvents,
                              OrganizationApi organizations, ProposalApi proposals, VerificationTokens tokens,
                              PlateParser plates, CurrentUser currentUser, ObjectMapper objectMapper, Clock clock) {
        this.policies = policies;
        this.instalments = instalments;
        this.certificates = certificates;
        this.policyEvents = policyEvents;
        this.organizations = organizations;
        this.proposals = proposals;
        this.tokens = tokens;
        this.plates = plates;
        this.currentUser = currentUser;
        this.objectMapper = objectMapper;
        this.clock = clock;
    }

    // --- Modelos de leitura ---

    /** Linha da lista do cliente. */
    public record CustomerSummary(Policy policy, String insurerName, boolean certificateReady, Instant now) {
    }

    /** Linha da lista da seguradora. */
    public record InsurerSummary(Policy policy, Instant now) {
    }

    /** Proposta de origem (só no detalhe da seguradora). */
    public record ProposalRef(UUID id, String number) {
    }

    /**
     * Detalhe de uma apólice.
     *
     * @param verificationUrl {@code null} sem certificado válido
     * @param events          só no detalhe da seguradora ({@code null} no do cliente)
     * @param proposal        só no detalhe da seguradora
     */
    public record Details(Policy policy, String insurerName, List<PolicyCoverageView> coverages,
                          List<Instalment> instalments, Certificate certificate, String verificationUrl,
                          List<PolicyEvent> events, ProposalRef proposal, Instant now) {
    }

    /**
     * Filtros da lista da seguradora.
     *
     * @param issuedFrom primeiro dia (Maputo) de {@code issued_at}, inclusivo
     * @param issuedTo   último dia (Maputo) de {@code issued_at}, inclusivo
     * @param q          número {@code AP-…} exacto; ou matrícula; senão parte do nome do tomador
     */
    public record Filter(Set<PolicyStatus> statuses, String productType, LocalDate issuedFrom, LocalDate issuedTo,
                         String q) {
    }

    // --- Cliente ---

    /** {@code listMyPolicies}: as apólices do próprio cliente; {@code statuses} vazio = todas. */
    @PreAuthorize("hasAuthority('self:policies')")
    @Transactional(readOnly = true)
    public Page<CustomerSummary> mine(Set<PolicyStatus> statuses, Pageable pageable) {
        UUID userId = currentUser.id();
        Page<Policy> page = statuses == null || statuses.isEmpty()
                ? policies.findByPolicyholderUserId(userId, pageable)
                : policies.findByPolicyholderUserIdAndStatusIn(userId, statuses, pageable);
        Map<UUID, String> names = insurerNames(page.getContent().stream().map(Policy::getInsurerId).toList());
        Set<UUID> ready = certificates.findByPolicyIdInAndStatus(page.getContent().stream().map(Policy::getId)
                        .toList(), CertificateStatus.VALID).stream()
                .filter(c -> c.getPdfFileId() != null)
                .map(Certificate::getPolicyId)
                .collect(Collectors.toSet());
        Instant now = clock.instant();
        return page.map(p -> new CustomerSummary(p, names.get(p.getInsurerId()), ready.contains(p.getId()), now));
    }

    /** {@code getMyPolicy}: só a do próprio cliente (outra → 404 {@code POLICY_NOT_FOUND}). */
    @PreAuthorize("hasAuthority('self:policies')")
    @Transactional(readOnly = true)
    public Details myPolicy(UUID policyId) {
        Policy policy = policies.findById(policyId)
                .filter(p -> p.getPolicyholderUserId().equals(currentUser.id()))
                .orElseThrow(PolicyQueryService::notFound);
        return details(policy, false);
    }

    // --- Seguradora ---

    @PreAuthorize("hasAuthority('policy:read')")
    @Transactional(readOnly = true)
    public Page<InsurerSummary> insurerList(Filter filter, Pageable pageable) {
        UUID insurerId = TenantContext.insurerId();
        if (insurerId == null) {
            return Page.empty(pageable);
        }
        Instant now = clock.instant();
        return policies.findAll(specification(insurerId, filter), pageable).map(p -> new InsurerSummary(p, now));
    }

    /** {@code getInsurerPolicy}: só as da própria seguradora (outra → 404 {@code POLICY_NOT_FOUND}). */
    @PreAuthorize("hasAuthority('policy:read')")
    @Transactional(readOnly = true)
    public Details insurerPolicy(UUID policyId) {
        UUID insurerId = TenantContext.insurerId();
        Policy policy = policies.findById(policyId)
                .filter(p -> insurerId != null && p.getInsurerId().equals(insurerId))
                .orElseThrow(PolicyQueryService::notFound);
        return details(policy, true);
    }

    // --- PolicyApi ---

    /** No âmbito de quem chama. @throws DomainException 404 {@code POLICY_NOT_FOUND} */
    @Transactional(readOnly = true)
    public PolicyView view(UUID policyId) {
        Policy p = policyId == null ? null : policies.findById(policyId).orElse(null);
        if (p == null) {
            throw notFound();
        }
        return toView(p, insurerNames(List.of(p.getInsurerId())).get(p.getInsurerId()));
    }

    /** As apólices do cliente visíveis no âmbito de quem chama, as mais recentes primeiro. */
    @Transactional(readOnly = true)
    public List<PolicySummaryView> customerPolicies(UUID userId) {
        List<Policy> list = policies.findByPolicyholderUserIdOrderByIssuedAtDesc(userId);
        Map<UUID, String> names = insurerNames(list.stream().map(Policy::getInsurerId).toList());
        Instant now = clock.instant();
        return list.stream().map(p -> new PolicySummaryView(p.getId(), p.getNumber(), p.getStatus(), p.coverAt(now),
                p.getInsurerId(), names.get(p.getInsurerId()), p.getProductName(), p.getCoverageType(),
                p.getVehicleId(), p.getPlate(), p.getStartAt(), p.getEndAt())).toList();
    }

    // --- Apoio ---

    /** As coberturas gravadas na apólice. */
    public List<PolicyCoverageView> coverages(Policy p) {
        return List.of(objectMapper.convertValue(p.getCoverages(), PolicyCoverageView[].class));
    }

    private Details details(Policy policy, boolean insurerView) {
        Certificate certificate = certificates.findByPolicyIdAndStatus(policy.getId(), CertificateStatus.VALID)
                .orElse(null);
        String url = certificate == null ? null : tokens.verificationUrl(certificate.getVerificationToken());
        List<PolicyEvent> events = insurerView ? policyEvents.findByPolicyIdOrderByOccurredAtAscIdAsc(policy.getId())
                : null;
        ProposalRef proposal = insurerView ? proposals.view(policy.getProposalId())
                .map(ProposalView::number)
                .map(n -> new ProposalRef(policy.getProposalId(), n))
                .orElse(new ProposalRef(policy.getProposalId(), null)) : null;
        return new Details(policy, insurerNames(List.of(policy.getInsurerId())).get(policy.getInsurerId()),
                coverages(policy), instalments.findByPolicyIdOrderBySeqAsc(policy.getId()), certificate, url,
                events, proposal, clock.instant());
    }

    private PolicyView toView(Policy p, String insurerName) {
        return new PolicyView(p.getId(), p.getNumber(), p.getStatus(), p.coverAt(clock.instant()), p.getInsurerId(),
                insurerName, p.getProposalId(), p.getPolicyholderUserId(), p.getPolicyholderName(), p.getChannel(),
                p.getBrokerOrgId(), p.getProductVersionId(), p.getProductName(), p.getProductType(),
                p.getCoverageType(), p.getVehicleId(), p.getPlate(), p.getVehicleSnapshot(), p.getDriverSnapshot(),
                coverages(p), p.getStartAt(), p.getEndAt(), p.getRenewalType(), p.getPaymentPlan(),
                Money.ofCentavos(p.getTotalAmount()), p.getIssuedAt());
    }

    private Map<UUID, String> insurerNames(Collection<UUID> ids) {
        if (ids.isEmpty()) {
            return Map.of();
        }
        return organizations.insurerProfiles(Set.copyOf(ids)).values().stream()
                .collect(Collectors.toMap(InsurerProfileView::insurerId, InsurerProfileView::displayName,
                        (a, b) -> a));
    }

    private Specification<Policy> specification(UUID insurerId, Filter filter) {
        return (root, query, cb) -> {
            List<Predicate> where = new ArrayList<>();
            where.add(cb.equal(root.get("insurerId"), insurerId));
            if (filter.statuses() != null && !filter.statuses().isEmpty()) {
                where.add(root.get("status").in(filter.statuses()));
            }
            if (filter.productType() != null && !filter.productType().isBlank()) {
                where.add(cb.equal(root.get("productType"), filter.productType().trim().toUpperCase(Locale.ROOT)));
            }
            if (filter.issuedFrom() != null) {
                where.add(cb.greaterThanOrEqualTo(root.get("issuedAt"), startOfDay(filter.issuedFrom())));
            }
            if (filter.issuedTo() != null) {
                where.add(cb.lessThan(root.get("issuedAt"), startOfDay(filter.issuedTo().plusDays(1))));
            }
            String q = filter.q() == null ? null : filter.q().trim();
            if (q != null && !q.isEmpty()) {
                where.add(search(q, root, cb));
            }
            return cb.and(where.toArray(Predicate[]::new));
        };
    }

    private Predicate search(String q, jakarta.persistence.criteria.Root<Policy> root,
                             jakarta.persistence.criteria.CriteriaBuilder cb) {
        String upper = q.toUpperCase(Locale.ROOT);
        if (POLICY_NUMBER.matcher(upper).matches()) {
            return cb.equal(root.get("number"), upper);
        }
        String plate = parsePlate(q);
        if (plate != null) {
            return cb.equal(root.get("plate"), plate);
        }
        String pattern = "%" + q.toLowerCase(Locale.ROOT).replace("\\", "\\\\").replace("%", "\\%")
                .replace("_", "\\_") + "%";
        return cb.like(cb.lower(root.get("policyholderName")), pattern, '\\');
    }

    private String parsePlate(String q) {
        try {
            return plates.parse(q).value();
        } catch (DomainException e) {
            return null;
        }
    }

    private static Instant startOfDay(LocalDate date) {
        return date.atStartOfDay(Policy.ZONE).toInstant();
    }

    static DomainException notFound() {
        return DomainException.of(ErrorCodes.POLICY_NOT_FOUND, "Policy not found");
    }
}
