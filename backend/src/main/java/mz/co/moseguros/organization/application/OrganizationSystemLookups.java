package mz.co.moseguros.organization.application;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import mz.co.moseguros.organization.InsurerProfileView;
import mz.co.moseguros.organization.OrganizationType;
import mz.co.moseguros.organization.domain.InsurerCharges;
import mz.co.moseguros.organization.domain.InsurerProfile;
import mz.co.moseguros.organization.domain.InsurerProfileRepository;
import mz.co.moseguros.organization.domain.Organization;
import mz.co.moseguros.organization.domain.OrganizationRepository;
import mz.co.moseguros.shared.tenant.RunAsSystem;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Leituras de organizações entre âmbitos, em {@code SYSTEM} (S3-01 §3): quem cota é um cliente
 * ou um mediador, e o RLS de {@code organization} não lhes mostra as seguradoras. Lê só as linhas
 * pedidas e só de organizações {@code INSURER}. Só leitura; nunca devolve segredos.
 */
@Component
public class OrganizationSystemLookups {

    private final OrganizationRepository organizations;
    private final InsurerProfileRepository profiles;

    public OrganizationSystemLookups(OrganizationRepository organizations, InsurerProfileRepository profiles) {
        this.organizations = organizations;
        this.profiles = profiles;
    }

    /** Perfil da seguradora; vazio se não existir ou se a organização não for uma seguradora. */
    @RunAsSystem
    @Transactional(readOnly = true)
    public Optional<InsurerProfileView> insurerProfile(UUID insurerId) {
        if (insurerId == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(views(Set.of(insurerId)).get(insurerId));
    }

    /** Perfis das seguradoras pedidas que existem, numa só consulta de cada tabela. */
    @RunAsSystem
    @Transactional(readOnly = true)
    public Map<UUID, InsurerProfileView> insurerProfiles(Collection<UUID> insurerIds) {
        if (insurerIds == null || insurerIds.isEmpty()) {
            return Map.of();
        }
        return views(insurerIds.stream().filter(Objects::nonNull).collect(Collectors.toSet()));
    }

    private Map<UUID, InsurerProfileView> views(Set<UUID> ids) {
        Map<UUID, InsurerProfileView> result = new LinkedHashMap<>();
        if (ids.isEmpty()) {
            return result;
        }
        Map<UUID, Organization> insurers = organizations.findAllById(ids).stream()
                .filter(o -> o.getType() == OrganizationType.INSURER)
                .collect(Collectors.toMap(Organization::getId, Function.identity()));
        if (insurers.isEmpty()) {
            return result;
        }
        List<InsurerProfile> found = profiles.findByOrganizationIdIn(insurers.keySet());
        for (InsurerProfile p : found) {
            Organization org = insurers.get(p.getOrganizationId());
            result.put(org.getId(), new InsurerProfileView(org.getId(), org.displayName(), p.getShortCode(),
                    p.getLogoFileId(), org.getStatus(), p.getRatingMode(), p.getRatingEndpointUrl(),
                    p.getRatingSecretRef(), InsurerCharges.parse(p.getCharges())));
        }
        return result;
    }
}
