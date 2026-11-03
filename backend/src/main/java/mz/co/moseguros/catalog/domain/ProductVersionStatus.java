package mz.co.moseguros.catalog.domain;

/**
 * Estado de uma versão de produto (S2-04 §2). Só {@code DRAFT} se altera; {@code PUBLISHED} é a
 * versão em vigor (no máximo uma por produto); {@code SUPERSEDED} quando outra é publicada;
 * {@code RETIRED} quando o produto é retirado.
 */
public enum ProductVersionStatus {
    DRAFT,
    PUBLISHED,
    SUPERSEDED,
    RETIRED
}
