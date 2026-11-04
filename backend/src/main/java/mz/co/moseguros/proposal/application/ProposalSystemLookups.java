package mz.co.moseguros.proposal.application;

import java.util.Optional;
import java.util.UUID;

import mz.co.moseguros.catalog.CatalogApi;
import mz.co.moseguros.catalog.ProductVersionView;
import mz.co.moseguros.organization.OrganizationApi;
import mz.co.moseguros.organization.OrganizationSummary;
import mz.co.moseguros.organization.OrganizationType;
import mz.co.moseguros.proposal.domain.UnderwritingRules;
import mz.co.moseguros.shared.tenant.RunAsSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Leituras entre âmbitos das propostas, em {@code SYSTEM} (S4-02 §9): o cliente não vê as
 * organizações pelo RLS. Lê só a organização pedida e só se for uma seguradora.
 */
@Component
public class ProposalSystemLookups {

    private static final Logger log = LoggerFactory.getLogger(ProposalSystemLookups.class);

    private final OrganizationApi organizations;
    private final CatalogApi catalog;

    public ProposalSystemLookups(OrganizationApi organizations, CatalogApi catalog) {
        this.organizations = organizations;
        this.catalog = catalog;
    }

    /** Seguradora: nome comercial congelado na proposta e se está activa. */
    public record InsurerRef(UUID id, String displayName, boolean active) {
    }

    /** Seguradora da cotação; vazio se não existir ou não for uma seguradora. */
    @RunAsSystem
    @Transactional(readOnly = true)
    public Optional<InsurerRef> insurer(UUID insurerId) {
        return organizations.findSummary(insurerId)
                .filter(o -> o.type() == OrganizationType.INSURER)
                .map(ProposalSystemLookups::ref);
    }

    /**
     * Regras de aceitação da versão do produto da proposta (S4-05 §3): {@code rules.underwriting} da
     * versão publicada ({@code CatalogApi.version}, já em {@code SYSTEM}) ou os valores por defeito. Um
     * JSON gravado que não cumpra o schema (só por alteração manual: o {@code catalog} valida-o) conta
     * como aceitação automática desligada, para a proposta ir para análise humana.
     */
    public UnderwritingRules underwritingRules(ProductVersionView version) {
        try {
            return UnderwritingRules.fromProductRules(version.rules());
        } catch (IllegalArgumentException e) {
            log.warn("Product version {}: invalid underwriting rules, automatic acceptance disabled", version.id());
            return UnderwritingRules.disabled(UnderwritingRules.Source.PRODUCT);
        }
    }

    /** {@link #underwritingRules(ProductVersionView)} da versão indicada. */
    public UnderwritingRules underwritingRules(UUID productVersionId) {
        return underwritingRules(catalog.version(productVersionId));
    }

    private static InsurerRef ref(OrganizationSummary o) {
        return new InsurerRef(o.id(), o.displayName(), o.isActive());
    }
}
