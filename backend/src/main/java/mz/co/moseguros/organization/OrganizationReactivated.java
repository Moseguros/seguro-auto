package mz.co.moseguros.organization;

import java.util.UUID;

/**
 * Evento público: a organização suspensa voltou a ficar activa.
 */
public record OrganizationReactivated(UUID organizationId) {
}
