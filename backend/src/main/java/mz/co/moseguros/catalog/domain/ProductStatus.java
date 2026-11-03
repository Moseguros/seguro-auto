package mz.co.moseguros.catalog.domain;

/**
 * Estado de um produto (S2-04 §2): {@code DRAFT} até à primeira publicação de uma versão,
 * {@code PUBLISHED} depois, {@code RETIRED} quando é retirado (irreversível).
 */
public enum ProductStatus {
    DRAFT,
    PUBLISHED,
    RETIRED
}
