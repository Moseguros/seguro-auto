package mz.co.moseguros.organization;

import java.util.UUID;

/**
 * Evento público: a organização foi suspensa. O módulo {@code identity} revoga as sessões dos
 * utilizadores dela. Publicado depois de gravar a suspensão (registo de publicações do Spring
 * Modulith).
 *
 * @param reason motivo indicado por quem suspendeu
 */
public record OrganizationSuspended(UUID organizationId, String reason) {
}
