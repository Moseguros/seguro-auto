package mz.co.moseguros.catalog;

import java.time.Instant;
import java.util.UUID;

/**
 * Uma versão de produto foi publicada (S2-04). Publicado com o outbox do Spring Modulith; ninguém
 * o ouve no Sprint 2.
 */
public record ProductVersionPublished(UUID eventId, Instant occurredAt, UUID insurerId, UUID productId,
                                      UUID productVersionId, int versionNo, ProductType productType) {
}
