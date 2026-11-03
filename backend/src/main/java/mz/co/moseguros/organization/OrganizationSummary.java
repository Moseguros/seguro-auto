package mz.co.moseguros.organization;

import java.util.UUID;

/**
 * Resumo público de uma organização, para os outros módulos ({@link OrganizationApi}).
 *
 * @param displayName nome comercial, se existir, senão a denominação social
 */
public record OrganizationSummary(UUID id, OrganizationType type, OrganizationStatus status, String displayName) {

    public boolean isActive() {
        return status == OrganizationStatus.ACTIVE;
    }
}
