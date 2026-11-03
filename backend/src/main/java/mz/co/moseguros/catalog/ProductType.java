package mz.co.moseguros.catalog;

/**
 * Tipo de produto (S2-04): só RC obrigatória, danos próprios, temporário, de fronteira ou de
 * frota. O tipo condiciona as coberturas permitidas na publicação ({@code PublicationRules}).
 */
public enum ProductType {
    MTPL_ONLY,
    COMPREHENSIVE,
    TEMPORARY,
    BORDER,
    FLEET
}
