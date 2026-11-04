package mz.co.moseguros.proposal.application;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import mz.co.moseguros.catalog.CatalogApi;
import mz.co.moseguros.identity.IdentityApi;
import mz.co.moseguros.proposal.ProposalStatus;
import mz.co.moseguros.proposal.ProposalView;
import mz.co.moseguros.proposal.domain.Declaration;
import mz.co.moseguros.proposal.domain.DeclarationRepository;
import mz.co.moseguros.proposal.domain.PolicyholderSnapshot;
import mz.co.moseguros.proposal.domain.Proposal;
import mz.co.moseguros.proposal.domain.ProposalRepository;
import mz.co.moseguros.proposal.domain.Signature;
import mz.co.moseguros.proposal.domain.SuitabilityAnswer;
import mz.co.moseguros.proposal.domain.SuitabilityAnswerRepository;
import mz.co.moseguros.shared.domain.Money;
import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.error.ErrorCodes;
import mz.co.moseguros.shared.security.CurrentUser;
import mz.co.moseguros.shared.tenant.Scope;
import mz.co.moseguros.shared.tenant.TenantContext;
import mz.co.moseguros.shared.tenant.TenantSnapshot;
import org.hibernate.Hibernate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Leituras das propostas (S4-02 §9 e §10) e a verificação de acesso usada pelas escritas. Correm no
 * âmbito de quem pede (RLS); o serviço confirma também que a proposta é do cliente actual ou do
 * mediador actual, e a seguradora só vê propostas já submetidas (um rascunho nunca chegou a ela).
 * Qualquer outra → 404 {@code PROPOSAL_NOT_FOUND}.
 */
@Service
public class ProposalQueryService {

    private final ProposalRepository proposals;
    private final DeclarationRepository declarations;
    private final SuitabilityAnswerRepository suitabilityAnswers;
    private final IdentityApi identity;
    private final ProposalJson json;
    private final CatalogApi catalog;
    private final CurrentUser currentUser;

    public ProposalQueryService(ProposalRepository proposals, DeclarationRepository declarations,
                                SuitabilityAnswerRepository suitabilityAnswers, IdentityApi identity,
                                ProposalJson json, CatalogApi catalog, CurrentUser currentUser) {
        this.proposals = proposals;
        this.declarations = declarations;
        this.suitabilityAnswers = suitabilityAnswers;
        this.identity = identity;
        this.json = json;
        this.catalog = catalog;
        this.currentUser = currentUser;
    }

    /** Proposta do cliente actual ou do mediador actual ({@code getProposal}). */
    @PreAuthorize("hasAnyAuthority('self:proposals','proposal:create-for-client')")
    @Transactional(readOnly = true)
    public ProposalDetails get(UUID proposalId) {
        return details(accessible(proposalId));
    }

    /** Propostas do cliente actual, com filtro opcional por estado ({@code listMyProposals}). */
    @PreAuthorize("hasAuthority('self:proposals')")
    @Transactional(readOnly = true)
    public Page<Proposal> listMine(Collection<ProposalStatus> statuses, Pageable pageable) {
        UUID customerId = currentUser.id();
        if (statuses == null || statuses.isEmpty()) {
            return proposals.findByCustomerUserId(customerId, pageable);
        }
        return proposals.findByCustomerUserIdAndStatusIn(customerId, statuses, pageable);
    }

    /**
     * Proposta visível e de quem pede: cliente dono ({@code CUSTOMER}), mediador da proposta
     * ({@code ORG} com {@code broker_org_id}) ou a seguradora, só depois da submissão.
     *
     * @throws DomainException 404 {@code PROPOSAL_NOT_FOUND}
     */
    @Transactional(readOnly = true)
    public Proposal accessible(UUID proposalId) {
        Proposal proposal = proposalId == null ? null : proposals.findById(proposalId).orElse(null);
        if (proposal == null || !canAccess(proposal, TenantContext.current())) {
            throw notFound();
        }
        return proposal;
    }

    /**
     * Proposta com as respostas, o estado KYC do cliente, a assinatura válida e os documentos do produto
     * (na língua do tomador, S4-04 §8).
     */
    @Transactional(readOnly = true)
    public ProposalDetails details(Proposal proposal) {
        List<Declaration> decl = declarations.findByProposalId(proposal.getId()).stream()
                .sorted(Comparator.comparing(Declaration::getQuestionCode)).toList();
        List<SuitabilityAnswer> suit = suitabilityAnswers.findByProposalId(proposal.getId()).stream()
                .sorted(Comparator.comparing(SuitabilityAnswer::getQuestionCode)).toList();
        // A assinatura é LAZY: a resposta é montada fora da transacção.
        Signature signature = proposal.getCurrentSignature() == null ? null
                : Hibernate.unproxy(proposal.getCurrentSignature(), Signature.class);
        String language = ProposalPdfModelBuilder.language(json.policyholder(proposal.getPolicyholder()));
        List<ProductDocuments.Selected> documents = ProductDocuments.select(
                catalog.version(proposal.getProductVersionId()).documents(), language);
        return new ProposalDetails(proposal, decl, suit, identity.kycStatus(proposal.getCustomerUserId()).state(),
                signature, documents);
    }

    /** {@code ProposalApi.view}: no âmbito de quem chama, sem a verificação do serviço. */
    @Transactional(readOnly = true)
    public Optional<ProposalView> view(UUID proposalId) {
        return proposalId == null ? Optional.empty() : proposals.findById(proposalId).map(this::toView);
    }

    private ProposalView toView(Proposal p) {
        PolicyholderSnapshot holder = json.policyholder(p.getPolicyholder());
        return new ProposalView(p.getId(), p.getNumber(), p.getStatus(), p.getInsurerId(), p.getProductVersionId(),
                p.getProductType().name(), p.getCustomerUserId(), holder.fullName(), p.getChannel().name(),
                p.getBrokerOrgId(), p.getVehicleId(), p.getDriverId(), p.getVehicleSnapshot(), p.getDriverSnapshot(),
                json.coverages(p.getCoverages()).stream().map(ProposalJson::view).toList(), p.getStartAt(),
                p.getEndAt(), p.getDurationDays(), Money.ofCentavos(p.getTotalAmount()),
                json.premium(p.getPremiumBreakdown()).commercial(),
                p.getCurrentSignature() == null ? null : p.getCurrentSignature().getDocumentFileId());
    }

    static boolean canAccess(Proposal proposal, TenantSnapshot tenant) {
        Scope scope = tenant.scope();
        if (scope == Scope.CUSTOMER) {
            return proposal.getCustomerUserId().equals(tenant.userId());
        }
        if (scope == Scope.ORG) {
            if (tenant.orgId() != null && tenant.orgId().equals(proposal.getBrokerOrgId())) {
                return true;
            }
            return tenant.insurerId() != null && tenant.insurerId().equals(proposal.getInsurerId())
                    && proposal.getSubmittedAt() != null;
        }
        return scope == Scope.SYSTEM || scope == Scope.PLATFORM;
    }

    static DomainException notFound() {
        return DomainException.of(ErrorCodes.PROPOSAL_NOT_FOUND, "Proposal not found");
    }
}
