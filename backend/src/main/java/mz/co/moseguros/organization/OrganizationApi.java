package mz.co.moseguros.organization;

import java.util.Optional;
import java.util.UUID;

import mz.co.moseguros.organization.domain.OrganizationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Interface pública do módulo Organizações (só leitura). Os outros módulos usam-na em vez dos
 * repositórios. As consultas correm no âmbito do chamador ({@code TenantContext} e RLS).
 */
@Service
public class OrganizationApi {

    private final OrganizationRepository organizations;

    public OrganizationApi(OrganizationRepository organizations) {
        this.organizations = organizations;
    }

    /** Resumo da organização, se existir e for visível no âmbito actual. */
    @Transactional(readOnly = true)
    public Optional<OrganizationSummary> findSummary(UUID id) {
        if (id == null) {
            return Optional.empty();
        }
        return organizations.findById(id)
                .map(org -> new OrganizationSummary(org.getId(), org.getType(), org.getStatus(), org.displayName()));
    }
}
