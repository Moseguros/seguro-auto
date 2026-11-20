package mz.co.moseguros.verification.application;

import mz.co.moseguros.policy.PolicyIssued;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

/**
 * Apólice emitida ({@link PolicyIssued}) → retira da {@link PolicyLookupCache} a entrada da matrícula (S6-03
 * §6): uma matrícula antes {@code NOT_FOUND} passa logo a {@code VALID}. Não acede à BD (sem
 * {@code @RunAsSystem}). O S7 acrescenta o mesmo para {@code PolicyStatusChanged}.
 */
@Component
public class PolicyCacheListener {

    private final PolicyLookupCache cache;

    public PolicyCacheListener(PolicyLookupCache cache) {
        this.cache = cache;
    }

    @ApplicationModuleListener
    public void on(PolicyIssued event) {
        cache.evictPlate(event.plate());
    }
}
