package mz.co.moseguros.verification.application;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import mz.co.moseguros.organization.InsurerProfileView;
import mz.co.moseguros.organization.OrganizationApi;
import mz.co.moseguros.policy.PolicyApi;
import mz.co.moseguros.policy.PolicyCoverView;
import org.springframework.stereotype.Component;

/**
 * Cache das consultas à apólice da verificação (S6-03 §6): por matrícula formatada e por código, com
 * {@code expireAfterWrite} de {@code moseguros.verification.cache-ttl} (60 s) e até 50 000 entradas cada;
 * guardam também os resultados vazios. O estado nunca fica em cache: o {@code VerificationService}
 * calcula-o em cada pedido com o instante actual.
 *
 * <p>Uma apólice emitida retira da cache a entrada da sua matrícula ({@link PolicyCacheListener}); noutras
 * instâncias a entrada antiga dura no máximo o TTL. O nome da seguradora fica 10 minutos.
 */
@Component
public class PolicyLookupCache {

    static final long MAX_ENTRIES = 50_000;
    static final Duration INSURER_NAME_TTL = Duration.ofMinutes(10);

    private final PolicyApi policies;
    private final OrganizationApi organizations;
    private final Cache<String, Optional<PolicyCoverView>> byPlate;
    private final Cache<String, Optional<PolicyCoverView>> byToken;
    private final Cache<UUID, Optional<String>> insurerNames;

    public PolicyLookupCache(PolicyApi policies, OrganizationApi organizations, VerificationProperties properties) {
        this.policies = policies;
        this.organizations = organizations;
        this.byPlate = Caffeine.newBuilder().expireAfterWrite(properties.cacheTtl()).maximumSize(MAX_ENTRIES).build();
        this.byToken = Caffeine.newBuilder().expireAfterWrite(properties.cacheTtl()).maximumSize(MAX_ENTRIES).build();
        this.insurerNames = Caffeine.newBuilder().expireAfterWrite(INSURER_NAME_TTL).maximumSize(1_000).build();
    }

    /** {@code PolicyApi.findLatestByPlate(plate, at)}, pela cache. @param plate matrícula formatada */
    public Optional<PolicyCoverView> byPlate(String plate, Instant at) {
        return byPlate.get(plate, key -> policies.findLatestByPlate(key, at));
    }

    /** {@code PolicyApi.findByCertificateToken(token)}, pela cache. */
    public Optional<PolicyCoverView> byToken(String token) {
        return byToken.get(token, policies::findByCertificateToken);
    }

    /** {@code displayName} da seguradora ({@code OrganizationApi.insurerProfile}, em {@code SYSTEM}). */
    public Optional<String> insurerName(UUID insurerId) {
        return insurerNames.get(insurerId,
                id -> organizations.insurerProfile(id).map(InsurerProfileView::displayName));
    }

    /** Retira a entrada da matrícula (formatada). */
    public void evictPlate(String plate) {
        if (plate != null) {
            byPlate.invalidate(plate);
        }
    }
}
