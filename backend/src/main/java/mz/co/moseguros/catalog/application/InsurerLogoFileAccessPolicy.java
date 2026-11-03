package mz.co.moseguros.catalog.application;

import java.util.Set;
import java.util.UUID;

import mz.co.moseguros.document.FileAccessPolicy;
import mz.co.moseguros.shared.security.CurrentUser;
import org.springframework.stereotype.Component;

/**
 * Logótipos das seguradoras (S2-04 §7.1): públicos, também para anónimos e para os PDF gerados em
 * {@code SYSTEM}.
 */
@Component
public class InsurerLogoFileAccessPolicy implements FileAccessPolicy {

    @Override
    public Set<String> ownerTypes() {
        return Set.of(InsurerLogoService.OWNER_TYPE);
    }

    @Override
    public boolean canRead(String ownerType, UUID ownerId, CurrentUser user) {
        return true;
    }
}
