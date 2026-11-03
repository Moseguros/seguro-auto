package mz.co.moseguros.organization;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import mz.co.moseguros.organization.domain.InsurerProfile;
import mz.co.moseguros.organization.domain.InsurerProfileRepository;
import mz.co.moseguros.organization.domain.Organization;
import mz.co.moseguros.organization.domain.OrganizationRepository;
import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.error.ErrorCodes;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Interface pública do módulo Organizações. Os outros módulos usam-na em vez dos repositórios.
 * As consultas e alterações correm no âmbito do chamador ({@code TenantContext} e RLS).
 */
@Service
public class OrganizationApi {

    private final OrganizationRepository organizations;
    private final InsurerProfileRepository insurerProfiles;

    public OrganizationApi(OrganizationRepository organizations, InsurerProfileRepository insurerProfiles) {
        this.organizations = organizations;
        this.insurerProfiles = insurerProfiles;
    }

    /** Resumo da organização, se existir e for visível no âmbito actual. */
    @Transactional(readOnly = true)
    public Optional<OrganizationSummary> findSummary(UUID id) {
        if (id == null) {
            return Optional.empty();
        }
        return organizations.findById(id).map(OrganizationApi::summary);
    }

    /**
     * Resumos das organizações indicadas que existem e são visíveis no âmbito actual, numa só
     * consulta (S2-04: o catálogo chama-o em {@code SYSTEM} para saber que seguradoras estão
     * activas). As que faltam não aparecem no mapa.
     */
    @Transactional(readOnly = true)
    public Map<UUID, OrganizationSummary> findSummaries(Set<UUID> ids) {
        Map<UUID, OrganizationSummary> result = new LinkedHashMap<>();
        if (ids == null || ids.isEmpty()) {
            return result;
        }
        for (Organization org : organizations.findAllById(ids)) {
            result.put(org.getId(), summary(org));
        }
        return result;
    }

    /** Ficheiro do logótipo da seguradora ({@code document.file}), se tiver e for visível no âmbito actual. */
    @Transactional(readOnly = true)
    public Optional<UUID> insurerLogoFileId(UUID insurerId) {
        if (insurerId == null) {
            return Optional.empty();
        }
        return insurerProfiles.findByOrganizationId(insurerId).map(InsurerProfile::getLogoFileId);
    }

    /**
     * Grava (ou limpa, com {@code null}) o logótipo da seguradora (S2-04 §7.1). O módulo
     * {@code organization} só guarda o id; o envio e a validação do ficheiro são do {@code catalog}.
     *
     * @throws DomainException {@code ORGANIZATION_NOT_FOUND} (404) se a organização não for uma
     *                         seguradora visível no âmbito actual
     */
    @Transactional
    public void setInsurerLogo(UUID insurerId, UUID fileIdOrNull) {
        InsurerProfile profile = (insurerId == null ? Optional.<InsurerProfile>empty()
                : insurerProfiles.findByOrganizationId(insurerId))
                .orElseThrow(() -> DomainException.of(ErrorCodes.ORGANIZATION_NOT_FOUND, "Insurer not found"));
        profile.changeLogo(fileIdOrNull);
        insurerProfiles.flush();
    }

    private static OrganizationSummary summary(Organization org) {
        return new OrganizationSummary(org.getId(), org.getType(), org.getStatus(), org.displayName());
    }
}
