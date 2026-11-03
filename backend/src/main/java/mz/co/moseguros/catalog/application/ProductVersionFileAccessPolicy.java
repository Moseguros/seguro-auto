package mz.co.moseguros.catalog.application;

import java.util.Set;
import java.util.UUID;

import mz.co.moseguros.catalog.application.CatalogSystemLookups.VersionAccess;
import mz.co.moseguros.document.FileAccessPolicy;
import mz.co.moseguros.shared.security.CurrentUser;
import org.springframework.stereotype.Component;

/**
 * Quem lê os documentos das versões de produtos (S2-04 §7): de uma versão publicada, qualquer
 * pessoa (também anónimos); de um rascunho, só o staff da seguradora dona com {@code product:read}.
 */
@Component
public class ProductVersionFileAccessPolicy implements FileAccessPolicy {

    private final CatalogSystemLookups lookups;

    public ProductVersionFileAccessPolicy(CatalogSystemLookups lookups) {
        this.lookups = lookups;
    }

    @Override
    public Set<String> ownerTypes() {
        return Set.of(ProductDocumentService.OWNER_TYPE);
    }

    @Override
    public boolean canRead(String ownerType, UUID ownerId, CurrentUser user) {
        VersionAccess access = lookups.versionAccess(ownerId).orElse(null);
        if (access == null) {
            return false;
        }
        if (access.publishedAt() != null) {
            return true;
        }
        return user.isAuthenticated() && access.insurerId().equals(user.orgId())
                && user.hasPermission("product:read");
    }
}
